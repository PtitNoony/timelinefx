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
package com.github.noony.app.timelinefx.hmi.freemap;

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.freemap.DateHandle;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.Link;
import com.github.noony.app.timelinefx.core.freemap.PersonInitLink;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.core.freemap.Portrait;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class FriezeFreeFormDrawing implements ZoomProvider {

    public static final double MAP_BORDER = 0;//todo investigate why size does not take this into account
    public static final double MAP_PADDING = 8;

    private final PropertyChangeSupport propertyChangeSupport;

    private final Pane mainNode;
    private final Group freeMapGroup;
    private final Group freeMapInnerGroup;
    private final Group personsGroup;
    private final Group personLinksGroup;
    private final Group placesGroup;
    private final Group linkGroup;
    private final Group plotGroup;
    private final Group startDateHandleGroup;
    private final Group endDateHandleGroup;
    //
    private final Rectangle background;
    private final Rectangle innerBackground;
    private final Rectangle personsBackground;
    private final Rectangle placesBackground;
    private final Rectangle startDatesBackground;
    private final Rectangle endDatesBackground;

    private final FriezeFreeMap friezeFreeMap;
    //
    private final Map<Plot, RectanglePlot> plotDrawings;
    private final Map<Person, PortraitDrawing> portraitDrawings;
    private final Map<Person, PersonInitLinkDrawing> personInitLinkDrawings;
    private final Map<FreeMapPlace, PlaceDrawing> placeDrawings;
    private final Map<Long, DateHandleDrawing> startDatesHandles;
    private final Map<Long, DateHandleDrawing> endDatesHandles;
    //
    private final List<IFxScalableNode> scalableNodes;
    //
    private double drawingWidth;
    private double drawingHeight;
    //
    private double scale = 1.0;

    public FriezeFreeFormDrawing(FriezeFreeMap aFriezeFreeMap) {
        friezeFreeMap = aFriezeFreeMap;
        //
        placeDrawings = new HashMap<>();
        plotDrawings = new HashMap<>();
        portraitDrawings = new HashMap<>();
        personInitLinkDrawings = new HashMap<>();
        startDatesHandles = new HashMap<>();
        endDatesHandles = new HashMap<>();
        //
        scalableNodes = new LinkedList<>();
        //
        drawingWidth = friezeFreeMap.getFreeMapWidth() + 2 * (MAP_BORDER + MAP_PADDING);
        drawingHeight = friezeFreeMap.getFreeMapHeight() + friezeFreeMap.getTimeHeight() + 2 * (MAP_BORDER + MAP_PADDING);
        //
        propertyChangeSupport = new PropertyChangeSupport(FriezeFreeFormDrawing.this);
        //
        mainNode = new Pane();
        freeMapGroup = new Group();
        freeMapInnerGroup = new Group();
        personsGroup = new Group();
        personLinksGroup = new Group();
        placesGroup = new Group();
        linkGroup = new Group();
        plotGroup = new Group();
        startDateHandleGroup = new Group();
        endDateHandleGroup = new Group();
        //
        background = new Rectangle(500, 500);
        innerBackground = new Rectangle(500, 500);
        placesBackground = new Rectangle(500, 500);
        personsBackground = new Rectangle(500, 500);
        startDatesBackground = new Rectangle(500, 500);
        startDatesBackground.setOpacity(1);
        endDatesBackground = new Rectangle(500, 500);
        endDatesBackground.setOpacity(1);
        //
        initFx();
        //
        friezeFreeMap.addPropertyChangeListener(FriezeFreeFormDrawing.this::handleFriezeChange);
    }

    public FriezeFreeMap getFriezeFreeMap() {
        return friezeFreeMap;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Node getNode() {
        return mainNode;
    }

    @Override
    public double getViewingScale() {
        return scale;
    }

    public void zoomIn() {
        scale = Math.max(0.2, scale - 0.1);
        updateLayout();
    }

    public void zoomOut() {
        scale = Math.min(10, scale + 0.1);
        updateLayout();
    }

    private void updateWidth() {
        drawingWidth = (friezeFreeMap.getFreeMapWidth() + 2 * (MAP_PADDING)) * scale;
        background.setWidth(drawingWidth);
        innerBackground.setWidth(friezeFreeMap.getFreeMapWidth() * scale);
        personsBackground.setWidth(friezeFreeMap.getPersonWidth() * scale);
        placesBackground.setWidth(friezeFreeMap.getPlaceDrawingWidth() * scale);
        startDatesBackground.setWidth(friezeFreeMap.getPlaceDrawingWidth() * scale);
        endDatesBackground.setWidth(friezeFreeMap.getPlaceDrawingWidth() * scale);
        startDateHandleGroup.setTranslateX((MAP_PADDING + 2 * friezeFreeMap.getPadding() + friezeFreeMap.getPersonWidth()) * scale);
        endDateHandleGroup.setTranslateX((MAP_PADDING + 2 * friezeFreeMap.getPadding() + friezeFreeMap.getPersonWidth()) * scale);
    }

    private void updateHeight() {
        drawingHeight = (friezeFreeMap.getFreeMapHeight() + 2 * (MAP_PADDING + friezeFreeMap.getTimeHeight())) * scale;
        background.setHeight(drawingHeight);
        innerBackground.setHeight(friezeFreeMap.getFreeMapHeight() * scale);
        personsBackground.setHeight(friezeFreeMap.getPersonHeight() * scale);
        placesBackground.setHeight(friezeFreeMap.getPlaceDrawingHeight() * scale);
        endDateHandleGroup.setTranslateY((drawingHeight - friezeFreeMap.getTimeHeight() - MAP_PADDING) * scale);
        startDatesBackground.setHeight(friezeFreeMap.getTimeHeight() * scale);
        endDatesBackground.setHeight(friezeFreeMap.getTimeHeight() * scale);
    }

    public double getWidth() {
        return (drawingWidth + 2.0 * MAP_BORDER) * scale;
    }

    public double getHeight() {
        return (drawingHeight + 2.0 * MAP_BORDER) * scale;
    }

    protected RectanglePlot getPlotDrawing(Plot plot) {
        return plotDrawings.get(plot);
    }

    protected PortraitDrawing getPortrait(Person person) {
        return portraitDrawings.get(person);
    }

    private void createStartDateHandle(DateHandle date) {
        var handle = new DateHandleDrawing(date, scale);
        startDateHandleGroup.getChildren().add(handle.getNode());
        startDatesHandles.put(date.getDate(), handle);
        scalableNodes.add(handle);
    }

    private void createEndDateHandle(DateHandle date) {
        var handle = new DateHandleDrawing(date, scale);
        endDateHandleGroup.getChildren().add(handle.getNode());
        endDatesHandles.put(date.getDate(), handle);
        scalableNodes.add(handle);
    }

    private void createPlaceDrawing(FreeMapPlace place) {
        var placeDrawing = new PlaceDrawing(place, friezeFreeMap);
        placesGroup.getChildren().add(placeDrawing.getNode());
        placeDrawings.put(place, placeDrawing);
        scalableNodes.add(placeDrawing);
    }

    private void createPlot(Plot plot) {
        var rectanglePlot = new RectanglePlot(plot);
        plotDrawings.put(plot, rectanglePlot);
        scalableNodes.add(rectanglePlot);
        plotGroup.getChildren().add(rectanglePlot.getNode());
    }

    private void createPortraitDrawing(Portrait portrait) {
        var portraitDrawing = new PortraitDrawing(portrait);
        portraitDrawings.put(portrait.getPerson(), portraitDrawing);
        personsGroup.getChildren().add(portraitDrawing.getNode());
        scalableNodes.add(portraitDrawing);
    }

    private void createLink(Link link) {
        var linkDrawing = new LinkDrawing(link);
        scalableNodes.add(linkDrawing);
        linkGroup.getChildren().add(linkDrawing.getNode());
    }

    private void createPersonInitLinkDrawing(PersonInitLink personInitLink) {
        PersonInitLinkDrawing linkDrawing = new PersonInitLinkDrawing(personInitLink, this);
        scalableNodes.add(linkDrawing);
        personInitLinkDrawings.put(personInitLink.getPerson(), linkDrawing);
        personLinksGroup.getChildren().add(linkDrawing.getNode());
    }

    private void initFx() {
        background.setFill(Color.BLACK);
        background.setArcWidth(MAP_PADDING);
        background.setArcHeight(MAP_PADDING);
        //
        innerBackground.setFill(Color.BLACK);
        innerBackground.setArcWidth(MAP_PADDING);
        innerBackground.setArcHeight(MAP_PADDING);
        //
        personsBackground.setFill(Color.ANTIQUEWHITE);
        //
        placesBackground.setFill(Color.FIREBRICK);
        placesBackground.setOpacity(0);
        //
        startDatesBackground.setFill(Color.CRIMSON);
        //
        endDatesBackground.setFill(Color.CRIMSON);
        //
        //
        personsGroup.getChildren().add(personsBackground);
        placesGroup.getChildren().add(placesBackground);
        startDateHandleGroup.getChildren().addAll(startDatesBackground);
        endDateHandleGroup.getChildren().addAll(endDatesBackground);
        friezeFreeMap.getStartDateHandles().forEach(this::createStartDateHandle);
        friezeFreeMap.getEndDateHandles().forEach(this::createEndDateHandle);
        //
        friezeFreeMap.getPlaces().forEach(this::createPlaceDrawing);
        //
        List<Plot> plots = friezeFreeMap.getPlots();
        plots.forEach(this::createPlot);
        //
        List<Link> links = new LinkedList<>();
        links.addAll(friezeFreeMap.getStayLinks());
        links.addAll(friezeFreeMap.getTravelLinks());
        links.forEach(this::createLink);
        //
        friezeFreeMap.getPortraits().forEach(this::createPortraitDrawing);
        //
        friezeFreeMap.getPersonInitLinks().forEach(this::createPersonInitLinkDrawing);
        //
        mainNode.getChildren().addAll(freeMapGroup);
        freeMapGroup.getChildren().addAll(background, freeMapInnerGroup, startDateHandleGroup, endDateHandleGroup);
        freeMapInnerGroup.getChildren().addAll(innerBackground, placesGroup, personLinksGroup, personsGroup, linkGroup, plotGroup);
        //
        // TODO
        startDatesBackground.setVisible(false);
        endDatesBackground.setVisible(false);
        personsBackground.setVisible(false);
        //
        updateLayout();
    }

    private void handleSelectedItemChange(PropertyChangeEvent event) {
        propertyChangeSupport.firePropertyChange(event);
    }

    public void setTimeHandlesVisible(boolean visibility) {
        startDatesHandles.values().forEach(d -> d.setVisible(visibility));
        endDatesHandles.values().forEach(d -> d.setVisible(visibility));
    }

    public void setDateHandlesColor(Color color) {
        startDatesHandles.values().forEach(d -> d.setColor(color));
        endDatesHandles.values().forEach(d -> d.setColor(color));
    }

    public void updateLayout() {
        updateWidth();
        updateHeight();
        scalableNodes.forEach(node -> node.updateScale(scale));
        //
        startDatesBackground.setArcWidth(scale * MAP_PADDING / 2.0);
        startDatesBackground.setArcHeight(scale * MAP_PADDING / 2.0);
        endDatesBackground.setArcWidth(scale * MAP_PADDING / 2.0);
        endDatesBackground.setArcHeight(scale * MAP_PADDING / 2.0);
        //
        startDatesBackground.setHeight(friezeFreeMap.getTimeHeight() * scale);
        endDatesBackground.setHeight(friezeFreeMap.getTimeHeight() * scale);
        //
        freeMapGroup.setTranslateX(MAP_BORDER * scale);
        freeMapGroup.setTranslateY(MAP_BORDER * scale);
        freeMapInnerGroup.setTranslateX(MAP_PADDING * scale);
        freeMapInnerGroup.setTranslateY((MAP_PADDING + friezeFreeMap.getTimeHeight()) * scale);
        //
        personsGroup.setTranslateX(friezeFreeMap.getPersonsDrawingX() * scale);
        personsGroup.setTranslateY(friezeFreeMap.getPersonsDrawingY() * scale);
        //
        placesGroup.setTranslateX(friezeFreeMap.getPlaceDrawingX() * scale);
        placesGroup.setTranslateY(friezeFreeMap.getPlaceDrawingY() * scale);
        linkGroup.setTranslateX(friezeFreeMap.getPlaceDrawingX() * scale);
        linkGroup.setTranslateY(friezeFreeMap.getPlaceDrawingY() * scale);
        plotGroup.setTranslateX(friezeFreeMap.getPlaceDrawingX() * scale);
        plotGroup.setTranslateY(friezeFreeMap.getPlaceDrawingY() * scale);
        personInitLinkDrawings.values().forEach(PersonInitLinkDrawing::updatePosition);
    }

    private void handleFriezeChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FriezeFreeMap.LAYOUT_CHANGED -> {
                updateLayout();
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }
}
