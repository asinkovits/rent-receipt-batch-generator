package com.sinkovits.rent.generator.batch.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinkovits.rent.generator.batch.PDElementFactory;

public class ImageProcessor extends BaseFileProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessor.class);

	private PDElementFactory pdElementFactory;

	public ImageProcessor() {
		this(new PDElementFactory());
	}

	public ImageProcessor(PDElementFactory pdElementFactory) {
		super("jpg,jpeg,tif,tiff,png,bmp,gif");
		this.pdElementFactory = pdElementFactory;
	}

	public Optional<PDDocument> process(Path path) {
		try {
			PDDocument doc = pdElementFactory.createPDDocument();
			PDPage page = pdElementFactory.createPDPage();
			PDImageXObject pdImage = pdElementFactory.createPDImageXObject(path, doc);
			PDPageContentStream contentStream = pdElementFactory.createPDPageContentStream(doc, page);

			float scale = calculateScale(pdImage, page.getBBox());
			float scaledHeight = pdImage.getHeight() * scale;
			float heightOffset = page.getBBox().getHeight() - scaledHeight;
			contentStream.drawImage(pdImage, 0, heightOffset, pdImage.getWidth() * scale, scaledHeight);
			contentStream.close();
			doc.addPage(page);
			return Optional.of(doc);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			return Optional.empty();
		}
	}

	private float calculateScale(PDImageXObject pdImage, PDRectangle bBox) {
		if (bBox.getWidth() > pdImage.getWidth() && bBox.getHeight() > pdImage.getHeight())
			return 1;
		float xs = bBox.getWidth() / pdImage.getWidth();
		float ys = bBox.getHeight() / pdImage.getHeight();
		return Math.min(xs, ys);
	}

}
