/*
 * Copyright (C) 2019 NoOnY
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
import javafx.geometry.Dimension2D;

/**
 * Bundles the layout attributes needed to create or reconfigure a {@link FriezeFreeMap}, replacing the
 * previous {@code Map<String, String>}-based parameter passing with a typed value object. {@link #toParameterMap()}
 * and {@link #fromParameterMap(Map, FriezeFreeMapProperties)} keep it interoperable with the existing XML
 * save/load format, which still stores each attribute as a named string parameter.
 *
 * @param dimension the free map's overall width/height
 * @param personWidth the width reserved for the persons column
 * @param placeNameWidth the width reserved for place names
 * @param fontSize the font size used to draw place/person names
 * @param plotSeparation the spacing between plots on a place
 * @param plotSize the size of a single plot
 * @param plotVisibility whether plots are drawn
 * @param portraitConnectorVisibility whether portrait connectors are drawn
 * @param portraitRadius the radius of a person's portrait
 * @author hamon
 */
public record FriezeFreeMapProperties(Dimension2D dimension, double personWidth, double placeNameWidth,
        double fontSize, double plotSeparation, double plotSize, boolean plotVisibility,
        boolean portraitConnectorVisibility, double portraitRadius) {

    /**
     * @return this instance's attributes as a {@code Map<String, String>}, keyed by
     * {@link FriezeFreeMap}'s parameter name constants, for XML persistence.
     */
    public Map<String, String> toParameterMap() {
        final var parameters = new HashMap<String, String>();
        parameters.put(FriezeFreeMap.FRIEZE_WIDTH, Double.toString(dimension.getWidth()));
        parameters.put(FriezeFreeMap.FRIEZE_HEIGHT, Double.toString(dimension.getHeight()));
        parameters.put(FriezeFreeMap.PERSONS_WIDTH, Double.toString(personWidth));
        parameters.put(FriezeFreeMap.PLACE_NAMES_WIDTH, Double.toString(placeNameWidth));
        parameters.put(FriezeFreeMap.FONT_SIZE, Double.toString(fontSize));
        parameters.put(FriezeFreeMap.PLOT_SEPARATION, Double.toString(plotSeparation));
        parameters.put(FriezeFreeMap.PLOT_SIZE, Double.toString(plotSize));
        parameters.put(FriezeFreeMap.PLOT_VISIBILITY, Boolean.toString(plotVisibility));
        parameters.put(FriezeFreeMap.PORTRAIT_CONNECTOR_VISIBILITY, Boolean.toString(portraitConnectorVisibility));
        parameters.put(FriezeFreeMap.PORTRAIT_RADIUS, Double.toString(portraitRadius));
        return parameters;
    }

    /**
     * @param parameters a {@code Map<String, String>} as produced by {@link #toParameterMap()}, possibly
     * missing some keys
     * @param defaults the values to fall back to for any key missing from {@code parameters}
     * @return a new instance parsed from {@code parameters}, defaulting missing/unparseable keys to
     * {@code defaults}
     */
    public static FriezeFreeMapProperties fromParameterMap(final Map<String, String> parameters, final FriezeFreeMapProperties defaults) {
        final var width = parseDoubleOrDefault(parameters, FriezeFreeMap.FRIEZE_WIDTH, defaults.dimension().getWidth());
        final var height = parseDoubleOrDefault(parameters, FriezeFreeMap.FRIEZE_HEIGHT, defaults.dimension().getHeight());
        final var personWidthValue = parseDoubleOrDefault(parameters, FriezeFreeMap.PERSONS_WIDTH, defaults.personWidth());
        final var placeNameWidthValue = parseDoubleOrDefault(parameters, FriezeFreeMap.PLACE_NAMES_WIDTH, defaults.placeNameWidth());
        final var fontSizeValue = parseDoubleOrDefault(parameters, FriezeFreeMap.FONT_SIZE, defaults.fontSize());
        final var plotSeparationValue = parseDoubleOrDefault(parameters, FriezeFreeMap.PLOT_SEPARATION, defaults.plotSeparation());
        final var plotSizeValue = parseDoubleOrDefault(parameters, FriezeFreeMap.PLOT_SIZE, defaults.plotSize());
        final var plotVisibilityValue = Boolean.parseBoolean(parameters.getOrDefault(FriezeFreeMap.PLOT_VISIBILITY, Boolean.toString(defaults.plotVisibility())));
        final var portraitConnectorVisibilityValue = Boolean.parseBoolean(parameters.getOrDefault(FriezeFreeMap.PORTRAIT_CONNECTOR_VISIBILITY, Boolean.toString(defaults.portraitConnectorVisibility())));
        final var portraitRadiusValue = parseDoubleOrDefault(parameters, FriezeFreeMap.PORTRAIT_RADIUS, defaults.portraitRadius());
        return new FriezeFreeMapProperties(new Dimension2D(width, height), personWidthValue, placeNameWidthValue,
                fontSizeValue, plotSeparationValue, plotSizeValue, plotVisibilityValue, portraitConnectorVisibilityValue,
                portraitRadiusValue);
    }

    /**
     * @param parameters the raw parameter map
     * @param key the key to look up
     * @param defaultValue the value to fall back to when the key is missing or its value is not a valid double
     * @return the parsed value, or {@code defaultValue} if missing/unparseable
     */
    private static double parseDoubleOrDefault(final Map<String, String> parameters, final String key, final double defaultValue) {
        final var raw = parameters.get(key);
        if (raw == null) {
            return defaultValue;
        }
        var parsedValue = defaultValue;
        try {
            parsedValue = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            // keep the default already assigned above
        }
        return parsedValue;
    }

}
