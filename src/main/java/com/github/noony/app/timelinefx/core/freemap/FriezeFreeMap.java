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
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public final class FriezeFreeMap {

    public static final String LAYOUT_CHANGED = "layoutChanged";
    public static final String NAME_CHANGED = "nameChanged";

    private static final double DEFAULT_WIDTH = 1600;
    private static final double DEFAULT_HEIGHT = 1200;
    private static final double DEFAULT_PERSONS_WIDTH = 220;
    private static final double DEFAULT_PLACE_NAMES_WIDTH = 100;

    private static final double DEFAULT_TIME_HEIGHT = 25;
    private static final double DEFAULT_TIME_PADDING = 8;
    private static final double DEFAULT_PORTRAIT_WIDTH_RATIO = 0.9;
    private static final double DEFAULT_FONT_SIZE = 12;
    private static final double DEFAULT_PLOT_SEPARATION = 8.0;
    private static final boolean DEFAULT_PLOT_VISIBILITY = true;
    private static final double DEFAULT_PLOT_SIZE = 4;

    private static final double DEFAULT_PADDING = 15;

    private static final String DEFAULT_NAME = "FreeMap";

    private final PropertyChangeSupport propertyChangeSupport;

    private final Frieze frieze;
    private final List<Person> persons;
    private final List<Plot> plots;
    private final List<Link> stayLinks;
    private final List<Link> travelLinks;
    private final Map<Place, FreeMapPlace> places;
    private final Map<Person, Portrait> portraits;
    private final List<Long> dates;
    private final List<Long> startDates;
    private final List<Long> endDates;
    private final Map<Long, List<Plot>> plotsByDate;
    private final List<PersonInitLink> personInitLinks;
    private final Map<Long, DateHandle> startDateHandles;
    private final Map<Long, DateHandle> endDateHandles;
    //
    private String name;
    //
    private double width;
    private double height;
    //
    private double personsWidth;
    private double padding;
    private double placeDrawingWidth;
    private double placeNamesWidth;
    @Deprecated
    private double portraitRadius;
    private double fontSize;
    private double plotSeparation;
    private boolean plotVisibiltiy;
    private double plotSize;
    //
    private TimeMode timeMode;

    public FriezeFreeMap(Frieze aFrieze, Dimension2D aFriezeDimension, double aPersonWidth, double aPlaceNameWidth, double aFontSize, double aPlotSeparation, boolean aPlotVisibilty, double aPlotSize) {
        propertyChangeSupport = new PropertyChangeSupport(FriezeFreeMap.this);
        frieze = aFrieze;
        persons = frieze.getPersons();
        plots = new LinkedList<>();
        personInitLinks = new LinkedList<>();
        stayLinks = new LinkedList<>();
        travelLinks = new LinkedList<>();
        places = new HashMap<>();
        plotsByDate = new HashMap<>();
        portraits = new HashMap<>();
        startDateHandles = new HashMap<>();
        endDateHandles = new HashMap<>();
        //
        name = DEFAULT_NAME;
        width = aFriezeDimension.getWidth();
        height = aFriezeDimension.getHeight();
        padding = DEFAULT_PADDING;
        // TODO set in method
        personsWidth = aPersonWidth;
        placeNamesWidth = aPlaceNameWidth;
        fontSize = aFontSize;
        plotSeparation = aPlotSeparation;
        plotVisibiltiy = aPlotVisibilty;
        plotSize = aPlotSize;
        // TODO use a map for all relevant attributes
        //
        frieze.getPlaces().forEach(this::createFreeMapPlace);
        persons.forEach(this::createPerson);
        persons.forEach(this::createPortrait);
        linkPlacesAndPlots();
        // to be created after plot creation
        persons.forEach(this::createPersonInitLink);
        //
        dates = plotsByDate.keySet().stream().distinct().sorted().collect(Collectors.toList());
        startDates = plots.stream().filter(p -> p.getType().equals(PlotType.START)).map(Plot::getDate).distinct().sorted().collect(Collectors.toList());
        endDates = plots.stream().filter(p -> p.getType().equals(PlotType.END)).map(Plot::getDate).distinct().sorted().collect(Collectors.toList());
        //
        updateLayout();
        //
        createStartDateHandles();
        createEndDateHandles();
        addPlotsToHandles();
        //
        distributePlaces();
        distributePortraits();
        displayTimeAsProportional();
    }

    public FriezeFreeMap(Frieze aFrieze) {
        this(aFrieze, new Dimension2D(DEFAULT_WIDTH, DEFAULT_HEIGHT), DEFAULT_PERSONS_WIDTH, DEFAULT_PLACE_NAMES_WIDTH, DEFAULT_FONT_SIZE, DEFAULT_PLOT_SEPARATION, DEFAULT_PLOT_VISIBILITY, DEFAULT_PLOT_SIZE);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
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
        return personsWidth + 2 * padding;
    }

    public double getPlaceDrawingY() {
        return padding;
    }

    public double getPersonsDrawingX() {
        return padding;
    }

    public double getPersonsDrawingY() {
        return padding;
    }

    public double getPersonWidth() {
        return personsWidth;
    }

    public double getPersonHeight() {
        return height - padding * 2;
    }

    public double getPlaceDrawingWidth() {
        return placeDrawingWidth;
    }

    public double getPlaceDrawingHeight() {
        return height - padding * 2;
    }

    public double getPlaceNamesWidth() {
        return placeNamesWidth;
    }

    public double getPadding() {
        return padding;
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

    public List<Long> getDates() {
        // unnecessary unomodif ?
        return Collections.unmodifiableList(dates.stream().sorted().collect(Collectors.toList()));
    }

    public List<DateHandle> getStartDateHandles() {
        return Collections.unmodifiableList(startDateHandles.values().stream().collect(Collectors.toList()));
    }

    public List<DateHandle> getEndDateHandles() {
        return Collections.unmodifiableList(endDateHandles.values().stream().collect(Collectors.toList()));
    }

    public List<Link> getStayLinks() {
        return Collections.unmodifiableList(stayLinks);
    }

    public List<Link> getTravelLinks() {
        return Collections.unmodifiableList(travelLinks);
    }

    public List<Plot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    public List<PersonInitLink> getPersonInitLinks() {
        return Collections.unmodifiableList(personInitLinks);
    }

    public List<FreeMapPlace> getPlaces() {
        return Collections.unmodifiableList(places.values().stream().collect(Collectors.toList()));
    }

    public List<Portrait> getPortraits() {
        return Collections.unmodifiableList(portraits.values().stream().collect(Collectors.toList()));
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

    public int getNbDates() {
        return dates.size();
    }

    public FreeMapPlace getFreeMapPlace(long placeID) {
        return places.values().stream().filter(p -> p.getPlace().getId() == placeID).findAny().orElse(null);
    }

    public Plot getFreeMapPlace(long stayId, PlotType type) {
        return plots.stream().filter(p -> p.getType().equals(type) & p.getParentPeriodID() == stayId).findAny().orElse(null);
    }

    public Portrait getPortrait(long portraitID) {
        return portraits.values().stream().filter(p -> p.getPerson().getId() == portraitID).findAny().orElse(null);
    }

    public Plot getPlot(long stayId, PlotType type) {
        return plots.stream().filter(p -> p.getParentPeriodID() == stayId & p.getType().equals(type)).findAny().orElse(null);
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
        plots.forEach(plot -> plot.setVisible(plotVisibiltiy));
    }

    public void setPlotSize(double newPlotSize) {
        plotSize = newPlotSize;
        plots.forEach(plot -> plot.setPlotSize(plotSize));
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
        var minDate = dates.stream().mapToLong(l -> l).min().orElse(0);
        var maxDate = dates.stream().mapToLong(l -> l).max().orElse(1);
        var availableWidth = getPlaceDrawingWidth() - 2 * DEFAULT_TIME_PADDING;
        var ratio = (maxDate - minDate) / availableWidth;
        startDateHandles.forEach((d, h) -> h.setX(DEFAULT_TIME_PADDING + (d - minDate) / ratio));
        endDateHandles.forEach((d, h) -> h.setX(DEFAULT_TIME_PADDING + (d - minDate) / ratio));
    }

    public void displayTimeAsEqualSplit() {
        throw new UnsupportedOperationException("displayTimeAsEqualSplit :: TODO");
    }

    public void setPortraitRadius(double newPortraitRadius) {
        portraitRadius = newPortraitRadius;
        portraits.values().forEach(portrait -> portrait.setRadius(newPortraitRadius));
    }

    private void createPerson(Person person) {
        var periods = frieze.getStayPeriods(person);
        Map<Long, Plot> startPlots = new HashMap<>();
        Map<Long, Plot> endPlots = new HashMap<>();
        var y = 10 * persons.indexOf(person);
        periods.stream().forEachOrdered(s -> {
            var startPlot = new StartPlot(s, plotSize);
            startPlots.put(startPlot.getDate(), startPlot);
            var endPlot = new EndPlot(s, plotSize);
            endPlots.put(endPlot.getDate(), endPlot);
            var link = new StayLink(s, startPlot, endPlot);
            stayLinks.add(link);
            plots.add(startPlot);
            plots.add(endPlot);
            //
            if (!plotsByDate.containsKey(startPlot.getDate())) {
                plotsByDate.put(startPlot.getDate(), new LinkedList<>());
            }
            plotsByDate.get(startPlot.getDate()).add(startPlot);
            //
            if (!plotsByDate.containsKey(endPlot.getDate())) {
                plotsByDate.put(endPlot.getDate(), new LinkedList<>());
            }
            plotsByDate.get(endPlot.getDate()).add(endPlot);
            //
        });
        var plotList = plots.stream().collect(Collectors.toList());
        for (int i = 0; i < plotList.size(); i++) {
            var plot = plotList.get(i);
            plot.setPosition(i * 10, y);
        }
        //
        for (int i = 0; i < periods.size() - 1; i++) {
            var previousEndDate = periods.get(i).getEndDate();
            var nextStartDate = periods.get(i + 1).getStartDate();
            var previousPlot = endPlots.get(previousEndDate);
            var nextPlort = startPlots.get(nextStartDate);
            var link = new TravelLink(person, previousPlot, nextPlort);
            travelLinks.add(link);
        }
    }

    private void createPortrait(Person person) {
        var portrait = new Portrait(person, portraitRadius);
        portraits.put(person, portrait);
    }

    private void createPersonInitLink(Person person) {
        var initLink = new PersonInitLink(person, this);
        personInitLinks.add(initLink);
    }

    private void createFreeMapPlace(Place aPlace) {
        var freeMapPlace = new FreeMapPlace(aPlace, plotSeparation, placeNamesWidth, fontSize);
        freeMapPlace.setWidth(getPlaceDrawingWidth());
        places.put(aPlace, freeMapPlace);
    }

    private void linkPlacesAndPlots() {
        plots.forEach(plot -> {
            var freeMapPlace = places.get(plot.getPlace());
            if (freeMapPlace != null) {
                freeMapPlace.addPlot(plot);
            } else {
                System.err.println("Could not find place " + plot.getPlace());
            }
        });
    }

    private void createStartDateHandles() {
        int nbDates = dates.size();
        double separation = (getPlaceDrawingWidth()) / (1 + 2 * nbDates);
        startDates.forEach(date -> {
            int index = dates.indexOf(date);
            var position = new Point2D((index * 2 + 1) * separation, DEFAULT_TIME_HEIGHT / 2.0);
            var handle = new DateHandle(date, DateHandle.TimeType.START, position);
            startDateHandles.put(date, handle);
        });
    }

    private void createEndDateHandles() {
        int nbDates = dates.size();
        double separation = (getPlaceDrawingWidth()) / (1 + 2 * nbDates);
        endDates.forEach(date -> {
            int index = dates.indexOf(date);
            var position = new Point2D((index * 2 + 1) * separation, DEFAULT_TIME_HEIGHT / 2.0);
            var handle = new DateHandle(date, DateHandle.TimeType.END, position);
            endDateHandles.put(date, handle);
        });
    }

    private void addPlotsToHandles() {
        plots.forEach(plot -> {
            // add plot to dataHandle
            switch (plot.getType()) {
                case END:
                    endDateHandles.get(plot.getDate()).addPlot(plot);
                    break;
                case START:
                    startDateHandles.get(plot.getDate()).addPlot(plot);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        });
    }

    private void updateLayout() {
        var newDimension = new Dimension2D(width, height);
        placeDrawingWidth = width - 4 * padding - personsWidth - placeNamesWidth;
        propertyChangeSupport.firePropertyChange(LAYOUT_CHANGED, this, newDimension);
        places.values().forEach(p -> p.setWidth(getPlaceDrawingWidth()));
        //
        displayTimeAsProportional();
    }

}
