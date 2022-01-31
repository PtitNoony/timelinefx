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
package com.github.noony.app.timelinefx.utils;

import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author hamon
 */
public class CustomFileUtils {

    private CustomFileUtils() {
        //private utility constructor
    }

    public static String fromAbsoluteToProjectRelative(TimeLineProject project, File file) {
        File projectFolder = project.getProjectFolder();
        if (projectFolder != null) {
            Path projectPath = projectFolder.toPath();
            Path selectedFilePath = file.toPath();
            Path relativePath = projectPath.relativize(selectedFilePath);
            return relativePath.toString();
        } else {
            return file.getAbsolutePath();
        }
    }

    public static String fromProjectRelativeToAbsolute(TimeLineProject project, String relavivePath) {
        File projectFolder = project.getProjectFolder();
        if (projectFolder != null) {
            return projectFolder.getAbsolutePath() + File.separator + relavivePath;
        } else {
            return relavivePath;
        }
    }

}
