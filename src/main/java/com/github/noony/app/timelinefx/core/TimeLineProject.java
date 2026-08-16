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

package com.github.noony.app.timelinefx.core;

import com.github.noony.app.timelinefx.Configuration;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A timeline project: the top-level container for a set of places, persons, stays,
 * friezes and picture chronologies, backed by a folder on disk.
 *
 * @author hamon
 */
public final class TimeLineProject {

    /**
     * Name of the property change event fired when a person is added.
     */
    public static final String PERSON_ADDED = "personAdded";

    /**
     * Name of the property change event fired when a place is added.
     */
    public static final String PLACE_ADDED = "placeAdded";

    /**
     * Name of the property change event fired when a root-level place is added.
     */
    public static final String HIGH_LEVEL_PLACE_ADDED = "highLevelPlaceAdded";

    /**
     * Name of the property change event fired when a stay is added.
     */
    public static final String STAY_ADDED = "stayAdded";

    /**
     * Name of the property change event fired when a person is removed.
     */
    public static final String PERSON_REMOVED = "personRemoved";

    /**
     * Name of the property change event fired when a place is removed.
     */
    public static final String PLACE_REMOVED = "placeRemoved";

    /**
     * Name of the property change event fired when a stay is removed.
     */
    public static final String STAY_REMOVED = "stayRemoved";
    //

    /**
     * Configuration key for the project's name.
     */
    public static final String PROJECT_NAME_KEY = "projectNameKey";

    /**
     * Configuration key for the project's folder location.
     */
    public static final String PROJECT_FOLDER_KEY = "projectFolderKey";

    /**
     * Configuration key for the pictures folder location.
     */
    public static final String PICTURES_FOLDER_KEY = "picturesFolderKey";

    /**
     * Configuration key for the miniatures folder location.
     */
    public static final String MINIATURES_FOLDER_KEY = "miniaturesFolderKey";

    /**
     * Configuration key for the portraits folder location.
     */
    public static final String PORTRAIT_FOLDER_KEY = "portraitsFolderKey";

    /**
     * Default portraits folder name, relative to the project folder.
     */
    public static final String DEFAULT_PORTRAIT_FOLDER = "portraits";

    /**
     * Default pictures folder name, relative to the project folder.
     */
    public static final String DEFAULT_PICTURES_FOLDER = "pictures";

    /**
     * Default miniatures folder name, relative to the project folder.
     */
    public static final String DEFAULT_MINIATURES_FOLDER = "miniatures";

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Log message used when an exception is caught while saving a resource.
     */
    private static final String EXCEPTION_LOG_MESSAGE = "> Exception :: {0}";

    /**
     * Separator used between a class name and an unsupported event in exception messages.
     */
    private static final String CLASS_EVENT_SEPARATOR = " :: ";

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * The project's name.
     */
    private final String name;

    // Reference files
    /**
     * The project's folder.
     */
    private File projectFolder;

    /**
     * The folder containing portrait pictures.
     */
    private File portraitsFolder;

    /**
     * The folder containing pictures.
     */
    private File picturesFolder;

    /**
     * The folder containing miniature pictures.
     */
    private File miniaturesFolder;

    /**
     * The project's save file.
     */
    private File projectFile;

    /**
     * The places with no parent.
     */
    private final List<Place> highLevelPlaces;

    /**
     * All places in the project, keyed by name.
     */
    private final Map<String, Place> allPlaces;

    /**
     * The persons in the project.
     */
    private final List<Person> persons;

    /**
     * The stays in the project.
     */
    private final List<StayPeriod> stays;

    /**
     * The friezes built from this project.
     */
    private final List<Frieze> friezes;

    /**
     * The picture chronologies built from this project.
     */
    private final List<PictureChronology> pictureChronologies;

    protected TimeLineProject(final String projectName, final Map<String, String> configParams) {
        name = projectName;
        initFolders(configParams);
        propertyChangeSupport = new PropertyChangeSupport(TimeLineProject.this);
        highLevelPlaces = new LinkedList<>();
        allPlaces = new HashMap<>();
        persons = new LinkedList<>();
        stays = new LinkedList<>();
        friezes = new LinkedList<>();
        pictureChronologies = new LinkedList<>();
    }

