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

import static com.github.noony.app.timelinefx.core.IDateObject.DATE_CHANGED;
import com.github.noony.app.timelinefx.utils.MathUtils;
import com.github.noony.app.timelinefx.utils.TimeFormatToString;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public abstract class AbstractPicture extends FriezeObject implements IPicture {

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport;

    private final List<Person> persons;
    private final List<Place> places;
    private final String filePath;
    private final int width;
    private final int height;
    //
    private String name;
    //
    private TimeFormat timeFormat;
    private double timestamp;
    private LocalDate date;

    protected AbstractPicture(long anID, String aName, String aFilePath, int aWidth, int aHeight, LocalDate aDate) {
        super(anID);
        propertyChangeSupport = new PropertyChangeSupport(AbstractPicture.this);
        persons = new LinkedList<>();
        places = new LinkedList<>();
        width = aWidth;
        height = aHeight;
        filePath = aFilePath;
        //
        timeFormat = TimeFormat.LOCAL_TIME;
        date = aDate != null ? aDate : LocalDate.MIN;
        timestamp = -1;
        //
        name = aName;
    }

    protected AbstractPicture(long anID, String aName, String aFilePath, int aWidth, int aHeight, double aTimestamp) {
        super(anID);
        propertyChangeSupport = new PropertyChangeSupport(AbstractPicture.this);
        persons = new LinkedList<>();
        places = new LinkedList<>();
        width = aWidth;
        height = aHeight;
        filePath = aFilePath;
        //
        timeFormat = TimeFormat.TIME_MIN;
        timestamp = aTimestamp;
        date = null;
        //
        name = aName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String aName) {
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
    public boolean addPerson(Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePerson(Person aPerson) {
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
    public boolean addPlace(Place aPlace) {
        if (!places.contains(aPlace)) {
            places.add(aPlace);
            propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlace(Place aPlace) {
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
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    @Override
    public void setTimeFormat(TimeFormat aTimeFormat) {
        timeFormat = aTimeFormat;
        switch (timeFormat) {
            case LOCAL_TIME ->
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, date);
            case TIME_MIN ->
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, timestamp);
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public void setDate(LocalDate aDate) {
        if (aDate != null) {
            if (date != null && !date.equals(aDate)) {
                date = aDate;
                timeFormat = TimeFormat.LOCAL_TIME;
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, date);
            }
        }
    }

    @Override
    public void setValue(String aTimeValue) {
        if (aTimeValue == null) {
            return;
        }
        switch (timeFormat) {
            case LOCAL_TIME -> {
                try {
                    var newDate = LocalDate.parse(aTimeValue);
                    if (!newDate.isEqual(date)) {
                        date = newDate;
                        propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, date);
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Could not set date value to {0}, with '{1}': error: {2}", new Object[]{this, aTimeValue, e.getMessage()});
                }
            }
            case TIME_MIN -> {
                try {
                    timestamp = Double.parseDouble(aTimeValue);
                } catch (NumberFormatException e) {
                    LOG.log(Level.WARNING, "Could not set timestamp value to {0}, with '{1}': error: {2}", new Object[]{this, aTimeValue, e.getMessage()});
                }
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, timestamp);
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
    }

    @Override
    public void setTimestamp(double aTimestamp) {
        if (aTimestamp != timestamp) {
            timestamp = aTimestamp;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, timestamp);
        }
    }

    @Override
    public void setDate(IDateObject aDateObject) {
        if (aDateObject == null) {
            return;
        }
        timeFormat = aDateObject.getTimeFormat();
        switch (timeFormat) {
            case LOCAL_TIME -> {
                date = LocalDate.ofEpochDay(aDateObject.getDate().toEpochDay());
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, date);
            }
            case TIME_MIN -> {
                timestamp = aDateObject.getTimestamp();
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, timestamp);
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
    }

    @Override
    public double getAbsoluteTime() {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                return date.toEpochDay();
            }
            case TIME_MIN -> {
                return timestamp;
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
    }

    @Override
    public String getAbsoluteTimeAsString() {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                return date.format(TimeFormatToString.DATE_TIME_FORMATTER);
            }
            case TIME_MIN -> {
                return MathUtils.doubleToString(timestamp);
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }

    }

    @Override
    public String getProjectRelativePath() {
        return filePath;
    }

    @Override
    public String getAbsolutePath() {
        return getProject().getProjectFolder().getAbsolutePath() + File.separator + filePath;
    }

    // TODO add to interface ???
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
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

    @Override
    public int compareTo(IFileObject other) {
        if (other == null) {
            return 1;
        }
        var timeComparison = Double.compare(getAbsoluteTime(), getAbsoluteTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        return getAbsolutePath().compareTo(other.getAbsolutePath());
    }
}
