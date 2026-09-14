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
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Note: uses a real {@link TimeLineProject}, created in a JUnit {@code @TempDir}. Project creation copies the
 * bundled default portrait resource ({@link Person#DEFAULT_PICTURE_NAME}) into the project's portraits folder,
 * which is what makes these portrait creations resolve to a real, readable file.
 *
 * @author hamon
 */
public final class PortraitFactoryTest {

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
     * A person used in these tests.
     */
    private Person person;

    /**
     * Default constructor.
     */
    public PortraitFactoryTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        PersonFactory.reset();
        PortraitFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("PortraitFactoryTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        PersonFactory.reset();
        PortraitFactory.reset();
    }

    /**
     * Test of createPortrait(Person) method, of class PortraitFactory: uses the default picture.
     */
    @Test
    public void testCreatePortraitDefault() {
        final var portrait = PortraitFactory.createPortrait(person);
        assertEquals(person, portrait.getPerson());
        assertEquals(portrait, PortraitFactory.getPortrait(portrait.getId()));
    }

    /**
     * Test of createPortrait(Person, String) method, of class PortraitFactory: uses an explicit file path.
     */
    @Test
    public void testCreatePortraitWithFilePath() {
        final var relativePath = person.getProject().getPortraitsRelativeFolder() + File.separator + Person.DEFAULT_PICTURE_NAME;
        final var portrait = PortraitFactory.createPortrait(person, relativePath);
        assertEquals(person, portrait.getPerson());
    }

    /**
     * Test of createPortrait(long, Person, String) method, of class PortraitFactory: uses a specific id.
     */
    @Test
    public void testCreatePortraitWithId() {
        final var relativePath = person.getProject().getPortraitsRelativeFolder() + File.separator + Person.DEFAULT_PICTURE_NAME;
        final var portrait = PortraitFactory.createPortrait(42L, person, relativePath);
        assertEquals(42L, portrait.getId());
    }

    /**
     * Test of createPortrait(Person) method, of class PortraitFactory, with the project's default (LOCAL_TIME)
     * time format: the portrait's date is taken from the picture's own (parsed, or metadata-parser-default)
     * creation date, not from an unrelated default value.
     */
    @Test
    public void testCreatePortraitUsesLocalTimeFormat() {
        final var portrait = PortraitFactory.createPortrait(person);
        assertEquals(TimeFormat.LOCAL_TIME, portrait.getTimeFormat());
        assertEquals(LocalDate.MIN, portrait.getDate());
    }

    /**
     * Test of createPortrait(Person) method, of class PortraitFactory, with the project set to the TIME_MIN
     * time format: the portrait's timestamp is derived from the picture's own creation date.
     */
    @Test
    public void testCreatePortraitUsesTimeMinFormat() {
        project.setTimeFormat(TimeFormat.TIME_MIN);
        final var portrait = PortraitFactory.createPortrait(person);
        assertEquals(TimeFormat.TIME_MIN, portrait.getTimeFormat());
        assertEquals(LocalDate.MIN.toEpochDay(), portrait.getTimestamp());
    }

    /**
     * Test of getPortrait method, of class PortraitFactory, with an unknown id.
     */
    @Test
    public void testGetPortraitUnknownId() {
        assertNull(PortraitFactory.getPortrait(999L));
    }

    /**
     * Test of getPortraits method, of class PortraitFactory.
     */
    @Test
    public void testGetPortraits() {
        final var portrait = PortraitFactory.createPortrait(person);
        assertEquals(1, PortraitFactory.getPortraits().size());
        assertEquals(portrait, PortraitFactory.getPortraits().get(0));
    }

    /**
     * Test of reset method, of class PortraitFactory.
     */
    @Test
    public void testReset() {
        PortraitFactory.createPortrait(person);
        PortraitFactory.reset();
        assertEquals(0, PortraitFactory.getPortraits().size());
    }

}
