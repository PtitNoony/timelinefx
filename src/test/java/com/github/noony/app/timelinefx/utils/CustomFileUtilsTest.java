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

package com.github.noony.app.timelinefx.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link CustomFileUtils}.
 *
 * @author solun
 */
public final class CustomFileUtilsTest {

    /**
     * Default constructor.
     */
    public CustomFileUtilsTest() {
    }

    /**
     * Test of toDoubleArray method, of class CustomFileUtils, with a well-formed input.
     */
    @Test
    public void testToDoubleArrayValidInput() {
        final var result = CustomFileUtils.toDoubleArray("[1.0, 2.5, -3.25]");
        assertArrayEquals(new double[]{1.0, 2.5, -3.25}, result);
    }

    /**
     * Regression test: a malformed token used to throw a bare NumberFormatException with no indication of which
     * value, or which array, was at fault.
     */
    @Test
    public void testToDoubleArrayThrowsClearExceptionOnMalformedValue() {
        final var exception = assertThrows(IllegalArgumentException.class, () -> CustomFileUtils.toDoubleArray("[1.0, oops, 3.0]"));
        assertTrue(exception.getMessage().contains("oops"));
        assertTrue(exception.getMessage().contains("[1.0, oops, 3.0]"));
    }

}
