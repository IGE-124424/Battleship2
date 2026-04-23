package battleship;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportGeneratorTest {

    private static Path tempDir;
    private static Path outputFile;

    @BeforeAll
    static void setup() throws Exception {
        tempDir = Files.createTempDirectory("pdf_test");
        outputFile = tempDir.resolve("report.pdf");
    }

    @AfterAll
    static void cleanup() throws Exception {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {}
                });
    }

    // ----------------------------------------------------

    @Test
    void testGenerateWithNullLines() throws Exception {
        Path result = PdfReportGenerator.generateMovesReport(null, outputFile);

        assertNotNull(result);
        assertTrue(Files.exists(result));
        assertEquals(outputFile, result);
    }

    @Test
    void testGenerateWithEmptyLines() throws Exception {
        Path result = PdfReportGenerator.generateMovesReport(new ArrayList<>(), outputFile);

        assertTrue(Files.exists(result));
    }

    @Test
    void testGenerateWithSimpleLines() throws Exception {
        List<String> lines = List.of(
                "Jogada qualquer",
                "Outra jogada"
        );

        Path result = PdfReportGenerator.generateMovesReport(lines, outputFile);

        assertTrue(Files.exists(result));
    }

    @Test
    void testGenerateWithSpecialFleetMessages() throws Exception {
        List<String> lines = List.of(
                "Frota aleatória gerada.",
                "Frota personalizada carregada."
        );

        Path result = PdfReportGenerator.generateMovesReport(lines, outputFile);

        assertTrue(Files.exists(result));
    }

    @Test
    void testGenerateWithCoordinates() throws Exception {
        List<String> lines = List.of(
                "{\"row\":\"A\",\"column\":1}",
                "{\"row\":\"B\",\"column\":5}"
        );

        Path result = PdfReportGenerator.generateMovesReport(lines, outputFile);

        assertTrue(Files.exists(result));
    }

    @Test
    void testGenerateIgnoresBlankLines() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add("   ");
        lines.add(null);
        lines.add("{\"row\":\"C\",\"column\":3}");

        Path result = PdfReportGenerator.generateMovesReport(lines, outputFile);

        assertTrue(Files.exists(result));
    }

    @Test
    void testDirectoryCreation() throws Exception {
        Path newPath = tempDir.resolve("subdir/report.pdf");

        Path result = PdfReportGenerator.generateMovesReport(
                List.of("Teste"),
                newPath
        );

        assertTrue(Files.exists(result));
    }
}
