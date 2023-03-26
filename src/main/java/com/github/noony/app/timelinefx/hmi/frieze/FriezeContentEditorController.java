/*
 * Copyright (C) 2023 NoOnY
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
package com.github.noony.app.timelinefx.hmi.frieze;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.drawings.IFriezeView;
import com.github.noony.app.timelinefx.hmi.AppInstanceConfiguration;
import com.github.noony.app.timelinefx.hmi.FriezePeopleViewController;
import com.github.noony.app.timelinefx.hmi.StageFactory;
import com.github.noony.app.timelinefx.hmi.byplace.FriezePlaceViewController;
import com.github.noony.app.timelinefx.hmi.freemap.FreeMapListCellImpl;
import com.github.noony.app.timelinefx.hmi.freemap.FreeMapView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.runLater;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckTreeView;

/**
 *
 * @author hamon
 */
public class FriezeContentEditorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private TabPane tabPane;
    @FXML
    private TextField nameField;
    @FXML
    private ListView<FriezeFreeMap> freemapListView;
    @FXML
    private Button deleteFreemapButton;
    @FXML
    private CheckTreeView<Place> placesCheckTreeView;
    @FXML
    private CheckListView<Person> personCheckListView;
    @FXML
    private Button clearPeopleSelectionB;
    @FXML
    private Button clearPlaceSelectionB;
    @FXML
    private CheckListView<StayPeriod> staysCheckListView;
    // properties
    @FXML
    private GridPane timeGrid;
    //
    private List<CheckBoxTreeItem<Place>> allTreeItems = new LinkedList<>();
    private Map<FriezeFreeMap, Tab> freemapTabs;
    //
    private TimeLineProject timeLineProject;
    private Frieze frieze;
    //
    private FriezePlaceViewController spatialViewController;
    private FriezePeopleViewController peopleViewController;
    // properties
    private TextField friezeStartTimeField;
    private TextField friezeEndTimeField;
    //
    private AnchorPane spatialViewRootPane;
    private AnchorPane peopleViewRootPane;
    //
    private PropertyChangeListener projectListener;
    private PropertyChangeListener friezeListener;
    private PropertyChangeListener placeCheckTreeItemChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
        tabPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        //
        freemapTabs = new HashMap<>();
        createPropertyControls();
        createSpatialView();
        createPeopleView();
        projectListener = this::handleProjectChanges;
        friezeListener = this::handleFriezeChanges;
        placeCheckTreeItemChangeListener = this::handlePlaceCheckTreeItemChanges;
        nameField.textProperty().addListener((var ov, var t, var t1) -> {
            if (frieze != null) {
                frieze.setName(t1);
                nameField.setText(frieze.getName());
            }
        });
        //
        freemapListView.setEditable(true);
        freemapListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        freemapListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends FriezeFreeMap> ov, FriezeFreeMap t, FriezeFreeMap t1) -> {
            deleteFreemapButton.setDisable(t1 == null);
            if (t1 != null) {
                tabPane.getSelectionModel().select(freemapTabs.get(t1));
            }
        });
        freemapListView.setCellFactory((ListView<FriezeFreeMap> p) -> {
            return new FreeMapListCellImpl(this);
        });
        deleteFreemapButton.setDisable(true);
        //
        tabPane.getSelectionModel().selectedItemProperty().addListener((var ov, var t, var t1) -> {
            if (t1 != null) {
                for (Map.Entry<FriezeFreeMap, Tab> entry : freemapTabs.entrySet()) {
                    if (entry.getValue() == t1) {
                        freemapListView.getSelectionModel().select(entry.getKey());
                        break;
                    }
                }
            }
        });
        // Manage people selection
        personCheckListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        personCheckListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> ov, Person t, Person t1) -> {
            LOG.log(Level.FINE, "Handling personCheckListView selection change {0}", ov);
            updateStaysPane();
        });
        // Manage people add/remove
        personCheckListView.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends Person> change) -> {
            LOG.log(Level.FINE, "Handling personCheckListView add/remove change {0}", change);
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(p -> frieze.updatePeopleSelection(p, true));
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(p -> frieze.updatePeopleSelection(p, false));
                }
            }
        });
        // Manage places selection
        placesCheckTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        placesCheckTreeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Object> ov, Object t, Object t1) -> {
            LOG.log(Level.FINE, "Handling placesCheckTreeView selection change {0}", ov);
            updateStaysPane();
        });
        // Manage stay add/remove
        staysCheckListView.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends StayPeriod> change) -> {
            LOG.log(Level.FINE, "Handling staysCheckListView add/remove change {0}", change);
            while (change.next()) {
                if (change.wasAdded()) {
                    frieze.addAllStays(change.getAddedSubList());
                } else if (change.wasRemoved()) {
                    frieze.removeAllStays(change.getRemoved());
                }
            }
        });
        //
        AppInstanceConfiguration.addPropertyChangeListener(this::handleAppInstanceConfigChanges);
        runLater(StageFactory::reApplyTheme);
        StageFactory.reApplyTheme();
    }

    @FXML
    protected void handleFreeMapCreation(ActionEvent event) {
        LOG.log(Level.INFO, "handleFreeMapCreation {0}", event);
        var freeMap = frieze.createFriezeFreeMap();
        createFreeMapView(freeMap);
        runLater(() -> freemapListView.getSelectionModel().select(freeMap));
    }

    @FXML
    protected void handleDeleteFreeMap(ActionEvent event) {
        LOG.log(Level.INFO, "handleDeleteFreeMap {0}", event);
        var freeMap = freemapListView.getSelectionModel().getSelectedItem();
        deleteFreeMap(freeMap);
    }

    @FXML
    protected void handleClearPeopleSelection(ActionEvent event) {
        LOG.log(Level.INFO, "handleClearPeopleSelection {0}", event);
        personCheckListView.getSelectionModel().clearSelection();
        // no update needed since it will occur via property change
    }

    @FXML
    protected void handleClearPlaceSelection(ActionEvent event) {
        LOG.log(Level.INFO, "handleClearPeopleSelection {0}", event);
        placesCheckTreeView.getSelectionModel().clearSelection();
        // no update needed since it will occur via property change
    }

    public FriezeFreeMap getSelectedFreeMap() {
        return freemapListView.getSelectionModel().getSelectedItem();
    }

    private void setTimeline(TimeLineProject aTimeLineProject) {
        if (timeLineProject != null) {
            timeLineProject.removeListener(projectListener);
        }
        timeLineProject = aTimeLineProject;
        timeLineProject.addListener(projectListener);
    }

    private void setFrieze(Frieze aFrieze) {
        if (aFrieze == frieze) {
            return;
        } else if (frieze != null) {
            frieze.removeListener(friezeListener);
        }
        frieze = aFrieze;
        frieze.addListener(friezeListener);
        updatePropertiesTab();
        updatePeoplePane();
        updatePlacesPane();
        updateStaysPane();
        if (frieze != null) {
            frieze.getFriezeFreeMaps().forEach(this::createFreeMapView);
        }
        //
        spatialViewController.setFrieze(frieze);
        peopleViewController.setFrieze(frieze);
    }

    private void reset() {
        updatePeoplePane();
        updatePlacesPane();
        updatePropertiesTab();
    }

    private void updatePropertiesTab() {
        if (frieze != null) {
            nameField.setText(frieze.getName());
            switch (frieze.getTimeFormat()) {
                case LOCAL_TIME -> {
//                    throw new AssertionError();
                    System.err.println("TODO:: updatePropertiesTab -> LOCAL_TIME");
                }
                case TIME_MIN -> {
                    friezeStartTimeField.setText(Double.toString(frieze.getMinDate()));
                    friezeEndTimeField.setText(Double.toString(frieze.getMaxDate()));
                    if (!timeGrid.getChildren().contains(friezeStartTimeField)) {
                        timeGrid.add(friezeStartTimeField, 1, 2);
                    }
                    if (!timeGrid.getChildren().contains(friezeEndTimeField)) {
                        timeGrid.add(friezeEndTimeField, 4, 2);
                    }
                }
                default ->
                    throw new AssertionError();
            }
        } else {
            nameField.setText("");
        }
    }

    private void updatePeoplePane() {
        //TODO remove old listeners
        if (frieze == null) {
            personCheckListView.getItems().clear();
            return;
        }
        var people = frieze.getPersons();
        personCheckListView.getItems().setAll(people);
        // Not Optimal...
        runLater(() -> {
            people.forEach(p -> personCheckListView.getCheckModel().check(p));
            personCheckListView.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends Person> change) -> {
                while (change.next()) {
                    change.getAddedSubList().forEach(p -> frieze.updatePersonSelection(p, true));
                    change.getRemoved().forEach(p -> frieze.updatePersonSelection(p, false));
                }
            });
        });
    }

    private void updatePlacesPane() {
        // TODO remove listeners
        allTreeItems = new LinkedList<>();
        var rootPlaceItem = createRootPlaceItem();
        PlaceFactory.getRootPlaces().forEach(p -> {
            rootPlaceItem.getChildren().add(createTreeItemPlace(p));
            allTreeItems.add(rootPlaceItem);
        });
        placesCheckTreeView.setRoot(rootPlaceItem);
        rootPlaceItem.setExpanded(true);
        //Not Optimal...
        if (frieze != null) {
            var placesInFrieze = frieze.getPlaces();
            allTreeItems.forEach(treeItem -> treeItem.setSelected(placesInFrieze.contains(treeItem.getValue())));
        }
    }

    private void updateStaysPane() {
        if (frieze == null) {
            staysCheckListView.getItems().clear();
            return;
        }
        clearPeopleSelectionB.setDisable(personCheckListView.getSelectionModel().getSelectedItems().isEmpty());
        clearPlaceSelectionB.setDisable(placesCheckTreeView.getSelectionModel().getSelectedItems().isEmpty());

        var staysFiltered = timeLineProject.getStays();
        var selectedPlaces = placesCheckTreeView.getSelectionModel().getSelectedItems().stream().map(item -> item.getValue()).toList();
        if (!selectedPlaces.isEmpty()) {
            staysFiltered = staysFiltered.stream().filter(s -> selectedPlaces.contains(s.getPlace())).toList();
        }
        var selectedPeople = personCheckListView.getSelectionModel().getSelectedItems();
        if (!selectedPeople.isEmpty()) {
            staysFiltered = staysFiltered.stream().filter(s -> selectedPeople.contains(s.getPerson())).toList();
        }
        staysCheckListView.getItems().setAll(staysFiltered);
        frieze.getStayPeriods().forEach(staysCheckListView.getCheckModel()::check);
    }

    private void handleAppInstanceConfigChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case AppInstanceConfiguration.FRIEZE_SELECTED_CHANGED ->
                setFrieze((Frieze) event.getNewValue());
            case AppInstanceConfiguration.TIMELINE_SELECTED_CHANGED -> {
                frieze = null;
                setTimeline(AppInstanceConfiguration.getSelectedProject());
                reset();
            }
            default ->
                throw new AssertionError();
        }
    }

    private CheckBoxTreeItem<Place> createRootPlaceItem() {
        var rootPlace = PlaceFactory.PLACES_PLACE;
        var rootPlaceItem = new CheckBoxTreeItem<>(rootPlace);
        rootPlaceItem.setIndependent(true);
        allTreeItems.add(rootPlaceItem);
        rootPlaceItem.selectedProperty().addListener((ov, t, t1)
                -> {
            if (frieze != null) {
                frieze.updatePlaceSelection(rootPlace, t1);
            }
        });
        return rootPlaceItem;
    }

    private CheckBoxTreeItem<Place> createTreeItemPlace(Place place) {
        var placeItem = new CheckBoxTreeItem<>(place);
        placeItem.setIndependent(true);
        placeItem.setExpanded(true);
        allTreeItems.add(placeItem);
        //
        placeItem.selectedProperty().addListener((ov, t, t1)
                -> {
            if (frieze != null) {
                frieze.updatePlaceSelection(place, t1);
            }
        });
        //
        place.getPlaces().forEach(childPlace -> placeItem.getChildren().add(createTreeItemPlace(childPlace)));
        return placeItem;
    }

    private void createFreeMapView(FriezeFreeMap aFreeMap) {
        if (freemapListView.getItems().contains(aFreeMap)) {
            LOG.log(Level.SEVERE, "View for {0} already created.", new Object[]{aFreeMap});
            return;
        }
        var freeMapView = new FreeMapView(aFreeMap);
        freemapListView.getItems().add(aFreeMap);
        var tab = new Tab(aFreeMap.getName(), freeMapView.getNode());
        tab.setClosable(true);
        tab.setOnClosed(e -> {
            LOG.log(Level.INFO, "Deleted FriezeFreeMap {0} on {1}", new Object[]{aFreeMap.getName(), e});
            deleteFreeMap(aFreeMap);
        });
        aFreeMap.addPropertyChangeListener(e -> {
            if (FriezeFreeMap.NAME_CHANGED.equals(e.getPropertyName())) {
                tab.setText(aFreeMap.getName());
            }
        });
        freemapTabs.put(aFreeMap, tab);
        tabPane.getTabs().add(tab);
    }

    private void deleteFreeMap(FriezeFreeMap aFreeMap) {
        if (frieze != null) {
            frieze.removeFriezeFreeMap(aFreeMap);
        }
        freemapListView.getItems().remove(aFreeMap);
        var tab = freemapTabs.remove(aFreeMap);
        tabPane.getTabs().remove(tab);
    }

    private void createPropertyControls() {
        friezeStartTimeField = new TextField();
        //
        friezeEndTimeField = new TextField();

    }

    private void createSpatialView() {
        try {
            var loader = new FXMLLoader(FriezePlaceViewController.class.getResource("FriezePlaceView.fxml"));
            spatialViewRootPane = loader.load();
            spatialViewController = loader.getController();
            var tab = new Tab("Space", spatialViewRootPane);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while creating spatial view: {0}.", new Object[]{ex});
        }
    }

    private void createPeopleView() {
        try {
            var loader = new FXMLLoader(IFriezeView.class.getResource("/fxml/FriezePeopleView.fxml"));
            peopleViewRootPane = loader.load();
            peopleViewController = loader.getController();
            var tab = new Tab("People", peopleViewRootPane);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while creating people view {0}", new Object[]{ex});
        }
    }

    private void handleProjectChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.PERSON_ADDED, TimeLineProject.PERSON_REMOVED ->
                updatePeoplePane();
            case TimeLineProject.PLACE_ADDED, TimeLineProject.PLACE_REMOVED ->
                updatePlacesPane();
            case TimeLineProject.STAY_ADDED, TimeLineProject.STAY_REMOVED -> {
                // ignored
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
    }

    private void handleFriezeChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case Frieze.PERSON_ADDED, Frieze.PERSON_REMOVED ->
                updatePeoplePane();
            case Frieze.PLACE_ADDED, Frieze.PLACE_REMOVED ->
                updatePlacesPane();
            case Frieze.STAY_ADDED, Frieze.STAY_REMOVED, Frieze.STAY_UPDATED ->
                updateStaysPane();
            case Frieze.DATE_WINDOW_CHANGED -> {
                // TODO
            }
            case Frieze.END_DATE_ADDED, Frieze.END_DATE_REMOVED, Frieze.START_DATE_ADDED, Frieze.START_DATE_REMOVED -> {
                // nothing to be done
            }
            case Frieze.NAME_CHANGED -> {
                // TODO
            }

            default ->
                throw new AssertionError();
        }
    }

    private void handlePlaceCheckTreeItemChanges(PropertyChangeEvent event) {
//        switch(event.getPropertyName()){
//            case CheckBoxTreeItem.checkBoxSelectionChangedEvent().getName():
//                break;
//        }
        System.err.println(event);

    }
}
