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

import java.util.Comparator;

/**
 *
 * @author arnaud
 */
public abstract class StayPeriod extends FriezeObject {

    public static final Comparator<? super StayPeriod> STAY_COMPARATOR = (s1, s2) -> Long.compare(s1.getStartDate(), s2.getStartDate());

    private Person person;
    private Place place;

    public StayPeriod(long id, Person aPerson, Place aPlace) {
        super(id);
        person = aPerson;
        place = aPlace;
    }

    public Person getPerson() {
        return person;
    }

    public Place getPlace() {
        return place;
    }

    public void setPerson(Person aPerson) {
        person = aPerson;
    }

    public void setPlace(Place aPlace) {
        place = aPlace;
    }

    public abstract long getStartDate();

    public abstract long getEndDate();

    public abstract TimeFormat getTimeFormat();

    public abstract String getDisplayString();
}
