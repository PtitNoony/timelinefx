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
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;

/**
 *
 * @author hamon
 */
public class Frieze extends FriezeObject {

    public static final String CLASS_NAME = "Frieze";
    public static final String DATE_WINDOW_CHANGED = CLASS_NAME + "__dateWindowChanged";
    public static final String NAME_CHANGED = CLASS_NAME + "__nameChanged";
    public static final String STAY_ADDED = CLASS_NAME + "__stayAdded";
    public static final String STAY_REMOVED = CLASS_NAME + "__stayRemoved";
    public static final String PERSON_ADDED = CLASS_NAME + "__personAdded";
    public static final String PLACE_ADDED = CLASS_NAME + "__placeAdded";
    public static final String PERSON_REMOVED = CLASS_NAME + "__personRemoved";
    public static final String PLACE_REMOVED = CLASS_NAME + "__placeRemoved";
    // TODO : merge with other use
    private static final long DEFAULT_MIN_DATE = 0;
    private static final long DEFAULT_MAX_DATE = 500;

    private final TimeLineProject project;
    private final List<StayPeriod> stayPeriods;
    private final List<Place> places;
    private final List<Person> persons;
    private final Map<Place, List<Person>> personsAtPlaces;
    private final PropertyChangeSupport propertyChangeSupport;
    private final List<FriezeFreeMap> friezeFreeMaps;
    //
    private final List<Long> dates;
    private final List<Long> startDates;
    private final List<Long> endDates;
    //
    private String name;
    //
    private long minDate = DEFAULT_MIN_DATE;
    private long maxDate = DEFAULT_MAX_DATE;
    //
    private long minDateWindow = minDate;
    private long maxDateWindow = maxDate;

    protected Frieze(long anID, TimeLineProject aProject, String friezeName, List<StayPeriod> staysToConsider) {
        super(anID);
        project = aProject;
        name = friezeName;
        stayPeriods = new LinkedList<>();
        places = new LinkedList<>();
        persons = new LinkedList<>();
        friezeFreeMaps = new LinkedList<>();
        personsAtPlaces = new HashMap<>();
        //
        dates = new LinkedList<>();
        startDates = new LinkedList<>();
        endDates = new LinkedList<>();
        //
        propertyChangeSupport = new PropertyChangeSupport(Frieze.this);
        //
        project.addFrieze(Frieze.this);
        project.addListener(Frieze.this::handleTimeLineProjectChanges);
        // TODO : optimize
        staysToConsider.stream().forEachOrdered(Frieze.this::addStayPeriod);
    }

    public Frieze(long anID, TimeLineProject aProject, String friezeName) {
        this(anID, aProject, friezeName, Collections.EMPTY_LIST);
    }

    public void setName(String aName) {
        name = aName;
        propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
    }

    public String getName() {
        return name;
    }

    public TimeLineProject getProject() {
        return project;
    }

