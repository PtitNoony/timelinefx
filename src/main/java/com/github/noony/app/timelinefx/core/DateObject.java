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
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Standalone {@link IDateObject} implementation, used where a date/time value is needed
 * without being attached to a picture or stay.
 *
 * @author hamon
 */
public class DateObject implements IDateObject {

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Support object used to fire property change events.
     */
    protected final PropertyChangeSupport propertyChangeSupport;

    /**
     * This instance's date.
     */
    private final Date date;


    /**
     * Creates a date object holding a calendar date.
     *
     * @param aDate the date value
     */
    @SuppressWarnings("this-escape")
    public DateObject(final Date aDate) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        date = aDate;
    }

    /**
     * Creates a date object holding a calendar date.
     *
     * @param aDate the date value
     */
    @SuppressWarnings("this-escape")
    public DateObject(final LocalDate aDate) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        date = new Date(aDate);
    }

    /**
     * Creates a date object holding a raw numeric timestamp.
     *
     * @param aTimestamp the timestamp value
     */
    @SuppressWarnings("this-escape")
    public DateObject(final double aTimestamp) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        date = new Date(aTimestamp);
    }

    /**
     * Creates a date object copying the value of another one.
     *
     * @param anotherDateObject the date object to copy
     */
    @SuppressWarnings("this-escape")
    public DateObject(final IDateObject anotherDateObject) {
        propertyChangeSupport = new PropertyChangeSupport(DateObject.this);
        //
        switch (anotherDateObject.getTimeFormat()) {
            case LOCAL_TIME ->
                date = new Date(anotherDateObject.getDate());
            case TIME_MIN ->
                date = new Date(anotherDateObject.getTimestamp());
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + anotherDateObject.getTimeFormat());
        }
    }

    @Override public TimeFormat getTimeFormat() {
        return date.getTimeFormat();
    }

    @Override
    public void setValue(final String aTimeValue) {
        if (aTimeValue == null) {
            return;
        }
        switch (date.getTimeFormat()) {
            case LOCAL_TIME -> {
                try {
                    final var newDate = LocalDate.parse(aTimeValue);
                    if (!newDate.isEqual(date.getDateAsLocal())) {
                        date.setDateAsLocal(newDate);
                        propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Could not set date value to {0}, with '{1}': error: {2}",
                            new Object[]{this, aTimeValue, e.getMessage()});
                }
            }
            case TIME_MIN -> {
                try {
                    date.setDateAsDouble(Double.parseDouble(aTimeValue));
                } catch (NumberFormatException e) {
                    LOG.log(Level.WARNING, "Could not set timestamp value to {0}, with '{1}': error: {2}",
                            new Object[]{this, aTimeValue, e.getMessage()});
                }
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
            }
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + date.getTimeFormat());
        }
    }

    @Override
    public void setDate(final LocalDate aDate) {
        if (aDate != null && !date.getDateAsLocal().equals(aDate)) {
            date.setDateAsLocal(aDate);
            if(date.getTimeFormat() == TimeFormat.LOCAL_TIME){
            propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
            }
        }
    }

    @Override
    public void setDate(final IDateObject aDateObject) {
        if (aDateObject == null) {
            return;
        }
        switch (date.getTimeFormat()) {
            case LOCAL_TIME -> {
                date.setDateAsLocal(LocalDate.ofEpochDay(aDateObject.getDate().toEpochDay()));
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
            }
            case TIME_MIN -> {
                date.setDateAsDouble(aDateObject.getTimestamp());
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
            }
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + date.getTimeFormat());
        }
    }

    @Override
    public void setTimestamp(final double aTimestamp) {
        if (date.getDateAsDouble() != aTimestamp) {
            date.setDateAsDouble(aTimestamp);
            if(date.getTimeFormat() == TimeFormat.TIME_MIN){
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, date.getTimeFormat(), date);
            }
        }
    }

    @Override
    public double getAbsoluteTime() {
        return date.getAbsoluteTime();
    }

    @Override
    public String getAbsoluteTimeAsString() {
        return date.getAbsoluteTimeAsString();
    }

    @Override
    public LocalDate getDate() {
        return date.getDateAsLocal();
    }

    @Override
    public double getTimestamp() {
        return date.getDateAsDouble();
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
