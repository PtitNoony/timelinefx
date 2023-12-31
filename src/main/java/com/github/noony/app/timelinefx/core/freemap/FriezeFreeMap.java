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
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
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
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public final class FriezeFreeMap implements FriezeObject {

    public static final String FRIEZE_WIDTH = "friezeWidth";
    public static final String FRIEZE_HEIGHT = "friezeHeight";
    public static final String PERSONS_WIDTH = "personsWidth";
    public static final String PLACE_NAMES_WIDTH = "placeNamesWidth";
    public static final String FONT_SIZE = "fontSize";
    public static final String PLOT_SEPARATION = "plotSeparation";
    public static final String PLOT_SIZE = "plotSize";
    public static final String PLOT_VISIBILITY = "plotVisibility";
    public static final String PORTRAIT_CONNECTOR_VISIBILITY = "portraitConnectorVisibility";
    public static final String PORTRAIT_RADIUS = "portraitRadius";

    // PropertyChangeEvent names
    public static final String LAYOUT_CHANGED = "layoutChanged";
    public static final String NAME_CHANGED = "nameChanged";
    public static final String FREE_MAP_PLACE_ADDED = "freeMapPlaceAdded";
    public static final String FREE_MAP_PLACE_REMOVED = "freeMapPlaceRemoved";
    public static final String FREE_MAP_PERSON_ADDED = "freeMapPersonAdded";
    public static final String FREE_MAP_PERSON_REMOVED = "freeMapPersonRemoved";
    public static final String FREE_MAP_PORTRAIT_REMOVED = "freeMapPortraitRemoved";
    public static final String FREE_MAP_PLOT_SIZE_CHANGED = "freeMapPlotSizeChanged";
    public static final String START_DATE_HANDLE_ADDED = "freeMapStartDateAdded";
    public static final String START_DATE_HANDLE_REMOVED = "freeMapStartDateRemoved";
    public static final String END_DATE_HANDLE_ADDED = "freeMapEndDateAdded";
    public static final String END_DATE_HANDLE_REMOVED = "freeMapEndDateRemoved";

    // Default Values for layout
    private static final double DEFAULT_WIDTH = 3508;
    private static final double DEFAULT_HEIGHT = 2480;
    private static final double DEFAULT_PERSONS_WIDTH = 0;
    private static final double DEFAULT_PLACE_NAMES_WIDTH = 500;
    public static final double MAP_PADDING = 40;
    //
    public static final double DEFAULT_PORTRAIT_RADIUS = 180;
    public static final double DEFAULT_TIME_HEIGHT = 25;
    public static final double DEFAULT_FONT_SIZE = 46;
    public static final double DEFAULT_PLOT_SIZE = 8;
    //
    private static final double DEFAULT_PLOT_SEPARATION = 20;
    private static final boolean DEFAULT_PLOT_VISIBILITY = true;
    private static final boolean DEFAULT_PORTRAIT_CONNECTOR_VISIBILITY = true;

    private static final String DEFAULT_NAME = "FreeMap";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport;
    private final long id;

    private final Frieze frieze;
    // TODO remove ?
    private final List<FreeMapPerson> persons;
    private final Map<Place, FreeMapPlace> places;
    private final Map<Person, FreeMapPerson> freeMapPersons;
    private final Map<Double, FreeMapDateHandle> startDateHandles;
    private final Map<Double, FreeMapDateHandle> endDateHandles;
    //
    private String name;
    //
    private double width;
    private double height;
    //
    private double portraitWidth;
    private double placeDrawingWidth;
    private double placeNamesWidth;
    //
    private double portraitRadius;
    private double fontSize;
    private double plotSeparation;
    private boolean plotVisibiltiy;
    private boolean portraitConnectorsVisibiltiy;
    private double plotSize;
    //
    private double minDate;
    private double maxDate;
    private double availableWidth;
    private double timeRatio;
    //
//    private TimeMode timeMode;

    protected FriezeFreeMap(long anID, Frieze aFrieze, Map<String, String> inputParameters, List<FreeMapDateHandle> dateHandles,
            List<FreeMapPerson> somePersons, List<FreeMapPlace> somePlaces, List<FreeMapStay> someStays, boolean allStays) {
        id = anID;
        propertyChangeSupport = new PropertyChangeSupport(FriezeFreeMap.this);
        frieze = aFrieze;
        persons = new LinkedList<>();
        places = new HashMap<>();
        freeMapPersons = new HashMap<>();
        startDateHandles = new HashMap<>();
        endDateHandles = new HashMap<>();
        //
        name = DEFAULT_NAME;
        //
        loadParameters(inputParameters);
        //
        minDate = frieze.getMinDate();
        maxDate = frieze.getMaxDate();
        //
        dateHandles.forEach(dateHandle -> {
            switch (dateHandle.getTimeType()) {
                case START ->
                    startDateHandles.put(dateHandle.getDate(), dateHandle);
                case END ->
                    endDateHandles.put(dateHandle.getDate(), dateHandle);
                default ->
                    throw new UnsupportedOperationException("Could not load FreeMapDateHandle of type: " + dateHandle.getTimeType());
            }
        });
        // Add missing persons
        somePersons.forEach(this::addFreeMapPerson);
        // Add missing places
        somePlaces.forEach(this::addFreeMapPlace);
        //
        if (allStays) {
            frieze.getPlaces().stream().forEachOrdered(FriezeFreeMap.this::addPlace);
            frieze.getPersons().stream().forEachOrdered(FriezeFreeMap.this::addPerson);
            frieze.getStayPeriods().stream().forEachOrdered(FriezeFreeMap.this::addStay);

        } else {
            someStays.forEach(FriezeFreeMap.this::addFreeMapStay);
        }
        //
        frieze.addListener(FriezeFreeMap.this::handleFriezeChanges);
        //
        updateLayout();
        //
        distributePlaces();
// removed until new modes are created, at the moment part of updateLayout
//        displayTimeAsProportional();
    }

    protected FriezeFreeMap(long anID, Frieze aFrieze, boolean allStays) {
        this(anID, aFrieze, new HashMap<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), allStays);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public long getId() {
        return id;
    }

    //     <editor-fold defaultstate="collapsed" desc="Getters">
    public FreeMapPlace getFreeMapPlace(Place place) {
        return places.get(place);
    }

    public FreeMapPerson getFreeMapPerson(Person aPerson) {
        return freeMapPersons.get(aPerson);
    }

    public Frieze getFrieze() {
        return frieze;
    }

    public String getName() {
        return name;
    }

    public double getFreeMapDrawingWidth() {
        return width - 2 * getDrawingOffsetX();
    }

    public double getFreeMapDrawingHeight() {
        return height - 2 * getDrawingOffsetY();
    }

    public double getPlaceDrawingX() {
        return portraitWidth;
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
        return portraitWidth;
    }

    public double getPersonHeight() {
        return height;
    }

    public double getPlaceDrawingWidth() {
        return placeDrawingWidth;
    }

    public double getDrawingOffsetX() {
        return MAP_PADDING;
    }

    public double getDrawingOffsetY() {
        return MAP_PADDING;
    }

    public double getPlaceDrawingHeight() {
        return height;
    }

    public double getPlaceNamesWidth() {
        return placeNamesWidth;
    }

    public double getPlaceNamesXOffset() {
        return MAP_PADDING + placeDrawingWidth + MAP_PADDING;
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

    /**
     * This method can become time consuming.
     *
     * @return the start date handles
     */
    public List<FreeMapDateHandle> getStartDateHandles() {
        return Collections.unmodifiableList(startDateHandles.values().stream().collect(Collectors.toList()));
    }

    /**
     * This method can become time consuming.
     *
     * @return the end date handles
     */
    public List<FreeMapDateHandle> getEndDateHandles() {
        return Collections.unmodifiableList(endDateHandles.values().stream().collect(Collectors.toList()));
    }

    public FreeMapDateHandle getStartDateHandle(double aDate) {
        return startDateHandles.get(aDate);
    }

    public FreeMapDateHandle getEndDateHandle(double aDate) {
        return endDateHandles.get(aDate);
    }

    public List<FreeMapPlace> getPlaces() {
        return Collections.unmodifiableList(places.values().stream().collect(Collectors.toList()));
    }

    public List<FreeMapPerson> getPersons() {
        return Collections.unmodifiableList(freeMapPersons.values().stream().collect(Collectors.toList()));
    }

    /**
     * Costly method
     *
     * @return
     */
    public List<FreeMapPlot> getPlots() {
        return places.values().stream().flatMap(x -> x.getPlots().stream()).collect(Collectors.toList());
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
        portraitWidth = aPersonWidth;
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
        distributePlaces();
    }

    public void setPlotVisibility(boolean plotVisible) {
        plotVisibiltiy = plotVisible;
        freeMapPersons.forEach((p, fMP) -> fMP.setPlotsVisibilty(plotVisibiltiy));
    }

    public void setPortraitConnectorVisibility(boolean pConnectorVisibiliy) {
        portraitConnectorsVisibiltiy = pConnectorVisibiliy;
        freeMapPersons.forEach((p, fMP) -> fMP.setPortraitConnectorsVisibilty(portraitConnectorsVisibiltiy));
    }

    public void setPlotSize(double newPlotSize) {
        plotSize = newPlotSize;
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLOT_SIZE_CHANGED, this, plotSize);
    }

// </editor-fold>
    //
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
//        startDateHandles.forEach((d, h) -> h.setX((d - minDate) / timeRatio + FreeMapPlace.DEFAULT_PLACE_PADDING * 2));
//        endDateHandles.forEach((d, h) -> h.setX((d - minDate) / timeRatio + FreeMapPlace.DEFAULT_PLACE_PADDING * 2));
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
        System.err.println(" todo : setPortraitRadius");
//        portraits.values().forEach(portrait -> portrait.setRadius(newPortraitRadius));
    }

    /**
     *
     * @return a new map containing the frieze parameters
     */
    public Map<String, String> getParemeters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(FRIEZE_WIDTH, Double.toString(width));
        parameters.put(FRIEZE_HEIGHT, Double.toString(height));
        parameters.put(PERSONS_WIDTH, Double.toString(portraitWidth));
        parameters.put(PLACE_NAMES_WIDTH, Double.toString(placeNamesWidth));
        parameters.put(FONT_SIZE, Double.toString(fontSize));
        parameters.put(PLOT_SEPARATION, Double.toString(plotSeparation));
        parameters.put(PLOT_SIZE, Double.toString(plotSize));
        parameters.put(PLOT_VISIBILITY, Boolean.toString(plotVisibiltiy));
        parameters.put(PORTRAIT_CONNECTOR_VISIBILITY, Boolean.toString(portraitConnectorsVisibiltiy));
        parameters.put(PORTRAIT_RADIUS, Double.toString(portraitRadius));
        return parameters;
    }

    private void loadParameters(Map<String, String> parameters) {
        width = Double.parseDouble(parameters.getOrDefault(FRIEZE_WIDTH, Double.toString(DEFAULT_WIDTH)));
        height = Double.parseDouble(parameters.getOrDefault(FRIEZE_HEIGHT, Double.toString(DEFAULT_HEIGHT)));
        portraitWidth = Double.parseDouble(parameters.getOrDefault(PERSONS_WIDTH, Double.toString(DEFAULT_PERSONS_WIDTH)));
        placeNamesWidth = Double.parseDouble(parameters.getOrDefault(PLACE_NAMES_WIDTH, Double.toString(DEFAULT_PLACE_NAMES_WIDTH)));
        fontSize = Double.parseDouble(parameters.getOrDefault(FONT_SIZE, Double.toString(DEFAULT_FONT_SIZE)));
        plotSeparation = getPlotSeparationParamerter(parameters);
        plotSize = Double.parseDouble(parameters.getOrDefault(PLOT_SIZE, Double.toString(DEFAULT_PLOT_SIZE)));
        plotVisibiltiy = Boolean.parseBoolean(parameters.getOrDefault(PLOT_VISIBILITY, Boolean.toString(DEFAULT_PLOT_VISIBILITY)));
        portraitConnectorsVisibiltiy = Boolean.parseBoolean(parameters.getOrDefault(PORTRAIT_CONNECTOR_VISIBILITY, Boolean.toString(DEFAULT_PORTRAIT_CONNECTOR_VISIBILITY)));
        portraitRadius = Double.parseDouble(parameters.getOrDefault(PORTRAIT_RADIUS, Double.toString(DEFAULT_PORTRAIT_RADIUS)));
    }

    private void addPerson(Person person) {
        if (freeMapPersons.containsKey(person)) {
            return;
        }
        var freeMapPerson = FreeMapPerson.createFreeMapPerson(id, person);
        freeMapPerson.addPropertyChangeListener(this::handleFreeMapPersonChanges);
        persons.add(freeMapPerson);
        freeMapPersons.put(person, freeMapPerson);
        propertyChangeSupport.firePropertyChange(FREE_MAP_PERSON_ADDED, this, freeMapPerson);
    }

    private void addFreeMapPerson(FreeMapPerson freeMapPerson) {
        if (freeMapPersons.containsValue(freeMapPerson)) {
            return;
        }
        freeMapPerson.addPropertyChangeListener(this::handleFreeMapPersonChanges);
        persons.add(freeMapPerson);
        freeMapPersons.put(freeMapPerson.getPerson(), freeMapPerson);
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
        var freeMapPlace = places.get(stayPeriod.getPlace());
        if (freeMapPlace == null) {
            LOG.log(Level.SEVERE, "Could not add stayDrawing ({0}) since corresponding freemapPlace ({1}) does not exits.", new Object[]{stayPeriod, stayPeriod.getPlace()});
            return;
        }
        // This should be the responsability of the Frieze to propagate changes on these...
        var startDate = stayPeriod.getStartDate();
        createDateHandle(startDate, FreeMapDateHandle.TimeType.START);
        var endDate = stayPeriod.getEndDate();
        createDateHandle(endDate, FreeMapDateHandle.TimeType.END);
        freeMapPerson.addStay(stayPeriod, freeMapPlace);
    }

    private void addFreeMapStay(FreeMapStay freeMapStay) {
        var person = freeMapStay.getPerson();
        // TODO make sure the person is already added
        var freeMapPerson = freeMapStay.getPerson();
        if (!persons.contains(freeMapPerson)) {
            LOG.log(Level.SEVERE, "Could not add FreeMapStay ({0}) since corresponding freemapPerson ({1}) does not exits.", new Object[]{freeMapStay, person});
            return;
        }
        //
        var freeMapPlace = freeMapStay.getPlace();
        if (!places.containsValue(freeMapPlace)) {
            LOG.log(Level.SEVERE, "Could not add FreeMapStay ({0}) since corresponding freemapPlace ({1}) does not exits.", new Object[]{freeMapStay, freeMapStay.getPlace()});
            return;
        }
        // This should be the responsability of the Frieze to propagate changes on these...
        var startDate = freeMapStay.getStartDate();
        createDateHandle(startDate, FreeMapDateHandle.TimeType.START);
        var endDate = freeMapStay.getEndDate();
        var endDateHandle = endDateHandles.get(endDate);
        if (endDateHandle == null) {
            throw new IllegalStateException("Cannot ");
        }
        //
        System.err.println(" TODO FIX MY CODE");
        //
        freeMapPerson.addFreeMapStay(freeMapStay);
    }

    private void updateStay(StayPeriod stayPeriod) {
        var person = stayPeriod.getPerson();
        var freeMapPerson = freeMapPersons.get(person);
        if (freeMapPerson == null) {
            LOG.log(Level.SEVERE, "Could not updated stayDrawing ({0}) since corresponding freemapPerson ({1}) does not exits.", new Object[]{stayPeriod, person});
            return;
        }
        //
        updateDateHandles(); // SHOULD HAVE BEEN TAKEN CARE BEFORE
        //
        System.err.println("todo confirm");
//        freeMapPerson.updateStay(stayPeriod);
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
        updateDateHandles();
    }

    private void removePerson(Person person) {
        var periods = frieze.getStayPeriods(person);
        var freeMapPerson = freeMapPersons.remove(person);
        persons.remove(freeMapPerson);
        periods.forEach(this::removeStay);
        if (freeMapPerson == null) {
            LOG.log(Level.SEVERE, "Could not remove freemapPerson ({0}) which does not exits.", new Object[]{person});
            return;
        }
        places.forEach((place, freemapPlace) -> freemapPlace.removePerson(person));
        //
        propertyChangeSupport.firePropertyChange(FREE_MAP_PERSON_REMOVED, this, freeMapPerson);
//        var removedPortrait = portraits.remove(person);
//        if (removedPortrait != null) {
//            propertyChangeSupport.firePropertyChange(FREE_MAP_PORTRAIT_REMOVED, this, removedPortrait);
//        }
    }

    private void addPlace(Place aPlace) {
        if (places.containsKey(aPlace)) {
            return;
        }
        var freeMapPlace = FreeMapPlace.createFreeMapPlace(id, aPlace, plotSeparation, placeNamesWidth, fontSize);
        freeMapPlace.setPlaceStaysWidth(getPlaceDrawingWidth());
        places.put(aPlace, freeMapPlace);
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLACE_ADDED, null, freeMapPlace);
    }

    private void addFreeMapPlace(FreeMapPlace freeMapPlace) {
        if (places.containsValue(freeMapPlace)) {
            return;
        }
//        var freeMapPlace = FreeMapPlace.createFreeMapPlace(id, aPlace, plotSeparation, placeNamesWidth, fontSize);
        freeMapPlace.setPlaceStaysWidth(getPlaceDrawingWidth());
        places.put(freeMapPlace.getPlace(), freeMapPlace);
        propertyChangeSupport.firePropertyChange(FREE_MAP_PLACE_ADDED, null, freeMapPlace);
    }

    private FreeMapDateHandle createDateHandle(double date, FreeMapDateHandle.TimeType type) {
        var calculatedX = getPlaceDrawingWidth() * (date - minDate) / (maxDate - minDate);
        var position = new Point2D(calculatedX, DEFAULT_TIME_HEIGHT / 2.0);
        switch (type) {
            case START -> {
                if (!startDateHandles.containsKey(date)) {
                    var startDateHandle = FreeMapDateHandle.createFreeMapDateHandle(id, date, type, position);
                    startDateHandles.put(date, startDateHandle);
                    propertyChangeSupport.firePropertyChange(START_DATE_HANDLE_ADDED, this, startDateHandle);
                    return startDateHandle;
                }
            }
            case END -> {
                if (!endDateHandles.containsKey(date)) {
                    var endDateHandle = FreeMapDateHandle.createFreeMapDateHandle(id, date, type, position);
                    endDateHandles.put(date, endDateHandle);
                    propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_ADDED, this, endDateHandle);
                    return endDateHandle;
                }
            }
            default ->
                throw new UnsupportedOperationException("Unknown TimeType: " + type);
        }
        return null;
    }

    private void removeFreeMapPlace(Place aPlace) {
        var freeMapPlace = places.remove(aPlace);
        if (freeMapPlace != null) {
            propertyChangeSupport.firePropertyChange(FREE_MAP_PLACE_REMOVED, null, freeMapPlace);
        }
    }

    private void updateLayout() {
        var newDimension = new Dimension2D(width, height);
        // 3 times DEFAULT_PLACE_PADDING = (2 on the left, 1 on the right)
        placeDrawingWidth = width - portraitWidth - placeNamesWidth - 3 * MAP_PADDING;
        if (placeDrawingWidth < 0) {
            throw new IllegalStateException("placeDrawingWidth cannot be negative");
        }
        propertyChangeSupport.firePropertyChange(LAYOUT_CHANGED, this, newDimension);
        places.values().forEach(p -> p.setPlaceStaysWidth(getPlaceDrawingWidth()));
        //
        displayTimeAsProportional();
    }

    private void updateDatePositions() {
        var oldMinDate = minDate;
        var oldMaxDate = maxDate;
        minDate = frieze.getMinDate();
        maxDate = frieze.getMaxDate();
        //
        double windowChangedRadio = (maxDate - minDate) / (oldMaxDate - oldMinDate);
        //
        startDateHandles.forEach((date, handle) -> {
            var oldX = handle.getXPos();
            var newX = oldX / windowChangedRadio;
            handle.setX(newX);
        });
        endDateHandles.forEach((date, handle) -> {
            var oldX = handle.getXPos();
            var newX = oldX / windowChangedRadio;
            handle.setX(newX);
        });
    }

    private void updateDateHandles() {
        List<Double> startDates = frieze.getStartDates();
        List<Double> startDatesToBeRemoved = new LinkedList<>();
        startDates.forEach(startDate -> {
            if (!startDateHandles.containsKey(startDate)) {
                var startDateHandle = createDateHandle(startDate, FreeMapDateHandle.TimeType.START);
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
        List<Double> endDates = frieze.getEndDates();
        List<Double> endDatesToBeRemoved = new LinkedList<>();
        frieze.getEndDates().forEach(endDate -> {
            if (!endDateHandles.containsKey(endDate)) {
                var endDateHandle = createDateHandle(endDate, FreeMapDateHandle.TimeType.END);
                propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_ADDED, this, endDateHandle);
            }
        });
        endDateHandles.forEach((endDate, handle) -> {
            if (!endDates.contains(endDate)) {
                endDatesToBeRemoved.add(endDate);
            }
        });
        LOG.log(Level.FINE, "EndDatesToRemove in {0}: {1}", new Object[]{this, endDatesToBeRemoved});
        endDatesToBeRemoved.forEach(endDateToRemove -> {
            var endDateHandle = endDateHandles.remove(endDateToRemove);
            propertyChangeSupport.firePropertyChange(END_DATE_HANDLE_REMOVED, this, endDateHandle);
        });
    }

    private void handleFriezeChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.DATE_WINDOW_CHANGED ->
                updateDatePositions();
            case Frieze.PERSON_ADDED -> {
                var personAdded = (Person) event.getNewValue();
                addPerson(personAdded);
            }
            case Frieze.PERSON_REMOVED -> {
                var personRemoved = (Person) event.getNewValue();
                removePerson(personRemoved);
            }
            case Frieze.PLACE_ADDED -> {
                var placeAdded = (Place) event.getNewValue();
                addPlace(placeAdded);
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
            case Frieze.STAY_UPDATED -> {
                var stayPeriodUpdated = (StayPeriod) event.getNewValue();
                updateStay(stayPeriodUpdated);
            }
            case Frieze.START_DATE_ADDED -> {
                updateDateHandles();
            }
            case Frieze.START_DATE_REMOVED -> {
                updateDateHandles();
            }
            case Frieze.END_DATE_ADDED -> {
                updateDateHandles();
            }
            case Frieze.END_DATE_REMOVED -> {
                updateDateHandles();
            }
            case Frieze.NAME_CHANGED -> {
                // Nothing to do
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event.getPropertyName());
        }
    }

    private void handleFreeMapPersonChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPerson.FREEMAP_STAY_ADDED -> {
                var aFreeMapStayToAdd = (FreeMapStay) event.getNewValue();
                var startPlotToAdd = aFreeMapStayToAdd.getStartPlot();
                var endPlotToAdd = aFreeMapStayToAdd.getEndPlot();
                getStartDateHandle(startPlotToAdd.getDate()).addPlot(startPlotToAdd);
                getEndDateHandle(endPlotToAdd.getDate()).addPlot(endPlotToAdd);
            }
            case FreeMapPerson.FREEMAP_STAY_REMOVED -> {
                var aFreeMapStayToRemove = (FreeMapStay) event.getNewValue();
                var startPlotToRemove = aFreeMapStayToRemove.getStartPlot();
                var endPlotToRemove = aFreeMapStayToRemove.getEndPlot();
                getStartDateHandle(startPlotToRemove.getDate()).removePlot(startPlotToRemove);
                getEndDateHandle(endPlotToRemove.getDate()).removePlot(endPlotToRemove);
            }
            default -> {
                // nothing
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "_" + getName();
    }

    public static double getPlotSeparationParamerter(Map<String, String> parameters) {
        return Double.parseDouble(parameters.getOrDefault(PLOT_SEPARATION, Double.toString(DEFAULT_PLOT_SEPARATION)));

    }

}
