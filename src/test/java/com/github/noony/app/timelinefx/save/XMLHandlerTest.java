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

package com.github.noony.app.timelinefx.save;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link XMLHandler}.
 *
 * @author hamon
 */
public final class XMLHandlerTest {

    /**
     * Default constructor.
     */
    public XMLHandlerTest() {
    }

    /**
     * Test of compareVersions method, of class XMLHandler, with equal versions.
     */
    @Test
    public void testCompareVersionsEqual() {
        assertEquals(0, XMLHandler.compareVersions("3", "3"));
        assertEquals(0, XMLHandler.compareVersions("3.1", "3.1"));
    }

    /**
     * Test of compareVersions method, of class XMLHandler, with simple single-segment versions: the higher
     * version compares greater, regardless of argument order.
     */
    @Test
    public void testCompareVersionsSimple() {
        assertTrue(XMLHandler.compareVersions("4", "3") > 0);
        assertTrue(XMLHandler.compareVersions("3", "4") < 0);
    }

    /**
     * Test of compareVersions method, of class XMLHandler, with multi-segment versions.
     */
    @Test
    public void testCompareVersionsMultiSegment() {
        assertTrue(XMLHandler.compareVersions("3.2", "3.1") > 0);
        assertTrue(XMLHandler.compareVersions("3.1", "3.2") < 0);
    }

    /**
     * Test that the highest-version provider found via the stream/max pattern used by XMLHandler's save-provider
     * selection is picked correctly, mirroring the fix to that selection logic.
     */
    @Test
    public void testCompareVersionsPicksMax() {
        final var versions = List.of("3", "1", "4", "2");
        final var max = versions.stream().max(XMLHandler::compareVersions).get();
        assertEquals("4", max);
    }

}
