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
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Note: {@link Frieze#addPerson(Person)} calls {@code Platform.runLater(...)}, which requires the JavaFX
 * toolkit to be initialized. Bootstrapped once here since plain JUnit tests don't run inside a JavaFX
 * Application.
 *
 * @author solun
 */
public final class FriezeTest {

    static {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException alreadyStarted) {
            // toolkit already initialized elsewhere in this JVM -- fine
        }
    }

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    private Person person;

    private Place place;

    private StayPeriod stay;

    public FriezeTest() {
    }

    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
        FriezeFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("FriezeTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        stay = StayFactory.createStayPeriodSimpleTime(person, 10.0, 20.0, place);
    }

    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
        FriezeFactory.reset();
    }

    /**
     * Test of getId method, of class Frieze.
     */
    @Test
    public void testGetId() {
        final var instance = FriezeFactory.createFrieze(42L, project, "testGetId", List.of());
        assertEquals(42L, instance.getId());
    }

    /**
     * Test of getName and setName methods, of class Frieze.
     */
    @Test
    public void testName() {
        final var instance = FriezeFactory.createFrieze(project, "testName");
        assertEquals("testName", instance.getName());
        instance.setName("newName");
        assertEquals("newName", instance.getName());
    }

    /**
     * Test of getProject method, of class Frieze.
     */
    @Test
    public void testGetProject() {
        final var instance = FriezeFactory.createFrieze(project, "testGetProject");
        assertEquals(project, instance.getProject());
    }

    /**
     * Test of addStay, getStayPeriods and getNbStays methods, of class Frieze.
     */
    @Test
    public void testAddStay() {
        final var instance = FriezeFactory.createFrieze(project, "testAddStay");
        assertTrue(instance.getStayPeriods().isEmpty());
        instance.addStay(stay);
        assertEquals(1, instance.getNbStays());
        assertTrue(instance.getStayPeriods().contains(stay));
        assertTrue(instance.getPersons().contains(person));
        assertTrue(instance.getPlaces().contains(place));
    }

    /**
     * Test of removeStay method, of class Frieze.
     */
    @Test
    public void testRemoveStay() {
        final var instance = FriezeFactory.createFrieze(project, "testRemoveStay");
        instance.addStay(stay);
        instance.removeStay(stay);
        assertTrue(instance.getStayPeriods().isEmpty());
    }

    /**
     * Test of addAllStays(StayPeriod...) and removeAllStays(StayPeriod...) methods, of class Frieze.
     */
    @Test
    public void testAddRemoveAllStaysVarargs() {
        final var instance = FriezeFactory.createFrieze(project, "testAddRemoveAllStaysVarargs");
        instance.addAllStays(stay);
        assertEquals(1, instance.getNbStays());
        instance.removeAllStays(stay);
        assertTrue(instance.getStayPeriods().isEmpty());
    }

    /**
     * Test of addAllStays(Collection) and removeAllStays(Collection) methods, of class Frieze.
     */
    @Test
    public void testAddRemoveAllStaysCollection() {
        final var instance = FriezeFactory.createFrieze(project, "testAddRemoveAllStaysCollection");
        instance.addAllStays(List.of(stay));
        assertEquals(1, instance.getNbStays());
        instance.removeAllStays(List.of(stay));
        assertTrue(instance.getStayPeriods().isEmpty());
    }

    /**
     * Test of getStayPeriods(Person) and getStayPeriods(Place) methods, of class Frieze.
     */
    @Test
    public void testGetStayPeriodsByPersonAndPlace() {
        final var instance = FriezeFactory.createFrieze(project, "testGetStayPeriodsByPersonAndPlace");
        instance.addStay(stay);
        assertEquals(List.of(stay), instance.getStayPeriods(person));
        assertEquals(List.of(stay), instance.getStayPeriods(place));
    }

    /**
     * Test of getStayIndex method, of class Frieze.
     */
    @Test
    public void testGetStayIndex() {
        final var instance = FriezeFactory.createFrieze(project, "testGetStayIndex");
        instance.addStay(stay);
        assertEquals(instance.getPersons().indexOf(person), instance.getStayIndex(stay));
    }

    /**
     * Test of updatePersonSelection method, of class Frieze.
     */
    @Test
    public void testUpdatePersonSelection() {
        final var instance = FriezeFactory.createFrieze(project, "testUpdatePersonSelection");
        instance.updatePersonSelection(person, true);
        assertTrue(instance.getPersons().contains(person));
        instance.updatePersonSelection(person, false);
        assertFalse(instance.getPersons().contains(person));
    }

    /**
     * Test of updatePeopleSelection method, of class Frieze.
     */
    @Test
    public void testUpdatePeopleSelection() {
        final var instance = FriezeFactory.createFrieze(project, "testUpdatePeopleSelection");
        instance.updatePeopleSelection(person, true);
        assertTrue(instance.getPersons().contains(person));
        instance.updatePeopleSelection(person, false);
        assertFalse(instance.getPersons().contains(person));
    }

    /**
     * Test of getPersonsAtPlace method, of class Frieze.
     */
    @Test
    public void testGetPersonsAtPlace() {
        final var instance = FriezeFactory.createFrieze(project, "testGetPersonsAtPlace");
        assertTrue(instance.getPersonsAtPlace(place).isEmpty());
        instance.addStay(stay);
        assertTrue(instance.getPersonsAtPlace(place).contains(person));
    }

    /**
     * Test of getMinDate, getMaxDate and getNbDates methods, of class Frieze.
     */
    @Test
    public void testDatesRange() {
        final var instance = FriezeFactory.createFrieze(project, "testDatesRange");
        instance.addStay(stay);
        assertEquals(10.0, instance.getMinDate());
        assertEquals(20.0, instance.getMaxDate());
        assertEquals(2, instance.getNbDates());
        assertTrue(instance.getDates().containsAll(List.of(10.0, 20.0)));
        assertTrue(instance.getStartDates().contains(10.0));
        assertTrue(instance.getEndDates().contains(20.0));
    }

    /**
     * Test of getMinDateWindow, setMinDateWindow, getMaxDateWindow and setMaxDateWindow methods, of class Frieze.
     */
    @Test
    public void testDateWindow() {
        final var instance = FriezeFactory.createFrieze(project, "testDateWindow");
        instance.setMinDateWindow(5.0);
        assertEquals(5.0, instance.getMinDateWindow());
        instance.setMaxDateWindow(50.0);
        assertEquals(50.0, instance.getMaxDateWindow());
    }

    /**
     * Test of getTimeFormat method, of class Frieze.
     */
    @Test
    public void testGetTimeFormat() {
        final var instance = FriezeFactory.createFrieze(project, "testGetTimeFormat");
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        instance.addStay(stay);
        assertEquals(stay.getTimeFormat(), instance.getTimeFormat());
    }

    /**
     * Test of toString method, of class Frieze.
     */
    @Test
    public void testToString() {
        final var instance = FriezeFactory.createFrieze(project, "testToString");
        assertEquals("Frieze [testToString].", instance.toString());
    }

    /**
     * Test of addListener/removeListener methods, of class Frieze.
     */
    @Test
    public void testListener() {
        final var instance = FriezeFactory.createFrieze(project, "testListener");
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        instance.addListener(listener);
        instance.addStay(stay);
        assertTrue(fired[0]);
        //
        fired[0] = false;
        instance.removeListener(listener);
        instance.removeStay(stay);
        assertFalse(fired[0]);
    }

    /**
     * Test of createFriezeFreeMap, addFriezeFreeMap, removeFriezeFreeMap and getFriezeFreeMaps methods, of class
     * Frieze.
     */
    @Test
    public void testFriezeFreeMaps() {
        final var instance = FriezeFactory.createFrieze(project, "testFriezeFreeMaps");
        instance.addStay(stay);
        final var freeMap = instance.createFriezeFreeMap();
        assertTrue(instance.getFriezeFreeMaps().contains(freeMap));
        instance.removeFriezeFreeMap(freeMap);
        assertFalse(instance.getFriezeFreeMaps().contains(freeMap));
    }

}
