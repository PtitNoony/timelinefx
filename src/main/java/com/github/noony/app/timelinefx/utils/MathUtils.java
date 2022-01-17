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
package com.github.noony.app.timelinefx.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public final class MathUtils {

    public static final double HALF_PI = Math.PI / 2.0;

    private static DecimalFormat DEFAULT_DECIMAL_FORMAT = null;

    public static double getAngle(Point2D point1, Point2D point2) {
        if (point1.getY() == point2.getY()) {
            if (point2.getX() > point1.getX()) {
                return 0.0;
            } else {
                return Math.PI;
            }
        } else {
            return Math.atan2(point2.getY() - point1.getY(), point2.getX() - point1.getX());
        }
    }

    /**
     * Function to convert double values to string without trailing zeros
     *
     * @param value the double value to convert
     * @return the corresponding string
     */
    public static String doubleToString(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        } else {
            if (DEFAULT_DECIMAL_FORMAT == null) {
                DEFAULT_DECIMAL_FORMAT = new DecimalFormat("0.00");
                DEFAULT_DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(false);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                DEFAULT_DECIMAL_FORMAT.setDecimalFormatSymbols(symbols);
            }
            return DEFAULT_DECIMAL_FORMAT.format(value);
        }
    }

    /**
     * Converts the angle value from radian to degree
     *
     * @param value angle value
     * @return the corresponding degree value
     */
    public static double toDegree(double value) {
        return value * 180.0 / Math.PI;
    }

    /**
     * Converts the angle value from degree to radian
     *
     * @param value angle value
     * @return the corresponding radian value
     */
    public static double toRadian(double value) {
        return value * Math.PI / 180.0;
    }

}
