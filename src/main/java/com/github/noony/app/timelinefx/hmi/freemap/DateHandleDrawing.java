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

import com.github.noony.app.timelinefx.core.freemap.DateHandle;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 *
 * @author hamon
 */
public class DateHandleDrawing extends AbstractFxScalableNode {

    public static final Color DEFAULT_COLOR = Color.CHARTREUSE;

    private static final double SIZE = 10;

    public enum ORIENTATION {
        UP, DOWN
    }

    private final DateHandle dateHandle;

    private final ORIENTATION orientation;
    private final Polygon handle;
    //
    private double lastXmouse;
    private double currentXmouse;

    public DateHandleDrawing(DateHandle aDateHandle, double aScale) {
        super();
        dateHandle = aDateHandle;
        dateHandle.addListener(DateHandleDrawing.this::handleUpdate);
        switch (dateHandle.getTimeType()) {
            case START ->
                orientation = ORIENTATION.DOWN;
            case END ->
                orientation = ORIENTATION.UP;
            default ->
                throw new IllegalMonitorStateException();
        }
        handle = new Polygon();
        //
        Double[] points = createPoints();
        handle.getPoints().setAll(points);
        handle.setFill(DEFAULT_COLOR);
        //
        initInteractivity();
        //
        addNode(handle);
        setNodeTranslateX(dateHandle.getXPos());
        setNodeTranslateY(dateHandle.getYPos());
        //
    }

    @Override
    protected void updateLayout() {
        Double[] points = createPoints();
        handle.getPoints().setAll(points);
        setNodeTranslateX(dateHandle.getXPos() * getScale());
    }

    public void setX(double xPos) {
        setNodeTranslateX(xPos * getScale());
    }

    public void setY(double yPos) {
        setNodeTranslateY(yPos);
        //TODO see if relevant (should be)
    }

    public void setColor(Color color) {
        handle.setFill(color);
    }

    private Double[] createPoints() {
        var size = SIZE * getScale();
        var halfSize = size / 2.0;
        Double[] points;
        points = switch (orientation) {
            case DOWN ->
                new Double[]{0.0, 0.0, -halfSize, -size, halfSize, -size};
            case UP ->
                new Double[]{0.0, 0.0, -halfSize, size, halfSize, size};
            default ->
                new Double[]{-halfSize, size, halfSize, size, halfSize, -size, -halfSize, -size};
        };
        return points;
    }

    private void initInteractivity() {
        handle.setOnMousePressed(e -> {
            lastXmouse = e.getSceneX();
        });
        handle.setOnMouseDragged(e -> {
            currentXmouse = e.getSceneX();
            dateHandle.setX(getNode().getTranslateX() + currentXmouse - lastXmouse);
            //
            lastXmouse = currentXmouse;
        });
    }

    private void handleUpdate(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case DateHandle.POSITION_CHANGED ->
                setX((double) event.getOldValue());
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }
}
