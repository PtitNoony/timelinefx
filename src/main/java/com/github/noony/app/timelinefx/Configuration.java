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
package com.github.noony.app.timelinefx;

import com.github.noony.app.timelinefx.hmi.ConfigurationViewController;
import com.github.noony.app.timelinefx.utils.CustomProfiler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jfxtras.styles.jmetro.Style;

/**
 *
 * @author hamon
 */
public class Configuration {

    public static final String TIMELINES_FOLDER_LOCATION_CHANGED = "timelinesFolderLocationChanged";
    public static final String PORTRAITS_FOLDER_LOCATION_CHANGED = "portraitsFolderLocationChanged";
    public static final String PICTURES_FOLDER_LOCATION_CHANGED = "picturesFolderLocationChanged";
    public static final String MINIATURES_FOLDER_LOCATION_CHANGED = "miniaturesFolderLocationChanged";
    public static final String THEME_CHANGED = "miniaturesFolderLocationChanged";
    //
    public static final double DEFAULT_FXML_GAP = 8.0;
    //
    private static final String DEFAULT_PORTRAITS_FOLDER_NAME = "portraits";
    private static final String DEFAULT_PICTURES_FOLDER_NAME = "pictures";
    private static final String DEFAULT_MINIATURES_FOLDER_NAME = "miniatures";

    private static final String DEFAULT_PROJECTS_FOLDER_NAME = "Timelines";
    private static final String PREFERENCE_FILE_NAME = ".timelines";
    private static final String TIMELINES_FOLDER_PROPERTY_NAME = "TimelinesFolder";
    private static final String PORTRAITS_FOLDER_PROPERTY_NAME = "PortraitsFolder";
    private static final String PICTURES_FOLDER_PROPERTY_NAME = "PicturesFolder";
    private static final String MINIATURES_FOLDER_PROPERTY_NAME = "MiniaturesFolder";
    private static final String THEME_PROPERTY_NAME = "Theme";
    //
    private static final String DEFAULT_TIMELINES_FOLDER_PATH = System.getProperty("user.home") + File.separator + DEFAULT_PROJECTS_FOLDER_NAME;
    private static final Style DEFAULT_THEME = Style.DARK;

    //
    private static final Logger LOG = Logger.getGlobal();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(ConfigurationViewController.class);
    //
    private static File preferenceFile = null;
    private static Properties properties = null;

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void loadPreferences() {
        CustomProfiler.start("loadPreferences");
        var userDirFile = new File(DEFAULT_TIMELINES_FOLDER_PATH + File.separator + PREFERENCE_FILE_NAME);
        if (userDirFile.exists()) {
            preferenceFile = userDirFile;
        } else {
            preferenceFile = getUserHomePreferenceFile();
        }
        loadPreferenceFile();
        CustomProfiler.stop("loadPreferences");
    }

    private static File getUserHomePreferenceFile() {
        var filePathS = DEFAULT_TIMELINES_FOLDER_PATH;
        LOG.log(Level.INFO, "Creating preference file in user.home :: {0}", filePathS);
        var preferenceFolder = new File(filePathS);
        if (!preferenceFolder.exists()) {
            try {
                var path = preferenceFolder.toPath();
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create folder : {0} :: {1}", new Object[]{filePathS, ex});
                return null;
            }
        }
        var preferenceFileS = filePathS + File.separator + PREFERENCE_FILE_NAME;
        var tmpPreferenceFile = new File(preferenceFileS);
        if (!tmpPreferenceFile.exists()) {
            try {
                var createdFile = tmpPreferenceFile.createNewFile();
                LOG.log(Level.INFO, "Created preference file: {0}", new Object[]{createdFile});
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not create user home preference file : ", new Object[]{ex});
            }
        }
        return tmpPreferenceFile;
    }

