package com.sinkovits.rent.generator.batch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BatchPdfGeneratorResult {

	public static final String STATUS_OK = "OK";
	public static final String STATUS_ERROR = "ERROR";

	private final String status;
	private final List<String> errors;

	private BatchPdfGeneratorResult(BatchPdfGeneratorResultBuilder builder) {
		status = builder.status;
		errors = Collections.unmodifiableList(builder.errors);
	}

	public String getStatus() {
		return status == null ? STATUS_OK : status;
	}

	public List<String> getErrors() {
		return errors;
	}

	public static BatchPdfGeneratorResultBuilder builder() {
		return new BatchPdfGeneratorResultBuilder();
	}

	public static class BatchPdfGeneratorResultBuilder {

		private String status;
		private List<String> errors = new LinkedList<>();

		public BatchPdfGeneratorResultBuilder setStatus(String status) {
			this.status = status;
			return this;
		}

		public BatchPdfGeneratorResultBuilder addErrorMessage(String message) {
			errors.add(message);
			return this;
		}

		public BatchPdfGeneratorResult result() {
			BatchPdfGeneratorResult result = new BatchPdfGeneratorResult(this);
			return result;
		}

	}
}
