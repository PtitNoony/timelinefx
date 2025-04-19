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
package com.github.noony.app.timelinefx.core.freemap.connectors;

import com.github.noony.app.timelinefx.core.freemap.FreeMapLink;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class FreeMapLinkConnector implements FreeMapConnector {

    public static final String PLOT_DATE_CHANGED = "plotDateChanged";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport;
    private final long id;
    private final FreeMapLink sourceLink;

    private double date;
    private final DoubleProperty xPos;
    private final DoubleProperty yPos;

    private boolean isVisible = true;
    private boolean isSelected = false;
    private double plotSize;
    private Color color;

    protected FreeMapLinkConnector(long anID, FreeMapLink aSourceLink, double aDate, double aPlotSize) {
        id = anID;
        propertyChangeSupport = new PropertyChangeSupport(FreeMapLinkConnector.this);
        sourceLink = aSourceLink;
        //
        xPos = new SimpleDoubleProperty();
        yPos = new SimpleDoubleProperty();
        //
        date = aDate;
        plotSize = aPlotSize;
        updateXPosFollowingDateUpdate();
        // Assumption: the link is horizontal
        yPos.setValue(sourceLink.getBeginConnector().getY());
        color = DEFAULT_COLOR;
    }

    @Override
    public long getId() {
        return id;
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
    public Color getColor() {
        return color;
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
    public void setColor(Color color) {
        this.color = color;
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
        var boundedNewX = Math.max(sourceLink.getBeginConnector().getX(), newX);
        boundedNewX = Math.min(sourceLink.getEndConnector().getX(), boundedNewX);
        if (Math.abs(boundedNewX - xPos.doubleValue()) > EPSILON) {
            xPos.setValue(boundedNewX);
            propertyChangeSupport.firePropertyChange(POS_CHANGED, xPos.doubleValue(), yPos.doubleValue());
            //
            var dateMin = sourceLink.getBeginConnector().getDate();
            var dateMax = sourceLink.getEndConnector().getDate();
            var xMin = sourceLink.getBeginConnector().getX();
            var xMax = sourceLink.getEndConnector().getX();
            var xPercentage = (xPos.getValue() - xMin) / (xMax - xMin);
            var newDate = dateMin + xPercentage * (dateMax - dateMin);
            date = newDate;
            propertyChangeSupport.firePropertyChange(PLOT_DATE_CHANGED, this, date);
        }
    }

    public void setY(double newY) {
        // hypothesis: the link is horizontal
        // overriding newY
        newY = sourceLink.getBeginConnector().getY();
        if (Math.abs(yPos.doubleValue() - newY) > EPSILON) {
            yPos.setValue(sourceLink.getBeginConnector().getY());
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
            var dateMin = sourceLink.getBeginConnector().getDate();
            var dateMax = sourceLink.getEndConnector().getDate();
            if (date < dateMin || date > dateMax) {
                LOG.log(Level.SEVERE, "Could not set date for {0} since not in the correct bounds. ({1} not in [ {2} - {3} ]",
                        new Object[]{this, date, dateMin, dateMax});
            } else {
                date = aDate;
                propertyChangeSupport.firePropertyChange(PLOT_DATE_CHANGED, this, date);
                updateXPosFollowingDateUpdate();
            }
        }
    }

    private void updateXPosFollowingDateUpdate() {
        var dateMin = sourceLink.getBeginConnector().getDate();
        var dateMax = sourceLink.getEndConnector().getDate();
        // assumes checks on date range have been made
        var xMin = sourceLink.getBeginConnector().getX();
        var xMax = sourceLink.getEndConnector().getX();
        var datePercentage = (date - dateMin) / (dateMax - dateMin);
        var newX = xMin + datePercentage * (xMax - xMin);
        xPos.setValue(newX);
        propertyChangeSupport.firePropertyChange(POS_CHANGED, xPos.doubleValue(), yPos.doubleValue());
    }

    @Override
    public String toString() {
        return "[Plot t=" + date + "]";
    }

}
