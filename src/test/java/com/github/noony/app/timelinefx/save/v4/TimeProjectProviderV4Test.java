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

package com.github.noony.app.timelinefx.save.v4;

import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import javafx.scene.paint.Color;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Element;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TimeProjectProviderV4}.
 *
 * @author hamon
 */
public final class TimeProjectProviderV4Test {

    /**
     * Temporary directory used to create a test project without touching the real user directories.
     */
    @TempDir
    private Path tempDir;

    /**
     * The project used in these tests.
     */
    private TimeLineProject project;

    /**
     * Default constructor.
     */
    public TimeProjectProviderV4Test() {
    }

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    public void setUp() {
        PersonFactory.reset();
        PlaceFactory.reset();
        StayFactory.reset();
        PictureFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("TimeProjectProviderV4Test", configParams);
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    public void tearDown() {
        PersonFactory.reset();
        PlaceFactory.reset();
        StayFactory.reset();
        PictureFactory.reset();
    }

    /**
     * Builds a single-attribute element to feed to {@code parseDoubleAttribute}.
     *
     * @param tagName the element's tag name
     * @param attributeName the attribute's name
     * @param attributeValue the attribute's raw string value
     * @return the constructed element
     * @throws Exception if the document builder cannot be created
     */
    private static Element newElement(final String tagName, final String attributeName, final String attributeValue) throws Exception {
        final var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final var element = doc.createElement(tagName);
        element.setAttribute(attributeName, attributeValue);
        return element;
    }

    /**
     * Test of parseDoubleAttribute method, of class TimeProjectProviderV4, with a well-formed value.
     */
    @Test
    public void testParseDoubleAttributeValidValue() throws Exception {
        final var element = newElement("stay", "startDate", "12.5");
        assertEquals(12.5, TimeProjectProviderV4.parseDoubleAttribute(element, "startDate"));
    }

    /**
     * Test of parseDoubleAttribute method, of class TimeProjectProviderV4, with a malformed value: fails with a
     * message identifying the element, attribute and raw value instead of a bare NumberFormatException.
     */
    @Test
    public void testParseDoubleAttributeThrowsClearExceptionOnMalformedValue() throws Exception {
        final var element = newElement("stay", "startDate", "not-a-number");
        final var exception = assertThrows(IllegalStateException.class,
                () -> TimeProjectProviderV4.parseDoubleAttribute(element, "startDate"));
        assertTrue(exception.getMessage().contains("startDate"));
        assertTrue(exception.getMessage().contains("stay"));
        assertTrue(exception.getMessage().contains("not-a-number"));
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    /**
     * Test of parseObjectTimeValue method, of class TimeProjectProviderV4, with an explicit LOCAL_TIME format:
     * unlike TimeProjectProviderV3 (which reads a per-element "timeFormat" attribute), the format is entirely
     * determined by the caller-supplied argument.
     */
    @Test
    public void testParseObjectTimeValueLocalTime() throws Exception {
        final var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final var element = doc.createElement("picture");
        element.setAttribute("date", "2024-05-06");
        final var picture = PictureFactory.createPicture(project, 1L, "testPicture", LocalDateTime.MIN, "pictures/foo.png", 10, 10);
        TimeProjectProviderV4.parseObjectTimeValue(element, picture, TimeFormat.LOCAL_TIME);
        assertEquals(TimeFormat.LOCAL_TIME, picture.getTimeFormat());
        assertEquals(LocalDate.of(2024, 5, 6), picture.getDate());
    }

    /**
     * Test of parseObjectTimeValue method, of class TimeProjectProviderV4, with an explicit TIME_MIN format.
     */
    @Test
    public void testParseObjectTimeValueTimeMin() throws Exception {
        final var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final var element = doc.createElement("picture");
        element.setAttribute("date", "42.0");
        final var picture = PictureFactory.createPicture(project, 1L, "testPicture", LocalDateTime.MIN, "pictures/foo.png", 10, 10);
        TimeProjectProviderV4.parseObjectTimeValue(element, picture, TimeFormat.TIME_MIN);
        assertEquals(TimeFormat.TIME_MIN, picture.getTimeFormat());
        assertEquals(42.0, picture.getTimestamp());
    }

    /**
     * Builds a minimal {@code <PROJECT>} element with one place, one person and one stay linking them,
     * ready to feed to {@link TimeProjectProviderV4#load}. Everything is described in the XML itself
     * (rather than pre-seeded via the core factories) since {@code load} reconstructs the whole object graph
     * from scratch, resetting the shared factories as its first step.
     *
     * @param projectTimeFormat the project's own "timeFormat" root attribute
     * @param stayTimeFormat the stay's own "timeFormat" attribute, or {@code null} to omit it
     * @param startDate the stay's raw "startDate" attribute value
     * @param endDate the stay's raw "endDate" attribute value
     * @return the constructed root element
     * @throws Exception if the document builder cannot be created
     */
    private static Element newProjectWithOneStay(final TimeFormat projectTimeFormat, final TimeFormat stayTimeFormat,
            final String startDate, final String endDate) throws Exception {
        final var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final var rootElement = doc.createElement(TimeProjectProviderV4.PROJECT_GROUP);
        rootElement.setAttribute(TimeProjectProviderV4.NAME_ATR, "TestProject");
        rootElement.setAttribute(TimeProjectProviderV4.TIME_FORMAT_ATR, projectTimeFormat.name());
        //
        final var placeElement = doc.createElement(TimeProjectProviderV4.PLACE_ELEMENT);
        placeElement.setAttribute(TimeProjectProviderV4.ID_ATR, "1");
        placeElement.setAttribute(TimeProjectProviderV4.NAME_ATR, "testPlace");
        placeElement.setAttribute(TimeProjectProviderV4.PLACE_LEVEL_ATR, PlaceLevel.PLANET.name());
        placeElement.setAttribute(TimeProjectProviderV4.COLOR_ATR, Color.WHITE.toString());
        final var placesGroupElement = doc.createElement(TimeProjectProviderV4.PLACES_GROUP);
        placesGroupElement.appendChild(placeElement);
        rootElement.appendChild(placesGroupElement);
        //
        final var personElement = doc.createElement(TimeProjectProviderV4.PERSON_ELEMENT);
        personElement.setAttribute(TimeProjectProviderV4.ID_ATR, "1");
        personElement.setAttribute(TimeProjectProviderV4.NAME_ATR, "testPerson");
        personElement.setAttribute(TimeProjectProviderV4.COLOR_ATR, Color.WHITE.toString());
        final var personsGroupElement = doc.createElement(TimeProjectProviderV4.PERSONS_GROUP);
        personsGroupElement.appendChild(personElement);
        rootElement.appendChild(personsGroupElement);
        //
        final var stayElement = doc.createElement(TimeProjectProviderV4.STAY_ELEMENT);
        stayElement.setAttribute(TimeProjectProviderV4.ID_ATR, "1");
        stayElement.setAttribute(TimeProjectProviderV4.PERSON_ATR, "1");
        stayElement.setAttribute(TimeProjectProviderV4.PLACE_ID_ATR, "1");
        stayElement.setAttribute(TimeProjectProviderV4.START_DATE_ATR, startDate);
        stayElement.setAttribute(TimeProjectProviderV4.END_DATE_ATR, endDate);
        if (stayTimeFormat != null) {
            stayElement.setAttribute(TimeProjectProviderV4.TIME_FORMAT_ATR, stayTimeFormat.name());
        }
        final var staysGroupElement = doc.createElement(TimeProjectProviderV4.STAYS_GROUP);
        staysGroupElement.appendChild(stayElement);
        rootElement.appendChild(staysGroupElement);
        //
        return rootElement;
    }

    /**
     * Regression test: a stay keeps the time format it was created with, which may predate a later change to
     * the project's own time format. Builds a minimal save file where the project's own time format is
     * TIME_MIN but the one saved stay carries its own "timeFormat" attribute of LOCAL_TIME, and confirms the
     * stay is parsed using its own format rather than silently defaulting to the project's.
     */
    @Test
    public void testLoadPrefersStayOwnTimeFormatOverProjectDefault() throws Exception {
        final var rootElement = newProjectWithOneStay(TimeFormat.TIME_MIN, TimeFormat.LOCAL_TIME, "2024-01-01", "2024-01-05");
        final var provider = new TimeProjectProviderV4();
        final var loadedProject = provider.load(tempDir.resolve("test.xml").toFile(), rootElement);
        //
        assertEquals(1, loadedProject.getStays().size());
        assertEquals(TimeFormat.LOCAL_TIME, loadedProject.getStays().get(0).getTimeFormat());
    }

    /**
     * Test of load method, of class TimeProjectProviderV4, without an explicit stay-level "timeFormat"
     * attribute: falls back to the project's own time format, matching pre-fix behavior for freshly-saved
     * V4 files where every stay already matches the project's current format.
     */
    @Test
    public void testLoadFallsBackToProjectTimeFormatWhenStayHasNone() throws Exception {
        final var rootElement = newProjectWithOneStay(TimeFormat.TIME_MIN, null, "1.0", "5.0");
        final var provider = new TimeProjectProviderV4();
        final var loadedProject = provider.load(tempDir.resolve("test.xml").toFile(), rootElement);
        //
        assertEquals(1, loadedProject.getStays().size());
        assertEquals(TimeFormat.TIME_MIN, loadedProject.getStays().get(0).getTimeFormat());
    }

}
