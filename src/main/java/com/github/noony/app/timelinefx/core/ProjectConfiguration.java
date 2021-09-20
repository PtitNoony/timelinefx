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

import com.github.noony.app.timelinefx.Configuration;
import com.github.noony.app.timelinefx.hmi.ConfigurationViewController;
import com.github.noony.app.timelinefx.save.XMLHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class ProjectConfiguration {

    public static final String DEFAULT_PORTRAITS_FOLDER = "portraits";
    public static final String DEFAULT_PICTURES_FOLDER = "pictures";
    public static final String DEFAULT_MINIATURES_FOLDER = "miniatures";

    private static final Logger LOG = Logger.getGlobal();

    private static File PROJECT_FOLDER = null;
    private static File PORTRAITS_FOLDER = null;
    private static File PICTURES_FOLDER = null;
    private static File MINIATURES_FOLDER = null;
    private static File TIMELINE_FILE = null;
    private static String PROJECT_LOCATION = null;

    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(ConfigurationViewController.class);

    private ProjectConfiguration() {
        // private utility constructor
    }

    public static TimeLineProject createProject(String projectName) {
        LOG.log(Level.INFO, "Creating project :: {0}", projectName);
        File projectsFolder = new File(Configuration.getProjectsParentFolder());
        if (!projectsFolder.exists()) {
            try {
                Path path = projectsFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create projects folder : {0}", ex);
                return null;
            }
        }
        String projectRoot = Configuration.getProjectsParentFolder() + File.separator + projectName;
        PROJECT_FOLDER = new File(projectRoot);
        if (!PROJECT_FOLDER.exists()) {
            try {
                Path path = PROJECT_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create project folder : {0}", ex);
                return null;
            }
        }
        PROJECT_LOCATION = PROJECT_FOLDER.getAbsolutePath();
        TIMELINE_FILE = new File(PROJECT_LOCATION + File.separator + projectName + ".xml");
        //
        String portraitsRoot = PROJECT_FOLDER + File.separator + DEFAULT_PORTRAITS_FOLDER;
        PORTRAITS_FOLDER = new File(portraitsRoot);
        if (!PORTRAITS_FOLDER.exists()) {
            try {
                Path path = PORTRAITS_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create portrait folder : {0}", ex);
                return null;
            }
        }
        String picturesRoot = PROJECT_FOLDER + File.separator + DEFAULT_PICTURES_FOLDER;
        PICTURES_FOLDER = new File(picturesRoot);
        if (!PICTURES_FOLDER.exists()) {
            try {
                Path path = PICTURES_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create pictures folder : {0}", ex);
                return null;
            }
        }
        MINIATURES_FOLDER = new File(PROJECT_FOLDER + File.separator + DEFAULT_MINIATURES_FOLDER);
        if (!MINIATURES_FOLDER.exists()) {
            try {
                Path path = MINIATURES_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create miniature folder : {0}", ex);
                return null;
            }
        }
        //
        savePortraitRessources();
        //
        return TimeLineProjectFactory.createTimeline(projectName);
    }

    public static TimeLineProject loadProject(File aFile) {
        if (aFile == null) {
            throw new IllegalStateException("Project File cannot be null.");
        } else if (aFile.isFile()) {
            TIMELINE_FILE = aFile;
            PROJECT_FOLDER = TIMELINE_FILE.getParentFile();
            LOG.log(Level.INFO, "Project Folder:: {0}", new Object[]{Configuration.getProjectsParentFolder()});
        } else {
            PROJECT_FOLDER = aFile;
            File fileFound = Arrays.asList(PROJECT_FOLDER.listFiles()).stream().filter(file -> file.getName().endsWith("xml")).findAny().orElse(null);
            if (fileFound == null) {
                throw new IllegalStateException("No save file was found in " + PROJECT_FOLDER);
            }
            TIMELINE_FILE = fileFound;
        }
        PROJECT_LOCATION = PROJECT_FOLDER.getAbsolutePath();
        // TODO factorize code
        String picturesRoot = PROJECT_FOLDER + File.separator + DEFAULT_PICTURES_FOLDER;
        PICTURES_FOLDER = new File(picturesRoot);
        if (!PICTURES_FOLDER.exists()) {
            try {
                Path path = PICTURES_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create pictures folder : {0}", ex);
                return null;
            }
        }
        // TODO handle code duplication
        MINIATURES_FOLDER = new File(PROJECT_FOLDER + File.separator + DEFAULT_MINIATURES_FOLDER);
        if (!MINIATURES_FOLDER.exists()) {
            try {
                Path path = MINIATURES_FOLDER.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create miniature folder : {0}", ex);
                return null;
            }
        }
        //
        return XMLHandler.loadFile(TIMELINE_FILE);
    }

    public static File getProjectFolder() {
        return PROJECT_FOLDER;
    }

    public static File getTimelineFile() {
        return TIMELINE_FILE;
    }

    public static File getPicturesFolder() {
        return PICTURES_FOLDER;
    }

    public static File getMiniaturesFolder() {
        return MINIATURES_FOLDER;
    }

    public static String getProjectLocation() {
        return PROJECT_LOCATION;
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    private static void savePortraitRessources() {
        try {
            FileOutputStream outputStream;
            try (InputStream inputstream = ProjectConfiguration.class.getResourceAsStream(Person.DEFAULT_PICTURE_NAME)) {
                String outputPath = PORTRAITS_FOLDER + File.separator + Person.DEFAULT_PICTURE_NAME;
                File outputFile = new File(outputPath);
                LOG.log(Level.INFO, "> savePortraitRessources :: {0}", outputPath);
                outputStream = new FileOutputStream(outputFile);
                outputStream.write(inputstream.readAllBytes());
            }
            outputStream.close();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Ressource file not found :: {0}", new Object[]{Person.DEFAULT_PICTURE_NAME});
            LOG.log(Level.SEVERE, "> Exception :: {0}", new Object[]{ex});
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while saving ressource :: {0}", new Object[]{Person.DEFAULT_PICTURE_NAME});
            LOG.log(Level.SEVERE, "> Exception :: {0}", new Object[]{ex});
        }
    }

}
