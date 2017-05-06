package com.sinkovits.rent.generator.batch.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;

import com.sinkovits.rent.generator.batch.processor.PdfProcessor;

public class PdfProcessorTest {

	private PdfProcessor processor;

	@Before
	public void setUp() {
		processor = new PdfProcessor();
	}

	@Test
	public void testProcessPdf() throws IOException {
		// When
		Optional<PDDocument> result = processor.process(getTestFile("testPdf1.pdf"));
		
		// Then
		assertTrue(result.isPresent());
		result.get().close();
	}

	@Test
	public void testProcessNonExistingPdf() throws IOException {
		// When
		Optional<PDDocument> result = processor.process(Paths.get("testPdfNA.pdf"));
		
		// Then
		assertFalse(result.isPresent());
	}

	private Path getTestFile(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return file.toPath();
	}
}
