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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author hamon
 */
public final class FriezeFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Map<Long, Frieze> FRIEZES = new HashMap<>();

    private FriezeFactory() {
        // private utility constructor
    }

    public static void reset() {
        FRIEZES.clear();
    }

    public static List<Frieze> getFriezes() {
        return new ArrayList<>(FRIEZES.values());
    }

    public static Frieze getFrieze(long friezeID) {
        return FRIEZES.get(friezeID);
    }

    public static Frieze createFrieze(TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1} staysToConsider={2} ", new Object[]{aProject, friezeName, staysToConsider});
        var frieze = new Frieze(FriezeObjectFactory.getNextID(), aProject, friezeName, staysToConsider);
        FRIEZES.put(frieze.getId(), frieze);
        FriezeObjectFactory.addObject(frieze);
        return frieze;
    }

    public static Frieze createFrieze(long anID, TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        if (!FriezeObjectFactory.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a frieze " + friezeName + " with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze (id={0} with TimeLineProject={1} friezeName={2} staysToConsider={3} ", new Object[]{anID, aProject, friezeName, staysToConsider});
        var frieze = new Frieze(anID, aProject, friezeName, staysToConsider);
        FRIEZES.put(frieze.getId(), frieze);
        FriezeObjectFactory.addObject(frieze);
        return frieze;
    }

    public static Frieze createFrieze(TimeLineProject aProject, String friezeName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze with TimeLineProject={0} friezeName={1}", new Object[]{aProject, friezeName});
        var frieze = new Frieze(FriezeObjectFactory.getNextID(), aProject, friezeName);
        FRIEZES.put(frieze.getId(), frieze);
        FriezeObjectFactory.addObject(frieze);
        return frieze;
    }

}
