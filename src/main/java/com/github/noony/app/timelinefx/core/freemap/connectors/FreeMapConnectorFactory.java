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
package com.github.noony.app.timelinefx.core.freemap.connectors;

import com.github.noony.app.timelinefx.core.Factory;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.freemap.FreeMapLink;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapStay;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class FreeMapConnectorFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<FreeMapConnector> FACTORY = new Factory<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(FACTORY);

    private FreeMapConnectorFactory() {
        // private utility constructor
    }

    public static void reset() {
        FACTORY.reset();
    }

    public static FreeMapConnector getConnector(long id) {
        return FACTORY.get(id);
    }

    public static FreeMapBasicConnector createFreeMapBasicConnector(long anID, FriezeObject aParentObject, double aDate, double aPlotSize) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating FreeMapBasicConnector with id={0}.", new Object[]{anID});
        long idToUse = anID;
        if (anID == IFileObject.NO_ID) {
            idToUse = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("Trying to create a FreeMapBasicConnector with existing id=" + anID);
        }
        return new FreeMapBasicConnector(idToUse, aParentObject, aDate, aPlotSize);
    }

    public static FreeMapBasicConnector createFreeMapBasicConnector(FriezeObject aParentObject, double aDate, double aPlotSize) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating FreeMapBasicConnector witout id.");
        return new FreeMapBasicConnector(FACTORY.getNextID(), aParentObject, aDate, aPlotSize);
    }

    public static FreeMapPlot createPlot(FreeMapPerson aPerson, FreeMapPlace aPlace, double aDate, PlotType aType, long aPeriodID, double aPlotSize) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating plot with person={0} and default picture.", new Object[]{aPerson});

        var plot = new FreeMapPlot(FACTORY.getNextID(), aPerson, aPlace, aDate, aType, aPeriodID, aPlotSize);
        FACTORY.addObject(plot);
//        PROPERTY_CHANGE_SUPPORT.firePropertyChange(PLOT_CREATED, null, portrait);
        return plot;
    }

    public static FreeMapLinkConnector createFreeMapLinkConnector(long anID, FreeMapLink aSourceLink, double aDate, double aPlotSize) {
        long idToUse = anID;
        if (anID == IFileObject.NO_ID) {
            idToUse = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a FreeMapLinkConnector with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating FreeMapLinkConnector with existing={0}.", new Object[]{anID});
        var linkConnector =  new FreeMapLinkConnector(idToUse, aSourceLink, aDate, aPlotSize);
        aSourceLink.addIntermediateConnector(linkConnector);
        return linkConnector;
    }

//    // see id id needed?
//    @Deprecated
//    public FreeMapConnector createConnector(long anID) {
//        var newConnector = FreeMapConnectorFactory.createFreeMapLinkConnector(anID, this, (endConnector.getDate() - beginConnector.getDate()) / 2.0 + beginConnector.getDate(), beginConnector.getPlotSize());
//        // todo, manage person properly
//        intermediateConnectors.add(newConnector);
//        propertyChangeSupport.firePropertyChange(CONNECTOR_ADDED, this, newConnector);
//        return newConnector;
//    }

    public static final StartPlot createStartPlot(long anID, FreeMapStay stayPeriod, double plotSize) {
        long idToUse = anID;
        // TODO: clean code
        var freeMapPerson = stayPeriod.getPerson();
        var freeMapPlace = stayPeriod.getPlace();
        if (anID == IFileObject.NO_ID) {
            idToUse = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a StartPlot with existing id=" + anID);
        }
        return new StartPlot(idToUse, stayPeriod, freeMapPerson, freeMapPlace, plotSize);
    }

    public static final EndPlot createEndPlot(long anID, FreeMapStay stayPeriod, double plotSize) {
        long idToUse = anID;
        // TODO: clean code
        var freeMapPerson = stayPeriod.getPerson();
        var freeMapPlace = stayPeriod.getPlace();
        if (anID == IFileObject.NO_ID) {
            idToUse = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("trying to create a EndPlot with existing id=" + anID);
        }
        return new EndPlot(idToUse, stayPeriod, freeMapPerson, freeMapPlace, plotSize);
    }

    public static List<FreeMapConnector> getConnectors() {
        return FACTORY.getObjects();
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
