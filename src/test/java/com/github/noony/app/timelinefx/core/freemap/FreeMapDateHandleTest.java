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

import javafx.geometry.Point2D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Note: no {@link FriezeFreeMap} is registered for {@code FREE_MAP_ID} here since these tests only exercise
 * {@link FreeMapDateHandle} in isolation; the duplicate-creation test therefore only asserts that some
 * {@link RuntimeException} is thrown, since the error path itself looks up the owning {@link FriezeFreeMap} by
 * id to build its message.
 *
 * @author hamon
 */
public final class FreeMapDateHandleTest {

    /**
     * An arbitrary free map id; no real {@link FriezeFreeMap} is registered under it.
     */
    private static final long FREE_MAP_ID = 1L;

    /**
     * Default constructor.
     */
    public FreeMapDateHandleTest() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        FreeMapDateHandle.resetFactory();
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        FreeMapDateHandle.resetFactory();
    }

    /**
     * Test of createFreeMapDateHandle method, of class FreeMapDateHandle.
     */
    @Test
    public void testCreateFreeMapDateHandle() {
        final var handle = FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(5.0, 6.0));
        assertEquals(10.0, handle.getDate());
        assertEquals(FreeMapDateHandle.TimeType.START, handle.getTimeType());
        assertEquals(5.0, handle.getXPos());
        assertEquals(6.0, handle.getYPos());
        assertTrue(handle.getPlots().isEmpty());
    }

    /**
     * Test of createFreeMapDateHandle method, of class FreeMapDateHandle, with a duplicate (date, type) pair.
     */
    @Test
    public void testCreateDuplicateDateAndTypeThrows() {
        FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(0.0, 0.0));
        assertThrows(RuntimeException.class,
                () -> FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(0.0, 0.0)));
    }

    /**
     * Test of createFreeMapDateHandle method, of class FreeMapDateHandle, with the same date but a different type.
     */
    @Test
    public void testCreateSameDateDifferentTypeSucceeds() {
        FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(0.0, 0.0));
        final var endHandle = FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.END, new Point2D(0.0, 0.0));
        assertEquals(FreeMapDateHandle.TimeType.END, endHandle.getTimeType());
    }

    /**
     * Test of setX method, of class FreeMapDateHandle, with a change below the dirty-check epsilon.
     */
    @Test
    public void testSetXIgnoresChangeBelowEpsilon() {
        final var handle = FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(5.0, 0.0));
        final var fired = new boolean[]{false};
        handle.addListener(e -> fired[0] = true);
        handle.setX(5.0 + GridPositionable.EPSILON / 2.0);
        assertFalse(fired[0]);
        assertEquals(5.0, handle.getXPos());
    }

    /**
     * Test of setX method, of class FreeMapDateHandle, with a change above the dirty-check epsilon.
     */
    @Test
    public void testSetXFiresChangeAboveEpsilon() {
        final var handle = FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(5.0, 0.0));
        final var fired = new boolean[]{false};
        handle.addListener(e -> fired[0] = true);
        handle.setX(20.0);
        assertTrue(fired[0]);
        assertEquals(20.0, handle.getXPos());
    }

    /**
     * Test of setY method, of class FreeMapDateHandle.
     */
    @Test
    public void testSetYFiresChange() {
        final var handle = FreeMapDateHandle.createFreeMapDateHandle(FREE_MAP_ID, 10.0, FreeMapDateHandle.TimeType.START, new Point2D(0.0, 5.0));
        final var fired = new boolean[]{false};
        handle.addListener(e -> fired[0] = true);
        handle.setY(15.0);
        assertTrue(fired[0]);
        assertEquals(15.0, handle.getYPos());
    }

}
