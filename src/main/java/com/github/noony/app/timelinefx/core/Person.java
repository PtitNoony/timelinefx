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
 * A person who can be associated with places, stays and pictures.
 *
 * @author hamon
 */
public final class Person implements FriezeObject {

    /**
     * Default portrait picture used when a person has no portrait of their own.
     */
    public static final String DEFAULT_PICTURE_NAME = "LegoHead.png";

    /**
     * Name of the property change event fired when this person's selection changes.
     */
    public static final String SELECTION_CHANGED = "selectionChanged";
    /**
     * Name of the property change event fired when this person's visibility changes.
     */
    public static final String VISIBILITY_CHANGED = "visibilityChanged";
    /**
     * Name of the property change event fired when this person's name changes.
     */
    public static final String NAME_CHANGED = "nameChanged";
    /**
     * Name of the property change event fired when this person's date of birth changes.
     */
    public static final String DATE_OF_BIRTH_CHANGED = "dateOfBirthChanged";
    /**
     * Name of the property change event fired when this person's date of death changes.
     */
    public static final String DATE_OF_DEATH_CHANGED = "dateOfDeathChanged";
    /**
     * Name of the property change event fired when this person's color changes.
     */
    public static final String COLOR_CHANGED = "colorChanged";

    /**
     * Name of the property change event fired when a portrait is added.
     */
    public static final String PORTRAIT_ADDED = "portraitAdded";

    /**
     * Name of the property change event fired when a portrait is removed.
     */
    public static final String PORTRAIT_REMOVED = "portraitRemoved";

    /**
     * Name of the property change event fired when the default portrait changes.
     */
    public static final String DEFAULT_PORTRAIT_CHANGED = "defaultPortaitChanged";

    /**
     * Orders persons by name.
     */
    public static final Comparator<Person> COMPARATOR = Comparator.comparing(Person::getName);

    /**
     * The color assigned to a person when none is specified.
     */
    private static final Color DEFAULT_COLOR = Color.CHOCOLATE;

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Fallback value returned when no date/time is available.
     */
    private static final long DEFAULT_TIME = -1;

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final Long id;
    //

    /**
     * The project this person belongs to.
     */
    private final TimeLineProject project;

    /**
     * This person's portraits.
     */
    private final List<Portrait> portraits;
    //

    /**
     * This person's display name.
     */
    private String name;

    /**
     * This person's display color.
     */
    private Color color;

    /**
     * Whether {@link #dateOfBirth}/{@link #dateOfDeath} or {@link #timeOfBirth}/{@link #timeOfDeath}
     * holds this person's dates.
     */
    private TimeFormat timeFormat;

    /**
     * This person's date of birth, used when {@link #timeFormat} is {@code LOCAL_TIME}.
     */
    private LocalDate dateOfBirth;

    /**
     * This person's date of death, used when {@link #timeFormat} is {@code LOCAL_TIME}.
     */
    private LocalDate dateOfDeath;

    /**
     * This person's raw numeric birth time, used when {@link #timeFormat} is {@code TIME_MIN}.
     */
    private long timeOfBirth;

    /**
     * This person's raw numeric death time, used when {@link #timeFormat} is {@code TIME_MIN}.
     */
    private long timeOfDeath;

    /**
     * This person's default portrait, lazily created on first access.
     */
    private Portrait defaultPortrait;
    //

    /**
     * Whether this person is currently selected.
     */
    private boolean selected;

    /**
     * Whether this person is currently visible.
     */
    private boolean visible;

