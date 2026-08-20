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
 * Note: {@link TimeLineProjectFactory#createProject(String, Map)} is exercised here with an explicit
 * {@link TimeLineProject#PROJECT_FOLDER_KEY} pointing at a JUnit {@code @TempDir}, so it never falls back to
 * {@code Configuration.getProjectsParentFolder()} (which reads/writes a preferences file under the user's home
 * directory). It does still create real folders under the temp directory, which JUnit cleans up automatically.
 *
 * @author hamon
 */
public final class TimeLineProjectFactoryTest {

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * Default constructor.
     */
    public TimeLineProjectFactoryTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
    }

    private TimeLineProject createTestProject(final String name) {
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.resolve(name).toString());
        return TimeLineProjectFactory.createProject(name, configParams);
    }

    /**
     * Test of createProject method, of class TimeLineProjectFactory.
     */
    @Test
    public void testCreateProject() {
        final var instance = createTestProject("testCreateProject");
        assertEquals("testCreateProject", instance.getName());
        assertTrue(instance.getProjectFolder().exists());
        assertTrue(instance.getPortraitsAbsoluteFolder().exists());
        assertTrue(instance.getPicturesFolder().exists());
        assertTrue(instance.getMiniaturesFolder().exists());
    }

}
