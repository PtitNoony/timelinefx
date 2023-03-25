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

import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.Link;
import com.github.noony.app.timelinefx.core.freemap.Plot;
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
//    private final PersonInitLinkDrawing personInitLink;
    //
    private final PropertyChangeListener linkListener = PersonDrawing.this::handleLinkChanges;
    //
    private final List<IFxScalableNode> scalableNodes;
    private final Map<Link, LinkDrawing> linkDrawings;
    // TODO : use interface
    private final Map<Plot, RectanglePlot> plotDrawings;
    //
    private final Group linkGroup;
    private final Group plotGroup;

    // todo remove FriezeFreeMap
    public PersonDrawing(FreeMapPerson aFreeMapPerson, FriezeFreeMap aFriezeFreeMap, FriezeFreeFormDrawing freeFormDrawing) {
        super();
        freeMap = aFriezeFreeMap;
        freeMapPerson = aFreeMapPerson;
        //
        scalableNodes = new LinkedList<>();
        linkDrawings = new HashMap<>();
        plotDrawings = new HashMap<>();
        //
        linkGroup = new Group();
        plotGroup = new Group();
        //
//        personInitLink = new PersonInitLinkDrawing(freeMapPerson.getPersonInitLink(), freeFormDrawing, PersonDrawing.this);
//        scalableNodes.add(personInitLink);
        initLayout();
        //
        freeMapPerson.getStayLinks().forEach(PersonDrawing.this::createLink);
        freeMapPerson.getTravelLinks().forEach(PersonDrawing.this::createLink);
        //
        freeMapPerson.getPlots().forEach(PersonDrawing.this::createPlot);
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

    protected RectanglePlot getPlotDrawing(Plot plot) {
        return plotDrawings.get(plot);
    }

    private void initLayout() {
//        addNode(personInitLink.getNode());
        addNode(linkGroup);
        linkGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
        addNode(plotGroup);
        plotGroup.setTranslateX(freeMap.getPersonWidth() * getScale());
    }

    private void createLink(Link link) {
        var linkDrawing = new LinkDrawing(link);
        scalableNodes.add(linkDrawing);
        linkDrawings.put(link, linkDrawing);
        linkGroup.getChildren().add(linkDrawing.getNode());
        linkDrawing.addPropertyChangeListener(linkListener);
    }

    private void removeLink(Link link) {
        var linkDrawing = linkDrawings.remove(link);
        if (linkDrawing != null) {
            linkDrawing.removePropertyChangeListener(linkListener);
            scalableNodes.remove(linkDrawing);
            linkGroup.getChildren().remove(linkDrawing.getNode());
        }
    }

    private void createPlot(Plot plot) {
        var rectanglePlot = new RectanglePlot(plot);
        plotDrawings.put(plot, rectanglePlot);
        scalableNodes.add(rectanglePlot);
        plotGroup.getChildren().add(rectanglePlot.getNode());
    }

    private void removePlot(Plot plot) {
        var rectanglePlot = plotDrawings.remove(plot);
        if (rectanglePlot != null) {
            scalableNodes.remove(rectanglePlot);
            plotGroup.getChildren().remove(rectanglePlot.getNode());
        }
    }

    protected void updateTimeXOffset(double offset) {
        linkGroup.setTranslateX(offset);
    }

    private void handleFreeMapPersonChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FreeMapPerson.LINK_ADDED, FreeMapPerson.TRAVEL_LINK_ADDED ->
                createLink((Link) event.getNewValue());
            case FreeMapPerson.LINK_REMOVED, FreeMapPerson.TRAVEL_LINK_REMOVED ->
                removeLink((Link) event.getNewValue());
            case FreeMapPerson.PLOTS_ADDED -> {
                var startEndPlot = (Pair<Plot, Plot>) event.getNewValue();
                createPlot(startEndPlot.getKey());
                createPlot(startEndPlot.getValue());
            }
            case FreeMapPerson.PLOTS_REMOVED -> {
                var startEndPlot = (Pair<Plot, Plot>) event.getNewValue();
                removePlot(startEndPlot.getKey());
                removePlot(startEndPlot.getValue());
            }
            case FreeMapPerson.STAY_ADDED, FreeMapPerson.STAY_REMOVED -> {
                // nothing to do ??
            }
            default -> {
                System.err.println(" TODO handleFreeMapPersonChanges:" + event.getPropertyName());
            }
        }
    }

    private void handleLinkChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case LinkDrawing.SECONDARY_CLICKED -> {
                System.err.println(" TODO handleFreeMapPersonChanges:" + event.getPropertyName());
                var link = (Link)event.getNewValue();
                freeMapPerson.createPortrait(link);
            }
            default -> {
                System.err.println(" TODO handleFreeMapPersonChanges:" + event.getPropertyName());
            }
        }
    }
}
