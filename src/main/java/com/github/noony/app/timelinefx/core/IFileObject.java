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

/**
 * Contract for a domain object backed by a file relative to its project.
 *
 * @author hamon
 */
public interface IFileObject extends FriezeObject {

    /**
     * @return the object's display name
     */
    abstract String getName();

    //TODO :: in FriezeObject ?
    /**
     * @param aName the object's new display name
     */
    abstract void setName(String aName);

    /**
     * @return the project this object belongs to
     */
    abstract TimeLineProject getProject();

    /**
     * @return the object's file path, relative to the project's folder
     */
    abstract String getProjectRelativePath();

    /**
     * @return the object's absolute file path
     */
    abstract String getAbsolutePath();

    /**
     * @param other the object to compare with
     * @return a negative, zero, or positive value as this object precedes, equals, or follows {@code other}
     */
    abstract int compareTo(IFileObject other);
}
