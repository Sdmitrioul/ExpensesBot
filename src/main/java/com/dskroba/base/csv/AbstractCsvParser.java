package com.dskroba.base.csv;

import com.dskroba.base.exception.CustomException;
import io.vavr.control.Either;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCsvParser<T> implements CsvParser<T> {
    protected final String delimiter;
    protected final Pattern csvPattern;

    protected AbstractCsvParser(String delimiter) {
        this.delimiter = delimiter;
        String escapedDelimiter = Pattern.quote(delimiter);
        this.csvPattern = Pattern.compile(
                "(?:^|" + escapedDelimiter + ")" +
                        "(?:\"((?:[^\"]|\"\")*)\"|([^" + escapedDelimiter + "\"]*))"
        );
    }

    public String parse(InputStream inputStream,
                        Consumer<RowEntry<T>> entryConsumer,
                        Consumer<IncorrectRowEntry> unparsedDataConsumer) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> schema = parseSchema(bufferedReader);
            long lineCounter = 2;
            String input;

            while ((input = bufferedReader.readLine()) != null) {
                List<String> entries = parseCSVLine(input);

                if (entries.size() != schema.size()) {
                    String causeMessage = String.format(
                            "Line %d can't be processed: expected %d columns (header: %s), but found %d columns.",
                            lineCounter,
                            schema.size(),
                            String.join(", ", schema),
                            entries.size()
                    );
                    unparsedDataConsumer.accept(new IncorrectRowEntry(lineCounter, causeMessage, input));
                    lineCounter++;
                    continue;
                }

                Map<String, String> entry = new HashMap<>();
                for (int i = 0; i < entries.size(); i++) {
                    entry.put(schema.get(i), entries.get(i));
                }
                var result = parseLine(entry);
                if (result.isLeft()) {
                    unparsedDataConsumer.accept(new IncorrectRowEntry(lineCounter, result.getLeft(), input));
                } else {
                    entryConsumer.accept(new RowEntry<>(lineCounter, result.get()));
                }
                lineCounter++;
            }
            return String.join(delimiter, schema);
        } catch (IOException e) {
            throw new CustomException("Unable to start reading file.", e);
        }
    }

    protected List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        Matcher matcher = csvPattern.matcher(line);
        while (matcher.find()) {
            String quotedField = matcher.group(1);
            String unquotedField = matcher.group(2);

            if (quotedField != null) {
                // Unescape double quotes (CSV escapes quotes as "")
                result.add(quotedField.replace("\"\"", "\""));
            } else {
                result.add(Objects.requireNonNullElse(unquotedField, ""));
            }
        }
        if (line.isEmpty() && result.isEmpty()) {
            result.add("");
        }
        return result.stream().map(String::trim).map(String::toLowerCase).toList();
    }

    private List<String> parseSchema(BufferedReader bufferedReader) {
        try {
            String headerLine = bufferedReader.readLine();
            if (headerLine == null) {
                throw new CustomException("File is empty - no header found!");
            }

            List<String> columnsNames = parseCSVLine(headerLine);
            validateColumns(columnsNames);
            return columnsNames;
        } catch (IOException e) {
            throw new CustomException("Unable to read header of the file!", e);
        }
    }

    protected abstract Either<String, T> parseLine(Map<String, String> entry);

    protected abstract void validateColumns(List<String> columns) throws CustomException;
}
