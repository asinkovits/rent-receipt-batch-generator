package com.sinkovits.rent.generator.batch;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;

public class PDDocumentHolderTest {

	private PDDocumentHolder holder;

	@Before
	public void setUp() {
		holder = new PDDocumentHolder();
	}

	@Test
	public void testCloseQuietly() throws IOException {
		// Given
		PDDocument doc = mock(PDDocument.class);
		doThrow(IOException.class).when(doc).close();
		holder.addDocument(doc);

		// When
		holder.close();

		// Then
		// No exception happened
	}
}
