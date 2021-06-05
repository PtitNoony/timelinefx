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

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author arnaud
 */
public class FreeMapPlace {

    public static final String Y_POS_CHANGED = "yPosChanged";
    public static final String WIDTH_POS_CHANGED = "widthPosChanged";
    public static final String HEIGHT_POS_CHANGED = "heightPosChanged";
    public static final String MIN_MAX_X_CHANGED = "minMaxXChanged";
    public static final String NAME_WIDTH_CHANGED = "nameWidthChanged";
    public static final String FONT_SIZE_CHANGED = "fontSizeChanged";
    public static final String PLOT_SEPARATION_CHANGED = "plotSeparationChanged";

    public static final double PLACE_NAME_HEIGHT = 18;
    public static final double DEFAULT_HEIGHT = 25;

    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final Place place;
    private final List<Person> persons;
    private final List<Plot> plots;
    //
    private double yPos;
    private double fullWidth;
    private double height = DEFAULT_HEIGHT;
    private double fontSize;
    private double nameWidth;
    private double plotSeparation;
    //
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;

    public FreeMapPlace(Place aPlace, double aPlotSeparation, double aNameWidth, double aFontSize) {
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPlace.this);
        place = aPlace;
        plots = new LinkedList<>();
        persons = new LinkedList<>();
        plotSeparation = aPlotSeparation;
        nameWidth = aNameWidth;
        fontSize = aFontSize;
    }

    public Place getPlace() {
        return place;
    }

    public void setNameWidtah(double newNameWidth) {
        nameWidth = newNameWidth;
        propertyChangeSupport.firePropertyChange(NAME_WIDTH_CHANGED, this, nameWidth);
    }

    public double getNameWidth() {
        return nameWidth;
    }

    public void setFontSize(double newFontSize) {
        fontSize = newFontSize;
        propertyChangeSupport.firePropertyChange(FONT_SIZE_CHANGED, this, fontSize);
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setPlotSeparation(double newPlotSeparation) {
        plotSeparation = newPlotSeparation;
        propertyChangeSupport.firePropertyChange(PLOT_SEPARATION_CHANGED, this, plotSeparation);
    }

    public double getPlotSeparation() {
        return plotSeparation;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPlot(Plot plot) {
        plots.add(plot);
        var person = plot.getPerson();
        if (!persons.contains(person)) {
            persons.add(person);
        }
        var index = indexOf(person);
        plot.setY(yPos + (index + 1) * plotSeparation);
        plot.addPropertyChangeListener(this::handlePlotChange);
        var oldMin = minX;
        var oldMax = maxX;
        minX = Math.min(minX, plot.getX());
        maxX = Math.max(maxX, plot.getX());
        if (Math.abs(oldMax - maxX) + Math.abs(oldMin - minX) > GridPositionable.EPSILON) {
            propertyChangeSupport.firePropertyChange(MIN_MAX_X_CHANGED, minX, maxX);
        }
        height = Math.max(PLACE_NAME_HEIGHT, plotSeparation * (persons.size() + 1));
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public int indexOf(Person p) {
        return persons.indexOf(p);
    }

    public double getYPos() {
        return yPos;
    }

    public double getWidth() {
        return fullWidth;
    }

    public double getHeight() {
        return height;
    }

    public void setWidth(double newWidth) {
        fullWidth = newWidth;
        propertyChangeSupport.firePropertyChange(WIDTH_POS_CHANGED, this, fullWidth);
    }

    public void setHeight(double newHeight) {
        height = newHeight;
        propertyChangeSupport.firePropertyChange(HEIGHT_POS_CHANGED, this, height);
    }

    public void setY(double newY) {
        var oldY = yPos;
        yPos = newY;
        plots.forEach(plot -> {
            var deltaY = plot.getY() - oldY;
            plot.setY(yPos + deltaY);
        });
        propertyChangeSupport.firePropertyChange(Y_POS_CHANGED, this, yPos);
    }

    private void handlePlotChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED -> {
                var oldMin = minX;
                var oldMax = maxX;
                minX = plots.stream().mapToDouble(Plot::getX).min().orElse(0);
                maxX = plots.stream().mapToDouble(Plot::getX).max().orElse(0);
                if (Math.abs(oldMax - maxX) + Math.abs(oldMin - minX) > GridPositionable.EPSILON) {
                    propertyChangeSupport.firePropertyChange(MIN_MAX_X_CHANGED, minX, maxX);
                }
            }
            case Plot.PLOT_SIZE_CHANGED, Plot.PLOT_VISIBILITY_CHANGED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

}
