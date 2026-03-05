package battleship;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class PdfReportGenerator {

    public static Path generateMovesReport(List<String> lines, Path outputPdf) throws IOException {
        // garante que a pasta existe (ex: target/)
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
                for (String line : lines) {
                    doc.add(new Paragraph(line));
                }
            }
        }

        return outputPdf;
    }
}

