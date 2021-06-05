/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.noony.app.timelinefx.utils;

import com.github.noony.app.timelinefx.core.PictureInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

/**
 *
 * @author hamon
 */
public class MetadataParser {

    private static final Logger LOG = Logger.getGlobal();

    private static final DateTimeFormatter DT_PARSER = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    private static final LocalDateTime DEFAULT_DATE = LocalDateTime.MIN;
    private static final int DEFAULT_RESOLUTION = -1;

    public static PictureInfo parseMetadata(File file) {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        String projectRelativePath = FileUtils.fromAbsoluteToProjectRelative(file);
        LocalDateTime creationDate = DEFAULT_DATE;
        int xRes = DEFAULT_RESOLUTION;
        int yRes = DEFAULT_RESOLUTION;

        try {
            // Using the Javafx API to get resolution
            var imageStream = new FileInputStream(filePath);
            var image = new Image(imageStream);
            xRes = (int) image.getWidth();
            yRes = (int) image.getHeight();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Exception while loading image :: {0}", new Object[]{ex});
        }

        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            if (metadata instanceof JpegImageMetadata jpegMetadata) {
                creationDate = getExifValueDate(jpegMetadata);
                final PictureInfo picInfo = new PictureInfo(fileName, projectRelativePath, creationDate, xRes, yRes);
                return picInfo;
            } else {
                LOG.log(Level.WARNING, "Could not parse file :: {0} \n > {1}",new Object[]{file,metadata});
                final PictureInfo picInfo = new PictureInfo(fileName, projectRelativePath, creationDate, xRes, yRes);
                return picInfo;
            }
        } catch (ImageReadException | IOException ex) {
            LOG.log(Level.SEVERE, "Exception while reading image :: {0}", new Object[]{ex});
        }
        return null;
    }

    private static LocalDateTime getExifValueDate(final JpegImageMetadata jpegMetadata) {
        LocalDateTime result = DEFAULT_DATE;
        try {
            final TiffField exifDate = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            if (exifDate != null) {
                result = LocalDateTime.parse(exifDate.getStringValue(), DT_PARSER);
            }
            final TiffField tiffDate = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_DATE_TIME);
            if (tiffDate != null) {
                result = LocalDateTime.parse(tiffDate.getStringValue(), DT_PARSER);
            }
        } catch (final ImageReadException | DateTimeParseException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return result;
    }

}
