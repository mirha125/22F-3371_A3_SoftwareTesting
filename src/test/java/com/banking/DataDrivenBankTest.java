package com.banking;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 3: Data-Driven Testing using CSV and Excel sources.
 * Tests BankAccount deposit behavior across Valid, Invalid, Edge, and Stress categories.
 */
@Tag("slow")
@DisplayName("Data-Driven Bank Account Tests")
class DataDrivenBankTest {

    private static final String CSV_RESOURCE = "/bank_test_data.csv";
    private static Path excelFilePath;

    // ─── Excel Setup ──────────────────────────────────────────────────────────

    @BeforeAll
    static void generateExcelFile() throws Exception {
        // Generate Excel test data file programmatically if not present
        excelFilePath = Paths.get("src/test/resources/bank_test_data.xlsx");
        if (!Files.exists(excelFilePath)) {
            createExcelTestData(excelFilePath);
        }
    }

    // ─── CSV-Driven Tests ─────────────────────────────────────────────────────

    @ParameterizedTest(name = "[CSV] {0} - testId:{1} deposit:{2} initial:{3}")
    @MethodSource("provideCsvDepositData")
    @DisplayName("should_HandleDeposit_when_CsvDataProvided")
    void should_HandleDeposit_when_CsvDataProvided(
            String category, int testId, double depositAmount,
            double initialBalance, double expectedBalance, boolean shouldSucceed) {

        // Arrange
        BankAccount account = new BankAccount("ACC-" + testId, "Test User", initialBalance);

        if (shouldSucceed) {
            // Act
            account.deposit(depositAmount);

            // Assert
            assertEquals(expectedBalance, account.getBalance(), 0.01,
                    String.format("Category=%s testId=%d", category, testId));
        } else {
            // Act / Assert — invalid input should throw
            assertThrows(Exception.class, () -> account.deposit(depositAmount),
                    String.format("Category=%s testId=%d should throw", category, testId));
        }
    }

    static Stream<Arguments> provideCsvDepositData() throws Exception {
        List<Arguments> args = new ArrayList<>();
        try (InputStream is = DataDrivenBankTest.class.getResourceAsStream(CSV_RESOURCE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                args.add(Arguments.of(
                        record.get("category"),
                        Integer.parseInt(record.get("testId")),
                        Double.parseDouble(record.get("depositAmount")),
                        Double.parseDouble(record.get("initialBalance")),
                        Double.parseDouble(record.get("expectedBalance")),
                        Boolean.parseBoolean(record.get("shouldSucceed"))
                ));
            }
        }
        return args.stream();
    }

    // ─── Excel-Driven Tests ───────────────────────────────────────────────────

    @ParameterizedTest(name = "[Excel] {0} - testId:{1} deposit:{2}")
    @MethodSource("provideExcelDepositData")
    @DisplayName("should_HandleDeposit_when_ExcelDataProvided")
    void should_HandleDeposit_when_ExcelDataProvided(
            String category, int testId, double depositAmount,
            double initialBalance, double expectedBalance, boolean shouldSucceed) {

        // Arrange
        BankAccount account = new BankAccount("EXCEL-" + testId, "Excel User", initialBalance);

        if (shouldSucceed) {
            // Act
            account.deposit(depositAmount);

            // Assert
            assertEquals(expectedBalance, account.getBalance(), 0.01,
                    String.format("Excel Category=%s testId=%d", category, testId));
        } else {
            // Act / Assert
            assertThrows(Exception.class, () -> account.deposit(depositAmount),
                    String.format("Excel Category=%s testId=%d should throw", category, testId));
        }
    }

