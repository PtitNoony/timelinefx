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
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.drawings.FXDrawing;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author hamon
 */
public final class PlaceDrawing extends FXDrawing {

    public static final double DEFAULT_HEIGHT = 20;
    public static final double DEFAULT_WIDTH = 500;

    public static final double DEFAULT_SEPARATION = 12.0;

    public static final double DEFAULT_NAME_WIDTH = 100;

    private final Place place;
    private final Frieze frieze;
    //
    private final List<Person> visiblePersons;
    private final Map<StayPeriod, StayDrawing> staysAndDrawings;
    private final Map<Person, List<StayDrawing>> personsAndDrawings;
    //
    private final Label nameLabel;
    private final Line nameSeparationLine;
    private final Group stayGroup;
    private final Rectangle stayGroupClip;
    //
    private long currentMinDate = 0L;
    private double currentRatio = 1;

    public PlaceDrawing(Place aPlace, Frieze aFrieze) {
        super();
        visiblePersons = new LinkedList<>();
        staysAndDrawings = new HashMap<>();
        personsAndDrawings = new HashMap<>();
        stayGroupClip = new Rectangle();
        place = aPlace;
        frieze = aFrieze;
        nameLabel = new Label(place.getName());
        nameSeparationLine = new Line();
        stayGroup = new Group();
        addNode(nameLabel);
        addNode(nameSeparationLine);
        addNode(stayGroup);
        stayGroup.setClip(stayGroupClip);
        setWidth(DEFAULT_WIDTH);
        initLayout();
    }

    @Override
    public void setWidth(double w) {
        super.setWidth(w);
        stayGroupClip.setWidth(w - DEFAULT_NAME_WIDTH - DEFAULT_SEPARATION);
    }

    public Place getPlace() {
        return place;
    }

    private void initLayout() {
        List<StayPeriod> stayPeriods = frieze.getStayPeriods().stream()
                .filter(s -> s.getPlace().equals(place))
                .collect(Collectors.toList());
        List<Person> persons = stayPeriods.stream()
                .map(StayPeriod::getPerson).distinct()
                .collect(Collectors.toList());
        visiblePersons.addAll(persons.stream().filter(Person::isVisible).sorted(Person.COMPARATOR).collect(Collectors.toList()));
        //
        persons.forEach(p -> {
            p.addPropertyChangeListener(this::handlePersonChange);
            personsAndDrawings.put(p, new LinkedList<>());
        });
        //
        stayPeriods.forEach(this::addStayNoUpdate);
        //
        setWidth(DEFAULT_WIDTH);
        setBackgroundFill(Color.BLACK);
        updateLayout();
    }

    protected void addStay(StayPeriod stayAdded) {
        addStayNoUpdate(stayAdded);
        updateLayout();
    }

    protected void updateStay(StayPeriod stayUpdated) {
        StayDrawing stayUpdatedDrawing = staysAndDrawings.get(stayUpdated);
        if (stayUpdatedDrawing != null) {
            stayUpdatedDrawing.updateDateRatio(currentMinDate, currentRatio);
            updateLayout();
        }
    }

    protected void removeStay(StayPeriod stayRemoved) {
        StayDrawing stayRemovedDrawing = staysAndDrawings.get(stayRemoved);
        if (stayRemovedDrawing != null) {
            stayGroup.getChildren().remove(stayRemovedDrawing.getLine());
            staysAndDrawings.remove(stayRemoved);
            Person person = stayRemoved.getPerson();
            var associatedDrawings = personsAndDrawings.get(person);
            if (associatedDrawings != null) {
                // redundant check ?
                associatedDrawings.remove(stayRemovedDrawing);
                if (associatedDrawings.isEmpty()) {
                    personsAndDrawings.remove(person);
                    visiblePersons.remove(person);
                }
            }
            updateLayout();
        }
    }

    protected void updateDateRatio(long minDate, double ratio) {
        currentMinDate = minDate;
        currentRatio = ratio;
        staysAndDrawings.values().forEach(s -> s.updateDateRatio(currentMinDate, currentRatio));
    }

    private void handlePersonChange(PropertyChangeEvent event) {
        // Todo manage order
        switch (event.getPropertyName()) {
            case Person.VISIBILITY_CHANGED -> {
                Person p = (Person) event.getOldValue();
                List<StayDrawing> drawings = personsAndDrawings.get(p);
                List<Node> nodes = drawings.stream().map(StayDrawing::getLine).collect(Collectors.toList());
                if (p.isVisible()) {
                    visiblePersons.add(p);
                    stayGroup.getChildren().addAll(nodes);
                } else {
                    visiblePersons.remove(p);
                    stayGroup.getChildren().removeAll(nodes);
                }
                runLater(() -> updateLayout());
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

    private void addStayNoUpdate(StayPeriod stay) {
        if (staysAndDrawings.get(stay) == null) {
            StayDrawing stayDrawing = new StayDrawing(stay);
            stayDrawing.updateDateRatio(currentMinDate, currentRatio);
            staysAndDrawings.put(stay, stayDrawing);
            var stayPerson = stay.getPerson();
            var staysForPerson = personsAndDrawings.get(stayPerson);
            if (staysForPerson == null) {
                staysForPerson = new LinkedList<>();
                personsAndDrawings.put(stay.getPerson(), staysForPerson);
            }
            staysForPerson.add(stayDrawing);
            if (stay.getPerson().isVisible()) {
                stayGroup.getChildren().add(stayDrawing.getLine());
                if (!visiblePersons.contains(stay.getPerson())) {
                    visiblePersons.add(stay.getPerson());
                }
            }
        }
    }

    private void updateLayout() {
        visiblePersons.sort(Person.COMPARATOR);
        //
        double newHeight = DEFAULT_SEPARATION + visiblePersons.size() * (StayDrawing.DEFAULT_SEPARATION + StayDrawing.STROKE_WIDTH);
        newHeight = Math.max(DEFAULT_HEIGHT, newHeight);
        setHeight(newHeight);
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
        //
        nameSeparationLine.setStroke(Color.WHITESMOKE);
        nameSeparationLine.setStartX(DEFAULT_NAME_WIDTH);
        nameSeparationLine.setStartY(DEFAULT_SEPARATION);
        nameSeparationLine.setEndX(DEFAULT_NAME_WIDTH);
        nameSeparationLine.setEndY(getHeight() - DEFAULT_SEPARATION);
        //
        stayGroup.setTranslateX(DEFAULT_NAME_WIDTH + DEFAULT_SEPARATION);
        stayGroupClip.setWidth(getWidth() - DEFAULT_NAME_WIDTH - DEFAULT_SEPARATION);
        stayGroupClip.setHeight(newHeight);
        //
        staysAndDrawings.forEach((s, sD) -> {
            int index = visiblePersons.indexOf(s.getPerson());
            double y = StayDrawing.DEFAULT_SEPARATION + index * (StayDrawing.STROKE_WIDTH + StayDrawing.DEFAULT_SEPARATION);
            sD.setY(y);
        });
    }

}
