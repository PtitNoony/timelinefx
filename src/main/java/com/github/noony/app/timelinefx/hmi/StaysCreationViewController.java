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

package com.github.noony.app.timelinefx.hmi;

import com.github.noony.app.timelinefx.core.Date;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.StayPeriodLocalDate;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.SearchableComboBox;

/**
 *
 * @author hamon
 */
public final class StaysCreationViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(StaysCreationViewController.this);

    @FXML
    private SearchableComboBox<Person> personCB;
    //
    @FXML
    private SearchableComboBox<Place> placesSearchCB;
    //
    @FXML
    private HBox startDateBox, endDateBox;
    //
    @FXML
    private Button createB, updateB;
    //
    @FXML
    private ListView<StayPeriod> chronologyListView;

    private TimeFormat timeFormat;

    private TimeLineProject timeline = null;
    //
    // is there a better way ?
    private final PropertyChangeListener timelineListener = StaysCreationViewController.this::handleTimelineChanges;

    private boolean personOK = false;

    private boolean placeOK = false;

    private DateViewer startDateViewer;

    private DateViewer endDateViewer;

    private StayPeriod selectedStayPeriod = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateB.setDisable(true);
        personCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> ov, Person t, Person t1) -> {
            personOK = t1 != null;
            updateCreateStatus();
        });
        placesSearchCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Place> ov, Place t, Place t1) -> {
            placeOK = t1 != null;
            updateCreateStatus();
        });
        //
        chronologyListView.setCellFactory((ListView<StayPeriod> p) -> {
            return new StayPeriodListCellImpl();
        });
        chronologyListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends StayPeriod> ov, StayPeriod t, StayPeriod t1) -> {
            if (t1 != null) {
                selectedStayPeriod = t1;
                updateB.setDisable(false);
                displaySelectedStay();
            } else {
                selectedStayPeriod = null;
                updateB.setDisable(true);
                clearFields();
                applyProjectTimeFormat();
            }
        });
        //
        updateCreateStatus();
    }

    @FXML
    protected void handleUpdateAction(ActionEvent event) {
        selectedStayPeriod.setPerson(personCB.getValue());
        selectedStayPeriod.setPlace(placesSearchCB.getValue());
        switch (timeFormat) {
            case LOCAL_TIME -> {
                var stay = (StayPeriodLocalDate) selectedStayPeriod;
                stay.setStartDate(startDateViewer.getDate().getDateAsLocal());
                stay.setEndDate(endDateViewer.getDate().getDateAsLocal());
            }
            case TIME_MIN -> {
                var stay = (StayPeriodSimpleTime) selectedStayPeriod;
                stay.setStartDate(startDateViewer.getDate().getDateAsDouble());
                stay.setEndDate(endDateViewer.getDate().getDateAsDouble());
            }
            default ->
                throw new UnsupportedOperationException("Trying to create a Stay of unsupported type :: " + timeFormat);
        }
        chronologyListView.refresh();
    }

    @FXML
    protected void handleClearAction(ActionEvent event) {
        chronologyListView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void handleCreateAction(ActionEvent event) {
        StayPeriod stay;
        switch (timeFormat) {
            case LOCAL_TIME ->
                stay = StayFactory.createStayPeriodLocalDate(personCB.getValue(), startDateViewer.getDate().getDateAsLocal(), endDateViewer.getDate().getDateAsLocal(), placesSearchCB.getValue());
            case TIME_MIN ->
                stay = StayFactory.createStayPeriodSimpleTime(personCB.getValue(), startDateViewer.getDate().getDateAsDouble(), endDateViewer.getDate().getDateAsDouble(), placesSearchCB.getValue());
            default ->
                throw new UnsupportedOperationException("Trying to create a Stay of unsupported type :: " + timeFormat);
        }
        if (timeline != null) {
            timeline.addStay(stay);
        } else {
            LOG.log(Level.SEVERE, "Could not create stay : {1} because no timeline project is set.", new Object[]{stay});
        }
    }

    protected void setTimelineProject(TimeLineProject aTimeline) {
        if (timeline != null) {
            timeline.removeListener(timelineListener);
        }
        timeline = aTimeline;
        if (timeline != null) {
            timeline.addListener(timelineListener);
            applyProjectTimeFormat();
            runLater(() -> {
                personCB.getItems().setAll(timeline.getPersons());
                chronologyListView.getItems().setAll(timeline.getStays());
                ObservableList<Place> myPlaces = FXCollections.observableList(PlaceFactory.getPlaces());
                placesSearchCB.setItems(myPlaces);
                placesSearchCB.getItems().sorted(Place.COMPARATOR);
            });
        }
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void filterByPlace(Place aPlace) {
        if (aPlace == null) {
            // TODO factorize code
            chronologyListView.getItems().setAll(timeline.getStays());
        } else {
            chronologyListView.getItems().setAll(timeline.getStays().stream().
                    filter(s -> s.getPlace() == aPlace)
                    .sorted(StayPeriod.STAY_COMPARATOR)
                    .collect(Collectors.toList()));
        }
    }

    protected void filterByPerson(Person aPerson) {
        if (aPerson == null) {
            // TODO factorize code
            chronologyListView.getItems().setAll(timeline.getStays());
        } else {
            chronologyListView.getItems().setAll(timeline.getStays().stream()
                    .filter(s -> s.getPerson() == aPerson)
                    .sorted(StayPeriod.STAY_COMPARATOR)
                    .collect(Collectors.toList()));
        }
    }

    protected void displayAll() {
        chronologyListView.getItems().setAll(timeline.getStays());
    }

    private void handleTimelineChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.HIGH_LEVEL_PLACE_ADDED, TimeLineProject.PLACE_ADDED, TimeLineProject.PLACE_REMOVED -> {
                placesSearchCB.getItems().setAll(timeline.getAllPlaces());
                placesSearchCB.getItems().sorted(Place.COMPARATOR);
            }
            case TimeLineProject.PERSON_ADDED, TimeLineProject.PERSON_REMOVED ->
                personCB.getItems().setAll(timeline.getPersons());
            case TimeLineProject.STAY_ADDED ->
                chronologyListView.getItems().add((StayPeriod) event.getNewValue());
            case TimeLineProject.STAY_REMOVED ->
                chronologyListView.getItems().remove((StayPeriod) event.getNewValue());
            case TimeLineProject.TIME_FORMAT_CHANGED -> {
                if (selectedStayPeriod == null) {
                    applyProjectTimeFormat();
                }
            }
            default ->
                throw new UnsupportedOperationException(event.toString());
        }
    }

    /**
     * Rebuilds the start/end {@link DateViewer}s to match the project's own time format,
     * used as the format for the next stay to be created.
     */
    private void applyProjectTimeFormat() {
        if (timeline == null) {
            return;
        }
        switch (timeline.getTimeFormat()) {
            case LOCAL_TIME ->
                setDateViewers(LocalDate.now(), LocalDate.now());
            case TIME_MIN ->
                setDateViewers(0d, 0d);
            default ->
                throw new UnsupportedOperationException();
        }
    }

    private void updateCreateStatus() {
        var ready = personOK && placeOK;
        createB.setDisable(!ready);
    }

    private void displaySelectedStay() {
        if (selectedStayPeriod == null) {
            return;
        }
        switch (selectedStayPeriod.getTimeFormat()) {
            case LOCAL_TIME ->
                setDateViewers(
                        LocalDate.ofEpochDay((long) selectedStayPeriod.getStartDate()),
                        LocalDate.ofEpochDay((long) selectedStayPeriod.getEndDate()));
            case TIME_MIN ->
                setDateViewers(selectedStayPeriod.getStartDate(), selectedStayPeriod.getEndDate());
            default ->
                throw new UnsupportedOperationException();
        }
        placesSearchCB.getSelectionModel().select(selectedStayPeriod.getPlace());
        personCB.getSelectionModel().select(selectedStayPeriod.getPerson());

    }

    private void clearFields() {
        placesSearchCB.getSelectionModel().clearSelection();
        personCB.getSelectionModel().clearSelection();
    }

    private void setDateViewers(final LocalDate startDate, final LocalDate endDate) {
        timeFormat = TimeFormat.LOCAL_TIME;
        installDateViewers(new Date(startDate), new Date(endDate));
    }

    private void setDateViewers(final double startValue, final double endValue) {
        timeFormat = TimeFormat.TIME_MIN;
        installDateViewers(new Date(startValue), new Date(endValue));
    }

    private void installDateViewers(final Date startSeed, final Date endSeed) {
        startDateViewer = new DateViewer(startSeed);
        endDateViewer = new DateViewer(endSeed);
        startDateBox.getChildren().setAll(startDateViewer.getNode());
        endDateBox.getChildren().setAll(endDateViewer.getNode());
    }

    private static class StayPeriodListCellImpl extends ListCell<StayPeriod> {

        private StayPeriodListCellImpl() {
        }

        @Override
        public void updateItem(StayPeriod item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getString());
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem().getDisplayString();
        }
    }

}
