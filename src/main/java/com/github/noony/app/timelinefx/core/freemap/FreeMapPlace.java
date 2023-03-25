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
import java.util.Collections;
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
    public static final double DEFAULT_PLACE_PADDING = 8;

    private static final double DEFAULT_MIN_X = 0;
    private static final double DEFAULT_MAX_X = 0;

    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final Place place;
    private final List<Person> persons;
    private final List<Plot> plots;
    //
    private double yPos;
    private double placeStayWidth;
    private double height = DEFAULT_HEIGHT;
    private double fontSize;
    private double placeNameWidth;
    private double plotSeparation;
    //
    private double minX = DEFAULT_MIN_X;
    private double maxX = DEFAULT_MAX_X;

    protected FreeMapPlace(Place aPlace, double aPlotSeparation, double aNameWidth, double aFontSize) {
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPlace.this);
        place = aPlace;
        plots = new LinkedList<>();
        persons = new LinkedList<>();
        plotSeparation = aPlotSeparation;
        placeNameWidth = aNameWidth;
        fontSize = aFontSize;
    }

    public Place getPlace() {
        return place;
    }

    // TODO update layout
//    public void setNameWidth(double newNameWidth) {
//        placeNameWidth = newNameWidth;
//        propertyChangeSupport.firePropertyChange(NAME_WIDTH_CHANGED, this, placeNameWidth);
//    }
    public double getPlaceNameWidth() {
        return placeNameWidth;
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
        updateMinMaxX();
        setHeight(Math.max(PLACE_NAME_HEIGHT, plotSeparation * (persons.size() + 1)));
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

    public double getPlaceStayWidth() {
        return placeStayWidth;
    }

    public double getHeight() {
        return height;
    }

    public void setPlaceStaysWidth(double newWidth) {
        placeStayWidth = newWidth;
        propertyChangeSupport.firePropertyChange(WIDTH_POS_CHANGED, this, placeStayWidth);
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

    public List<Plot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    protected void removePerson(Person aPerson) {
        if (persons.remove(aPerson)) {
            plots.stream().forEach(plot -> {
                var personPlot = plot.getPerson();
                var index = indexOf(personPlot);
                plot.setY(yPos + (index + 1) * plotSeparation);
                plot.addPropertyChangeListener(this::handlePlotChange);
            });
            updateMinMaxX();
            setHeight(Math.max(PLACE_NAME_HEIGHT, plotSeparation * (persons.size() + 1)));
        }
    }

    private void handlePlotChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Plot.POS_CHANGED, Plot.PLOT_DATE_CHANGED -> {
                updateMinMaxX();
            }
            case Plot.PLOT_SIZE_CHANGED, Plot.PLOT_VISIBILITY_CHANGED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    private void updateMinMaxX() {
        var oldMin = minX;
        var oldMax = maxX;
        //
        minX = plots.stream().mapToDouble(Plot::getX).min().orElse(DEFAULT_MIN_X) - DEFAULT_PLACE_PADDING;
        maxX = plots.stream().mapToDouble(Plot::getX).max().orElse(DEFAULT_MAX_X) + DEFAULT_PLACE_PADDING;
        //
        if (Math.abs(oldMax - maxX) + Math.abs(oldMin - minX) > GridPositionable.EPSILON) {
            propertyChangeSupport.firePropertyChange(MIN_MAX_X_CHANGED, minX, maxX);
        }
    }

}
