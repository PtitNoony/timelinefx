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
package com.github.noony.app.timelinefx.hmi.freemap;

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author hamon
 */
public class FreeMapPortraitDrawing extends AbstractFxScalableNode {

    private static final Logger LOG = Logger.getGlobal();

    private final FreeMapPortrait freeMapPortrait;
    private final Circle circle;
    private final Circle smallerCircle;
    private final Circle imageClip;
    private final ImageView imageView;
    //
    private Image image;
    //
    private double xPos;
    private double yPos;
    //
    private double oldScreenX;
    private double oldScreenY;
    private double oldTranslateX;
    private double oldTranslateY;

    public FreeMapPortraitDrawing(FreeMapPortrait aPortrait) {
        super();
        freeMapPortrait = aPortrait;
        circle = new Circle();
        smallerCircle = new Circle();
        imageClip = new Circle();
        imageView = new ImageView();
        //
        updateImage();
        initLayout();
        initInteractivity();
        freeMapPortrait.addListener(FreeMapPortraitDrawing.this::handlePropertyChange);
        //
    }

    public Point2D getScenePosition() {
        return getNode().localToScene(0, 0);
    }

    public Point2D getScreenPosition() {
        return getNode().localToScreen(0, 0);
    }

    public FreeMapPortrait getPortrait() {
        return freeMapPortrait;
    }

    private void initLayout() {
        updateX(freeMapPortrait.getX());
        updateY(freeMapPortrait.getY());
        circle.setFill(null);
        circle.setStroke(freeMapPortrait.getPerson().getColor());
        //
        smallerCircle.setFill(null);
        smallerCircle.setStroke(Color.BLACK);
        imageView.setClip(imageClip);
        addNode(imageView);
        addNode(smallerCircle);
        addNode(circle);
        updateLayout();
    }

    private void initInteractivity() {
        imageView.setOnMousePressed(event -> {
            oldScreenX = event.getScreenX();
            oldScreenY = event.getScreenY();
            oldTranslateX = getNode().getTranslateX();
            oldTranslateY = getNode().getTranslateY();
        });
        imageView.setOnMouseDragged(event -> {
            getNode().setTranslateX(oldTranslateX + event.getScreenX() - oldScreenX);
            getNode().setTranslateY(oldTranslateY + event.getScreenY() - oldScreenY);
        });
        imageView.setOnMouseReleased(event -> {
            var translateXScaled = oldTranslateX + event.getScreenX() - oldScreenX;
            var translateYScaled = oldTranslateY + event.getScreenY() - oldScreenY;
            //
            freeMapPortrait.setX(translateXScaled / getScale());
            freeMapPortrait.setY(translateYScaled / getScale());
        });
    }

    private void handlePropertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPortrait.POSITION_CHANGED:
                updateX((double) event.getOldValue());
                updateY((double) event.getNewValue());
                break;
            case FreeMapPortrait.RADIUS_CHANGED:
                updateLayout();
                break;
            case Person.PICTURE_CHANGED:
                updateImage();
                break;
            case Person.DATE_OF_BIRTH_CHANGED, Person.DATE_OF_DEATH_CHANGED:
                // nothin to do
                break;
            default:
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    private void updateX(double newX) {
        xPos = newX;
        updateLayout();
    }

    private void updateY(double newY) {
        yPos = newY;
        updateLayout();
    }

    private void updateImage() {
        try {
            // the file path shall always be relative to the project Folder
            var portrait = freeMapPortrait.getPortrait();
            var project = portrait.getPerson().getProject();
            var filePathS = project.getProjectFolder().getAbsolutePath() + File.separatorChar + portrait.getProjectRelativePath();
            LOG.log(Level.FINE, "Trying to load file {0}", new Object[]{filePathS});
            FileInputStream inputstream = new FileInputStream(filePathS);
            image = new Image(inputstream);
            imageView.setImage(image);
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, " Ex :: {0}", new Object[]{ex});
            throw new IllegalStateException();
        }
    }

    @Override
    protected void updateLayout() {
        // TODO optim calcul
        setNodeTranslateX(xPos * getScale());
        setNodeTranslateY(yPos * getScale());
        //
        circle.setStrokeWidth(4 * getScale());
        circle.setRadius(freeMapPortrait.getRadius() * getScale());
        //
        smallerCircle.setStrokeWidth(6 * getScale());
        smallerCircle.setRadius((freeMapPortrait.getRadius() - 2) * getScale());
        //
        imageView.setX(-freeMapPortrait.getRadius() * getScale());
        imageView.setY(-freeMapPortrait.getRadius() * getScale());
        imageView.setFitWidth(freeMapPortrait.getRadius() * 2.0 * getScale());
        imageView.setFitHeight(freeMapPortrait.getRadius() * 2.0 * getScale());
        //
        imageClip.setRadius(freeMapPortrait.getRadius() * getScale());
    }
}
