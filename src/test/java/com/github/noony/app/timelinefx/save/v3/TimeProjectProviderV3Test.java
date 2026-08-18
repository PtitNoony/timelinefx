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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author solun
 */
public final class TimeProjectProviderV3Test {

    /**
     * Default constructor.
     */
    public TimeProjectProviderV3Test() {
    }

    private static Element newElement(String tagName, String attributeName, String attributeValue) throws Exception {
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        var element = doc.createElement(tagName);
        element.setAttribute(attributeName, attributeValue);
        return element;
    }

    /**
     * Test of parseDoubleAttribute method, of class TimeProjectProviderV3, with a well-formed value.
     */
    @Test
    public void testParseDoubleAttributeValidValue() throws Exception {
        var element = newElement("stay", "startDate", "12.5");
        assertEquals(12.5, TimeProjectProviderV3.parseDoubleAttribute(element, "startDate"));
    }

    /**
     * Regression test: a malformed attribute used to throw a bare NumberFormatException with no indication of
     * which element/attribute in the save file was at fault.
     */
    @Test
    public void testParseDoubleAttributeThrowsClearExceptionOnMalformedValue() throws Exception {
        var element = newElement("stay", "startDate", "not-a-number");
        var exception = assertThrows(IllegalStateException.class,
                () -> TimeProjectProviderV3.parseDoubleAttribute(element, "startDate"));
        assertTrue(exception.getMessage().contains("startDate"));
        assertTrue(exception.getMessage().contains("stay"));
        assertTrue(exception.getMessage().contains("not-a-number"));
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    /**
     * Test of parseDoubleAttribute method, of class TimeProjectProviderV3, with a missing attribute.
     */
    @Test
    public void testParseDoubleAttributeThrowsClearExceptionOnMissingAttribute() throws Exception {
        var element = newElement("stay", "otherAttribute", "1.0");
        var exception = assertThrows(IllegalStateException.class,
                () -> TimeProjectProviderV3.parseDoubleAttribute(element, "startDate"));
        assertTrue(exception.getMessage().contains("startDate"));
        assertTrue(exception.getMessage().contains("stay"));
    }

}