    static Stream<Arguments> provideExcelDepositData() throws Exception {
        List<Arguments> args = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath.toFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("DepositTests");
            if (sheet == null) {
                throw new IOException("Sheet 'DepositTests' not found in Excel file.");
            }

            Row headerRow = sheet.getRow(0);
            // columns: category, testId, depositAmount, initialBalance, expectedBalance, shouldSucceed
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                args.add(Arguments.of(
                        getCellString(row.getCell(0)),
                        (int) row.getCell(1).getNumericCellValue(),
                        row.getCell(2).getNumericCellValue(),
                        row.getCell(3).getNumericCellValue(),
                        row.getCell(4).getNumericCellValue(),
                        getCellBoolean(row.getCell(5))
                ));
            }
        }
        return args.stream();
    }

    // ─── Excel File Generator ─────────────────────────────────────────────────

    private static void createExcelTestData(Path path) throws Exception {
        Files.createDirectories(path.getParent());

        try (Workbook workbook = new XSSFWorkbook()) {

            // Sheet 1: DepositTests
            Sheet depositSheet = workbook.createSheet("DepositTests");
            String[] headers = {"category", "testId", "depositAmount", "initialBalance", "expectedBalance", "shouldSucceed"};
            Row headerRow = depositSheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            Object[][] depositData = {
                // Valid
                {"Valid",   1,  500.0,   1000.0, 1500.0, true},
                {"Valid",   2,  1000.0,  500.0,  1500.0, true},
                {"Valid",   3,  100.0,   0.0,    100.0,  true},
                {"Valid",   4,  9999.99, 0.01,   10000.0,true},
                {"Valid",   5,  250.0,   750.0,  1000.0, true},
                // Invalid
                {"Invalid", 6,  -100.0,  500.0,  500.0,  false},
                {"Invalid", 7,  0.0,     500.0,  500.0,  false},
                {"Invalid", 8,  -1.0,    1000.0, 1000.0, false},
                {"Invalid", 9,  -999.99, 200.0,  200.0,  false},
                {"Invalid", 10, -0.01,   300.0,  300.0,  false},
                // Edge
                {"Edge",    11, 0.01,    0.0,    0.01,   true},
                {"Edge",    12, 10000.0, 0.0,    10000.0,true},
                {"Edge",    13, 0.001,   999.999,1000.0, true},
                {"Edge",    14, 1.0,     9999.0, 10000.0,true},
                {"Edge",    15, 100.0,   0.0,    100.0,  true},
                // Stress
                {"Stress",  16, 999999.99, 0.0,   999999.99,  true},
                {"Stress",  17, 500000.0,  500000.0,1000000.0,true},
                {"Stress",  18, 123456.78, 876543.22,1000000.0,true},
                {"Stress",  19, 1000000.0, 0.0,    1000000.0, true},
                {"Stress",  20, 750000.0,  250000.0,1000000.0,true},
            };

            for (int i = 0; i < depositData.length; i++) {
                Row row = depositSheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) depositData[i][0]);
                row.createCell(1).setCellValue((Integer) depositData[i][1]);
                row.createCell(2).setCellValue((Double) depositData[i][2]);
                row.createCell(3).setCellValue((Double) depositData[i][3]);
                row.createCell(4).setCellValue((Double) depositData[i][4]);
                row.createCell(5).setCellValue((Boolean) depositData[i][5]);
            }

            // Sheet 2: LoanTests
            Sheet loanSheet = workbook.createSheet("LoanTests");
            String[] loanHeaders = {"category", "testId", "principal", "annualRate", "tenureMonths", "annualIncome", "expectedEligible"};
            Row loanHeader = loanSheet.createRow(0);
            for (int i = 0; i < loanHeaders.length; i++) {
                loanHeader.createCell(i).setCellValue(loanHeaders[i]);
            }

            Object[][] loanData = {
                // Valid
                {"Valid",   1,  500000.0,  12.0, 60,  1200000.0, true},
                {"Valid",   2,  1000000.0, 10.0, 120, 3000000.0, true},
                {"Valid",   3,  200000.0,  8.0,  24,  800000.0,  true},
                {"Valid",   4,  300000.0,  0.0,  12,  900000.0,  true},
                {"Valid",   5,  750000.0,  15.0, 84,  2500000.0, true},
                // Invalid
                {"Invalid", 6,  500000.0,  12.0, 60,  100000.0,  false},
                {"Invalid", 7,  500000.0,  12.0, 60,  200000.0,  false},
                {"Invalid", 8,  5000000.0, 20.0, 60,  400000.0,  false},
                {"Invalid", 9,  2000000.0, 15.0, 60,  500000.0,  false},
                {"Invalid", 10, 800000.0,  12.0, 60,  250000.0,  false},
                // Edge
                {"Edge",    11, 300001.0,  12.0, 60, 299999.0,   false},
                {"Edge",    12, 1.0,       0.0,  1,  300000.0,   true},
                {"Edge",    13, 500000.0,  12.0, 360, 2000000.0, true},
                {"Edge",    14, 300000.0,  12.0, 60,  900000.0,  true},
                {"Edge",    15, 200000.0,  8.0,  24,  300001.0,  true},
                // Stress
                {"Stress",  16, 10000000.0, 12.0, 360, 50000000.0, true},
                {"Stress",  17, 5000000.0,  8.0,  120, 20000000.0, true},
                {"Stress",  18, 2000000.0,  15.0, 240, 10000000.0, true},
                {"Stress",  19, 1000000.0,  20.0, 180, 8000000.0,  true},
                {"Stress",  20, 3000000.0,  10.0, 300, 15000000.0, true},
            };

            for (int i = 0; i < loanData.length; i++) {
                Row row = loanSheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) loanData[i][0]);
                row.createCell(1).setCellValue((Integer) loanData[i][1]);
                row.createCell(2).setCellValue((Double) loanData[i][2]);
                row.createCell(3).setCellValue((Double) loanData[i][3]);
                row.createCell(4).setCellValue((Integer) loanData[i][4]);
                row.createCell(5).setCellValue((Double) loanData[i][5]);
                row.createCell(6).setCellValue((Boolean) loanData[i][6]);
            }

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }
        }
        System.out.println("[DataDrivenBankTest] Excel test data generated at: " + path.toAbsolutePath());
    }

    // ─── Excel-Driven Loan Eligibility Tests ──────────────────────────────────

    @ParameterizedTest(name = "[Excel-Loan] {0} - testId:{1}")
    @MethodSource("provideExcelLoanEligibilityData")
    @DisplayName("should_DetermineLoanEligibility_when_ExcelDataProvided")
    void should_DetermineLoanEligibility_when_ExcelDataProvided(
            String category, int testId, double principal, double annualRate,
            int tenureMonths, double annualIncome, boolean expectedEligible) {

        // Arrange
        LoanCalculator calculator = new LoanCalculator();

        // Act
        boolean eligible = calculator.isEligible(annualIncome, principal, 0, annualRate, tenureMonths);

        // Assert
        assertEquals(expectedEligible, eligible,
                String.format("Category=%s testId=%d", category, testId));
    }

    static Stream<Arguments> provideExcelLoanEligibilityData() throws Exception {
        List<Arguments> args = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath.toFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("LoanTests");
            if (sheet == null) throw new IOException("Sheet 'LoanTests' not found.");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                args.add(Arguments.of(
                        getCellString(row.getCell(0)),
                        (int) row.getCell(1).getNumericCellValue(),
                        row.getCell(2).getNumericCellValue(),
                        row.getCell(3).getNumericCellValue(),
                        (int) row.getCell(4).getNumericCellValue(),
                        row.getCell(5).getNumericCellValue(),
                        getCellBoolean(row.getCell(6))
                ));
            }
        }
        return args.stream();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        return "";
    }

    private static boolean getCellBoolean(Cell cell) {
        if (cell == null) return false;
        if (cell.getCellType() == CellType.BOOLEAN) return cell.getBooleanCellValue();
        if (cell.getCellType() == CellType.STRING) return Boolean.parseBoolean(cell.getStringCellValue());
        return false;
    }
}
