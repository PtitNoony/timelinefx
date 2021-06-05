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

/**
 *
 * @author hamon
 */
public class StayPeriodSimpleTime extends StayPeriod {

    private long startTime;
    private long endTime;

    protected StayPeriodSimpleTime(Long anId, Person aPerson, long aStartTime, long anEndTime, Place aPlace) {
        super(anId, aPerson, aPlace);
        startTime = aStartTime;
        endTime = anEndTime;
    }

    @Override
    public long getStartDate() {
        return startTime;
    }

    @Override
    public long getEndDate() {
        return endTime;
    }

    public void setStartDate(long aStartDate) {
        startTime = aStartDate;
    }

    public void setEndDate(long aEndDate) {
        endTime = aEndDate;
    }

    @Override
    public TimeFormat getTimeFormat() {
        return TimeFormat.TIME_MIN;
    }

    @Override
    public String getDisplayString() {
        return "Stay: " + getPerson().getName() + " @ " + getPlace().getName() + " [" + startTime + " -> " + endTime + "]";
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

}