    private void initFolders(final Map<String, String> configParams) {
        final var projectFolderLocation = configParams.getOrDefault(PROJECT_FOLDER_KEY, Configuration.getProjectsParentFolder() + File.separator + name);
        final var portraitsFolderLocation = configParams.getOrDefault(PORTRAIT_FOLDER_KEY, DEFAULT_PORTRAIT_FOLDER);
        final var picturesFolderLocation = configParams.getOrDefault(PICTURES_FOLDER_KEY, DEFAULT_PICTURES_FOLDER);
        final var miniaturesFolderLocation = configParams.getOrDefault(MINIATURES_FOLDER_KEY, DEFAULT_MINIATURES_FOLDER);
        //
        projectFolder = new File(projectFolderLocation);
        LOG.log(Level.INFO, "Creating Project {0} in folder: {1}.", new Object[]{name, projectFolder});
        if (!projectFolder.exists()) {
            try {
                final Path path = projectFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create project folder : {0}", new Object[]{ex});
            }
        }
        projectFile = new File(projectFolderLocation + File.separator + name + ".xml");
        //
        final String portraitsRoot = projectFolderLocation + File.separator + portraitsFolderLocation;
        portraitsFolder = new File(portraitsRoot);
        if (!portraitsFolder.exists()) {
            try {
                final Path path = portraitsFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create portrait folder : {0}.", new Object[]{ex});
            }
        }
        final String picturesRoot = projectFolderLocation + File.separator + picturesFolderLocation;
        picturesFolder = new File(picturesRoot);
        if (!picturesFolder.exists()) {
            try {
                final Path path = picturesFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create pictures folder : {0}.", new Object[]{ex});
            }
        }
        miniaturesFolder = new File(projectFolderLocation + File.separator + miniaturesFolderLocation);
        if (!miniaturesFolder.exists()) {
            try {
                final Path path = miniaturesFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create miniature folder : {0}.", new Object[]{ex});
            }
        }
        //
        saveDefaultPortraitResources();
    }

