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
 *
 * @author arnaud
 */
public abstract class StayPeriod extends FriezeObject {

    public static final Comparator<? super StayPeriod> STAY_COMPARATOR = Comparator.comparingDouble(StayPeriod::getStartDate);

    public static final String PERSON_CHANGED = "StayPeriod__personChanged";
    public static final String PLACE_CHANGED = "StayPeriod__placeChanged";
    public static final String START_DATE_CHANGED = "StayPeriod__startDateChanged";
    public static final String END_DATE_CHANGED = "StayPeriod__endDateChanged";
    private final PropertyChangeSupport propertyChangeSupport;
    private Person person;
    private Place place;

    public StayPeriod(long id, Person aPerson, Place aPlace) {
        super(id);
        propertyChangeSupport = new PropertyChangeSupport(StayPeriod.this);
        person = aPerson;
        place = aPlace;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Person getPerson() {
        return person;
    }

    public Place getPlace() {
        return place;
    }

    public void setPerson(Person aPerson) {
        if (aPerson == null) {
            return;
        }
        if (aPerson != person) {
            person = aPerson;
            propertyChangeSupport.firePropertyChange(PERSON_CHANGED, this, person);
        }
    }

    public void setPlace(Place aPlace) {
        if (aPlace == null) {
            return;
        }
        if (aPlace != place) {
            place = aPlace;
            propertyChangeSupport.firePropertyChange(PLACE_CHANGED, this, place);
        }
    }

    public abstract double getPreviousStartDate();

    public abstract double getPreviousEndDate();

    public abstract double getStartDate();

    public abstract double getEndDate();

    public abstract TimeFormat getTimeFormat();

    public abstract String getDisplayString();

    protected void firePropertyChange(String eventName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(eventName, oldValue, newValue);
    }
}
