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

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.StayPeriodLocalDate;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

/**
 *
 * @author hamon
 */
public class StaysCreationViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(StaysCreationViewController.this);
    //
    @FXML
    private RadioButton timeRB, dateRB;
    //
    @FXML
    private SearchableComboBox<Person> personCB;
    //
    @FXML
    private SearchableComboBox<Place> placesSearchCB;
    //
    @FXML
    private Label startTimeL, startDateL, endTimeL, endDateL;
    @FXML
    private TextField startTimeTF, endTimeTF;
    @FXML
    private DatePicker startDateP, endDateP;
    //
    @FXML
    private Button createB, updateB;
    //
    @FXML
    private ListView<StayPeriod> chronologyListView;
    //
    private TimeFormat timeFormat;
    //
    private TimeLineProject timeline = null;
    //
    // is there a better way ?
    private final PropertyChangeListener timelineListener = event -> handleTimelineChanges(event);
    //
    private boolean personOK = false;
    private boolean placeOK = false;
    private boolean startOK = false;
    private boolean endOK = false;
    //
    private long startTime = -1;
    private long endTime = -1;
    //
    private StayPeriod selectedStayPeriod = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup stayModeToggelGroup = new ToggleGroup();
        timeRB.setToggleGroup(stayModeToggelGroup);
        dateRB.setToggleGroup(stayModeToggelGroup);
        updateB.setDisable(true);
        timeRB.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                timeFormat = TimeFormat.TIME_MIN;
                startTimeL.setDisable(false);
                endTimeL.setDisable(false);
                startTimeTF.setDisable(false);
                endTimeTF.setDisable(false);
                startDateL.setDisable(true);
                endDateL.setDisable(true);
                startDateP.setDisable(true);
                endDateP.setDisable(true);
                //
                retreiveStartTime(startTimeTF.getText());
                retreiveEndTime(endTimeTF.getText());
                updateCreateStatus();
            }
        });
        dateRB.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                timeFormat = TimeFormat.LOCAL_TIME;
                startTimeL.setDisable(true);
                endTimeL.setDisable(true);
                startTimeTF.setDisable(true);
                endTimeTF.setDisable(true);
                startDateL.setDisable(false);
                endDateL.setDisable(false);
                startDateP.setDisable(false);
                endDateP.setDisable(false);
                //
                startOK = startDateP.getValue() != null;
                endOK = endDateP.getValue() != null;
                updateCreateStatus();
            }
        });
        dateRB.setSelected(true);
        //
        personCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> ov, Person t, Person t1) -> {
            personOK = t1 != null;
            updateCreateStatus();
        });
        placesSearchCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Place> ov, Place t, Place t1) -> {
            placeOK = t1 != null;
            updateCreateStatus();
        });
        startTimeTF.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            retreiveStartTime(t1);
        });
        endTimeTF.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            retreiveEndTime(t1);
        });
        startDateP.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            startOK = t1 != null;
            updateCreateStatus();
        });
        endDateP.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            endOK = t1 != null;
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
            }
        });
        //
        startDateP.getEditor().setOnAction(eh -> {
            System.err.println("!! " + eh);
        });

        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter
                    = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        startDateP.setConverter(converter);
        startDateP.setPromptText("dd-MM-yyyy");
        //
        startDateP.getEditor().setOnKeyTyped(eh -> {
            try {
                LocalDate aDate = converter.fromString(startDateP.getEditor().getText());
                startDateP.setValue(aDate);
                System.err.println(" !! !!! DONE !!");
            } catch (Exception e) {
                System.err.println(" ^^ ignoring");
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
                stay.setStartDate(startDateP.getValue());
                stay.setEndDate(endDateP.getValue());
            }
            case TIME_MIN -> {
                var stay = (StayPeriodSimpleTime) selectedStayPeriod;
                stay.setStartDate(startTime);
                stay.setEndDate(endTime);
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
                stay = StayFactory.createStayPeriodLocalDate(personCB.getValue(), startDateP.getValue(), endDateP.getValue(), placesSearchCB.getValue());
            case TIME_MIN ->
                stay = StayFactory.createStayPeriodSimpleTime(personCB.getValue(), startTime, endTime, placesSearchCB.getValue());
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
            default ->
                throw new UnsupportedOperationException(event.toString());
        }
    }

    private void retreiveStartTime(String value) {
        try {
            startTime = Long.parseLong(value);
            startOK = true;
        } catch (NumberFormatException e) {
            startOK = false;
        }
        updateCreateStatus();
    }

    private void retreiveEndTime(String value) {
        try {
            endTime = Long.parseLong(value);
            endOK = true;
        } catch (NumberFormatException e) {
            endOK = false;
        }
        updateCreateStatus();
    }

    private void updateCreateStatus() {
        var ready = personOK & placeOK & startOK & endOK;
        createB.setDisable(!ready);
    }

    private void displaySelectedStay() {
        if (selectedStayPeriod == null) {
            return;
        }
        switch (selectedStayPeriod.getTimeFormat()) {
            case LOCAL_TIME -> {
                dateRB.setSelected(true);
                startDateP.setValue(LocalDate.ofEpochDay((long) selectedStayPeriod.getStartDate()));
                endDateP.setValue(LocalDate.ofEpochDay((long) selectedStayPeriod.getEndDate()));
            }
            case TIME_MIN -> {
                timeRB.setSelected(true);
                startTimeTF.setText(MathUtils.doubleToString(selectedStayPeriod.getStartDate()));
                endTimeTF.setText(MathUtils.doubleToString(selectedStayPeriod.getEndDate()));
            }
            default ->
                throw new UnsupportedOperationException();
        }
        placesSearchCB.getSelectionModel().select(selectedStayPeriod.getPlace());
        personCB.getSelectionModel().select(selectedStayPeriod.getPerson());

    }

    private void clearFields() {
        placesSearchCB.getSelectionModel().clearSelection();
        personCB.getSelectionModel().clearSelection();
        startTimeTF.setText("");
        endTimeTF.setText("");
        startDateP.setValue(null);
        endDateP.setValue(null);
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
