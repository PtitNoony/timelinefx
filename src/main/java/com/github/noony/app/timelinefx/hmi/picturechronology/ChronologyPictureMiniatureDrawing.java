/*
 * Copyright (C) 2021 NoOnY
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
package com.github.noony.app.timelinefx.hmi.picturechronology;

import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class ChronologyPictureMiniatureDrawing implements IFxScalableNode {

    public static final double ARC_WIDTH = 32;
    //
    private static final Logger LOG = Logger.getGlobal();
    private static final double MIN_SCALE = 0.05;
    private static final double SCALE_STEP = 0.05;
    //
    private final ChronologyPictureMiniature chronologyPictureMiniature;
    private final PictureChronologyDrawing pictureChronologyDrawing;
    private final List<ContourDrawing> contours;
    //
    private final Group mainNode;
    private final Group contentNode;
    private final Group contoursNode;
    private final Rectangle clipRectangle;
    private final Rectangle frontGlass;
    private final Image image;
    private final ImageView imageView;
    //
    private double scale;
    private double viewingScale = 1.0;
    private double renderingScale;
    private double width;
    private double height;
    //
    private double oldScreenX;
    private double oldScreenY;
    private double oldTranslateX;
    private double oldTranslateY;

    public ChronologyPictureMiniatureDrawing(ChronologyPictureMiniature aChronologyPictureMiniature, PictureChronologyDrawing aPictureChronologyDrawing) {
        chronologyPictureMiniature = aChronologyPictureMiniature;
        pictureChronologyDrawing = aPictureChronologyDrawing;
        contours = new LinkedList<>();
        scale = chronologyPictureMiniature.getScale();
        //
        mainNode = new Group();
        contentNode = new Group();
        clipRectangle = new Rectangle();
        //
        contoursNode = new Group();
        var nbPersons = chronologyPictureMiniature.getPersons().size();
        for (int index = nbPersons - 1; index >= 0; index--) {
            var personContour = new ContourDrawing(aChronologyPictureMiniature, aChronologyPictureMiniature.getPersonAtIndex(index));
            contours.add(personContour);
            contoursNode.getChildren().add(personContour.getNode());
        }
        //
        String imagePath = chronologyPictureMiniature.getPicture().getAbsolutePath();
        InputStream imageStream;
        try {
            imageStream = new FileInputStream(imagePath);
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            // Ugly... but will do for now
            imageStream = null;
        }
        image = new Image(imageStream);
        imageView = new ImageView(image);
        imageView.setClip(clipRectangle);
        //
        frontGlass = new Rectangle();
        frontGlass.setFill(Color.PINK);
        frontGlass.setOpacity(0.0);
        // debugBackground,
        contentNode.getChildren().addAll(contoursNode, imageView, frontGlass);
        mainNode.getChildren().addAll(contentNode);
        //
        initInteractivity();
        //
        updateLayout();
        //
        chronologyPictureMiniature.addListener(ChronologyPictureMiniatureDrawing.this::handleChronologyPictureMiniatureChanges);
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public double getScale() {
        return scale;
    }

    protected void setPictureVisibility(boolean visibility) {
        imageView.setVisible(visibility);
    }

    private void initInteractivity() {
        frontGlass.setOnMousePressed(event -> {
            oldScreenX = event.getScreenX();
            oldScreenY = event.getScreenY();
            oldTranslateX = getNode().getTranslateX();
            oldTranslateY = getNode().getTranslateY();
        });
        frontGlass.setOnMouseDragged(event -> {
            getNode().setTranslateX(oldTranslateX + event.getScreenX() - oldScreenX);
            getNode().setTranslateY(oldTranslateY + event.getScreenY() - oldScreenY);
        });
        frontGlass.setOnMouseReleased(event -> {
            var translateXScaled = oldTranslateX + event.getScreenX() - oldScreenX;
            var translateYScaled = oldTranslateY + event.getScreenY() - oldScreenY;
            //
            mainNode.setTranslateX(translateXScaled);
            mainNode.setTranslateY(translateYScaled);
            chronologyPictureMiniature.setPosition(new Point2D(translateXScaled / viewingScale, translateYScaled / viewingScale));
        });
        frontGlass.setOnScroll(event -> {
            if (event.isControlDown()) {
                double newScale;
                if (event.getDeltaY() < 0) {
                    newScale = Math.max(MIN_SCALE, scale - SCALE_STEP);
                } else {
                    newScale = scale + SCALE_STEP;
                }
                chronologyPictureMiniature.setScale(newScale);
                event.consume();
            }
        });
        frontGlass.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2 && event.getButton().equals(MouseButton.SECONDARY)) {
                pictureChronologyDrawing.getPictureChronology().removeChronologyPicture(chronologyPictureMiniature);
            }
        });
    }

    @Override
    public void updateScale(double newScale) {
        viewingScale = newScale;
        updateLayout();
    }

    private void updateMiniatureScale(double newChronologyScale) {
        scale = newChronologyScale;
        updateLayout();
    }

    private void updateLayout() {
        renderingScale = viewingScale * scale;
        width = chronologyPictureMiniature.getPicture().getWidth() * renderingScale;
        height = chronologyPictureMiniature.getPicture().getHeight() * renderingScale;
        //
        contentNode.setTranslateX(-width / 2.0);
        contentNode.setTranslateY(-height / 2.0);
        clipRectangle.setWidth(width);
        clipRectangle.setHeight(height);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        frontGlass.setWidth(width);
        frontGlass.setHeight(height);
        //
        clipRectangle.setArcWidth(ARC_WIDTH * viewingScale);
        clipRectangle.setArcHeight(ARC_WIDTH * viewingScale);
        //
        mainNode.setTranslateX(chronologyPictureMiniature.getPosition().getX() * viewingScale);
        mainNode.setTranslateY(chronologyPictureMiniature.getPosition().getY() * viewingScale);
        //
        contours.forEach(contour -> contour.updateScale(viewingScale));
    }

    private void handleChronologyPictureMiniatureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED -> {
                Point2D newPosition = (Point2D) event.getNewValue();
                mainNode.setTranslateX(newPosition.getX() * viewingScale);
                mainNode.setTranslateY(newPosition.getY() * viewingScale);
            }
            case ChronologyPictureMiniature.SCALE_CHANGED ->
                updateMiniatureScale((double) event.getNewValue());
        }
    }
}
