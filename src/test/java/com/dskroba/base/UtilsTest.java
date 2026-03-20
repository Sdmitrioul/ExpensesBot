package com.dskroba.base;

import com.dskroba.type.Currency;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {
    @Test
    public void testCreateLabels() {
        assertEquals(
                """
                        Penensilua -      110.00€
                        Car        - 55555555.50€
                        Home       -       10.00€""",
                String.join("\n", Utils.createLabels(Map.of("Home", 10.00,
                        "Penensilua", 110.00,
                        "Car", 55555555.50), Currency.EUR).values()));
    }
}
