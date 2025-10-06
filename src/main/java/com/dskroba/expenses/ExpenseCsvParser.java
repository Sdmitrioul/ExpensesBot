package com.dskroba.expenses;

import com.dskroba.base.csv.AbstractCsvParser;
import com.dskroba.base.exception.CustomException;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Validation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExpenseCsvParser extends AbstractCsvParser<Expense.Builder> {
    private static final List<String> REQUIRED_COLUMNS = List.of("amount", "tag", "date");

    public ExpenseCsvParser(String delimiter) {
        super(delimiter);
    }

    @Override
    protected Either<String, Expense.Builder> parseLine(Map<String, String> entry) {
        return Validation.combine(
                        validateField("amount", entry, ExpenseUtils::parseAmount, "Incorrect amount value"),
                        validateField("tag", entry, ExpenseUtils::parseTag, "Unsupported tag"),
                        validateField("date", entry, ExpenseUtils::parseDate, "Invalid date")
                )
                .ap((amount, tag, date) -> new Expense.Builder()
                        .amount(amount)
                        .date(date)
                        .description(entry.get("description"))
                        .currency(entry.get("currency"))
                        .type(tag))
                .toEither()
                .mapLeft(errors -> errors.mkString("\"[", ", ", "]\""));
    }

    @Override
    protected void validateColumns(List<String> columns) throws CustomException {
        List<String> missingColumns = REQUIRED_COLUMNS.stream().filter(Predicate.not(columns::contains)).toList();
        if (!missingColumns.isEmpty()) {
            throw new CustomException("Some required columns are missing: " + String.join(", ", missingColumns));
        }
    }

    private <T> Validation<String, T> validateField(
            String fieldName,
            Map<String, String> entry,
            Function<String, Optional<T>> parser,
            String errorPrefix) {
        String value = entry.get(fieldName);
        return Option.ofOptional(parser.apply(value))
                .toValidation(errorPrefix + ": " + value);
    }
}
