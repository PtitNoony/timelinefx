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
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class FreeMapPersonTest {

    private static final long FREE_MAP_ID = 1L;
    private static final double PLOT_SEPARATION = 8.0;
    private static final double NAME_WIDTH = 100.0;
    private static final double FONT_SIZE = 12.0;

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
     * A place used in these tests.
     */
    private Place place;

    /**
     * The freemap place used in these tests.
     */
    private FreeMapPlace freeMapPlace;

    /**
     * Default constructor.
     */
    public FreeMapPersonTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
        FreeMapPerson.resetFactory();
        FreeMapPlace.resetFactory();
        FreeMapStayFactory.reset();
        FreeMapConnectorFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("FreeMapPersonTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
        FreeMapPerson.resetFactory();
        FreeMapPlace.resetFactory();
        FreeMapStayFactory.reset();
        FreeMapConnectorFactory.reset();
    }

    /**
     * Test of createFreeMapPerson method, of class FreeMapPerson.
     */
    @Test
    public void testCreateFreeMapPerson() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        assertEquals(person.getId(), freeMapPerson.getId());
        assertEquals(person, freeMapPerson.getPerson());
        assertEquals(person.getName(), freeMapPerson.getName());
        assertTrue(freeMapPerson.getFreeMapStays().isEmpty());
        assertTrue(freeMapPerson.getFreeMapPortraits().isEmpty());
    }

    /**
     * Test of createFreeMapPerson method, of class FreeMapPerson, with the same person twice.
     */
    @Test
    public void testCreateFreeMapPersonTwiceThrows() {
        FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        assertThrows(RuntimeException.class, () -> FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person));
    }

    /**
     * Regression test for a bug where removeFreeMapPerson removed from the FACTORY_CONTENT map using the
     * person's own id instead of the friezeFreeMapID the map is actually keyed by, so a removed person could
     * never be re-added to the same free map.
     */
    @Test
    public void testRemoveFreeMapPersonAllowsRecreation() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        assertTrue(FreeMapPerson.removeFreeMapPerson(freeMapPerson));
        assertDoesNotThrow(() -> FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person));
    }

    /**
     * Test of removeFreeMapPerson method, of class FreeMapPerson, with a person that was never added.
     */
    @Test
    public void testRemoveFreeMapPersonNotTracked() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        FreeMapPerson.removeFreeMapPerson(freeMapPerson);
        assertEquals(false, FreeMapPerson.removeFreeMapPerson(freeMapPerson));
    }

    /**
     * Test of addStay method, of class FreeMapPerson: registers a FreeMapStay and its start/end plots on the
     * place.
     */
    @Test
    public void testAddStayRegistersFreeMapStayAndPlots() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        freeMapPerson.addStay(stay, freeMapPlace);
        assertEquals(1, freeMapPerson.getFreeMapStays().size());
        assertEquals(2, freeMapPlace.getPlots().size());
    }

    /**
     * Test of addStay method, of class FreeMapPerson, called twice with the same StayPeriod.
     */
    @Test
    public void testAddStayIgnoresDuplicateStayPeriod() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        freeMapPerson.addStay(stay, freeMapPlace);
        freeMapPerson.addStay(stay, freeMapPlace);
        assertEquals(1, freeMapPerson.getFreeMapStays().size());
    }

    /**
     * Test of removeStay method, of class FreeMapPerson.
     */
    @Test
    public void testRemoveStay() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        freeMapPerson.addStay(stay, freeMapPlace);
        freeMapPerson.removeStay(stay);
        assertTrue(freeMapPerson.getFreeMapStays().isEmpty());
    }

    /**
     * Test of addStay method, of class FreeMapPerson, with 2 consecutive stays: a travel link should be created
     * between them.
     */
    @Test
    public void testAddTwoStaysCreatesTravelLink() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var firstStay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var secondStay = StayFactory.createStayPeriodSimpleTime(person, 10.0, 20.0, place);
        freeMapPerson.addStay(firstStay, freeMapPlace);
        freeMapPerson.addStay(secondStay, freeMapPlace);
        assertEquals(1, freeMapPerson.getFreeMapTravelLinks().size());
    }

    /**
     * Regression test: FreeMapPortrait used to fall into an UnsupportedOperationException default case for
     * FREEMAP_STAY_ADDED, reachable simply by adding a stay to a person after creating one of their portraits.
     */
    @Test
    public void testAddStayAfterCreatingPortraitDoesNotThrow() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var firstStay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        freeMapPerson.addStay(firstStay, freeMapPlace);
        freeMapPerson.createPortrait(freeMapPerson.getFreeMapStays().get(0));
        final var secondStay = StayFactory.createStayPeriodSimpleTime(person, 10.0, 20.0, place);
        assertDoesNotThrow(() -> freeMapPerson.addStay(secondStay, freeMapPlace));
    }

    /**
     * Test of setPlotsVisibilty and setPortraitConnectorsVisibilty methods, of class FreeMapPerson.
     */
    @Test
    public void testVisibilitySetters() {
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        freeMapPerson.addStay(stay, freeMapPlace);
        freeMapPerson.createPortrait(freeMapPerson.getFreeMapStays().get(0));
        assertDoesNotThrow(() -> freeMapPerson.setPlotsVisibilty(false));
        assertDoesNotThrow(() -> freeMapPerson.setPortraitConnectorsVisibilty(false));
    }

}
