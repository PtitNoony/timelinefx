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

import com.github.noony.app.timelinefx.core.freemap.Link;
import com.github.noony.app.timelinefx.core.freemap.LinkType;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.core.freemap.Selectable;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author hamon
 */
public class LinkDrawing implements Selectable, IFxScalableNode {

    private static final Color DEFAULT_COLOR = Color.BLUEVIOLET;

    private static final double CURVE_DELTA = 18.0;

    private final PropertyChangeSupport propertyChangeSupport;

    private final Link link;
    private final Plot beginPlot;
    private final Plot endPlot;
    //
    private double scale = 1.0;
    //
    private Line line;
    private CubicCurve cubicCurve;
    //
    private Color color;
    private boolean isSelected = false;
    //
    private double startX = 1.0;
    private double startY = 1.0;
    private double endX = 1.0;
    private double endY = 1.0;

    public LinkDrawing(Link aLink) {
        propertyChangeSupport = new PropertyChangeSupport(LinkDrawing.this);
        //
        link = aLink;
        //
        beginPlot = link.getBeginPlot();
        endPlot = link.getEndPlot();
        color = link.getColor();
        //
        line = new Line();
        cubicCurve = new CubicCurve();
        cubicCurve.setStrokeType(StrokeType.CENTERED);
        cubicCurve.setFill(null);
        //
        line.setOnMouseClicked(event -> {
            setSelected(!isSelected);
        });
        line.setStroke(color);
        cubicCurve.setStroke(color);
        switch (link.getType()) {
            case STAY:
                line.setStrokeWidth(4.0);
                cubicCurve.setStrokeWidth(4.0);
                break;
            case TRAVEL:
                line.setStrokeWidth(1.5);
                line.setStrokeDashOffset(45);
                cubicCurve.setStrokeWidth(1.5);
                cubicCurve.setStrokeDashOffset(45);
                break;
            default:
                throw new IllegalStateException();
        }
        startX = beginPlot.getX();
        startY = beginPlot.getY();
        endX = endPlot.getX();
        endY = endPlot.getY();
        updateLayout();
        //
        beginPlot.addPropertyChangeListener(LinkDrawing.this::handleStartPlotChanged);
        endPlot.addPropertyChangeListener(LinkDrawing.this::handleEndPlotChanged);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, this, this.isSelected);
        updateLayout();
    }

    public Plot getBeginPlot() {
        return beginPlot;
    }

    public Plot getEndPlot() {
        return endPlot;
    }

    public LinkType getType() {
        return link.getType();
    }

    public void setColor(Color aColor) {
        color = aColor;
        updateLayout();
    }

    @Override
    public Node getNode() {
        return cubicCurve;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public String getInfo() {
        return "Link for " + link.getInfo();
    }

    private void handleStartPlotChanged(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED:
                updateX1((double) event.getOldValue());
                updateY1((double) event.getNewValue());
                break;
            case Plot.SELECTION_CHANGED:
            case Plot.PLOT_SIZE_CHANGED:
            case Plot.PLOT_VISIBILITY_CHANGED:
                // nothing to do
                break;
            case Plot.PLOT_DATE_CHANGED:
                // nothing to do since X position shall be updated when plot added in the new dateHandle
                break;
            default:
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    private void handleEndPlotChanged(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED:
                updateX2((double) event.getOldValue());
                updateY2((double) event.getNewValue());
                break;
            case Plot.SELECTION_CHANGED:
            case Plot.PLOT_SIZE_CHANGED:
            case Plot.PLOT_VISIBILITY_CHANGED:
                // nothing to do
                break;
            case Plot.PLOT_DATE_CHANGED:
                // nothing to do since X position shall be updated when plot added in the new dateHandle
                break;
            default:
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    // ugly use fonctions
    private void updateX1(double newX) {
        startX = newX;
        updateLayout();
    }

    private void updateY1(double newY) {
        startY = newY;
        updateLayout();
    }

    private void updateX2(double newX) {
        endX = newX;
        updateLayout();
    }

    private void updateY2(double newY) {
        endY = newY;
        updateLayout();
    }

    @Override
    public void updateScale(double newScale) {
        scale = newScale;
        updateLayout();
    }

    private void updateLayout() {
        if (isSelected == true) {
            switch (link.getType()) {
                case STAY:
                    line.setStrokeWidth(6.0 * scale);
                    cubicCurve.setStrokeWidth(6.0 * scale);
                    break;
                case TRAVEL:
                    line.setStrokeWidth(2.5 * scale);
                    line.setStrokeDashOffset(45 * scale);
                    cubicCurve.setStrokeWidth(2.5 * scale);
                    cubicCurve.setStrokeDashOffset(45 * scale);
                    break;
                default:
                    throw new IllegalStateException();
            }
        } else {
            switch (link.getType()) {
                case STAY:
                    line.setStrokeWidth(4.0 * scale);
                    cubicCurve.setStrokeWidth(4.0 * scale);
                    break;
                case TRAVEL:
                    line.setStrokeWidth(1.5 * scale);
                    line.setStrokeDashOffset(45 * scale);
                    cubicCurve.setStrokeWidth(1.5 * scale);
                    cubicCurve.setStrokeDashOffset(45 * scale);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        //
        var scaledStartX = scale * startX;
        var scaledStartY = scale * startY;
        var scaledEndX = scale * endX;
        var scaledEndY = scale * endY;
        var curveScaled = CURVE_DELTA * scale;
        //
        line.setStartX(scaledStartX);
        cubicCurve.setStartX(scaledStartX);
        cubicCurve.setControlX1(scaledStartX + curveScaled);
        //
        line.setStartY(scaledStartY);
        cubicCurve.setStartY(scaledStartY);
        cubicCurve.setControlY1(scaledStartY);
        //
        line.setEndX(scaledEndX);
        cubicCurve.setEndX(scaledEndX);
        cubicCurve.setControlX2(scaledEndX - curveScaled);
        //
        line.setEndY(scaledEndY);
        cubicCurve.setEndY(scaledEndY);
        cubicCurve.setControlY2(scaledEndY);
    }

}
