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

package com.github.noony.app.timelinefx.core;

import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import com.github.noony.app.timelinefx.utils.MetadataParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;

/**
 * Entry point for creating and retrieving {@link Portrait} instances.
 *
 * @author hamon
 */
public final class PortraitFactory {

    /**
     * Name of the property change event fired when a portrait is created.
     */
    public static final String PORTRAIT_CREATED = "portraitCreatedInFactory";

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Registry of created portraits.
     */
    private static final Factory<Portrait> FACTORY = new Factory<>();

    /**
     * Support object used to fire property change events.
     */
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(FACTORY);

    private PortraitFactory() {
        // private utility constructor
    }

    /**
     * Resets the factory, discarding all created portraits.
     */
    public static void reset() {
        FACTORY.reset();
    }

    /**
     * @param id a portrait's id
     * @return the portrait with the given id, or null if none exists
     */
    public static Portrait getPortrait(final long id) {
        return FACTORY.get(id);
    }

    /**
     * Creates a new portrait for a person using their default picture.
     *
     * @param person the person the portrait belongs to
     * @return the created portrait
     */
    public static Portrait createPortrait(final Person person) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with person={0} and default picture.", new Object[]{person});
        final var filePath = person.getProject().getPortraitsAbsoluteFolder().getAbsolutePath() + File.separator + Person.DEFAULT_PICTURE_NAME;
        final var fileRelativePath = person.getProject().getPortraitsRelativeFolder() + File.separator + Person.DEFAULT_PICTURE_NAME;
        final var file = new File(filePath);
        final var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        assert picInfo != null;
        final var portrait = new Portrait(FACTORY.getNextID(), person, fileRelativePath, picInfo.getWidth(), picInfo.getHeight());
        FACTORY.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    /**
     * Creates a new portrait for a person using a specific picture file.
     *
     * @param person the person the portrait belongs to
     * @param filePath the portrait picture's project-relative file path
     * @return the created portrait
     */
    public static Portrait createPortrait(final Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with person={0} filePath={1}.", new Object[]{person, filePath});
        final var file = new File(CustomFileUtils.fromProjectRelativeToAbsolute(person.getProject(), filePath));
        var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        assert picInfo != null;
        final var portrait = new Portrait(FACTORY.getNextID(), person, filePath, picInfo.getWidth(), picInfo.getHeight());
        FACTORY.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    /**
     * Creates a new portrait with a specific id, referencing an already-existing file.
     *
     * @param id the id to assign to the new portrait
     * @param person the person the portrait belongs to
     * @param filePath the portrait picture's project-relative file path
     * @return the created portrait
     */
    public static Portrait createPortrait(final long id, Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with id={0} person={1} filePath={2}.", new Object[]{id, person, filePath});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create portrait " + filePath + " with existing id=" + id + " (exists : " + FACTORY.get(id) + "[" + FACTORY.get(id).getId() + "])");
        }
        var file = new File(CustomFileUtils.fromProjectRelativeToAbsolute(person.getProject(), filePath));
        if (!file.exists()) {
            throw new IllegalArgumentException("Trying to create portrait for " + person + " with missing file=" + filePath);
        }
        var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        final var portrait = new Portrait(id, person, filePath, picInfo.getWidth(), picInfo.getHeight());
        FACTORY.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    /**
     * @return all created portraits, sorted by id
     */
    public static List<Portrait> getPortraits() {
        return FACTORY.getObjects().stream().sorted(Portrait.COMPARATOR).toList();
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
