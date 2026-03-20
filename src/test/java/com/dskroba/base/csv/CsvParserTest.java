package com.dskroba.base.csv;

import com.dskroba.base.exception.CustomException;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvParserTest {

    private CsvParser<TestData> csvParser;
    private List<RowEntry<TestData>> parsedEntries;
    private List<IncorrectRowEntry> incorrectEntries;

    @BeforeEach
    void setUp() {
        csvParser = new AbstractCsvParser<>(",") {
            @Override
            protected Either<String, TestData> parseLine(Map<String, String> entry) {
                try {
                    String firstName = entry.get("firstName");
                    String lastName = entry.get("lastName");
                    String ageStr = entry.get("age");

                    // Validation
                    if (firstName == null || lastName == null || ageStr == null) {
                        return Either.left("Missing required fields");
                    }

                    if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
                        return Either.left("firstName and lastName cannot be empty");
                    }

                    int age = Integer.parseInt(ageStr.trim());

                    if (age < 0 || age > 150) {
                        return Either.left("Age must be between 0 and 150");
                    }

                    TestData data = new TestData(firstName.trim(), lastName.trim(), age);
                    return Either.right(data);

                } catch (NumberFormatException e) {
                    return Either.left("Invalid age format: " + e.getMessage());
                } catch (Exception e) {
                    return Either.left("Parsing error: " + e.getMessage());
                }
            }

            @Override
            protected void validateColumns(List<String> columns) throws CustomException {
                Set.of("lastName", "firstName", "age")
                        .stream()
                        .filter(Predicate.not(columns::contains))
                        .findFirst()
                        .ifPresent(column -> {
                            throw new CustomException("Column is missing: " + column);
                        });
            }
        };

        parsedEntries = new ArrayList<>();
        incorrectEntries = new ArrayList<>();
    }

    @Test
    void shouldParseValidCsvWithSingleRow() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                """;
        InputStream inputStream = toInputStream(csv);

        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);

        assertThat(parsedEntries).hasSize(1);
        assertThat(incorrectEntries).isEmpty();

        TestData data = parsedEntries.getFirst().value();
        assertThat(data.getFirstName()).isEqualTo("John");
        assertThat(data.getLastName()).isEqualTo("Doe");
        assertThat(data.getAge()).isEqualTo(30);
    }

    @Test
    void shouldParseValidCsvWithMultipleRows() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                Jane,Smith,25
                Bob,Johnson,35
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(3);
        assertThat(incorrectEntries).isEmpty();
        assertThat(parsedEntries.get(0).value().getFirstName()).isEqualTo("John");
        assertThat(parsedEntries.get(1).value().getFirstName()).isEqualTo("Jane");
        assertThat(parsedEntries.get(2).value().getFirstName()).isEqualTo("Bob");
    }

    @Test
    void shouldHandleEmptyInputStream() {
        InputStream inputStream = toInputStream("");
        var e = assertThrows(CustomException.class, () -> csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add));
        assertEquals("File is empty - no header found!", e.getMessage());
    }

    @Test
    void shouldValidateHeader() {
        InputStream inputStream = toInputStream("firstName,lastName");
        var e = assertThrows(CustomException.class, () -> csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add));
        assertEquals("Column is missing: age", e.getMessage());
    }

    @Test
    void shouldHandleOnlyHeaderLine() {
        String csv = "firstName,lastName,age";
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).isEmpty();
        assertThat(incorrectEntries).isEmpty();
    }

    @Test
    void shouldHandleQuotedFieldsWithDelimiters() {
        String csv = """
                firstName,lastName,age
                John,"Doe, Jr.",30
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(1);
        assertThat(incorrectEntries).isEmpty();
        assertThat(parsedEntries.getFirst().value().getLastName()).isEqualTo("Doe, Jr.");
    }

    @Test
    void shouldHandleEscapedQuotes() {
        String csv = """
                firstName,lastName,age
                John,"Doe ""The Great""\",30
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(1);
        assertThat(incorrectEntries).isEmpty();
        assertThat(parsedEntries.getFirst().value().getLastName()).isEqualTo("Doe \"The Great\"");
    }

    @Test
    void shouldReportIncorrectRowsToUnparsedConsumer() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                Invalid,Data
                Jane,Smith,25
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(2);
        assertThat(incorrectEntries).hasSize(1);
        assertThat(incorrectEntries.getFirst().causeMessage()).contains("Line 3 can't be processed: expected 3 columns (header: firstName, lastName, age), but found 2 columns.");
    }

    @Test
    void shouldHandleInvalidAgeFormat() {
        String csv = """
                firstName,lastName,age
                John,Doe,notanumber
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).isEmpty();
        assertThat(incorrectEntries).hasSize(1);
        assertThat(incorrectEntries.getFirst().causeMessage()).contains("Invalid age format");
    }

    @Test
    void shouldHandleInvalidAgeRange() {
        String csv = """
                firstName,lastName,age
                John,Doe,200
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).isEmpty();
        assertThat(incorrectEntries).hasSize(1);
        assertThat(incorrectEntries.getFirst().causeMessage()).contains("Age must be between 0 and 150");
    }

    @Test
    void shouldCaptureIncorrectRowDetails() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                Bad,Row,
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(1);
        assertThat(incorrectEntries).hasSize(1);
        IncorrectRowEntry incorrectRow = incorrectEntries.getFirst();
        assertThat(incorrectRow).isNotNull();
        assertThat(incorrectRow.rowNumber()).isGreaterThan(2);
        assertThat(incorrectRow.causeMessage()).isEqualTo("Invalid age format: For input string: \"\"");
    }

    @Test
    void shouldHandleTrailingNewlines() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                
                
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(1);
        assertThat(incorrectEntries).hasSize(2);
    }

    @Test
    void shouldHandleRowsWithEmptyFields() {
        String csv = """
                firstName,lastName,age
                John,,30
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).isEmpty();
        assertThat(incorrectEntries).hasSize(1);
        assertThat(incorrectEntries.getFirst().causeMessage()).contains("cannot be empty");
    }

    @Test
    void shouldTrimWhitespace() {
        String csv = """
                firstName,lastName,age
                  John  ,  Doe  ,  30 \s
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(1);
        TestData data = parsedEntries.getFirst().value();
        assertThat(data.getFirstName()).isEqualTo("John");
        assertThat(data.getLastName()).isEqualTo("Doe");
        assertThat(data.getAge()).isEqualTo(30);
    }

    @Test
    void shouldHandleSpecialCharacters() {
        // given
        String csv = """
                firstName,lastName,age
                José,O'Brien,30
                Müller,François,25
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(2);
        assertThat(incorrectEntries).isEmpty();
        assertThat(parsedEntries.get(0).value().getFirstName()).isEqualTo("José");
        assertThat(parsedEntries.get(1).value().getFirstName()).isEqualTo("Müller");
    }

    @Test
    void shouldHandleQuotedEmptyFields() {
        String csv = """
                firstName,lastName,age
                John,"",30
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).isEmpty();
        assertThat(incorrectEntries).hasSize(1);
    }

    @Test
    void shouldHandleMultipleIncorrectRows() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                Bad
                Jane,Smith,25
                Also,Bad
                Bob,Johnson,35
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(3);
        assertThat(incorrectEntries).hasSize(2);
    }

    @Test
    void shouldHandleWindowsLineEndings() {
        String csv = """
                firstName,lastName,age\r
                John,Doe,30\r
                Jane,Smith,25\r
                """;
        InputStream inputStream = toInputStream(csv);
        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(2);
        assertThat(incorrectEntries).isEmpty();
    }

    @Test
    void shouldPreserveRowOrder() {
        String csv = """
                firstName,lastName,age
                Alice,Adams,20
                Bob,Brown,30
                Charlie,Clark,40
                """;
        InputStream inputStream = toInputStream(csv);

        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);
        assertThat(parsedEntries).hasSize(3);
        assertThat(parsedEntries.get(0).value().getFirstName()).isEqualTo("Alice");
        assertThat(parsedEntries.get(1).value().getFirstName()).isEqualTo("Bob");
        assertThat(parsedEntries.get(2).value().getFirstName()).isEqualTo("Charlie");
    }

    @Test
    void shouldHandleMixedValidAndInvalidRows() {
        String csv = """
                firstName,lastName,age
                John,Doe,30
                Jane,Smith,invalid
                Bob,Johnson,25
                ,Empty,30
                Alice,Adams,35
                """;
        InputStream inputStream = toInputStream(csv);

        csvParser.parse(inputStream, parsedEntries::add, incorrectEntries::add);

        assertThat(parsedEntries).hasSize(3);
        assertThat(incorrectEntries).hasSize(2);

        assertThat(parsedEntries.get(0).value().getFirstName()).isEqualTo("John");
        assertThat(parsedEntries.get(1).value().getFirstName()).isEqualTo("Bob");
        assertThat(parsedEntries.get(2).value().getFirstName()).isEqualTo("Alice");
    }

    private InputStream toInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    // Test data class
    private static class TestData {
        private final String firstName;
        private final String lastName;
        private final int age;

        public TestData(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }
    }
}