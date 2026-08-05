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

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A named point in time, optionally associated with persons and places.
 *
 * @author hamon
 */
public class Event {

    /**
     * The event's name.
     */
    private final String name;

    /**
     * The persons associated with this event.
     */
    private final List<Person> persons;

    /**
     * The places associated with this event.
     */
    private final List<Place> places;

    /**
     * Whether {@link #localDate} or {@link #date} holds this event's time value.
     */
    private final TimeFormat timeFormat;

    /**
     * This event's calendar date value, or null when {@link #timeFormat} is {@code TIME_MIN}.
     */
    private final LocalDate localDate;

    /**
     * This event's raw numeric time value.
     */
    private final long date;

    /**
     * Creates an event with a raw numeric time value.
     *
     * @param eventName the event's name
     * @param aDate the event's time value
     */
    public Event(final String eventName, long aDate) {
        name = eventName;
        persons = new LinkedList<>();
        places = new LinkedList<>();
        date = aDate;
        localDate = null;
        timeFormat = TimeFormat.TIME_MIN;
    }

    /**
     * Creates an event with a calendar date.
     *
     * @param eventName the event's name
     * @param aDate the event's date
     */
    public Event(final String eventName, LocalDate aDate) {
        name = eventName;
        persons = new LinkedList<>();
        places = new LinkedList<>();
        date = aDate.toEpochDay();
        localDate = aDate;
        timeFormat = TimeFormat.LOCAL_TIME;
    }

    /**
     * @return this event's raw numeric time value
     */
    public long getDate() {
        return date;
    }

    /**
     * @return this event's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return this event's time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * @return this event's calendar date value, or null if it uses a raw numeric time value
     */
    public LocalDate getLocalDate() {
        return localDate;
    }

    /**
     * @return an unmodifiable list of the persons associated with this event
     */
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    /**
     * @return an unmodifiable list of the places associated with this event
     */
    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

}
