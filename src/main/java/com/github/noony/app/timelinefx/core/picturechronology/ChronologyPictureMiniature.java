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
package com.github.noony.app.timelinefx.core.picturechronology;

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Person;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class ChronologyPictureMiniature extends FriezeObject {

    public static final String POSITION_CHANGED = "ChronologyPictureMiniature" + "__positionChanged";
    public static final String SCALE_CHANGED = "ChronologyPictureMiniature" + "__scaleChanged";

    public static final Comparator<ChronologyPictureMiniature> COMPARATOR = (c1, c2) -> Long.compare(c1.getPicture().getAbsoluteTime(), c2.getPicture().getAbsoluteTime());

    private final PropertyChangeSupport propertyChangeSupport;

    private final IPicture picture;
    private final List<Person> persons;
    private Point2D position;
    private double scale;

    protected ChronologyPictureMiniature(long id, IPicture picture, Point2D position, double scale) {
        super(id);
        propertyChangeSupport = new PropertyChangeSupport(ChronologyPictureMiniature.this);
        persons = new LinkedList<>(picture.getPersons());
        this.picture = picture;
        this.position = position;
        this.scale = scale;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public IPicture getPicture() {
        return picture;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getScale() {
        return scale;
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public int getPersonIndex(Person aPerson) {
        return persons.indexOf(aPerson);
    }

    public Person getPersonAtIndex(int index) {
        return persons.get(index);
    }

    public void setPosition(Point2D newPosition) {
        position = newPosition;
        propertyChangeSupport.firePropertyChange(POSITION_CHANGED, this, position);
    }

    public void setScale(double newScale) {
        scale = newScale;
        propertyChangeSupport.firePropertyChange(SCALE_CHANGED, this, scale);
    }

    public double getWidth() {
        return scale * picture.getWidth();
    }

    public double getHeight() {
        return scale * picture.getHeight();
    }

    @Override
    public String toString() {
        return "Miniature_" + getId() + "_[" + picture.getName() + "]";
    }

}
