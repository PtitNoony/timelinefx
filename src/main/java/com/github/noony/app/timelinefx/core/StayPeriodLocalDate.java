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

import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 *
 * @author hamon
 */
public class StayPeriodLocalDate extends StayPeriod {

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private LocalDate startDate;
    private LocalDate endDate;

    protected StayPeriodLocalDate(long id, Person aPerson, LocalDate startDate, LocalDate endDate, Place aPlace) {
        super(id, aPerson, aPlace);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public long getStartDate() {
        return startDate.toEpochDay();
    }

    @Override
    public long getEndDate() {
        return endDate.toEpochDay();
    }

    @Override
    public TimeFormat getTimeFormat() {
        return TimeFormat.LOCAL_TIME;
    }

    public void setStartDate(LocalDate aStartDate) {
        if (aStartDate == null) {
            return;
        }
        if (!startDate.isEqual(aStartDate)) {
            var oldStartDate = startDate;
            startDate = aStartDate;
            firePropertyChange(START_DATE_CHANGED, oldStartDate.toEpochDay(), startDate.toEpochDay());
        }
    }

    public void setEndDate(LocalDate aEndDate) {
        if (aEndDate == null) {
            return;
        }
        if (!endDate.isEqual(aEndDate)) {
            var oldEndDate = endDate;
            endDate = aEndDate;
            firePropertyChange(END_DATE_CHANGED, oldEndDate.toEpochDay(), endDate.toEpochDay());
        }
    }

    @Override
    public String getDisplayString() {
        return "Stay: " + getPerson().getName() + " @ " + getPlace().getName() + " [" + startDate + " -> " + endDate + "]";
    }
}
