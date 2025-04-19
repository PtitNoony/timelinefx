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

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapTravelLink;
import com.github.noony.app.timelinefx.core.freemap.links.PortraitLink;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hamon
 */
public class FreeMapPerson implements FriezeObject {

    public static final String FREEMAP_STAY_ADDED = "stayAdded";
    public static final String FREEMAP_STAY_REMOVED = "stayRemoved";
    public static final String FIRST_PLOT_CHANGED = "firstPlotChanged";
    public static final String TRAVEL_LINK_ADDED = "travelLinkAdded";
    public static final String TRAVEL_LINK_REMOVED = "travelLinkRemoved";
    public static final String PORTRAIT_ADDED = "portraitAdded";
    public static final String PORTRAIT_REMOVED = "portraitRemoved";
    public static final String PORTRAIT_LINK_ADDED = "portraitLinkAdded";
    public static final String PORTRAIT_LINK_REMOVED = "portraitLinkRemoved";

    //
    private static final Map<Long, List<FreeMapPerson>> FACTORY_CONTENT = new HashMap<>();

    public static final FreeMapPerson createFreeMapPerson(long aFriezeFreeMapID, Person aPerson) {
        var freeMapPersons = FACTORY_CONTENT.getOrDefault(aFriezeFreeMapID, new LinkedList<>());
        if (freeMapPersons.isEmpty()) {
            FACTORY_CONTENT.put(aFriezeFreeMapID, freeMapPersons);
        }
        if (freeMapPersons.stream().anyMatch(fp -> fp.getPerson() == aPerson)) {
            throw new IllegalStateException("Cannot create a FreeMapPerson twice. (" + aPerson.getName() + " in " + FriezeFreeMapFactory.getFriezeFreeMap(aFriezeFreeMapID).getName() + ")");
        }
        var freeMapPerson = new FreeMapPerson(aPerson);
        freeMapPersons.add(freeMapPerson);
        return freeMapPerson;
    }

    public static final void resetFactory() {
        FACTORY_CONTENT.clear();
    }

