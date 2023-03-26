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

import com.github.noony.app.timelinefx.core.freemap.AbstractFreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

/**
 * @author hamon
 */
public class RectangleConnector extends AbstractFxScalableNode {

    private final AbstractFreeMapConnector connector;
    private final Rectangle connectorRectangle;
    //
    private Point2D oldScene;
    private Point2D currentScene;
    private double oldMainNodeTranslateX;
    private double oldMainNodeTranslateY;

    protected RectangleConnector(AbstractFreeMapConnector abstractConnector) {
        connector = abstractConnector;
        connector.addPropertyChangeListener(RectangleConnector.this::handlePropertyChange);
        connectorRectangle = new Rectangle();
        connectorRectangle.setFill(connector.getColor());
        addNode(connectorRectangle);
        initInteractivity();
        updateLayout();
    }

    protected AbstractFreeMapConnector getConnector() {
        return connector;
    }

    protected Point2D getScenePosition() {
        return getNode().localToScene(0, 0);
    }

    private void initInteractivity() {
        double gridSpace = 1; //TODO use a grid
        connectorRectangle.setOnMousePressed(event -> {
            oldScene = new Point2D(event.getScreenX(), event.getScreenY());
            oldMainNodeTranslateX = getNode().getTranslateX();
            oldMainNodeTranslateY = getNode().getTranslateY();
        });
        connectorRectangle.setOnMouseDragged(event -> {
            currentScene = new Point2D(event.getScreenX(), event.getScreenY());
            setNodeTranslateX(oldMainNodeTranslateX + currentScene.getX() - oldScene.getX());
            setNodeTranslateY(oldMainNodeTranslateY + currentScene.getY() - oldScene.getY());
        });
        connectorRectangle.setOnMouseReleased(event -> {
            currentScene = new Point2D(event.getScreenX(), event.getScreenY());
            var deltaXScaled = currentScene.getX() - oldScene.getX();
            var deltaYScaled = currentScene.getY() - oldScene.getY();
            var deltaX = deltaXScaled / getScale();
            var deltaY = deltaYScaled / getScale();
            var newX = connector.getX() + (deltaX / gridSpace) * gridSpace;
            var newY = connector.getY() + (deltaY % gridSpace) * gridSpace;
            connector.setPosition(newX, newY);
        });
        connectorRectangle.setOnMouseClicked(event -> connector.setSelected(!connector.isSelected()));
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
            case Plot.PLOT_SIZE_CHANGED ->
                updateLayout();
            case Plot.PLOT_VISIBILITY_CHANGED ->
                setVisible((boolean) event.getNewValue());
            case Plot.PLOT_DATE_CHANGED -> {
                // nothing to do since X position shall be updated when plot added in the new dateHandle
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    @Override
    protected final void updateLayout() {
        updateX();
        updateY();
        var rectangleHalfWidthScaled = connector.getPlotSize() * getScale();
        var size = rectangleHalfWidthScaled * 2;
        connectorRectangle.setWidth(size);
        connectorRectangle.setHeight(size);
        connectorRectangle.setX(-rectangleHalfWidthScaled);
        connectorRectangle.setY(-rectangleHalfWidthScaled);
    }

    private void updateX() {
        setNodeTranslateX(connector.getX() * getScale());
    }

    private void updateY() {
        setNodeTranslateY(connector.getY() * getScale());
    }

}
