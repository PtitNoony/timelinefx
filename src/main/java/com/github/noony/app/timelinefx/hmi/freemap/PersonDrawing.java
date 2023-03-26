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
package com.github.noony.app.timelinefx.hmi.freemap;

import com.github.noony.app.timelinefx.core.freemap.AbstractFreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.FreeMapLink;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.core.freemap.PortraitLink;
import com.github.noony.app.timelinefx.drawings.AbstractFxScalableNode;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.Group;
import javafx.util.Pair;

/**
 *
 * @author hamon
 */
public class PersonDrawing extends AbstractFxScalableNode {

    private final FreeMapPerson freeMapPerson;
    private final FriezeFreeMap freeMap;
    //
    private final PropertyChangeListener linkListener = PersonDrawing.this::handleLinkChanges;
    // TODO: remove work around
    private final FriezeFreeFormDrawing freeFormDrawing;
    //
    private final List<IFxScalableNode> scalableNodes;
    private final Map<FreeMapLink, LinkDrawing> linkDrawings;
    // TODO : use interface
    private final Map<AbstractFreeMapConnector, RectangleConnector> plotDrawings;
    //
    private final Group linkGroup;
    private final Group plotGroup;

    // todo remove FriezeFreeMap
    public PersonDrawing(FreeMapPerson aFreeMapPerson, FriezeFreeMap aFriezeFreeMap, FriezeFreeFormDrawing aFreeFormDrawing) {
        super();
        freeMap = aFriezeFreeMap;
        freeMapPerson = aFreeMapPerson;
        //
        freeFormDrawing = aFreeFormDrawing;
        //
        scalableNodes = new LinkedList<>();
        linkDrawings = new HashMap<>();
        plotDrawings = new HashMap<>();
        //
        linkGroup = new Group();
        plotGroup = new Group();
        //
        initLayout();
        //
        freeMapPerson.getStayLinks().forEach(PersonDrawing.this::createLink);
        freeMapPerson.getTravelLinks().forEach(PersonDrawing.this::createLink);
        freeMapPerson.getPlots().forEach(PersonDrawing.this::createConnector);
        //
        freeMapPerson.addPropertyChangeListener(PersonDrawing.this::handleFreeMapPersonChanges);
    }

    @Override
    protected void updateLayout() {
        scalableNodes.forEach(node -> node.updateScale(getScale()));
        linkGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
        plotGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
    }

    @Override
    public void updateScale(double newScale) {
        super.updateScale(newScale);
        updateLayout();
    }

    protected RectangleConnector getPlotDrawing(Plot plot) {
        return plotDrawings.get(plot);
    }

    private void initLayout() {
        addNode(linkGroup);
        linkGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
        addNode(plotGroup);
        plotGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
    }

    private void createLink(FreeMapLink link) {
        var linkDrawing = new LinkDrawing(link);
        scalableNodes.add(linkDrawing);
        linkDrawings.put(link, linkDrawing);
        linkGroup.getChildren().add(linkDrawing.getNode());
        linkDrawing.addPropertyChangeListener(linkListener);
    }

    private void removeLink(FreeMapLink link) {
        var linkDrawing = linkDrawings.remove(link);
        if (linkDrawing != null) {
            linkDrawing.removePropertyChangeListener(linkListener);
            scalableNodes.remove(linkDrawing);
            linkGroup.getChildren().remove(linkDrawing.getNode());
        }
    }

    private void createConnector(AbstractFreeMapConnector connector) {
        var rectangleConnector = new RectangleConnector(connector);
        plotDrawings.put(connector, rectangleConnector);
        scalableNodes.add(rectangleConnector);
        plotGroup.getChildren().add(rectangleConnector.getNode());
    }

    private void removeConnector(AbstractFreeMapConnector connector) {
        var rectangleConnector = plotDrawings.remove(connector);
        if (rectangleConnector != null) {
            scalableNodes.remove(rectangleConnector);
            plotGroup.getChildren().remove(rectangleConnector.getNode());
        }
    }

    protected void updateTimeXOffset(double offset) {
        linkGroup.setTranslateX(offset);
    }

    private void handleFreeMapPersonChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPerson.LINK_ADDED, FreeMapPerson.TRAVEL_LINK_ADDED ->
                createLink((FreeMapLink) event.getNewValue());
            case FreeMapPerson.LINK_REMOVED, FreeMapPerson.TRAVEL_LINK_REMOVED ->
                removeLink((FreeMapLink) event.getNewValue());
            case FreeMapPerson.PLOTS_ADDED -> {
                var startEndConnector = (Pair<AbstractFreeMapConnector, AbstractFreeMapConnector>) event.getNewValue();
                createConnector(startEndConnector.getKey());
                createConnector(startEndConnector.getValue());
            }
            case FreeMapPerson.PLOTS_REMOVED -> {
                var startEndPlot = (Pair<AbstractFreeMapConnector, AbstractFreeMapConnector>) event.getNewValue();
                removeConnector(startEndPlot.getKey());
                removeConnector(startEndPlot.getValue());
            }
            case FreeMapPerson.STAY_ADDED, FreeMapPerson.STAY_REMOVED -> {
                // nothing to do ??
            }
            case FreeMapPerson.PORTRAIT_LINK_ADDED -> {
                var portraitLink = (PortraitLink) event.getNewValue();
                createLink(portraitLink);
            }
            case FreeMapPerson.PORTRAIT_ADDED -> {
                var freeMapPortrait = (FreeMapPortrait) event.getNewValue();
                freeFormDrawing.createPortraitDrawing(freeMapPortrait);
            }
            default -> {
                System.err.println(" TODO handleFreeMapPersonChanges_01:" + event.getPropertyName());
            }
        }
    }

    private void handleLinkChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case LinkDrawing.SECONDARY_CLICKED -> {
                var link = (FreeMapLink) event.getNewValue();
                freeMapPerson.createPortrait(link);
            }
            default -> {
                System.err.println(" TODO handleLinkChanges:" + event.getPropertyName());
            }
        }
    }
}
