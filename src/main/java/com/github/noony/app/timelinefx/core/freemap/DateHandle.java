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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class DateHandle {

    public enum TimeType {
        START, END
    }

    public static final String POSITION_CHANGED = "datePositionChanged";

    private final PropertyChangeSupport propertyChangeSupport;
    private final TimeType timeType;
    private final long date;
    //
    private final List<Plot> plots;
    //
    private double xPos;
    private double yPos;

    public DateHandle(long aDate, TimeType aTimeType, Point2D aPosition) {
        propertyChangeSupport = new PropertyChangeSupport(DateHandle.this);
        date = aDate;
        timeType = aTimeType;
        plots = new LinkedList<>();
        xPos = aPosition.getX();
        yPos = aPosition.getY();
    }

    public long getDate() {
        return date;
    }

    public List<Plot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    public void addPlot(Plot plot) {
        plots.add(plot);
        // TODO : check if really needed
        plot.setX(xPos);
        plot.addPropertyChangeListener(this::handlePlotUpdate);
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void setX(double newXPos) {
        if (Math.abs(xPos - newXPos) > GridPositionable.EPSILON) {
            xPos = newXPos;
            plots.forEach(p -> p.setX(xPos));
            propertyChangeSupport.firePropertyChange(POSITION_CHANGED, xPos, yPos);
        }
    }

    public void setY(double newYPos) {
        if (Math.abs(yPos - newYPos) > GridPositionable.EPSILON) {
            yPos = newYPos;
            propertyChangeSupport.firePropertyChange(POSITION_CHANGED, xPos, yPos);
        }
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    private void handlePlotUpdate(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED ->
                setX((double) event.getOldValue());
            case Plot.SELECTION_CHANGED, Plot.PLOT_SIZE_CHANGED, Plot.PLOT_VISIBILITY_CHANGED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }
}
