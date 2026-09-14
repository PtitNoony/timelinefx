/*
 * Copyright (C) 2021 NoOnY
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
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Base implementation shared by {@link Picture} and {@link Portrait}.
 *
 * @author hamon
 */
public abstract class AbstractPicture extends DateObject implements IPicture {

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * This picture's unique id.
     */
    private final Long id;

    /**
     * The persons appearing on this picture.
     */
    private final List<Person> persons;

    /**
     * The places appearing on this picture.
     */
    private final List<Place> places;

    /**
     * The picture file's path, relative to the project.
     */
    private final String filePath;

    /**
     * The picture's width.
     */
    private final int width;

    /**
     * The picture's height.
     */
    private final int height;

    /**
     * The picture's display name.
     */
    private String name;

    @SuppressWarnings("this-escape")
    protected AbstractPicture(final long anID, final String aName, final String aFilePath, final int aWidth, final int aHeight, final Date aDate) {
        super(aDate);
        id = anID;
        persons = new LinkedList<>();
        places = new LinkedList<>();
        width = aWidth;
        height = aHeight;
        filePath = aFilePath;
        //
        name = aName;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String aName) {
        if (!Objects.equals(aName, name)) {
            name = aName;
            propertyChangeSupport.firePropertyChange(NAME_CHANGED, null, name);
        }
    }

    @Override
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    @Override
    public boolean addPerson(final Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePerson(final Person aPerson) {
        if (persons.contains(aPerson)) {
            persons.remove(aPerson);
            propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, aPerson);
            return true;
        }
        return false;
    }

    @Override
    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

    @Override
    public boolean addPlace(final Place aPlace) {
        if (!places.contains(aPlace)) {
            places.add(aPlace);
            propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlace(final Place aPlace) {
        if (places.contains(aPlace)) {
            places.remove(aPlace);
            propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, aPlace);
            return true;
        }
        return false;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public String getProjectRelativePath() {
        return filePath;
    }

    @Override
    public String getAbsolutePath() {
        return getProject().getProjectFolder().getAbsolutePath() + File.separator + filePath;
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Moves a person up in the persons list.
     *
     * @param aPerson the person to move up in the persons list
     */
    public void movePersonUp(final Person aPerson) {
        final int index = persons.indexOf(aPerson);
        if (index > 0) {
            Collections.swap(persons, index, index - 1);
            propertyChangeSupport.firePropertyChange(PERSONS_REORDED, this, index);
        }
    }

    /**
     * @param aPerson the person to move down in the persons list
     */
    public void movePersonDown(final Person aPerson) {
        final int index = persons.indexOf(aPerson);
        if (index != -1 && index < persons.size() - 1) {
            Collections.swap(persons, index + 1, index);
            propertyChangeSupport.firePropertyChange(PERSONS_REORDED, this, index);
        }
    }

    @Override
    public int compareTo(final IFileObject other) {
        if (other == null) {
            return 1;
        }
        final var timeComparison = Double.compare(getAbsoluteTime(), getAbsoluteTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        return getAbsolutePath().compareTo(other.getAbsolutePath());
    }
}
