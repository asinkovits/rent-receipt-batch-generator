package com.sinkovits.rent.generator.batch.processor;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

public class DelegateProcessor implements FileProcessor {

	private List<FileProcessor> processors;

	public DelegateProcessor(FileProcessor... processors) {
		Assert.notNull(processors, "Add at least one fileProcessor!");
		Assert.notEmpty(processors, "Add at least one fileProcessor!");
		this.processors = Lists.newArrayList(processors);
	}

	@Override
	public boolean canProcess(Path path) {
		for (FileProcessor processor : processors) {
			if (processor.canProcess(path)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Optional<PDDocument> process(Path path) {
		for (FileProcessor processor : processors) {
			if (processor.canProcess(path)) {
				return processor.process(path);
			}
		}
		return Optional.empty();
	}

}
