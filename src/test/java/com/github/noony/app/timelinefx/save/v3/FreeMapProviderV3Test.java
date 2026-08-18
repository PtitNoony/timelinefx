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

package com.github.noony.app.timelinefx.save.v3;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class FreeMapProviderV3Test {

    /**
     * Default constructor.
     */
    public FreeMapProviderV3Test() {
    }

    /**
     * Builds a minimal {@code <freeMaps>} element containing one {@code <freeMap>} whose only place has a
     * malformed {@code height} attribute. Parsing fails while resolving that attribute, before the
     * {@code frieze} argument of {@link FreeMapProviderV3#parseFreeMaps} is ever dereferenced, so {@code null}
     * is a safe stand-in for it here.
     */
    private static Element newFreeMapsElementWithMalformedPlaceHeight() throws Exception {
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        var freeMapsElement = doc.createElement("freeMaps");
        var freeMapElement = doc.createElement("freeMap");
        freeMapElement.setAttribute("id", "1");
        freeMapElement.setAttribute("name", "Test FreeMap");
        freeMapElement.appendChild(doc.createElement("freeMapDateHandles"));
        freeMapElement.appendChild(doc.createElement("freeMapPersons"));
        var placesElement = doc.createElement("freeMapPlaces");
        var placeElement = doc.createElement("freeMapPlace");
        placeElement.setAttribute("placeID", "1");
        placeElement.setAttribute("height", "not-a-number");
        placeElement.setAttribute("yPos", "0.0");
        placeElement.setAttribute("fontSize", "10.0");
        placeElement.setAttribute("placeNameWidth", "10.0");
        placesElement.appendChild(placeElement);
        freeMapElement.appendChild(placesElement);
        freeMapsElement.appendChild(freeMapElement);
        return freeMapsElement;
    }

    /**
     * Regression test: confirms FreeMapProviderV3's own parsing methods are wired to the shared
     * parseDoubleAttribute helper too, surfacing a clear IllegalStateException instead of a bare
     * NumberFormatException for a malformed freemap place height.
     */
    @Test
    public void testParseFreeMapsThrowsClearExceptionOnMalformedPlaceHeight() throws Exception {
        var freeMapsElement = newFreeMapsElementWithMalformedPlaceHeight();
        var exception = assertThrows(IllegalStateException.class,
                () -> FreeMapProviderV3.parseFreeMaps(freeMapsElement, null));
        assertTrue(exception.getMessage().contains("height"));
        assertTrue(exception.getMessage().contains("not-a-number"));
    }

}
