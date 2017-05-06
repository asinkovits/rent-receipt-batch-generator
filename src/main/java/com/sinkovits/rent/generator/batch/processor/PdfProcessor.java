package com.sinkovits.rent.generator.batch.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfProcessor extends BaseFileProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessor.class);

	public PdfProcessor() {
		super("pdf");
	}

	public Optional<PDDocument> process(Path path) {
		try {
			PDDocument pdf = PDDocument.load(path.toFile());
			return Optional.of(pdf);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			return Optional.empty();
		}
	}
}
