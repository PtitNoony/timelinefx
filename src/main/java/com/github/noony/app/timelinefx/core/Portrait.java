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
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;

/**
 *
 * @author hamon
 */
public class Portrait extends FriezeObject implements IFileObject, IDateObject {

    public static final String TIME_CHANGED = "portraitTimeChanged";

    public static final Comparator<Portrait> COMPARATOR = (p1, p2) -> Long.compare(p1.getId(), p2.getId());

    public static final long DEFAULT_TIMESTAMP = 0;

    private final PropertyChangeSupport propertyChangeSupport;
    private final Person person;
    private final String filePath;
    //
    private TimeFormat timeFormat;
    private long timestamp;
    private LocalDate date;

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath, long aTimestamp) {
        super(aPortraitID);
        propertyChangeSupport = new PropertyChangeSupport(Portrait.this);
        person = aPerson;
        filePath = aFilePath;
        timeFormat = TimeFormat.TIME_MIN;
        timestamp = aTimestamp;
        date = null;
    }

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath, LocalDate aDate) {
        super(aPortraitID);
        propertyChangeSupport = new PropertyChangeSupport(Portrait.this);
        person = aPerson;
        filePath = aFilePath;
        timeFormat = TimeFormat.LOCAL_TIME;
        date = aDate;
        timestamp = -1;
    }

    protected Portrait(long aPortraitID, Person aPerson, String aFilePath) {
        this(aPortraitID, aPerson, aFilePath, DEFAULT_TIMESTAMP);
    }

    @Override
    public String getName() {
        return toString();
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    @Override
    public void setTimeFormat(TimeFormat aTimeFormat) {
        timeFormat = aTimeFormat;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setDate(LocalDate aDate) {
        date = aDate;
        timeFormat = TimeFormat.LOCAL_TIME;
        propertyChangeSupport.firePropertyChange(TIME_CHANGED, timeFormat, date);
    }

    @Override
    public void setTimestamp(long aTimestamp) {
        timestamp = aTimestamp;
        timeFormat = TimeFormat.TIME_MIN;
        propertyChangeSupport.firePropertyChange(TIME_CHANGED, timeFormat, timestamp);
    }

    @Override
    public long getAbsoluteTime() {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                return date.toEpochDay();
            }
            case TIME_MIN -> {
                return timestamp;
            }
        }
        throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
    }

    @Override
    public String toString() {
        return filePath;
    }

}
