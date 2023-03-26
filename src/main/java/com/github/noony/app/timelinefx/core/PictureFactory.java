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

import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
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
 *
 * @author hamon
 */
public final class PictureFactory {

    public static final String PICTURE_ADDED = "pictureAdded";

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<Picture> FACTORY = new Factory<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(FACTORY);

    private PictureFactory() {
        // private utility constructor
    }

    public static void reset() {
        FACTORY.reset();
    }

    public static List<Picture> getPictures() {
        return FACTORY.getObjects();
    }

    public static Picture getPicture(long pictureID) {
        return FACTORY.get(pictureID);
    }

    public static Picture createPicture(TimeLineProject project, File originalPictureFile, String pictureName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating picture with pictureName={0} file={1}", new Object[]{pictureName, originalPictureFile});
        File pictureFile;
        pictureFile = new File(project.getPicturesFolder(), originalPictureFile.getName());
        if (!pictureFile.exists()) {
            try {
                FileUtils.copyFile(originalPictureFile, pictureFile);
                LOG.log(CREATION_LOGGING_LEVEL, "Copying picture file to: {0}", new Object[]{pictureFile});
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error while copying picture file to: {0} : {1}", new Object[]{pictureFile, ex});
            }
        }
        var picInfo = MetadataParser.parseMetadata(project, pictureFile);
        assert picInfo != null;
        var picture = new Picture(project, FACTORY.getNextID(), pictureName, picInfo.getCreationDate().toLocalDate(), picInfo.getPath(), picInfo.getWidth(), picInfo.getHeight());
        FACTORY.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    public static Picture createPicture(TimeLineProject project, long id, String pictureName, LocalDateTime pictureCreationDate, String picturePath, int pictureWidth, int pictureHeight) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating picture with id={0} pictureName={1}", new Object[]{id, pictureName});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create picture " + pictureName + " with existing id=" + id + " :: " + FACTORY.get(id));
        }
        var picture = new Picture(project, id, pictureName, pictureCreationDate.toLocalDate(), picturePath, pictureWidth, pictureHeight);
        FACTORY.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
