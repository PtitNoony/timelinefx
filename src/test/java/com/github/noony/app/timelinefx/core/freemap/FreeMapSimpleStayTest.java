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
import com.github.noony.app.timelinefx.core.freemap.links.LinkType;
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
 * @author solun
 */
public final class FreeMapSimpleStayTest {

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
     * A person used in these tests.
     */
    private Person person;

    /**
     * The freemap person used in these tests.
     */
    private FreeMapPerson freeMapPerson;

    /**
     * The freemap place used in these tests.
     */
    private FreeMapPlace freeMapPlace;

    /**
     * A core place used in these tests.
     */
    private Place place;

    /**
     * Default constructor.
     */
    public FreeMapSimpleStayTest() {
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
        final TimeLineProject project = TimeLineProjectFactory.createProject("FreeMapSimpleStayTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
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

    /**
     * Test of createFreeMapStay method, of class FreeMapStayFactory.
     */
    @Test
    public void testCreateFreeMapStay() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(stay, freeMapPerson, freeMapPlace);
        assertEquals(freeMapPerson, freeMapStay.getPerson());
        assertEquals(freeMapPlace, freeMapStay.getPlace());
        assertEquals(0.0, freeMapStay.getStartDate());
        assertEquals(10.0, freeMapStay.getEndDate());
        assertEquals(1, freeMapStay.getStayPeriods().size());
        assertTrue(freeMapStay.getFreeMapStayPeriods().isEmpty());
        assertTrue(freeMapStay.containsStay(stay));
        assertEquals(LinkType.STAY, freeMapStay.getType());
        assertEquals(person.getColor(), freeMapStay.getColor());
    }

    /**
     * Test of setSelected method, of class FreeMapSimpleStay.
     */
    @Test
    public void testSetSelected() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(stay, freeMapPerson, freeMapPlace);
        assertFalse(freeMapStay.isSelected());
        final var fired = new boolean[]{false};
        freeMapStay.addPropertyChangeListener(e -> fired[0] = true);
        freeMapStay.setSelected(true);
        assertTrue(freeMapStay.isSelected());
        assertTrue(fired[0]);
    }

    /**
     * Test of setLinkShape method, of class FreeMapSimpleStay: unconditionally throws.
     */
    @Test
    public void testSetLinkShapeThrows() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(stay, freeMapPerson, freeMapPlace);
        assertThrows(UnsupportedOperationException.class, () -> freeMapStay.setLinkShape(null));
    }

    /**
     * Test of addIntermediateConnector and removeIntermediateConnector methods, of class FreeMapSimpleStay.
     */
    @Test
    public void testIntermediateConnectors() {
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(stay, freeMapPerson, freeMapPlace);
        final var connector = FreeMapConnectorFactory.createFreeMapLinkConnector(IFileObject.NO_ID, freeMapStay, 5.0, 4.0);
        freeMapStay.addIntermediateConnector(connector);
        assertEquals(1, freeMapStay.getIntermediateConnectors().size());
        freeMapStay.removeIntermediateConnector(connector);
        assertTrue(freeMapStay.getIntermediateConnectors().isEmpty());
    }

}
