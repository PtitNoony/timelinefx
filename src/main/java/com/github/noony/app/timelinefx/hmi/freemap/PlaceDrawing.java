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

import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author hamon
 */
public final class PlaceDrawing extends AbstractFxScalableNode {

    private final FreeMapPlace place;
    private final FriezeFreeMap friezeFreeMap;
    //
    private final Rectangle debugBackground;
    private final Rectangle background;
    private final Rectangle backgroundClip;
    //
    private final Group placePlotsGroup;
    private final Group nameGroup;
    private final Label nameLabel;
    //
    private PlaceDrawingMode drawingMode;
    //
    private double minX;
    private double maxX;
    //
    private double oldScreenY;
    private double oldTranslateY;

    protected PlaceDrawing(FreeMapPlace aPlace, FriezeFreeMap aFriezeFreeMap) {
        super();
        place = aPlace;
        friezeFreeMap = aFriezeFreeMap;
        drawingMode = PlaceDrawingMode.MIN_MAX;
        minX = place.getMinX();
        maxX = place.getMaxX();
        //
        debugBackground = new Rectangle();
        debugBackground.setFill(Color.BLUEVIOLET);
        debugBackground.setWidth(100);
        debugBackground.setHeight(20);
        //
        background = new Rectangle();
        background.setFill(Color.GRAY);
        background.setStroke(Color.LIGHTGREY);
        background.setArcHeight(8);
        background.setArcWidth(8);
        //
        backgroundClip = new Rectangle();
        //
        placePlotsGroup = new Group();
        nameGroup = new Group();
        //
        nameLabel = new Label(place.getPlace().getName());
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setTextFill(Color.WHITESMOKE);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setMinHeight(FreeMapPlace.PLACE_NAME_HEIGHT);
        nameLabel.setMaxHeight(FreeMapPlace.PLACE_NAME_HEIGHT);
        nameLabel.setMinWidth(place.getPlaceNameWidth());
        nameLabel.setMaxWidth(place.getPlaceNameWidth());
        nameLabel.setFont(new Font(16));
        nameLabel.setWrapText(true);
//        nameLabel.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //
        place.addListener(PlaceDrawing.this::handlePropertyChange);
        //
        placePlotsGroup.getChildren().add(background);
//        addNode(debugBackground);
        addNode(placePlotsGroup);
        addNode(nameGroup);
        nameGroup.getChildren().add(nameLabel);
        background.setClip(backgroundClip);
        //
        initInteractivity();
        //
        updateLayout();
    }

    public void setMode(PlaceDrawingMode mode) {
        drawingMode = mode;
        updateWidth();
    }

    public FreeMapPlace getFreeMapPlace() {
        return place;
    }

    @Override
    protected void updateLayout() {
        updateY(place.getYPos());
        updateWidth();
        updateHeight();
    }

    private void initInteractivity() {
        background.setOnScroll(e -> {
            if (e.isControlDown()) {
                place.setHeight(Math.max(FreeMapPlace.PLACE_NAME_HEIGHT, background.getHeight() + Math.signum(e.getDeltaY()) * 5));
                e.consume();
            }
        });

        background.setOnMousePressed(event -> {
            oldScreenY = event.getScreenY();
            oldTranslateY = getNode().getTranslateY();
        });
        background.setOnMouseDragged(event -> {
            getNode().setTranslateY(oldTranslateY + event.getScreenY() - oldScreenY);
        });
        background.setOnMouseReleased(event -> {
            var translateYScaled = oldTranslateY + event.getScreenY() - oldScreenY;
            place.setY(translateYScaled / getScale());
        });
    }

    private void handlePropertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPlace.Y_POS_CHANGED ->
                updateY((double) event.getNewValue());
            case FreeMapPlace.WIDTH_POS_CHANGED ->
                updateWidth();
            case FreeMapPlace.HEIGHT_POS_CHANGED ->
                updateHeight();
            case FreeMapPlace.MIN_MAX_X_CHANGED -> {
                minX = (double) event.getOldValue();
                maxX = (double) event.getNewValue();
                updateWidth();
            }
            case FreeMapPlace.FONT_SIZE_CHANGED -> {
                var fontSize = (double) event.getNewValue();
                nameLabel.setFont(new Font(fontSize));
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    private void updateY(double value) {
        getNode().setTranslateY(value * getScale());
    }

    private void updateWidth() {
        var placePlotsWidth = place.getPlaceStayWidth() * getScale();
        switch (drawingMode) {
//            case FULL -> {
//                backgroundClip.setX(0);
//                backgroundClip.setWidth(placePlotsWidth);
//                background.setX(0);
//                background.setWidth(placePlotsWidth);
//            }
            case MIN_MAX -> {
                var x = minX;
                var w = (maxX - minX) * getScale();
                var xScaled = x * getScale();
                backgroundClip.setX(xScaled);
                backgroundClip.setWidth(w);
                background.setX(xScaled);
                background.setWidth(w);
            }
            default ->
                throw new UnsupportedOperationException();
        }
        nameLabel.setTranslateX(placePlotsWidth + friezeFreeMap.MAP_PADDING * getScale());
        nameLabel.setMinWidth(friezeFreeMap.getPlaceNamesWidth() * getScale());
        nameLabel.setMaxWidth(friezeFreeMap.getPlaceNamesWidth() * getScale());
    }

    private void updateHeight() {
        var height = place.getHeight() * getScale();
        background.setHeight(height);
        backgroundClip.setHeight(height);
        nameLabel.setMinHeight(height);
        nameLabel.setMaxHeight(height);
    }
}
