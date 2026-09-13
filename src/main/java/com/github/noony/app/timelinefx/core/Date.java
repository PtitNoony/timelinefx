/*
 * Copyright (C) 2026 NoOnY
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

import com.github.noony.app.timelinefx.utils.DateUtils;
import com.github.noony.app.timelinefx.utils.MathUtils;
import com.github.noony.app.timelinefx.utils.TimeFormatToString;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class Date {

    private static final Logger LOG = Logger.getGlobal();

    private final TimeFormat timeFormat;

    private LocalDate dateAsLocal;

    private double dateAsDouble;

    public Date(LocalDate aDateAsLocal) {
        timeFormat = TimeFormat.LOCAL_TIME;
        if (aDateAsLocal == null) {
            dateAsLocal = IDateObject.DEFAULT_DATE;
        } else {
            dateAsLocal = aDateAsLocal;
        }
        dateAsDouble = IDateObject.DEFAULT_TIMESTAMP;
    }

    public Date(double aDateAsDouble) {
        timeFormat = TimeFormat.TIME_MIN;
        dateAsLocal = IDateObject.DEFAULT_DATE;
        dateAsDouble = aDateAsDouble;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public double getDateAsDouble() {
        return dateAsDouble;
    }

    public LocalDate getDateAsLocal() {
        return dateAsLocal;
    }

    public void setDateAsDouble(double aDateAsDouble) {
        dateAsDouble = aDateAsDouble;
    }

    public void setDateAsLocal(LocalDate aDateAsLocal) {
        dateAsLocal = aDateAsLocal;
    }

    public void setValue(Object aValue) {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                try {
                    dateAsLocal = (LocalDate) aValue;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Could not convert {0} to localDate {1}.", new Object[]{aValue, e.getMessage()});
                }
            }
            case TIME_MIN -> {
                try {
                    dateAsDouble = (Double) aValue;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Could not convert {0} to double {1}.", new Object[]{aValue, e.getMessage()});
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported timeFormat: " + timeFormat.name());
        }
    }

    public double getAbsoluteTime() {
        return switch (timeFormat) {
            case LOCAL_TIME ->
                dateAsLocal.toEpochDay();
            case TIME_MIN ->
                dateAsDouble;
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + timeFormat.name());
        };
    }

    public String getAbsoluteTimeAsString() {
       return switch (timeFormat) {
            case LOCAL_TIME ->
                 dateAsLocal.format(TimeFormatToString.DATE_TIME_FORMATTER);
            case TIME_MIN ->
                 MathUtils.doubleToString(dateAsDouble);
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + timeFormat.name());
        };
    }

    @Override
    public String toString() {
        return switch (timeFormat) {
            case LOCAL_TIME ->
                DateUtils.CONVERTER.toString(dateAsLocal);
            case TIME_MIN ->
                Double.toString(dateAsDouble);
            default ->
                super.toString();
        };
    }

}
