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
        final var width = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.FRIEZE_WIDTH, Double.toString(defaults.dimension().getWidth())));
        final var height = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.FRIEZE_HEIGHT, Double.toString(defaults.dimension().getHeight())));
        final var personWidthValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.PERSONS_WIDTH, Double.toString(defaults.personWidth())));
        final var placeNameWidthValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.PLACE_NAMES_WIDTH, Double.toString(defaults.placeNameWidth())));
        final var fontSizeValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.FONT_SIZE, Double.toString(defaults.fontSize())));
        final var plotSeparationValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.PLOT_SEPARATION, Double.toString(defaults.plotSeparation())));
        final var plotSizeValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.PLOT_SIZE, Double.toString(defaults.plotSize())));
        final var plotVisibilityValue = Boolean.parseBoolean(parameters.getOrDefault(FriezeFreeMap.PLOT_VISIBILITY, Boolean.toString(defaults.plotVisibility())));
        final var portraitConnectorVisibilityValue = Boolean.parseBoolean(parameters.getOrDefault(FriezeFreeMap.PORTRAIT_CONNECTOR_VISIBILITY, Boolean.toString(defaults.portraitConnectorVisibility())));
        final var portraitRadiusValue = Double.parseDouble(parameters.getOrDefault(FriezeFreeMap.PORTRAIT_RADIUS, Double.toString(defaults.portraitRadius())));
        return new FriezeFreeMapProperties(new Dimension2D(width, height), personWidthValue, placeNameWidthValue,
                fontSizeValue, plotSeparationValue, plotSizeValue, plotVisibilityValue, portraitConnectorVisibilityValue,
                portraitRadiusValue);
    }

}
