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

import com.github.noony.app.timelinefx.core.*;
import static com.github.noony.app.timelinefx.core.FriezeObjectFactory.CREATION_LOGGING_LEVEL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Dimension2D;

/**
 *
 * @author hamon
 */
public class FriezeFreeMapFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Map<Long, FriezeFreeMap> FREE_MAPS = new HashMap<>();

    private FriezeFreeMapFactory() {
        // private utility constructor
    }

    public static final void reset() {
        FREE_MAPS.clear();
    }

    public static List<FriezeFreeMap> getFriezeFreeMaps() {
        return FREE_MAPS.values().stream().collect(Collectors.toList());
    }

    public static FriezeFreeMap getFriezeFreeMap(long friezeID) {
        return FREE_MAPS.get(friezeID);
    }

    public static FriezeFreeMap createFriezeFreeMap(Frieze aFrieze, Dimension2D aFriezeDimension, double aPersonWidth, double aPlaceNameWidth, double aFontSize, double aPlotSeparation, boolean aPlotVisibilty, double aPlotSize) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a friezeFreeMap with Frieze={0}", new Object[]{aFrieze});
        var friezeFreeMap = new FriezeFreeMap(FriezeObjectFactory.getNextID(), aFrieze, aFriezeDimension, aPersonWidth, aPlaceNameWidth, aFontSize, aPlotSeparation, aPlotVisibilty, aPlotSize);
        FREE_MAPS.put(friezeFreeMap.getId(), friezeFreeMap);
        FriezeObjectFactory.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    public static FriezeFreeMap createFriezeFreeMap(long anID, Frieze aFrieze, Dimension2D aFriezeDimension, double aPersonWidth, double aPlaceNameWidth, double aFontSize, double aPlotSeparation, boolean aPlotVisibilty, double aPlotSize) {
        if (!FriezeObjectFactory.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a frieze (id={0} with Frieze={1}", new Object[]{anID, aFrieze});
        var friezeFreeMap = new FriezeFreeMap(FriezeObjectFactory.getNextID(), aFrieze, aFriezeDimension, aPersonWidth, aPlaceNameWidth, aFontSize, aPlotSeparation, aPlotVisibilty, aPlotSize);
        FREE_MAPS.put(friezeFreeMap.getId(), friezeFreeMap);
        FriezeObjectFactory.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    public static FriezeFreeMap createFriezeFreeMap(Frieze aFrieze) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a friezeFreeMap with Frieze={0} ", new Object[]{aFrieze});
        var friezeFreeMap = new FriezeFreeMap(FriezeObjectFactory.getNextID(), aFrieze);
        FREE_MAPS.put(friezeFreeMap.getId(), friezeFreeMap);
        FriezeObjectFactory.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

    public static FriezeFreeMap createFriezeFreeMap(long anID, Frieze aFrieze) {
        if (!FriezeObjectFactory.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a friezeFreeMap (id={0} with Frieze={1}", new Object[]{anID, aFrieze});
        var friezeFreeMap = new FriezeFreeMap(anID, aFrieze);
        FREE_MAPS.put(friezeFreeMap.getId(), friezeFreeMap);
        FriezeObjectFactory.addObject(friezeFreeMap);
        return friezeFreeMap;
    }

}
