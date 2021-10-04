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

import static com.github.noony.app.timelinefx.core.FriezeObjectFactory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.utils.MetadataParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author hamon
 */
public class PictureFactory {

    public static final String PICTURE_ADDED = "pictureAdded";

    private static final Logger LOG = Logger.getGlobal();

    private static final Map<Long, Picture> PICTURES = new HashMap<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(PICTURES);

    private PictureFactory() {
        // private utility constructor
    }

    public static final void reset() {
        PICTURES.clear();
    }

    public static List<Picture> getPictures() {
        return PICTURES.values().stream().collect(Collectors.toList());
    }

    public static Picture getPicture(long pictureID) {
        return PICTURES.get(pictureID);
    }

    public static Picture createPicture(File orignialPictureFile, String pictureName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating picture with pictureName={0} file={1}", new Object[]{pictureName, orignialPictureFile});
        File pictureFile;
        pictureFile = new File(ProjectConfiguration.getPicturesFolder(), orignialPictureFile.getName());
        if (!pictureFile.exists()) {
            try {
                FileUtils.copyFile(orignialPictureFile, pictureFile);
                LOG.log(CREATION_LOGGING_LEVEL, "Copying picture file to: {0}", new Object[]{pictureFile});
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error while copying picture file to: {0} : {1}", new Object[]{pictureFile, ex});
            }
        }
        var picInfo = MetadataParser.parseMetadata(pictureFile);
        var picture = new Picture(FriezeObjectFactory.getNextID(), pictureName, picInfo.getCreationDate(), picInfo.getPath(), picInfo.getWidth(), picInfo.getHeight());
        PICTURES.put(picture.getId(), picture);
        FriezeObjectFactory.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    public static Picture createPicture(long id, String pictureName, LocalDateTime pictureCreationDate, String picturePath, int pictureWidth, int pictureHeight) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating picture with id={0} pictureName={1}", new Object[]{id, pictureName});
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create picture " + pictureName + " with existing id=" + id + " :: " + FriezeObjectFactory.get(id));
        }
        var picture = new Picture(id, pictureName, pictureCreationDate, picturePath, pictureWidth, pictureHeight);
        PICTURES.put(picture.getId(), picture);
        FriezeObjectFactory.addObject(picture);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PICTURE_ADDED, null, picture);
        return picture;
    }

    public static final void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static final void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
