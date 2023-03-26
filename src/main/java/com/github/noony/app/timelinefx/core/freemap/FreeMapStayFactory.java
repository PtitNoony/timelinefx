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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.Factory;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.StayPeriod;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class FreeMapStayFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<FreeMapStay> FACTORY = new Factory<>();

    private FreeMapStayFactory() {
        // private utility constructor
    }

    public static final void reset() {
        FACTORY.reset();
    }

    public static List<FreeMapStay> getFreeMapStays() {
        return FACTORY.getObjects();
    }

    public static FreeMapStay getFriezeFreeMap(long anID) {
        return FACTORY.get(anID);
    }

    public static FreeMapSimpleStay createFreeMapStay(StayPeriod aStayPeriod, FreeMapPerson aFreeMapPerson, FreeMapPlace aFreeMapPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapStay with StayPeriod={0} ", new Object[]{aStayPeriod.getDisplayString()});
        var freeMapStay = new FreeMapSimpleStay(FACTORY.getNextID(), aStayPeriod, IFileObject.NO_ID, IFileObject.NO_ID, aFreeMapPerson, aFreeMapPlace, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        //        (Long anID, StayPeriod aStay, long aStartID, long anEndID, FreeMapPerson aFreeMapPerson, FreeMapPlace aFreeMapPlace, double aPlotSize)
        FACTORY.addObject(freeMapStay);
        return freeMapStay;
    }

    public static FreeMapSimpleStay createFreeMapStay(StayPeriod aStayPeriod, long aStartID, long anEndID, FreeMapPerson aFreeMapPerson, FreeMapPlace aFreeMapPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapStay with StayPeriod={0} ", new Object[]{aStayPeriod.getDisplayString()});
        var freeMapStay = new FreeMapSimpleStay(FACTORY.getNextID(), aStayPeriod, aStartID, anEndID, aFreeMapPerson, aFreeMapPlace, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        FACTORY.addObject(freeMapStay);
        return freeMapStay;
    }


    public static FreeMapSimpleStay createFreeMapStay(long anID, StayPeriod aStayPeriod, long aStartID, long anEndID, FreeMapPerson aFreeMapPerson, FreeMapPlace aFreeMapPlace) {
                if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a friezeFreeMap with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapStay with StayPeriod={0} ", new Object[]{aStayPeriod.getDisplayString()});
        var freeMapStay = new FreeMapSimpleStay(anID, aStayPeriod, aStartID, anEndID, aFreeMapPerson, aFreeMapPlace, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        FACTORY.addObject(freeMapStay);
        return freeMapStay;
    }

    public static FreeMapMergedStay createFreeMapStay(FreeMapStay... stays) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapStay with FreeMapStays={0} ", new Object[]{stays});
        var freeMapMergedStay = new FreeMapMergedStay(FACTORY.getNextID(), stays);
        FACTORY.addObject(freeMapMergedStay);
        return freeMapMergedStay;
    }

}
