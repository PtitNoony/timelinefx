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

package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.PlotType;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Note: no {@link FriezeFreeMap} is registered for {@code FREE_MAP_ID} here since these tests only exercise
 * {@link FreeMapPlace} in isolation; the duplicate-creation test therefore only asserts that some
 * {@link RuntimeException} is thrown.
 *
 * @author solun
 */
public final class FreeMapPlaceTest {

    /**
     * An arbitrary free map id; no real {@link FriezeFreeMap} is registered under it.
     */
    private static final long FREE_MAP_ID = 1L;

    /**
     * The plot separation used when creating the fixture's FreeMapPlace.
     */
    private static final double PLOT_SEPARATION = 8.0;

    /**
     * The place name width used when creating the fixture's FreeMapPlace.
     */
    private static final double NAME_WIDTH = 100.0;

    /**
     * The font size used when creating the fixture's FreeMapPlace.
     */
    private static final double FONT_SIZE = 12.0;

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * A place used in these tests.
     */
    private Place place;

    /**
     * Default constructor.
     */
    public FreeMapPlaceTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
        FreeMapPlace.resetFactory();
        FreeMapPerson.resetFactory();
        FreeMapConnectorFactory.reset();
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
        FreeMapPlace.resetFactory();
        FreeMapPerson.resetFactory();
        FreeMapConnectorFactory.reset();
    }

    /**
     * Test of createFreeMapPlace method, of class FreeMapPlace.
     */
    @Test
    public void testCreateFreeMapPlace() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        assertEquals(place.getId(), freeMapPlace.getId());
        assertEquals(place, freeMapPlace.getPlace());
        assertEquals(place.getName(), freeMapPlace.getName());
        assertEquals(NAME_WIDTH, freeMapPlace.getNameWidth());
        assertEquals(FONT_SIZE, freeMapPlace.getFontSize());
        assertEquals(PLOT_SEPARATION, freeMapPlace.getPlotSeparation());
        assertTrue(freeMapPlace.getPersons().isEmpty());
        assertTrue(freeMapPlace.getPlots().isEmpty());
    }

    /**
     * Test of createFreeMapPlace method, of class FreeMapPlace, with the same place twice.
     */
    @Test
    public void testCreateFreeMapPlaceTwiceThrows() {
        FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        assertThrows(RuntimeException.class,
                () -> FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE));
    }

    /**
     * Test of setFontSize and setPlotSeparation methods, of class FreeMapPlace.
     */
    @Test
    public void testSetters() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        freeMapPlace.setFontSize(20.0);
        assertEquals(20.0, freeMapPlace.getFontSize());
        freeMapPlace.setPlotSeparation(30.0);
        assertEquals(30.0, freeMapPlace.getPlotSeparation());
        freeMapPlace.setHeight(50.0);
        assertEquals(50.0, freeMapPlace.getHeight());
    }

    /**
     * Test of registerFreeMapPlot method, of class FreeMapPlace: registering a plot tracks its person and
     * positions it below the place's current Y.
     */
    @Test
    public void testRegisterFreeMapPlot() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var freeMapPerson = newFreeMapPerson("testPerson");
        final var plot = FreeMapConnectorFactory.createPlot(freeMapPerson, freeMapPlace, 10.0, PlotType.START, 1L, 4.0);
        freeMapPlace.registerFreeMapPlot(plot);
        assertTrue(freeMapPlace.getPersons().contains(freeMapPerson));
        assertEquals(1, freeMapPlace.getPlots().size());
        assertEquals(PLOT_SEPARATION, plot.getY());
    }

    /**
     * Test of setY method, of class FreeMapPlace: moving the place shifts its registered plots by the same delta.
     */
    @Test
    public void testSetYCascadesToRegisteredPlots() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var freeMapPerson = newFreeMapPerson("testPerson");
        final var plot = FreeMapConnectorFactory.createPlot(freeMapPerson, freeMapPlace, 10.0, PlotType.START, 1L, 4.0);
        freeMapPlace.registerFreeMapPlot(plot);
        final var yBeforeMove = plot.getY();
        freeMapPlace.setY(100.0);
        assertEquals(yBeforeMove + 100.0, plot.getY());
    }

    /**
     * Test of setPersonOrder method, of class FreeMapPlace, with an array missing an already-tracked person.
     */
    @Test
    public void testSetPersonOrderFailsWhenMissingTrackedPerson() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var freeMapPerson = newFreeMapPerson("testPerson");
        final var plot = FreeMapConnectorFactory.createPlot(freeMapPerson, freeMapPlace, 10.0, PlotType.START, 1L, 4.0);
        freeMapPlace.registerFreeMapPlot(plot);
        assertFalse(freeMapPlace.setPersonOrder(new FreeMapPerson[0]));
    }

    /**
     * Test of setPersonOrder method, of class FreeMapPlace, with a valid reordering.
     */
    @Test
    public void testSetPersonOrderSucceeds() {
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var freeMapPerson = newFreeMapPerson("testPerson");
        final var plot = FreeMapConnectorFactory.createPlot(freeMapPerson, freeMapPlace, 10.0, PlotType.START, 1L, 4.0);
        freeMapPlace.registerFreeMapPlot(plot);
        assertTrue(freeMapPlace.setPersonOrder(new FreeMapPerson[]{freeMapPerson}));
    }

    private FreeMapPerson newFreeMapPerson(final String name) {
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final TimeLineProject project = TimeLineProjectFactory.createProject("FreeMapPlaceTest", configParams);
        final var person = PersonFactory.createPerson(project, name);
        return FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
    }

}
