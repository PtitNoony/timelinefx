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

import com.github.noony.app.timelinefx.core.freemap.FreeMapDateHandle;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import static com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap.DEFAULT_TIME_HEIGHT;
import com.github.noony.app.timelinefx.drawings.FxScalableParent;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author hamon
 */
public class FriezeFreeFormDrawing {

    public static final String PORTRAIT_SECTION_REQUEST = "portraitSelectionRequest";

    public static final double MAIN_CONTAINER_PADDING = 8;

    private static final double INITIAL_BACKGROUND_SIZE = 500;

    private final PropertyChangeSupport propertyChangeSupport;

    private final VBox mainNode;
    // Group which contains all the graphical elements necessary for the free map display
    private final Group freeMapGroup;
    //
    private final Group startDateHandleGroup;
    private final Group endDateHandleGroup;
    // Group that contains only the printable elements (places, persons...). Date handles are not part of if for e.g.
    private final Group freeMapInnerGroup;
    //
    private final Group personsGroup;
    private final Group personLinksGroup;
    private final Group placesGroup;
    private final Group portraitsGroup;
    //
    private final Rectangle background;
    private final Rectangle innerBackground;
    private final Rectangle personsBackground;
    private final Rectangle placesBackground;
    private final Pane startDatesPane;
    private final Pane endDatesPane;

