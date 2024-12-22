package com.dskroba.base;

import com.dskroba.base.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TablePrinter {
    private static final int MIN_COLUMN_SIZE = 5;
    private final String[] headers;
    private final String[] delimiters;
    private final List<String[]> rows = new ArrayList<>();
    private final Format format;

    public TablePrinter(Format format, String... headers) {
        this.headers = headers;
        this.format = format;
        this.delimiters = IntStream.range(0, headers.length)
                .mapToObj(i -> "-")
                .toArray(String[]::new);
    }

    public void addRow(String... row) {
        if (row == null || row.length != headers.length) {
            throw new CustomException("Invalid row, expected " + headers.length + " but got " + (row == null ? "null" : row.length));
        }
        for (int i = 0; i < row.length; i++) {
            row[i] = row[i].strip();
        }
        rows.add(row);
    }

    public String print() {
        StringBuilder table = new StringBuilder();
        int[] sizes = new int[headers.length];
        rows.forEach(row -> {
            for (int i = 0; i < row.length; i++) {
                sizes[i] = Math.max(row[i].length(), sizes[i]);
            }
        });
        for (int i = 0; i < headers.length; i++) {
            if (sizes[i] != 0) {
                sizes[i] = Math.max(Math.max(MIN_COLUMN_SIZE, sizes[i]), headers[i].length());
            }
        }
        appendSeparator(table, sizes);
        appendRow(table, sizes, headers);
        appendSeparator(table, sizes);
        for (String[] row : rows) {
            appendRow(table, sizes, row);
        }
        appendSeparator(table, sizes);
        return table.toString();
    }

    private void appendRow(StringBuilder table, int[] sizes, String[] row) {
        appendLine(table, sizes, row, " ", " ", " ", "|");
    }

    private void appendSeparator(StringBuilder table, int[] sizes) {
        appendLine(table, sizes, delimiters, "-", "-", "-", "+");
    }

    private void appendLine(StringBuilder table,
                            int[] sizes,
                            String[] columns,
                            String prefix,
                            String suffix,
                            String filler,
                            String delimiter) {
        table.append(delimiter);
        for (int i = 0; i < headers.length; i++) {
            if (format == Format.ELIMINATE_EMPTY_COLUMNS && sizes[i] == 0) {
                continue;
            }
            int size = sizes[i] - columns[i].length();
            table.append(prefix).append(columns[i]).append(filler.repeat(size)).append(suffix).append(delimiter);
        }
        table.append(System.lineSeparator());
    }

    public enum Format {
        FULL, ELIMINATE_EMPTY_COLUMNS;
    }
}
