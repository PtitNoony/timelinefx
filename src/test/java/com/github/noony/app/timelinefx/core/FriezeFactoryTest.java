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
import java.util.Collections;
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
 * @author solun
 */
public class FriezeFactoryTest {

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    public FriezeFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        FriezeFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("FriezeFactoryTest", configParams);
    }

    @AfterEach
    public void tearDown() {
        FriezeFactory.reset();
    }

    /**
     * Test of createFrieze and getFrieze methods, of class FriezeFactory.
     */
    @Test
    public void testCreateFriezeAndGetFrieze() {
        final var frieze = FriezeFactory.createFrieze(project, "testCreateFriezeAndGetFrieze");
        assertEquals(frieze, FriezeFactory.getFrieze(frieze.getId()));
        assertEquals(project, frieze.getProject());
    }

    /**
     * Test of createFrieze method, of class FriezeFactory, with an explicit stays list.
     */
    @Test
    public void testCreateFriezeWithStays() {
        final var frieze = FriezeFactory.createFrieze(project, "testCreateFriezeWithStays", Collections.emptyList());
        assertTrue(frieze.getStayPeriods().isEmpty());
    }

    /**
     * Test of createFrieze method, of class FriezeFactory, with a specific id.
     */
    @Test
    public void testCreateFriezeWithId() {
        final var frieze = FriezeFactory.createFrieze(42L, project, "testCreateFriezeWithId", Collections.emptyList());
        assertEquals(42L, frieze.getId());
    }

    /**
     * Test of createFrieze method, of class FriezeFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreateFriezeDuplicateId() {
        FriezeFactory.createFrieze(42L, project, "testCreateFriezeDuplicateIdA", Collections.emptyList());
        assertThrows(IllegalArgumentException.class,
                () -> FriezeFactory.createFrieze(42L, project, "testCreateFriezeDuplicateIdB", Collections.emptyList()));
    }

    /**
     * Test of getFrieze method, of class FriezeFactory, with an unknown id.
     */
    @Test
    public void testGetFriezeUnknownId() {
        assertNull(FriezeFactory.getFrieze(999L));
    }

    /**
     * Test of getFriezes method, of class FriezeFactory.
     */
    @Test
    public void testGetFriezes() {
        final var frieze = FriezeFactory.createFrieze(project, "testGetFriezes");
        assertTrue(FriezeFactory.getFriezes().contains(frieze));
    }

    /**
     * Test of reset method, of class FriezeFactory.
     */
    @Test
    public void testReset() {
        FriezeFactory.createFrieze(project, "testReset");
        FriezeFactory.reset();
        assertTrue(FriezeFactory.getFriezes().isEmpty());
    }

}
