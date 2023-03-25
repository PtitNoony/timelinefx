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

import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.PersonInitLink;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeType;

/**
 * @author arnaud
 */
public class PersonInitLinkDrawing implements IFxScalableNode {

    //todo variable
    public static final double TANGENT_LENGTH = 25;

    //TODO implement request replace in parent when shape is changed
    private final PersonInitLink link;
    private final FriezeFreeFormDrawing friezeFreeFormDrawing;
    private final PersonDrawing personDrawing;
    //
    private final CubicCurve cubicCurve;
    //
    private FreeMapPortraitDrawing portraitDrawing;
    private Plot firstPlot;
    private RectanglePlot plotDrawing;
    private double scale = 1.0;

    @Deprecated
    public PersonInitLinkDrawing(PersonInitLink personInitLink, FriezeFreeFormDrawing freeFormDrawing, PersonDrawing aPersonDrawing) {
        link = personInitLink;
        link.addListener(PersonInitLinkDrawing.this::handlePersonLinkChange);
        friezeFreeFormDrawing = freeFormDrawing;
        personDrawing = aPersonDrawing;
        cubicCurve = new CubicCurve();
        cubicCurve.setStroke(link.getPerson().getColor());
//        updatePortraitConfiguration();
        cubicCurve.setStrokeType(StrokeType.CENTERED);
        cubicCurve.setStrokeWidth(3);
        cubicCurve.setFill(null);
        // no need to updatePosition(); since it is called in updateFirstPlotConfiguration
        updateFirstPlotConfiguration(link.getFirstPlot());
    }

    @Override
    public Node getNode() {
        return cubicCurve;
    }

    @Override
    public double getScale() {
        return scale;
    }


    private void handleChange(PropertyChangeEvent event) {
        // TODO
        updatePosition();
    }

    private void handlePersonLinkChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PersonInitLink.FIRST_PLOT_CHANGED -> updateFirstPlotConfiguration((Plot) event.getNewValue());
            case PersonInitLink.FIRST_PLOT_POSITION_CHANGED -> updatePosition();
            default -> throw new UnsupportedOperationException("handlePersonLinkChange:: " + event);
        }
    }

//    private void updatePortraitConfiguration() {
//        if (portraitDrawing != null) {
//            portraitDrawing.getPortrait().removeListener(this::handleChange);
//        }
//        portraitDrawing = friezeFreeFormDrawing.getPortrait(link.getPerson());
//        if (portraitDrawing == null) {
//            waitPortraitDrawingCreation();
//        } else {
//            portraitDrawing.getPortrait().addListener(PersonInitLinkDrawing.this::handleChange);
//        }
//        updatePosition();
//    }

    private void updateFirstPlotConfiguration(Plot newFirstPlot) {
        if (firstPlot != null) {
            firstPlot.removePropertyChangeListener(this::handleChange);
        }
        firstPlot = newFirstPlot;
        if (firstPlot != null) {
            plotDrawing = personDrawing.getPlotDrawing(firstPlot);
            if (plotDrawing == null) {
                // Depending on the property events, might not be created yet
                Platform.runLater(this::waitPlotDrawingCreation);
            }
            firstPlot.addPropertyChangeListener(this::handleChange);
        } else {
            plotDrawing = null;
        }
        updatePosition();
    }

//    private void waitPortraitDrawingCreation() {
//        portraitDrawing = friezeFreeFormDrawing.getPortrait(link.getPerson());
//        if (plotDrawing == null) {
//            Platform.runLater(this::waitPlotDrawingCreation);
//        } else {
//            portraitDrawing.getPortrait().addListener(PersonInitLinkDrawing.this::handleChange);
//            updatePosition();
//        }
//    }

    private void waitPlotDrawingCreation() {
        plotDrawing = personDrawing.getPlotDrawing(firstPlot);
        if (plotDrawing == null) {
            Platform.runLater(this::waitPlotDrawingCreation);
        } else {
            updatePosition();
        }
    }

    @Override
    public void updateScale(double newScale) {
        scale = newScale;
        updatePosition();
    }

    public final void updatePosition() {
        Point2D originInScene = Point2D.ZERO;
        if (portraitDrawing != null) {
            originInScene = portraitDrawing.getScenePosition();
        }
        Point2D origin = cubicCurve.sceneToLocal(originInScene);
        Point2D target;
        if (plotDrawing == null) {
            target = cubicCurve.sceneToLocal(originInScene);
        } else {
            Point2D targetInScene = plotDrawing.getScenePosition();
            target = cubicCurve.sceneToLocal(targetInScene);
        }
        var curveRadius = portraitDrawing != null ? portraitDrawing.getPortrait().getRadius() : FriezeFreeMap.DEFAULT_PORTRAIT_RADIUS;
        cubicCurve.setStartX(origin.getX());
        cubicCurve.setStartY(origin.getY());
        cubicCurve.setControlX1(origin.getX() + 1.5 * curveRadius * scale);
        cubicCurve.setControlY1(origin.getY());
        cubicCurve.setControlX2(target.getX() - TANGENT_LENGTH * scale);
        cubicCurve.setControlY2(target.getY());
        cubicCurve.setEndX(target.getX());
        cubicCurve.setEndY(target.getY());
    }
}
