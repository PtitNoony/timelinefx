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

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.StayPeriod;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public final class FriezeFreeMap extends FriezeObject {

    // PropertyChangeEvent names
    public static final String LAYOUT_CHANGED = "layoutChanged";
    public static final String NAME_CHANGED = "nameChanged";
    public static final String FREE_MAP_PLACE_ADDED = "freeMapPlaceAdded";
    public static final String FREE_MAP_PLACE_REMOVED = "freeMapPlaceRemoved";
    public static final String FREE_MAP_PERSON_ADDED = "freeMapPersonAdded";
    public static final String FREE_MAP_PERSON_REMOVED = "freeMapPersonRemoved";
    public static final String FREE_MAP_PORTRAIT_REMOVED = "freeMapPortraitRemoved";
    public static final String FREE_MAP_PLOT_VISIBILITY_CHANGED = "freeMapPlotVisibilityChanged";
    public static final String FREE_MAP_PLOT_SIZE_CHANGED = "freeMapPlotSizeChanged";
    public static final String START_DATE_HANDLE_ADDED = "freeMapStartDateAdded";
    public static final String START_DATE_HANDLE_REMOVED = "freeMapStartDateRemoved";
    public static final String END_DATE_HANDLE_ADDED = "freeMapEndDateAdded";
    public static final String END_DATE_HANDLE_REMOVED = "freeMapEndDateRemoved";

    // Default Values for layout
    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 600;
    private static final double DEFAULT_PERSONS_WIDTH = 220;
    private static final double DEFAULT_PLACE_NAMES_WIDTH = 100;
    //
    public static final double DEFAULT_PORTRAIT_RADIUS = 50;

    public static final double DEFAULT_TIME_HEIGHT = 25;
    private static final double DEFAULT_FONT_SIZE = 12;
    private static final double DEFAULT_PLOT_SEPARATION = 8.0;
    private static final boolean DEFAULT_PLOT_VISIBILITY = true;
    private static final double DEFAULT_PLOT_SIZE = 4;

    private static final String DEFAULT_NAME = "FreeMap";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport;

    private final Frieze frieze;
    private final List<Person> persons;
    private final Map<Place, FreeMapPlace> places;
    private final Map<Person, FreeMapPerson> freeMapPersons;
    private final Map<Person, Portrait> portraits;
    private final List<PersonInitLink> personInitLinks;
    private final Map<Long, DateHandle> startDateHandles;
    private final Map<Long, DateHandle> endDateHandles;
    //
    private String name;
    //
    private double width;
    private double height;
    //TODO : rename into portraitWidth
    private double personsWidth;
//    private double padding;
    private double placeDrawingWidth;
    private double placeNamesWidth;
//    @Deprecated
    private double portraitRadius;
    private double fontSize;
    private double plotSeparation;
    private boolean plotVisibiltiy;
    private double plotSize;
    //
    private long minDate;
    private long maxDate;
    private double availableWidth;
    private double timeRatio;
    //
    private TimeMode timeMode;

    protected FriezeFreeMap(long anID, Frieze aFrieze, Dimension2D aFriezeDimension, double aPersonWidth, double aPlaceNameWidth, double aFontSize, double aPlotSeparation, boolean aPlotVisibilty, double aPlotSize) {
        super(anID);
        propertyChangeSupport = new PropertyChangeSupport(FriezeFreeMap.this);
        frieze = aFrieze;
        persons = new LinkedList<>();
        personInitLinks = new LinkedList<>();
        places = new HashMap<>();
        freeMapPersons = new HashMap<>();
        portraits = new HashMap<>();
        startDateHandles = new HashMap<>();
        endDateHandles = new HashMap<>();
        //
        name = DEFAULT_NAME;
        width = aFriezeDimension.getWidth();
        height = aFriezeDimension.getHeight();
//        padding = DEFAULT_PADDING;
        // TODO set in method
        personsWidth = aPersonWidth;
        placeNamesWidth = aPlaceNameWidth;
        placeDrawingWidth = width - placeNamesWidth - personsWidth;
        if (placeDrawingWidth < 0) {
            throw new IllegalStateException("placeDrawingWidth cannot be negative");
        }
        fontSize = aFontSize;
        plotSeparation = aPlotSeparation;
        plotVisibiltiy = aPlotVisibilty;
        plotSize = aPlotSize;
        portraitRadius = DEFAULT_PORTRAIT_RADIUS;
        // TODO use a map for all relevant attributes
        minDate = frieze.getMinDate();
        maxDate = frieze.getMaxDate();
        //
        frieze.getPlaces().stream().forEachOrdered(FriezeFreeMap.this::addFreeMapPlace);
        frieze.getPersons().stream().forEachOrdered(FriezeFreeMap.this::addFreeMapPerson);
        frieze.getStayPeriods().stream().forEachOrdered(FriezeFreeMap.this::addStay);
        //
        frieze.addListener(FriezeFreeMap.this::handleFriezeChanges);
        //
        updateLayout();
        //
        distributePlaces();
        distributePortraits();
        displayTimeAsProportional();
    }

    protected FriezeFreeMap(long anID, Frieze aFrieze) {
        this(anID, aFrieze, new Dimension2D(DEFAULT_WIDTH, DEFAULT_HEIGHT), DEFAULT_PERSONS_WIDTH, DEFAULT_PLACE_NAMES_WIDTH, DEFAULT_FONT_SIZE, DEFAULT_PLOT_SEPARATION, DEFAULT_PLOT_VISIBILITY, DEFAULT_PLOT_SIZE);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    //     <editor-fold defaultstate="collapsed" desc="Getters">
    public FreeMapPlace getFreeMapPlace(Place place) {
        return places.get(place);
    }

    public Frieze getFrieze() {
        return frieze;
    }

    public String getName() {
        return name;
    }

    public double getFreeMapWidth() {
        return width;
    }

    public double getFreeMapHeight() {
        return height;
    }

    public double getPlaceDrawingX() {
        return personsWidth;
    }

    public double getPlaceDrawingY() {
        return 0.0;
    }

    public double getPersonsDrawingX() {
        return 0.0;
    }

    public double getPersonsDrawingY() {
        return 0.0;
    }

    public double getPersonWidth() {
        return personsWidth;
    }

    public double getPersonHeight() {
        return height;
    }

    public double getPlaceDrawingWidth() {
        return placeDrawingWidth;
    }

    public double getPlaceDrawingHeight() {
        return height;
    }

    public double getPlaceNamesWidth() {
        return placeNamesWidth;
    }

    public double getFontSize() {
        return fontSize;
    }

    public double getPlotSeparation() {
        return plotSeparation;
    }

    public boolean getPlotVisibility() {
        return plotVisibiltiy;
    }

    public double getTimeHeight() {
        // todo make it variable
        return DEFAULT_TIME_HEIGHT;
    }

    public List<DateHandle> getStartDateHandles() {
        return Collections.unmodifiableList(startDateHandles.values().stream().collect(Collectors.toList()));
    }

    public List<DateHandle> getEndDateHandles() {
        return Collections.unmodifiableList(endDateHandles.values().stream().collect(Collectors.toList()));
    }

    public DateHandle getStartDateHandle(long aDate) {
        return startDateHandles.get(aDate);
    }

    public DateHandle getEndDateHandle(long aDate) {
        return endDateHandles.get(aDate);
    }

    public List<PersonInitLink> getPersonInitLinks() {
        return Collections.unmodifiableList(personInitLinks);
    }

    public List<FreeMapPlace> getPlaces() {
        return Collections.unmodifiableList(places.values().stream().collect(Collectors.toList()));
    }

    public List<FreeMapPerson> getPersons() {
        return Collections.unmodifiableList(freeMapPersons.values().stream().collect(Collectors.toList()));
    }

    public List<Portrait> getPortraits() {
        return Collections.unmodifiableList(portraits.values().stream().collect(Collectors.toList()));
    }

    // TODO :: usefull ?
    public int getPersonIndexAtPlace(Person aPerson, Place aPlace) {
        var freeMapPlace = places.get(aPlace);
        if (freeMapPlace == null) {
            return -1;
        }
        return freeMapPlace.indexOf(aPerson);
    }

    public double getPlotSize() {
        return plotSize;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public FreeMapPlace getFreeMapPlace(long placeID) {
        return places.values().stream().filter(p -> p.getPlace().getId() == placeID).findAny().orElse(null);
    }

    public Portrait getPortrait(long portraitID) {
        return portraits.values().stream().filter(p -> p.getPerson().getId() == portraitID).findAny().orElse(null);
    }

    public Portrait getPortrait(Person aPerson) {
        return portraits.values().stream().filter(p -> p.getPerson() == aPerson).findAny().orElse(null);
    }

    @Deprecated
    public double getPortraitRadius() {
        return portraitRadius;
    }

// </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void setName(String newName) {
        name = newName;
        propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
    }

    public void setWidth(double newWidth) {
        width = newWidth;
        updateLayout();
    }

    public void setHeight(double newHeight) {
        height = newHeight;
        updateLayout();
    }

    public void setPersonWidth(double aPersonWidth) {
        personsWidth = aPersonWidth;
        updateLayout();
    }

    public void setPlaceNameWidth(double aPlaceNameWidth) {
        placeNamesWidth = aPlaceNameWidth;
        updateLayout();
    }

    public void setFontSize(double newFontSize) {
        fontSize = newFontSize;
        places.values().forEach(freeMapPlace -> freeMapPlace.setFontSize(newFontSize));
    }

    public void setPlotSeparation(double plotSeparation) {
        places.values().forEach(place -> place.setPlotSeparation(plotSeparation));
        runLater(this::distributePlaces);
    }

    public void setPlotVisibility(boolean plotVisible) {
        plotVisibiltiy = plotVisible;
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLOT_VISIBILITY_CHANGED, this, plotVisibiltiy);
    }

    public void setPlotSize(double newPlotSize) {
        plotSize = newPlotSize;
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLOT_SIZE_CHANGED, this, plotSize);
    }

