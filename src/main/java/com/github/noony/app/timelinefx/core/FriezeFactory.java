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

import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public final class FriezeFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<Frieze> FACTORY =new Factory<>();

    private FriezeFactory() {
        // private utility constructor
    }

    public static void reset() {
        FACTORY.reset();
    }

    public static List<Frieze> getFriezes() {
        return FACTORY.getObjects();
    }

    public static Frieze getFrieze(long friezeID) {
        return FACTORY.get(friezeID);
    }

    public static Frieze createFrieze(TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1} staysToConsider={2} ", new Object[]{aProject.getName(), friezeName, staysToConsider});
        var frieze = new Frieze(FACTORY.getNextID(), aProject, friezeName, staysToConsider);
        FACTORY.addObject(frieze);
        return frieze;
    }

    public static Frieze createFrieze(long anID, TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a frieze " + friezeName + " with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze (id={0} with TimeLineProject={1} friezeName={2} staysToConsider={3} ", new Object[]{anID, aProject.getName(), friezeName, staysToConsider});
        var frieze = new Frieze(anID, aProject, friezeName, staysToConsider);
        FACTORY.addObject(frieze);
        return frieze;
    }

    public static Frieze createFrieze(TimeLineProject aProject, String friezeName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1}", new Object[]{aProject.getName(), friezeName});
        var frieze = new Frieze(FACTORY.getNextID(), aProject, friezeName);
        FACTORY.addObject(frieze);
        return frieze;
    }

}
