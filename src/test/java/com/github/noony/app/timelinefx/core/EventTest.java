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

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Event}.
 *
 * @author solun
 */
public final class EventTest {

    /**
     * Default constructor.
     */
    public EventTest() {
    }

    /**
     * Test of the raw-timestamp constructor and getters, of class Event.
     */
    @Test
    public void testRawTimestampEvent() {
        final var instance = new Event("testRawTimestampEvent", 42L);
        assertEquals("testRawTimestampEvent", instance.getName());
        assertEquals(42L, instance.getDate());
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertNull(instance.getLocalDate());
    }

    /**
     * Test of the calendar-date constructor and getters, of class Event.
     */
    @Test
    public void testLocalDateEvent() {
        final var aDate = LocalDate.of(2023, 1, 2);
        final var instance = new Event("testLocalDateEvent", aDate);
        assertEquals("testLocalDateEvent", instance.getName());
        assertEquals(aDate, instance.getLocalDate());
        assertEquals(aDate.toEpochDay(), instance.getDate());
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
    }

    /**
     * Test of getPersons and getPlaces methods, of class Event: both start out empty.
     */
    @Test
    public void testPersonsAndPlacesStartEmpty() {
        final var instance = new Event("testPersonsAndPlacesStartEmpty", 0L);
        assertTrue(instance.getPersons().isEmpty());
        assertTrue(instance.getPlaces().isEmpty());
    }

}
