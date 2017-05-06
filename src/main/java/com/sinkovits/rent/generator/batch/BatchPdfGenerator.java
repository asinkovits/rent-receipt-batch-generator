package com.sinkovits.rent.generator.batch;

import java.nio.file.Path;
import java.util.List;

import com.sinkovits.rent.generator.batch.BatchPdfGeneratorResult.BatchPdfGeneratorResultBuilder;

public class BatchPdfGenerator {

	public BatchPdfGeneratorResult generate(Path outputFile, List<Path> files) throws Exception {
		BatchPdfGeneratorResultBuilder resultBuilder = BatchPdfGeneratorResult.builder();
		validateInputs(resultBuilder, outputFile, files);
		return resultBuilder.result();
	}

	private void validateInputs(BatchPdfGeneratorResultBuilder resultBuilder, Path outputFile, List<Path> files) {
		if (outputFile == null) {
			resultBuilder.setStatus(BatchPdfGeneratorResult.STATUS_ERROR);
			resultBuilder.addErrorMessage("The output file is null!");
		}
	}

}
