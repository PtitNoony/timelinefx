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

package com.github.noony.app.timelinefx.drawings;

/**
 * This enum is intended to describe / constraint the allowed movements of various nodes while drag and dropping them.
 *
 * @author hamon
 */
public enum InteractiveDragType {

    /**
     * No movement allowed.
     */
    NOT_ALLOWED,

    /**
     * Position update only allowed along the X-axis.
     */
    X_ONLY,

    /**
     * Position update only allowed along the Y-axis.
     */
    Y_ONLY,

    /**
     * No movement restriction.
     */
    FREE_TRANSLATION

}
