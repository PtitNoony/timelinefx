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

import com.github.noony.app.timelinefx.core.Factory;
import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class FriezeFreeMapFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<FriezeFreeMap> FACTORY = new Factory<>();

    private FriezeFreeMapFactory() {
        // private utility constructor
    }

    public static final void reset() {
        FACTORY.reset();
    }

    public static List<FriezeFreeMap> getFriezeFreeMaps() {
        return FACTORY.getObjects();
    }

    public static FriezeFreeMap getFriezeFreeMap(long friezeID) {
        return FACTORY.get(friezeID);
    }

    public static FriezeFreeMap createFriezeFreeMap(Frieze aFrieze, boolean allStays) {
        LOG.log(Level.WARNING, "Creating a friezeFreeMap with Frieze={0} ", new Object[]{aFrieze.getName()});
        var friezeFreeMap = new FriezeFreeMap(FACTORY.getNextID(), aFrieze, allStays);
        FACTORY.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    public static FriezeFreeMap createFriezeFreeMap(long anID, Frieze aFrieze, boolean allStays) {
        if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(Level.WARNING, "Creating a friezeFreeMap (id={0} with Frieze={1}", new Object[]{anID, aFrieze});
        var friezeFreeMap = new FriezeFreeMap(anID, aFrieze, allStays);
        FACTORY.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    /**
     * Creates a {@link FriezeFreeMap} with a fully specified content, as used when restoring one from a
     * saved project.
     *
     * @param anID the id to assign to the new free map
     * @param aFrieze the frieze the free map belongs to
     * @param properties the free map's layout properties
     * @param dateHandles the date handles to restore
     * @param persons the persons to restore
     * @param places the places to restore
     * @param stays the stays to restore
     * @return the created free map
     */
    public static FriezeFreeMap createFriezeFreeMap(final long anID, final Frieze aFrieze, final FriezeFreeMapProperties properties,
            List<FreeMapDateHandle> dateHandles, List<FreeMapPerson> persons, List<FreeMapPlace> places, List<FreeMapStay> stays) {
        //
        if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("Trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(Level.WARNING, "Creating a friezeFreeMap (id={0} with Frieze={1} with its full content.", new Object[]{anID, aFrieze});
        final var friezeFreeMap = new FriezeFreeMap(anID, aFrieze, properties, dateHandles, persons, places, stays, false);
        FACTORY.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    /**
     * Creates a fully independent copy of {@code source}: same layout properties, persons, places, stays and
     * portraits, but built from brand-new objects that share no mutable state with the source (only the
     * underlying {@link Person}/{@link com.github.noony.app.timelinefx.core.Place}/
     * {@link com.github.noony.app.timelinefx.core.StayPeriod}/{@link com.github.noony.app.timelinefx.core.Portrait}
     * domain objects are reused, since duplicating the free map's layout must not duplicate the frieze's actual
     * content).
     *
     * @param source the free map to duplicate
     * @return the newly created copy
     */
    public static FriezeFreeMap duplicateFriezeFreeMap(final FriezeFreeMap source) {
        final var newID = FACTORY.getNextID();
        final var frieze = source.getFrieze();
        final var properties = source.getProperties();
        LOG.log(Level.WARNING, "Duplicating FriezeFreeMap (id={0}) into a new one (id={1}).", new Object[]{source.getId(), newID});
        //
        final var dateHandles = duplicateDateHandles(source, newID);
        //
        final var places = new LinkedList<FreeMapPlace>();
        final Map<Place, FreeMapPlace> placesByPlace = new HashMap<>();
        source.getPlaces().forEach(sourcePlace -> {
            final var newPlace = FreeMapPlace.createFreeMapPlace(newID, sourcePlace.getPlace(),
                    properties.plotSeparation(), properties.placeNameWidth(), properties.fontSize());
            newPlace.setY(sourcePlace.getYPos());
            newPlace.setHeight(sourcePlace.getHeight());
            places.add(newPlace);
            placesByPlace.put(sourcePlace.getPlace(), newPlace);
        });
        //
        final var persons = new LinkedList<FreeMapPerson>();
        final Map<Person, FreeMapPerson> personsByPerson = new HashMap<>();
        final var stays = new LinkedList<FreeMapStay>();
        final Map<Long, FreeMapStay> newStaysBySourceStayId = new HashMap<>();
        source.getPersons().forEach(sourcePerson -> {
            final var newPerson = FreeMapPerson.createFreeMapPerson(newID, sourcePerson.getPerson());
            persons.add(newPerson);
            personsByPerson.put(sourcePerson.getPerson(), newPerson);
            sourcePerson.getFreeMapStays().forEach(sourceStay -> {
                if (sourceStay instanceof FreeMapSimpleStay sourceSimpleStay) {
                    final var newPlace = placesByPlace.get(sourceStay.getPlace().getPlace());
                    final var stayPeriod = sourceSimpleStay.getStayPeriods().get(0);
                    final var newStay = FreeMapStayFactory.createFreeMapStay(stayPeriod, newPerson, newPlace);
                    stays.add(newStay);
                    newStaysBySourceStayId.put(sourceStay.getId(), newStay);
                } else {
                    LOG.log(Level.WARNING, "Skipping merged stay {0} while duplicating FriezeFreeMap: not supported.", new Object[]{sourceStay});
                }
            });
        });
        //
        final var duplicate = createFriezeFreeMap(newID, frieze, properties, dateHandles, persons, places, stays);
        duplicate.setName(source.getName() + " (copy)");
        //
        duplicatePortraits(source, personsByPerson, newStaysBySourceStayId);
        //
        return duplicate;
    }

    private static List<FreeMapDateHandle> duplicateDateHandles(final FriezeFreeMap source, final long newID) {
        final var dateHandles = new LinkedList<FreeMapDateHandle>();
        source.getStartDateHandles().forEach(handle -> dateHandles.add(FreeMapDateHandle.createFreeMapDateHandle(
                newID, handle.getDate(), FreeMapDateHandle.TimeType.START, new Point2D(handle.getXPos(), handle.getYPos()))));
        source.getEndDateHandles().forEach(handle -> dateHandles.add(FreeMapDateHandle.createFreeMapDateHandle(
                newID, handle.getDate(), FreeMapDateHandle.TimeType.END, new Point2D(handle.getXPos(), handle.getYPos()))));
        return dateHandles;
    }

    private static void duplicatePortraits(final FriezeFreeMap source, final Map<Person, FreeMapPerson> personsByPerson,
            final Map<Long, FreeMapStay> newStaysBySourceStayId) {
        source.getPersons().forEach(sourcePerson -> {
            final var newPerson = personsByPerson.get(sourcePerson.getPerson());
            sourcePerson.getFreeMapPortraits().forEach(sourcePortrait -> {
                final var sourceLink = sourcePerson.getPortraitLink(sourcePortrait);
                final var sourceConnector = sourceLink.getEndConnector();
                final var newStay = newStaysBySourceStayId.get(sourceConnector.getLinkedElementID());
                if (newStay == null) {
                    LOG.log(Level.WARNING, "Skipping portrait {0} while duplicating FriezeFreeMap: its stay was not duplicated.", new Object[]{sourcePortrait});
                    return;
                }
                final var newConnector = FreeMapConnectorFactory.createFreeMapLinkConnector(
                        IFileObject.NO_ID, newStay, sourceConnector.getDate(), FriezeFreeMap.DEFAULT_PLOT_SIZE);
                final var newPortrait = FreeMapPortraitFactory.createFreeMapPortrait(
                        sourcePortrait.getPortrait(), newPerson, sourcePortrait.getRadius());
                newPortrait.setX(sourcePortrait.getX());
                newPortrait.setY(sourcePortrait.getY());
                final var newPortraitLink = FreeMapLinkFactory.createPortraitLink(newPortrait, newConnector);
                newPerson.addFreeMapPortrait(newPortrait, newPortraitLink);
            });
        });
    }

}
