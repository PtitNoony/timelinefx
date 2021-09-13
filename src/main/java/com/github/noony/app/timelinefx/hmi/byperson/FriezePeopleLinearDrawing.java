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

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.drawings.FriezeView;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static javafx.application.Platform.runLater;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class FriezePeopleLinearDrawing implements FriezeView {

    private final Frieze frieze;

    private final Group mainNode;
    private final Group personsGroup;
    private final Group stayGroup;
    private final Rectangle stayGroupClip;

    private final Rectangle background;

    private final Map<Person, PersonDrawing> personsAndDrawings;
    private final List<Person> visiblePersons;
    //
    private double width = 800;
    private double height = 600;
    private double placeWith;
    // temp since no zoom
    private double timeWindowWidth;
    private double timeWindowX;

    private double previewsPersonsHeight = 0;

    public FriezePeopleLinearDrawing(Frieze aFrieze) {
        frieze = aFrieze;
        frieze.addListener(FriezePeopleLinearDrawing.this::handleFriezeChange);
        //
        personsAndDrawings = new HashMap<>();
        visiblePersons = new LinkedList<>();
        //
        mainNode = new Group();
        personsGroup = new Group();
        stayGroup = new Group();
        background = new Rectangle();
        background.setFill(Color.DARKGREY);
        stayGroupClip = new Rectangle();
        stayGroup.setClip(stayGroupClip);

        mainNode.getChildren().addAll(background, personsGroup, stayGroup);
        //
        frieze.getPersons().stream().forEachOrdered(p -> addPersonDrawing(p));
        //
        runLater(() -> {
            setWidth(width);
            setHeight(height);
        });
    }

    private void addPersonDrawing(final Person person) {
        PersonDrawing personDrawing = new PersonDrawing(this, person);
        person.addPropertyChangeListener(this::handlePersonChange);
        if (person.isVisible()) {
            visiblePersons.add(person);
            personsGroup.getChildren().add(personDrawing.getNode());
            personDrawing.setX(PersonDrawing.DEFAULT_SEPARATION);
            personDrawing.setY(PersonDrawing.DEFAULT_SEPARATION + (visiblePersons.size()) * (PersonDrawing.DEFAULT_HEIGHT + PersonDrawing.DEFAULT_SEPARATION));
        }
        personsAndDrawings.put(person, personDrawing);
        updateStaysWidth();
        updateStaysHeight();
        setWidth(width);
        setHeight(height);
    }

    private void removePersonDrawing(final Person person) {
        visiblePersons.remove(person);
        PersonDrawing personDrawing = personsAndDrawings.get(person);
        person.removePropertyChangeListener(this::handlePersonChange);
        if (personDrawing != null) {
            if (person.isVisible()) {
                personsGroup.getChildren().remove(personDrawing.getNode());
            }
            personsAndDrawings.remove(person);
        }
        updateStaysWidth();
        updateStaysHeight();
        setWidth(width);
        setHeight(height);
    }

    public Node getNode() {
        return mainNode;
    }

    public final void setWidth(double w) {
        width = w;
        placeWith = width - 2 * PersonDrawing.DEFAULT_SEPARATION;
        timeWindowX = 2 * PersonDrawing.DEFAULT_SEPARATION + PersonDrawing.DEFAULT_NAME_WIDTH;
        timeWindowWidth = placeWith - 2 * PersonDrawing.DEFAULT_SEPARATION - PersonDrawing.DEFAULT_NAME_WIDTH;
        background.setWidth(width);
        personsAndDrawings.values().forEach(d -> d.setWidth(placeWith));
        stayGroup.setTranslateX(timeWindowX);
        //
        stayGroupClip.setWidth(timeWindowWidth);
        //
        updateStaysWidth();
    }

    public final void setHeight(double h) {
        height = h;
        stayGroupClip.setHeight(previewsPersonsHeight + 2 * PersonDrawing.DEFAULT_SEPARATION);
        updateStaysHeight();
    }

    @Override
    public void setFrieze(Frieze f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Frieze getFrieze() {
        return frieze;
    }

    private void updateStaysWidth() {
        // simple for now
        double ratio = timeWindowWidth / (frieze.getMaxDateWindow() - frieze.getMinDateWindow());
        personsAndDrawings.values().forEach(personDrawing -> personDrawing.updateDateRatio(frieze.getMinDateWindow(), ratio));
    }

    private void updateStaysHeight() {
        previewsPersonsHeight = PersonDrawing.DEFAULT_SEPARATION;
        //
        visiblePersons.sort(Person.COMPARATOR);
        for (int i = 0; i < visiblePersons.size(); i++) {
            PersonDrawing drawing = personsAndDrawings.get(visiblePersons.get(i));
            drawing.setY(PersonDrawing.DEFAULT_SEPARATION + i * (PersonDrawing.DEFAULT_HEIGHT + PersonDrawing.DEFAULT_SEPARATION));
        }
    }

    private void addStayPeriod(StayPeriod aStayPeriod) {
        PersonDrawing personDrawing = personsAndDrawings.get(aStayPeriod.getPerson());
        if (personDrawing != null) {
            personDrawing.addStay(aStayPeriod);
        }
    }

    private void updateStayPeriod(StayPeriod aStayPeriod) {
        PersonDrawing personDrawing = personsAndDrawings.get(aStayPeriod.getPerson());
        if (personDrawing != null) {
            personDrawing.updateStay(aStayPeriod);
        }
    }

    private void removeStayPeriod(StayPeriod aStayPeriod) {
        PersonDrawing personDrawing = personsAndDrawings.get(aStayPeriod.getPerson());
        if (personDrawing != null) {
            personDrawing.removeStay(aStayPeriod);
        }
    }

    private void handleFriezeChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.DATE_WINDOW_CHANGED ->
                updateStaysWidth();
            case Frieze.STAY_ADDED -> {
                StayPeriod stay = (StayPeriod) event.getNewValue();
                addStayPeriod(stay);
            }
            case Frieze.PERSON_ADDED ->
                addPersonDrawing((Person) event.getNewValue());
            case Frieze.PLACE_ADDED,Frieze.PLACE_REMOVED -> {
                // nothing to do, frieze handles stay add/remove
            }
            case Frieze.STAY_REMOVED -> {
                StayPeriod stayToRemove = (StayPeriod) event.getNewValue();
                removeStayPeriod(stayToRemove);
            }
            case Frieze.PERSON_REMOVED ->
                removePersonDrawing((Person) event.getNewValue());
            case Frieze.NAME_CHANGED -> {
                // Nothing to do
            }
            case Frieze.STAY_UPDATED -> {
                var stay = (StayPeriod) event.getNewValue();
                updateStayPeriod(stay);
            }
            case Frieze.START_DATE_ADDED,Frieze.START_DATE_REMOVED -> {// ignore : taken care of in STAY_UPDATED
            }
            case Frieze.END_DATE_ADDED,Frieze.END_DATE_REMOVED -> {// ignore : taken care of in STAY_UPDATED
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event.getPropertyName());
        }
    }

    private void handlePersonChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Person.VISIBILITY_CHANGED -> {
                Person p = (Person) event.getOldValue();
                PersonDrawing personDrawing = personsAndDrawings.get(p);
                if (p.isVisible()) {
                    visiblePersons.add(p);
                    personsGroup.getChildren().add(personDrawing.getNode());
                } else {
                    visiblePersons.remove(p);
                    personsGroup.getChildren().remove(personDrawing.getNode());
                }
                runLater(() -> updateStaysHeight());
            }
            case Person.SELECTION_CHANGED ->
                System.err.println(" Person.SELECTION_CHANGED :: TODO in FriezePeopleLinearDrawing");
            case Person.PICTURE_CHANGED ->
                System.err.println(" Person.PICTURE_CHANGED :: TODO in FriezePeopleLinearDrawing");
            default ->
                throw new UnsupportedOperationException(event.getPropertyName());
        }
    }

}
