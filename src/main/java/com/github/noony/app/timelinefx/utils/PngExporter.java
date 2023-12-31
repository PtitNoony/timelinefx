/*
 * Copyright (C) 2019 NoOnY
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.noony.app.timelinefx.utils;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.runLater;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author hamon
 */
public class PngExporter {

    private static final Logger LOG = Logger.getGlobal();

    public static final void exportToPNG(Node node, File file) {
        runLater(() -> {
            var snapShotparams = new SnapshotParameters();
            snapShotparams.setFill(Color.BLACK);
            WritableImage temp = node.snapshot(snapShotparams,
                    new WritableImage((int) node.getLayoutBounds().getWidth(),
                            (int) node.getLayoutBounds().getHeight()));
            ImageView tempImage = new ImageView(temp);
            tempImage.setCache(true);
            tempImage.setCacheHint(CacheHint.QUALITY);

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(temp, null), "png", file);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "EXCEPTION: {0}", new Object[]{e});
            }
        });
    }

    public static void saveImage(Image image, String outputPath) {
        var outputFile = new File(outputPath);
        var bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while saving .png: {0}", new Object[]{e});
        }
    }

    /**
     * https://stackoverflow.com/questions/10245220/java-image-resize-maintain-aspect-ratio
     *
     * @param imgSize
     * @param boundary
     * @return
     */
    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // first check if we need to scale width
        if (originalWidth > boundWidth) {
            //scale width to fit
            newWidth = boundWidth;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (newHeight > boundHeight) {
            //scale height to fit instead
            newHeight = boundHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return new Dimension(newWidth, newHeight);
    }

    public static Image resize(Image originalFxImage, Dimension dimension) {
        var bufferedImage = SwingFXUtils.fromFXImage(originalFxImage, null);
        java.awt.Image awtImage = resizeToBig(bufferedImage, dimension.width, dimension.height);
        return SwingFXUtils.toFXImage(toBufferedImage(awtImage), null);
    }

    /**
     * we want the x and o to be resized when the JFrame is resized
     * https://stackoverflow.com/questions/3967731/how-to-improve-the-performance-of-g-drawimage-method-for-resizing-images/11371387#11371387
     *
     * @param originalImage an x or an o. Use cross or oh fields.
     *
     * @param biggerWidth
     * @param biggerHeight
     * @return
     */
    public static java.awt.Image resizeToBig(java.awt.Image originalImage, int biggerWidth, int biggerHeight) {
        var type = BufferedImage.TYPE_INT_ARGB;

        var resizedImage = new BufferedImage(biggerWidth, biggerHeight, type);
        var g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, biggerWidth, biggerHeight, null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Converts a given Image into a BufferedImage https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage/13605485
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(java.awt.Image img) {
        if (img instanceof BufferedImage bufferedImage) {
            return bufferedImage;
        }
        if (img == null) {
            return null;
        }

        // Create a buffered image with transparency
        var bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        var bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private PngExporter() {
        // private utility constructor
    }

}
