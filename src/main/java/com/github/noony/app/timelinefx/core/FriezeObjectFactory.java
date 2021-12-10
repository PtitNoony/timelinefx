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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arnaud
 */
public class FriezeObjectFactory {

    /**
     * Default logging level to be used when creating an object in a Factory
     */
    public static final Level CREATION_LOGGING_LEVEL = Level.FINE;

    private static final Logger LOG = Logger.getGlobal();
    private static final Map<Long, FriezeObject> OBJECTS = new HashMap<>();
    //
    private static long nextUniqueId = 0L;

    private FriezeObjectFactory() {
        // private utility constructor
    }

    /**
     * Resets all the factories and restarts the unique ID counter to 0.
     */
    public static final void reset() {
        OBJECTS.clear();
        nextUniqueId = 0L;
        //
        PlaceFactory.reset();
        PersonFactory.reset();
        StayFactory.reset();
        PictureFactory.reset();
        PortraitFactory.reset();
    }

    /**
     * Checks that the object's id is not already used.
     *
     * @param object the object the be added
     */
    public static final void addObject(FriezeObject object) {
        if (OBJECTS.containsKey(object.getId())) {
            throw new IllegalStateException();
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating object (id={0}) :: {1}", new Object[]{object.getId(), object});
        OBJECTS.put(object.getId(), object);
        nextUniqueId = Math.max(nextUniqueId, object.getId() + 1);
    }

    /**
     *
     * @return the next available unique id
     */
    public static final long getNextID() {
        long result = nextUniqueId;
        incrID();
        return result;
    }

    /**
     *
     * @param id an id
     * @return the corresponding FriezeObject if it exists, null otherwise.
     */
    public static final FriezeObject get(long id) {
        return OBJECTS.get(id);
    }

    /**
     *
     * @param id an id
     * @return whether the id is already used by an object.
     */
    public static final boolean isIdAvailable(long id) {
        return !OBJECTS.containsKey(id);
    }

    private static void incrID() {
        nextUniqueId++;
    }
}
