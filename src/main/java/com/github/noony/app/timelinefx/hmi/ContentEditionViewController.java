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
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.runLater;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author hamon
 */
public final class ContentEditionViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private enum EditType {
        PERSON, PLACE, NONE
    }

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab placeTab, personTab;

    @FXML
    private TreeView<Place> placesCheckTreeView;
    @FXML
    private ListView<Person> personCheckListView;
    @FXML
    private Button clearSelectionButton, createButton, editButton, deleteButton;

    @FXML
    private SplitPane splitPane;

    private final PropertyChangeListener timelineChangeListener = this::handleTimelineChanges;
    //
    private TimeLineProject timeLineProject;
    //
    private CustomModalWindow customModalWindow = null;
    //
    private Parent placeCreationView = null;
    private Parent personCreationView = null;
    private Parent staysCreationView = null;
    private PlaceCreationViewController placeCreationController = null;
    private PersonCreationViewController personCreationController = null;
    private StaysCreationViewController staysCreationViewController = null;
    private EditType editType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // init
        loadStaysCreationView();
        //
        createButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        clearSelectionButton.setDisable(true);
        placesCheckTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        placesCheckTreeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<Place>> ov, TreeItem<Place> t, TreeItem<Place> t1) -> {
            editButton.setDisable(t1 == null);
            deleteButton.setDisable(t1 == null);
            clearSelectionButton.setDisable(t1 == null);
            if (t1 != null & staysCreationViewController != null) {
                staysCreationViewController.filterByPlace(t1.getValue());
            }
        });
        //
        personCheckListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> ov, Person t, Person t1) -> {
            editButton.setDisable(t1 == null);
            deleteButton.setDisable(t1 == null);
            clearSelectionButton.setDisable(t1 == null);
            if (t1 != null & staysCreationViewController != null) {
                staysCreationViewController.filterByPerson(t1);
            }
        });
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            if (t1 == placeTab) {
                setEditMode(EditType.PLACE);
            } else if (t1 == personTab) {
                setEditMode(EditType.PERSON);
            } else {
                setEditMode(EditType.NONE);
            }
        });
        mainTabPane.getSelectionModel().select(placeTab);
        setEditMode(EditType.PLACE);
    }

    @FXML
    protected void handleClearSelection(ActionEvent event) {
        personCheckListView.getSelectionModel().clearSelection();
        placesCheckTreeView.getSelectionModel().clearSelection();
        if (staysCreationViewController != null) {
            staysCreationViewController.displayAll();
        }
    }

    @FXML
    protected void handleCreate(ActionEvent event) {
        LOG.log(Level.INFO, "handleCreate {0}", event);
        switch (editType) {
            case PERSON ->
                createNewPerson();
            case PLACE ->
                createNewPlace();
            default ->
                throw new IllegalStateException("Creation forbidden for edition type : " + editType);
        }
    }

    @FXML
    protected void handleEdit(ActionEvent event) {
        LOG.log(Level.INFO, "handleEdit {0}", event);
        switch (editType) {
            case PERSON ->
                editPerson();
            case PLACE ->
                editPlace();
            default ->
                throw new IllegalStateException("Edition forbidden for edition type : " + editType);
        }
    }

    @FXML
    protected void handleDelete(ActionEvent event) {
        LOG.log(Level.INFO, "handleDeletePlace {0}", event);
        if (timeLineProject != null) {
            switch (editType) {
                case PERSON ->
                    removePerson();
                case PLACE ->
                    removePlace();
                default ->
                    throw new IllegalStateException("Edition forbidden for edition type : " + editType);
            }
        }
    }

    protected void setTimeLineProject(TimeLineProject aTimeLineProject) {
        if (timeLineProject != null) {
            timeLineProject.removeListener(timelineChangeListener);
        }
        timeLineProject = aTimeLineProject;
        timeLineProject.addListener(timelineChangeListener);
        //
        if (staysCreationViewController != null) {
            staysCreationViewController.setTimelineProject(aTimeLineProject);
        }
        if (personCreationController != null) {
            personCreationController.setTimelineProject(aTimeLineProject);
        }
        //
        updatePersonTab();
        updatePlacesTab();
        // Todo remove old tabs
//        timeLineProject.getFriezes().forEach(f -> mainTabPane.getTabs().add(createFriezeTab(f)));
    }

    private void showModalStage(Parent content) {
        if (customModalWindow == null) {
            customModalWindow = new CustomModalWindow(mainTabPane.getScene().getWindow(), content);
        } else {
            customModalWindow.setRoot(content);
        }
        customModalWindow.show();
    }

    private void updatePersonTab() {
        personCheckListView.getItems().setAll(timeLineProject.getPersons());
    }

    private void updatePlacesTab() {
        var rootPlaceItem = createRootPlaceItem();
        timeLineProject.getHighLevelPlaces().forEach(p -> rootPlaceItem.getChildren().add(createTreeItemPlace(p)));
        placesCheckTreeView.setRoot(rootPlaceItem);
        rootPlaceItem.setExpanded(true);
        placesCheckTreeView.refresh();
    }

    private TreeItem<Place> createRootPlaceItem() {
        var rootPlace = PlaceFactory.PLACES_PLACE;
        var rootPlaceItem = new TreeItem(rootPlace);
        return rootPlaceItem;
    }

    private TreeItem<Place> createTreeItemPlace(Place place) {
        var placeItem = new TreeItem(place);
        place.getPlaces().forEach(p -> placeItem.getChildren().add(createTreeItemPlace(p)));
        placeItem.setExpanded(true);
        return placeItem;
    }

    private void setEditMode(EditType mode) {
        editType = mode;
        switch (editType) {
            case PERSON, PLACE ->
                createButton.setDisable(false);
            case NONE ->
                createButton.setDisable(true);
            default ->
                throw new IllegalStateException("Illegal edition type : " + editType);
        }
    }

    private void createNewPlace() {
        if (placeCreationView == null) {
            loadPlaceCreationView();
        } else {
            placeCreationController.reset();
        }
        placeCreationController.setEditionMode(EditionMode.CREATION);
        showModalStage(placeCreationView);
    }

    private void createNewPerson() {
        if (personCreationView == null) {
            loadPersonCreationView();
        } else {
            personCreationController.reset();
        }
        personCreationController.setEditionMode(EditionMode.CREATION);
        showModalStage(personCreationView);
    }

    private void editPlace() {
        if (placeCreationController == null) {
            loadPlaceCreationView();
        }
        LOG.log(Level.INFO, "Opening Place Edition view");
        placeCreationController.reset();
        placeCreationController.setEditionMode(EditionMode.EDITION);
        placeCreationController.setEditPlace(placesCheckTreeView.getSelectionModel().getSelectedItem().getValue());
        showModalStage(placeCreationView);
    }

    private void editPerson() {
        if (personCreationController == null) {
            loadPersonCreationView();
        }
        LOG.log(Level.INFO, "Opening Person Edition view");
        personCreationController.reset();
        personCreationController.setEditionMode(EditionMode.EDITION);
        personCreationController.setPerson(personCheckListView.getSelectionModel().getSelectedItem());
        showModalStage(personCreationView);
    }

    private void removePlace() {
        if (timeLineProject != null) {
            Place deletedPlace = placesCheckTreeView.getSelectionModel().getSelectedItem().getValue();
            LOG.log(Level.INFO, "Removing place {0}", new Object[]{deletedPlace});
            timeLineProject.removePlace(deletedPlace);
        }
    }

    private void removePerson() {
        if (timeLineProject != null) {
            Person deletedPerson = personCheckListView.getSelectionModel().getSelectedItem();
            LOG.log(Level.INFO, "Removing person {0}", new Object[]{deletedPerson});
            timeLineProject.removePerson(deletedPerson);
        }
    }

    private void loadStaysCreationView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("StaysCreationView.fxml"));
        try {
            staysCreationView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load StaysCreationView ::  {0}", new Object[]{ex});
        }
        splitPane.getItems().add(staysCreationView);
        splitPane.setDividerPositions(0.25);
        staysCreationViewController = loader.getController();
        staysCreationViewController.setTimelineProject(timeLineProject);
        staysCreationViewController.addPropertyChangeListener(this::handleStaysCreationControllerChanges);
    }

    private void loadPlaceCreationView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("PlaceCreationView.fxml"));
        try {
            placeCreationView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load PlaceCreationView ::  {0}", new Object[]{ex});
        }
        placeCreationController = loader.getController();
        placeCreationController.addPropertyChangeListener(this::handlePlaceCreationControllerChanges);
    }

    private void loadPersonCreationView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("PersonCreationView.fxml"));
        try {
            personCreationView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load PersonCreationView ::  {0}", new Object[]{ex});
        }
        personCreationController = loader.getController();
        personCreationController.addPropertyChangeListener(this::handlePersonCreationControllerChanges);
        if (timeLineProject != null) {
            personCreationController.setTimelineProject(timeLineProject);
        }
    }

    private void handleStaysCreationControllerChanges(PropertyChangeEvent event) {
        System.err.println("TODO :: handleStaysCreationControllerChanges");
    }

    private void handlePlaceCreationControllerChanges(PropertyChangeEvent event) {
        Place place;
        switch (event.getPropertyName()) {
            case PlaceCreationViewController.PLACE_CREATED -> {
                place = (Place) event.getNewValue();
                customModalWindow.hide();
                timeLineProject.addPlace(place);
                updatePlacesTab();
            }

            case PlaceCreationViewController.PLACE_EDITIED -> {
                place = (Place) event.getNewValue();
                if (place.isRootPlace()) {
                    timeLineProject.addHighLevelPlace(place);
                } else {
//                    timeLineProject.addHighLevelPlace(place);
// nothing is needed since the parent place will take care of things
                }
                customModalWindow.hide();
                updatePlacesTab();
            }
            case PlaceCreationViewController.CANCEL_PLACE_CREATION ->
                customModalWindow.hide();
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    private void handleTimelineChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.PLACE_REMOVED, TimeLineProject.PLACE_ADDED ->
                runLater(this::updatePlacesTab);
            case TimeLineProject.PERSON_ADDED, TimeLineProject.PERSON_REMOVED ->
                runLater(this::updatePersonTab);
            case TimeLineProject.STAY_ADDED, TimeLineProject.STAY_REMOVED -> {
                // TODO : see if ignoring is OK
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    private void handlePersonCreationControllerChanges(PropertyChangeEvent event) {
        Person person;
        switch (event.getPropertyName()) {
            case PersonCreationViewController.PERSON_CREATED -> {
                person = (Person) event.getNewValue();
                customModalWindow.hide();
                timeLineProject.addPerson(person);
                updatePersonTab();
            }
            case PersonCreationViewController.PERSON_EDITIED -> {
                customModalWindow.hide();
                updatePersonTab();
            }
            case PersonCreationViewController.CANCEL_PERSON_CREATION ->
                customModalWindow.hide();
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

}
