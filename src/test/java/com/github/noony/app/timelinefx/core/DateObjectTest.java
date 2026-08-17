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

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public class DateObjectTest {

    public DateObjectTest() {
    }

    /**
     * Test of the calendar-date constructor, of class DateObject.
     */
    @Test
    public void testLocalDateConstructor() {
        final var aDate = LocalDate.of(2023, 1, 2);
        final var instance = new DateObject(aDate);
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
        assertEquals(aDate, instance.getDate());
        assertEquals(aDate.toEpochDay(), instance.getAbsoluteTime());
    }

    /**
     * Test of the calendar-date constructor with a null date, of class DateObject: falls back to LocalDate.MIN.
     */
    @Test
    public void testLocalDateConstructorWithNull() {
        final var instance = new DateObject((LocalDate) null);
        assertEquals(LocalDate.MIN, instance.getDate());
    }

    /**
     * Test of the raw-timestamp constructor, of class DateObject.
     */
    @Test
    public void testTimestampConstructor() {
        final var instance = new DateObject(42.0);
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertEquals(42.0, instance.getTimestamp());
        assertEquals(42.0, instance.getAbsoluteTime());
        assertNull(instance.getDate());
    }

    /**
     * Test of the copy constructor, of class DateObject, copying a LOCAL_TIME instance.
     */
    @Test
    public void testCopyConstructorLocalTime() {
        final var original = new DateObject(LocalDate.of(2023, 1, 2));
        final var copy = new DateObject(original);
        assertEquals(original.getTimeFormat(), copy.getTimeFormat());
        assertEquals(original.getDate(), copy.getDate());
    }

    /**
     * Test of the copy constructor, of class DateObject, copying a TIME_MIN instance.
     */
    @Test
    public void testCopyConstructorTimeMin() {
        final var original = new DateObject(42.0);
        final var copy = new DateObject(original);
        assertEquals(original.getTimeFormat(), copy.getTimeFormat());
        assertEquals(original.getTimestamp(), copy.getTimestamp());
    }

    /**
     * Test of setDate(LocalDate) method, of class DateObject.
     */
    @Test
    public void testSetDate() {
        final var instance = new DateObject(LocalDate.of(2023, 1, 2));
        final var newDate = LocalDate.of(2024, 5, 6);
        instance.setDate(newDate);
        assertEquals(newDate, instance.getDate());
    }

    /**
     * Test of setDate(IDateObject) method, of class DateObject.
     */
    @Test
    public void testSetDateFromDateObject() {
        final var instance = new DateObject(42.0);
        final var other = new DateObject(LocalDate.of(2023, 1, 2));
        instance.setDate(other);
        assertEquals(TimeFormat.LOCAL_TIME, instance.getTimeFormat());
        assertEquals(other.getDate(), instance.getDate());
    }

    /**
     * Test of setTimestamp method, of class DateObject.
     */
    @Test
    public void testSetTimestamp() {
        final var instance = new DateObject(LocalDate.of(2023, 1, 2));
        instance.setTimestamp(99.0);
        assertEquals(TimeFormat.TIME_MIN, instance.getTimeFormat());
        assertEquals(99.0, instance.getTimestamp());
    }

    /**
     * Test of setValue method, of class DateObject, with a LOCAL_TIME instance.
     */
    @Test
    public void testSetValueLocalTime() {
        final var instance = new DateObject(LocalDate.of(2023, 1, 2));
        instance.setValue("2024-05-06");
        assertEquals(LocalDate.of(2024, 5, 6), instance.getDate());
    }

    /**
     * Test of setValue method, of class DateObject, with a TIME_MIN instance.
     */
    @Test
    public void testSetValueTimeMin() {
        final var instance = new DateObject(0.0);
        instance.setValue("12.5");
        assertEquals(12.5, instance.getTimestamp());
    }

    /**
     * Test of setValue method, of class DateObject, ignoring a null value.
     */
    @Test
    public void testSetValueNull() {
        final var instance = new DateObject(LocalDate.of(2023, 1, 2));
        instance.setValue(null);
        assertEquals(LocalDate.of(2023, 1, 2), instance.getDate());
    }

    /**
     * Test of getAbsoluteTimeAsString method, of class DateObject.
     */
    @Test
    public void testGetAbsoluteTimeAsString() {
        final var instance = new DateObject(12.5);
        assertFalse(instance.getAbsoluteTimeAsString().isEmpty());
    }

    /**
     * Test of addPropertyChangeListener/removePropertyChangeListener methods, of class DateObject.
     */
    @Test
    public void testPropertyChangeListener() {
        final var instance = new DateObject(LocalDate.of(2023, 1, 2));
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        instance.addPropertyChangeListener(listener);
        instance.setDate(LocalDate.of(2024, 5, 6));
        assertTrue(fired[0]);
        //
        fired[0] = false;
        instance.removePropertyChangeListener(listener);
        instance.setDate(LocalDate.of(2025, 1, 1));
        assertFalse(fired[0]);
    }

}
