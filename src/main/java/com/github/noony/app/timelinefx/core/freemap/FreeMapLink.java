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

import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.IFriezeObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class FreeMapLink implements Selectable, IFriezeObject {

    private static final Color DEFAULT_COLOR = Color.BLUEVIOLET;
    private static final String COLOR_CHANGED = "colorChanged";
    private static final String LINK_SHAPE_CHANGED = "linkShapeChanged";
    private static final String CONNECTOR_ADDED = "connectorAdded";

    private final PropertyChangeSupport propertyChangeSupport;

    private final long id;
    private final Person person;
    private final AbstractFreeMapConnector beginConnector;
    private final AbstractFreeMapConnector endConnector;
    private final LinkType linkType;
    //
    private final List<AbstractFreeMapConnector> intermediateConnectors;
    //
    private LinkShape linkShape;
    private Color color;
    private boolean isSelected = false;

    protected FreeMapLink(long anID, Person aPerson, AbstractFreeMapConnector aBeginPlot, AbstractFreeMapConnector aEndPlot, LinkType type, Color aColor, LinkShape aLinkShape) {
        id = anID;
        FriezeObjectFactory.addObject(FreeMapLink.this);
        //
        propertyChangeSupport = new PropertyChangeSupport(FreeMapLink.this);
        intermediateConnectors = new LinkedList<>();
        //
        person = aPerson;
        beginConnector = aBeginPlot;
        endConnector = aEndPlot;
        linkType = type;
        color = aColor;
        linkShape = aLinkShape;
    }

    protected FreeMapLink(Person aPerson, AbstractFreeMapConnector aBeginPlot, AbstractFreeMapConnector aEndPlot, LinkType type, Color aColor, LinkShape aLinkShape) {
        this(FriezeObjectFactory.getNextID(), aPerson, aBeginPlot, aEndPlot, type, aColor, aLinkShape);
    }

    protected FreeMapLink(long anID, Person aPerson, AbstractFreeMapConnector aBeginPlot, AbstractFreeMapConnector aEndPlot, LinkType type, Color aColor) {
        this(anID, aPerson, aBeginPlot, aEndPlot, type, aColor, LinkShape.QUAD_LINE);
    }

    protected FreeMapLink(Person aPerson, AbstractFreeMapConnector aBeginPlot, AbstractFreeMapConnector aEndPlot, LinkType type, Color aColor) {
        this(FriezeObjectFactory.getNextID(), aPerson, aBeginPlot, aEndPlot, type, aColor, LinkShape.QUAD_LINE);
    }

    protected FreeMapLink(Person aPerson, AbstractFreeMapConnector beginPlot, AbstractFreeMapConnector endPlot, LinkType linkType) {
        this(FriezeObjectFactory.getNextID(), aPerson, beginPlot, endPlot, linkType, DEFAULT_COLOR);
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
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, this, this.isSelected);
    }

    @Override
    public long getId() {
        return id;
    }

    public AbstractFreeMapConnector getBeginPlot() {
        return beginConnector;
    }

    public AbstractFreeMapConnector getEndPlot() {
        return endConnector;
    }

    public Person getPerson() {
        return person;
    }

    public LinkType getType() {
        return linkType;
    }

    public LinkShape getLinkShape() {
        return linkShape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color aColor) {
        color = aColor;
        propertyChangeSupport.firePropertyChange(COLOR_CHANGED, this, color);
    }

    protected void setConnectorsVisible(boolean visibility) {
        beginConnector.setVisible(visibility);
        endConnector.setVisible(visibility);
    }

    protected void setLinkShape(LinkShape aLinkShape) {
        linkShape = aLinkShape;
        propertyChangeSupport.firePropertyChange(LINK_SHAPE_CHANGED, this, linkShape);
    }

    protected AbstractFreeMapConnector createConnector(long anID) {
        var newConnector = new FreeMapLinkConnector(anID, this, (endConnector.getDate() - beginConnector.getDate()) / 2.0 + beginConnector.getDate(), beginConnector.getPlotSize());
        // todo, manage person properly
        intermediateConnectors.add(newConnector);
        propertyChangeSupport.firePropertyChange(CONNECTOR_ADDED, this, newConnector);
        return newConnector;
    }

    @Override
    public String getInfo() {
        return "Link " + linkType + " from: " + beginConnector + "  to:" + endConnector;
    }

}
