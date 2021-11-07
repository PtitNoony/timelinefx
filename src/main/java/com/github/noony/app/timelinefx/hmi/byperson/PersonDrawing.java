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
package com.github.noony.app.timelinefx.hmi.byperson;

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.drawings.FXDrawing;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import com.github.noony.app.timelinefx.drawings.IFriezeView;

/**
 *
 * @author hamon
 */
public class PersonDrawing extends FXDrawing {

    public static final double DEFAULT_HEIGHT = 24;
    public static final double DEFAULT_WIDTH = 500;

    public static final double DEFAULT_SEPARATION = 12.0;
    public static final double DEFAULT_INNER_SEPARATION = 8.0;

    public static final double DEFAULT_NAME_WIDTH = 140;

    private final Person person;
    private final IFriezeView friezeView;
    private final Map<StayPeriod, PlaceStayDrawing> staysAndDrawings;

    //
    private final Label nameLabel;
    private final Line nameSeparationLine;
    private final Group placesGroup;
    private final Rectangle placesGroupClip;
    //
    private long currentMinDate = 0L;
    private double currentRatio = 1;

    public PersonDrawing(IFriezeView aFriezeView, Person aPerson) {
        super();
        person = aPerson;
        friezeView = aFriezeView;
        staysAndDrawings = new HashMap<>();
        //
        nameLabel = new Label(person.getName());
        nameSeparationLine = new Line();
        placesGroup = new Group();
        placesGroupClip = new Rectangle();
        placesGroup.setClip(placesGroupClip);
        addNode(nameLabel);
        addNode(nameSeparationLine);
        addNode(placesGroup);
        initLayout();
    }

    @Override
    public void setWidth(double w) {
        super.setWidth(w);
        placesGroupClip.setWidth(w - DEFAULT_NAME_WIDTH - DEFAULT_SEPARATION);
    }

    public Person getPerson() {
        return person;
    }

    private void initLayout() {
        double newHeight = DEFAULT_HEIGHT;
        newHeight = Math.max(DEFAULT_HEIGHT, newHeight);
        setWidth(DEFAULT_WIDTH);
        setHeight(newHeight);
        setBackgroundFill(Color.BLACK);
        //
        nameLabel.setTextFill(Color.WHITESMOKE);
        double labelMargin = 2.0;
        double labelWidth = DEFAULT_NAME_WIDTH - 2.0 * labelMargin;
        double labelHeight = getHeight() - 2.0 * labelMargin;
        nameLabel.setFont(new Font(14));
        nameLabel.setTranslateX(labelMargin);
        nameLabel.setTranslateY(labelMargin);
        nameLabel.setMinSize(labelWidth, labelHeight);
        nameLabel.setMaxSize(labelWidth, labelHeight);
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setTextFill(person.getColor());
        //
        nameSeparationLine.setStroke(Color.WHITESMOKE);
        nameSeparationLine.setStartX(DEFAULT_NAME_WIDTH);
        nameSeparationLine.setStartY(DEFAULT_SEPARATION);
        nameSeparationLine.setEndX(DEFAULT_NAME_WIDTH);
        nameSeparationLine.setEndY(getHeight() - DEFAULT_SEPARATION);
        //
        placesGroup.setTranslateX(DEFAULT_NAME_WIDTH + DEFAULT_SEPARATION);
        //
        friezeView.getFrieze().getStayPeriods(person).stream().forEachOrdered(this::addStay);
        //
        placesGroupClip.setWidth(getWidth() - DEFAULT_NAME_WIDTH - DEFAULT_SEPARATION);
        placesGroupClip.setHeight(newHeight);
        //
        person.addPropertyChangeListener(this::handlePersonEvents);
    }

    protected void updateDateRatio(long minDate, double ratio) {
        currentMinDate = minDate;
        currentRatio = ratio;
        staysAndDrawings.values().forEach(s -> s.updateDateRatio(currentMinDate, currentRatio));
    }

    protected void addStay(StayPeriod stay) {
        PlaceStayDrawing stayDrawing = new PlaceStayDrawing(stay);
        staysAndDrawings.put(stay, stayDrawing);
        stayDrawing.updateDateRatio(currentMinDate, currentRatio);
        placesGroup.getChildren().add(stayDrawing.getNode());
    }

    protected void updateStay(StayPeriod stay) {
        PlaceStayDrawing stayDrawing = staysAndDrawings.get(stay);
        if (stayDrawing != null) {
            stayDrawing.updateDateRatio(currentMinDate, currentRatio);
        }
    }

    protected void removeStay(StayPeriod stay) {
        PlaceStayDrawing stayDrawing = staysAndDrawings.remove(stay);
        if (stayDrawing != null) {
            placesGroup.getChildren().remove(stayDrawing.getNode());
        }
    }

    private void handlePersonEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Person.VISIBILITY_CHANGED -> {
                System.err.println(" TODO handle person visibility changed");
            }
            case Person.SELECTION_CHANGED ->
                System.err.println(" Person.SELECTION_CHANGED :: TODO");
            case Person.PICTURE_CHANGED ->
                System.err.println(" Person.PICTURE_CHANGED :: TODO");
            case Person.DATE_OF_BIRTH_CHANGED, Person.DATE_OF_DEATH_CHANGED -> {
                // nothin to do
            }
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

    @Override
    public String toString() {
        return person.getName();
    }

}