    private static void loadPreferenceFile() {
        InputStream inputStream = null;
        properties = new Properties();
        try {
            inputStream = new FileInputStream(preferenceFile);
            properties.load(inputStream);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load preference file. Exception:: {0}", new Object[]{ex});
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not properly close preference file stream. Exception:: {0}", new Object[]{ex});
            }
        }
        var propertiesChanged = false;
        // checking for key properties needed
        if (!properties.containsKey(TIMELINES_FOLDER_PROPERTY_NAME)) {
            propertiesChanged = true;
            properties.setProperty(TIMELINES_FOLDER_PROPERTY_NAME, DEFAULT_TIMELINES_FOLDER_PATH);
        }
        if (!properties.containsKey(PORTRAITS_FOLDER_PROPERTY_NAME)) {
            propertiesChanged = true;
            properties.setProperty(PORTRAITS_FOLDER_PROPERTY_NAME, DEFAULT_PORTRAITS_FOLDER_NAME);
        }
        if (!properties.containsKey(PICTURES_FOLDER_PROPERTY_NAME)) {
            propertiesChanged = true;
            properties.setProperty(PICTURES_FOLDER_PROPERTY_NAME, DEFAULT_PICTURES_FOLDER_NAME);
        }
        if (!properties.containsKey(MINIATURES_FOLDER_PROPERTY_NAME)) {
            propertiesChanged = true;
            properties.setProperty(MINIATURES_FOLDER_PROPERTY_NAME, DEFAULT_MINIATURES_FOLDER_NAME);
        }
        if (!properties.containsKey(THEME_PROPERTY_NAME)) {
            propertiesChanged = true;
            properties.setProperty(THEME_PROPERTY_NAME, DEFAULT_THEME.name());
        }
        //
        if (propertiesChanged) {
            savePreferences();
        }
    }

    public static void savePreferences() {
        if (properties == null) {
            LOG.log(Level.SEVERE, "Could not save preferences since they have not been loaded yet.");
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(preferenceFile);
            properties.store(outputStream, "..");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not save preferences. Exception:: {0}", new Object[]{ex});
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Could not properly close preference file output stream. Exception:: {0}", new Object[]{ex});
            }
        }
    }

    public static String getProjectsParentFolder() {
        return properties.getProperty(TIMELINES_FOLDER_PROPERTY_NAME);
    }

    public static String getPortraitsFolder() {
        return properties.getProperty(PORTRAITS_FOLDER_PROPERTY_NAME);
    }

    public static String getPicturesFolder() {
        return properties.getProperty(PICTURES_FOLDER_PROPERTY_NAME);
    }

    public static String getMiniaturesFolder() {
        return properties.getProperty(MINIATURES_FOLDER_PROPERTY_NAME);
    }

    public static boolean withProfiling() {
        return true;
    }

    public static Style getTheme() {
        return Style.valueOf(properties.getProperty(THEME_PROPERTY_NAME));
    }

    public static void setProjectsParentFolder(String newValue) {
        if (newValue != null && !newValue.equals(getProjectsParentFolder())) {
            properties.setProperty(TIMELINES_FOLDER_PROPERTY_NAME, newValue);
            savePreferences();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(TIMELINES_FOLDER_LOCATION_CHANGED, null, newValue);
        }
    }

    public static void setPortraitsFolder(String newValue) {
        if (newValue != null && !newValue.equals(getPortraitsFolder())) {
            properties.setProperty(PORTRAITS_FOLDER_PROPERTY_NAME, newValue);
            savePreferences();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAITS_FOLDER_LOCATION_CHANGED, null, newValue);
        }
    }

    public static void setPicturesFolder(String newValue) {
        if (newValue != null && !newValue.equals(getPicturesFolder())) {
            properties.setProperty(PICTURES_FOLDER_PROPERTY_NAME, newValue);
            savePreferences();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURES_FOLDER_LOCATION_CHANGED, null, newValue);
        }
    }

    public static void setMiniaturesFolder(String newValue) {
        if (newValue != null && !newValue.equals(getMiniaturesFolder())) {
            properties.setProperty(MINIATURES_FOLDER_PROPERTY_NAME, newValue);
            savePreferences();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(MINIATURES_FOLDER_LOCATION_CHANGED, null, newValue);
        }
    }

    public static void setTheme(Style newStyle) {
        if (newStyle != null && !newStyle.name().equals(getMiniaturesFolder())) {
            properties.setProperty(THEME_PROPERTY_NAME, newStyle.name());
            savePreferences();
            PROPERTY_CHANGE_SUPPORT.firePropertyChange(THEME_CHANGED, null, newStyle);
        }
    }
}
