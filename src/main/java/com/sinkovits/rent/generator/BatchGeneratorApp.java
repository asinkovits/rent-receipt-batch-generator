package com.sinkovits.rent.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sinkovits.rent.generator.batch.BatchPdfGenerator;
import com.sinkovits.rent.generator.batch.processor.DelegateProcessor;
import com.sinkovits.rent.generator.batch.processor.ImageProcessor;
import com.sinkovits.rent.generator.batch.processor.PdfProcessor;

@SpringBootApplication
public class BatchGeneratorApp implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchGeneratorApp.class);

	public static void main(String[] args) {
		SpringApplication.run(BatchGeneratorApp.class, args);
	}

	@Bean
	public BatchPdfGenerator generator() {
		BatchPdfGenerator generator = new BatchPdfGenerator();
		generator.setProcessor(new DelegateProcessor(new PdfProcessor(), new ImageProcessor()));
		return generator;
	}

	@Autowired
	private BatchPdfGenerator generator;

	@Override
	public void run(String... args) throws Exception {
		Path output = Paths.get(args[0]).resolve(args[args.length - 1]);
		List<Path> files = getBatchFiles(args);
		generator.generate(files, output);
	}

	private List<Path> getBatchFiles(String... args) {
		Path workDir = Paths.get(args[0]);
		List<Path> files = new ArrayList<>();
		for (int i = 1; i < args.length - 1; i++) {
			files.add(workDir.resolve(args[i]));
		}
		return files;
	}
}
