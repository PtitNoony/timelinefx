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

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FriezeFreeMapFactory}.
 *
 * @author hamon
 */
public final class FriezeFreeMapFactoryTest {

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * The frieze used in these tests.
     */
    private Frieze frieze;

    /**
     * Default constructor.
     */
    public FriezeFreeMapFactoryTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
        FriezeFactory.reset();
        FriezeFreeMapFactory.reset();
        FreeMapPerson.resetFactory();
        FreeMapPlace.resetFactory();
        FreeMapStayFactory.reset();
        FreeMapConnectorFactory.reset();
        FreeMapDateHandle.resetFactory();
        FreeMapLinkFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final TimeLineProject project = TimeLineProjectFactory.createProject("FriezeFreeMapFactoryTest", configParams);
        frieze = FriezeFactory.createFrieze(project, "testFrieze");
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
        FriezeFactory.reset();
        FriezeFreeMapFactory.reset();
        FreeMapPerson.resetFactory();
        FreeMapPlace.resetFactory();
        FreeMapStayFactory.reset();
        FreeMapConnectorFactory.reset();
        FreeMapDateHandle.resetFactory();
        FreeMapLinkFactory.reset();
    }

    /**
     * Test of createFriezeFreeMap(Frieze, boolean) method, of class FriezeFreeMapFactory.
     */
    @Test
    public void testCreateFriezeFreeMap() {
        final var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(frieze, true);
        assertEquals(frieze, freeMap.getFrieze());
        assertTrue(FriezeFreeMapFactory.getFriezeFreeMaps().contains(freeMap));
        assertEquals(freeMap, FriezeFreeMapFactory.getFriezeFreeMap(freeMap.getId()));
    }

    /**
     * Test of createFriezeFreeMap(long, Frieze, boolean) method, of class FriezeFreeMapFactory, with an
     * already-used id.
     */
    @Test
    public void testCreateFriezeFreeMapWithExistingIdThrows() {
        final var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(frieze, true);
        assertThrows(IllegalArgumentException.class,
                () -> FriezeFreeMapFactory.createFriezeFreeMap(freeMap.getId(), frieze, true));
    }

    /**
     * Test of createFriezeFreeMap with explicit content, of class FriezeFreeMapFactory: it uses the supplied
     * lists rather than pulling live data off the frieze.
     */
    @Test
    public void testCreateFriezeFreeMapWithExplicitContent() {
        final var place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        final var person = PersonFactory.createPerson(frieze.getProject(), "testPerson");
        StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(1L, person);
        final var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(2L, frieze, FriezeFreeMap.DEFAULT_PROPERTIES,
                java.util.List.of(), java.util.List.of(freeMapPerson), java.util.List.of(), java.util.List.of());
        assertEquals(1, freeMap.getPersons().size());
        assertTrue(freeMap.getPersons().contains(freeMapPerson));
        assertTrue(freeMap.getPlaces().isEmpty());
    }

    /**
     * Test of duplicateFriezeFreeMap(FriezeFreeMap) method, of class FriezeFreeMapFactory: the copy has the same
     * structure as the source, but is built from fully independent objects.
     */
    @Test
    public void testDuplicateFriezeFreeMap() {
        final var place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        final var person = PersonFactory.createPerson(frieze.getProject(), "testPerson");
        final var stayPeriod = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var sourceID = 10L;
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(sourceID, place, FriezeFreeMap.DEFAULT_PROPERTIES.plotSeparation(),
                FriezeFreeMap.DEFAULT_PROPERTIES.placeNameWidth(), FriezeFreeMap.DEFAULT_PROPERTIES.fontSize());
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(sourceID, person);
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(stayPeriod, freeMapPerson, freeMapPlace);
        final var source = FriezeFreeMapFactory.createFriezeFreeMap(sourceID, frieze, FriezeFreeMap.DEFAULT_PROPERTIES,
                java.util.List.of(), java.util.List.of(freeMapPerson), java.util.List.of(freeMapPlace), java.util.List.of(freeMapStay));
        source.setName("Source");
        //
        final var duplicate = FriezeFreeMapFactory.duplicateFriezeFreeMap(source);
        //
        assertEquals("Source (copy)", duplicate.getName());
        assertTrue(duplicate.getId() != source.getId());
        assertEquals(source.getPersons().size(), duplicate.getPersons().size());
        assertEquals(source.getPlaces().size(), duplicate.getPlaces().size());
        //
        final var duplicatePerson = duplicate.getPersons().get(0);
        final var duplicatePlace = duplicate.getPlaces().get(0);
        assertTrue(duplicatePerson != freeMapPerson);
        assertTrue(duplicatePlace != freeMapPlace);
        assertEquals(freeMapPerson.getPerson(), duplicatePerson.getPerson());
        assertEquals(freeMapPlace.getPlace(), duplicatePlace.getPlace());
        assertEquals(freeMapPerson.getFreeMapStays().size(), duplicatePerson.getFreeMapStays().size());
        //
        // editing the duplicate must not affect the source
        duplicatePlace.setY(123.0);
        assertTrue(freeMapPlace.getYPos() != duplicatePlace.getYPos());
    }

    /**
     * Test of reset method, of class FriezeFreeMapFactory.
     */
    @Test
    public void testReset() {
        FriezeFreeMapFactory.createFriezeFreeMap(frieze, true);
        FriezeFreeMapFactory.reset();
        assertTrue(FriezeFreeMapFactory.getFriezeFreeMaps().isEmpty());
    }

}
