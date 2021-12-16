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

import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class MathUtils {

    public static final double HALF_PI = Math.PI / 2.0;

    public static final double getAngle(Point2D point1, Point2D point2) {
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

}
