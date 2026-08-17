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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author solun
 */
public class TimeFormatTest {

    public TimeFormatTest() {
    }

    /**
     * Test of values method, of class TimeFormat.
     */
    @Test
    public void testValues() {
        assertArrayEquals(new TimeFormat[]{TimeFormat.LOCAL_TIME, TimeFormat.TIME_MIN}, TimeFormat.values());
    }

    /**
     * Test of valueOf method, of class TimeFormat.
     */
    @Test
    public void testValueOf() {
        assertSame(TimeFormat.LOCAL_TIME, TimeFormat.valueOf("LOCAL_TIME"));
    }

}
