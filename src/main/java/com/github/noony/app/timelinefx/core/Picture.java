/*
 * Copyright (C) 2020 NoOnY
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author hamon
 */
public class Picture extends FriezeObject {

    public static final String NAME_CHANGED = "pictureNameChanged";
    public static final String DATE_CHANGED = "pictureDateChanged";
    public static final String PERSON_ADDED = "picturePersonAdded";
    public static final String PERSON_REMOVED = "picturePersonRemoved";
    public static final String PERSONS_REORDED = "picturePersonsReordered";
    public static final String PLACE_ADDED = "picturePlaceAdded";
    public static final String PLACE_REMOVED = "picturePlaceRemoved";

    private final PropertyChangeSupport propertyChangeSupport;
    private final List<Person> persons;
    private final List<Place> places;
    //
    private final TimeLineProject project;
    private final String path;
    private final int width;
    private final int height;
    //
    private String name;
    private LocalDateTime creationDate;

    protected Picture(TimeLineProject aProject, long id, String pictureName, LocalDateTime pictureCreationDate, String picturePath, int pictureWidth, int pictureHeight) {
        super(id);
        project = aProject;
        propertyChangeSupport = new PropertyChangeSupport(Picture.this);
        name = pictureName;
        creationDate = pictureCreationDate;
        path = picturePath;
        persons = new ArrayList<>();
        places = new ArrayList<>();
        width = pictureWidth;
        height = pictureHeight;
    }

    public TimeLineProject getProject() {
        return project;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setName(String aName) {
        name = aName;
        propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
    }

    public void setCreationDate(LocalDateTime aCreationDate) {
        creationDate = aCreationDate;
        propertyChangeSupport.firePropertyChange(DATE_CHANGED, this, creationDate);
    }

    public String getName() {
        return name;
    }

    public void addPerson(Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
        }
    }

    public void removePerson(Person aPerson) {
        if (persons.contains(aPerson)) {
            persons.remove(aPerson);
            propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, aPerson);
        }
    }

    public void movePersonUp(Person aPerson) {
        int index = persons.indexOf(aPerson);
        if (index > 0) {
            Collections.swap(persons, index, index - 1);
            propertyChangeSupport.firePropertyChange(PERSONS_REORDED, this, index);
        }
    }

    public void movePersonDown(Person aPerson) {
        int index = persons.indexOf(aPerson);
        if (index != 1 & index < persons.size() - 1) {
            Collections.swap(persons, index + 1, index);
            propertyChangeSupport.firePropertyChange(PERSONS_REORDED, this, index);
        }
    }

    public void addPlace(Place aPlace) {
        if (!places.contains(aPlace)) {
            places.add(aPlace);
            propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
        }
    }

    public void removePlace(Place aPlace) {
        if (places.contains(aPlace)) {
            places.remove(aPlace);
            propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, aPlace);
        }
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

    public String getPath() {
        return path;
    }

    public String getAbsolutePath() {
        return project.getProjectFolder().getAbsolutePath() + File.separator + path;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Pic[" + name + "]";
    }

}
