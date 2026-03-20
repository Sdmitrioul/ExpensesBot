package com.dskroba.base.formatter;

import com.dskroba.type.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvFormatter implements Formatter {
    private static final Logger log = LoggerFactory.getLogger(CsvFormatter.class);

    private final String delimiter;

    public CsvFormatter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public ContentType contentType() {
        return ContentType.CSV;
    }

    @Override
    public <T> byte[] formatOutput(List<FieldSelector<T>> fieldSelectors, List<T> values) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(result, StandardCharsets.UTF_8)) {
            for (int i = 0; i < fieldSelectors.size(); i++) {
                var selector = fieldSelectors.get(i);
                writer.append(selector.name());
                if (i != fieldSelectors.size() - 1) {
                    writer.append(delimiter);
                }
            }
            writer.write(System.lineSeparator());
            for (var value : values) {
                for (int i = 0; i < fieldSelectors.size(); i++) {
                    var selector = fieldSelectors.get(i);
                    writer.append(selector.valueExtractor().extractString(value));
                    if (i != fieldSelectors.size() - 1) {
                        writer.append(delimiter);
                    }
                }
                writer.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Unexpected exception!");
        }
        return result.toByteArray();
    }
}
