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
package com.github.noony.app.timelinefx.core.freemap;

/**
 *
 * @author hamon
 */
public interface GridPositionable {

    public static final String POS_CHANGED = "posChanged";
    public static final String PLOT_SIZE_CHANGED = "plotSizeChanged";
    public static final String PLOT_VISIBILITY_CHANGED = "plotVisibilityChanged";
    public static final String PLOT_COLOR_CHANGED = "plotColorChanged";

    public static final double EPSILON = 0.000000001;

    void setVisible(boolean visibility);

    void setPosition(double newX, double newY);

    boolean isVisible();

    double getX();

    double getY();

}
