package com.sinkovits.rent.generator.batch.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sinkovits.rent.generator.batch.PDElementFactory;
import com.sinkovits.rent.generator.batch.processor.ImageProcessor;

public class ImageProcessorTest {

	private ImageProcessor processor;

	@Before
	public void setUp() {
		processor = new ImageProcessor(new PDElementFactory());
	}

	@Test
	public void testProcessImage() throws IOException {
		// When
		Optional<PDDocument> result = processor.process(getTestFile("testImage1.png"));

		// Then
		assertTrue(result.isPresent());
		result.get().close();
	}

	@Test
	public void testProcessLargeImage() throws IOException {
		// When
		Optional<PDDocument> result = processor.process(getTestFile("testImage2.png"));
		
		// Then
		assertTrue(result.isPresent());
		result.get().close();
	}

	@Test
	public void testProcessImageException() throws IOException {
		// Given
		Path testFile = getTestFile("testImage1.png");
		PDElementFactory mockPdElementFactory = Mockito.mock(PDElementFactory.class);
		ImageProcessor processor = new ImageProcessor(mockPdElementFactory);
		Mockito.when(mockPdElementFactory.createPDImageXObject(testFile, null)).thenThrow(IOException.class);

		// When
		Optional<PDDocument> result = processor.process(testFile);

		// Then
		assertFalse(result.isPresent());
	}

	private Path getTestFile(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return file.toPath();
	}
}
