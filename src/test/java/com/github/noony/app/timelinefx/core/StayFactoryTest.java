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

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author solun
 */
public class StayFactoryTest {

    @TempDir
    private Path tempDir;

    private Person person;

    private Place place;

    public StayFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        PersonFactory.reset();
        PlaceFactory.reset();
        StayFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final var project = TimeLineProjectFactory.createProject("StayFactoryTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
    }

    @AfterEach
    public void tearDown() {
        PersonFactory.reset();
        PlaceFactory.reset();
        StayFactory.reset();
    }

    /**
     * Test of createStayPeriodSimpleTime and getStay methods, of class StayFactory.
     */
    @Test
    public void testCreateStayPeriodSimpleTimeAndGetStay() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        assertEquals(stay, StayFactory.getStay(stay.getId()));
        assertEquals(person, stay.getPerson());
        assertEquals(place, stay.getPlace());
    }

    /**
     * Test of createStayPeriodSimpleTime method, of class StayFactory, with a specific id.
     */
    @Test
    public void testCreateStayPeriodSimpleTimeWithId() {
        final var stay = StayFactory.createStayPeriodSimpleTime(42L, person, 0.0, 10.0, place);
        assertEquals(42L, stay.getId());
    }

    /**
     * Test of createStayPeriodSimpleTime method, of class StayFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreateStayPeriodSimpleTimeDuplicateId() {
        StayFactory.createStayPeriodSimpleTime(42L, person, 0.0, 10.0, place);
        assertThrows(IllegalArgumentException.class,
                () -> StayFactory.createStayPeriodSimpleTime(42L, person, 0.0, 10.0, place));
    }

    /**
     * Test of createStayPeriodLocalDate method, of class StayFactory.
     */
    @Test
    public void testCreateStayPeriodLocalDate() {
        final var start = LocalDate.of(2023, 1, 1);
        final var end = LocalDate.of(2023, 1, 10);
        final var stay = StayFactory.createStayPeriodLocalDate(person, start, end, place);
        assertEquals(stay, StayFactory.getStay(stay.getId()));
        assertEquals(person, stay.getPerson());
        assertEquals(place, stay.getPlace());
    }

    /**
     * Test of createStayPeriodLocalDate method, of class StayFactory, with a specific id.
     */
    @Test
    public void testCreateStayPeriodLocalDateWithId() {
        final var start = LocalDate.of(2023, 1, 1);
        final var end = LocalDate.of(2023, 1, 10);
        final var stay = StayFactory.createStayPeriodLocalDate(42L, person, start, end, place);
        assertEquals(42L, stay.getId());
    }

    /**
     * Test of createStayPeriodLocalDate method, of class StayFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreateStayPeriodLocalDateDuplicateId() {
        final var start = LocalDate.of(2023, 1, 1);
        final var end = LocalDate.of(2023, 1, 10);
        StayFactory.createStayPeriodLocalDate(42L, person, start, end, place);
        assertThrows(IllegalArgumentException.class,
                () -> StayFactory.createStayPeriodLocalDate(42L, person, start, end, place));
    }

    /**
     * Test of getStay method, of class StayFactory, with an unknown id.
     */
    @Test
    public void testGetStayUnknownId() {
        assertNull(StayFactory.getStay(999L));
    }

    /**
     * Test of reset method, of class StayFactory.
     */
    @Test
    public void testReset() {
        StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        StayFactory.reset();
        assertNull(StayFactory.getStay(0L));
    }

}
