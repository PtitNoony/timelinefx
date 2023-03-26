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
package com.github.noony.app.timelinefx.core.freemap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author hamon
 */
public class FreeMapLinkConnector extends AbstractFreeMapConnector {

    public static final String PLOT_DATE_CHANGED = "plotDateChanged";

    private final PropertyChangeSupport propertyChangeSupport;

    private final FreeMapLink sourceLink;

    private double date;
    private final DoubleProperty xPos;
    private final DoubleProperty yPos;

    private boolean isVisible = true;
    private boolean isSelected = false;
    private double plotSize;

    protected FreeMapLinkConnector(FreeMapLink aSourceLink, double aDate, double aPlotSize) {
        super();
        propertyChangeSupport = new PropertyChangeSupport(FreeMapLinkConnector.this);
        sourceLink = aSourceLink;
        //
        xPos = new SimpleDoubleProperty();
        yPos = new SimpleDoubleProperty();
        //
        date = aDate;
        plotSize = aPlotSize;
        xPos.setValue(sourceLink.getBeginPlot().getX() + (sourceLink.getEndPlot().getX() - sourceLink.getBeginPlot().getX()) / 2.0);
        // Assumption: the link is horizontal
        yPos.setValue(sourceLink.getBeginPlot().getY());
    }

    @Override
    public long getLinkedElementID() {
        return sourceLink.getId();
    }

    @Override
    public double getPlotSize() {
        return plotSize;
    }

    @Override
    public double getX() {
        return xPos.doubleValue();
    }

    @Override
    public double getY() {
        return yPos.doubleValue();
    }

    @Override
    public void setVisible(boolean visibility) {
        isVisible = visibility;
        propertyChangeSupport.firePropertyChange(PLOT_VISIBILITY_CHANGED, this, isVisible);
    }

    @Override
    public void setPosition(double newX, double newY) {
        setX(newX);
        setY(newY);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
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
    }

    @Override
    public String getInfo() {
        return "Connector [t=" + date + "  x=" + xPos.doubleValue() + ", y=" + yPos.doubleValue() + "]";
    }

    public void setX(double newX) {
        var boundedNewX = Math.max(sourceLink.getBeginPlot().getX(), newX);
        boundedNewX = Math.min(sourceLink.getEndPlot().getX(), boundedNewX);
        if (Math.abs(boundedNewX - xPos.doubleValue()) > EPSILON) {
            xPos.setValue(boundedNewX);
            propertyChangeSupport.firePropertyChange(POS_CHANGED, xPos.doubleValue(), yPos.doubleValue());
        }
    }

    public void setY(double newY) {
        // hypothesis: the link is horizontal
        // overriding newY
        newY = sourceLink.getBeginPlot().getY();
        if (Math.abs(yPos.doubleValue() - newY) > EPSILON) {
            yPos.setValue(sourceLink.getBeginPlot().getY());
            propertyChangeSupport.firePropertyChange(POS_CHANGED, xPos.doubleValue(), yPos.doubleValue());
        }
    }

    public void setPlotSize(double newPlotSize) {
        plotSize = newPlotSize;
        propertyChangeSupport.firePropertyChange(PLOT_SIZE_CHANGED, this, plotSize);
    }

    @Override
    public double getDate() {
        return date;
    }

    public DoubleProperty getXProperty() {
        return xPos;
    }

    public DoubleProperty getYProperty() {
        return yPos;
    }

    public void setDate(double aDate) {
        if (aDate != date) {
            date = aDate;
            propertyChangeSupport.firePropertyChange(PLOT_DATE_CHANGED, this, date);
        }
    }

    @Override
    public String toString() {
        return "[Plot t=" + date + "]";
    }

}
