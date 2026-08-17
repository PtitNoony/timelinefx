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
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class StayPeriodLocalDateTest {

    @TempDir
    private Path tempDir;

    private Person person;

    private Person otherPerson;

    private Place place;

    private Place otherPlace;

    public StayPeriodLocalDateTest() {
    }

    @BeforeEach
    public void setUp() {
        StayFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final var project = TimeLineProjectFactory.createProject("StayPeriodLocalDateTest", configParams);
        person = PersonFactory.createPerson(project, "person");
        otherPerson = PersonFactory.createPerson(project, "otherPerson");
        place = PlaceFactory.createPlace("place", PlaceLevel.PLANET, null);
        otherPlace = PlaceFactory.createPlace("otherPlace", PlaceLevel.PLANET, null);
    }

    @AfterEach
    public void tearDown() {
        StayFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
    }

    private StayPeriodLocalDate createStay() {
        return StayFactory.createStayPeriodLocalDate(person, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10), place);
    }

    /**
     * Test of getPerson and setPerson methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testPerson() {
        final var instance = createStay();
        assertEquals(person, instance.getPerson());
        instance.setPerson(otherPerson);
        assertEquals(otherPerson, instance.getPerson());
    }

    /**
     * Test of getPlace and setPlace methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testPlace() {
        final var instance = createStay();
        assertEquals(place, instance.getPlace());
        instance.setPlace(otherPlace);
        assertEquals(otherPlace, instance.getPlace());
    }

    /**
     * Test of getStartDate and setStartDate methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testStartDate() {
        final var instance = createStay();
        assertEquals(LocalDate.of(2023, 1, 1).toEpochDay(), instance.getStartDate());
        final var newStart = LocalDate.of(2023, 2, 1);
        instance.setStartDate(newStart);
        assertEquals(newStart.toEpochDay(), instance.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 1).toEpochDay(), instance.getPreviousStartDate());
    }

    /**
     * Test of getEndDate and setEndDate methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testEndDate() {
        final var instance = createStay();
        assertEquals(LocalDate.of(2023, 1, 10).toEpochDay(), instance.getEndDate());
        final var newEnd = LocalDate.of(2023, 2, 10);
        instance.setEndDate(newEnd);
        assertEquals(newEnd.toEpochDay(), instance.getEndDate());
        assertEquals(LocalDate.of(2023, 1, 10).toEpochDay(), instance.getPreviousEndDate());
    }

    /**
     * Test of getTimeFormat method, of class StayPeriodLocalDate.
     */
    @Test
    public void testGetTimeFormat() {
        assertEquals(TimeFormat.LOCAL_TIME, createStay().getTimeFormat());
    }

    /**
     * Test of getDisplayString and toString methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testDisplayString() {
        final var instance = createStay();
        assertEquals(instance.getDisplayString(), instance.toString());
        assertTrue(instance.getDisplayString().contains("person"));
        assertTrue(instance.getDisplayString().contains("place"));
    }

    /**
     * Test of addListener/removeListener methods, of class StayPeriodLocalDate.
     */
    @Test
    public void testListener() {
        final var instance = createStay();
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        instance.addListener(listener);
        instance.setStartDate(LocalDate.of(2023, 3, 1));
        assertTrue(fired[0]);
        //
        fired[0] = false;
        instance.removeListener(listener);
        instance.setStartDate(LocalDate.of(2023, 4, 1));
        assertEquals(false, fired[0]);
    }

    /**
     * Test of STAY_COMPARATOR field, of class StayPeriod.
     */
    @Test
    public void testStayComparator() {
        final var early = StayFactory.createStayPeriodLocalDate(person, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 10), place);
        final var late = StayFactory.createStayPeriodLocalDate(person, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10), place);
        assertTrue(StayPeriod.STAY_COMPARATOR.compare(early, late) < 0);
        assertTrue(StayPeriod.STAY_COMPARATOR.compare(late, early) > 0);
    }

}
