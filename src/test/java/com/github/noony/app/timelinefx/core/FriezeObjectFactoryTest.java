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
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class FriezeObjectFactoryTest {

    @TempDir
    private Path tempDir;

    public FriezeObjectFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
    }

    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
    }

    /**
     * Test of reset method, of class FriezeObjectFactory: resets every core factory.
     */
    @Test
    public void testReset() {
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final var project = TimeLineProjectFactory.createProject("FriezeObjectFactoryTest", configParams);
        PlaceFactory.createPlace("testReset", PlaceLevel.PLANET, null);
        PersonFactory.createPerson(project, "testReset");
        PictureFactory.createPicture(project, 0L, "testReset", LocalDateTime.now(), "pictures/foo.png", 640, 480);
        //
        FriezeObjectFactory.reset();
        //
        assertTrue(PlaceFactory.getPlaces().isEmpty());
        assertTrue(PersonFactory.getPERSONS().isEmpty());
        assertTrue(PictureFactory.getPictures().isEmpty());
    }

}
