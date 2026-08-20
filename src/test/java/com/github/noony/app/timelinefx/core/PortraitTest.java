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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Portrait}.
 *
 * @author hamon
 */
public final class PortraitTest {

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * A person used in these tests.
     */
    private Person person;

    /**
     * Another person used in these tests.
     */
    private Person otherPerson;

    /**
     * A place used in these tests.
     */
    private Place place;

    /**
     * Default constructor.
     */
    public PortraitTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        PortraitFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final var project = TimeLineProjectFactory.createProject("PortraitTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        otherPerson = PersonFactory.createPerson(project, "otherPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        PortraitFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
    }

    /**
     * Test of getPerson method, of class Portrait.
     */
    @Test
    public void testGetPerson() {
        final var instance = PortraitFactory.createPortrait(person);
        assertEquals(person, instance.getPerson());
    }

    /**
     * Test of getProject method, of class Portrait: delegates to the owning person's project.
     */
    @Test
    public void testGetProject() {
        final var instance = PortraitFactory.createPortrait(person);
        assertEquals(person.getProject(), instance.getProject());
    }

    /**
     * Test of getPersons method, of class Portrait: always the single owning person.
     */
    @Test
    public void testGetPersons() {
        final var instance = PortraitFactory.createPortrait(person);
        assertEquals(1, instance.getPersons().size());
        assertEquals(person, instance.getPersons().get(0));
    }

    /**
     * Test of addPerson and removePerson methods, of class Portrait: always rejected.
     */
    @Test
    public void testAddRemovePersonAlwaysRejected() {
        final var instance = PortraitFactory.createPortrait(person);
        assertFalse(instance.addPerson(otherPerson));
        assertEquals(1, instance.getPersons().size());
        assertFalse(instance.removePerson(person));
        assertEquals(1, instance.getPersons().size());
    }

    /**
     * Test of addPlace and removePlace methods, of class Portrait: always rejected.
     */
    @Test
    public void testAddRemovePlaceAlwaysRejected() {
        final var instance = PortraitFactory.createPortrait(person);
        assertFalse(instance.addPlace(place));
        assertTrue(instance.getPlaces().isEmpty());
        assertFalse(instance.removePlace(place));
    }

    /**
     * Test of compareTo method, of class Portrait.
     */
    @Test
    public void testCompareTo() {
        final var instance = PortraitFactory.createPortrait(person);
        assertEquals(1, instance.compareTo(null));
        assertEquals(0, instance.compareTo(instance));
    }

    /**
     * Test of toString method, of class Portrait.
     */
    @Test
    public void testToString() {
        final var instance = PortraitFactory.createPortrait(person);
        assertEquals(instance.getName(), instance.toString());
    }

    /**
     * Test of COMPARATOR field, of class Portrait.
     */
    @Test
    public void testComparator() {
        final var first = PortraitFactory.createPortrait(1L, person, "portraits/LegoHead.png");
        final var second = PortraitFactory.createPortrait(2L, person, "portraits/LegoHead.png");
        assertTrue(Portrait.COMPARATOR.compare(first, second) < 0);
        assertTrue(Portrait.COMPARATOR.compare(second, first) > 0);
    }

}
