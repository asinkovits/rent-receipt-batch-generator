package com.sinkovits.rent.generator.batch;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinkovits.rent.generator.batch.BatchPdfGeneratorResult.BatchPdfGeneratorResultBuilder;
import com.sinkovits.rent.generator.batch.processor.FileProcessor;

public class BatchPdfGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchPdfGenerator.class);

	private PathMatcher pdfMatcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
	private PDDocumentHolder documentHolder = new PDDocumentHolder();
	private FileProcessor processor;

	public BatchPdfGeneratorResult generate(List<Path> files, Path outputFile) throws Exception {
		notNull(processor, "File processor is not set!");
		BatchPdfGeneratorResultBuilder resultBuilder = BatchPdfGeneratorResult.builder();
		try {
			validateInputFiles(resultBuilder, files);
			validateOutputFile(resultBuilder, outputFile);
			if (!resultBuilder.hasErrors()) {
				generateBatchPdf(resultBuilder, files, outputFile);
			}
		} finally {
			documentHolder.close();
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

		processFiles(files);
		validateProcessedFiles(resultBuilder);
		if (!resultBuilder.hasErrors()) {
			saveFinalPdf(outputFile);
		}
	}

	private void processFiles(List<Path> files) throws IOException {
		for (Path path : files) {
			Optional<PDDocument> document = processFile(path);
			document.ifPresent(doc -> documentHolder.addDocument(doc));
			if (!document.isPresent()) {
				LOGGER.warn(path + " could not be converted. Skipping!");
			}
		}
	}

	private void validateProcessedFiles(BatchPdfGeneratorResultBuilder resultBuilder) {
		if (documentHolder.isEmpty()) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("None of the input files could be processed!");
		}
	}

	private Optional<PDDocument> processFile(Path path) throws IOException {
		if (processor.canProcess(path)) {
			return processor.process(path);
		} else {
			return Optional.empty();
		}
	}

	private void saveFinalPdf(Path outputFile) throws IOException {
		try (PDDocument doc = mergeDocuments()) {
			doc.save(outputFile.toFile());
		}
	}

	private PDDocument mergeDocuments() throws IOException {
		PDDocument doc = new PDDocument();
		for (PDDocument pdf : documentHolder.getDocuments()) {
			merge(doc, pdf);
		}
		return doc;
	}

	private void merge(PDDocument doc1, PDDocument doc2) throws IOException {
		for (PDPage page : doc2.getPages()) {
			doc1.addPage(page);
		}
	}

	public void setProcessor(FileProcessor processor) {
		this.processor = processor;
	}

}
