package com.dskroba.expenses;

import com.dskroba.google.GoogleTable;
import com.dskroba.type.Currency;
import com.dskroba.type.Timeframe;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExpenseRepositoryImpl implements ExpenseRepository {
    private static final Logger log = LogManager.getLogger(ExpenseRepositoryImpl.class);

    private final Sheets service;

    @Autowired
    public ExpenseRepositoryImpl(Sheets service) {
        this.service = service;
    }

    @Override
    public boolean insertExpense(List<Expense> expenses, GoogleTable table) {
        try {
            List<List<Object>> rows = new ArrayList<>();

            for (Expense expense : expenses) {
                rows.add(List.of(
                        expense.expenseType().name(),
                        expense.amount(),
                        expense.currency().name(),
                        expense.description(),
                        expense.issuer(),
                        Timestamp.from(expense.date().toInstant()).toString()
                ));
            }

            ValueRange body = new ValueRange().setValues(rows);

            AppendValuesResponse response = service.spreadsheets()
                    .values()
                    .append(table.spreadsheetId(), table.range(), body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            log.info("Inserted {} expenses into Google Sheet",
                    response.getUpdates().getUpdatedRows());

            return true;

        } catch (Exception e) {
            log.error("Error while inserting expense into Google Sheet", e);
            return false;
        }
    }

    @Override
    public List<Expense> getExpenses(
            Timeframe timeframe,
            Currency currency,
            GoogleTable table
    ) {
        // Sheets API has no server-side filtering → read & filter
        try {
            var response = service.spreadsheets()
                    .values()
                    .get(table.spreadsheetId(), table.range())
                    .execute();

            List<Expense> result = new ArrayList<>();

            if (response.getValues() == null) {
                return result;
            }

            for (List<Object> row : response.getValues()) {
                Date createdAt = Timestamp.valueOf(row.get(6).toString());

                if (createdAt.toInstant().isBefore(timeframe.from())
                        || createdAt.toInstant().isAfter(timeframe.to())) {
                    continue;
                }

                if (!currency.name().equals(row.get(2).toString())) {
                    continue;
                }

                result.add(new Expense.Builder()
                        .type(row.get(0).toString())
                        .amount(Double.parseDouble(row.get(1).toString()))
                        .currency(row.get(2).toString())
                        .description(row.get(3).toString())
                        .issuerId(Long.parseLong(row.get(4).toString()))
                        .date(createdAt)
                        .build());
            }

            return result;

        } catch (Exception e) {
            log.error("Error while querying expenses from Google Sheet", e);
            return List.of();
        }
    }

    public static Sheets create(String credentialsPath, String appName) {

    }
}
