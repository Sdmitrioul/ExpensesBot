package com.dskroba.notion;

import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseTag;
import com.dskroba.type.Timeframe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String tags = expense.expenseTag().name();

        return """
                {"parent":{"database_id":"%s"},"properties":{"Amount":{"number":%s},"Tags":{"multi_select":[%s]},"Month":{"select":{"name":"%s"}},"Date":{"date":{"start":"%s","end":null,"time_zone":null}},"Notes":{"title":[{"text":{"content":"%s","link":null}}]}}}
                """.formatted(databaseId, expense.amount(), tags, expense.date(), DATE_TIME_FORMATTER.format(expense.date()), Optional.ofNullable(expense.description()).orElse(""));
    }

    public static String wrapTimeInterval(Timeframe timeframe) {
        return """
                {"filter":{"and":[{"property":"Time","formula":{"date":{"on_or_after":"%s"}}},{"property":"Time","formula":{"date":{"before":"%s"}}}]},"sorts":[{"property":"Time","direction":"descending"}]}
                """
                .formatted(INSTANT_TIME_FORMATTER.format(timeframe.from()), INSTANT_TIME_FORMATTER.format(timeframe.to()));
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
                            .forEach(builder::type);
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
