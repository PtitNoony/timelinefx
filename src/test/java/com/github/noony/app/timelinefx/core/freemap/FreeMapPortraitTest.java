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
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.PortraitFactory;
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

/**
 * Unit tests for {@link FreeMapPortrait}.
 *
 * @author solun
 */
public final class FreeMapPortraitTest {

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
     * The portrait radius used in these tests.
     */
    private static final double RADIUS = 30.0;

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
     * Default constructor.
     */
    public FreeMapPortraitTest() {
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
        FreeMapPortraitFactory.reset();
        FreeMapConnectorFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        final TimeLineProject project = TimeLineProjectFactory.createProject("FreeMapPortraitTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        freeMapPerson = FreeMapPerson.createFreeMapPerson(FREE_MAP_ID, person);
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
        FreeMapPortraitFactory.reset();
        FreeMapConnectorFactory.reset();
    }

    /**
     * Test of createFreeMapPortrait method, of class FreeMapPortraitFactory.
     */
    @Test
    public void testCreateFreeMapPortrait() {
        final var portrait = PortraitFactory.createPortrait(person);
        final var freeMapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(portrait, freeMapPerson, RADIUS);
        assertEquals(portrait, freeMapPortrait.getPortrait());
        assertEquals(freeMapPerson, freeMapPortrait.getPerson());
        assertEquals(RADIUS, freeMapPortrait.getRadius());
        assertEquals(0.0, freeMapPortrait.getX());
        assertEquals(0.0, freeMapPortrait.getY());
        assertEquals(person.getColor(), freeMapPortrait.getColor());
    }

    /**
     * Regression test: the connector used to be built from the radius/xPos instance fields before they were
     * assigned (still holding their Java default 0.0), instead of the aRadius constructor argument -- so the
     * underlying connector's "date" slot (which this connector repurposes to carry the radius) never reflected
     * the requested one, always reading back as 0.0 regardless of aRadius.
     */
    @Test
    public void testConnectorUsesRequestedRadius() {
        final var portrait = PortraitFactory.createPortrait(person);
        final var freeMapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(portrait, freeMapPerson, RADIUS);
        assertEquals(RADIUS, freeMapPortrait.getConnector().getDate());
    }

    /**
     * Test of setX and setY methods, of class FreeMapPortrait.
     */
    @Test
    public void testSetPosition() {
        final var portrait = PortraitFactory.createPortrait(person);
        final var freeMapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(portrait, freeMapPerson, RADIUS);
        freeMapPortrait.setX(12.0);
        freeMapPortrait.setY(34.0);
        assertEquals(12.0, freeMapPortrait.getX());
        assertEquals(34.0, freeMapPortrait.getY());
        assertEquals(12.0, freeMapPortrait.getConnector().getX());
        assertEquals(34.0, freeMapPortrait.getConnector().getY());
    }

    /**
     * Test of setRadius method, of class FreeMapPortrait.
     */
    @Test
    public void testSetRadius() {
        final var portrait = PortraitFactory.createPortrait(person);
        final var freeMapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(portrait, freeMapPerson, RADIUS);
        freeMapPortrait.setRadius(50.0);
        assertEquals(50.0, freeMapPortrait.getRadius());
    }

    /**
     * Regression test: FreeMapPortrait's handleFreeMapPersonChanges used to handle FREEMAP_STAY_REMOVED but not
     * the sibling FREEMAP_STAY_ADDED event, falling into a default case that threw UnsupportedOperationException.
     */
    @Test
    public void testDoesNotThrowWhenStayAddedAfterPortraitCreation() {
        final var portrait = PortraitFactory.createPortrait(person);
        FreeMapPortraitFactory.createFreeMapPortrait(portrait, freeMapPerson, RADIUS);
        final var place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(FREE_MAP_ID, place, PLOT_SEPARATION, NAME_WIDTH, FONT_SIZE);
        final var stay = StayFactory.createStayPeriodSimpleTime(person, 0.0, 10.0, place);
        assertDoesNotThrow(() -> freeMapPerson.addStay(stay, freeMapPlace));
    }

}
