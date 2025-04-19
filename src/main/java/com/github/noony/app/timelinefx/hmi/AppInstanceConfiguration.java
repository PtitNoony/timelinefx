/*
 * Copyright (C) 2023 NoOnY
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
package com.github.noony.app.timelinefx.hmi;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class AppInstanceConfiguration {

    public static final double ANCHOR_CONSTRAINT_ZERO = 0.0;

    public static final String TIMELINE_SELECTED_CHANGED = "timelineSelectedChanged";
    public static final String FRIEZE_SELECTED_CHANGED = "friezeSelectedChanged";
    //
    private static final Logger LOG = Logger.getGlobal();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(ConfigurationViewController.class);
    //
    //
    private static TimeLineProject selectedProject = null;
    private static Frieze selectedFrieze = null;

    private AppInstanceConfiguration() {
        // private utility constructor
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void setSelectedTimeline(TimeLineProject timeline) {
        selectedProject = timeline;
        LOG.log(Level.INFO, "Updating selected timeline: {0}", new Object[]{selectedProject});
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(TIMELINE_SELECTED_CHANGED, null, selectedProject);
        if (selectedProject != null) {
            var tmp = selectedProject.getFriezes().stream().findFirst();
            setSelectedFrieze(tmp.orElse(null));
        } else {
            setSelectedFrieze(null);
        }
    }

    public static TimeLineProject getSelectedProject() {
        return selectedProject;
    }

    public static void setSelectedFrieze(Frieze frieze) {
        selectedFrieze = frieze;
        LOG.log(Level.INFO, "Updating selected frieze: {0}", new Object[]{selectedFrieze});
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(FRIEZE_SELECTED_CHANGED, null, selectedFrieze);
    }

    public static Frieze getSelectedFrieze() {
        return selectedFrieze;
    }

}
