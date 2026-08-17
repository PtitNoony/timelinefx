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
public final class StayPeriodSimpleTimeTest {

    @TempDir
    private Path tempDir;

    private Person person;

    private Place place;

    public StayPeriodSimpleTimeTest() {
    }

    @BeforeEach
    public void setUp() {
        StayFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final var project = TimeLineProjectFactory.createProject("StayPeriodSimpleTimeTest", configParams);
        person = PersonFactory.createPerson(project, "person");
        place = PlaceFactory.createPlace("place", PlaceLevel.PLANET, null);
    }

    @AfterEach
    public void tearDown() {
        StayFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
    }

    private StayPeriodSimpleTime createStay() {
        return StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
    }

    /**
     * Test of getStartDate and setStartDate methods, of class StayPeriodSimpleTime.
     */
    @Test
    public void testStartDate() {
        final var instance = createStay();
        assertEquals(0.0, instance.getStartDate());
        instance.setStartDate(5.0);
        assertEquals(5.0, instance.getStartDate());
        assertEquals(0.0, instance.getPreviousStartDate());
    }

    /**
     * Test of getEndDate and setEndDate methods, of class StayPeriodSimpleTime.
     */
    @Test
    public void testEndDate() {
        final var instance = createStay();
        assertEquals(10.0, instance.getEndDate());
        instance.setEndDate(20.0);
        assertEquals(20.0, instance.getEndDate());
        assertEquals(10.0, instance.getPreviousEndDate());
    }

    /**
     * Test of getTimeFormat method, of class StayPeriodSimpleTime.
     */
    @Test
    public void testGetTimeFormat() {
        assertEquals(TimeFormat.TIME_MIN, createStay().getTimeFormat());
    }

    /**
     * Test of getDisplayString and toString methods, of class StayPeriodSimpleTime.
     */
    @Test
    public void testDisplayString() {
        final var instance = createStay();
        assertEquals(instance.getDisplayString(), instance.toString());
        assertTrue(instance.getDisplayString().contains("person"));
        assertTrue(instance.getDisplayString().contains("place"));
    }

    /**
     * Test of addListener/removeListener methods, of class StayPeriodSimpleTime.
     */
    @Test
    public void testListener() {
        final var instance = createStay();
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        instance.addListener(listener);
        instance.setStartDate(3.0);
        assertTrue(fired[0]);
        //
        fired[0] = false;
        instance.removeListener(listener);
        instance.setStartDate(4.0);
        assertEquals(false, fired[0]);
    }

}
