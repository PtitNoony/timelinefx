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

import com.github.noony.app.timelinefx.core.DateObject;
import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.IDateObject;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Person;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class ChronologyPictureMiniature extends FriezeObject {

    public static final String POSITION_CHANGED = "ChronologyPictureMiniature" + "__positionChanged";
    public static final String SCALE_CHANGED = "ChronologyPictureMiniature" + "__scaleChanged";
    public static final String TIME_CHANGED = "ChronologyPictureMiniature" + "__timeChanged";

    public static final Comparator<ChronologyPictureMiniature> COMPARATOR = (c1, c2) -> Double.compare(c1.getCurrenltyUsedAbsoluteTime(), c2.getCurrenltyUsedAbsoluteTime());

    private final PropertyChangeSupport propertyChangeSupport;

    private final IPicture picture;
    private final DateObject dateObject;
    private Point2D position;
    private double scale;
    private boolean usesCustomTime;

    protected ChronologyPictureMiniature(long id, IPicture aPicture, Point2D aPosition, double aScale) {
        super(id);
        propertyChangeSupport = new PropertyChangeSupport(ChronologyPictureMiniature.this);
        picture = aPicture;
        position = aPosition;
        scale = aScale;
        dateObject = new DateObject(aPicture);
        dateObject.addPropertyChangeListener(e -> propertyChangeSupport.firePropertyChange(TIME_CHANGED, e, dateObject.getAbsoluteTime()));
        usesCustomTime = false;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setUseCustomTime(boolean customTime) {
        if (usesCustomTime != customTime) {
            usesCustomTime = customTime;
            if (!isInSyncWithPicture()) {
                propertyChangeSupport.firePropertyChange(TIME_CHANGED, this, getCurrenltyUsedAbsoluteTime());
            }
        }
    }

    public boolean usesCustomTime() {
        return usesCustomTime;
    }

    public double getCurrenltyUsedAbsoluteTime() {
        if (usesCustomTime) {
            return dateObject.getAbsoluteTime();
        }
        return picture.getAbsoluteTime();
    }

    public void setCurrenltyUsedTimeValue(String aTimeValue) {
        if (usesCustomTime) {
            dateObject.setValue(aTimeValue);
        } else {
            picture.setValue(aTimeValue);
        }
    }

    public double getChonologyAbsoluteTime() {
        return dateObject.getAbsoluteTime();
    }

    public boolean isInSyncWithPicture() {
        return Double.compare(dateObject.getAbsoluteTime(), picture.getAbsoluteTime()) == 0;
    }

    public IDateObject getDateObject() {
        return dateObject;
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
        return picture.getPersons();
    }

    public int getPersonIndex(Person aPerson) {
        return picture.getPersons().indexOf(aPerson);
    }

    public Person getPersonAtIndex(int index) {
        return picture.getPersons().get(index);
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
