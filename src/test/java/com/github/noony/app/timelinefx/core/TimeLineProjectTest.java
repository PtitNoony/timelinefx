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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.io.TempDir;

/**
 * Note: uses a real {@link TimeLineProject}, created in a JUnit {@code @TempDir} via an explicit
 * {@link TimeLineProject#PROJECT_FOLDER_KEY} override so it never touches the real user home directory.
 *
 * @author solun
 */
public final class TimeLineProjectTest {

    @TempDir
    private Path tempDir;

    private TimeLineProject project;

    private Person person;

    private Place place;

    private StayPeriod stay;

    public TimeLineProjectTest() {
    }

    @BeforeEach
    public void setUp() {
        FriezeObjectFactory.reset();
        final var configParams = Map.of(TimeLineProject.PROJECT_FOLDER_KEY, tempDir.toString());
        project = TimeLineProjectFactory.createProject("TimeLineProjectTest", configParams);
        person = PersonFactory.createPerson(project, "testPerson");
        place = PlaceFactory.createPlace("testPlace", PlaceLevel.PLANET, null);
        stay = StayFactory.createStayPeriodSimpleTime(person, 10.0, 20.0, place);
    }

    @AfterEach
    public void tearDown() {
        FriezeObjectFactory.reset();
    }

    /**
     * Test of getName method, of class TimeLineProject.
     */
    @Test
    public void testGetName() {
        assertEquals("TimeLineProjectTest", project.getName());
    }

    /**
     * Test of getProjectFolder, getPortraitsAbsoluteFolder, getPicturesFolder and getMiniaturesFolder methods,
     * of class TimeLineProject.
     */
    @Test
    public void testFolders() {
        assertTrue(project.getProjectFolder().exists());
        assertTrue(project.getPortraitsAbsoluteFolder().exists());
        assertTrue(project.getPicturesFolder().exists());
        assertTrue(project.getMiniaturesFolder().exists());
    }

    /**
     * Test of getPortraitsRelativeFolder method, of class TimeLineProject.
     */
    @Test
    public void testGetPortraitsRelativeFolder() {
        assertEquals(TimeLineProject.DEFAULT_PORTRAIT_FOLDER, project.getPortraitsRelativeFolder());
    }

    /**
     * Test of getTimelineFile and getProjectLocation methods, of class TimeLineProject.
     */
    @Test
    public void testTimelineFile() {
        assertEquals("TimeLineProjectTest.xml", project.getTimelineFile().getName());
        assertEquals(project.getTimelineFile().getAbsolutePath(), project.getProjectLocation());
    }

    /**
     * Test of addHighLevelPlace, getHighLevelPlaces and getPlaceByName methods, of class TimeLineProject.
     */
    @Test
    public void testAddHighLevelPlace() {
        assertTrue(project.getHighLevelPlaces().isEmpty());
        assertTrue(project.addHighLevelPlace(place));
        assertFalse(project.addHighLevelPlace(place));
        assertTrue(project.getHighLevelPlaces().contains(place));
        assertEquals(place, project.getPlaceByName("testPlace"));
    }

    /**
     * Test of removeHighLevelPlace method, of class TimeLineProject.
     */
    @Test
    public void testRemoveHighLevelPlace() {
        project.addHighLevelPlace(place);
        assertTrue(project.removeHighLevelPlace(place));
        assertTrue(project.getHighLevelPlaces().isEmpty());
    }

    /**
     * Test of addPlace method, of class TimeLineProject, with a nested place.
     */
    @Test
    public void testAddPlaceWithParent() {
        final var child = PlaceFactory.createPlace("testAddPlaceWithParentChild", PlaceLevel.TOWN, place);
        assertTrue(project.addPlace(child));
        assertTrue(project.getHighLevelPlaces().contains(place));
        assertEquals(child, project.getPlaceByName("testAddPlaceWithParentChild"));
    }

    /**
     * Test of addPlace method, of class TimeLineProject, with a null place.
     */
    @Test
    public void testAddPlaceNull() {
        assertFalse(project.addPlace(null));
    }

    /**
     * Test of getAllPlaces method, of class TimeLineProject.
     */
    @Test
    public void testGetAllPlaces() {
        project.addHighLevelPlace(place);
        assertTrue(project.getAllPlaces().contains(place));
    }

    /**
     * Test of removePlace method, of class TimeLineProject.
     */
    @Test
    public void testRemovePlace() {
        project.addHighLevelPlace(place);
        project.removePlace(place);
        assertNull(project.getPlaceByName("testPlace"));
        assertFalse(project.getHighLevelPlaces().contains(place));
    }

    /**
     * Test of addStay, getStays and removeStay methods, of class TimeLineProject.
     */
    @Test
    public void testStays() {
        assertTrue(project.getStays().isEmpty());
        project.addStay(stay);
        assertTrue(project.getStays().contains(stay));
        project.removeStay(stay);
        assertTrue(project.getStays().isEmpty());
    }

    /**
     * Test of addAllStays(StayPeriod...) method, of class TimeLineProject.
     */
    @Test
    public void testAddAllStaysVarargs() {
        project.addAllStays(stay);
        assertTrue(project.getStays().contains(stay));
    }

    /**
     * Test of addAllStays(Collection) method, of class TimeLineProject.
     */
    @Test
    public void testAddAllStaysCollection() {
        project.addAllStays(List.of(stay));
        assertTrue(project.getStays().contains(stay));
    }

    /**
     * Test of addPerson, getPersons and removePerson methods, of class TimeLineProject.
     */
    @Test
    public void testPersons() {
        assertFalse(project.getPersons().contains(person));
        assertTrue(project.addPerson(person));
        assertFalse(project.addPerson(person));
        assertTrue(project.getPersons().contains(person));
        project.removePerson(person);
        assertFalse(project.getPersons().contains(person));
    }

    /**
     * Test of removePerson method, of class TimeLineProject: also removes the person's stays.
     */
    @Test
    public void testRemovePersonRemovesStays() {
        project.addPerson(person);
        project.addStay(stay);
        project.removePerson(person);
        assertFalse(project.getStays().contains(stay));
    }

    /**
     * Test of getFriezes method, of class TimeLineProject: populated via FriezeFactory.
     */
    @Test
    public void testGetFriezes() {
        assertTrue(project.getFriezes().isEmpty());
        final var frieze = FriezeFactory.createFrieze(project, "testGetFriezes");
        assertTrue(project.getFriezes().contains(frieze));
        FriezeFactory.reset();
    }

    /**
     * Test of getPictureChronologies method, of class TimeLineProject: starts empty.
     */
    @Test
    public void testGetPictureChronologies() {
        assertTrue(project.getPictureChronologies().isEmpty());
    }

    /**
     * Test of addListener/removeListener methods, of class TimeLineProject.
     */
    @Test
    public void testListener() {
        final var fired = new boolean[]{false};
        final java.beans.PropertyChangeListener listener = e -> fired[0] = true;
        project.addListener(listener);
        project.addHighLevelPlace(place);
        assertTrue(fired[0]);
        //
        fired[0] = false;
        project.removeListener(listener);
        project.removeHighLevelPlace(place);
        assertFalse(fired[0]);
    }

}
