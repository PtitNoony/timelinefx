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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class Person extends FriezeObject {

    public static final String DEFAULT_PICTURE_NAME = "LegoHead.png";

    public static final String SELECTION_CHANGED = "selectionChanged";
    public static final String VISIBILITY_CHANGED = "visibilityChanged";
    public static final String NAME_CHANGED = "nameChanged";
    public static final String DATE_OF_BIRTH_CHANGED = "dateOfBirthChanged";
    public static final String DATE_OF_DEATH_CHANGED = "dateOfDeathChanged";
    public static final String COLOR_CHANGED = "colorChanged";

    public static final String PORTRAIT_ADDED = "portraitAdded";
    public static final String PORTRAIT_REMOVED = "portraitRemoved";
    public static final String DEFAULT_PORTRAIT_CHANGED = "defaultPortaitChanged";

    public static final Comparator<Person> COMPARATOR = Comparator.comparing(Person::getName);

    private static final Color DEFAULT_COLOR = Color.CHOCOLATE;
    private static final Logger LOG = Logger.getGlobal();
    private static final long DEFAULT_TIME = -1;

    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final TimeLineProject project;
    private final List<Portrait> portraits;
    //
    private String name;
    private Color color;
    private TimeFormat timeFormat;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private long timeOfBirth;
    private long timeOfDeath;
    private Portrait defaultPortrait = null;
    //
    private boolean selected;
    private boolean visible;

    protected Person(TimeLineProject aProject, Long personId, String personName, Color aColor, LocalDate aDoB, LocalDate aDoD) {
        super(personId);
        project = aProject;
        portraits = new LinkedList<>();
        name = personName;
        color = aColor;
        dateOfBirth = aDoB;
        dateOfDeath = aDoD;
        timeFormat = TimeFormat.LOCAL_TIME;
        propertyChangeSupport = new PropertyChangeSupport(Person.this);
        selected = false;
        visible = true;
    }

    protected Person(TimeLineProject aProject, Long personId, String personName, Color aColor, long aToB, long aToD) {
        super(personId);
        project = aProject;
        portraits = new LinkedList<>();
        name = personName;
        color = aColor;
        timeOfBirth = aToB;
        timeOfDeath = aToD;
        timeFormat = TimeFormat.TIME_MIN;
        propertyChangeSupport = new PropertyChangeSupport(Person.this);
        selected = false;
        visible = true;
    }

    protected Person(TimeLineProject aProject, Long personId, String personName) {
        this(aProject, personId, personName, DEFAULT_COLOR, 0, 0);
    }

    public TimeLineProject getProject() {
        return project;
    }

    public Portrait getDefaultPortrait() {
        return defaultPortrait;
    }

    public void setName(String aName) {
        if (!name.equals(aName)) {
            name = aName;
            propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
        }
    }

    public void setDefaultPortrait(Portrait aPortrait) {
        if (aPortrait != null && aPortrait != defaultPortrait) {
            addPortrait(aPortrait);
            defaultPortrait = aPortrait;
            propertyChangeSupport.firePropertyChange(DEFAULT_PORTRAIT_CHANGED, this, defaultPortrait);
        }
    }

    public void addPortrait(Portrait aPortrait) {
        if (!portraits.contains(aPortrait)) {
            portraits.add(aPortrait);
            propertyChangeSupport.firePropertyChange(PORTRAIT_ADDED, this, aPortrait);
        }
    }

    public List<Portrait> getPortraits() {
        return Collections.unmodifiableList(portraits);
    }

    public void removePortrait(Portrait aPortrait) {
        if (portraits.remove(aPortrait)) {
            propertyChangeSupport.firePropertyChange(PORTRAIT_REMOVED, null, aPortrait);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }

    public void setColor(Color aColor) {
        if (!color.equals(aColor)) {
            color = aColor;
            propertyChangeSupport.firePropertyChange(COLOR_CHANGED, this, color);
        }
    }

    public Color getColor() {
        return color;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(TimeFormat aTimeFormat) {
        timeFormat = aTimeFormat;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate newDoB) {
        if (newDoB == null) {
            // for the time being we do not support clearing date of birth
            LOG.log(Level.INFO,"Clearing date of birth is not supported yet in {0}", new Object[]{this});
        } else if (dateOfBirth == null) {
            dateOfBirth = newDoB;
            timeFormat = TimeFormat.LOCAL_TIME;
            propertyChangeSupport.firePropertyChange(DATE_OF_BIRTH_CHANGED, timeFormat, dateOfBirth);
        } else if (!dateOfBirth.isEqual(newDoB)) {
            dateOfBirth = newDoB;
            timeFormat = TimeFormat.LOCAL_TIME;
            propertyChangeSupport.firePropertyChange(DATE_OF_BIRTH_CHANGED, timeFormat, dateOfBirth);
        }
    }

    public long getTimeOfBirth() {
        return timeOfBirth;
    }

    public long getAbsolutTimeOfBirth() {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                if (dateOfBirth != null) {
                    return dateOfBirth.toEpochDay();
                } else {
                    return DEFAULT_TIME;
                }
            }
            case TIME_MIN -> {
                return timeOfBirth;
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time mode : " + timeFormat);
        }
    }

    public long getAbsolutTimeOfDeath() {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                if (dateOfDeath != null) {
                    return dateOfDeath.toEpochDay();
                } else {
                    return DEFAULT_TIME;
                }
            }
            case TIME_MIN -> {
                return timeOfDeath;
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time mode : " + timeFormat);
        }
    }

    public void setTimeOfBirth(long newToB) {
        if (timeOfBirth != newToB) {
            timeOfBirth = newToB;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, timeOfBirth);
        }
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate newDoD) {
        if (newDoD == null) {
            // for the time being we do not support clearing date of death
            LOG.log(Level.INFO,"Clearing date of death is not supported yet in {0}", new Object[]{this});
        } else if (dateOfDeath == null) {
            dateOfDeath = newDoD;
            timeFormat = TimeFormat.LOCAL_TIME;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, dateOfDeath);
        } else if (!dateOfDeath.isEqual(newDoD)) {
            dateOfDeath = newDoD;
            timeFormat = TimeFormat.LOCAL_TIME;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, dateOfDeath);
        }
    }

    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(long newToD) {
        if (timeOfDeath != newToD) {
            timeOfDeath = newToD;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, timeOfDeath);
        }
    }

    public void setSelected(boolean isSelected) {
        var update = selected != isSelected;
        selected = isSelected;
        if (update) {
            propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, null, selected);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setVisible(boolean isVisible) {
        var update = visible != isVisible;
        visible = isVisible;
        if (update) {
            propertyChangeSupport.firePropertyChange(VISIBILITY_CHANGED, this, visible);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return name;
    }

}
