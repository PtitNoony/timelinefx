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
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author solun
 */
public class PersonTest {

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    public PersonTest() {
    }

    @BeforeEach
    public void setUp() {
        PersonFactory.reset();
        PortraitFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("PersonTest", configParams);
    }

    @AfterEach
    public void tearDown() {
        PersonFactory.reset();
        PortraitFactory.reset();
    }

    /**
     * Test of getId method, of class Person.
     */
    @Test
    public void testGetId() {
        final var instance = PersonFactory.createPerson(project, 23L, "testGetId", Color.CORAL);
        assertEquals(23L, instance.getId());
    }

    /**
     * Test of getProject method, of class Person.
     */
    @Test
    public void testGetProject() {
        final var instance = PersonFactory.createPerson(project, "testGetProject");
        assertEquals(project, instance.getProject());
    }

    /**
     * Test of getName and setName methods, of class Person.
     */
    @Test
    public void testName() {
        final var instance = PersonFactory.createPerson(project, "testName");
        assertEquals("testName", instance.getName());
        instance.setName("newName");
        assertEquals("newName", instance.getName());
    }

    /**
     * Test of getColor and setColor methods, of class Person.
     */
    @Test
    public void testColor() {
        final var instance = PersonFactory.createPerson(project, "testColor", Color.CORAL);
        assertEquals(Color.CORAL, instance.getColor());
        instance.setColor(Color.OLDLACE);
        assertEquals(Color.OLDLACE, instance.getColor());
    }

    /**
     * Test of getDefaultPortrait method, of class Person: lazily created on first access.
     */
    @Test
    public void testGetDefaultPortrait() {
        final var instance = PersonFactory.createPerson(project, "testGetDefaultPortrait");
        final var portrait = instance.getDefaultPortrait();
        assertNotNull(portrait);
        assertEquals(portrait, instance.getDefaultPortrait());
    }

    /**
     * Test of addPortrait, getPortraits, getPortrait and removePortrait methods, of class Person.
     */
    @Test
    public void testPortraits() {
        final var instance = PersonFactory.createPerson(project, "testPortraits");
        assertTrue(instance.getPortraits().isEmpty());
        final var portrait = PortraitFactory.createPortrait(instance);
        instance.addPortrait(portrait);
        assertEquals(1, instance.getPortraits().size());
        assertEquals(portrait, instance.getPortrait(portrait.getId()));
        //
        instance.removePortrait(portrait);
        assertTrue(instance.getPortraits().isEmpty());
        assertNull(instance.getPortrait(portrait.getId()));
    }

    /**
     * Test of setDefaultPortrait method, of class Person.
     */
    @Test
    public void testSetDefaultPortrait() {
        final var instance = PersonFactory.createPerson(project, "testSetDefaultPortrait");
        final var portrait = PortraitFactory.createPortrait(instance);
        instance.setDefaultPortrait(portrait);
        assertEquals(portrait, instance.getDefaultPortrait());
        assertTrue(instance.getPortraits().contains(portrait));
    }

    /**
     * Test of getTimeFormat and setTimeFormat methods, of class Person.
     */
    @Test
    public void testTimeFormat() {
        final var instance = PersonFactory.createPerson(project, "testTimeFormat");
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        instance.setTimeFormat(TimeFormat.LOCAL_TIME);
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
    }

    /**
     * Test of getDateOfBirth and setDateOfBirth methods, of class Person.
     */
    @Test
    public void testDateOfBirth() {
        final var instance = PersonFactory.createPerson(project, "testDateOfBirth");
        final var dob = LocalDate.of(1990, 1, 2);
        instance.setDateOfBirth(dob);
        assertEquals(dob, instance.getDateOfBirth());
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
        assertEquals(dob.toEpochDay(), instance.getAbsolutTimeOfBirth());
    }

    /**
     * Test of getDateOfDeath and setDateOfDeath methods, of class Person.
     */
    @Test
    public void testDateOfDeath() {
        final var instance = PersonFactory.createPerson(project, "testDateOfDeath");
        final var dod = LocalDate.of(2020, 1, 2);
        instance.setDateOfDeath(dod);
        assertEquals(dod, instance.getDateOfDeath());
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
        assertEquals(dod.toEpochDay(), instance.getAbsolutTimeOfDeath());
    }

    /**
     * Test of getTimeOfBirth and setTimeOfBirth methods, of class Person.
     */
    @Test
    public void testTimeOfBirth() {
        final var instance = PersonFactory.createPerson(project, "testTimeOfBirth");
        instance.setTimeOfBirth(42L);
        assertEquals(42L, instance.getTimeOfBirth());
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertEquals(42L, instance.getAbsolutTimeOfBirth());
    }

    /**
     * Test of getTimeOfDeath and setTimeOfDeath methods, of class Person.
     */
    @Test
    public void testTimeOfDeath() {
        final var instance = PersonFactory.createPerson(project, "testTimeOfDeath");
        instance.setTimeOfDeath(99L);
        assertEquals(99L, instance.getTimeOfDeath());
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertEquals(99L, instance.getAbsolutTimeOfDeath());
    }

    /**
     * Test of setSelected and isSelected methods, of class Person.
     */
    @Test
    public void testSelected() {
        final var instance = PersonFactory.createPerson(project, "testSelected");
        assertFalse(instance.isSelected());
        instance.setSelected(true);
        assertTrue(instance.isSelected());
    }

    /**
     * Test of setVisible and isVisible methods, of class Person.
     */
    @Test
    public void testVisible() {
        final var instance = PersonFactory.createPerson(project, "testVisible");
        assertTrue(instance.isVisible());
        instance.setVisible(false);
        assertFalse(instance.isVisible());
    }

    /**
     * Test of toString method, of class Person.
     */
    @Test
    public void testToString() {
        final var instance = PersonFactory.createPerson(project, "testToString");
        assertEquals("testToString", instance.toString());
    }

    /**
     * Test of addPropertyChangeListener/removePropertyChangeListener methods, of class Person.
     */
    @Test
    public void testPropertyChangeListener() {
        final var instance = PersonFactory.createPerson(project, "testPropertyChangeListener");
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        instance.addPropertyChangeListener(listener);
        instance.setName("newName");
        assertTrue(fired[0]);
        //
        fired[0] = false;
        instance.removePropertyChangeListener(listener);
        instance.setName("anotherName");
        assertFalse(fired[0]);
    }

    /**
     * Test of COMPARATOR field, of class Person.
     */
    @Test
    public void testComparator() {
        final var zebra = PersonFactory.createPerson(project, "Zebra");
        final var apple = PersonFactory.createPerson(project, "Apple");
        assertTrue(Person.COMPARATOR.compare(apple, zebra) < 0);
        assertTrue(Person.COMPARATOR.compare(zebra, apple) > 0);
    }

}
