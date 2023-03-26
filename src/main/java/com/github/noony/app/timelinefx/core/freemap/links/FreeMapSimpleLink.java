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
package com.github.noony.app.timelinefx.core.freemap.links;

import com.github.noony.app.timelinefx.core.freemap.FreeMapLink;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class FreeMapSimpleLink implements FreeMapLink {

    public static final Color DEFAULT_COLOR = Color.BLUEVIOLET;
    public static final String COLOR_CHANGED = "colorChanged";
    public static final String LINK_SHAPE_CHANGED = "linkShapeChanged";
    public static final String CONNECTOR_ADDED = "connectorAdded";

    private final PropertyChangeSupport propertyChangeSupport;

    private final long id;
    private final FreeMapPerson person;
    private final FreeMapConnector beginConnector;
    private final FreeMapConnector endConnector;
    private final LinkType linkType;
    //
    private final List<FreeMapConnector> intermediateConnectors;
    //
    private LinkShape linkShape;
    private Color color;
    private boolean isSelected = false;

    protected FreeMapSimpleLink(long anID, FreeMapPerson aPerson, FreeMapConnector aBeginPlot, FreeMapConnector aEndPlot, LinkType type, Color aColor, LinkShape aLinkShape) {
        id = anID;
        propertyChangeSupport = new PropertyChangeSupport(FreeMapSimpleLink.this);
        intermediateConnectors = new LinkedList<>();
        //
        person = aPerson;
        beginConnector = aBeginPlot;
        endConnector = aEndPlot;
        linkType = type;
        color = aColor;
        linkShape = aLinkShape;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean newIsSelected) {
        if (newIsSelected != isSelected) {
            isSelected = newIsSelected;
            propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, this, isSelected);
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public FreeMapConnector getBeginConnector() {
        return beginConnector;
    }

    @Override
    public FreeMapConnector getEndConnector() {
        return endConnector;
    }

    @Override
    public FreeMapPerson getPerson() {
        return person;
    }

    @Override
    public LinkType getType() {
        return linkType;
    }

    @Override
    public LinkShape getLinkShape() {
        return linkShape;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color aColor) {
        color = aColor;
        propertyChangeSupport.firePropertyChange(COLOR_CHANGED, this, color);
    }

    @Override
    public void setConnectorsVisibility(boolean visibility) {
        beginConnector.setVisible(visibility);
        endConnector.setVisible(visibility);
    }

    @Override
    public void setLinkShape(LinkShape aLinkShape) {
        linkShape = aLinkShape;
        propertyChangeSupport.firePropertyChange(LINK_SHAPE_CHANGED, this, linkShape);
    }

    @Override
    public void addIntermediateConnector(FreeMapConnector aConnector) {
        if (!intermediateConnectors.contains(aConnector)) {
            intermediateConnectors.add(aConnector);
            propertyChangeSupport.firePropertyChange(INTERMEDIATE_CONNECTOR_ADDED, this, aConnector);
        }
    }

    @Override
    public void removeIntermediateConnector(FreeMapConnector aConnector) {
        if (!intermediateConnectors.remove(aConnector)) {
            propertyChangeSupport.firePropertyChange(INTERMEDIATE_CONNECTOR_REMOVED, this, aConnector);
        }
    }

    @Override
    public List<FreeMapConnector> getIntermediateConnectors() {
        return Collections.unmodifiableList(intermediateConnectors);
    }


    @Override
    public String getInfo() {
        return "Link " + linkType + " from: " + beginConnector + "  to:" + endConnector;
    }

}
