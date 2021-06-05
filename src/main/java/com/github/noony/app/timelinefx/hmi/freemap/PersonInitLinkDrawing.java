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

import com.github.noony.app.timelinefx.core.freemap.PersonInitLink;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author arnaud
 */
public class PersonInitLinkDrawing implements IFxScalableNode {

    //todo variable
    public static final double TANGEANT_LENGHT = 25;

    //TODO implement request replace in parent when shape is changed
    private final PersonInitLink link;
    //
    private final RectanglePlot plotDrawing;
    private final PortraitDrawing portraitDrawing;
    //
    private final CubicCurve cubicCurve;
    //
    private double scale = 1.0;

    public PersonInitLinkDrawing(PersonInitLink personInitLink, FriezeFreeFormDrawing freeFormDrawing) {
        link = personInitLink;
        cubicCurve = new CubicCurve();
        cubicCurve.setStroke(link.getPerson().getColor());
        plotDrawing = freeFormDrawing.getPlotDrawing(link.getFirstPlot());
        plotDrawing.getPlot().addPropertyChangeListener(PersonInitLinkDrawing.this::handleChange);
        portraitDrawing = freeFormDrawing.getPortrait(link.getPerson());
        portraitDrawing.getPortrait().addListener(PersonInitLinkDrawing.this::handleChange);
        cubicCurve.setStrokeType(StrokeType.CENTERED);
        cubicCurve.setStrokeWidth(3);
        cubicCurve.setFill(null);
        updatePosition();
    }

    @Override
    public Node getNode() {
        return cubicCurve;
    }

    private void handleChange(PropertyChangeEvent event) {
        // TODO
        updatePosition();
    }

    @Override
    public void updateScale(double newScale) {
        scale = newScale;
        updatePosition();
    }

    public final void updatePosition() {
        Point2D originInScene = portraitDrawing.getScenePosition();
        Point2D targetInScene = plotDrawing.getScenePosition();
        Point2D origin = cubicCurve.sceneToLocal(originInScene);
        Point2D target = cubicCurve.sceneToLocal(targetInScene);
        cubicCurve.setStartX(origin.getX());
        cubicCurve.setStartY(origin.getY());
        cubicCurve.setControlX1(origin.getX() + 1.5 * portraitDrawing.getPortrait().getRadius() * scale);
        cubicCurve.setControlY1(origin.getY());
        cubicCurve.setControlX2(target.getX() - TANGEANT_LENGHT * scale);
        cubicCurve.setControlY2(target.getY());
        cubicCurve.setEndX(target.getX());
        cubicCurve.setEndY(target.getY());
    }
}
