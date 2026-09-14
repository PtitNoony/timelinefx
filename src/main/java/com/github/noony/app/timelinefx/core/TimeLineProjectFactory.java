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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for creating a new {@link TimeLineProject} or loading one from disk.
 *
 * @author hamon
 */
public final class TimeLineProjectFactory {

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    private TimeLineProjectFactory() {
        // private utility constructor
    }

    /**
     * Creates a new, empty project.
     *
     * @param name the project's name
     * @param configParams optional configuration overrides (folder locations, etc.)
     * @return the created project
     */
    public static TimeLineProject createProject(final String name, final Map<String, String> configParams) {
        return createProject(name, configParams, TimeFormat.LOCAL_TIME);
    }

    /**
     * Creates a new, empty project with an explicit time format.
     *
     * @param name the project's name
     * @param configParams optional configuration overrides (folder locations, etc.)
     * @param timeFormat the time format to use for stays created in this project
     * @return the created project
     */
    public static TimeLineProject createProject(final String name, final Map<String, String> configParams, final TimeFormat timeFormat) {
        final TimeLineProject timeLineProject = new TimeLineProject(name, configParams, timeFormat);
        FriezeObjectFactory.reset();
        return timeLineProject;
    }

    /**
     * Loads a project from a save file, or from the single save file found in a folder.
     *
     * @param aFile the project's save file, or its containing folder
     * @return the loaded project
     */
    public static TimeLineProject loadProject(final File aFile) {
        final File timelineFile;
        if (aFile == null) {
            throw new IllegalStateException("Project File cannot be null.");
        } else if (aFile.isFile()) {
            timelineFile = aFile;
            LOG.log(Level.INFO, "Project Folder:: {0}.", new Object[]{timelineFile.getParent()});
        } else {
            final File fileFound = Arrays.stream(Objects.requireNonNull(aFile.listFiles())).filter(file -> file.getName().endsWith("xml")).findAny().orElse(null);
            if (fileFound == null) {
                throw new IllegalStateException("No save file was found in " + aFile);
            }
            timelineFile = fileFound;
        }
        final var timeline = XMLHandler.loadFile(timelineFile);
        LOG.log(Level.FINE, "Project created: {0}", new Object[]{timeline});
        return timeline;
    }
}
