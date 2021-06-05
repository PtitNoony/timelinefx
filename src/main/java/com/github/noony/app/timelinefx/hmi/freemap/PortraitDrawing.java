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
import com.github.noony.app.timelinefx.core.ProjectConfiguration;
import com.github.noony.app.timelinefx.core.freemap.Portrait;
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
public class PortraitDrawing extends AbstractFxScalableNode {

    private static final Logger LOG = Logger.getGlobal();

    private final Portrait portrait;
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

    public PortraitDrawing(Portrait aPortrait) {
        super();
        portrait = aPortrait;
        circle = new Circle();
        smallerCircle = new Circle();
        imageClip = new Circle();
        imageView = new ImageView();
        //
        updateImage();
        initLayout();
        initInteractivity();
        portrait.addListener(PortraitDrawing.this::handlePropertyChange);
        //
    }

    public Point2D getScenePosition() {
        return getNode().localToScene(0, 0);
    }

    public Point2D getScreenPosition() {
        return getNode().localToScreen(0, 0);
    }

    public Portrait getPortrait() {
        return portrait;
    }

    private void initLayout() {
        updateX(portrait.getX());
        updateY(portrait.getY());
        circle.setFill(null);
        circle.setStroke(portrait.getPerson().getColor());
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
            portrait.setX(translateXScaled / getScale());
            portrait.setY(translateYScaled / getScale());
        });
    }

    private void handlePropertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Portrait.POSITION_CHANGED:
                updateX((double) event.getOldValue());
                updateY((double) event.getNewValue());
                break;
            case Portrait.RADIUS_CHANGED:
                updateLayout();
                break;
            case Person.PICTURE_CHANGED:
                updateImage();
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
            LOG.log(Level.INFO, "Trying to load file {0}{1}{2}", new Object[]{ProjectConfiguration.getProjectLocation(), File.separatorChar, portrait.getPerson().getPictureName()});
            FileInputStream inputstream = new FileInputStream(ProjectConfiguration.getProjectLocation() + File.separatorChar + portrait.getPerson().getPictureName());
            image = new Image(inputstream);
            imageView.setImage(image);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PortraitDrawing.class.getName()).log(Level.SEVERE, " Ex :: {0}", ex);
            ex.printStackTrace();
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
        circle.setRadius(portrait.getRadius() * getScale());
        //
        smallerCircle.setStrokeWidth(6 * getScale());
        smallerCircle.setRadius((portrait.getRadius() - 2) * getScale());
        //
        imageView.setX(-portrait.getRadius() * getScale());
        imageView.setY(-portrait.getRadius() * getScale());
        imageView.setFitWidth(portrait.getRadius() * 2.0 * getScale());
        imageView.setFitHeight(portrait.getRadius() * 2.0 * getScale());
        //
        imageClip.setRadius(portrait.getRadius() * getScale());
    }
}
