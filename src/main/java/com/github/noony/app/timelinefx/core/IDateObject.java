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

/**
 *
 * @author hamon
 */
public interface IDateObject {

    long DEFAULT_TIMESTAMP = 0;

    TimeFormat getTimeFormat();

    void setTimeFormat(TimeFormat aTimeFormat);

    LocalDate getDate();

    long getTimestamp();

    void setDate(LocalDate aDate);

    void setTimestamp(long aTimestamp);

    long getAbsoluteTime();

    String getAbsoluteTimeAsString();
}
