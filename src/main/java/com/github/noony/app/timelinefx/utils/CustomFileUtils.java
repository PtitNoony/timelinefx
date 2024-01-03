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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class CustomFileUtils {

    private static final Logger LOG = Logger.getGlobal();

    private CustomFileUtils() {
        //private utility constructor
    }

    public static String fromAbsoluteToProjectRelative(TimeLineProject project, File file) {
        File projectFolder = project.getProjectFolder();
        if (projectFolder != null) {
            Path projectPath = projectFolder.toPath();
            Path selectedFilePath = file.toPath();
            try {
                Path relativePath = projectPath.relativize(selectedFilePath);
                return relativePath.toString();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception while using fromAbsoluteToProjectRelative: {0}", new Object[]{e});
            }
        } else {
            return file.getAbsolutePath();
        }
        return file.getAbsolutePath();
    }

    public static String fromProjectRelativeToAbsolute(TimeLineProject project, String relativePath) {
        File projectFolder = project.getProjectFolder();
        if (projectFolder != null) {
            return projectFolder.getAbsolutePath() + File.separator + relativePath;
        } else {
            return relativePath;
        }
    }

    public static long[] toLongArray(String input) {
        String[] valuesAsString = input.replace("[", "").replace("]", "").split(", ");
        long[] result = new long[valuesAsString.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Long.parseLong(valuesAsString[i]);
        }
        return result;
    }

    public static double[] toDoubleArray(String input) {
        String[] valuesAsString = input.replace("[", "").replace("]", "").split(", ");
        double[] result = new double[valuesAsString.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(valuesAsString[i]);
        }
        return result;
    }

}
