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
 * A {@link StayPeriod} whose start/end times are represented as raw numeric values.
 *
 * @author hamon
 */
public class StayPeriodSimpleTime extends StayPeriod {

    /**
     * The start time before the last change, used to fire change notifications.
     */
    private double previousStartTime;

    /**
     * The end time before the last change, used to fire change notifications.
     */
    private double previousEndTime;

    /**
     * The stay's start time.
     */
    private double startTime;

    /**
     * The stay's end time.
     */
    private double endTime;

    protected StayPeriodSimpleTime(final Long anId, final Person aPerson, final double aStartTime, double anEndTime, Place aPlace) {
        super(anId, aPerson, aPlace);
        previousStartTime = aStartTime;
        previousEndTime = anEndTime;
        startTime = aStartTime;
        endTime = anEndTime;
    }

    @Override public double getPreviousStartDate() {
        return previousStartTime;
    }

    @Override
    public double getPreviousEndDate() {
        return previousEndTime;
    }

    @Override
    public double getStartDate() {
        return startTime;
    }

    @Override
    public double getEndDate() {
        return endTime;
    }

    /**
     * @param aStartDate the stay's new start time
     */
    public void setStartDate(final double aStartDate) {
        if (startTime != aStartDate) {
            previousStartTime = startTime;
            startTime = aStartDate;
            firePropertyChange(START_DATE_CHANGED, previousStartTime, startTime);
        }
    }

    /**
     * @param aEndDate the stay's new end time
     */
    public void setEndDate(final double aEndDate) {
        if (endTime != aEndDate) {
            previousEndTime = endTime;
            endTime = aEndDate;
            firePropertyChange(END_DATE_CHANGED, previousEndTime, endTime);
        }
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
