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

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Note: these tests deliberately stay at the {@link FriezeFreeMapProperties} level rather than going through
 * {@link FriezeFreeMapFactory}/{@link com.github.noony.app.timelinefx.core.Frieze}, since constructing a
 * {@link com.github.noony.app.timelinefx.core.TimeLineProject} requires app-level {@code Configuration} to be
 * initialized first (it reads/writes a preferences file under the user's home directory), which existing tests
 * in this project (e.g. {@code PlaceTest}) also avoid.
 *
 * @author solun
 */
public class FriezeFreeMapPropertiesTest {

    private static final double CUSTOM_FONT_SIZE = 99.0;

    /**
     * Default constructor.
     */
    public FriezeFreeMapPropertiesTest() {
    }

    /**
     * Test that DEFAULT_PROPERTIES survives a round-trip through toParameterMap/fromParameterMap.
     */
    @Test public void testParameterMapRoundTrip() {
        final var defaults = FriezeFreeMap.DEFAULT_PROPERTIES;
        final var roundTripped = FriezeFreeMapProperties.fromParameterMap(defaults.toParameterMap(), defaults);
        assertEquals(defaults, roundTripped);
    }

    /**
     * Test that fromParameterMap falls back to the supplied defaults for missing/partial keys.
     */
    @Test public void testFromParameterMapUsesDefaultsForMissingKeys() {
        final var defaults = FriezeFreeMap.DEFAULT_PROPERTIES;
        final Map<String, String> partialParameters = new HashMap<>();
        partialParameters.put(FriezeFreeMap.FONT_SIZE, Double.toString(CUSTOM_FONT_SIZE));
        final var properties = FriezeFreeMapProperties.fromParameterMap(partialParameters, defaults);
        assertEquals(CUSTOM_FONT_SIZE, properties.fontSize());
        assertEquals(defaults.personWidth(), properties.personWidth());
        assertEquals(defaults.plotSeparation(), properties.plotSeparation());
        assertEquals(defaults.portraitRadius(), properties.portraitRadius());
    }

    /**
     * Regression test: the javadoc has always promised a fallback to defaults for keys that are "missing/
     * unparseable", but the implementation used to only handle missing keys -- a present-but-garbled value threw
     * NumberFormatException instead of honoring that contract.
     */
    @Test public void testFromParameterMapFallsBackOnUnparseableValues() {
        final var defaults = FriezeFreeMap.DEFAULT_PROPERTIES;
        final Map<String, String> garbledParameters = new HashMap<>();
        garbledParameters.put(FriezeFreeMap.FONT_SIZE, "not-a-number");
        garbledParameters.put(FriezeFreeMap.PLOT_SEPARATION, Double.toString(CUSTOM_FONT_SIZE));
        final var properties = FriezeFreeMapProperties.fromParameterMap(garbledParameters, defaults);
        assertEquals(defaults.fontSize(), properties.fontSize());
        assertEquals(CUSTOM_FONT_SIZE, properties.plotSeparation());
    }

}
