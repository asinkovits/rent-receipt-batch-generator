package com.sinkovits.rent.generator.batch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinkovits.rent.generator.batch.BatchPdfGeneratorResult.BatchPdfGeneratorResultBuilder;

public class BatchPdfGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchPdfGenerator.class);

	private PathMatcher pdfMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");

	public BatchPdfGeneratorResult generate(List<Path> files, Path outputFile) throws Exception {
		BatchPdfGeneratorResultBuilder resultBuilder = BatchPdfGeneratorResult.builder();
		validateInputFiles(resultBuilder, files);
		validateOutputFile(resultBuilder, outputFile);
		if (!resultBuilder.hasErrors()) {
			generateBatchPdf(resultBuilder, files, outputFile);
		}
		return resultBuilder.result();
	}

	private void validateInputFiles(BatchPdfGeneratorResultBuilder resultBuilder, List<Path> files) {
		if (files == null) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("The input files are null!");
		} else if (files.isEmpty()) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("The input files are empty!");
		} else {
			checkIfAtLeastOneInputFileIsValid(resultBuilder, files);
		}
	}

	private void checkIfAtLeastOneInputFileIsValid(BatchPdfGeneratorResultBuilder resultBuilder, List<Path> files) {
		List<String> notFound = new ArrayList<>();
		for (Path file : files) {
			if (!file.toFile().exists()) {
				notFound.add(file + " not found!");
			}
		}
		if (notFound.size() == files.size()) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("None of the input files were found!");
		}
	}

	private void validateOutputFile(BatchPdfGeneratorResultBuilder resultBuilder, Path outputFile) {
		if (outputFile == null) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("The output file is null!");
		} else if (!pdfMatcher.matches(outputFile.getFileName())) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("The output file should be a pdf!");
		}
	}

	private void generateBatchPdf(BatchPdfGeneratorResultBuilder resultBuilder, List<Path> files, Path outputFile)
			throws IOException {
		List<PDDocument> pdfFiles = new ArrayList<>();
		try {
			processFiles(files, pdfFiles);
			validateProcessedFiles(resultBuilder, pdfFiles);
			if (!resultBuilder.hasErrors()) {
				saveFinalPdf(outputFile, pdfFiles);
			}
		} finally {
			closeDocuments(pdfFiles);
		}
	}

	private void processFiles(List<Path> files, List<PDDocument> pdfFiles) throws IOException {
		for (Path path : files) {
			Optional<PDDocument> document = processFile(path);
			document.ifPresent(doc -> pdfFiles.add(doc));
			if (!document.isPresent()) {
				LOGGER.warn(path + " could not be converted. Skipping!");
			}
		}
	}

	private void validateProcessedFiles(BatchPdfGeneratorResultBuilder resultBuilder, List<PDDocument> pdfFiles) {
		if (pdfFiles.isEmpty()) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("None of the input files could be processed!");
		}
	}

	private Optional<PDDocument> processFile(Path path) throws IOException {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		PDImageXObject pdImage = PDImageXObject.createFromFileByExtension(path.toFile(), doc);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true);
		float scale = calculateScale(pdImage, page.getBBox());
		contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
		contentStream.close();
		doc.addPage(page);
		return Optional.of(doc);
	}

	private float calculateScale(PDImageXObject pdImage, PDRectangle bBox) {
		float xs = bBox.getWidth() / pdImage.getWidth();
		float ys = bBox.getHeight() / pdImage.getHeight();
		return Math.min(xs, ys);
	}

	private void saveFinalPdf(Path outputFile, List<PDDocument> pdfFiles) throws IOException {
		try (PDDocument doc = mergeDocuments(pdfFiles)) {
			doc.save(outputFile.toFile());
		}
	}

	private PDDocument mergeDocuments(List<PDDocument> pdfFiles) throws IOException {
		PDDocument doc = new PDDocument();
		for (PDDocument pdf : pdfFiles) {
			merge(doc, pdf);
		}
		return doc;
	}

	private void merge(PDDocument doc1, PDDocument doc2) throws IOException {
		for (PDPage page : doc2.getPages()) {
			doc1.addPage(page);
		}
	}

	private void closeDocuments(List<PDDocument> pdfFiles) {
		for (PDDocument pdDocument : pdfFiles) {
			try {
				pdDocument.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
