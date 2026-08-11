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

package com.github.noony.app.timelinefx.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

/**
 * Entry point for creating and retrieving {@link Place} instances.
 *
 * @author hamon
 */
public final class PlaceFactory {

    /**
     * The implicit root place all top-level places are attached to.
     */
    public static final Place PLACES_PLACE = new Place(-1, "PLACES", PlaceLevel.UNIVERSE, null);

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Registry of created places.
     */
    private static final Factory<Place> FACTORY = new Factory<>();

    /**
     * The places with no parent (or whose parent is {@link #PLACES_PLACE}).
     */
    private static final List<Place> ROOT_PLACES = new LinkedList<>();

    private PlaceFactory() {
        // private utility constructor
    }

    /**
     * Resets the factory, discarding all created places.
     */
    public static void reset() {
        FACTORY.reset();
        ROOT_PLACES.clear();
    }

    /**
     * Returns all created places, sorted by name.
     *
     * @return all created places, sorted by name
     */
    public static List<Place> getPlaces() {
        return FACTORY.getObjects().stream().sorted(Place.COMPARATOR).collect(Collectors.toList());
    }

    /**
     * @return the root places
     */
    public static List<Place> getRootPlaces() {
        return new ArrayList<>(ROOT_PLACES);
    }

    /**
     * @param placeID a place's id
     * @return the place with the given id, or null if none exists
     */
    public static Place getPlace(final long placeID) {
        return FACTORY.get(placeID);
    }

    /**
     * Creates a new place with the default color.
     *
     * @param placeName the place's name
     * @param placeLevel the place's level
     * @param parentPlace the place's parent, or null for a root place
     * @return the created place
     */
    public static Place createPlace(final String placeName, final PlaceLevel placeLevel, final Place parentPlace) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating place with placeName={0} placeLevel={1} parentPlace={2} ", new Object[]{placeName, placeLevel, parentPlace});
        final var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        final var place = new Place(FACTORY.getNextID(), placeName, placeLevel, trueParentPlace);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FACTORY.addObject(place);
        return place;
    }

    /**
     * Creates a new place.
     *
     * @param placeName the place's name
     * @param placeLevel the place's level
     * @param parentPlace the place's parent, or null for a root place
     * @param color the place's color
     * @return the created place
     */
    public static Place createPlace(final String placeName, final PlaceLevel placeLevel, final Place parentPlace, final Color color) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating place with placeName={0} placeLevel={1} parentPlace={2} color={3} ", new Object[]{placeName, placeLevel, parentPlace, color});
        final var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        final var place = new Place(FACTORY.getNextID(), placeName, placeLevel, trueParentPlace, color);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FACTORY.addObject(place);
        return place;
    }

    /**
     * Creates a new place with a specific id.
     *
     * @param id the id to assign to the new place
     * @param placeName the place's name
     * @param placeLevel the place's level
     * @param parentPlace the place's parent, or null for a root place
     * @param color the place's color
     * @return the created place
     */
    public static Place createPlace(final long id, final String placeName, final PlaceLevel placeLevel, final Place parentPlace, Color color) {
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("trying to create place " + placeName + " with existing id=" + id);
        }
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating place (id={0} with placeName={1} placeLevel={2} parentPlace={3} ", new Object[]{id, placeName, placeLevel, parentPlace});
        var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        final var place = new Place(id, placeName, placeLevel, trueParentPlace, color);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FACTORY.addObject(place);
        return place;
    }

    private static void addRootPlace(final Place aRootPlace) {
        ROOT_PLACES.add(aRootPlace);
        ROOT_PLACES.sort(Place.COMPARATOR);
    }
}
