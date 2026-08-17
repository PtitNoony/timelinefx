/*
 * Copyright (C) 2026 NoOnY
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class PlaceLevelTest {

    public PlaceLevelTest() {
    }

    /**
     * Test of getLevelValue method, of class PlaceLevel.
     */
    @Test
    public void testGetLevelValue() {
        assertEquals(10, PlaceLevel.ADDRESS.getLevelValue());
        assertEquals(1000, PlaceLevel.UNIVERSE.getLevelValue());
        assertEquals(1_000_000, PlaceLevel.ROOT.getLevelValue());
    }

    /**
     * Test that every level is ordered strictly below the next one, and that ROOT is the largest.
     */
    @Test
    public void testLevelsAreOrdered() {
        final var levels = PlaceLevel.values();
        for (int i = 1; i < levels.length; i++) {
            assertTrue(levels[i - 1].getLevelValue() < levels[i].getLevelValue(),
                    levels[i - 1] + " should be lower than " + levels[i]);
        }
        assertEquals(PlaceLevel.ROOT, levels[levels.length - 1]);
    }

}
