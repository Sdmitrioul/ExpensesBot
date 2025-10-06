package com.dskroba.base.formatter.extractor.type;

import java.time.LocalDateTime;

public record DateTimeValue(LocalDateTime value) implements TypeValue<LocalDateTime> {
}
