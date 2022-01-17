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

import com.github.noony.app.timelinefx.utils.MathUtils;
import com.github.noony.app.timelinefx.utils.TimeFormatToString;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class DateObject implements IDateObject {

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport;
    //
    private TimeFormat timeFormat;
    private double timestamp;
    private LocalDate date;

    public DateObject(LocalDate aDate) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        timeFormat = TimeFormat.LOCAL_TIME;
        date = aDate != null ? aDate : LocalDate.MIN;
        timestamp = -1;
    }

    public DateObject(double aTimestamp) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        timeFormat = TimeFormat.TIME_MIN;
        timestamp = aTimestamp;
        date = null;
    }

    public DateObject(IDateObject anotherDateObject) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        timeFormat = anotherDateObject.getTimeFormat();
        switch (timeFormat) {
            case LOCAL_TIME ->
                date = LocalDate.ofEpochDay(anotherDateObject.getDate().toEpochDay());
            case TIME_MIN ->
                timestamp = anotherDateObject.getTimestamp();
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
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
    public void setDate(LocalDate aDate) {
        if (aDate != null && !date.equals(aDate)) {
            date = aDate;
            timeFormat = TimeFormat.LOCAL_TIME;
            propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, date);
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
    public void setTimestamp(double aTimestamp) {
        if (aTimestamp != timestamp) {
            timestamp = aTimestamp;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_CHANGED, timeFormat, timestamp);
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

    // TODO add to interface ???
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
