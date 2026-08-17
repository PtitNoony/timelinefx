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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Note: exercised with {@link Place} as the concrete {@link FriezeObject}, since it does not require a
 * {@link TimeLineProject}.
 *
 * @author solun
 */
public class FactoryTest {

    private Factory<Place> instance;

    public FactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        PlaceFactory.reset();
        instance = new Factory<>();
    }

    @AfterEach
    public void tearDown() {
        PlaceFactory.reset();
    }

    /**
     * Test of getNextID method, of class Factory.
     */
    @Test
    public void testGetNextID() {
        assertEquals(0L, instance.getNextID());
        assertEquals(1L, instance.getNextID());
    }

    /**
     * Test of addObject and get methods, of class Factory.
     */
    @Test
    public void testAddObjectAndGet() {
        final var place = PlaceFactory.createPlace(7L, "testAddObjectAndGet", PlaceLevel.PLANET, null, null);
        instance.addObject(place);
        assertEquals(place, instance.get(7L));
    }

    /**
     * Test of get method, of class Factory, with an unknown id.
     */
    @Test
    public void testGetUnknownId() {
        assertNull(instance.get(999L));
    }

    /**
     * Test of addObject method, of class Factory, rejecting a duplicate id.
     */
    @Test
    public void testAddObjectDuplicateId() {
        final var place = PlaceFactory.createPlace(7L, "testAddObjectDuplicateId", PlaceLevel.PLANET, null, null);
        instance.addObject(place);
        assertThrows(IllegalStateException.class, () -> instance.addObject(place));
    }

    /**
     * Test of isIdAvailable method, of class Factory.
     */
    @Test
    public void testIsIdAvailable() {
        assertTrue(instance.isIdAvailable(7L));
        final var place = PlaceFactory.createPlace(7L, "testIsIdAvailable", PlaceLevel.PLANET, null, null);
        instance.addObject(place);
        assertFalse(instance.isIdAvailable(7L));
    }

    /**
     * Test of getObjects method, of class Factory.
     */
    @Test
    public void testGetObjects() {
        assertTrue(instance.getObjects().isEmpty());
        final var place = PlaceFactory.createPlace(7L, "testGetObjects", PlaceLevel.PLANET, null, null);
        instance.addObject(place);
        assertEquals(1, instance.getObjects().size());
        assertTrue(instance.getObjects().contains(place));
    }

    /**
     * Test of reset method, of class Factory.
     */
    @Test
    public void testReset() {
        final var place = PlaceFactory.createPlace(7L, "testReset", PlaceLevel.PLANET, null, null);
        instance.addObject(place);
        instance.reset();
        assertTrue(instance.getObjects().isEmpty());
        assertEquals(0L, instance.getNextID());
    }

}
