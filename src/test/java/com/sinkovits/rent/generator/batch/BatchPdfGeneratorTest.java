package com.sinkovits.rent.generator.batch;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sinkovits.rent.generator.batch.processor.DelegateProcessor;
import com.sinkovits.rent.generator.batch.processor.ImageProcessor;
import com.sinkovits.rent.generator.batch.processor.PdfProcessor;

public class BatchPdfGeneratorTest {

	private BatchPdfGenerator generator;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() {
		generator = new BatchPdfGenerator();
		generator.setProcessor(new DelegateProcessor(new PdfProcessor(), new ImageProcessor()));
	}

	@Test
	public void canGeneratePdfFromImage() throws Exception {
		// Given
		Path outputFolder = folder.newFolder("test").toPath();
		Path outputFile = outputFolder.resolve("output.pdf");
		List<Path> files = new ArrayList<>();
		files.add(getTestFile("testImage1.png"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_OK));
		assertTrue(outputFile.toFile().exists());
	}

	@Test
	public void canGeneratePdfFromPdf() throws Exception {
		// Given
		Path outputFolder = folder.newFolder("test").toPath();
		Path outputFile = outputFolder.resolve("output.pdf");
		List<Path> files = new ArrayList<>();
		files.add(getTestFile("testPdf1.pdf"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_OK));
		assertTrue(outputFile.toFile().exists());
	}

	@Test
	public void noneOfTheInputFilesCanBeProcessed() throws Exception {
		// Given
		Path outputFolder = folder.newFolder("test").toPath();
		Path outputFile = outputFolder.resolve("output.pdf");
		List<Path> files = new ArrayList<>();
		files.add(getTestFile("test1.txt"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertFalse(outputFile.toFile().exists());
	}

	@Test
	public void errorInCaseOfNullOutput() throws Exception {
		// Given
		Path outputFile = null;
		List<Path> files = new ArrayList<>();
		files.add(getTestFile("testImage1.png"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}

	@Test
	public void errorInCaseOfOutputIsNotAPdfFile() throws Exception {
		// Given
		Path outputFile = Paths.get("output.txt");
		List<Path> files = new ArrayList<>();
		files.add(getTestFile("testImage1.png"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}

	@Test
	public void errorInCaseOfNullInput() throws Exception {
		// Given
		Path outputFile = Paths.get("output.pdf");
		List<Path> files = null;

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

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
		List<Path> files = new ArrayList<>();

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}

	@Test
	public void errorInCaseOfMultipleInputViolations() throws Exception {
		// Given
		Path outputFile = null;
		List<Path> files = new ArrayList<>();

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(2));
	}

	@Test
	public void errorInCaseOfAllTheInputFilesAreNotFound() throws Exception {
		// Given
		Path outputFile = Paths.get("output.pdf");
		List<Path> files = new ArrayList<>();
		files.add(Paths.get("invalid.pdf"));

		// When
		BatchPdfGeneratorResult result = generator.generate(files, outputFile);

		// Then
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(BatchPdfGeneratorResult.STATUS_ERROR));
		assertThat(result.getErrors(), not(empty()));
		assertThat(result.getErrors(), hasSize(1));
	}

	private Path getTestFile(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return file.toPath();
	}
}
