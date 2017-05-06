package com.sinkovits.rent.generator.batch.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class DelegateProcessorTest {

	@Mock
	private FileProcessor mockProcessor1;
	@Mock
	private FileProcessor mockProcessor2;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void testProcessWithEligibleProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(true);
		when(mockProcessor1.process(testPath)).thenReturn(Optional.of(mock(PDDocument.class)));

		// When
		Optional<PDDocument> result = processor.process(testPath);

		// Then
		assertTrue(result.isPresent());
	}

	@Test
	public void testProcessWithNonEligibleProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(false);
		
		// When
		Optional<PDDocument> result = processor.process(testPath);
		
		// Then
		assertFalse(result.isPresent());
	}

	@Test
	public void testCanProcessWithEligibleProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(true);
		
		// When
		boolean result = processor.canProcess(testPath);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void testCanProcessWithNonEligibleProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(false);
		
		// When
		boolean result = processor.canProcess(testPath);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void testProcessWithTwoProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1, mockProcessor2);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(false);
		when(mockProcessor2.canProcess(testPath)).thenReturn(true);
		when(mockProcessor2.process(testPath)).thenReturn(Optional.of(mock(PDDocument.class)));
		
		// When
		Optional<PDDocument> result = processor.process(testPath);
		
		// Then
		assertTrue(result.isPresent());
	}

	@Test
	public void testCanProcessWithTwoProcessor() throws IOException {
		// Given
		DelegateProcessor processor = new DelegateProcessor(mockProcessor1, mockProcessor2);
		Path testPath = Paths.get("test");
		when(mockProcessor1.canProcess(testPath)).thenReturn(false);
		when(mockProcessor2.canProcess(testPath)).thenReturn(true);
		
		// When
		boolean result = processor.canProcess(testPath);
		
		// Then
		assertTrue(result);
	}

}
