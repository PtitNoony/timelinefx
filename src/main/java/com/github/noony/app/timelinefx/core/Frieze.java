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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;

/**
 * A subset of a project's places, persons and stays, laid out over a shared time window.
 *
 * @author hamon
 */
public final class Frieze implements FriezeObject {

    /**
     * Prefix used to namespace this class's property change event names.
     */
    public static final String CLASS_NAME = "Frieze";

    /**
     * Name of the property change event fired when the visible date window changes.
     */
    public static final String DATE_WINDOW_CHANGED = CLASS_NAME + "__dateWindowChanged";

    /**
     * Name of the property change event fired when this frieze's name changes.
     */
    public static final String NAME_CHANGED = CLASS_NAME + "__nameChanged";

    /**
     * Name of the property change event fired when a stay is added.
     */
    public static final String STAY_ADDED = CLASS_NAME + "__stayAdded";

    /**
     * Name of the property change event fired when a stay is removed.
     */
    public static final String STAY_REMOVED = CLASS_NAME + "__stayRemoved";

    /**
     * Name of the property change event fired when a stay's dates change.
     */
    public static final String STAY_UPDATED = CLASS_NAME + "__stayUpdated";

    /**
     * Name of the property change event fired when a person is added.
     */
    public static final String PERSON_ADDED = CLASS_NAME + "__personAdded";

    /**
     * Name of the property change event fired when a place is added.
     */
    public static final String PLACE_ADDED = CLASS_NAME + "__placeAdded";

    /**
     * Name of the property change event fired when a person is removed.
     */
    public static final String PERSON_REMOVED = CLASS_NAME + "__personRemoved";

    /**
     * Name of the property change event fired when a place is removed.
     */
    public static final String PLACE_REMOVED = CLASS_NAME + "__placeRemoved";

    /**
     * Name of the property change event fired when a start date is added.
     */
    public static final String START_DATE_ADDED = CLASS_NAME + "__startDateAdded";

    /**
     * Name of the property change event fired when a start date is removed.
     */
    public static final String START_DATE_REMOVED = CLASS_NAME + "__startDateRemoved";

    /**
     * Name of the property change event fired when an end date is added.
     */
    public static final String END_DATE_ADDED = CLASS_NAME + "__endDateAdded";

    /**
     * Name of the property change event fired when an end date is removed.
     */
    public static final String END_DATE_REMOVED = CLASS_NAME + "__endDateRemoved";
    // TODO : merge with other use

    /**
     * Fallback minimum date used when the frieze has no stays.
     */
    private static final long DEFAULT_MIN_DATE = 0;

    /**
     * Fallback maximum date used when the frieze has no stays.
     */
    private static final long DEFAULT_MAX_DATE = 500;

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * This frieze's unique id.
     */
    private final Long id;

    /**
     * The project this frieze belongs to.
     */
    private final TimeLineProject project;

    /**
     * The stays represented in this frieze.
     */
    private final List<StayPeriod> stayPeriods;

    /**
     * The places represented in this frieze.
     */
    private final List<Place> places;

    /**
     * The persons represented in this frieze.
     */
    private final List<Person> persons;

    /**
     * The persons present at each place.
     */
    private final Map<Place, List<Person>> personsAtPlaces;

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * The free maps built from this frieze.
     */
    private final List<FriezeFreeMap> friezeFreeMaps;
    //

    /**
     * All the start/end dates represented in this frieze.
     */
    private final List<Double> dates;

    /**
     * The start dates represented in this frieze.
     */
    private final List<Double> startDates;

    /**
     * The end dates represented in this frieze.
     */
    private final List<Double> endDates;
    //

    /**
     * Listener attached to every stay in this frieze, to react to date changes.
     */
    private final PropertyChangeListener stayChangesListener;
    //

    /**
     * This frieze's display name.
     */
    private String name;
    //

    /**
     * The earliest date across this frieze's stays.
     */
    private double minDate = DEFAULT_MIN_DATE;

    /**
     * The latest date across this frieze's stays.
     */
    private double maxDate = DEFAULT_MAX_DATE;
    //

    /**
     * The earliest date currently visible.
     */
    private double minDateWindow = minDate;

    /**
     * The latest date currently visible.
     */
    private double maxDateWindow = maxDate;
    //

