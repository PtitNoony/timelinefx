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
package com.github.noony.app.timelinefx.hmi.byperson;

import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.StayPeriod;
import java.beans.PropertyChangeEvent;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class PlaceStayDrawing {

    public static final double STROKE_WIDTH = 6;
    public static final double SELECTED_STROKE_WIDTH = 12;

    public static final double PLACE_HEIGHT = PersonDrawing.DEFAULT_HEIGHT - 2.0 * PersonDrawing.DEFAULT_INNER_SEPARATION;

    private final StayPeriod stayPeriod;

    private final Rectangle rectangle;
    private final double yPos = PersonDrawing.DEFAULT_INNER_SEPARATION;

    public PlaceStayDrawing(StayPeriod stay) {
        stayPeriod = stay;
        //
        rectangle = new Rectangle();
        rectangle.setFill(stayPeriod.getPlace().getColor());
        rectangle.setHeight(PLACE_HEIGHT);
        rectangle.setY(yPos);
        //
        stayPeriod.getPlace().addPropertyChangeListener(PlaceStayDrawing.this::handlePlaceChanged);
        //
        rectangle.setOnMouseEntered(event -> {
            stayPeriod.getPlace().setSelected(true);
        });
        rectangle.setOnMouseExited(event -> {
            stayPeriod.getPlace().setSelected(false);
        });
    }

    public Node getNode() {
        return rectangle;
    }

    public StayPeriod getStayPeriod() {
        return stayPeriod;
    }

    protected void updateDateRatio(long minDate, double ratio) {
        double startX = (stayPeriod.getStartDate() - minDate) * ratio;
        double endX = (stayPeriod.getEndDate() - minDate) * ratio;
        endX = Math.max(startX + 1, endX);
        rectangle.setX(startX);
        rectangle.setWidth(endX - startX);
    }

    private void handlePlaceChanged(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Place.CONTENT_CHANGED ->
                rectangle.setFill(stayPeriod.getPlace().getColor());
            case Place.SELECTION_CHANGED -> {
                if (stayPeriod.getPlace().isSelected()) {
                    rectangle.setStrokeWidth(SELECTED_STROKE_WIDTH);
                } else {
                    rectangle.setStrokeWidth(STROKE_WIDTH);
                }
            }
            default ->
                throw new AssertionError();
        }
    }
}
