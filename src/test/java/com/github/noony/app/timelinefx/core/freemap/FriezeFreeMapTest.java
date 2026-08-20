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
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import java.nio.file.Path;
import java.util.Map;
import javafx.geometry.Dimension2D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Note: {@link Frieze#addStay(com.github.noony.app.timelinefx.core.StayPeriod)} does not call
 * {@code Platform.runLater} (only {@code updatePersonSelection}/{@code updatePeopleSelection} do, neither of
 * which is exercised here), so no JavaFX toolkit bootstrap is needed for this fixture, unlike {@code FriezeTest}.
 *
 * @author hamon
 */
public final class FriezeFreeMapTest {

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
     * The frieze used in these tests.
     */
    private Frieze frieze;

    /**
     * Default constructor.
     */
    public FriezeFreeMapTest() {
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
        project = TimeLineProjectFactory.createProject("FriezeFreeMapTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
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
     * Test of the allStays constructor, of class FriezeFreeMap: picks up the frieze's places/persons/stays.
     */
    @Test
    public void testCreateWithAllStays() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        frieze.addStay(stay);
        final var freeMap = frieze.createFriezeFreeMap();
        assertEquals(1, freeMap.getPersons().size());
        assertEquals(1, freeMap.getPlaces().size());
        assertEquals(person, freeMap.getFreeMapPerson(person).getPerson());
        assertEquals(place, freeMap.getFreeMapPlace(place).getPlace());
        assertTrue(frieze.getFriezeFreeMaps().contains(freeMap));
    }

    /**
     * Test of getFreeMapPerson and getFreeMapPlace methods, of class FriezeFreeMap, with an unknown person/place.
     */
    @Test
    public void testGetFreeMapPersonUnknown() {
        final var freeMap = frieze.createFriezeFreeMap();
        final var otherPerson = PersonFactory.createPerson(project, "otherPerson");
        assertNull(freeMap.getFreeMapPerson(otherPerson));
    }

    /**
     * Test of getProperties and setProperties methods, of class FriezeFreeMap: round-trips through the typed
     * properties record.
     */
    @Test
    public void testPropertiesRoundTrip() {
        final var freeMap = frieze.createFriezeFreeMap();
        final var customProperties = new FriezeFreeMapProperties(new Dimension2D(1000.0, 800.0), 100.0, 150.0,
                14.0, 10.0, 6.0, false, false, 40.0);
        freeMap.setProperties(customProperties);
        assertEquals(customProperties, freeMap.getProperties());
        assertEquals(1000.0, freeMap.getWidth());
        assertEquals(800.0, freeMap.getHeight());
        assertFalse(freeMap.getPlotVisibility());
    }

    /**
     * Test of getParemeters method, of class FriezeFreeMap: round-trips through the String map used for XML
     * persistence.
     */
    @Test
    public void testGetParemetersRoundTrip() {
        final var freeMap = frieze.createFriezeFreeMap();
        final var parameters = freeMap.getParemeters();
        final var roundTripped = FriezeFreeMapProperties.fromParameterMap(parameters, FriezeFreeMap.DEFAULT_PROPERTIES);
        assertEquals(freeMap.getProperties(), roundTripped);
    }

    /**
     * Test of setWidth method, of class FriezeFreeMap, with a width too small to fit the persons/place-names
     * columns: updateLayout should refuse a negative drawing width.
     */
    @Test
    public void testSetWidthTooSmallThrows() {
        final var freeMap = frieze.createFriezeFreeMap();
        assertThrows(IllegalStateException.class, () -> freeMap.setWidth(1.0));
    }

    /**
     * Test of addStay method, of class Frieze, propagated to an existing FriezeFreeMap via handleFriezeChanges.
     */
    @Test
    public void testFriezeStayAddedPropagatesToFreeMap() {
        final var freeMap = frieze.createFriezeFreeMap();
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        frieze.addStay(stay);
        assertEquals(1, freeMap.getPersons().size());
        assertEquals(1, freeMap.getPlaces().size());
    }

    /**
     * Test of setPlotVisibility and setPortraitConnectorVisibility methods, of class FriezeFreeMap.
     */
    @Test
    public void testVisibilitySetters() {
        final var freeMap = frieze.createFriezeFreeMap();
        assertDoesNotThrow(() -> freeMap.setPlotVisibility(false));
        assertDoesNotThrow(() -> freeMap.setPortraitConnectorVisibility(false));
    }

    /**
     * Test of distributePlaces method, of class FriezeFreeMap.
     */
    @Test
    public void testDistributePlaces() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        frieze.addStay(stay);
        final var freeMap = frieze.createFriezeFreeMap();
        assertDoesNotThrow(freeMap::distributePlaces);
    }

}
