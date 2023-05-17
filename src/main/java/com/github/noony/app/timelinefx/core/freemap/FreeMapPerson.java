/*
 * Copyright (C) 2021 NoOnY
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

import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.StayPeriod;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author hamon
 */
public class FreeMapPerson {

    public static final String STAY_ADDED = "stayAdded";
    public static final String STAY_REMOVED = "stayRemoved";
    public static final String LINK_ADDED = "linkAdded";
    public static final String LINK_REMOVED = "linkRemoved";
    public static final String PLOTS_ADDED = "plotsAdded";
    public static final String PLOTS_REMOVED = "plotsRemoved";
    public static final String FIRST_PLOT_CHANGED = "firstPlotChanged";
    public static final String TRAVEL_LINK_ADDED = "travelLinkAdded";
    public static final String TRAVEL_LINK_REMOVED = "travelLinkRemoved";
    public static final String PORTRAIT_ADDED = "portraitAdded";
    public static final String PORTRAIT_REMOVED = "portraitRemoved";
    public static final String PORTRAIT_LINK_ADDED = "portraitLinkAdded";
    public static final String PORTRAIT_LINK_REMOVED = "portraitLinkRemoved";

    private final Person person;
    private final FriezeFreeMap freeMap;
    //
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final List<StayPeriod> stays;
    private final Map<StayPeriod, FreeMapLink> stayLinks;
    private final List<Plot> plots;
    private final Map<StayPeriod, Pair<Plot, Plot>> plotsByPeriod;
    private final List<TravelLink> travelLinks;
    //
    private final List<FreeMapPortrait> portraits;
    private final List<PortraitLink> portraitLinks;
    //
    private Plot firstPlot = null;

    public FreeMapPerson(Person aPerson, FriezeFreeMap aFreemap) {
        person = aPerson;
        freeMap = aFreemap;
        //
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPerson.this);
        //
        stays = new LinkedList<>();
        plots = new LinkedList<>();
        stayLinks = new HashMap<>();
        plotsByPeriod = new HashMap<>();
        travelLinks = new LinkedList<>();
        portraits = new LinkedList<>();
        portraitLinks = new LinkedList<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Person getPerson() {
        return person;
    }

    public List<Plot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    public List<FreeMapLink> getStayLinks() {
        return Collections.unmodifiableList(stayLinks.values().stream().collect(Collectors.toList()));
    }

    public List<FreeMapLink> getTravelLinks() {
        return Collections.unmodifiableList(travelLinks);
    }

    public Plot getFirstPlot() {
        return firstPlot;
    }

    public Pair<Plot, Plot> getPlots(StayPeriod stayPeriod) {
        return plotsByPeriod.get(stayPeriod);
    }

    public void createPortrait(FreeMapLink sourceLink) {
        createPortrait(sourceLink, IFileObject.NO_ID, IFileObject.NO_ID, IFileObject.NO_ID);
    }

    public void createPortrait(FreeMapLink sourceLink, long portraitID, long connectorID, long linkID) {
        var xPosition = (sourceLink.getEndPlot().getX() - sourceLink.getBeginPlot().getX()) / 2.0 + sourceLink.getBeginPlot().getX();
        var yPosition = (sourceLink.getEndPlot().getY() - sourceLink.getBeginPlot().getY()) / 2.0 + sourceLink.getBeginPlot().getY();
        FreeMapPortrait freeMapPortrait = new FreeMapPortrait(portraitID != IFileObject.NO_ID ? portraitID : FriezeObjectFactory.getNextID(), person.getDefaultPortrait(), freeMap.getPortraitRadius());
        freeMapPortrait.setX(xPosition);
        freeMapPortrait.setY(yPosition);
        // TODO: also use cursor position
        var linkConnector = sourceLink.createConnector(connectorID != IFileObject.NO_ID ? connectorID : FriezeObjectFactory.getNextID());
        // todo create anchor
        var portraitLink = new PortraitLink(linkID, freeMapPortrait, linkConnector);
        portraits.add(freeMapPortrait);
        portraitLinks.add(portraitLink);
        propertyChangeSupport.firePropertyChange(PORTRAIT_ADDED, this, freeMapPortrait);
        propertyChangeSupport.firePropertyChange(PORTRAIT_LINK_ADDED, this, portraitLink);
        var startEndPlots = new Pair(portraitLink.getBeginPlot(), portraitLink.getEndPlot());
        propertyChangeSupport.firePropertyChange(PLOTS_ADDED, this, startEndPlots);
    }

    public List<FreeMapPortrait> getPortraits() {
        return Collections.unmodifiableList(portraits);
    }

    protected void addStay(StayPeriod stayPeriod) {
        if (stays.contains(stayPeriod)) {
            return;
        }
        stays.add(stayPeriod);
        stays.sort(StayPeriod.STAY_COMPARATOR);
        var startPlot = new StartPlot(stayPeriod, freeMap.getPlotSize());
        var endPlot = new EndPlot(stayPeriod, freeMap.getPlotSize());
        var link = new StayLink(stayPeriod, startPlot, endPlot);
        propertyChangeSupport.firePropertyChange(LINK_ADDED, this, link);
        stayLinks.put(stayPeriod, link);
        plots.add(startPlot);
        plots.add(endPlot);
        //
        var startEndPlots = new Pair(startPlot, endPlot);
        plotsByPeriod.put(stayPeriod, startEndPlots);
        propertyChangeSupport.firePropertyChange(PLOTS_ADDED, this, startEndPlots);
        //
        freeMap.getStartDateHandle(startPlot.getDate()).addPlot(startPlot);
        freeMap.getEndDateHandle(endPlot.getDate()).addPlot(endPlot);
        var freeMapPlace = freeMap.getFreeMapPlace(stayPeriod.getPlace());
        freeMapPlace.addPlot(startPlot);
        freeMapPlace.addPlot(endPlot);
        //
        propertyChangeSupport.firePropertyChange(STAY_ADDED, this, stayPeriod);
        //
        updateFirstPlot();
        recalculateTravelLinks();
        //
    }

