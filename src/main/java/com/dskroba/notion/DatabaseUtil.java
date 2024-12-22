package com.dskroba.notion;

import com.dskroba.base.exception.CustomException;
import com.dskroba.base.type.Pair;
import com.dskroba.type.Expense;
import com.dskroba.type.ExpenseTag;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DatabaseUtil {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseUtil.class);
    public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter INSTANT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());

    public static String wrapExpense(Expense expense, String databaseId) {
        String tags = expense.getTag()
                .stream()
                .map(tag -> "{\"name\":\"" + tag + "\"}")
                .collect(Collectors.joining(","));

        return new StringBuilder().append("{")
                .append("\"parent\":{\"database_id\":\"").append(databaseId).append("\"},")
                .append("\"properties\":{")
                .append("\"Amount\":{\"number\":").append(expense.getAmount()).append("},")
                .append("\"Tags\":{\"multi_select\":[").append(tags).append("]},")
                .append("\"Month\":{\"select\":{\"name\":\"").append(expense.getMonth()).append("\"}},")
                .append("\"Date\":{\"date\":{\"start\":\"").append(DATE_TIME_FORMATTER.format(expense.getDate()))
                .append("\",\"end\":null,\"time_zone\":null}},")
                .append("\"Notes\":{\"title\":[{\"text\":{\"content\":\"")
                .append(Optional.ofNullable(expense.getNote()).orElse("")).append("\",\"link\":null}}]}")
                .append("}}").toString();
    }

    public static String wrapTimeInterval(Pair<Instant, Instant> timeInterval) {
        return new StringBuilder().append("{")
                .append("\"filter\":{")
                .append("\"and\":[").append("{").append("\"property\":\"Time\",").append("\"formula\":{")
                .append("\"date\":{")
                .append("\"on_or_after\":\"").append(INSTANT_TIME_FORMATTER.format(timeInterval.first())).append("\"")
                .append("}").append("}").append("},")
                .append("{")
                .append("\"property\":\"Time\",").append("\"formula\":{").append("\"date\":{")
                .append("\"before\":\"").append(INSTANT_TIME_FORMATTER.format(timeInterval.second())).append("\"")
                .append("}").append("}").append("}").append("]").append("},")
                .append("\"sorts\":[").append("{")
                .append("\"property\":\"Time\",").append("\"direction\":\"descending\"")
                .append("}").append("]").append("}")
                .toString();
    }

    public static List<Expense> parseExpenses(Reader reader) {
        JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
        return response.get("results").getAsJsonArray().asList()
                .stream()
                .map(expense -> parseExpense(expense.getAsJsonObject()))
                .filter(Objects::nonNull)
                .toList();
    }

    private static Expense parseExpense(JsonObject expenseObject) {
        if (expenseObject.get("in_trash").getAsBoolean()) {
            return null;
        }
        try {
            Expense.Builder builder = new Expense.Builder();
            JsonObject properties = expenseObject.get("properties").getAsJsonObject();
            properties.entrySet().forEach(entry -> {
                if ("Amount".equals(entry.getKey())) {
                    builder.amount(entry.getValue().getAsJsonObject().get("number").getAsDouble());
                } else if ("Time".equals(entry.getKey())) {
                    try {
                        builder.date(DATE_TIME_FORMATTER.parse(entry.getValue().getAsJsonObject()
                                .getAsJsonObject("formula")
                                .getAsJsonObject("date")
                                .get("start")
                                .getAsString())
                        );
                    } catch (ParseException e) {
                        throw new CustomException(e);
                    }
                } else if ("Notes".equals(entry.getKey())) {
                    builder.description(
                            entry.getValue().getAsJsonObject()
                                    .getAsJsonArray("title")
                                    .asList()
                                    .stream()
                                    .map(element -> element.getAsJsonObject()
                                            .get("plain_text")
                                            .getAsString())
                                    .collect(Collectors.joining())
                    );
                } else if ("Tags".equals(entry.getKey())) {
                    entry.getValue().getAsJsonObject()
                            .getAsJsonArray("multi_select")
                            .asList()
                            .stream()
                            .map(element -> element.getAsJsonObject().get("name").getAsString())
                            .map(element -> element.toUpperCase(Locale.ROOT))
                            .map(tag -> {
                                try {
                                    return ExpenseTag.valueOf(tag);
                                } catch (IllegalArgumentException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .forEach(builder::putTag);
                }
            });
            return builder.build();
        } catch (CustomException | NullPointerException e) {
            LOGGER.error("Error while parsing expense entry: ", e);
            return null;
        }
    }

    private DatabaseUtil() {
    }
}
