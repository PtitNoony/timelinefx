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
package com.github.noony.app.timelinefx.core;

import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author hamon
 */
public class TimeLineProject {

    public static final String PERSON_ADDED = "personAdded";
    public static final String PLACE_ADDED = "placeAdded";
    public static final String HIGH_LEVEL_PLACE_ADDED = "highLevelPlaceAdded";
    public static final String STAY_ADDED = "stayAdded";
    public static final String PERSON_REMOVED = "personRemoved";
    public static final String PLACE_REMOVED = "placeRemoved";
    public static final String STAY_REMOVED = "stayRemoved";

    private final PropertyChangeSupport propertyChangeSupport;

    private final String name;

    private final List<Place> hightLevelPlaces;
    private final Map<String, Place> allPlaces;

    private final List<Person> persons;
    private final List<StayPeriod> stays;
    private final List<Frieze> friezes;
    private final List<PictureChronology> pictureChronologies;

    protected TimeLineProject(String projectName) {
        propertyChangeSupport = new PropertyChangeSupport(TimeLineProject.this);
        name = projectName;
        hightLevelPlaces = new LinkedList<>();
        allPlaces = new HashMap<>();
        persons = new LinkedList<>();
        stays = new LinkedList<>();
        friezes = new LinkedList<>();
        pictureChronologies = new LinkedList<>();
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }

    public boolean addPlace(Place aPlace) {
        if (aPlace == null) {
            return false;
        } else if (aPlace.isRootPlace()) {
            addHighLevelPlace(aPlace);
            return true;
        } else {
            addPlace(aPlace.getParent());
            if (!allPlaces.containsKey(aPlace.getName())) {
                allPlaces.put(aPlace.getName(), aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            }
            return true;
        }
    }

    public boolean addHighLevelPlace(Place aPlace) {
        if (!hightLevelPlaces.contains(aPlace)) {
            hightLevelPlaces.add(aPlace);
            hightLevelPlaces.sort(Place.COMPARATOR);
            if (!allPlaces.containsKey(aPlace.getName())) {
                allPlaces.put(aPlace.getName(), aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            }
            propertyChangeSupport.firePropertyChange(HIGH_LEVEL_PLACE_ADDED, this, aPlace);
            return true;
        }
        return false;
    }

    public boolean removeHighLevelPlace(Place aPlace) {
        // TODO fire
        return hightLevelPlaces.remove(aPlace);
    }

    public List<Place> getHightLevelPlaces() {
        return Collections.unmodifiableList(hightLevelPlaces);
    }

    public Place getPlaceByName(String placeName) {
        return allPlaces.get(placeName);
    }

    public void addStay(StayPeriod aStay) {
        if (!stays.contains(aStay)) {
            stays.add(aStay);
            propertyChangeSupport.firePropertyChange(STAY_ADDED, this, aStay);
        }
    }

    public void removeStay(StayPeriod aStay) {
        if (stays.contains(aStay)) {
            stays.remove(aStay);
            propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, aStay);
        }
    }

    public List<StayPeriod> getStays() {
        return Collections.unmodifiableList(stays);
    }

    protected boolean addFrieze(Frieze frieze) {
        if (!friezes.contains(frieze)) {
            frieze.addListener(this::handleFriezeChange);
            friezes.add(frieze);
            frieze.getPersons().stream().filter(p -> !persons.contains(p)).forEach(persons::add);
            frieze.getPlaces().stream().forEachOrdered(this::addPlace);
            frieze.getStayPeriods().stream().forEachOrdered(this::addStay);
            return true;
        }
        return false;
    }

    public boolean addPictureChronology(PictureChronology pictureChronology) {
        if (!pictureChronologies.contains(pictureChronology)) {
            pictureChronology.addListener(this::handlePicturesChronologyChange);
            pictureChronologies.add(pictureChronology);
            return true;
        }
        return false;
    }

    public List<Frieze> getFriezes() {
        return Collections.unmodifiableList(friezes);
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public List<PictureChronology> getPictureChonologies() {
        return Collections.unmodifiableList(pictureChronologies);
    }

    /**
     * NOTE: this method may take time with larger projects.
     *
     * @return all the places present in the project.
     */
    public List<Place> getAllPlaces() {
        return allPlaces.values().stream().sorted(Place.COMPARATOR).collect(Collectors.toList());
    }

    public boolean addPerson(Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            persons.sort(Person.COMPARATOR);
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            return true;
        }
        return false;
    }

    private void handleFriezeChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.PLACE_ADDED ->
                addPlace((Place) event.getNewValue());
            case Frieze.PERSON_ADDED ->
                addPerson((Person) event.getNewValue());
            case Frieze.STAY_ADDED ->
                addStay((StayPeriod) event.getNewValue());
            case Frieze.DATE_WINDOW_CHANGED -> {// ignoring
            }
            case Frieze.STAY_REMOVED -> {// ignored since removal from one Freize does not mean deleted
            }
            case Frieze.PERSON_REMOVED -> {// ignoring
            }
            case Frieze.PLACE_REMOVED -> {// ignoring
            }
            case Frieze.NAME_CHANGED -> {// ignoring
            }
            case Frieze.STAY_UPDATED -> {// ignoring
            }
            case Frieze.START_DATE_ADDED,Frieze.START_DATE_REMOVED -> {// ignoring
            }
            case Frieze.END_DATE_ADDED,Frieze.END_DATE_REMOVED -> {// ignoring
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    private void handlePicturesChronologyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronology.PICTURE_ADDED -> {
                // nothing to do
            }
            case PictureChronology.NAME_CHANGED -> {
                // nothing to do
            }
            case PictureChronology.LAYOUT_CHANGED -> {
                // nothing to do
            }
            case PictureChronology.LINK_ADDED , PictureChronology.LINK_REMOVED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    public void removePlace(Place deletedPlace) {
        if (allPlaces.containsKey(deletedPlace.getName())) {
            allPlaces.remove(deletedPlace.getName());
        }
        hightLevelPlaces.remove(deletedPlace);
        //
        if (deletedPlace.getParent() != null) {
            deletedPlace.getParent().removePlace(deletedPlace);
        }
        //
        removeStaysAt(deletedPlace);
        removeChildrenPlaces(deletedPlace);
        //
        propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, deletedPlace);
    }

    public void removePerson(Person deletedPerson) {
        if (persons.contains(deletedPerson)) {
            persons.remove(deletedPerson);
            List<StayPeriod> staysToRemove = stays.stream().filter(s -> s.getPerson() == deletedPerson).collect(Collectors.toList());
            staysToRemove.forEach(this::removeStay);
            //
            propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, deletedPerson);
        }
    }

    private void removeChildrenPlaces(Place aParentPlace) {
        List<Place> directChildren = allPlaces.entrySet().stream()
                .filter(entry -> (entry.getValue().getParent().equals(aParentPlace)))
                .map(entry -> entry.getValue()).collect(Collectors.toList());
        directChildren.forEach(child -> {
            allPlaces.remove(child.getName());
            removeStaysAt(child);
        });
        directChildren.forEach(this::removeChildrenPlaces);
    }

    private void removeStaysAt(Place aPlace) {
        List<StayPeriod> staysToRemove = stays.stream().filter(s -> s.getPlace() == aPlace).collect(Collectors.toList());
        staysToRemove.forEach(this::removeStay);
    }

}
