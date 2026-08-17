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
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Note: uses a real {@link TimeLineProject}, created in a JUnit {@code @TempDir} via an explicit
 * {@link TimeLineProject#PROJECT_FOLDER_KEY} override so it never touches the real user home directory.
 *
 * @author solun
 */
public final class PersonFactoryTest {

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * The project used in these tests.
     */
    private TimeLineProject project;

    /**
     * Default constructor.
     */
    public PersonFactoryTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        PersonFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("PersonFactoryTest", configParams);
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        PersonFactory.reset();
    }

    /**
     * Test of createPerson and getPerson methods, of class PersonFactory.
     */
    @Test
    public void testCreatePersonAndGetPerson() {
        final var person = PersonFactory.createPerson(project, "testCreatePersonAndGetPerson");
        assertEquals(person, PersonFactory.getPerson(person.getId()));
        assertEquals(project, person.getProject());
    }

    /**
     * Test of getPerson method, of class PersonFactory, with an unknown id.
     */
    @Test
    public void testGetPersonUnknownId() {
        assertNull(PersonFactory.getPerson(999L));
    }

    /**
     * Test of createPerson method, of class PersonFactory, with a color.
     */
    @Test
    public void testCreatePersonWithColor() {
        final var person = PersonFactory.createPerson(project, "testCreatePersonWithColor", Color.CORAL);
        assertEquals(Color.CORAL, person.getColor());
    }

    /**
     * Test of createPerson method, of class PersonFactory, with a specific id.
     */
    @Test
    public void testCreatePersonWithId() {
        final var person = PersonFactory.createPerson(project, 42L, "testCreatePersonWithId", Color.CORAL);
        assertEquals(42L, person.getId());
    }

    /**
     * Test of createPerson method, of class PersonFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreatePersonDuplicateId() {
        PersonFactory.createPerson(project, 42L, "testCreatePersonDuplicateIdA", Color.CORAL);
        assertThrows(IllegalArgumentException.class,
                () -> PersonFactory.createPerson(project, 42L, "testCreatePersonDuplicateIdB", Color.CORAL));
    }

    /**
     * Test of getPERSONS method, of class PersonFactory: sorted by name.
     */
    @Test
    public void testGetPersons() {
        PersonFactory.createPerson(project, "Zebra");
        PersonFactory.createPerson(project, "Apple");
        final var persons = PersonFactory.getPERSONS();
        assertEquals(2, persons.size());
        assertEquals("Apple", persons.get(0).getName());
        assertEquals("Zebra", persons.get(1).getName());
    }

    /**
     * Test of reset method, of class PersonFactory.
     */
    @Test
    public void testReset() {
        PersonFactory.createPerson(project, "testReset");
        PersonFactory.reset();
        assertEquals(0, PersonFactory.getPERSONS().size());
    }

}
