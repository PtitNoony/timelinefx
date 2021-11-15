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
package com.github.noony.app.timelinefx.core;

import com.github.noony.app.timelinefx.save.XMLHandler;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class TimeLineProjectFactory {

    private static final Logger LOG = Logger.getGlobal();

    private TimeLineProjectFactory() {
        // private utility constructor
    }

    public static final TimeLineProject createProject(String name, Map<String, String> configParams) {
        TimeLineProject timeLineProject = new TimeLineProject(name, configParams);
        FriezeObjectFactory.reset();
        return timeLineProject;
    }

    public static TimeLineProject loadProject(File aFile) {
        File timelineFile = null;
        if (aFile == null) {
            throw new IllegalStateException("Project File cannot be null.");
        } else if (aFile.isFile()) {
            timelineFile = aFile;
            LOG.log(Level.INFO, "Project Folder:: {0}", new Object[]{timelineFile.getParent()});
        } else {
            File projectFolder = aFile;
            File fileFound = Arrays.asList(projectFolder.listFiles()).stream().filter(file -> file.getName().endsWith("xml")).findAny().orElse(null);
            if (fileFound == null) {
                throw new IllegalStateException("No save file was found in " + projectFolder);
            }
            timelineFile = fileFound;
        }
        return XMLHandler.loadFile(timelineFile);
    }
}
