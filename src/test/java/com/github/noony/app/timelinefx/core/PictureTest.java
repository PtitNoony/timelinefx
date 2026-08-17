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

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.io.TempDir;

/**
 * Note: also covers the {@link AbstractPicture} behavior shared with {@link Portrait}, since
 * {@code AbstractPicture} is abstract and {@link Picture} does not override most of it.
 *
 * @author solun
 */
public final class PictureTest {

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    private Person personA;

    private Person personB;

    private Place place;

    public PictureTest() {
    }

    @BeforeEach
    public void setUp() {
        PictureFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("PictureTest", configParams);
        personA = PersonFactory.createPerson(project, "personA");
        personB = PersonFactory.createPerson(project, "personB");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
    }

    @AfterEach
    public void tearDown() {
        PictureFactory.reset();
        PersonFactory.reset();
        PlaceFactory.reset();
    }

    private Picture createPicture() {
        return PictureFactory.createPicture(project, 7L, "testPicture", LocalDateTime.of(2023, 1, 2, 3, 4, 5), "pictures/foo.png", 640, 480);
    }

    /**
     * Test of getId method, of class Picture.
     */
    @Test
    public void testGetId() {
        assertEquals(7L, createPicture().getId());
    }

    /**
     * Test of getProject method, of class Picture.
     */
    @Test
    public void testGetProject() {
        assertEquals(project, createPicture().getProject());
    }

    /**
     * Test of getName and setName methods, of class Picture.
     */
    @Test
    public void testName() {
        final var instance = createPicture();
        assertEquals("testPicture", instance.getName());
        instance.setName("newName");
        assertEquals("newName", instance.getName());
    }

    /**
     * Test of getWidth and getHeight methods, of class Picture.
     */
    @Test
    public void testDimensions() {
        final var instance = createPicture();
        assertEquals(640, instance.getWidth());
        assertEquals(480, instance.getHeight());
    }

    /**
     * Test of getProjectRelativePath and getAbsolutePath methods, of class Picture.
     */
    @Test
    public void testPaths() {
        final var instance = createPicture();
        assertEquals("pictures/foo.png", instance.getProjectRelativePath());
        assertEquals(project.getProjectFolder().getAbsolutePath() + File.separator + "pictures/foo.png", instance.getAbsolutePath());
    }

    /**
     * Test of addPerson, getPersons and removePerson methods, of class Picture.
     */
    @Test
    public void testPersons() {
        final var instance = createPicture();
        assertTrue(instance.getPersons().isEmpty());
        assertTrue(instance.addPerson(personA));
        assertFalse(instance.addPerson(personA));
        assertEquals(1, instance.getPersons().size());
        assertTrue(instance.removePerson(personA));
        assertFalse(instance.removePerson(personA));
        assertTrue(instance.getPersons().isEmpty());
    }

    /**
     * Test of addPlace, getPlaces and removePlace methods, of class Picture.
     */
    @Test
    public void testPlaces() {
        final var instance = createPicture();
        assertTrue(instance.getPlaces().isEmpty());
        assertTrue(instance.addPlace(place));
        assertFalse(instance.addPlace(place));
        assertEquals(1, instance.getPlaces().size());
        assertTrue(instance.removePlace(place));
        assertFalse(instance.removePlace(place));
        assertTrue(instance.getPlaces().isEmpty());
    }

    /**
     * Test of movePersonUp and movePersonDown methods, of class Picture.
     */
    @Test
    public void testMovePerson() {
        final var instance = createPicture();
        instance.addPerson(personA);
        instance.addPerson(personB);
        assertEquals(personA, instance.getPersons().get(0));
        assertEquals(personB, instance.getPersons().get(1));
        //
        instance.movePersonUp(personB);
        assertEquals(personB, instance.getPersons().get(0));
        assertEquals(personA, instance.getPersons().get(1));
        //
        instance.movePersonDown(personB);
        assertEquals(personA, instance.getPersons().get(0));
        assertEquals(personB, instance.getPersons().get(1));
    }

    /**
     * Test of getTimeFormat, getDate and setDate methods, of class Picture.
     */
    @Test
    public void testDate() {
        final var instance = createPicture();
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
        assertEquals(LocalDate.of(2023, 1, 2), instance.getDate());
        final var newDate = LocalDate.of(2024, 5, 6);
        instance.setDate(newDate);
        assertEquals(newDate, instance.getDate());
    }

    /**
     * Test of setTimestamp method, of class Picture: switches to TIME_MIN.
     */
    @Test
    public void testSetTimestamp() {
        final var instance = createPicture();
        instance.setTimestamp(99.0);
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertEquals(99.0, instance.getTimestamp());
        assertEquals(99.0, instance.getAbsoluteTime());
    }

    /**
     * Test of setValue method, of class Picture, with a LOCAL_TIME instance.
     */
    @Test
    public void testSetValue() {
        final var instance = createPicture();
        instance.setValue("2024-05-06");
        assertEquals(LocalDate.of(2024, 5, 6), instance.getDate());
    }

    /**
     * Test of getAbsoluteTimeAsString method, of class Picture.
     */
    @Test
    public void testGetAbsoluteTimeAsString() {
        assertFalse(createPicture().getAbsoluteTimeAsString().isEmpty());
    }

    /**
     * Test of addPropertyChangeListener/removePropertyChangeListener methods, of class Picture.
     */
    @Test
    public void testPropertyChangeListener() {
        final var instance = createPicture();
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
     * Test of toString method, of class Picture.
     */
    @Test
    public void testToString() {
        assertEquals("Pic[testPicture]", createPicture().toString());
    }

}
