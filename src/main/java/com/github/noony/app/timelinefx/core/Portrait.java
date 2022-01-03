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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class Portrait extends AbstractPicture {

    public static final Comparator<Portrait> COMPARATOR = Comparator.comparingLong(FriezeObject::getId);

    private static final Logger LOG = Logger.getGlobal();

    private final Person person;
    private final ArrayList<Person> persons;

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath, int aWidth, int aHeight, long aTimestamp) {
        super(aPortraitID, aFilePath, aFilePath, aWidth, aHeight, aTimestamp);
        person = aPerson;
        persons = new ArrayList<>(1);
        persons.add(person);
    }

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath, int aWidth, int aHeight, LocalDate aDate) {
        super(aPortraitID, aFilePath, aFilePath, aWidth, aHeight, aDate);
        person = aPerson;
        persons = new ArrayList<>(1);
        persons.add(person);
    }

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath, int aWidth, int aHeight) {
        this(aPortraitID, aPerson, aFilePath, aWidth, aHeight, DEFAULT_TIMESTAMP);
    }

    @Override
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    @Override
    public boolean addPerson(Person aPerson) {
        LOG.log(Level.INFO, "No person can be added to a portrait. ({0}, {1})", new Object[]{this, aPerson});
        return false;
    }

    @Override
    public boolean removePerson(Person aPerson) {
        LOG.log(Level.INFO, "No person can be removed from a portrait. ({0}, {1})", new Object[]{this, aPerson});
        return false;
    }

    @Override
    public TimeLineProject getProject() {
        return person.getProject();
    }

    @Override
    public int compareTo(IFileObject other) {
        if (other == null) {
            return 1;
        } else if (other instanceof Portrait portrait) {
            return Long.compare(getAbsoluteTime(), portrait.getAbsoluteTime());
        }
        return getAbsolutePath().compareTo(other.getAbsolutePath());
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public boolean addPlace(Place aPlace) {
        return false;
    }

    @Override
    public boolean removePlace(Place aPlace) {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

}
