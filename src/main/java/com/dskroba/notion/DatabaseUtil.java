package com.dskroba.notion;

import com.dskroba.type.Expense;

import java.util.stream.Collectors;

public final class DatabaseUtil {
    public static String wrapExpense(Expense expense, String databaseId) {
        StringBuilder json = new StringBuilder();
        String tags = expense.getTag()
                .stream()
                .map(tag -> "{\"name\":\"" + tag + "\"}")
                .collect(Collectors.joining(","));

        json.append("{")
                .append("\"parent\":{\"database_id\":\"").append(databaseId).append("\"},")
                .append("\"properties\":{")
                .append("\"Amount\":{\"number\":").append(expense.getAmount()).append("},")
                .append("\"Tags\":{\"multi_select\":[").append(tags).append("]},")
                .append("\"Month\":{\"select\":{\"name\":\"").append(expense.getMonth()).append("\"}},")
                .append("\"Date\":{\"date\":{\"start\":\"").append(expense.getDate()).append("\",\"end\":null,\"time_zone\":null}},")
                .append("\"Notes\":{\"title\":[{\"text\":{\"content\":\"").append(expense.getNote()).append("\",\"link\":null}}]}")
                .append("}}");

        return json.toString();
    }

    private DatabaseUtil() {
    }
}
