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
import com.github.noony.app.timelinefx.core.IFileObject;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FreeMapMergedStay}.
 *
 * @author solun
 */
public final class FreeMapMergedStayTest {

    /**
     * An arbitrary free map id used to scope the fixture's FreeMapPerson/FreeMapPlace.
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
     * A person used in these tests.
     */
    private Person person;

    /**
     * A second person used in these tests.
     */
    private Person otherPerson;

    /**
     * The freemap person used in these tests.
     */
    private FreeMapPerson freeMapPerson;

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
    public FreeMapMergedStayTest() {
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
        final TimeLineProject project = TimeLineProjectFactory.createProject("FreeMapMergedStayTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        otherPerson = PersonFactory.createPerson(project, "otherPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
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

    private FreeMapStay newFreeMapStay(final Person aPerson, final FreeMapPerson aFreeMapPerson, final double start, final double end, final Place aPlace, final FreeMapPlace aFreeMapPlace) {
        final var stay = StayFactory.createStayPeriodSimpleTime(aPerson, start, end, aPlace);
        return FreeMapStayFactory.createFreeMapStay(stay, aFreeMapPerson, aFreeMapPlace);
    }

    /**
     * Test of createFreeMapStay(FreeMapStay...) method, of class FreeMapStayFactory, with no stay.
     */
    @Test
    public void testCreateWithNoStaysThrows() {
        assertThrows(IllegalStateException.class, () -> FreeMapStayFactory.createFreeMapStay());
    }

    /**
     * Test of createFreeMapStay(FreeMapStay...) method, of class FreeMapStayFactory, merging 2 stays.
     */
    @Test
    public void testCreateMergedStay() {
        final var firstStay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, place, freeMapPlace);
        final var secondStay = newFreeMapStay(person, freeMapPerson, 10.0, 20.0, place, freeMapPlace);
        final var merged = FreeMapStayFactory.createFreeMapStay(firstStay, secondStay);
        assertEquals(2, merged.getNumberOfMergedStays());
        assertEquals(0.0, merged.getStartDate());
        assertEquals(20.0, merged.getEndDate());
        assertEquals(freeMapPerson, merged.getPerson());
        assertEquals(2, merged.getFreeMapStayPeriods().size());
    }

    /**
     * Test of addStay method, of class FreeMapMergedStay, with a stay belonging to a different person.
     */
    @Test
    public void testAddStayDifferentPersonThrows() {
        final var firstStay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, place, freeMapPlace);
        final var merged = FreeMapStayFactory.createFreeMapStay(firstStay);
        final var otherFreeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, otherPerson);
        final var otherPersonStay = newFreeMapStay(otherPerson, otherFreeMapPerson, 10.0, 20.0, place, freeMapPlace);
        assertThrows(IllegalStateException.class, () -> merged.addStay(otherPersonStay));
    }

    /**
     * Test of removeStay method, of class FreeMapMergedStay, with the last remaining stay: refused.
     */
    @Test
    public void testRemoveLastStayFails() {
        final var firstStay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, place, freeMapPlace);
        final var merged = FreeMapStayFactory.createFreeMapStay(firstStay);
        assertFalse(merged.removeStay(firstStay));
    }

    /**
     * Test of removeStay method, of class FreeMapMergedStay, recomputing the earliest/latest dates afterward.
     */
    @Test
    public void testRemoveStayRecomputesDates() {
        final var firstStay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, place, freeMapPlace);
        final var secondStay = newFreeMapStay(person, freeMapPerson, 10.0, 20.0, place, freeMapPlace);
        final var merged = FreeMapStayFactory.createFreeMapStay(firstStay, secondStay);
        assertTrue(merged.removeStay(secondStay));
        assertEquals(1, merged.getNumberOfMergedStays());
        assertEquals(10.0, merged.getEndDate());
    }

    /**
     * Regression test: FreeMapMergedStay.addStay's forcedPlace consistency check had an inverted condition.
     * Place.encompasses(other) returns true when this place IS other or an ancestor of it. childPlace is nested
     * under parentPlace, so parentPlace.encompasses(childPlace) is true -- a stay placed at childPlace is a
     * legitimate leaf of a merged stay forced to represent parentPlace, and construction should succeed.
     */
    @Test
    public void testForcedPlaceConstructionWithEncompassedStaySucceeds() {
        final var parentPlace = PlaceFactory.createPlace("parentPlace", PlaceLevel.SYSTEM, null);
        final var childPlace = PlaceFactory.createPlace("childPlace", PlaceLevel.PLANET, parentPlace);
        final var parentFreeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, parentPlace, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var childFreeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, childPlace, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var stay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, childPlace, childFreeMapPlace);
        assertDoesNotThrow(() -> new FreeMapMergedStay(100L, IFileObject.NO_ID, IFileObject.NO_ID, IFileObject.NO_ID, parentFreeMapPlace, stay));
    }

    /**
     * Regression test (see {@link #testForcedPlaceConstructionWithEncompassedStaySucceeds}): a stay placed
     * somewhere NOT encompassed by the forced place must be rejected.
     */
    @Test
    public void testForcedPlaceConstructionWithUnrelatedStayThrows() {
        final var parentPlace = PlaceFactory.createPlace("parentPlace", PlaceLevel.SYSTEM, null);
        final var parentFreeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, parentPlace, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var unrelatedStay = newFreeMapStay(person, freeMapPerson, 0.0, 10.0, place, freeMapPlace);
        assertThrows(IllegalStateException.class,
                () -> new FreeMapMergedStay(100L, IFileObject.NO_ID, IFileObject.NO_ID, IFileObject.NO_ID, parentFreeMapPlace, unrelatedStay));
    }

}