    private final FriezeFreeMap friezeFreeMap;
    //
    private final Map<FreeMapPortrait, FreeMapPortraitDrawing> portraitDrawings;
    private final Map<FreeMapPlace, PlaceDrawing> placeDrawings;
    private final Map<FreeMapPerson, FreeMapPersonDrawing> personDrawings;
    private final Map<Double, DateHandleDrawing> startDatesHandles;
    private final Map<Double, DateHandleDrawing> endDatesHandles;
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
        personDrawings = new HashMap<>();
        portraitDrawings = new HashMap<>();
        startDatesHandles = new HashMap<>();
        endDatesHandles = new HashMap<>();
        //
        scalableNodes = new LinkedList<>();
        //
        drawingWidth = friezeFreeMap.getWidth() + 2 * (MAIN_CONTAINER_PADDING);
        drawingHeight = friezeFreeMap.getHeight() + DEFAULT_TIME_HEIGHT + 2 * (MAIN_CONTAINER_PADDING);
        //
        propertyChangeSupport = new PropertyChangeSupport(FriezeFreeFormDrawing.this);
        //
        mainNode = new VBox();
        mainNode.setSpacing(MAIN_CONTAINER_PADDING);
        //
        freeMapGroup = new Group();
        freeMapInnerGroup = new Group();
        portraitsGroup = new Group();
        personsGroup = new Group();
        personLinksGroup = new Group();
        placesGroup = new Group();
        startDateHandleGroup = new Group();
        endDateHandleGroup = new Group();
        //
        background = new Rectangle(INITIAL_BACKGROUND_SIZE, INITIAL_BACKGROUND_SIZE);
        innerBackground = new Rectangle(INITIAL_BACKGROUND_SIZE, INITIAL_BACKGROUND_SIZE);
        placesBackground = new Rectangle(INITIAL_BACKGROUND_SIZE, INITIAL_BACKGROUND_SIZE);
        personsBackground = new Rectangle(INITIAL_BACKGROUND_SIZE, INITIAL_BACKGROUND_SIZE);
        startDatesPane = new Pane();
        endDatesPane = new Pane();
        //
        initFx();
        //
        friezeFreeMap.addPropertyChangeListener(FriezeFreeFormDrawing.this::handleFreeMapChange);
    }

    public FriezeFreeMap getFriezeFreeMap() {
        return friezeFreeMap;
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Node getNode() {
        return mainNode;
    }

    protected void zoomIn() {
        scale = Math.max(FxScalableParent.MIN_SCALE, scale - FxScalableParent.SCALE_STEP);
        updateLayout();
    }

    protected void zoomOut() {
        scale = Math.min(FxScalableParent.MAX_SCALE, scale + FxScalableParent.SCALE_STEP);
        updateLayout();
    }

    protected void setZoomLevel(double newScale) {
        if (newScale > FxScalableParent.MAX_SCALE) {
            scale = FxScalableParent.MAX_SCALE;
        } else {
            scale = Math.max(newScale, FxScalableParent.MIN_SCALE);
        }
        updateLayout();
    }

    public double getScale() {
        return scale;
    }

    private void updateWidth() {
        drawingWidth = friezeFreeMap.getWidth() * scale;
//        mainNode.setMaxWidth(drawingWidth + 2 * MAIN_CONTAINER_PADDING);
        background.setWidth(drawingWidth);
        //
        innerBackground.setWidth(friezeFreeMap.getFreeMapDrawingWidth() * scale);
        personsBackground.setWidth(friezeFreeMap.getPersonWidth() * scale);
        placesBackground.setWidth(friezeFreeMap.getPlaceDrawingWidth() * scale);
        startDatesPane.setMinWidth(drawingWidth);
        endDatesPane.setMinWidth(drawingWidth);
        //
        var dateGroupsTranslateX = (friezeFreeMap.getDrawingOffsetX() + friezeFreeMap.getPersonWidth()) * scale;
        startDateHandleGroup.setTranslateX(dateGroupsTranslateX);
        endDateHandleGroup.setTranslateX(dateGroupsTranslateX);
    }

    private void updateHeight() {
        drawingHeight = (friezeFreeMap.getHeight()) * scale;//+ 2 * (MAIN_CONTAINER_PADDING + DEFAULT_TIME_HEIGHT);
//        mainNode.setMaxHeight(drawingHeight + 2 * MAIN_CONTAINER_PADDING);
        background.setHeight(drawingHeight);
        innerBackground.setHeight(friezeFreeMap.getFreeMapDrawingHeight() * scale);
        personsBackground.setHeight(friezeFreeMap.getPersonHeight() * scale);
        placesBackground.setHeight(friezeFreeMap.getPlaceDrawingHeight() * scale);
    }

    public double getWidth() {
        return (drawingWidth + 2.0 * MAIN_CONTAINER_PADDING) * scale;
    }

    public double getHeight() {
        return (drawingHeight + 2.0 * MAIN_CONTAINER_PADDING) * scale;
    }

    private void addStartDateHandleDrawing(FreeMapDateHandle date) {
        if (!startDatesHandles.containsKey(date.getDate())) {
            var handle = new DateHandleDrawing(date, scale);
            startDateHandleGroup.getChildren().add(handle.getNode());
            startDatesHandles.put(date.getDate(), handle);
            scalableNodes.add(handle);
        }
    }

    private void addEndDateHandleDrawing(FreeMapDateHandle date) {
        if (!endDatesHandles.containsKey(date.getDate())) {
            final var handle = new DateHandleDrawing(date, scale);
            endDateHandleGroup.getChildren().add(handle.getNode());
            endDatesHandles.put(date.getDate(), handle);
            scalableNodes.add(handle);
            handle.updateScale(scale);
        }
    }

    private void addPlaceDrawing(FreeMapPlace place) {
        var placeDrawing = new PlaceDrawing(place, friezeFreeMap);
        placesGroup.getChildren().add(placeDrawing.getNode());
        placeDrawings.put(place, placeDrawing);
        scalableNodes.add(placeDrawing);
        updateLayout();
    }

    private void removePlaceDrawing(FreeMapPlace place) {
        var placeDrawing = placeDrawings.get(place);
        if (placeDrawing != null) {
            scalableNodes.remove(placeDrawing);
            placesGroup.getChildren().remove(placeDrawing.getNode());
            updateLayout();
        }
    }

    private void addPersonDrawing(FreeMapPerson person) {
        var personDrawing = new FreeMapPersonDrawing(person, friezeFreeMap, this);
        personsGroup.getChildren().add(personDrawing.getNode());
        personDrawings.put(person, personDrawing);
        scalableNodes.add(personDrawing);
        updateLayout();
    }

    private void removePersonDrawing(FreeMapPerson person) {
        var personDrawing = personDrawings.get(person);
        if (personDrawing != null) {
            scalableNodes.remove(personDrawing);
            personsGroup.getChildren().remove(personDrawing.getNode());
            updateLayout();
        }
    }

    protected void createPortraitDrawing(FreeMapPortrait portrait) {
        var portraitDrawing = new FreeMapPortraitDrawing(portrait, this);
        portraitDrawings.put(portrait, portraitDrawing);
        portraitsGroup.getChildren().add(portraitDrawing.getNode());
        scalableNodes.add(portraitDrawing);
    }

    protected void removePortraitDrawing(FreeMapPortrait portrait) {
        var portraitDrawingRemoved = portraitDrawings.remove(portrait);
        if (portraitDrawingRemoved != null) {
            portraitsGroup.getChildren().remove(portraitDrawingRemoved.getNode());
            scalableNodes.remove(portraitDrawingRemoved);
        }
    }

    private void initFx() {
        background.setFill(Color.BLACK);
        background.setArcWidth(MAIN_CONTAINER_PADDING);
        background.setArcHeight(MAIN_CONTAINER_PADDING);
        //
        innerBackground.setFill(null);
        innerBackground.setArcWidth(MAIN_CONTAINER_PADDING);
        innerBackground.setArcHeight(MAIN_CONTAINER_PADDING);
        innerBackground.setStroke(null);
        //
        personsBackground.setFill(Color.ANTIQUEWHITE);
        placesBackground.setFill(Color.FIREBRICK);
        //
//        mainNode.setBackground(new Background(new BackgroundFill(Color.CHARTREUSE, CornerRadii.EMPTY, Insets.EMPTY)));
//        startDatesPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//        endDatesPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        //
        startDatesPane.setMinHeight(DEFAULT_TIME_HEIGHT);
        startDatesPane.setMaxHeight(DEFAULT_TIME_HEIGHT);
        endDatesPane.setMinHeight(DEFAULT_TIME_HEIGHT);
        endDatesPane.setMaxHeight(DEFAULT_TIME_HEIGHT);
        //
        personsGroup.getChildren().add(personsBackground);
        placesGroup.getChildren().add(placesBackground);
        //
        startDatesPane.getChildren().addAll(startDateHandleGroup);
        endDatesPane.getChildren().addAll(endDateHandleGroup);
        //
        friezeFreeMap.getStartDateHandles().forEach(this::addStartDateHandleDrawing);
        friezeFreeMap.getEndDateHandles().forEach(this::addEndDateHandleDrawing);
        friezeFreeMap.getPlaces().forEach(this::addPlaceDrawing);
        friezeFreeMap.getPersons().forEach(this::addPersonDrawing);
        //
        mainNode.getChildren().addAll(startDatesPane, freeMapGroup, endDatesPane);
        freeMapGroup.getChildren().addAll(background, freeMapInnerGroup);
        freeMapInnerGroup.getChildren().addAll(innerBackground, placesGroup, personLinksGroup, personsGroup, portraitsGroup);
        //
        innerBackground.setVisible(true);
        personsBackground.setVisible(false);
        placesBackground.setVisible(false);
        //
        updateLayout();
    }

    public void setTimeHandlesVisible(boolean visibility) {
        startDatesHandles.values().forEach(d -> d.setVisible(visibility));
        endDatesHandles.values().forEach(d -> d.setVisible(visibility));
    }

    public void setDateHandlesColor(Color color) {
        startDatesHandles.values().forEach(d -> d.setColor(color));
        endDatesHandles.values().forEach(d -> d.setColor(color));
    }

    protected void requestPortraitSelectionUpdate(FreeMapPortrait aFreeMapPortrait) {
        propertyChangeSupport.firePropertyChange(PORTRAIT_SECTION_REQUEST, this, aFreeMapPortrait);
    }

    protected void updateLayout() {
        //
        freeMapGroup.setTranslateX(MAIN_CONTAINER_PADDING);
        freeMapGroup.setTranslateY(MAIN_CONTAINER_PADDING);
        startDatesPane.setTranslateX(MAIN_CONTAINER_PADDING);
        endDatesPane.setTranslateX(MAIN_CONTAINER_PADDING);
        //
        freeMapInnerGroup.setTranslateX(scale * friezeFreeMap.getDrawingOffsetX());
        freeMapInnerGroup.setTranslateY(scale * friezeFreeMap.getDrawingOffsetX());
        //
        updateWidth();
        updateHeight();
        scalableNodes.forEach(node -> node.updateScale(scale));
        //
        personsGroup.setTranslateX(friezeFreeMap.getPersonsDrawingX() * scale);
        personsGroup.setTranslateY(friezeFreeMap.getPersonsDrawingY() * scale);
        //
        placesGroup.setTranslateX(friezeFreeMap.getPlaceDrawingX() * scale);
        placesGroup.setTranslateY(friezeFreeMap.getPlaceDrawingY() * scale);
    }

    private void handleFreeMapChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case FriezeFreeMap.LAYOUT_CHANGED ->
                updateLayout();
            case FriezeFreeMap.FREE_MAP_PLACE_ADDED -> {
                var freeMapPlaceAdded = (FreeMapPlace) event.getNewValue();
                addPlaceDrawing(freeMapPlaceAdded);
            }
            case FriezeFreeMap.FREE_MAP_PLACE_REMOVED -> {
                var freeMapPlaceRemoved = (FreeMapPlace) event.getNewValue();
                removePlaceDrawing(freeMapPlaceRemoved);
            }
            case FriezeFreeMap.FREE_MAP_PLOT_SIZE_CHANGED -> {
                // nothing to do, taken care of FreeMapPerson
            }
            case FriezeFreeMap.FREE_MAP_PERSON_ADDED -> {
                var freeMapPersonAdded = (FreeMapPerson) event.getNewValue();
                addPersonDrawing(freeMapPersonAdded);
            }
            case FriezeFreeMap.FREE_MAP_PERSON_REMOVED -> {
                var freeMapPersonRemoved = (FreeMapPerson) event.getNewValue();
                removePersonDrawing(freeMapPersonRemoved);
            }
            case FriezeFreeMap.FREE_MAP_PORTRAIT_REMOVED -> {
                var freeMapPortraitRemoved = (FreeMapPortrait) event.getNewValue();
                System.err.println("TODO FREE_MAP_PORTRAIT_REMOVED");
//                removeInitialPortraitDrawing(freeMapPortraitRemoved);
            }
            case FriezeFreeMap.NAME_CHANGED -> {
                // nothing to do
            }
            case FriezeFreeMap.START_DATE_HANDLE_ADDED ->
                addStartDateHandleDrawing((FreeMapDateHandle) event.getNewValue());
            case FriezeFreeMap.END_DATE_HANDLE_ADDED ->
                addEndDateHandleDrawing((FreeMapDateHandle) event.getNewValue());
            case FriezeFreeMap.START_DATE_HANDLE_REMOVED ->
                removeStartDateHandleDrawing((FreeMapDateHandle) event.getNewValue());
            case FriezeFreeMap.END_DATE_HANDLE_REMOVED ->
                removeEndDateHandleDrawing((FreeMapDateHandle) event.getNewValue());
//            case FriezeFreeMap.FREE_MAP_PLOT_VISIBILITY_CHANGED -> {
//                System.err.println("TODO : handle FREE_MAP_PLOT_VISIBILITY_CHANGED "+event);
//                friezeFreeMap.setPlotVisibility(true);
//            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    private void removeStartDateHandleDrawing(FreeMapDateHandle aDateHandle) {
        var handleDrawing = startDatesHandles.remove(aDateHandle.getDate());
        if (handleDrawing != null) {
            startDateHandleGroup.getChildren().remove(handleDrawing.getNode());
        }
    }

    private void removeEndDateHandleDrawing(FreeMapDateHandle aDateHandle) {
        var handleDrawing = endDatesHandles.remove(aDateHandle.getDate());
        if (handleDrawing != null) {
            endDateHandleGroup.getChildren().remove(handleDrawing.getNode());
        }
    }
}
