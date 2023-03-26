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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.freemap.connectors.EndPlot;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import static com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory.createEndPlot;
import static com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory.createStartPlot;
import com.github.noony.app.timelinefx.core.freemap.connectors.StartPlot;
import com.github.noony.app.timelinefx.core.freemap.links.LinkType;
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
public class FreeMapSimpleStay implements FreeMapStay {

    private final long id;
    //
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final StayPeriod stay;
    private final StartPlot beginPlot;
    private final EndPlot endPlot;
    //
    private final FreeMapPerson person;
    private final FreeMapPlace place;
    //
    private final List<StayPeriod> stays;
    private final List<FreeMapConnector> intermediateConnectors;
    //
    private Color color;
    private final LinkShape linkShape;
    private boolean isSelected = false;

    protected FreeMapSimpleStay(long anID, StayPeriod aStay, long aStartID, long anEndID, FreeMapPerson aFreeMapPerson, FreeMapPlace aFreeMapPlace, double aPlotSize) {
        id = anID;
        propertyChangeSupport = new PropertyChangeSupport(FreeMapSimpleStay.this);
        stay = aStay;
        person = aFreeMapPerson;
        place = aFreeMapPlace;
        beginPlot = createStartPlot(aStartID, FreeMapSimpleStay.this, aPlotSize);
        endPlot = createEndPlot(anEndID, FreeMapSimpleStay.this, aPlotSize);
        stays = new LinkedList<>();
        intermediateConnectors = new LinkedList<>();
        stays.add(stay);
        color = stay.getPerson().getColor();
        linkShape = LinkShape.QUAD_LINE;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean containsStay(StayPeriod anotherStay) {
        return stay.equals(anotherStay);
    }

    @Override
    public FreeMapPerson getPerson() {
        return person;
    }

    @Override
    public FreeMapPlace getPlace() {
        return place;
    }

    @Override
    public FreeMapConnector getBeginConnector() {
        return beginPlot;
    }

    @Override
    public FreeMapConnector getEndConnector() {
        return endPlot;
    }

    @Override
    public StartPlot getStartPlot() {
        return beginPlot;
    }

    @Override
    public EndPlot getEndPlot() {
        return endPlot;
    }

    @Override
    public List<StayPeriod> getStayPeriods() {
        return Collections.unmodifiableList(stays);
    }

    @Override
    public List<FreeMapStay> getFreeMapStayPeriods() {
        return Collections.emptyList();
    }

    /**
     *
     * @return the stay's start date
     */
    @Override
    public double getStartDate() {
        return stay.getStartDate();
    }

    @Override
    public double getEndDate() {
        return stay.getEndDate();
    }

    @Override
    public void setPlotVisibility(boolean visibility) {
        beginPlot.setVisible(visibility);
        endPlot.setVisible(visibility);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public LinkShape getLinkShape() {
        return linkShape;
    }

    @Override
    public void setColor(Color aColor) {
        color = aColor;
        System.err.println("TODO FIRE setColor");
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
    public LinkType getType() {
        return LinkType.STAY;
    }

    @Override
    public void setConnectorsVisibility(boolean visibility) {
        intermediateConnectors.forEach(c -> c.setVisible(visibility));
    }

    @Override
    public void setLinkShape(LinkShape aLinkShape) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
    public void addIntermediateConnector(FreeMapConnector aConnector) {
        if (!intermediateConnectors.contains(aConnector)) {
            intermediateConnectors.add(aConnector);
            propertyChangeSupport.firePropertyChange(INTERMEDIATE_CONNECTOR_ADDED, this, aConnector);
        }
    }

    @Override
    public void removeIntermediateConnector(FreeMapConnector aConnector) {
        if (intermediateConnectors.remove(aConnector)) {
            propertyChangeSupport.firePropertyChange(INTERMEDIATE_CONNECTOR_ADDED, this, aConnector);
        }
    }

    @Override
    public List<FreeMapConnector> getIntermediateConnectors() {
        return Collections.unmodifiableList(intermediateConnectors);
    }

    @Override
    public String getInfo() {
        return getClass().getSimpleName() + "_" + stay.getDisplayString();
    }

}
