package com.sinkovits.rent.generator.batch;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDDocumentHolder implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDDocumentHolder.class);

	List<PDDocument> pdfFiles = new ArrayList<>();

	public void addDocument(PDDocument doc) {
		pdfFiles.add(doc);
	}

	public List<PDDocument> getDocuments() {
		return Collections.unmodifiableList(pdfFiles);
	}

	public boolean isEmpty() {
		return pdfFiles.isEmpty();
	}

	public void close() {
		for (PDDocument pdDocument : pdfFiles) {
			try {
				pdDocument.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		pdfFiles.clear();
	}
}
