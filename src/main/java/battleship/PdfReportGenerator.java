package battleship;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfReportGenerator {

    public static Path generateMovesReport(List<String> lines, Path outputPdf) throws IOException {
        Path parent = outputPdf.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (PdfWriter writer = new PdfWriter(outputPdf.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Relatório de Jogadas - Battleship"));
            doc.add(new Paragraph("Gerado em: " + LocalDateTime.now()));
            doc.add(new Paragraph(" "));

            if (lines == null || lines.isEmpty()) {
                doc.add(new Paragraph("Sem jogadas para apresentar."));
            } else {
                int numeroJogada = 1;

                for (String line : lines) {
                    if (line == null || line.isBlank()) {
                        continue;
                    }

                    if (line.equalsIgnoreCase("Frota aleatória gerada.")
                            || line.equalsIgnoreCase("Frota personalizada carregada.")) {
                        doc.add(new Paragraph(line));
                        continue;
                    }

                    String jogadaFormatada = formatMoveLine(line, numeroJogada);
                    doc.add(new Paragraph(jogadaFormatada));
                    numeroJogada++;
                }
            }
        }

        return outputPdf;
    }

    private static String formatMoveLine(String rawLine, int numeroJogada) {
        List<String> coordenadas = extractCoordinates(rawLine);

        if (!coordenadas.isEmpty()) {
            return "Jogada " + numeroJogada + ": " + String.join(", ", coordenadas);
        }

        return "Jogada " + numeroJogada + ": " + rawLine.replaceAll("\\s+", " ").trim();
    }

    private static List<String> extractCoordinates(String text) {
        List<String> coordenadas = new ArrayList<>();

        Pattern pattern = Pattern.compile("\"row\"\\s*:\\s*\"([A-Z])\"\\s*,\\s*\"column\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String row = matcher.group(1);
            String column = matcher.group(2);
            coordenadas.add(row + column);
        }

        return coordenadas;
    }
}