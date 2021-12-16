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

import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Shape;

/**
 *
 * @author hamon
 */
public class ChronologyLinkDrawing implements IFxScalableNode {

    // TODO : think about classes
    public static final String LINK_CLICKED = "linkClicked";

    private static final Logger LOG = Logger.getGlobal();

    private static final double DEFAULT_RADIUS = 12;
    private static final double DEFAULT_STROKE = 4;
    private static final Color DEFAULT_CONTROLS_COLOR = Color.MAGENTA;

    private final PropertyChangeSupport propertyChangeSupport;
    private final ChronologyLink chronologyLink;
    //
    private final Group mainNode;
    private final Circle startNode;
    private final Circle endNode;
    private Line line = null;
    private QuadCurve quadCurve = null;
    private CubicCurve cubicCurve = null;
    //
    private Shape linkShape = null;
    // in the future, do not create all the controls at once
    private Circle cubicControlPoint1 = null;
    private Line cubicControlLine1 = null;
    private Line cubicControlLine2 = null;
    private Circle cubicControlPoint2 = null;
    //
    private double viewingScale = 1.0;
    private double[] linkParameters = null;

    public ChronologyLinkDrawing(ChronologyLink aChronologyLink) {
        propertyChangeSupport = new PropertyChangeSupport(ChronologyLinkDrawing.this);
        chronologyLink = aChronologyLink;
        linkParameters = chronologyLink.getLinkParameters();
        //
        mainNode = new Group();
        startNode = new Circle(DEFAULT_RADIUS, DEFAULT_CONTROLS_COLOR);
        endNode = new Circle(DEFAULT_RADIUS, DEFAULT_CONTROLS_COLOR);
        // handling start / end movements
        startNode.setOnMouseDragged(e -> {
            var newPosition = getAnchorPointPosition(chronologyLink.getStartMiniature(), e.getX(), e.getY());
            var newAngle = MathUtils.getAngle(chronologyLink.getStartMiniature().getPosition(), newPosition);
            var newDistance = chronologyLink.getStartMiniature().getPosition().distance(newPosition) / viewingScale;
            startNode.setCenterX(newPosition.getX());
            startNode.setCenterY(newPosition.getY());
            linkParameters[0] = newAngle;
            linkParameters[1] = newDistance;
            updateLocally();
        });
        startNode.setOnMouseReleased(e -> {
            var newPosition = getAnchorPointPosition(chronologyLink.getStartMiniature(), e.getX(), e.getY());
            var newAngle = MathUtils.getAngle(chronologyLink.getStartMiniature().getPosition(), newPosition);
            var newDistance = chronologyLink.getStartMiniature().getPosition().distance(newPosition) / viewingScale;
            startNode.setCenterX(newPosition.getX());
            startNode.setCenterY(newPosition.getY());
            linkParameters[0] = newAngle;
            linkParameters[1] = newDistance;
            chronologyLink.updateLinkParameters(linkParameters);
        });
        endNode.setOnMouseDragged(e -> {
            var newPosition = getAnchorPointPosition(chronologyLink.getEndMiniature(), e.getX(), e.getY());
            var newAngle = MathUtils.getAngle(chronologyLink.getEndMiniature().getPosition(), newPosition);
            var newDistance = chronologyLink.getEndMiniature().getPosition().distance(newPosition) / viewingScale;
            endNode.setCenterX(newPosition.getX());
            endNode.setCenterY(newPosition.getY());
            linkParameters[6] = newAngle;
            linkParameters[7] = newDistance;
            updateLocally();
        });
        endNode.setOnMouseReleased(e -> {
            var newPosition = getAnchorPointPosition(chronologyLink.getEndMiniature(), e.getX(), e.getY());
            var newAngle = MathUtils.getAngle(chronologyLink.getEndMiniature().getPosition(), newPosition);
            var newDistance = chronologyLink.getEndMiniature().getPosition().distance(newPosition) / viewingScale;
            endNode.setCenterX(newPosition.getX());
            endNode.setCenterY(newPosition.getY());
            linkParameters[6] = newAngle;
            linkParameters[7] = newDistance;
            chronologyLink.updateLinkParameters(linkParameters);
        });
        // creating controls for cubic shape
        cubicControlLine1 = new Line();
        cubicControlLine1.setStroke(Color.WHITESMOKE);
        cubicControlLine2 = new Line();
        cubicControlLine2.setStroke(Color.WHITESMOKE);
        cubicControlPoint1 = new Circle(DEFAULT_RADIUS, DEFAULT_CONTROLS_COLOR);
        cubicControlPoint1.setOnMouseDragged(e -> {
            cubicCurve.setControlX1(e.getX());
            cubicCurve.setControlY1(e.getY());
            cubicControlPoint1.setCenterX(e.getX());
            cubicControlPoint1.setCenterY(e.getY());
            cubicControlLine1.setEndX(e.getX());
            cubicControlLine1.setEndY(e.getY());
        });
        cubicControlPoint1.setOnMouseReleased(e -> {
            var controlPoint1 = new Point2D(e.getX(), e.getY());
            var startPoint = new Point2D(startNode.getCenterX(), startNode.getCenterY());
            //
            var angle = MathUtils.getAngle(startPoint, controlPoint1);
            var r = controlPoint1.distance(startPoint) / viewingScale;
            linkParameters[2] = angle;
            linkParameters[3] = r;
            chronologyLink.updateLinkParameters(linkParameters);
        });
        cubicControlPoint2 = new Circle(DEFAULT_RADIUS, DEFAULT_CONTROLS_COLOR);
        cubicControlPoint2.setOnMouseDragged(e -> {
            cubicCurve.setControlX2(e.getX());
            cubicCurve.setControlY2(e.getY());
            cubicControlLine2.setEndX(e.getX());
            cubicControlLine2.setEndY(e.getY());
            cubicControlPoint2.setCenterX(e.getX());
            cubicControlPoint2.setCenterY(e.getY());
        });
        cubicControlPoint2.setOnMouseReleased(e -> {
            var controlPoint2 = new Point2D(e.getX(), e.getY());
            var endPoint = new Point2D(endNode.getCenterX(), endNode.getCenterY());
            var angle = MathUtils.getAngle(endPoint, controlPoint2);
            var r = controlPoint2.distance(endPoint) / viewingScale;
            linkParameters[4] = angle;
            linkParameters[5] = r;
            chronologyLink.updateLinkParameters(linkParameters);
        });
        //
        createLinkShape();
        updateFromModel();
        //
        chronologyLink.addListener(ChronologyLinkDrawing.this::handleLinkChanges);
        //
        mainNode.getChildren().addAll(linkShape, cubicControlLine1, cubicControlLine2, startNode, cubicControlPoint1, cubicControlPoint2, endNode);
        // IMPR: In the future toggle visibility to override position
        startNode.setVisible(false);
        cubicControlPoint1.setVisible(false);
        cubicControlPoint2.setVisible(false);
        cubicControlLine1.setVisible(false);
        cubicControlLine2.setVisible(false);
        endNode.setVisible(false);
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public double getScale() {
        return viewingScale;
    }

    @Override
    public void updateScale(double newScale) {
        viewingScale = newScale;
        updateFromModel();
    }

    protected void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void displayControls(boolean visible) {
        // in the future handle the cases where type has changed
        startNode.setVisible(visible);
        cubicControlPoint1.setVisible(visible);
        cubicControlPoint2.setVisible(visible);
        cubicControlLine1.setVisible(visible);
        cubicControlLine2.setVisible(visible);
        endNode.setVisible(visible);
        System.err.println(" handle missing parts");
    }

    private void createLinkShape() {
        // in the future, handle remove listeners, when changing type
        switch (chronologyLink.getLinkType()) {
            case LINE -> {
                line = new Line();
                linkShape = line;
            }
            case QUAD -> {
            }
            case CUBIC -> {
                cubicCurve = new CubicCurve();
                linkShape = cubicCurve;
            }
            default ->
                throw new UnsupportedOperationException();
        }
        linkShape.setFill(null);
        linkShape.setStroke(chronologyLink.getPerson().getColor());
        linkShape.setStrokeWidth(DEFAULT_STROKE);
        linkShape.setOnMouseClicked(this::handleShapeClicked);

    }

    private void updateFromModel() {
        linkParameters = chronologyLink.getLinkParameters();
        update(linkParameters);
    }

    private void updateLocally() {
        // not fetching the model value but using the locally modified one
        update(linkParameters);
    }

    private void update(double[] parametersForUpdate) {
        var scaledStrokeWidth = DEFAULT_STROKE * viewingScale;
        var sX = (chronologyLink.getStartMiniature().getPosition().getX() + parametersForUpdate[1] * Math.cos(parametersForUpdate[0])) * viewingScale + scaledStrokeWidth;
        var sY = (chronologyLink.getStartMiniature().getPosition().getY() + parametersForUpdate[1] * Math.sin(parametersForUpdate[0])) * viewingScale;
        var eX = (chronologyLink.getEndMiniature().getPosition().getX() + parametersForUpdate[7] * Math.cos(parametersForUpdate[6])) * viewingScale - scaledStrokeWidth;
        var eY = (chronologyLink.getEndMiniature().getPosition().getY() + parametersForUpdate[7] * Math.sin(parametersForUpdate[6])) * viewingScale;
        switch (chronologyLink.getLinkType()) {
            case LINE -> {
                line.setStartX(sX);
                line.setStartY(sY);
                line.setEndX(eX);
                line.setEndY(eY);
            }
            case QUAD -> {
            }
            case CUBIC -> {
                cubicCurve.setStartX(sX);
                cubicCurve.setStartY(sY);
                var startAngle = parametersForUpdate[2];
                var startLenght = parametersForUpdate[3] * viewingScale;
                var endAngle = parametersForUpdate[4];
                var endLenght = parametersForUpdate[5] * viewingScale;
                //
                var controlX1 = sX + startLenght * Math.cos(startAngle);
                var controlY1 = sY + startLenght * Math.sin(startAngle);
                var controlX2 = eX + endLenght * Math.cos(endAngle);
                var controlY2 = eY + endLenght * Math.sin(endAngle);
                cubicCurve.setControlX1(controlX1);
                cubicCurve.setControlY1(controlY1);
                cubicCurve.setControlX2(controlX2);
                cubicCurve.setControlY2(controlY2);
                cubicControlPoint1.setCenterX(controlX1);
                cubicControlPoint1.setCenterY(controlY1);
                cubicControlPoint2.setCenterX(controlX2);
                cubicControlPoint2.setCenterY(controlY2);
                cubicCurve.setEndX(eX);
                cubicCurve.setEndY(eY);
                cubicControlLine1.setStartX(sX);
                cubicControlLine1.setStartY(sY);
                cubicControlLine1.setEndX(controlX1);
                cubicControlLine1.setEndY(controlY1);
                cubicControlLine2.setStartX(eX);
                cubicControlLine2.setStartY(eY);
                cubicControlLine2.setEndX(controlX2);
                cubicControlLine2.setEndY(controlY2);
            }
        }
        startNode.setCenterX(sX);
        startNode.setCenterY(sY);
        endNode.setCenterX(eX);
        endNode.setCenterY(eY);
        linkShape.setStrokeWidth(scaledStrokeWidth);
    }

    private void handleLinkChanges(PropertyChangeEvent event) {
        LOG.log(Level.FINE, "Handling link change for {0} :: {1}", new Object[]{this, event});
        updateFromModel();
    }

    private void handleShapeClicked(MouseEvent event) {
        propertyChangeSupport.firePropertyChange(LINK_CLICKED, event, this);
    }

    private Point2D getAnchorPointPosition(ChronologyPictureMiniature aMiniature, double xPos, double yPos) {
        var imageCenterX = aMiniature.getPosition().getX() * viewingScale;
        var imageCenterY = aMiniature.getPosition().getY() * viewingScale;
        var deltaPersonIndex = ChronologyLink.calculateDeltaXPosition(aMiniature, chronologyLink.getPerson()) * viewingScale;
        var imageHalfWidth = aMiniature.getWidth() / 2.0 + deltaPersonIndex * viewingScale;
        var imageHalfHeight = aMiniature.getHeight() / 2.0 + deltaPersonIndex * viewingScale;
        var minX = imageCenterX - imageHalfWidth;
        var maxX = imageCenterX + imageHalfWidth;
        var minY = imageCenterY - imageHalfHeight;
        var maxY = imageCenterY + imageHalfHeight;
        //
        double newX;
        double newY;
        //
        if (xPos <= minX) {
            //left on the X axis
            newX = minX;
        } else if (xPos < maxX) {
            // inside on the X axis
            newX = xPos;
        } else {
            // right on the X axis
            newX = maxX;
        }
        if (yPos <= minY) {
            //left on the Y axis
            newY = minY;
        } else if (yPos < maxY) {
            // inside on the Y axis
            newY = yPos;
        } else {
            // right on the Y axis
            newY = maxY;
        }
        return new Point2D(newX, newY);
    }
}
