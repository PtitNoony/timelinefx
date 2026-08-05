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

import java.util.List;
import java.util.logging.Logger;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;

/**
 * Entry point for creating and retrieving {@link Frieze} instances.
 *
 * @author hamon
 */
public final class FriezeFactory {

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Registry of created friezes.
     */
    private static final Factory<Frieze> FACTORY = new Factory<>();

    private FriezeFactory() {
        // private utility constructor
    }

    /**
     * Resets the factory, discarding all created friezes.
     */
    public static void reset() {
        FACTORY.reset();
    }

    /**
     * @return all the friezes created so far
     */
    public static List<Frieze> getFriezes() {
        return FACTORY.getObjects();
    }

    /**
     * @param friezeID a frieze's id
     * @return the frieze with the given id, or null if none exists
     */
    public static Frieze getFrieze(final long friezeID) {
        return FACTORY.get(friezeID);
    }

    /**
     * Creates a new frieze restricted to the given stays.
     *
     * @param aProject the project the frieze belongs to
     * @param friezeName the frieze's name
     * @param staysToConsider the stays to include in the frieze
     * @return the created frieze
     */
    public static Frieze createFrieze(final TimeLineProject aProject, final String friezeName, List<StayPeriod> staysToConsider) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1} staysToConsider={2} ", new Object[]{aProject.getName(), friezeName, staysToConsider});
        final var frieze = new Frieze(FACTORY.getNextID(), aProject, friezeName, staysToConsider);
        FACTORY.addObject(frieze);
        return frieze;
    }

    /**
     * Creates a new frieze with a specific id, restricted to the given stays.
     *
     * @param anID the id to assign to the new frieze
     * @param aProject the project the frieze belongs to
     * @param friezeName the frieze's name
     * @param staysToConsider the stays to include in the frieze
     * @return the created frieze
     */
    public static Frieze createFrieze(final long anID, final TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a frieze " + friezeName + " with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze (id={0} with TimeLineProject={1} friezeName={2} staysToConsider={3} ", new Object[]{anID, aProject.getName(), friezeName, staysToConsider});
        final var frieze = new Frieze(anID, aProject, friezeName, staysToConsider);
        FACTORY.addObject(frieze);
        return frieze;
    }

    /**
     * Creates a new frieze containing all of the project's stays.
     *
     * @param aProject the project the frieze belongs to
     * @param friezeName the frieze's name
     * @return the created frieze
     */
    public static Frieze createFrieze(final TimeLineProject aProject, final String friezeName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1}", new Object[]{aProject.getName(), friezeName});
        final var frieze = new Frieze(FACTORY.getNextID(), aProject, friezeName);
        FACTORY.addObject(frieze);
        return frieze;
    }

}
