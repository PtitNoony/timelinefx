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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import lombok.NonNull;

/**
 * A named location, optionally nested under a parent place (e.g. a town within a country).
 *
 * @author hamon
 */
public final class Place implements FriezeObject {

    /**
     * Name of the property change event fired when this place's selection changes.
     */
    public static final String SELECTION_CHANGED = "selectionChanged";

    /**
     * Name of the property change event fired when this place's content (name, level, color, parent) changes.
     */
    public static final String CONTENT_CHANGED = "contentChanged";

    /**
     * The color assigned to a place when none is specified.
     */
    public static final Color DEFAULT_COLOR = Color.GREY;

    /**
     * Orders places by name.
     */
    public static final Comparator<Place> COMPARATOR = Comparator.comparing(Place::getName);

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Fragment used to render a place's level in exception messages.
     */
    private static final String LEVEL_MESSAGE_FRAGMENT = "' (lvl ";

    /**
     * Placeholder used in exception messages when there is no parent place to describe.
     */
    private static final String NULL_PLACEHOLDER = "null";

    /**
     * This place's unique id.
     */
    private final Long id;

    /**
     * The places directly nested under this one.
     */
    private final List<Place> places;

    /**
     * This place's display name.
     */
    private String name;

    /**
     * This place's parent, or null for a root place.
     */
    private Place parent;

    /**
     * This place's nesting level.
     */
    @NonNull private PlaceLevel level;

    /**
     * Whether this place has no parent (or its parent is the implicit root place).
     */
    private boolean isRootPlace;

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport;

    // where to split into an HCI comp ?
    /**
     * This place's display color.
     */
    private Color color;

    /**
     * Whether this place is currently selected.
     */
    private boolean selected;

    protected Place(final long placeId, final String placeName, final PlaceLevel placeLevel, final Place parentPlace, final Color aColor) {
        id = placeId;
        name = placeName;
        parent = parentPlace;
        if (placeLevel == null) {
            level = PlaceLevel.ROOT;
            isRootPlace = true;
        } else {
            level = placeLevel;
            isRootPlace = parentPlace == null || PlaceFactory.PLACES_PLACE.equals(parentPlace);
        }
        places = new LinkedList<>();
        color = aColor;
        //
        if (parentPlace != null) {
            parentPlace.addPlace(Place.this);
        }
        propertyChangeSupport = new PropertyChangeSupport(Place.this);
        selected = false;
        if (!isLowerThanOrLeveled(parent)) {
            final String parentName;
            final Object parentLevel;
            if (parent == null) {
                parentName = NULL_PLACEHOLDER;
                parentLevel = NULL_PLACEHOLDER;
            } else {
                parentName = parent.name;
                parentLevel = parent.level;
            }
            throw new IllegalStateException("For place '" + name + LEVEL_MESSAGE_FRAGMENT + level + ") is greater or equal than its parent place '" + parentName + LEVEL_MESSAGE_FRAGMENT + parentLevel + ")");
        }
    }

    protected Place(final long placeId, final String placeName, final PlaceLevel placeLevel, final Place parentPlace) {
        this(placeId, placeName, placeLevel, parentPlace, DEFAULT_COLOR);
    }

    @Override
    public long getId() {
        return id;
    }

    /**
     * Adds a listener notified of this place's changes.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @param isSelected whether this place is now selected
     */
    public void setSelected(final boolean isSelected) {
        final var update = selected != isSelected;
        selected = isSelected;
        if (update) {
            propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, null, selected);
        }
    }

    /**
     * @return this place's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param aName this place's new name
     */
    public void setName(final String aName) {
        name = aName;
        propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
    }

    /**
     * @return this place's nesting level
     */
    public PlaceLevel getLevel() {
        return level;
    }

    /**
     * @param aLevel this place's new nesting level
     * @return true if the level was set, false if it conflicts with a child place's level
     */
    public boolean setLevel(final PlaceLevel aLevel) {
        final boolean placeConflict = places.stream().anyMatch(p -> p.getLevel().getLevelValue() >= aLevel.getLevelValue());
        if (placeConflict) {
            LOG.log(Level.SEVERE, "New level cannot be set because of children levels : {0}", new Object[]{aLevel});
            return false;
        }
        level = aLevel;
        propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
        return true;
    }

    /**
     * @return whether this place has no parent (or its parent is the implicit root place)
     */
    public boolean isRootPlace() {
        return isRootPlace;
    }

    /**
     * @return this place's parent, or null for a root place
     */
    public Place getParent() {
        return parent;
    }

    /**
     * @param aParentPlace this place's new parent
     */
    public void setParent(final Place aParentPlace) {
        final Place oldParent = parent;
        parent = aParentPlace;
        isRootPlace = parent == null || PlaceFactory.PLACES_PLACE.equals(parent);
        if (oldParent != null && oldParent != parent) {
            oldParent.removePlace(this);
            if (parent != null) {
                parent.addPlace(this);
            }
        }
        propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
    }

    /**
     * @return an unmodifiable list of the places directly nested under this one
     */
    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

    private boolean addPlace(final Place place) {
        if (place.getLevel().getLevelValue() < level.getLevelValue()) {
            places.add(place);
            propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param place the place to remove
     * @return true if the place was removed, false if it wasn't a direct child of this one
     */
    public boolean removePlace(final Place place) {
        final boolean result = places.remove(place);
        propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
        return result;
    }

    /**
     * @return this place's display color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param aColor this place's new display color
     */
    public void setColor(final Color aColor) {
        color = aColor;
        propertyChangeSupport.firePropertyChange(CONTENT_CHANGED, null, this);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return whether this place is currently selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Checks whether this place's level is lower than or equal to another place's level.
     *
     * @param anotherPlace the place to compare with, may be null
     * @return true if this place's level is lower than or equal to {@code anotherPlace}'s, or if {@code anotherPlace} is null
     */
    public boolean isLowerThanOrLeveled(final Place anotherPlace) {
        if (anotherPlace == null) {
            return true;
        } else {
            return level.getLevelValue() <= anotherPlace.level.getLevelValue();
        }
    }

    /**
     * Checks whether this place contains another place, directly or through a nested child.
     *
     * @param anotherPlace the place to check, may be null
     * @return true if {@code anotherPlace} is this place or one of its descendants
     */
    public boolean encompasses(final Place anotherPlace) {
        if (anotherPlace == null || level.getLevelValue() < anotherPlace.level.getLevelValue()) {
            return false;
        }
        if (anotherPlace.equals(this)) {
            return true;
        }
        if (places.isEmpty()) {
            return false;
        } else {
            for (Place childPlace : places) {
                if (childPlace.encompasses(anotherPlace)) {
                    return true;
                }
            }
        }
        return false;
    }

}
