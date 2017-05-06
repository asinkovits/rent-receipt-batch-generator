package com.sinkovits.rent.generator.batch;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDElementFactory {

	public PDDocument createPDDocument() {
		return new PDDocument();
	}

	public PDPage createPDPage() {
		return new PDPage();
	}

	public PDImageXObject createPDImageXObject(Path path, PDDocument doc) throws IOException {
		return PDImageXObject.createFromFileByExtension(path.toFile(), doc);
	}

	public PDPageContentStream createPDPageContentStream(PDDocument doc, PDPage page) throws IOException {
		return new PDPageContentStream(doc, page, AppendMode.APPEND, true);
	}

}
