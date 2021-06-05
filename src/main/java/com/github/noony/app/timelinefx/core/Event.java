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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamon
 */
public class Event {

    private final String name;
    private final List<Person> persons;
    private final List<Place> places;

    private final TimeFormat timeFormat;
    private final LocalDate localDate;
    private final long date;

    public Event(String eventName, long aDate) {
        name = eventName;
        persons = new LinkedList<>();
        places = new LinkedList<>();
        date = aDate;
        localDate = null;
        timeFormat = TimeFormat.TIME_MIN;
    }

    public Event(String eventName, LocalDate aDate) {
        name = eventName;
        persons = new LinkedList<>();
        places = new LinkedList<>();
        date = aDate.toEpochDay();
        localDate = aDate;
        timeFormat = TimeFormat.LOCAL_TIME;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

}
