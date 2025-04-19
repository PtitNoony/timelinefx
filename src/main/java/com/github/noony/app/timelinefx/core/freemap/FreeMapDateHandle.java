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

import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class FreeMapDateHandle {

    public enum TimeType {
        START, END
    }

    public static final String PLOT_REMOVED = "plotRemoved";
    public static final String POSITION_CHANGED = "datePositionChanged";

    private static final Map<Long, List<FreeMapDateHandle>> FACTORY_CONTENT = new HashMap<>();

    public static final FreeMapDateHandle createFreeMapDateHandle(long aFriezeFreeMapID, double aDate, TimeType aTimeType, Point2D aPosition) {
        var freeMapDateHandles = FACTORY_CONTENT.getOrDefault(aFriezeFreeMapID, new LinkedList<>());
        if (freeMapDateHandles.isEmpty()) {
            FACTORY_CONTENT.put(aFriezeFreeMapID, freeMapDateHandles);
        }
        if (freeMapDateHandles.stream().anyMatch(dh -> dh.date == aDate && aTimeType == dh.timeType)) {
            throw new IllegalStateException("Cannot create a FreeMapDateHandle twice. date(" + aDate + " in " + FriezeFreeMapFactory.getFriezeFreeMap(aFriezeFreeMapID).getName() + ")");
        }
        var freeMapDateHandle = new FreeMapDateHandle(aDate, aTimeType, aPosition);
        freeMapDateHandles.add(freeMapDateHandle);
        return freeMapDateHandle;
    }

    public static final void resetFactory() {
        FACTORY_CONTENT.clear();
    }

    private final PropertyChangeSupport propertyChangeSupport;
    private final PropertyChangeListener propertyChangeListener;
    private final TimeType timeType;
    private final double date;
    //
    private final List<FreeMapPlot> plots;
    //
    private double xPos;
    private double yPos;

    private FreeMapDateHandle(double aDate, TimeType aTimeType, Point2D aPosition) {
        propertyChangeSupport = new PropertyChangeSupport(FreeMapDateHandle.this);
        propertyChangeListener = FreeMapDateHandle.this::handlePlotUpdate;
        date = aDate;
        timeType = aTimeType;
        plots = new LinkedList<>();
        xPos = aPosition.getX();
        yPos = aPosition.getY();
    }

    public double getDate() {
        return date;
    }

    public List<FreeMapPlot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    protected void addPlot(FreeMapPlot plot) {
        if (plot.getDate() == date && !plots.contains(plot)) {
            plots.add(plot);
            plot.addPropertyChangeListener(propertyChangeListener);
            plot.setX(xPos);
        }
    }

    protected void removePlot(FreeMapPlot plot) {
        if (plot.getDate() == date && plots.remove(plot)) {
            plot.removePropertyChangeListener(propertyChangeListener);
        }
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
            case FreeMapPlot.POS_CHANGED -> {
                setX((double) event.getOldValue());
            }
            case FreeMapPlot.SELECTION_CHANGED, FreeMapPlot.PLOT_SIZE_CHANGED, FreeMapPlot.PLOT_VISIBILITY_CHANGED -> {
                // nothing to do
            }
            case FreeMapPlot.PLOT_DATE_CHANGED -> {
                var plot = (FreeMapPlot) event.getOldValue();
                if (plot.getDate() != date) {
                    plots.remove(plot);
                    plot.removePropertyChangeListener(propertyChangeListener);
                    propertyChangeSupport.firePropertyChange(PLOT_REMOVED, this, plot);
                }
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    @Override
    public String toString() {
        return "[t=" + date + "  " + timeType + "  x=" + xPos + "]";
    }

}