    //
    // paramters to be saved
    //
    private final long id;
    //
    private final List<FreeMapStay> stays;
    private final List<FreeMapPortrait> freeMapPortraits;
    //
    // to be saved in next version
    //
    private final List<FreeMapTravelLink> travelLinks;
    private final Map<FreeMapPortrait, PortraitLink> portraitLinks;
    //
    // other instance parameters, usually calculated
    //
    // no need to save since ids match by construction
    private final Person person;
    //
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * The FreeMapPerson instance has the same id as is related person
     *
     * @param aPerson the related person
     */
    private FreeMapPerson(Person aPerson) {
        person = aPerson;
        id = aPerson.getId();
        //
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPerson.this);
        //
        stays = new LinkedList<>();
        travelLinks = new LinkedList<>();
        freeMapPortraits = new LinkedList<>();
        portraitLinks = new HashMap<>();
    }

    @Override
    public long getId() {
        return id;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Person getPerson() {
        return person;
    }

    public String getName() {
        return person.getName();
    }

    public List<FreeMapStay> getFreeMapStays() {
        return Collections.unmodifiableList(stays);
    }

    public List<FreeMapTravelLink> getFreeMapTravelLinks() {
        return Collections.unmodifiableList(travelLinks);
    }

    public FreeMapPortrait createPortrait(FreeMapLink sourceLink) {
        return createPortrait(sourceLink, IFileObject.NO_ID, IFileObject.NO_ID, IFileObject.NO_ID);
    }

    public FreeMapPortrait createPortrait(FreeMapLink stayLink, long portraitRefID, long connectorID, long linkID) {
        var xPosition = (stayLink.getEndConnector().getX() - stayLink.getBeginConnector().getX()) / 2.0 + stayLink.getBeginConnector().getX();
        var yPosition = (stayLink.getEndConnector().getY() - stayLink.getBeginConnector().getY()) / 2.0 + stayLink.getBeginConnector().getY();
        var freeMapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(portraitRefID, person.getDefaultPortrait(), this, FriezeFreeMap.DEFAULT_PORTRAIT_RADIUS);
        freeMapPortrait.setX(xPosition);
        freeMapPortrait.setY(yPosition);
        var connectorTempDate = (stayLink.getEndConnector().getDate() - stayLink.getBeginConnector().getDate()) / 2.0;
        // TODO: also use cursor position
        var stayLinkConnector = FreeMapConnectorFactory.createFreeMapLinkConnector(connectorID, stayLink, connectorTempDate, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        // todo create anchor
        var portraitLink = FreeMapLinkFactory.createPortraitLink(linkID, freeMapPortrait, stayLinkConnector);
        freeMapPortraits.add(freeMapPortrait);
        portraitLinks.put(freeMapPortrait, portraitLink);
        propertyChangeSupport.firePropertyChange(PORTRAIT_ADDED, this, freeMapPortrait);
        propertyChangeSupport.firePropertyChange(PORTRAIT_LINK_ADDED, this, portraitLink);
        return freeMapPortrait;
    }

    public boolean addFreeMapPortrait(FreeMapPortrait aFreeMapPortrait, PortraitLink aPortraitLink) {
        if (freeMapPortraits.contains(aFreeMapPortrait)) {
            return false;
        }
        freeMapPortraits.add(aFreeMapPortrait);
        portraitLinks.put(aFreeMapPortrait, aPortraitLink);
        propertyChangeSupport.firePropertyChange(PORTRAIT_ADDED, this, aFreeMapPortrait);
        propertyChangeSupport.firePropertyChange(PORTRAIT_LINK_ADDED, this, aPortraitLink);
        return true;
    }

    public List<FreeMapPortrait> getFreeMapPortraits() {
        return Collections.unmodifiableList(freeMapPortraits);
    }

    public PortraitLink getPortraitLink(FreeMapPortrait aFreeMapPortrait) {
        return portraitLinks.get(aFreeMapPortrait);
    }

    protected void addStay(StayPeriod stayPeriod, FreeMapPlace aFreeMapPlace) {
        addStay(stayPeriod, aFreeMapPlace, IFileObject.NO_ID, IFileObject.NO_ID);
    }

    protected void addStay(StayPeriod stayPeriod, FreeMapPlace aFreeMapPlace, long aStartID, long anEndID) {
        for (FreeMapStay freeMapStay : stays) {
            if (freeMapStay.containsStay(stayPeriod)) {
                return;
            }
        }
        var freeMapStay = FreeMapStayFactory.createFreeMapStay(stayPeriod, aStartID, anEndID, this, aFreeMapPlace);
        addFreeMapStay(freeMapStay);
        //
    }

    protected void addFreeMapStay(FreeMapStay aFreeMapStay) {
        if (stays.contains(aFreeMapStay)) {
            return;
        }
        stays.add(aFreeMapStay);
        stays.sort(FreeMapSimpleStay.STAY_START_COMPARATOR);
        // TODO: temp remove unnecessary maps/lists
        var startPlot = aFreeMapStay.getStartPlot();
        var endPlot = aFreeMapStay.getEndPlot();
        //
        var freeMapPlace = aFreeMapStay.getPlace();
        freeMapPlace.registerFreeMapPlot(startPlot);
        freeMapPlace.registerFreeMapPlot(endPlot);
        //
        propertyChangeSupport.firePropertyChange(FREEMAP_STAY_ADDED, this, aFreeMapStay);
        //
        recalculateTravelLinks();
    }

    protected void removeStay(StayPeriod stayPeriod) {
        var optStayToRemove = stays.stream().filter(s -> s.containsStay(stayPeriod)).findAny();
        if (optStayToRemove.isEmpty()) {
            return;
        }
        var stayToRemove = optStayToRemove.get();
        stays.remove(stayToRemove);
        stays.sort(FreeMapSimpleStay.STAY_START_COMPARATOR);
        propertyChangeSupport.firePropertyChange(FREEMAP_STAY_REMOVED, this, stayPeriod);
        //
        // TODO remove ?
//        propertyChangeSupport.firePropertyChange(LINK_REMOVED, this, removedLink);
        //
//        System.err.println("TODO: notify stay removed");
//        updateFirstPlot();
        recalculateTravelLinks();
    }

    protected void setPlotsVisibilty(boolean visibility) {
        stays.forEach(s -> s.setPlotVisibility(visibility));
    }

    protected void setPortraitConnectorsVisibilty(boolean visibility) {
        portraitLinks.values().forEach(pLinks -> pLinks.setConnectorsVisibility(visibility));
    }

    private void recalculateTravelLinks() {
        List<FreeMapTravelLink> linksToBeRemoved = new LinkedList<>();
        List<FreeMapTravelLink> linksToBeAdded = new LinkedList<>();
        var nbStays = stays.size();
        // stays list is already sorted after the new stay has been added / removed
        if (nbStays <= 1) {
            linksToBeRemoved.addAll(travelLinks);
        }
        for (int i = 0; i < nbStays - 1; i++) {
            var previousStay = stays.get(i);
            var nextStay = stays.get(i + 1);
            var previousPlot = previousStay.getEndConnector();
            var nextPlot = nextStay.getBeginConnector();
            if (findLink(previousPlot, nextPlot) == null) {
                final var travelLink = FreeMapLinkFactory.createTravelLink(IFileObject.NO_ID, this, previousPlot, nextPlot);
                linksToBeAdded.add(travelLink);
                travelLinks.forEach(existingTravelLink -> {
                    if (existingTravelLink.getEndConnector().getDate() == nextPlot.getDate() && travelLink.getBeginConnector().getDate() != previousPlot.getDate()) {
                        linksToBeRemoved.add(existingTravelLink);
                    } else if (existingTravelLink.getBeginConnector().getDate() == previousPlot.getDate() && existingTravelLink.getEndConnector().getDate() != nextPlot.getDate()) {
                        linksToBeRemoved.add(existingTravelLink);
                    }
                });
            }
        }
        for (var trL : travelLinks) {
            var needed = false;
            for (int i = 0; i < nbStays - 1; i++) {
                var previousStay = stays.get(i);
                var nextStay = stays.get(i + 1);
                var previousPlot = previousStay.getEndConnector();
                var nextPlot = nextStay.getBeginConnector();
                if (trL.getEndConnector().getDate() == nextPlot.getDate() && trL.getBeginConnector().getDate() == previousPlot.getDate()) {
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

    private FreeMapLink findLink(FreeMapConnector previousPlot, FreeMapConnector nextPlot) {
        for (var travelLink : travelLinks) {
            if (travelLink.getEndConnector().getDate() == nextPlot.getDate() && travelLink.getBeginConnector().getDate() == previousPlot.getDate()) {
                return travelLink;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "" + getClass().getSimpleName() + "_" + id + "_refTo_" + person.getId();
    }

}
