package com.sinkovits.rent.generator.batch.processor;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public abstract class BaseFileProcessor implements FileProcessor {

	private PathMatcher matcher;

	public BaseFileProcessor(String filetypes) {
		super();
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:*.{" + filetypes + "}");
	}

	@Override
	public boolean canProcess(Path path) {
		return matcher.matches(path.getFileName());
	}

}