    /**
     * The lowest date allowed in the visible date window.
     */
    private double constraintMinDate = Double.NEGATIVE_INFINITY;

    /**
     * The highest date allowed in the visible date window.
     */
    private double constraintMaxDate = Double.POSITIVE_INFINITY;
    //

    /**
     * How selecting a person or place in this frieze propagates to related items.
     */
    private ItemSelectionPropagation itemSelectionPropagation = ItemSelectionPropagation.RECURSIVE;

    protected Frieze(final long anID, final TimeLineProject aProject, final String friezeName, final List<StayPeriod> staysToConsider) {
        id = anID;
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
        stayChangesListener = this::handleStayPeriodChanges;
        //
        propertyChangeSupport = new PropertyChangeSupport(Frieze.this);
        //
        project.addFrieze(Frieze.this);
        project.addListener(Frieze.this::handleTimeLineProjectChanges);
        // TODO : optimize
        staysToConsider.stream().forEachOrdered(Frieze.this::addStay);
    }

    @Override public long getId() {
        return id;
    }

    /**
     * Creates a frieze containing all of the project's stays.
     *
     * @param anID the id to assign to the new frieze
     * @param aProject the project the frieze belongs to
     * @param friezeName the frieze's name
     */
    public Frieze(final long anID, final TimeLineProject aProject, final String friezeName) {
        this(anID, aProject, friezeName, Collections.emptyList());
    }

    /**
     * Sets this frieze's name.
     *
     * @param aName this frieze's new name
     */
    public void setName(final String aName) {
        name = aName;
        propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
    }

    /**
     * @return this frieze's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the project this frieze belongs to
     */
    public TimeLineProject getProject() {
        return project;
    }

    private void addPerson(final Person aPerson) {
        if (!persons.contains(aPerson)) {
            persons.add(aPerson);
            final var stays = project.getStays().stream().filter(s -> s.getPerson() == aPerson).toList();
            final var tmpPlaces = stays.stream().map(StayPeriod::getPlace).distinct().toList();
            tmpPlaces.forEach(place -> {
                final var tempPersons = personsAtPlaces.computeIfAbsent(place, k -> new LinkedList<>());
                tempPersons.add(aPerson);
            });
            // notify place added
            // may be needed before adding stays for some variable updates
            propertyChangeSupport.firePropertyChange(PERSON_ADDED, this, aPerson);
            Platform.runLater(() -> stays.forEach(this::addStay));
        }
    }

    /**
     * Adds several stays to this frieze.
     *
     * @param stays the stays to add
     */
    public void addAllStays(final StayPeriod... stays) {
        for (StayPeriod s : stays) {
            addStay(s);
        }
    }

    /**
     * Adds several stays to this frieze.
     *
     * @param stays the stays to add
     */
    public void addAllStays(final Collection<? extends StayPeriod> stays) {
        stays.forEach(this::addStay);
    }

    /**
     * Stays lists are managed at frieze level.
     *
     * @param stay a stay to be represented in this Frieze
     */
    public void addStay(final StayPeriod stay) {
        if (!stayPeriods.contains(stay)) {
            stayPeriods.add(stay);
            //
            stay.addListener(stayChangesListener);
            //
            final var place = stay.getPlace();
            final var person = stay.getPerson();
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
            //
            updateMinMaxDates();
            //
            propertyChangeSupport.firePropertyChange(STAY_ADDED, this, stay);
        }
    }