    protected void removeStay(StayPeriod stayPeriod) {
        if (!stays.contains(stayPeriod)) {
            return;
        }
        stays.remove(stayPeriod);
        stays.sort(StayPeriod.STAY_COMPARATOR);
        propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, stayPeriod);
        //
        var removedLink = stayLinks.get(stayPeriod);
        // TODO remove ?
        propertyChangeSupport.firePropertyChange(LINK_REMOVED, this, removedLink);
        //
        var startEndPlots = plotsByPeriod.remove(stayPeriod);
        if (startEndPlots != null) {
            plots.remove(startEndPlots.getKey());
            plots.remove(startEndPlots.getValue());
            propertyChangeSupport.firePropertyChange(PLOTS_REMOVED, this, startEndPlots);
        }
        updateFirstPlot();
        recalculateTravelLinks();
    }

    protected void setPlotsVisibilty(boolean visibility) {
        plots.forEach(plot -> plot.setVisible(visibility));
    }

    protected void setPortraitConnectorsVisibilty(boolean visibility) {
        portraitLinks.forEach(pLinks -> pLinks.setConnectorsVisible(visibility));
    }

    protected void updateStay(StayPeriod stayPeriod) {
        var startEndPlots = plotsByPeriod.get(stayPeriod);
        if (startEndPlots != null) {
            var startPlot = startEndPlots.getKey();
            var endPlot = startEndPlots.getValue();
            //
            startPlot.setDate(stayPeriod.getStartDate());
            endPlot.setDate(stayPeriod.getEndDate());
            //
            freeMap.getStartDateHandle(startPlot.getDate()).addPlot(startPlot);
            freeMap.getEndDateHandle(endPlot.getDate()).addPlot(endPlot);
        }
    }

    private void updateFirstPlot() {
        var oldFirstPlot = firstPlot;
        firstPlot = plots.stream().filter(plot -> plot.getPerson().equals(person)).sorted((p1, p2) -> Double.compare(p1.getDate(), p2.getDate())).findFirst().orElse(null);
        if (oldFirstPlot != firstPlot) {
            propertyChangeSupport.firePropertyChange(FIRST_PLOT_CHANGED, this, firstPlot);
        }
    }

    private void recalculateTravelLinks() {
        List<TravelLink> linksToBeRemoved = new LinkedList<>();
        List<TravelLink> linksToBeAdded = new LinkedList<>();
        var nbStays = stays.size();
        // stays list is already sorted after the new stay has been added / removed
        if (nbStays <= 1) {
            linksToBeRemoved.addAll(travelLinks);
        }
        for (int i = 0; i < nbStays - 1; i++) {
            var previousStay = stays.get(i);
            var nextStay = stays.get(i + 1);
            var previousStartEndPlots = plotsByPeriod.get(previousStay);
            var nextStartEndPlots = plotsByPeriod.get(nextStay);
            var previousPlot = previousStartEndPlots.getValue();
            var nextPlot = nextStartEndPlots.getKey();
            if (findLink(previousPlot, nextPlot) == null) {
                final var travelLink = new TravelLink(person, previousPlot, nextPlot);
                linksToBeAdded.add(travelLink);
                travelLinks.forEach(existingTravelLink -> {
                    if (existingTravelLink.getEndPlot().getDate() == nextPlot.getDate() && travelLink.getBeginPlot().getDate() != previousPlot.getDate()) {
                        linksToBeRemoved.add(existingTravelLink);
                    } else if (existingTravelLink.getBeginPlot().getDate() == previousPlot.getDate() && existingTravelLink.getEndPlot().getDate() != nextPlot.getDate()) {
                        linksToBeRemoved.add(existingTravelLink);
                    }
                });
            }
        }
        for (TravelLink trL : travelLinks) {
            boolean needed = false;
            for (int i = 0; i < nbStays - 1; i++) {
                var previousStay = stays.get(i);
                var nextStay = stays.get(i + 1);
                var previousStartEndPlots = plotsByPeriod.get(previousStay);
                var nextStartEndPlots = plotsByPeriod.get(nextStay);
                var previousPlot = previousStartEndPlots.getValue();
                var nextPlot = nextStartEndPlots.getKey();
                if (trL.getEndPlot().getDate() == nextPlot.getDate() && trL.getBeginPlot().getDate() == previousPlot.getDate()) {
                    needed = true;
                    break;
                }
            }
            if (!needed) {
                linksToBeRemoved.add(trL);
            }
        }
        travelLinks.removeAll(linksToBeRemoved);
        travelLinks.addAll(linksToBeAdded);
        linksToBeRemoved.forEach(link -> propertyChangeSupport.firePropertyChange(TRAVEL_LINK_REMOVED, this, link));
        linksToBeAdded.forEach(link -> propertyChangeSupport.firePropertyChange(TRAVEL_LINK_ADDED, this, link));
    }

    private TravelLink findLink(Plot previousPlot, Plot nextPlot) {
        for (TravelLink travelLink : travelLinks) {
            if (travelLink.getEndPlot().getDate() == nextPlot.getDate() && travelLink.getBeginPlot().getDate() == previousPlot.getDate()) {
                return travelLink;
            }
        }
        return null;
    }

}
