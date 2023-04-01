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

import static com.github.noony.app.timelinefx.core.FriezeObjectFactory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import com.github.noony.app.timelinefx.utils.MetadataParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public final class PortraitFactory {

    public static final String PORTRAIT_CREATED = "portraitCreatedInFactory";

    private static final Map<Long, Portrait> PORTRAITS = new HashMap<>();

    private static final Logger LOG = Logger.getGlobal();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(PORTRAITS);

    private PortraitFactory() {
        // private utility constructor
    }

    public static void reset() {
        PORTRAITS.clear();
    }

    public static Portrait getPortrait(long id) {
        return PORTRAITS.get(id);
    }

    public static Portrait createPortrait(Person person) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with person={0} and default picture.", new Object[]{person});
        System.err.println(" !! person.getProject().getPortraitsFolder()");
        System.err.println(" >>> =" + person.getProject().getPortraitsFolder());
        String filePath = person.getProject().getPortraitsFolder().getAbsolutePath() + File.separator + Person.DEFAULT_PICTURE_NAME;
        var file = new File(filePath);
        var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        assert picInfo != null;
        var portrait = new Portrait(FriezeObjectFactory.getNextID(), person, filePath, picInfo.getWidth(), picInfo.getHeight());
        PORTRAITS.put(portrait.getId(), portrait);
        FriezeObjectFactory.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    public static Portrait createPortrait(Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with person={0} filePath={1}.", new Object[]{person, filePath});
        var file = new File(CustomFileUtils.fromProjectRelativeToAbsolute(person.getProject(), filePath));
        var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        assert picInfo != null;
        var portrait = new Portrait(FriezeObjectFactory.getNextID(), person, filePath, picInfo.getWidth(), picInfo.getHeight());
        PORTRAITS.put(portrait.getId(), portrait);
        FriezeObjectFactory.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    public static Portrait createPortrait(long id, Person person, String filePath) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating portrait with id={0} person={1} filePath={2}.", new Object[]{id, person, filePath});
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create portrait " + filePath + " with existing id=" + id + " (exists : " + PORTRAITS.get(id) + "[" + PORTRAITS.get(id).getId() + "])");
        }
        var file = new File(CustomFileUtils.fromProjectRelativeToAbsolute(person.getProject(), filePath));
        if (!file.exists()) {
            throw new IllegalArgumentException("Trying to create portrait for " + person + " with missing file=" + filePath);
        }
        var picInfo = MetadataParser.parseMetadata(person.getProject(), file);
        var portrait = new Portrait(id, person, filePath, picInfo.getWidth(), picInfo.getHeight());
        PORTRAITS.put(portrait.getId(), portrait);
        FriezeObjectFactory.addObject(portrait);
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PORTRAIT_CREATED, null, portrait);
        return portrait;
    }

    public static List<Portrait> getPortraits() {
        return PORTRAITS.values().stream().sorted(Portrait.COMPARATOR).toList();
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