// </editor-fold>
    //
    public void distributePortraits() {
        var nbPortraits = portraits.size();
        var separation = (getPersonHeight() - nbPortraits * portraitRadius * 2.0) / (1 + 2 * nbPortraits);
        var portraitList = portraits.values().stream().collect(Collectors.toList());
        for (var index = 0; index < nbPortraits; index++) {
            Portrait portrait = portraitList.get(index);
            portrait.setX(getPersonWidth() / 2.0);
            portrait.setY(separation * (index + 1) + (index + 0.5) * portraitRadius * 2.0);
        }
    }

    public final void distributePlaces() {
        var nbPlaces = places.size();
        var freeMapPlaces = places.values().stream().sorted((p1, p2) -> Double.compare(p1.getYPos(), p2.getYPos())).collect(Collectors.toList());
        var placesHeight = freeMapPlaces.stream().mapToDouble(FreeMapPlace::getHeight).sum();
        var separation = (getPersonHeight() - placesHeight) / (1 + 2 * nbPlaces);
        var currentHeight = 0;
        for (var index = 0; index < nbPlaces; index++) {
            FreeMapPlace freeMapPlace = freeMapPlaces.get(index);
            freeMapPlace.setY(separation * (index + 1) + currentHeight);
            currentHeight += freeMapPlace.getHeight();
        }
    }

    public final void displayTimeAsProportional() {
        availableWidth = getPlaceDrawingWidth();
        timeRatio = (maxDate - minDate) / availableWidth;
        startDateHandles.forEach((d, h) -> h.setX((d - minDate) / timeRatio));
        endDateHandles.forEach((d, h) -> h.setX((d - minDate) / timeRatio));
    }

    public void displayTimeAsEqualSplit() {
        throw new UnsupportedOperationException("displayTimeAsEqualSplit :: TODO");
    }

    public double getDateX(long aDate) {
        return (aDate - minDate) / timeRatio;
    }

    public void setPortraitRadius(double newPortraitRadius) {
        portraitRadius = newPortraitRadius;
        portraits.values().forEach(portrait -> portrait.setRadius(newPortraitRadius));
    }

    private void addFreeMapPerson(Person person) {
        var freeMapPerson = new FreeMapPerson(person, this);
        persons.add(person);
        freeMapPersons.put(person, freeMapPerson);
        portraits.put(person, freeMapPerson.getPortrait());
        propertyChangeSupport.firePropertyChange(FREE_MAP_PERSON_ADDED, this, freeMapPerson);
    }

    private void addStay(StayPeriod stayPeriod) {
        var person = stayPeriod.getPerson();
        // TODO make sure the person is already added
        var freeMapPerson = freeMapPersons.get(person);
        if (freeMapPerson == null) {
            LOG.log(Level.SEVERE, "Could not add stayDrawing ({0}) since corresponding freemapPerson ({1}) does not exits.", new Object[]{stayPeriod, person});
            return;
        }
        //
        minDate = frieze.getMinDate();
        maxDate = frieze.getMaxDate();

        // This should be the responsability of the Frieze to propagate changes on these...
        var startDate = stayPeriod.getStartDate();
        if (!startDateHandles.containsKey(startDate)) {
            var startDateHandle = createDateHandle(startDate, DateHandle.TimeType.START);
            startDateHandles.put(startDate, startDateHandle);
            propertyChangeSupport.firePropertyChange(START_DATE_HANDLE_ADDED, this, startDateHandle);

        }
        var endDate = stayPeriod.getEndDate();
        if (!endDateHandles.containsKey(endDate)) {
            var endDateHandle = createDateHandle(endDate, DateHandle.TimeType.END);
            endDateHandles.put(endDate, endDateHandle);
            propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_ADDED, this, endDateHandle);
        }
        freeMapPerson.addStay(stayPeriod);
    }

    private void removeStay(StayPeriod stayPeriod) {
        var person = stayPeriod.getPerson();
        // TODO make sure the person is already added
        var freeMapPerson = freeMapPersons.get(person);
        if (freeMapPerson == null) {
            LOG.log(Level.SEVERE, "Could not remove stayDrawing ({0}) since corresponding freemapPerson ({1}) does not exits.", new Object[]{stayPeriod, person});
            return;
        }
        freeMapPerson.removeStay(stayPeriod);
        //
        minDate = frieze.getMinDate();
        maxDate = frieze.getMaxDate();
        //
        updateDateHandles();
    }

    private void removePerson(Person person) {
        var periods = frieze.getStayPeriods(person);
        persons.remove(person);
        var freeMapPerson = freeMapPersons.remove(person);
        periods.forEach(this::removeStay);
        if (freeMapPerson == null) {
            LOG.log(Level.SEVERE, "Could not remove freemapPerson ({0}) which does not exits.", new Object[]{person});
            return;
        }
        places.forEach((place, freemapPlace) -> freemapPlace.removePerson(person));
        //
        propertyChangeSupport.firePropertyChange(FREE_MAP_PERSON_REMOVED, this, freeMapPerson);
        var removedPortrait = portraits.remove(person);
        if (removedPortrait != null) {
            propertyChangeSupport.firePropertyChange(FREE_MAP_PORTRAIT_REMOVED, this, removedPortrait);
        }
    }

