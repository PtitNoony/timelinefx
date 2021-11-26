/*
 * Copyright (C) 2021 NoOnY
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author hamon
 */
public class PortraitFactory {

    private static final Map<Long, Portrait> PORTRAITS = new HashMap<>();

    private static final Logger LOG = Logger.getGlobal();

    private PortraitFactory() {
        // private utility constructor
    }

    public static final void reset() {
        PORTRAITS.clear();
    }

    public static final Portrait getPortrait(long id) {
        return PORTRAITS.get(id);
    }

    public static Portrait createPortrait(Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with person={0} filePath={1}.", new Object[]{person, filePath});
        var portrait = new Portrait(FriezeObjectFactory.getNextID(), person, filePath);
        PORTRAITS.put(portrait.getId(), portrait);
        FriezeObjectFactory.addObject(portrait);
        return portrait;
    }

    public static Portrait createPortrait(long id, Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with id={0} person={1} filePath={2}.", new Object[]{id, person, filePath});
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("trying to create portrait " + filePath + " with existing id=" + id + " (exists : " + PORTRAITS.get(id) + "[" + PORTRAITS.get(id).getId() + "])");
        }
        var portrait = new Portrait(id, person, filePath);
        PORTRAITS.put(portrait.getId(), portrait);
        FriezeObjectFactory.addObject(portrait);
        return portrait;
    }

    public static List<Portrait> getPORTRAITS() {
        return Collections.unmodifiableList(
                PORTRAITS.values().stream().sorted(Portrait.COMPARATOR).collect(Collectors.toList())
        );
    }

}
