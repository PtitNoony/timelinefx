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

import com.github.noony.app.timelinefx.utils.MetadataParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Entry point for creating and retrieving {@link Picture} instances.
 *
 * @author hamon
 */
public final class PictureFactory {

    /**
     * Name of the property change event fired when a picture is created.
     */
    public static final String PICTURE_ADDED = "pictureAdded";

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Registry of created pictures.
     */
    private static final Factory<Picture> FACTORY = new Factory<>();

    /**
     * Support object used to fire property change events.
     */
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(FACTORY);

    private PictureFactory() {
        // private utility constructor
    }

    /**
     * Resets the factory, discarding all created pictures.
     */
    public static void reset() {
        FACTORY.reset();
    }

    /**
     * Returns all created pictures.
     *
     * @return all created pictures
     */
    public static List<Picture> getPictures() {
        return FACTORY.getObjects();
    }

    /**
     * @param pictureID a picture's id
     * @return the picture with the given id, or null if none exists
     */
    public static Picture getPicture(final long pictureID) {
        return FACTORY.get(pictureID);
    }

    /**
     * Creates a new picture by copying an existing file into the project's pictures folder.
     *
     * @param project the project the picture belongs to
     * @param originalPictureFile the source picture file to copy
     * @param pictureName the picture's name
     * @return the created picture
     */
    public static Picture createPicture(final TimeLineProject project, final File originalPictureFile, final String pictureName) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating picture with pictureName={0} file={1}", new Object[]{pictureName, originalPictureFile});
        final File pictureFile;
        pictureFile = new File(project.getPicturesFolder(), originalPictureFile.getName());
        if (!pictureFile.exists()) {
            try {
                FileUtils.copyFile(originalPictureFile, pictureFile);
                LOG.log(Factory.CREATION_LOGGING_LEVEL, "Copying picture file to: {0}", new Object[]{pictureFile});
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error while copying picture file to: {0} : {1}", new Object[]{pictureFile, ex});
            }
        }
        final var picInfo = MetadataParser.parseMetadata(project, pictureFile);
        assert picInfo != null;
        final var picture = new Picture(project, FACTORY.getNextID(), pictureName, picInfo.getCreationDate().toLocalDate(), picInfo.getPath(), picInfo.getWidth(), picInfo.getHeight());
        FACTORY.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    /**
     * Creates a new picture with a specific id, referencing an already-existing file.
     *
     * @param project the project the picture belongs to
     * @param id the id to assign to the new picture
     * @param pictureName the picture's name
     * @param pictureCreationDate the picture's creation date
     * @param picturePath the picture file's path
     * @param pictureWidth the picture's width
     * @param pictureHeight the picture's height
     * @return the created picture
     */
    public static Picture createPicture(final TimeLineProject project, final long id, final String pictureName, final LocalDateTime pictureCreationDate, String picturePath, int pictureWidth, int pictureHeight) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating picture with id={0} pictureName={1}", new Object[]{id, pictureName});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create picture " + pictureName + " with existing id=" + id + " :: " + FACTORY.get(id));
        }
        final var picture = new Picture(project, id, pictureName, pictureCreationDate.toLocalDate(), picturePath, pictureWidth, pictureHeight);
        FACTORY.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    /**
     * @param listener the listener to add
     */
    public static void addPropertyChangeListener(final PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public static void removePropertyChangeListener(final PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
