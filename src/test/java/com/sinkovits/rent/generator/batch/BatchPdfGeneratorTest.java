package com.sinkovits.rent.generator.batch;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class BatchPdfGeneratorTest {

	private BatchPdfGenerator generator;

	@Before
	public void setUp() {
		generator = new BatchPdfGenerator();
	}

	@Test
	public void canGeneratePdf() throws Exception {
		// Given
		Path outputFile = Paths.get("output.pdf");
		List<Path> files = Lists.newArrayList(Paths.get("test.pdf"));
		
		// When
		BatchPdfGeneratorResult result = generator.generate(outputFile, files);
		
		// Then
		assertThat(result, notNullValue());
	}

	@Test
	public void errorInCaseOfNullOutput() throws Exception {
		// Given
		Path outputFile = null;
		List<Path> files = Lists.newArrayList(Paths.get("test.pdf"));
		
		// When
		BatchPdfGeneratorResult result = generator.generate(outputFile, files);
		
		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}

	@Test
	public void errorInCaseOfEmptyInput() throws Exception {
		// Given
		Path outputFile = Paths.get("output.pdf");
		List<Path> files = Lists.newArrayList();
		
		// When
		BatchPdfGeneratorResult result = generator.generate(outputFile, files);
		
		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}
}
