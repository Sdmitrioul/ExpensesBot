package com.dskroba.expenses;

import com.dskroba.base.formatter.FieldSelector;
import com.dskroba.base.formatter.extractor.*;
import com.dskroba.base.formatter.extractor.type.TypeValue;

import java.util.Arrays;
import java.util.List;

public enum ExpenseFieldSelectors implements FieldSelector<Expense> {
    TAG(EnumExtractor.of(Expense::expenseType)),
    AMOUNT(DoubleExtractor.of(Expense::amount)),
    CURRENCY(EnumExtractor.of(Expense::currency)),
    DATE(DateExtractor.of(Expense::date)),
    DESCRIPTION(StringExtractor.of(Expense::description)),
    ISSUER(StringExtractor.of(Expense::issuer));

    public static final List<ExpenseFieldSelectors> ALL_FIELDS_SELECTOR =
            Arrays.stream(ExpenseFieldSelectors.values()).toList();
    public static final List<FieldSelector<Expense>> BASIC_SELECTORS =
            List.of(TAG, AMOUNT, CURRENCY, DATE, DESCRIPTION);

    private final Extractor<Expense, ? extends TypeValue<?>> extractor;

    ExpenseFieldSelectors(Extractor<Expense, ? extends TypeValue<?>> extractor) {
        this.extractor = extractor;
    }

    @Override
    public Extractor<Expense, ? extends TypeValue<?>> valueExtractor() {
        return extractor;
    }
}
