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
package com.github.noony.app.timelinefx.hmi.byplace;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.StayPeriod;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import static javafx.application.Platform.runLater;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class FriezeSpaceLinearDrawing {

    private final Frieze frieze;

    private final Group mainNode;
    private final VBox placesGroup;
    private final Group stayGroup;

    private final Rectangle background;

    private final Map<Place, PlaceDrawing> placesAndDrawings;
    //
    private double width = 800;
    private double height = 600;
    private double placeWith;
    // temp since no zoom
    private double timeWindowWidth;
    private double timeWindowX;
    //
    private double timeRatio = 1;

    public FriezeSpaceLinearDrawing(Frieze aFrieze) {
        frieze = aFrieze;
        frieze.addListener(FriezeSpaceLinearDrawing.this::handleFriezeChange);
        //
        placesAndDrawings = new HashMap<>();
        //
        mainNode = new Group();
        placesGroup = new VBox();
        placesGroup.setSpacing(8.0);
        placesGroup.setPadding(new Insets(8.0));
        stayGroup = new Group();
        background = new Rectangle();
        background.setFill(Color.DARKGREY);

        mainNode.getChildren().addAll(background, placesGroup, stayGroup);
        //
        frieze.getPlaces().forEach(p -> addPlaceDrawing(p));
        //
        runLater(() -> {
            setWidth(width);
            setHeight(height);
        });
    }

    private PlaceDrawing addPlaceDrawing(Place place) {
        if (!frieze.getStayPeriods(place).isEmpty()) {
            var placeDrawing = new PlaceDrawing(place, frieze);
            placesGroup.getChildren().add(placeDrawing.getNode());
            placesAndDrawings.put(place, placeDrawing);
            runLater(() -> {
                placeDrawing.setWidth(placeWith);
                updateStaysWidth();
                updateStaysHeight();
            });
            return placeDrawing;
        }
        return null;
    }

    private void removePlaceDrawing(Place deletedPlace) {
        PlaceDrawing placeDrawing = placesAndDrawings.get(deletedPlace);
        if (placeDrawing != null) {
            placesGroup.getChildren().remove(placeDrawing.getNode());
            placesAndDrawings.remove(deletedPlace);
            runLater(() -> {
                updateStaysWidth();
                updateStaysHeight();
            });
        }
    }

    private void addStayDrawing(StayPeriod stayAdded) {
        Place place = stayAdded.getPlace();
        PlaceDrawing placeDrawing = placesAndDrawings.get(place);
        if (placeDrawing == null) {
            placeDrawing = addPlaceDrawing(place);
        }
        placeDrawing.addStay(stayAdded);
        updateStaysHeight();
    }

    private void removeStayDrawing(StayPeriod stayRemoved) {
        Place place = stayRemoved.getPlace();
        PlaceDrawing placeDrawing = placesAndDrawings.get(place);
        if (placeDrawing != null) {
            placeDrawing.removeStay(stayRemoved);
        }
    }

    public Node getNode() {
        return mainNode;
    }

    public final void setWidth(double w) {
        width = w;
        placeWith = width - 2 * PlaceDrawing.DEFAULT_SEPARATION;
        timeWindowX = 2 * PlaceDrawing.DEFAULT_SEPARATION + PlaceDrawing.DEFAULT_NAME_WIDTH;
        timeWindowWidth = placeWith - 2 * PlaceDrawing.DEFAULT_SEPARATION - PlaceDrawing.DEFAULT_NAME_WIDTH;
        background.setWidth(width);
        placesAndDrawings.values().forEach(d -> d.setWidth(placeWith));
        stayGroup.setTranslateX(timeWindowX);
        //
        updateStaysWidth();
    }

    public final void setHeight(double h) {
        height = h;
        updateStaysHeight();
    }

    private void updateStaysWidth() {
        // simple for now
        double ratio = timeWindowWidth / (frieze.getMaxDateWindow() - frieze.getMinDateWindow());
        timeRatio = ratio;
        placesAndDrawings.values().forEach(p -> p.updateDateRatio(frieze.getMinDateWindow(), timeRatio));
    }

    private void updateStaysHeight() {
    }

    private void handleFriezeChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.DATE_WINDOW_CHANGED ->
                updateStaysWidth();
            case Frieze.STAY_ADDED -> {
                StayPeriod stayAdded = (StayPeriod) event.getNewValue();
                addStayDrawing(stayAdded);
            }
            case Frieze.PERSON_ADDED -> {
                // nothing to do
                // the frieze is in charge of adding itself the stays
            }
            case Frieze.PLACE_ADDED -> {
                Place newPlace = (Place) event.getNewValue();
                addPlaceDrawing(newPlace);
            }
            case Frieze.STAY_REMOVED -> {
                StayPeriod stayRemoved = (StayPeriod) event.getNewValue();
                removeStayDrawing(stayRemoved);
            }
            case Frieze.PERSON_REMOVED -> {
                // Nothing to do
            }
            case Frieze.PLACE_REMOVED -> {
                Place placeRemoved = (Place) event.getNewValue();
                removePlaceDrawing(placeRemoved);
            }
            case Frieze.NAME_CHANGED -> {
                // Nothing to do
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event.getPropertyName());
        }
    }

}
