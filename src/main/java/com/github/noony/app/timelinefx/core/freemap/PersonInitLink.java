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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author arnaud
 */
public class PersonInitLink {

    /**
     * Property name for first plot change.
     */
    public static final String FIRST_PLOT_CHANGED = "PersonInitLinkFirstPlotChanged";
    /**
     * Property name for first plot position change.
     */
    public static final String FIRST_PLOT_POSITION_CHANGED = "PersonInitLinkFirstPlotPositionChanged";

    private final Person person;
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private Plot firstPlot;

    public PersonInitLink(final FreeMapPerson aFreeMapPerson) {
        propertyChangeSupport = new PropertyChangeSupport(PersonInitLink.this);
        person = aFreeMapPerson.getPerson();
        firstPlot = aFreeMapPerson.getFirstPlot();
        aFreeMapPerson.addPropertyChangeListener(PersonInitLink.this::handleFreeMapPersonChanges);
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Plot getFirstPlot() {
        return firstPlot;
    }

    public Person getPerson() {
        return person;
    }

    private void handleFreeMapPersonChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPerson.FIRST_PLOT_CHANGED -> {
                firstPlot = (Plot) event.getNewValue();
                propertyChangeSupport.firePropertyChange(FIRST_PLOT_CHANGED, this, firstPlot);
            }
            case FreeMapPerson.TRAVEL_LINK_ADDED, FreeMapPerson.TRAVEL_LINK_REMOVED, FreeMapPerson.STAY_ADDED , FreeMapPerson.STAY_REMOVED -> {
                // Nothing to do
            }
            case FreeMapPerson.LINK_ADDED, FreeMapPerson.LINK_REMOVED -> {
                // Nothing to do
            }
            case FreeMapPerson.PLOTS_ADDED, FreeMapPerson.PLOTS_REMOVED -> {
                // Nothing to do
            }
            default ->
                throw new UnsupportedOperationException("handleFreeMapPersonChanges:: " + event);
        }
    }

}
