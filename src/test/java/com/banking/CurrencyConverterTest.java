package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("CurrencyConverter Tests")
@TestMethodOrder(OrderAnnotation.class)
class CurrencyConverterTest {

    private CurrencyConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CurrencyConverter();
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_ConvertCorrectly_when_PKRtoUSD")
    void should_ConvertCorrectly_when_PKRtoUSD() {
        // Arrange
        double pkrAmount = 1000.0;

        // Act
        double usd = converter.convert(pkrAmount, "PKR", "USD");

        // Assert
        assertTrue(usd > 0);
        assertEquals(3.6, usd, 0.1);
    }

    @Test
    @Order(2)
    @DisplayName("should_ReturnSameAmount_when_SameCurrency")
    void should_ReturnSameAmount_when_SameCurrency() {
        // Arrange / Act
        double result = converter.convert(500.0, "PKR", "PKR");

        // Assert
        assertEquals(500.0, result, 0.01);
    }

    @Test
    @Order(3)
    @DisplayName("should_ReturnZero_when_AmountIsZero")
    void should_ReturnZero_when_AmountIsZero() {
        assertEquals(0.0, converter.convert(0, "PKR", "USD"), 0.01);
    }

    @Test
    @Order(4)
    @DisplayName("should_ConvertBothDirections_when_USDtoPKRandBack")
    void should_ConvertBothDirections_when_USDtoPKRandBack() {
        // Arrange
        double originalUSD = 100.0;

        // Act
        double pkr = converter.convert(originalUSD, "USD", "PKR");
        double backToUSD = converter.convert(pkr, "PKR", "USD");

        // Assert
        assertEquals(originalUSD, backToUSD, 1.0);
    }

    @Test
    @Order(5)
    @DisplayName("should_ListAllSupportedCurrencies_when_Called")
    void should_ListAllSupportedCurrencies_when_Called() {
        // Act
        var currencies = converter.getSupportedCurrencies();

        // Assert
        assertTrue(currencies.contains("PKR"));
        assertTrue(currencies.contains("USD"));
        assertTrue(currencies.contains("EUR"));
        assertTrue(currencies.size() >= 6);
    }

    @Test
    @Order(6)
    @DisplayName("should_UpdateRate_when_SetRateCalled")
    void should_UpdateRate_when_SetRateCalled() {
        // Arrange
        converter.setRate("JPY", 0.58);

        // Act
        double rate = converter.getRate("JPY");

        // Assert
        assertEquals(0.58, rate, 0.001);
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("should_ThrowException_when_NegativeAmount")
    void should_ThrowException_when_NegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(-100, "PKR", "USD"));
    }

    @Test
    @Order(8)
    @DisplayName("should_ThrowException_when_UnsupportedCurrency")
    void should_ThrowException_when_UnsupportedCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(100, "PKR", "XYZ"));
    }

    @Test
    @Order(9)
    @DisplayName("should_ThrowException_when_NullCurrencyCode")
    void should_ThrowException_when_NullCurrencyCode() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(100, null, "USD"));
    }

    @Test
    @Order(10)
    @DisplayName("should_ThrowException_when_SetRateWithZero")
    void should_ThrowException_when_SetRateWithZero() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.setRate("USD", 0));
    }

    @Test
    @Order(11)
    @DisplayName("should_ThrowException_when_GetRateForUnsupported")
    void should_ThrowException_when_GetRateForUnsupported() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.getRate("XYZ"));
    }

    @Test
    @Order(12)
    @DisplayName("should_ThrowException_when_SetRateWithNullCurrency")
    void should_ThrowException_when_SetRateWithNullCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.setRate(null, 1.0));
    }
}
