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

import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;

import java.beans.PropertyChangeEvent;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author hamon
 */
public class RectanglePlot extends AbstractFxScalableNode {

    private final Plot plot;
    private final Rectangle plotRectangle;
    //
    private Point2D oldScene;
    private Point2D currentScene;
    private double oldMainNodeTranslateX;
    private double oldMainNodeTranslateY;

    public RectanglePlot(Plot aPlot) {
        plot = aPlot;
        plot.addPropertyChangeListener(RectanglePlot.this::handlePropertyChange);
        plotRectangle = new Rectangle();
        plotRectangle.setFill(Color.BLACK);
        addNode(plotRectangle);
        initInteractivity();
        updateLayout();
    }

    public Plot getPlot() {
        return plot;
    }

    public Point2D getScenePosition() {
        return getNode().localToScene(0, 0);
    }

    private void initInteractivity() {
        double gridSpace = 1; //TODO use a grid
        plotRectangle.setOnMousePressed(event -> {
            oldScene = new Point2D(event.getScreenX(), event.getScreenY());
            oldMainNodeTranslateX = getNode().getTranslateX();
            oldMainNodeTranslateY = getNode().getTranslateY();
        });
        plotRectangle.setOnMouseDragged(event -> {
            currentScene = new Point2D(event.getScreenX(), event.getScreenY());
            setNodeTranslateX(oldMainNodeTranslateX + currentScene.getX() - oldScene.getX());
            setNodeTranslateY(oldMainNodeTranslateY + currentScene.getY() - oldScene.getY());
        });
        plotRectangle.setOnMouseReleased(event -> {
            currentScene = new Point2D(event.getScreenX(), event.getScreenY());
            var deltaXScaled = currentScene.getX() - oldScene.getX();
            var deltaYScaled = currentScene.getY() - oldScene.getY();
            var deltaX = deltaXScaled / getScale();
            var deltaY = deltaYScaled / getScale();
            var newX = plot.getX() + (deltaX / gridSpace) * gridSpace;
            var newY = plot.getY() + (deltaY % gridSpace) * gridSpace;
            plot.setPosition(newX, newY);
        });
        plotRectangle.setOnMouseClicked(event -> plot.setSelected(!plot.isSelected()));
    }

    private void handlePropertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED -> {
                updateX();
                updateY();
            }
            case Plot.SELECTION_CHANGED -> {
                // nothing to do
            }
            case Plot.PLOT_SIZE_CHANGED -> updateLayout();
            case Plot.PLOT_VISIBILITY_CHANGED -> setVisible((boolean) event.getNewValue());
            case Plot.PLOT_DATE_CHANGED -> {
                // nothing to do since X position shall be updated when plot added in the new dateHandle
            }
            default -> throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    @Override
    protected final void updateLayout() {
        updateX();
        updateY();
        var rectangleHalfWidthScaled = plot.getPlotSize() * getScale();
        var size = rectangleHalfWidthScaled * 2;
        plotRectangle.setWidth(size);
        plotRectangle.setHeight(size);
        plotRectangle.setX(-rectangleHalfWidthScaled);
        plotRectangle.setY(-rectangleHalfWidthScaled);
    }

    private void updateX() {
        setNodeTranslateX(plot.getX() * getScale());
    }

    private void updateY() {
        setNodeTranslateY(plot.getY() * getScale());
    }
}
