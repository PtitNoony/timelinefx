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
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class FreeMapMergedStay implements FreeMapStay {

    private final long id;
    private final List<FreeMapStay> stays;
    private final List<FreeMapConnector> intermediateConnectors;
    //
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private final FreeMapPerson person;
    private FreeMapPlace forcedPlace = null;
    private FreeMapPlace place;
    //
    private FreeMapStay earliestStay;
    private FreeMapStay latestStay;
    private final StartPlot beginPlot;
    private final EndPlot endPlot;
    //
    private Color color;
    private LinkShape linkShape;
    private boolean isSelected = false;

    protected FreeMapMergedStay(long anID, long aStartID, long anEndID, long aLinkID, FreeMapPlace aForcedPlace, FreeMapStay... staysToMerge) {
        id = anID;
        stays = new LinkedList<>();
        intermediateConnectors = new LinkedList<>();
        //
        propertyChangeSupport = new PropertyChangeSupport(FreeMapMergedStay.this);
        //
        assert staysToMerge.length > 0;
        if (staysToMerge.length == 0) {
            throw new IllegalStateException("Cannot create a FreeMapMergedStay with NO stay.");
        }
        //
        updateEarliestStay(staysToMerge[0]);
        updateLatestStay(staysToMerge[0]);
        person = earliestStay.getPerson();
        forcedPlace = aForcedPlace;
        if (forcedPlace != null) {
            place = forcedPlace;
        } else {
            place = earliestStay.getPlace();
        }
        // loop and initialisation could be optimized
        for (FreeMapStay aStay : staysToMerge) {
            addStay(aStay);
        }
        beginPlot = createStartPlot(aStartID, FreeMapMergedStay.this, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        endPlot = createEndPlot(anEndID, FreeMapMergedStay.this, FriezeFreeMap.DEFAULT_PLOT_SIZE);
        color = earliestStay.getPerson().getPerson().getColor();
        linkShape = LinkShape.QUAD_LINE;
    }

    protected FreeMapMergedStay(long anId, FreeMapStay... staysToMerge) {
        this(anId, NO_ID, NO_ID, NO_ID, null, staysToMerge);
    }

    @Override
    public double getStartDate() {
        return beginPlot.getDate();
    }

    @Override
    public double getEndDate() {
        return endPlot.getDate();
    }

    /**
     * Costly method
     *
     * @return the stayPeriods the freemapStay represents.
     */
    @Override
    public List<StayPeriod> getStayPeriods() {
        return stays.stream().flatMap(s -> s.getStayPeriods().stream()).collect(Collectors.toList());
    }

    @Override
    public List<FreeMapStay> getFreeMapStayPeriods() {
        return Collections.unmodifiableList(stays);
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
    public FreeMapPerson getPerson() {
        return person;
    }

    @Override
    public FreeMapPlace getPlace() {
        return place;
    }

    @Override
    public boolean containsStay(StayPeriod anotherStay) {
        for (FreeMapStay fStay : stays) {
            if (fStay.containsStay(anotherStay)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setPlotVisibility(boolean visibility) {
        stays.forEach(stay -> stay.setPlotVisibility(visibility));
    }

    public final boolean addStay(FreeMapStay aStay) {
        // checks for consitency
        if (aStay.getPerson() != person) {
            throw new IllegalStateException("Cannot add  a stay " + aStay + " with person " + aStay.getPerson().getName()
                    + " to stay " + this + " with person " + person.getName());
        }
        // if the stay is forced to represent one specific place, ensure the stay is a leaf of this place
        if (forcedPlace != null && forcedPlace.getPlace().encompasses(aStay.getPlace().getPlace())) {
            throw new IllegalStateException("Cannot add a stay " + aStay + " with place " + aStay.getPerson().getName()
                    + " to stay " + this + " with place " + place.getName() + " since it is not encompassed.");
        }
//        if (aStay.getPlace() != place) {
//            throw new IllegalStateException("Cannot add a stay " + aStay + " with place " + aStay.getPlace().getName()
//                    + " to stay " + this + " with place " + place.getName());
//        }
        if (!stays.contains(aStay)) {
            if (forcedPlace == null && !aStay.getPlace().getPlace().isLowerThanOrLeveled(place.getPlace())) {
                place = aStay.getPlace();
                System.err.println("TODO : fire everywhere");
            }
            if (aStay.getStartDate() < earliestStay.getStartDate()) {
                updateEarliestStay(aStay);
            }
            if (aStay.getEndDate() > latestStay.getEndDate()) {
                updateLatestStay(aStay);
            }
            return true;
        }
        System.err.println("Todo: manage adding intermediate connectors");
        return false;
    }

    public boolean removeStay(FreeMapStay aStay) {
        if (!stays.contains(aStay) || stays.size() == 1) {
            return false;
        }
        stays.remove(aStay);
        var newEarliestStay = stays.stream().min(STAY_START_COMPARATOR).get();
        if (newEarliestStay != earliestStay) {
            updateEarliestStay(newEarliestStay);
        }
        var newLastestStay = stays.stream().max(STAY_END_COMPARATOR).get();
        if (newLastestStay != latestStay) {
            updateLatestStay(newLastestStay);
        }
        System.err.println("TODO: handle removing intermediate connectors");
        return true;
    }

    public int getNumberOfMergedStays() {
        return stays.size();
    }

    /**
     * This method does not compare with currenr earliestStay (on purpose, for initialisation purposes)
     *
     * @param aStay the stay to set as the starting point of the merged stay.
     */
    private void updateEarliestStay(FreeMapStay aStay) {
        earliestStay = aStay;
        beginPlot.setDate(earliestStay.getStartDate());
    }

    /**
     * This method does not compare with currenr latestStay (on purpose, for initialisation purposes)
     *
     * @param aStay the stay to set as the starting point of the merged stay.
     */
    private void updateLatestStay(FreeMapStay aStay) {
        latestStay = aStay;
        endPlot.setDate(latestStay.getEndDate());
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

    /**
     *
     * @return the merged stay info
     */
    @Override
    public String getInfo() {
        return getClass().getSimpleName() + "_" + getFreeMapStayPeriods().stream().map(s -> s.getInfo()).collect(Collectors.joining());
    }
}
