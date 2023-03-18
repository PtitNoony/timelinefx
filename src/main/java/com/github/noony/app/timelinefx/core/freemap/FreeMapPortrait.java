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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Portrait;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author hamon
 */
public class FreeMapPortrait {

    public static final String POSITION_CHANGED = "positionChanged";
    public static final String RADIUS_CHANGED = "portraitRadiusChanged";
    public static final String PORTRAIT_UPDATED = "portraitUpdated";

    private static final double DEFAULT_POSITION = 0.0;

    private final PropertyChangeSupport propertyChangeSupport;
    private final Person person;
    private Portrait portrait;
    //
    private double xPos;
    private double yPos;
    private double radius;

    public FreeMapPortrait(Portrait aPortrait, double aRadius) {
        propertyChangeSupport = new PropertyChangeSupport(FreeMapPortrait.this);
        portrait = aPortrait;
        person = portrait.getPerson();
        radius = aRadius;
        xPos = DEFAULT_POSITION;
        yPos = DEFAULT_POSITION;
        person.addPropertyChangeListener(this::handlePersonChanges);
    }

    public Portrait getPortrait() {
        return portrait;
    }

    public Person getPerson() {
        return portrait.getPerson();
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setX(double x) {
        xPos = x;
        propertyChangeSupport.firePropertyChange(POSITION_CHANGED, xPos, yPos);
    }

    public void setY(double y) {
        yPos = y;
        propertyChangeSupport.firePropertyChange(POSITION_CHANGED, xPos, yPos);
    }

    public void setRadius(double newRadius) {
        radius = newRadius;
        propertyChangeSupport.firePropertyChange(RADIUS_CHANGED, this, radius);
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getRadius() {
        return radius;
    }

    private void handlePersonChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Person.PORTRAIT_ADDED, Person.DEFAULT_PORTRAIT_CHANGED -> {
                // nothing to do: if another portrait is added or default portrait is changed,
                // this freemapPortrait is not automatically changed.
            }
            case Person.PORTRAIT_REMOVED -> {
                // in the case the portrait is removed, then we switch to the default portrait
                var removedPortrait = (Portrait) event.getNewValue();
                if (removedPortrait == portrait) {
                    portrait = person.getDefaultPortrait();
                    propertyChangeSupport.firePropertyChange(PORTRAIT_UPDATED, this, radius);
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported change : " + event);
        }

    }

}