    private void updateMinMaxDates() {
        minDate = stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).min().orElse(DEFAULT_MIN_DATE);
        maxDate = stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
    }

    /**
     * Removes several stays from this frieze.
     *
     * @param stays the stays to remove
     */
    public void removeAllStays(final StayPeriod... stays) {
        for (StayPeriod s : stays) {
            removeStay(s);
        }
    }

    /**
     * Removes several stays from this frieze.
     *
     * @param stays the stays to remove
     */
    public void removeAllStays(final Collection<? extends StayPeriod> stays) {
        stays.forEach(this::removeStay);
    }

    /**
     * @param stay the stay to remove from this frieze
     */
    public void removeStay(final StayPeriod stay) {
        if (stayPeriods.contains(stay)) {
            stayPeriods.remove(stay);
            stay.removeListener(stayChangesListener);
            //TODO : check if person list and place list is unchanged
            //
            updateMinMaxDates();
            //
            final var startDate = stay.getStartDate();
            final var endDate = stay.getEndDate();
            final var removeStart = stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).noneMatch(d -> d == startDate);
            final var removeEnd = stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).noneMatch(d -> d == endDate);
            maxDate = stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
            if (removeStart) {
                startDates.remove(startDate);
                // TODO fire ?
            }
            if (removeEnd) {
                endDates.remove(endDate);
                // TODO fire ?
            }
            if (stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).noneMatch(d -> d == startDate)
                    && stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).noneMatch(d -> d == startDate)) {
                dates.remove(startDate);
            }
            if (stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).noneMatch(d -> d == endDate)
                    && stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).noneMatch(d -> d == endDate)) {
                dates.remove(endDate);
            }
            //
            propertyChangeSupport.firePropertyChange(STAY_REMOVED, this, stay);
        }
    }

    /**
     * Updates a person's selection state in this frieze.
     *
     * @param aPerson the person whose selection changed
     * @param selected whether the person is now selected
     */
    public void updatePeopleSelection(final Person aPerson, final boolean selected) {
        LOG.log(Level.INFO, "updatePeopleSelection p:{0}, isSelected:{1}", new Object[]{aPerson, selected});
        if (selected) {
            addPerson(aPerson);
        } else {
            removePerson(aPerson);
        }
    }

    /**
     * @param aPlace the place whose selection changed
     * @param selected whether the place is now selected
     */
    public void updatePlaceSelection(final Place aPlace, final boolean selected) {
        //
        //System.err.println(" C'est ici qu'il faut faire l'update");
        if (selected) {
            if (!places.contains(aPlace)) {
                places.add(aPlace);
                propertyChangeSupport.firePropertyChange(PLACE_ADDED, this, aPlace);
                project.getStays().stream().filter(s -> s.getPlace() == aPlace & persons.contains(s.getPerson())).forEach(this::addStay);
            }
        } else {
            removePlace(aPlace);
        }
    }

    /**
     * Updates a person's selection state in this frieze.
     *
     * @param aPerson the person whose selection changed
     * @param selected whether the person is now selected
     */
    public void updatePersonSelection(final Person aPerson, final boolean selected) {
        LOG.log(Level.INFO, "Updating frieze for person: {0}, selected{1}.", new Object[]{aPerson, selected});
        if (selected) {
            addPerson(aPerson);
        } else {
            removePerson(aPerson);
        }
    }

    /**
     * @param p a place
     * @return an unmodifiable list of the persons present at the given place
     */
    public List<Person> getPersonsAtPlace(final Place p) {
        if (personsAtPlaces.containsKey(p)) {
            return Collections.unmodifiableList(personsAtPlaces.get(p));
        }
        return Collections.emptyList();
    }

    /**
     * @param listener the listener to add
     */
    public void addListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public void removeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @return an unmodifiable list of the stays in this frieze
     */
    public List<StayPeriod> getStayPeriods() {
        return Collections.unmodifiableList(stayPeriods);
    }

    /**
     * @param person a person
     * @return the stays for the given person
     */
    public List<StayPeriod> getStayPeriods(final Person person) {
        return stayPeriods.stream().filter(s -> s.getPerson().equals(person)).collect(Collectors.toList());
    }

    /**
     * @param aPlace a place
     * @return the stays at the given place
     */
    public List<StayPeriod> getStayPeriods(final Place aPlace) {
        return stayPeriods.stream().filter(s -> s.getPlace().equals(aPlace)).collect(Collectors.toList());
    }

    /**
     * @param stayPeriod a stay
     * @return the index of the stay's person in this frieze's persons list
     */
    public int getStayIndex(final StayPeriod stayPeriod) {
        return persons.indexOf(stayPeriod.getPerson());
    }

    /**
     * @return the number of stays in this frieze
     */
    public int getNbStays() {
        return stayPeriods.size();
    }

    /**
     * @return the number of persons in this frieze
     */
    public int getNbPersons() {
        return persons.size();
    }

    /**
     * @return an unmodifiable list of the persons in this frieze
     */
    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    /**
     * Creates a new free map view of this frieze, containing all of its content.
     *
     * @return the created free map
     */
    public FriezeFreeMap createFriezeFreeMap() {
        final var friezeFreeMap = FriezeFreeMapFactory.createFriezeFreeMap(this, true);
        friezeFreeMaps.add(friezeFreeMap);
        return friezeFreeMap;
    }

    /**
     * @param friezeFreeMap the free map to attach to this frieze
     */
    public void addFriezeFreeMap(final FriezeFreeMap friezeFreeMap) {
        if (!friezeFreeMaps.contains(friezeFreeMap)) {
            friezeFreeMaps.add(friezeFreeMap);
        }
    }

    /**
     * @param aFriezeFreeMap the free map to detach from this frieze
     */
    public void removeFriezeFreeMap(final FriezeFreeMap aFriezeFreeMap) {
        friezeFreeMaps.remove(aFriezeFreeMap);
    }

    /**
     * @return an unmodifiable list of the free maps built from this frieze
     */
    public List<FriezeFreeMap> getFriezeFreeMaps() {
        return Collections.unmodifiableList(friezeFreeMaps);
    }

    /**
     * @return an unmodifiable list of all the start/end dates in this frieze
     */
    public List<Double> getDates() {
        return Collections.unmodifiableList(dates);
    }

    /**
     * @return an unmodifiable list of the start dates in this frieze
     */
    public List<Double> getStartDates() {
        return Collections.unmodifiableList(startDates);
    }

    /**
     * @return an unmodifiable list of the end dates in this frieze
     */
    public List<Double> getEndDates() {
        return Collections.unmodifiableList(endDates);
    }

    /**
     * @return the earliest date across this frieze's stays
     */
    public double getMinDate() {
        return minDate;
    }

    /**
     * @return the latest date across this frieze's stays
     */
    public double getMaxDate() {
        return maxDate;
    }

    /**
     * @return the number of distinct dates in this frieze
     */
    public int getNbDates() {
        return dates.size();
    }

    /**
     * @return the earliest date currently visible
     */
    public double getMinDateWindow() {
        return minDateWindow;
    }

    /**
     * @return the latest date currently visible
     */
    public double getMaxDateWindow() {
        return maxDateWindow;
    }

    /**
     * @param newMinDateWindow the new earliest visible date
     */
    public void setMinDateWindow(final double newMinDateWindow) {
        minDateWindow = newMinDateWindow;
        propertyChangeSupport.firePropertyChange(DATE_WINDOW_CHANGED, minDateWindow, maxDateWindow);
    }

    /**
     * @param newMaxDateWindow the new latest visible date
     */
    public void setMaxDateWindow(final double newMaxDateWindow) {
        maxDateWindow = newMaxDateWindow;
        propertyChangeSupport.firePropertyChange(DATE_WINDOW_CHANGED, minDateWindow, maxDateWindow);
    }

    /**
     * @return an unmodifiable list of the places in this frieze
     */
    public List<Place> getPlaces() {
        return Collections.unmodifiableList(places);
    }

    /**
     * @return the time format used by this frieze's stays, or {@code TIME_MIN} if it has none
     */
    public TimeFormat getTimeFormat() {
        return stayPeriods.isEmpty() ? TimeFormat.TIME_MIN : stayPeriods.get(0).getTimeFormat();
    }

    private void handleTimeLineProjectChanges(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.HIGH_LEVEL_PLACE_ADDED, TimeLineProject.PLACE_ADDED -> {
                // Nothing to do
            }
            case TimeLineProject.PERSON_ADDED, TimeLineProject.STAY_ADDED -> {
                // Nothing to do
            }
            //            case TimeLineProject.STAY_ADDED ->
            //                addStay((StayPeriod) event.getNewValue());
            case TimeLineProject.STAY_REMOVED ->
                removeStay((StayPeriod) event.getNewValue());
            case TimeLineProject.PLACE_REMOVED -> {
                final var placeRemoved = (Place) event.getNewValue();
                removePlace(placeRemoved);
            }
            case TimeLineProject.PERSON_REMOVED -> {
                final var personRemoved = (Person) event.getNewValue();
                removePerson(personRemoved);
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    private void handleStayPeriodChanges(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case StayPeriod.START_DATE_CHANGED -> {
                final var stay = (StayPeriod) event.getSource();
                // First update the relevant dates before notifying of the stay change
                updateDatesOnRemoval((long) event.getOldValue(), true);
                updateDatesOnCreation((long) event.getNewValue(), true);
                propertyChangeSupport.firePropertyChange(STAY_UPDATED, this, stay);
            }
            case StayPeriod.END_DATE_CHANGED -> {
                // First update the relevant dates before notifying of the stay change
                updateDatesOnRemoval((long) event.getOldValue(), false);
                updateDatesOnCreation((long) event.getNewValue(), false);
                final var stay = (StayPeriod) event.getSource();
                propertyChangeSupport.firePropertyChange(STAY_UPDATED, this, stay);
            }
            default ->
                throw new UnsupportedOperationException("Property not supported in handleStayPeriodChanges :: " + event.getPropertyName());
        }
    }

    private void removePlace(final Place placeRemoved) {
        places.remove(placeRemoved);
        personsAtPlaces.remove(placeRemoved);
        final var staysToRemove = stayPeriods.stream().filter(s -> s.getPlace() == placeRemoved).toList();
        // for concurrency accces...
        staysToRemove.forEach(this::removeStay);
        propertyChangeSupport.firePropertyChange(PLACE_REMOVED, this, placeRemoved);
        //
        //placeRemoved.getPlaces().forEach(this::removePlace);
    }

    private boolean removePerson(final Person personRemoved) {
        LOG.log(Level.INFO, "Removing person: {0} from {1}.", new Object[]{personRemoved, this});
        final var removed = persons.remove(personRemoved);
        if (!removed) {
            return false;
        }
        personsAtPlaces.forEach((place, list) -> list.remove(personRemoved));
        final List<StayPeriod> impactedStays = stayPeriods.stream().filter(s -> s.getPerson() == personRemoved).toList();
        impactedStays.forEach(this::removeStay);
        LOG.log(Level.INFO, " > Done removing person: {0},\t >> propagating change.", new Object[]{personRemoved, this});
        propertyChangeSupport.firePropertyChange(PERSON_REMOVED, this, personRemoved);
        return true;
    }

    private void updateDatesOnRemoval(final double dateRemoved, final boolean isStartDate) {
        final var notInStartDates = stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).noneMatch(d -> d == dateRemoved);
        final var notInEndDates = stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).noneMatch(d -> d == dateRemoved);
        if (isStartDate && notInStartDates) {
            startDates.remove(dateRemoved);
            propertyChangeSupport.firePropertyChange(START_DATE_REMOVED, this, dateRemoved);
        } else if (!isStartDate && notInEndDates) {
            endDates.remove(dateRemoved);
            propertyChangeSupport.firePropertyChange(END_DATE_REMOVED, this, dateRemoved);
        }
        if (notInStartDates && notInEndDates) {
            dates.remove(dateRemoved);
        }

    }

    private void updateDatesOnCreation(final double dateAdded, final boolean isStartDate) {
        // start by updating min max
        final var minWindowsAtMinDate = minDate == minDateWindow;
        final var maxWindowsAtMaxDate = maxDate == maxDateWindow;
        final var oldMinDate = minDate;
        final var oldMaxDate = maxDate;
        minDate = stayPeriods.stream().mapToDouble(StayPeriod::getStartDate).min().orElse(DEFAULT_MIN_DATE);
        maxDate = stayPeriods.stream().mapToDouble(StayPeriod::getEndDate).max().orElse(DEFAULT_MAX_DATE);
        //
        if (!dates.contains(dateAdded)) {
            dates.add(dateAdded);
        }
        if (isStartDate && !startDates.contains(dateAdded)) {
            startDates.add(dateAdded);
            propertyChangeSupport.firePropertyChange(START_DATE_ADDED, this, dateAdded);
        } else if (!isStartDate && !endDates.contains(dateAdded)) {
            endDates.add(dateAdded);
            propertyChangeSupport.firePropertyChange(END_DATE_ADDED, this, dateAdded);
        }
        if (minWindowsAtMinDate && oldMinDate != minDate) {
            setMinDateWindow(minDate);
        }
        if (maxWindowsAtMaxDate && oldMaxDate != maxDate) {
            setMaxDateWindow(maxDate);
        }
    }

    @Override
    public String toString() {
        return "Frieze [" + name + "].";
    }

}
