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

import static com.github.noony.app.timelinefx.core.FriezeObjectFactory.CREATION_LOGGING_LEVEL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class PlaceFactory {

    public static final Place PLACES_PLACE = new Place(-1, "PLACES", PlaceLevel.UNIVERSE, null);

    private static final Logger LOG = Logger.getGlobal();

    private static final Map<Long, Place> PLACES = new HashMap<>();

    private static final List<Place> ROOT_PLACES = new LinkedList<>();

    private PlaceFactory() {
        // private utility constructor
    }

    public static final void reset() {
        PLACES.clear();
        ROOT_PLACES.clear();
    }

    public static List<Place> getPlaces() {
        return PLACES.values().stream().sorted(Place.COMPARATOR).collect(Collectors.toList());
    }

    public static List<Place> getRootPlaces() {
        return ROOT_PLACES.stream().collect(Collectors.toList());
    }

    public static Place getPlace(long placeID) {
        return PLACES.get(placeID);
    }

    public static Place createPlace(String placeName, PlaceLevel placeLevel, Place parentPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating place with placeName={0} placeLevel={1} parentPlace={2} ", new Object[]{placeName, placeLevel, parentPlace});
        var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        var place = new Place(FriezeObjectFactory.getNextID(), placeName, placeLevel, trueParentPlace);
        PLACES.put(place.getId(), place);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FriezeObjectFactory.addObject(place);
        return place;
    }

    public static Place createPlace(String placeName, PlaceLevel placeLevel, Place parentPlace, Color color) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating place with placeName={0} placeLevel={1} parentPlace={2} color={3} ", new Object[]{placeName, placeLevel, parentPlace, color});
        var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        var place = new Place(FriezeObjectFactory.getNextID(), placeName, placeLevel, trueParentPlace, color);
        PLACES.put(place.getId(), place);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FriezeObjectFactory.addObject(place);
        return place;
    }

    public static Place createPlace(long id, String placeName, PlaceLevel placeLevel, Place parentPlace, Color color) {
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("trying to create place " + placeName + " with existing id=" + id);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating place (id={0} with placeName={1} placeLevel={2} parentPlace={3} ", new Object[]{id, placeName, placeLevel, parentPlace});
        var trueParentPlace = parentPlace != null ? parentPlace : PLACES_PLACE;
        var place = new Place(id, placeName, placeLevel, trueParentPlace, color);
        PLACES.put(place.getId(), place);
        if (place.isRootPlace()) {
            addRootPlace(place);
        }
        FriezeObjectFactory.addObject(place);
        return place;
    }

    private static void addRootPlace(Place aRootPlace) {
        ROOT_PLACES.add(aRootPlace);
        ROOT_PLACES.sort(Place.COMPARATOR);
    }
}
