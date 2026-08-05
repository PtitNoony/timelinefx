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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;

/**
 * A person's stay at a place, over a period of time represented either as calendar dates
 * ({@link StayPeriodLocalDate}) or as raw numeric values ({@link StayPeriodSimpleTime}).
 *
 * @author arnaud
 */
public abstract class StayPeriod implements FriezeObject {

    /**
     * Orders stays by start date.
     */
    public static final Comparator<? super StayPeriod> STAY_COMPARATOR = Comparator.comparingDouble(StayPeriod::getStartDate);

    /**
     * Name of the property change event fired when the stay's person changes.
     */
    public static final String PERSON_CHANGED = "StayPeriod__personChanged";

    /**
     * Name of the property change event fired when the stay's place changes.
     */
    public static final String PLACE_CHANGED = "StayPeriod__placeChanged";

    /**
     * Name of the property change event fired when the stay's start date changes.
     */
    public static final String START_DATE_CHANGED = "StayPeriod__startDateChanged";

    /**
     * Name of the property change event fired when the stay's end date changes.
     */
    public static final String END_DATE_CHANGED = "StayPeriod__endDateChanged";

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport;

    private final Long id;

    /**
     * The person staying.
     */
    private Person person;

    /**
     * The place stayed at.
     */
    private Place place;

    @SuppressWarnings("this-escape")
    protected StayPeriod(final long anId, Person aPerson, Place aPlace) {
        id = anId;
        propertyChangeSupport = new PropertyChangeSupport(StayPeriod.this);
        person = aPerson;
        place = aPlace;
    }

    @Override
    public long getId() {
        return id;
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
     * @return the person staying
     */
    public Person getPerson() {
        return person;
    }

    /**
     * @return the place stayed at
     */
    public Place getPlace() {
        return place;
    }

    /**
     * @param aPerson the stay's new person
     */
    public void setPerson(final Person aPerson) {
        if (aPerson == null) {
            return;
        }
        if (aPerson != person) {
            person = aPerson;
            propertyChangeSupport.firePropertyChange(PERSON_CHANGED, this, person);
        }
    }

    /**
     * @param aPlace the stay's new place
     */
    public void setPlace(final Place aPlace) {
        if (aPlace == null) {
            return;
        }
        if (aPlace != place) {
            place = aPlace;
            propertyChangeSupport.firePropertyChange(PLACE_CHANGED, this, place);
        }
    }

    /**
     * @return the start date before the last change
     */
    public abstract double getPreviousStartDate();

    /**
     * @return the end date before the last change
     */
    public abstract double getPreviousEndDate();

    /**
     * @return the stay's start date
     */
    public abstract double getStartDate();

    /**
     * @return the stay's end date
     */
    public abstract double getEndDate();

    /**
     * @return the time format used to represent this stay's dates
     */
    public abstract TimeFormat getTimeFormat();

    /**
     * @return a human-readable description of this stay
     */
    public abstract String getDisplayString();

    protected void firePropertyChange(final String eventName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(eventName, oldValue, newValue);
    }
}
