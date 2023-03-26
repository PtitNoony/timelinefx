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
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.Portrait;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class FreeMapPortraitFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<FreeMapPortrait> FACTORY = new Factory<>();

    private FreeMapPortraitFactory() {
        // private utility constructor
    }

    public static final void reset() {
        FACTORY.reset();
    }

    public static List<FreeMapPortrait> getFreeMapPortraits() {
        return FACTORY.getObjects();
    }

    public static FreeMapPortrait getFreeMapPortrait(long freeMapPortraitID) {
        return FACTORY.get(freeMapPortraitID);
    }

    public static FreeMapPortrait createFreeMapPortrait(Portrait aPortrait, FreeMapPerson aFreeMapPerson, double aRadius) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapPortrait with Portrait={0}", new Object[]{aPortrait.getName()});
        var portrait = new FreeMapPortrait(FACTORY.getNextID(), aPortrait, aFreeMapPerson, aRadius);
        FACTORY.addObject(portrait);
        return portrait;
    }

    public static FreeMapPortrait createFreeMapPortrait(long anID, Portrait aPortrait, FreeMapPerson aFreeMapPerson, double aRadius) {
        var idUsed = anID;
        if (anID == IFileObject.NO_ID) {
            idUsed = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a FreeMapPortrait with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating a FreeMapPortrait (id={0} with Portrait={1}", new Object[]{anID, aPortrait});
        var portrait = new FreeMapPortrait(idUsed, aPortrait, aFreeMapPerson, aRadius);
        FACTORY.addObject(portrait);
        return portrait;
    }

}
