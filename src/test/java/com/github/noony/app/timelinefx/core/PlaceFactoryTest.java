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

import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PlaceFactory}.
 *
 * @author hamon
 */
public final class PlaceFactoryTest {

    /**
     * Default constructor.
     */
    public PlaceFactoryTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        PlaceFactory.reset();
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        PlaceFactory.reset();
    }

    /**
     * Test of createPlace and getPlace methods, of class PlaceFactory.
     */
    @Test
    public void testCreatePlaceAndGetPlace() {
        final var place = PlaceFactory.createPlace("testCreatePlaceAndGetPlace", PlaceLevel.PLANET, null);
        assertEquals(place, PlaceFactory.getPlace(place.getId()));
    }

    /**
     * Test of getPlace method, of class PlaceFactory, with an unknown id.
     */
    @Test
    public void testGetPlaceUnknownId() {
        assertNull(PlaceFactory.getPlace(999L));
    }

    /**
     * Test of createPlace method, of class PlaceFactory, with a specific id and color.
     */
    @Test
    public void testCreatePlaceWithIdAndColor() {
        final var place = PlaceFactory.createPlace(42L, "testCreatePlaceWithIdAndColor", PlaceLevel.PLANET, null, Color.CORAL);
        assertEquals(42L, place.getId());
        assertEquals(Color.CORAL, place.getColor());
    }

    /**
     * Test of createPlace method, of class PlaceFactory, rejecting a duplicate id.
     */
    @Test
    public void testCreatePlaceDuplicateId() {
        PlaceFactory.createPlace(42L, "testCreatePlaceDuplicateIdA", PlaceLevel.PLANET, null, Color.CORAL);
        assertThrows(IllegalArgumentException.class,
                () -> PlaceFactory.createPlace(42L, "testCreatePlaceDuplicateIdB", PlaceLevel.PLANET, null, Color.CORAL));
    }

    /**
     * Test of getPlaces method, of class PlaceFactory: sorted by name.
     */
    @Test
    public void testGetPlaces() {
        PlaceFactory.createPlace("Zebra", PlaceLevel.PLANET, null);
        PlaceFactory.createPlace("Apple", PlaceLevel.PLANET, null);
        final var places = PlaceFactory.getPlaces();
        assertEquals(2, places.size());
        assertEquals("Apple", places.get(0).getName());
        assertEquals("Zebra", places.get(1).getName());
    }

    /**
     * Test of getRootPlaces method, of class PlaceFactory.
     */
    @Test
    public void testGetRootPlaces() {
        final var rootPlace = PlaceFactory.createPlace("testGetRootPlacesRoot", PlaceLevel.PLANET, null);
        PlaceFactory.createPlace("testGetRootPlacesChild", PlaceLevel.TOWN, rootPlace);
        assertTrue(PlaceFactory.getRootPlaces().contains(rootPlace));
    }

    /**
     * Test of reset method, of class PlaceFactory.
     */
    @Test
    public void testReset() {
        PlaceFactory.createPlace("testReset", PlaceLevel.PLANET, null);
        PlaceFactory.reset();
        assertTrue(PlaceFactory.getPlaces().isEmpty());
        assertTrue(PlaceFactory.getRootPlaces().isEmpty());
    }

}
