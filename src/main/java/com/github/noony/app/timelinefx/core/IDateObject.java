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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author hamon
 */
public interface IDateObject {

    DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * The default time stamp
     */
    long DEFAULT_TIMESTAMP = 0;

    /**
     * Name of the property change event for date change
     */
    String DATE_CHANGED = "pictureDateChanged";

    /**
     *
     * @return the TimeFormat used by the instance
     */
    TimeFormat getTimeFormat();

    /**
     *
     * @param aTimeFormat the new time format to be used by the instance
     */
    void setTimeFormat(TimeFormat aTimeFormat);

    /**
     *
     * @return the instance date value, or null if not set or if the time format is not compatible
     */
    LocalDate getDate();

    /**
     *
     * @return the instance time stamp
     */
    double getTimestamp();

    /**
     *
     * @param aTimeValue updates the time value based on the input parameter
     */
    void setValue(String aTimeValue);

    /**
     *
     * @param aDate the instance's new date value
     */
    void setDate(LocalDate aDate);

    /**
     *
     * @param aTimestamp the instance new time stamp
     */
    void setTimestamp(double aTimestamp);

    /**
     *
     * @param aDateObject the date object containing the date to be retreived
     */
    void setDate(IDateObject aDateObject);

    /**
     *
     * @return an absolute time value to compare dateObjects no matter their time format
     */
    double getAbsoluteTime();

    /**
     *
     * @return a string representation of the time no matter the time format
     */
    String getAbsoluteTimeAsString();
}
