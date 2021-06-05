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
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.scene.Group;
import javafx.scene.Node;
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

    private static final double DEFAULT_RADIUS = 5;
    private static final double DEFAULT_STROKE = 4;

    private static final double DEFAULT_CURVE_RADIUS_PERCENTAGE = 0.75;

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
    //
    private double viewingScale = 1.0;

    public ChronologyLinkDrawing(ChronologyLink aChronologyLink) {
        chronologyLink = aChronologyLink;
        mainNode = new Group();
        startNode = new Circle(DEFAULT_RADIUS, chronologyLink.getPerson().getColor());
        endNode = new Circle(DEFAULT_RADIUS, chronologyLink.getPerson().getColor());
        //
        createLinkShape();
        update();
        //
        chronologyLink.addListener(ChronologyLinkDrawing.this::handleLinkChanges);
        //
        mainNode.getChildren().addAll(startNode, endNode, linkShape);
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public void updateScale(double newScale) {
        viewingScale = newScale;
        update();
    }

    private void createLinkShape() {
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
        }
        linkShape.setFill(null);
        linkShape.setStroke(chronologyLink.getPerson().getColor());
        linkShape.setStrokeWidth(DEFAULT_STROKE);

    }

    private void update() {
        var sX = chronologyLink.getStartPosition().getX() * viewingScale;
        var sY = chronologyLink.getStartPosition().getY() * viewingScale;
        var eX = chronologyLink.getEndPosition().getX() * viewingScale;
        var eY = chronologyLink.getEndPosition().getY() * viewingScale;
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
                var rX = (eX - sX) * DEFAULT_CURVE_RADIUS_PERCENTAGE * viewingScale;
                cubicCurve.setControlX1(sX + rX);
                cubicCurve.setControlY1(sY);
                cubicCurve.setControlX2(eX - rX);
                cubicCurve.setControlY2(eY);
                cubicCurve.setEndX(eX);
                cubicCurve.setEndY(eY);
            }
        }
        startNode.setCenterX(sX);
        startNode.setCenterY(sY);
        endNode.setCenterX(eX);
        endNode.setCenterY(eY);
        linkShape.setStrokeWidth(DEFAULT_STROKE * viewingScale);
    }

    private void handleLinkChanges(PropertyChangeEvent event) {
        update();
    }
}
