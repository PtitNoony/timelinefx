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
package com.github.noony.app.timelinefx.core.freemap.links;

import com.github.noony.app.timelinefx.core.Factory;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FreeMapStay;
import com.github.noony.app.timelinefx.core.freemap.connectors.*;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class FreeMapLinkFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<FreeMapSimpleLink> FACTORY = new Factory<>();
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(FACTORY);

    private FreeMapLinkFactory() {
        // private utility constructor
    }

    public static void reset() {
        FACTORY.reset();
    }

    public static FreeMapSimpleLink getConnector(long id) {
        return FACTORY.get(id);
    }

    public static FreeMapStayLink createStayLink(long anId, FreeMapStay aPeriod, FreeMapPlot aBeginPlot, FreeMapPlot aEndPlot) {
        var idUsed = anId;
        if (anId == IFileObject.NO_ID) {
            idUsed = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anId)) {
            throw new IllegalArgumentException("Trying to create a StayLink  with existing id=" + anId);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating StayLink with id={0} and StayPeriod={1}.", new Object[]{anId, aPeriod});
        var stayLink = new FreeMapStayLink(idUsed, aPeriod, aBeginPlot, aEndPlot);
        FACTORY.addObject(stayLink);
        return stayLink;
    }

    public static FreeMapTravelLink createTravelLink(long anId, FreeMapPerson aPerson, FreeMapConnector aBeginPlot, FreeMapConnector anEndPlot) {
        var idUsed = anId;
        if (anId == IFileObject.NO_ID) {
            idUsed = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anId)) {
            throw new IllegalArgumentException("Trying to create a TravelLink with existing id=" + anId);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating StayLink with id={0} and Person={1}.", new Object[]{idUsed, aPerson});
        var travelLink = new FreeMapTravelLink(idUsed, aPerson, aBeginPlot, anEndPlot);
        FACTORY.addObject(travelLink);
        return travelLink;
    }

    public static PortraitLink createPortraitLink(long anID, FreeMapPortrait aFreeMapPortrait, FreeMapConnector aConnector) {
        var idUsed = anID;
        if (anID == IFileObject.NO_ID) {
            idUsed = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("Trying to create a PortraitLink with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating PortraitLink for portrait={0}.", new Object[]{aFreeMapPortrait});
        var portraitLink = new PortraitLink(idUsed, aFreeMapPortrait, aConnector);
        LOG.log(CREATION_LOGGING_LEVEL, "> done creating PortraitLink={0}.", new Object[]{portraitLink});
        return portraitLink;
    }

    public static PortraitLink createPortraitLink(FreeMapPortrait aFreeMapPortrait, FreeMapConnector aConnector) {
        var idUsed = FACTORY.getNextID();
        LOG.log(CREATION_LOGGING_LEVEL, "Creating PortraitLink for portrait={0}.", new Object[]{aFreeMapPortrait});
        var portraitLink = new PortraitLink(idUsed, aFreeMapPortrait, aConnector);
        LOG.log(CREATION_LOGGING_LEVEL, "> done creating PortraitLink={0}.", new Object[]{portraitLink});
        return portraitLink;
    }

    public static FreeMapSimpleLink createFreeMapLink(long anID, FreeMapPerson aPerson, FreeMapConnector aBeginPlot, FreeMapConnector aEndPlot, LinkType type, Color aColor, LinkShape aLinkShape) {
        var idUsed = anID;
        if (anID == IFileObject.NO_ID) {
            idUsed = FACTORY.getNextID();
        } else if (!FACTORY.isIdAvailable(anID)) {
            throw new IllegalArgumentException("Trying to create a FreeMapLink with existing id=" + anID);
        }
        LOG.log(CREATION_LOGGING_LEVEL, "Creating FreeMapLink with person={0}.", new Object[]{aPerson});
        var link = new FreeMapSimpleLink(idUsed, aPerson, aBeginPlot, aEndPlot, type, aColor, aLinkShape);
        LOG.log(CREATION_LOGGING_LEVEL, "> done creating FreeMapLink={0}.", new Object[]{link});
        return link;
    }

    public static FreeMapSimpleLink createFreeMapLink(long anID, FreeMapPerson aPerson, FreeMapConnector aBeginPlot, FreeMapConnector aEndPlot, LinkType type, Color aColor) {
        return createFreeMapLink(anID, aPerson, aBeginPlot, aEndPlot, type, aColor, LinkShape.QUAD_LINE);
    }

    public static FreeMapSimpleLink createFreeMapLink(FreeMapPerson aPerson, FreeMapConnector aBeginPlot, FreeMapConnector aEndPlot, LinkType type, Color aColor) {
        return createFreeMapLink(FACTORY.getNextID(), aPerson, aBeginPlot, aEndPlot, type, aColor, LinkShape.QUAD_LINE);
    }

    public static FreeMapSimpleLink createFreeMapLink(FreeMapPerson aPerson, FreeMapConnector beginPlot, FreeMapConnector endPlot, LinkType linkType) {
        return createFreeMapLink(FACTORY.getNextID(), aPerson, beginPlot, endPlot, linkType, FreeMapSimpleLink.DEFAULT_COLOR);
    }

    public static List<FreeMapSimpleLink> getConnectors() {
        return FACTORY.getObjects();
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

}