    private void saveDefaultPortraitResources() {
        try {
            try (InputStream inputstream = getClass().getResourceAsStream(Person.DEFAULT_PICTURE_NAME)) {
                assert inputstream != null;
                final String outputPath = portraitsFolder + File.separator + Person.DEFAULT_PICTURE_NAME;
                final File outputFile = new File(outputPath);
                LOG.log(Level.INFO, "> savePortraitResources :: {0}", outputPath);
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(inputstream.readAllBytes());
                }
            }
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Resource file not found :: {0}", new Object[]{Person.DEFAULT_PICTURE_NAME});
            LOG.log(Level.SEVERE, EXCEPTION_LOG_MESSAGE, new Object[]{ex});
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while saving resource :: {0}", new Object[]{Person.DEFAULT_PICTURE_NAME});
            LOG.log(Level.SEVERE, EXCEPTION_LOG_MESSAGE, new Object[]{ex});
        }
    }

    /**
     * Returns the project's folder.
     *
     * @return the project's folder
     */
    public File getProjectFolder() {
        return projectFolder;
    }

    /**
     * Returns the project's portraits folder, as an absolute path.
     *
     * @return the project's portraits folder
     */
    public File getPortraitsAbsoluteFolder() {
        return portraitsFolder;
    }

    /**
     * Returns the project's portraits folder, as a path relative to the project's folder.
     *
     * @return the project's portraits folder, relative to the project's folder
     */
    public String getPortraitsRelativeFolder() {
        return CustomFileUtils.fromAbsoluteToProjectRelative(this, portraitsFolder);
    }

    /**
     * @return the project's save file
     */
    public File getTimelineFile() {
        return projectFile;
    }

    /**
     * @return the folder containing pictures
     */
    public File getPicturesFolder() {
        return picturesFolder;
    }

    /**
     * @return the folder containing miniature pictures
     */
    public File getMiniaturesFolder() {
        return miniaturesFolder;
    }

    /**
     * @return the project's save file's absolute path
     */
    public String getProjectLocation() {
        return projectFile.getAbsolutePath();
    }

    /**
     * @param listener the listener to add
     */
    public void addListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public void removeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return the project's name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a place (and its ancestors) to the project.
     *
     * @param aPlace the place to add
     * @return true if the place was handled (added, or already present)
     */
    public boolean addPlace(final Place aPlace) {
        if (aPlace == null) {
            return false;
        } else if (aPlace.isRootPlace()) {
            addHighLevelPlace(aPlace);
            return true;
        } else {
            addPlace(aPlace.getParent());
            if (!allPlaces.containsKey(aPlace.getName())) {
                allPlaces.put(aPlace.getName(), aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            }
            return true;
        }
    }

    /**
     * @param aPlace the root place to add
     * @return true if the place was added, false if it was already present
     */
    public boolean addHighLevelPlace(final Place aPlace) {
        if (!highLevelPlaces.contains(aPlace)) {
            highLevelPlaces.add(aPlace);
            highLevelPlaces.sort(Place.COMPARATOR);
            if (!allPlaces.containsKey(aPlace.getName())) {
                allPlaces.put(aPlace.getName(), aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            }
            propertyChangeSupport.firePropertyChange(HIGH_LEVEL_PLACE_ADDED, this, aPlace);
            return true;
        }
        return false;
    }

    /**
     * @param aPlace the root place to remove
     * @return true if the place was removed
     */
    public boolean removeHighLevelPlace(final Place aPlace) {
        // TODO fire
        return highLevelPlaces.remove(aPlace);
    }

    /**
     * @return an unmodifiable list of the places with no parent
     */
    public List<Place> getHighLevelPlaces() {
        return Collections.unmodifiableList(highLevelPlaces);
    }

    /**
     * @param placeName a place's name
     * @return the place with the given name, or null if none exists
     */
    public Place getPlaceByName(final String placeName) {
        return allPlaces.get(placeName);
    }

    /**
     * Adds several stays to this project.
     *
     * @param staysToAdd the stays to add
     */
    public void addAllStays(final StayPeriod... staysToAdd) {
        for (StayPeriod s : staysToAdd) {
            addStay(s);
        }
    }

    /**
     * Adds several stays to this project.
     *
     * @param staysToAdd the stays to add
     */
    public void addAllStays(final Collection<? extends StayPeriod> staysToAdd) {
        staysToAdd.forEach(this::addStay);
    }

    /**
     * @param aStay the stay to add
     */
    public void addStay(final StayPeriod aStay) {
        if (!stays.contains(aStay)) {
            stays.add(aStay);
            stays.sort(StayPeriod.STAY_COMPARATOR);
            propertyChangeSupport.firePropertyChange(STAY_ADDED, this, aStay);
        }
    }

    /**
     * @param aStay the stay to remove
     */
    public void removeStay(final StayPeriod aStay) {
        if (stays.contains(aStay)) {
            stays.remove(aStay);
            propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, aStay);
        }
    }

    /**
     * @return an unmodifiable list of the stays in the project
     */
    public List<StayPeriod> getStays() {
        return Collections.unmodifiableList(stays);
    }

    protected boolean addFrieze(final Frieze frieze) {
        if (!friezes.contains(frieze)) {
            frieze.addListener(this::handleFriezeChange);
            friezes.add(frieze);
            frieze.getPersons().stream().filter(p -> !persons.contains(p)).forEach(persons::add);
            frieze.getPlaces().stream().forEachOrdered(this::addPlace);
            frieze.getStayPeriods().stream().forEachOrdered(this::addStay);
            return true;
        }
        return false;
    }

    /**
     * @param pictureChronology the picture chronology to add
     * @return true if it was added, false if it was already present
     */
    public boolean addPictureChronology(final PictureChronology pictureChronology) {
        if (!pictureChronologies.contains(pictureChronology)) {
            pictureChronology.addListener(this::handlePicturesChronologyChange);
            pictureChronologies.add(pictureChronology);
            return true;
        }
        return false;
    }

    /**
     * @return an unmodifiable list of the friezes built from this project
     */
    public List<Frieze> getFriezes() {
        return Collections.unmodifiableList(friezes);
    }

    /**
     * @return an unmodifiable list of the persons in the project
     */
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    /**
     * @return an unmodifiable list of the picture chronologies built from this project
     */
    public List<PictureChronology> getPictureChronologies() {
        return Collections.unmodifiableList(pictureChronologies);
    }

    /**
     * NOTE: this method may take time with larger projects.
     *
     * @return all the places present in the project.
     */
    public List<Place> getAllPlaces() {
        return allPlaces.values().stream().sorted(Place.COMPARATOR).collect(Collectors.toList());
    }

    /**
     * @param aPerson the person to add
     * @return true if the person was added, false if it was already present
     */
    public boolean addPerson(final Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            persons.sort(Person.COMPARATOR);
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            return true;
        }
        return false;
    }

    private void handleFriezeChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.PLACE_ADDED ->
                addPlace((Place) event.getNewValue());
            case Frieze.PERSON_ADDED ->
                addPerson((Person) event.getNewValue());
            case Frieze.STAY_ADDED ->
                addStay((StayPeriod) event.getNewValue());
            case Frieze.DATE_WINDOW_CHANGED, Frieze.PERSON_REMOVED, Frieze.PLACE_REMOVED, Frieze.NAME_CHANGED, Frieze.STAY_UPDATED, Frieze.START_DATE_ADDED, Frieze.START_DATE_REMOVED, Frieze.END_DATE_ADDED, Frieze.END_DATE_REMOVED -> {
                // ignoring
            }
            case Frieze.STAY_REMOVED -> {
                // ignored since removal from one Frieze does not mean deleted
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + CLASS_EVENT_SEPARATOR + event);
        }
    }

    private void handlePicturesChronologyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronology.PICTURE_ADDED, PictureChronology.PICTURE_REMOVED, PictureChronology.NAME_CHANGED, PictureChronology.LAYOUT_CHANGED, PictureChronology.LINK_ADDED, PictureChronology.LINK_REMOVED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + CLASS_EVENT_SEPARATOR + event);
        }
    }

    /**
     * @param deletedPlace the place to remove, along with its stays and children
     */
    public void removePlace(final Place deletedPlace) {
        allPlaces.remove(deletedPlace.getName());
        highLevelPlaces.remove(deletedPlace);
        //
        if (deletedPlace.getParent() != null) {
            deletedPlace.getParent().removePlace(deletedPlace);
        }
        //
        removeStaysAt(deletedPlace);
        removeChildrenPlaces(deletedPlace);
        //
        propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, deletedPlace);
    }

    /**
     * @param deletedPerson the person to remove, along with their stays
     */
    public void removePerson(final Person deletedPerson) {
        if (persons.contains(deletedPerson)) {
            persons.remove(deletedPerson);
            final List<StayPeriod> staysToRemove = stays.stream().filter(s -> s.getPerson() == deletedPerson).toList();
            staysToRemove.forEach(this::removeStay);
            //
            propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, deletedPerson);
        }
    }

    private void removeChildrenPlaces(final Place aParentPlace) {
        final List<Place> directChildren = allPlaces.values().stream().
                filter(place -> place.getParent().equals(aParentPlace)).
                toList();
        directChildren.forEach(child -> {
            allPlaces.remove(child.getName());
            removeStaysAt(child);
        });
        directChildren.forEach(this::removeChildrenPlaces);
    }

    private void removeStaysAt(final Place aPlace) {
        final List<StayPeriod> staysToRemove = stays.stream().filter(s -> s.getPlace() == aPlace).toList();
        staysToRemove.forEach(this::removeStay);
    }

}
