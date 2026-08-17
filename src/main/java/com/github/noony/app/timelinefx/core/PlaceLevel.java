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

/**
 * The nesting level of a {@link Place}, from the smallest (address) to the largest (universe).
 *
 * @author hamon
 */
public enum PlaceLevel {

    /**
     * A single address.
     */
    ADDRESS(10),
    /**
     * A town.
     */
    TOWN(20),
    /**
     * A department.
     */
    DEPARTMENT(30),
    /**
     * A region.
     */
    REGION(40),
    /**
     * A country.
     */
    COUNTRY(50),
    /**
     * A continent.
     */
    CONTINENT(60),
    /**
     * A planet.
     */
    PLANET(70),
    /**
     * An orbit around a planet.
     */
    ORBIT(75),
    /**
     * A star system.
     */
    SYSTEM(80),
    /**
     * The space between star systems.
     */
    INTER_SYSTEM_SPACE(90),
    /**
     * A galaxy.
     */
    GALAXY(100),
    /**
     * The whole universe.
     */
    UNIVERSE(1000);

    /**
     * The numeric value used to compare levels.
     */
    private final int level;

    PlaceLevel(final int aLevel) {
        level = aLevel;
    }

    /**
     * Returns this level's numeric value.
     *
     * @return this level's numeric value
     */
    public int getLevelValue() {
        return level;
    }

}
