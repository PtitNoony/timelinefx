/*
 * Copyright (C) 2023 NoOnY
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

import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author solun
 */
public class PlaceTest {

    public PlaceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
        PlaceFactory.reset();
    }

    @BeforeEach
    public void setUp() {
        PlaceFactory.reset();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getId method, of class Place.
     */
    @Test
    public void testGetId() {
        long expResult = 23L;
        Place instance = PlaceFactory.createPlace(expResult, "Foo", PlaceLevel.PLANET, null, Color.CORAL);
        long result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of addPropertyChangeListener method, of class Place.
     */
    @Test
    public void testAddPropertyChangeListener() {
        PropertyChangeListener listener = e -> System.err.println("! " + e);
        Place instance = PlaceFactory.createPlace("Foo", PlaceLevel.PLANET, null);
        instance.addPropertyChangeListener(listener);
    }

    /**
     * Test of setSelected and isSelected method, of class Place.
     */
    @Test
    public void testSelected() {
        boolean isSelected = false;
        Place instance = PlaceFactory.createPlace("Foo", PlaceLevel.PLANET, null);
        instance.setSelected(isSelected);
        assertEquals(isSelected, instance.isSelected());
        instance.setSelected(!isSelected);
        assertEquals(!isSelected, instance.isSelected());
    }

    /**
     * Test of getName and setName methods, of class Place.
     */
    @Test
    public void testName() {
        String expResult = "Foo";
        Place instance = PlaceFactory.createPlace(expResult, PlaceLevel.PLANET, null);
        assertEquals(expResult, instance.getName());
        //
        String expResult2 = "Fii";
        instance.setName(expResult2);
        assertEquals(expResult2, instance.getName());
    }

    /**
     * Test of getLevel and setLevel methods, of class Place.
     */
    @Test
    public void testLevel() {
        PlaceLevel expResult = PlaceLevel.DEPARTMENT;
        Place instance = PlaceFactory.createPlace("testLevel", expResult, null);
        assertEquals(expResult, instance.getLevel());
        //
        PlaceLevel expResult2 = PlaceLevel.ORBIT;
        instance.setLevel(expResult2);
        assertEquals(expResult2, instance.getLevel());
    }

    /**
     * Test of isRootPlace method, of class Place.
     */
    @Test
    public void testIsRootPlace() {
        Place instance = PlaceFactory.createPlace("testIsRootPlace", PlaceLevel.ORBIT, null);
        boolean expResult = true;
        assertEquals(expResult, instance.isRootPlace());
        Place instance2 = PlaceFactory.createPlace("testIsRootPlace2", PlaceLevel.PLANET, instance);
        boolean expResult2 = false;
        assertEquals(expResult2, instance2.isRootPlace());
    }

    /**
     * Test of getParent and setParent methods, of class Place.
     */
    @Test
    public void testParent() {
        Place instance = PlaceFactory.createPlace("testParentPlace", PlaceLevel.ORBIT, null);
        Place instance2 = PlaceFactory.createPlace("testParentPlace2", PlaceLevel.PLANET, instance);
        Place expResult = PlaceFactory.PLACES_PLACE;
        assertEquals(expResult, instance.getParent());
        assertEquals(instance, instance2.getParent());
    }

    /**
     * Test of getPlaces method, of class Place.
     */
    @Test
    public void testGetPlaces() {
        var instance = PlaceFactory.createPlace("testGetPlaces", PlaceLevel.ORBIT, null);
        var instance2 = PlaceFactory.createPlace("testGetPlaces2", PlaceLevel.PLANET, instance);
        var instance3 = PlaceFactory.createPlace("testGetPlaces3", PlaceLevel.PLANET, instance);
        List<Place> expResult = new LinkedList<>();
        expResult.add(instance2);
        expResult.add(instance3);
        List<Place> result = instance.getPlaces();
        assertEquals(expResult, result);
    }



    /**
     * Test of removePlace method, of class Place.
     */
    @Test
    public void testRemovePlace() {
        var instance = PlaceFactory.createPlace("testRemovePlace", PlaceLevel.ORBIT, null);
        var instance2 = PlaceFactory.createPlace("testRemovePlace2", PlaceLevel.PLANET, instance);
        assertEquals(true, instance.removePlace(instance2));
        assertEquals(false, instance.removePlace(instance2));
    }

    /**
     * Test of getColor and setColor methods, of class Place.
     */
    @Test
    public void testColor() {
        Color expResult = Color.RED;
        Place instance = PlaceFactory.createPlace(23L, "testColorPlace", PlaceLevel.ORBIT, null, expResult);
        assertEquals(expResult, instance.getColor());
        Color expResult2 = Color.OLDLACE;
        instance.setColor(expResult2);
        assertEquals(expResult2, instance.getColor());
    }

    /**
     * Test of toString method, of class Place.
     */
    @Test
    public void testToString() {
        Place instance = PlaceFactory.createPlace("testToStringPlace", PlaceLevel.ORBIT, null);
        String expResult = instance.getName();
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of isLowerThan method, of class Place.
     */
    @Test
    public void testIsLowerThan() {
        var instance = PlaceFactory.createPlace("testIsLowerThan", PlaceLevel.ORBIT, null);
        var instance2 = PlaceFactory.createPlace("testIsLowerThan2", PlaceLevel.PLANET, instance);
        assertEquals(true, instance2.isLowerThanOrLeveled(instance));
        assertEquals(false, instance.isLowerThanOrLeveled(instance2));
    }


    /**
     * Test of encompasses method, of class Place.
     */
    @Test
    public void testEncompasses() {
        var earth = PlaceFactory.createPlace("Earth", PlaceLevel.PLANET, null);
        var africa = PlaceFactory.createPlace("Africa", PlaceLevel.CONTINENT, earth);
        var egypt = PlaceFactory.createPlace("Egypt", PlaceLevel.COUNTRY, africa);
        var cairo = PlaceFactory.createPlace("Cairo", PlaceLevel.TOWN, egypt);
        var atlantis = PlaceFactory.createPlace("Atlantis", PlaceLevel.TOWN, africa);
        var europe = PlaceFactory.createPlace("Europe", PlaceLevel.CONTINENT, earth);
        var france = PlaceFactory.createPlace("France", PlaceLevel.COUNTRY, europe);
        assertTrue( earth.encompasses(africa));
        assertTrue( earth.encompasses(egypt));
        assertTrue( earth.encompasses(cairo));
        assertTrue( earth.encompasses(atlantis));
        assertTrue( earth.encompasses(europe));
        assertTrue( earth.encompasses(france));
        //
        assertTrue( africa.encompasses(egypt));
        assertTrue( africa.encompasses(cairo));
        assertTrue( africa.encompasses(atlantis));
        assertTrue( europe.encompasses(france));
        assertTrue( europe.encompasses(europe));
        //
        assertFalse(africa.encompasses(earth));
        assertFalse(africa.encompasses(europe));
        assertFalse(africa.encompasses(france));
        //
        assertFalse(europe.encompasses(earth));
        assertFalse(europe.encompasses(africa));
        assertFalse(europe.encompasses(egypt));
        assertFalse(europe.encompasses(cairo));
        assertFalse(europe.encompasses(atlantis));
    }

}
