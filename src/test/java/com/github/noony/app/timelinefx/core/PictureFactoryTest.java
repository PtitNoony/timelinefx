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
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.io.TempDir;

/**
 * Note: uses a real {@link TimeLineProject}, created in a JUnit {@code @TempDir}. Only the id-based
 * {@code createPicture} overload is exercised; the file-copying overload needs a real, parseable image file
 * and is left to integration testing.
 *
 * @author solun
 */
public final class PictureFactoryTest {

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    public PictureFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        PictureFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("PictureFactoryTest", configParams);
    }

    @AfterEach
    public void tearDown() {
        PictureFactory.reset();
    }

    /**
     * Test of createPicture and getPicture methods, of class PictureFactory.
     */
    @Test
    public void testCreatePictureAndGetPicture() {
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        final var picture = PictureFactory.createPicture(project, 7L, "testCreatePictureAndGetPicture", creationDate, "pictures/foo.png", 640, 480);
        assertEquals(picture, PictureFactory.getPicture(7L));
        assertEquals(640, picture.getWidth());
        assertEquals(480, picture.getHeight());
    }

    /**
     * Test of getPicture method, of class PictureFactory, with an unknown id.
     */
    @Test
    public void testGetPictureUnknownId() {
        assertNull(PictureFactory.getPicture(999L));
    }

    /**
     * Test of createPicture method, of class PictureFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreatePictureDuplicateId() {
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        PictureFactory.createPicture(project, 7L, "testCreatePictureDuplicateIdA", creationDate, "pictures/foo.png", 640, 480);
        assertThrows(IllegalArgumentException.class,
                () -> PictureFactory.createPicture(project, 7L, "testCreatePictureDuplicateIdB", creationDate, "pictures/bar.png", 640, 480));
    }

    /**
     * Test of getPictures method, of class PictureFactory.
     */
    @Test
    public void testGetPictures() {
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        final var picture = PictureFactory.createPicture(project, 7L, "testGetPictures", creationDate, "pictures/foo.png", 640, 480);
        assertTrue(PictureFactory.getPictures().contains(picture));
    }

    /**
     * Test of addPropertyChangeListener method, of class PictureFactory: fires PICTURE_ADDED.
     */
    @Test
    public void testAddPropertyChangeListener() {
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> {
            if (PictureFactory.PICTURE_ADDED.equals(e.getPropertyName())) {
                fired[0] = true;
            }
        };
        PictureFactory.addPropertyChangeListener(listener);
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        PictureFactory.createPicture(project, 7L, "testAddPropertyChangeListener", creationDate, "pictures/foo.png", 640, 480);
        assertTrue(fired[0]);
        //
        fired[0] = false;
        PictureFactory.removePropertyChangeListener(listener);
        PictureFactory.createPicture(project, 8L, "testAddPropertyChangeListener2", creationDate, "pictures/bar.png", 640, 480);
        assertTrue(!fired[0]);
    }

    /**
     * Test of reset method, of class PictureFactory.
     */
    @Test
    public void testReset() {
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        PictureFactory.createPicture(project, 7L, "testReset", creationDate, "pictures/foo.png", 640, 480);
        PictureFactory.reset();
        assertTrue(PictureFactory.getPictures().isEmpty());
    }

}
