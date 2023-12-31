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

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arnaud
 */
public class FreeMapPlace implements FriezeObject {

    public static final String Y_POS_CHANGED = "yPosChanged";
    public static final String WIDTH_POS_CHANGED = "widthPosChanged";
    public static final String HEIGHT_POS_CHANGED = "heightPosChanged";
    public static final String MIN_MAX_X_CHANGED = "minMaxXChanged";
    public static final String NAME_WIDTH_CHANGED = "nameWidthChanged";
    public static final String FONT_SIZE_CHANGED = "fontSizeChanged";
    public static final String PLOT_SEPARATION_CHANGED = "plotSeparationChanged";

    public static final double PLACE_NAME_HEIGHT = 75;
    public static final double DEFAULT_HEIGHT = 75;
    public static final double DEFAULT_PLACE_PADDING = 16;

    private static final Logger LOG = Logger.getGlobal();

    private static final double DEFAULT_MIN_X = 0;
    private static final double DEFAULT_MAX_X = 0;

    //
    private static final Map<Long, List<FreeMapPlace>> FACTORY_CONTENT = new HashMap<>();

    public static final FreeMapPlace createFreeMapPlace(long aFriezeFreeMapID, Place aPlace, double aPlotSeparation, double aNameWidth, double aFontSize) {
        var freeMapPlaces = FACTORY_CONTENT.getOrDefault(aFriezeFreeMapID, new LinkedList<>());
        if (freeMapPlaces.isEmpty()) {
            FACTORY_CONTENT.put(aFriezeFreeMapID, freeMapPlaces);
        }
        if (freeMapPlaces.stream().anyMatch(fp -> fp.getPlace() == aPlace)) {
            throw new IllegalStateException("Cannot create a Freemap place twice. (" + aPlace.getName() + " in " + FriezeFreeMapFactory.getFriezeFreeMap(aFriezeFreeMapID).getName() + ")");
        }
        var freeMapPlace = new FreeMapPlace(aPlace, aPlotSeparation, aNameWidth, aFontSize);
        freeMapPlaces.add(freeMapPlace);
        return freeMapPlace;
    }

    public static final void resetFactory() {
        FACTORY_CONTENT.clear();
    }

    //
    private final PropertyChangeSupport propertyChangeSupport;

    //
    // paramters to be saved
    //
    private final long id;
    // for the order of each person when drawn
    private final List<FreeMapPerson> persons;
    private double yPos;
    private double fontSize;
    private double placeNameWidth;
    //
    // other instance parameters, usually calculated
    //
    private final Place place;
    private final List<FreeMapPlot> registeredPlots;
    //
    private double placeStayWidth;
    private double height = DEFAULT_HEIGHT;
    private double plotSeparation;
    //
    private double minX = DEFAULT_MIN_X;
    private double maxX = DEFAULT_MAX_X;

    private FreeMapPlace(Place aPlace, double aPlotSeparation, double aNameWidth, double aFontSize) {
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPlace.this);
        id = aPlace.getId();
        place = aPlace;
        registeredPlots = new LinkedList<>();
        persons = new LinkedList<>();
        plotSeparation = aPlotSeparation;
        placeNameWidth = aNameWidth;
        fontSize = aFontSize;
    }

    @Override
    public long getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

    public String getName() {
        return place.getName();
    }

    // TODO update layout
//    public void setNameWidth(double newNameWidth) {
//        placeNameWidth = newNameWidth;
//        propertyChangeSupport.firePropertyChange(NAME_WIDTH_CHANGED, this, placeNameWidth);
//    }
    public double getNameWidth() {
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

    /**
     * This method adds persons that are not contained and requires the array to contains at least the persons already contained in the place. And this method adds the remaining
     * persons even if no stays are linked.
     *
     * @param personsSorted an array containing the persons in the new order (top to bottom)
     * @return return false if the conditions are not met.
     */
    public boolean setPersonOrder(FreeMapPerson[] personsSorted) {
        List<FreeMapPerson> personsSortedAsList = Arrays.asList(personsSorted);
        for (var person : persons) {
            if (!personsSortedAsList.contains(person)) {
                LOG.log(Level.WARNING, "Could not setPersonOrder for place {0} since person {1} is missing.", new Object[]{getName(), person.getName()});
                return false;
            }
        }
        persons.clear();
        persons.addAll(personsSortedAsList);
        // update plots
        setY(yPos);
        LOG.log(Level.INFO, "Successfull setPersonOrder for FreeMapPlace {0}.", new Object[]{getName()});
        return true;
    }

    protected void registerFreeMapPlot(FreeMapPlot plot) {
        registeredPlots.add(plot);
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

    public List<FreeMapPerson> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public int indexOf(FreeMapPerson p) {
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
        registeredPlots.forEach(plot -> {
            var deltaY = plot.getY() - oldY;
            plot.setY(yPos + deltaY);
        });
        propertyChangeSupport.firePropertyChange(Y_POS_CHANGED, this, yPos);
    }

    public List<FreeMapPlot> getPlots() {
        return Collections.unmodifiableList(registeredPlots);
    }

    protected void removePerson(Person aPerson) {
        var freemapPersonToRemove = persons.stream().filter(fmP -> fmP.getPerson() == aPerson).findAny().orElse(null);
        if (freemapPersonToRemove != null) {
            persons.remove(freemapPersonToRemove);
            registeredPlots.stream().forEach(plot -> {
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
            case FreeMapPlot.POS_CHANGED, FreeMapPlot.PLOT_DATE_CHANGED -> {
                updateMinMaxX();
            }
            case FreeMapPlot.PLOT_SIZE_CHANGED, FreeMapPlot.PLOT_VISIBILITY_CHANGED -> {
                // nothing to do
            }
            case Selectable.SELECTION_CHANGED -> {
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
        minX = registeredPlots.stream().mapToDouble(FreeMapPlot::getX).min().orElse(DEFAULT_MIN_X) - DEFAULT_PLACE_PADDING;
        maxX = registeredPlots.stream().mapToDouble(FreeMapPlot::getX).max().orElse(DEFAULT_MAX_X) + DEFAULT_PLACE_PADDING;
        //
        if (Math.abs(oldMax - maxX) + Math.abs(oldMin - minX) > GridPositionable.EPSILON) {
            propertyChangeSupport.firePropertyChange(MIN_MAX_X_CHANGED, minX, maxX);
        }
    }

}