    public void addPerson(Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            var stays = project.getStays().stream().filter(s -> s.getPerson() == aPerson).collect(Collectors.toList());
            var tmpPlaces = stays.stream().map(s -> s.getPlace()).distinct().collect(Collectors.toList());
            tmpPlaces.forEach(place -> {
                var tempPersons = personsAtPlaces.get(place);
                if (tempPersons == null) {
                    tempPersons = new LinkedList<>();
                    personsAtPlaces.put(place, tempPersons);
                }
                tempPersons.add(aPerson);
            });
            // notify place added
            // may be needed before adding stays for some variable updates
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            runLater(() -> stays.forEach(this::addStayPeriod));
        }
    }

    public void addStayPeriod(StayPeriod stay) {
        if (!stayPeriods.contains(stay)) {
            stayPeriods.add(stay);
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
            // handle dates
            if (!dates.contains(stay.getStartDate())) {
                dates.add(stay.getStartDate());
            }
            if (!dates.contains(stay.getEndDate())) {
                dates.add(stay.getEndDate());
            }
            if (!startDates.contains(stay.getStartDate())) {
                startDates.add(stay.getStartDate());
            }
            if (!endDates.contains(stay.getEndDate())) {
                endDates.add(stay.getEndDate());
            }
            minDate = stayPeriods.stream().mapToLong(StayPeriod::getStartDate).min().orElse(DEFAULT_MIN_DATE);
            maxDate = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
            //
            propertyChangeSupport.firePropertyChange(STAY_ADDED, this, stay);
        }
    }

    public void removeStay(StayPeriod stay) {
        if (stayPeriods.contains(stay)) {
            stayPeriods.remove(stay);
            //TODO : check if person list and place list is unchanged
            //
            minDate = stayPeriods.stream().mapToLong(StayPeriod::getStartDate).min().orElse(DEFAULT_MIN_DATE);
            maxDate = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
            //
            var startDate = stay.getStartDate();
            var endDate = stay.getEndDate();
            var removeStart = stayPeriods.stream().mapToLong(StayPeriod::getStartDate).noneMatch(d -> d == startDate);
            var removeEnd = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).noneMatch(d -> d == endDate);
            maxDate = stayPeriods.stream().mapToLong(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
            if (removeStart) {
                startDates.remove(startDate);
                // TODO fire ?
            }
            if (removeEnd) {
                endDates.remove(endDate);
                // TODO fire ?
            }
            if (stayPeriods.stream().mapToLong(StayPeriod::getStartDate).noneMatch(d -> d == startDate)
                    && stayPeriods.stream().mapToLong(StayPeriod::getEndDate).noneMatch(d -> d == startDate)) {
                dates.remove(startDate);
            }
            if (stayPeriods.stream().mapToLong(StayPeriod::getStartDate).noneMatch(d -> d == endDate)
                    && stayPeriods.stream().mapToLong(StayPeriod::getEndDate).noneMatch(d -> d == endDate)) {
                dates.remove(endDate);
            }
            //
            propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, stay);
        }
    }

    public void updatePlaceSelection(Place aPlace, boolean selected) {
        if (selected) {
            if (!places.contains(aPlace)) {
                places.add(aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
            }
            project.getStays().stream().filter(s -> s.getPlace() == aPlace & persons.contains(s.getPerson())).forEach(this::addStayPeriod);
        } else {
            removePlace(aPlace);
        }
    }

    public void updatePersonSelection(Person aPerson, boolean selected) {
        if (selected) {
            addPerson(aPerson);
        } else {
            removePerson(aPerson);
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

    public List<StayPeriod> getStayPeriods(Place aPlace) {
        return stayPeriods.stream().filter(s -> s.getPlace().equals(aPlace)).collect(Collectors.toList());
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
        var friezeFreeMap = FriezeFreeMapFactory.createFriezeFreeMap(this);
        friezeFreeMaps.add(friezeFreeMap);
        return friezeFreeMap;
    }

    public void removeFriezeFreeMap(FriezeFreeMap aFriezeFreeMap) {
        friezeFreeMaps.remove(aFriezeFreeMap);
    }

    public List<FriezeFreeMap> getFriezeFreeMaps() {
        return Collections.unmodifiableList(friezeFreeMaps);
    }

    public List<Long> getDates() {
        return Collections.unmodifiableList(dates);
    }

    public List<Long> getStartDates() {
        return Collections.unmodifiableList(startDates);
    }

    public List<Long> getEndDates() {
        return Collections.unmodifiableList(endDates);
    }

    public long getMinDate() {
        return minDate;
    }

    public long getMaxDate() {
        return maxDate;
    }

    public int getNbDates() {
        return dates.size();
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
            case TimeLineProject.HIGH_LEVEL_PLACE_ADDED,  TimeLineProject.PLACE_ADDED -> {
                // Nothing to do
            }
            case TimeLineProject.PERSON_ADDED ->
                addPerson((Person) event.getNewValue());
            case TimeLineProject.STAY_ADDED ->
                addStayPeriod((StayPeriod) event.getNewValue());
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
