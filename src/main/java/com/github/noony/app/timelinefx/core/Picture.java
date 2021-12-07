/*
 * Copyright (C) 2020 NoOnY
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
public class Picture extends AbstractPicture {

    private final TimeLineProject project;

    protected Picture(TimeLineProject aProject, long id, String pictureName, LocalDate pictureCreationDate, String picturePath, int pictureWidth, int pictureHeight) {
        super(id, pictureName, picturePath, pictureWidth, pictureHeight, pictureCreationDate);
        project = aProject;
    }

    @Override
    public TimeLineProject getProject() {
        return project;
    }

    @Override
    public String toString() {
        return "Pic[" + getName() + "]";
    }

}