//    private void createPersonInitLink(Person person) {
//        var initLink = new PersonInitLink(person, this);
//        personInitLinks.add(initLink);
//    }
    private void addFreeMapPlace(Place aPlace) {
        var freeMapPlace = new FreeMapPlace(aPlace, plotSeparation, placeNamesWidth, fontSize);
        freeMapPlace.setWidth(getPlaceDrawingWidth());
        places.put(aPlace, freeMapPlace);
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLACE_ADDED, null, freeMapPlace);
    }

    private DateHandle createDateHandle(long date, DateHandle.TimeType type) {
        var dates = frieze.getDates();
        int nbDates = frieze.getNbDates();
        double separation = (getPlaceDrawingWidth()) / (1 + 2 * nbDates);
        int index = dates.indexOf(date);
        var position = new Point2D((index * 2 + 1) * separation, DEFAULT_TIME_HEIGHT / 2.0);
        var handle = new DateHandle(date, type, position);
        return handle;
    }

    private void removeFreeMapPlace(Place aPlace) {
        var freeMapPlace = places.remove(aPlace);
        if (freeMapPlace != null) {
            propertyChangeSupport.firePropertyChange(FREE_MAP_PLACE_REMOVED, null, freeMapPlace);
        }
    }

    private void updateLayout() {
        var newDimension = new Dimension2D(width, height);
        placeDrawingWidth = width - personsWidth - placeNamesWidth;
        propertyChangeSupport.firePropertyChange(LAYOUT_CHANGED, this, newDimension);
        places.values().forEach(p -> p.setWidth(getPlaceDrawingWidth()));
        //
        displayTimeAsProportional();
    }

    private void updateDateHandles() {
        List<Long> startDates = frieze.getStartDates();
        List<Long> startDatesToBeRemoved = new LinkedList<>();
        frieze.getStartDates().forEach(startDate -> {
            if (!startDateHandles.containsKey(startDate)) {
                var startDateHandle = createDateHandle(startDate, DateHandle.TimeType.START);
                propertyChangeSupport.firePropertyChange(START_DATE_HANDLE_ADDED, this, startDateHandle);
            }
        });
        startDateHandles.forEach((startDate, handle) -> {
            if (!startDates.contains(startDate)) {
                startDatesToBeRemoved.add(startDate);
            }
        });
        startDatesToBeRemoved.forEach(startDateToRemove -> {
            var startDateHandle = startDateHandles.remove(startDateToRemove);
            propertyChangeSupport.firePropertyChange(START_DATE_HANDLE_REMOVED, this, startDateHandle);
        });
        //
        List<Long> endDates = frieze.getEndDates();
        List<Long> endDatesToBeRemoved = new LinkedList<>();
        frieze.getEndDates().forEach(endDate -> {
            if (!endDateHandles.containsKey(endDate)) {
                var endDateHandle = createDateHandle(endDate, DateHandle.TimeType.END);
                propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_ADDED, this, endDateHandle);
            }
        });
        endDateHandles.forEach((endDate, handle) -> {
            if (!endDates.contains(endDate)) {
                endDatesToBeRemoved.add(endDate);
            }
        });
        endDatesToBeRemoved.forEach(endDateToRemove -> {
            var endDateHandle = endDateHandles.remove(endDateToRemove);
            propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_REMOVED, this, endDateHandle);
        });
    }

    private void handleFriezeChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.DATE_WINDOW_CHANGED ->
                System.err.println("FriezeFreeMap TODO DATE_WINDOW_CHANGED");
            case Frieze.PERSON_ADDED -> {
                var personAdded = (Person) event.getNewValue();
                addFreeMapPerson(personAdded);
            }
            case Frieze.PERSON_REMOVED -> {
                var personRemoved = (Person) event.getNewValue();
                removePerson(personRemoved);
            }
            case Frieze.PLACE_ADDED -> {
                var placeAdded = (Place) event.getNewValue();
                addFreeMapPlace(placeAdded);
            }
            case Frieze.PLACE_REMOVED -> {
                var placeRemoved = (Place) event.getNewValue();
                removeFreeMapPlace(placeRemoved);
            }
            case Frieze.STAY_ADDED -> {
                var stayPeriodAdded = (StayPeriod) event.getNewValue();
                addStay(stayPeriodAdded);
            }
            case Frieze.STAY_REMOVED -> {
                var stayPeriodRemoved = (StayPeriod) event.getNewValue();
                removeStay(stayPeriodRemoved);
            }
        }
    }

}