    protected Person(final TimeLineProject aProject, final Long personId, String personName, Color aColor, LocalDate aDoB, LocalDate aDoD) {
        id = personId;
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

    protected Person(final TimeLineProject aProject, final Long personId, String personName, Color aColor, long aToB, long aToD) {
        id = personId;
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

    protected Person(final TimeLineProject aProject, final Long personId, String personName) {
        this(aProject, personId, personName, DEFAULT_COLOR, 0, 0);
    }

    @Override public long getId() {
        return id;
    }

    /**
     * @return the project this person belongs to
     */
    public TimeLineProject getProject() {
        return project;
    }

    /**
     * @return this person's default portrait, creating it if it doesn't exist yet
     */
    public Portrait getDefaultPortrait() {
        // Checking it here to only create a default portrait when it really becomes necessary
        if (defaultPortrait == null) {
            defaultPortrait = PortraitFactory.createPortrait(this);
        }
        return defaultPortrait;
    }

    /**
     * @param aName this person's new name
     */
    public void setName(final String aName) {
        if (!name.equals(aName)) {
            name = aName;
            propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
        }
    }

    /**
     * @param aPortrait this person's new default portrait
     */
    public void setDefaultPortrait(final Portrait aPortrait) {
        if (aPortrait != null && aPortrait != defaultPortrait) {
            addPortrait(aPortrait);
            defaultPortrait = aPortrait;
            propertyChangeSupport.firePropertyChange(DEFAULT_PORTRAIT_CHANGED, this, defaultPortrait);
        }
    }

    /**
     * @param aPortrait the portrait to add
     */
    public void addPortrait(final Portrait aPortrait) {
        if (!portraits.contains(aPortrait)) {
            portraits.add(aPortrait);
            propertyChangeSupport.firePropertyChange(PORTRAIT_ADDED, this, aPortrait);
        }
    }

    /**
     * @return an unmodifiable list of this person's portraits
     */
    public List<Portrait> getPortraits() {
        return Collections.unmodifiableList(portraits);
    }

    public Portrait getPortrait(long aPortraiID) {
        return portraits.stream().filter(p -> p.getId() == aPortraiID).findAny().orElse(null);
    }

    /**
     * @param aPortrait the portrait to remove
     */
    public void removePortrait(final Portrait aPortrait) {
        if (portraits.remove(aPortrait)) {
            propertyChangeSupport.firePropertyChange(PORTRAIT_REMOVED, null, aPortrait);
        }
    }

    /**
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return this person's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param aColor this person's new color
     */
    public void setColor(final Color aColor) {
        if (!color.equals(aColor)) {
            color = aColor;
            propertyChangeSupport.firePropertyChange(COLOR_CHANGED, this, color);
        }
    }

    /**
     * @return this person's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return this person's time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * @param aTimeFormat this person's new time format
     */
    public void setTimeFormat(final TimeFormat aTimeFormat) {
        timeFormat = aTimeFormat;
    }

    /**
     * @return this person's date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param newDoB this person's new date of birth
     */
    public void setDateOfBirth(final LocalDate newDoB) {
        if (newDoB == null) {
            // for the time being we do not support clearing date of birth
            LOG.log(Level.INFO, "Clearing date of birth is not supported yet in {0}", new Object[]{this});
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

    /**
     * @return this person's raw numeric birth time
     */
    public long getTimeOfBirth() {
        return timeOfBirth;
    }

    /**
     * @return this person's birth time, as an absolute value comparable regardless of time format
     */
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

    /**
     * @return this person's death time, as an absolute value comparable regardless of time format
     */
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

    /**
     * @param newToB this person's new raw numeric birth time
     */
    public void setTimeOfBirth(final long newToB) {
        if (timeOfBirth != newToB) {
            timeOfBirth = newToB;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, timeOfBirth);
        }
    }

    /**
     * @return this person's date of death
     */
    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    /**
     * @param newDoD this person's new date of death
     */
    public void setDateOfDeath(final LocalDate newDoD) {
        if (newDoD == null) {
            // for the time being we do not support clearing date of death
            LOG.log(Level.INFO, "Clearing date of death is not supported yet in {0}", new Object[]{this});
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

    /**
     * @return this person's raw numeric death time
     */
    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    /**
     * @param newToD this person's new raw numeric death time
     */
    public void setTimeOfDeath(final long newToD) {
        if (timeOfDeath != newToD) {
            timeOfDeath = newToD;
            timeFormat = TimeFormat.TIME_MIN;
            propertyChangeSupport.firePropertyChange(DATE_OF_DEATH_CHANGED, timeFormat, timeOfDeath);
        }
    }

    /**
     * @param isSelected whether this person is now selected
     */
    public void setSelected(final boolean isSelected) {
        final var update = selected != isSelected;
        selected = isSelected;
        if (update) {
            propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, null, selected);
        }
    }

    /**
     * @return whether this person is currently selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param isVisible whether this person is now visible
     */
    public void setVisible(final boolean isVisible) {
        final var update = visible != isVisible;
        visible = isVisible;
        if (update) {
            propertyChangeSupport.firePropertyChange(VISIBILITY_CHANGED, this, visible);
        }
    }

    /**
     * @return whether this person is currently visible
     */
    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return name;
    }

}
