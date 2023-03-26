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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static FriezeFreeMap createFriezeFreeMap(long anID, Frieze aFrieze, Map<String, String> inputParameters,
            List<FreeMapDateHandle> dateHandles, List<FreeMapPerson> persons, List<FreeMapPlace> places, List<FreeMapStay> stays) {
        //
        if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("Trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(Level.WARNING, "Creating a friezeFreeMap (id={0} with Frieze={1} with its full content.", new Object[]{anID, aFrieze});
        var friezeFreeMap = new FriezeFreeMap(anID, aFrieze, inputParameters, dateHandles, persons, places, stays, false);
        FACTORY.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

}
