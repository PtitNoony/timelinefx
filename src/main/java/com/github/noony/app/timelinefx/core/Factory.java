/*
 * Copyright (C) 2023 NoOnY
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic registry assigning unique ids to, and keeping track of, instances of {@code T}.
 *
 * @author hamon
 * @param <T> Any class implementing FriezeObject
 */
public class Factory<T extends FriezeObject> {

    /**
     * Default logging level to be used when creating an object in a Factory.
     */
    public static final Level CREATION_LOGGING_LEVEL = Level.FINE;

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Objects registered in this factory, keyed by id.
     */
    private final Map<Long, T> objects = new HashMap<>();
    //

    /**
     * The next id to be handed out by {@link #getNextID()}.
     */
    private long nextUniqueId;

    /**
     * Default constructor.
     */
    public Factory() {
        // private utility constructor
    }

    /**
     * Resets all the factories and restarts the unique ID counter to 0.
     */
    public final void reset() {
        objects.clear();
        nextUniqueId = 0L;
    }

    /**
     * Checks that the object's id is not already used.
     *
     * @param object the object the be added
     */
    public final void addObject(final T object) {
        if (objects.containsKey(object.getId())) {
            throw new IllegalStateException();
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating object (id={0}) :: {1}", new Object[]{object.getId(), object});
        objects.put(object.getId(), object);
        nextUniqueId = Math.max(nextUniqueId, object.getId() + 1);
    }

    /**
     * Returns the next available unique id.
     *
     * @return the next available unique id
     */
    public final long getNextID() {
        final long result = nextUniqueId;
        incrID();
        return result;
    }

    /**
     *
     * @param id an id
     * @return the corresponding FriezeObject if it exists, null otherwise.
     */
    public final T get(final long id) {
        return objects.get(id);
    }

    /**
     *
     * @param id an id
     * @return whether the id is already used by an object.
     */
    public final boolean isIdAvailable(final long id) {
        return !objects.containsKey(id);
    }

    /**
     * @return all the objects currently registered in this factory
     */
    public List<T> getObjects() {
        return new ArrayList<>(objects.values());
    }

    private void incrID() {
        nextUniqueId++;
    }
}
