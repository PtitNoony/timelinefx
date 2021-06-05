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

import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
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
public class Frieze {

    public static final String CLASS_NAME = "Frieze";
    public static final String DATE_WINDOW_CHANGED = CLASS_NAME + "__dateWindowChanged";
    public static final String STAY_ADDED = CLASS_NAME + "__stayAdded";
    public static final String STAY_REMOVED = CLASS_NAME + "__stayRemoved";
    public static final String PERSON_ADDED = CLASS_NAME + "__personAdded";
    public static final String PLACE_ADDED = CLASS_NAME + "__placeAdded";
    public static final String PERSON_REMOVED = CLASS_NAME + "__personRemoved";
    public static final String PLACE_REMOVED = CLASS_NAME + "__placeRemoved";

    private final TimeLineProject project;
    private final String name;
    private final List<StayPeriod> stayPeriods;
    private final List<Place> places;
    private final List<Person> persons;
    private final Map<Place, List<Person>> personsAtPlaces;
    private final PropertyChangeSupport propertyChangeSupport;
    private final List<FriezeFreeMap> friezeFreeMaps;

    //
    private long minDate = Long.MAX_VALUE;
    private long maxDate = Long.MIN_VALUE;
    //
    private long minDateWindow = minDate;
    private long maxDateWindow = maxDate;

    public Frieze(TimeLineProject aProject, String friezeName) {
//        TODO use factory
        project = aProject;
        name = friezeName;
        stayPeriods = new LinkedList<>();
        places = new LinkedList<>();
        persons = new LinkedList<>();
        friezeFreeMaps = new LinkedList<>();
        personsAtPlaces = new HashMap<>();
        propertyChangeSupport = new PropertyChangeSupport(Frieze.this);
        //
        project.addFrieze(Frieze.this);
        project.addListener(Frieze.this::handleTimeLineProjectChanges);
    }

    public String getName() {
        return name;
    }

    public TimeLineProject getProject() {
        return project;
    }

    public void addStayPeriod(StayPeriod stay) {
        if (!stayPeriods.contains(stay)) {
            stayPeriods.add(stay);
            minDate = Math.min(minDate, stay.getStartDate());
            maxDate = Math.max(maxDate, stay.getEndDate());
            propertyChangeSupport.firePropertyChange(STAY_ADDED, this, stay);
            // Should this code be in the TimeLineProject Class ??
            Place place = stay.getPlace();
            Person person = stay.getPerson();
            // add place
            if (!places.contains(place)) {
                places.add(place);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, place);
            }
            // add person
            if (!persons.contains(person)) {
                persons.add(person);
                propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, person);
            }
            // add person at place
            if (!personsAtPlaces.containsKey(stay.getPlace())) {
                personsAtPlaces.put(stay.getPlace(), new LinkedList<>());
                personsAtPlaces.get(stay.getPlace()).add(stay.getPerson());
            } else if (!personsAtPlaces.get(stay.getPlace()).contains(stay.getPerson())) {
                personsAtPlaces.get(stay.getPlace()).add(stay.getPerson());
            }
        }
    }

    public void removeStay(StayPeriod stay) {
        if (stayPeriods.contains(stay)) {
            stayPeriods.remove(stay);
            //TODO : check if person list and place list is unchanged
            //
            minDate = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).min().orElse(0);
            maxDate = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).max().orElse(0);
            //
            propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, stay);
        }
    }

    public List<Person> getPersonsAtPlace(Place p) {
        if (personsAtPlaces.containsKey(p)) {
            return Collections.unmodifiableList(personsAtPlaces.get(p));
        }
        return Collections.EMPTY_LIST;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public List<StayPeriod> getStayPeriods() {
        return Collections.unmodifiableList(stayPeriods);
    }

    public List<StayPeriod> getStayPeriods(Person person) {
        return stayPeriods.stream().filter(s -> s.getPerson().equals(person)).collect(Collectors.toList());
    }

    public int getStayIndex(StayPeriod stayPeriod) {
        return persons.indexOf(stayPeriod.getPerson());
    }

    public int getNbStays() {
        return stayPeriods.size();
    }

    public int getNbPersons() {
        return persons.size();
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public FriezeFreeMap createFriezeFreeMap() {
        var friezeFreeMap = new FriezeFreeMap(this);
        friezeFreeMaps.add(friezeFreeMap);
        return friezeFreeMap;
    }

    public List<FriezeFreeMap> getFriezeFreeMaps() {
        return Collections.unmodifiableList(friezeFreeMaps);
    }

    public long getMinDate() {
        return minDate;
    }

    public long getMaxDate() {
        return maxDate;
    }

    public long getMinDateWindow() {
        return minDateWindow;
    }

    public long getMaxDateWindow() {
        return maxDateWindow;
    }

    public void setMinDateWindow(long newMinDateWindow) {
        minDateWindow = newMinDateWindow;
        propertyChangeSupport.firePropertyChange(DATE_WINDOW_CHANGED, minDateWindow, maxDateWindow);
    }

    public void setMaxDateWindow(long newMaxDateWindow) {
        maxDateWindow = newMaxDateWindow;
        propertyChangeSupport.firePropertyChange(DATE_WINDOW_CHANGED, minDateWindow, maxDateWindow);
    }

    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

    public TimeFormat getTimeFormat() {
        return stayPeriods.isEmpty() ? TimeFormat.TIME_MIN : stayPeriods.get(0).getTimeFormat();
    }

    private void handleTimeLineProjectChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.HIGH_LEVEL_PLACE_ADDED, TimeLineProject.PERSON_ADDED, TimeLineProject.PLACE_ADDED, TimeLineProject.STAY_ADDED -> {
            }
            case TimeLineProject.STAY_REMOVED ->
                removeStay((StayPeriod) event.getNewValue());
            case TimeLineProject.PLACE_REMOVED -> {
                Place placeRemoved = (Place) event.getNewValue();
                removePlace(placeRemoved);
            }
            case TimeLineProject.PERSON_REMOVED -> {
                Person personRemoved = (Person) event.getNewValue();
                removePerson(personRemoved);
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
        // ignore
    }

    private void removePlace(Place placeRemoved) {
        places.remove(placeRemoved);
        personsAtPlaces.remove(placeRemoved);
        List<StayPeriod> impactedStays = stayPeriods.stream().filter(s -> s.getPlace() == placeRemoved).collect(Collectors.toList());
        impactedStays.forEach(this::removeStay);
        propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, placeRemoved);
        //
        placeRemoved.getPlaces().forEach(this::removePlace);
    }

    private void removePerson(Person personRemoved) {
        persons.remove(personRemoved);
        personsAtPlaces.forEach((place, list) -> list.remove(personRemoved));
        List<StayPeriod> impactedStays = stayPeriods.stream().filter(s -> s.getPerson() == personRemoved).collect(Collectors.toList());
        impactedStays.forEach(this::removeStay);
        propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, personRemoved);
    }
}
