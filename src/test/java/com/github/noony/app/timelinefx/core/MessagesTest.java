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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link Messages}.
 *
 * @author hamon
 */
public final class MessagesTest {

    /**
     * Default constructor.
     */
    public MessagesTest() {
    }

    /**
     * Test that UNSUPPORTED_TIME_FORMAT is a non-empty, stable message prefix.
     */
    @Test
    public void testUnsupportedTimeFormat() {
        assertNotNull(Messages.UNSUPPORTED_TIME_FORMAT);
        assertEquals("Unsupported time format: ", Messages.UNSUPPORTED_TIME_FORMAT);
    }

}
