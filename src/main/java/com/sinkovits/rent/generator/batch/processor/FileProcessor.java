package com.sinkovits.rent.generator.batch.processor;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface FileProcessor {

	boolean canProcess(Path path);

	Optional<PDDocument> process(Path path);
}
