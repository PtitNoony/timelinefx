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

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.drawings.IFriezeView;
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
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckTreeView;

public class FriezeViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private CheckListView<Person> personCheckListView;
    @FXML
    private CheckTreeView<Place> placesCheckTreeView;

    @FXML
    private TabPane friezeTabPane;

    @FXML
    private Accordion leftAccordion;
    //
    @FXML
    private TitledPane propertiesPane;
    @FXML
    private TextField nameField;
    @FXML
    private ListView<FriezeFreeMap> freemapListView;
    @FXML
    private Button deleteFreemapButton;
    //
    private Frieze frieze;
    private TimeLineProject project;
    //
    private PropertyChangeListener projectListener;
    private PropertyChangeListener placeCheckTreeItemChangeListener;
    //
    private AnchorPane spatialViewRootPane;
    private AnchorPane peopleViewRootPane;

    private FriezePlaceViewController spatialViewController;
    private FriezePeopleViewController peopleViewController;
    //
    private List<CheckBoxTreeItem<Place>> allTreeItems = new LinkedList<>();
    private Map<FriezeFreeMap, Tab> freemapTabs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        freemapTabs = new HashMap<>();
        createSpatialView();
        createPeopleView();
        projectListener = this::handleProjectChanges;
        placeCheckTreeItemChangeListener = this::handlePlaceCheckTreeItemChanges;
        nameField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (frieze != null) {
                frieze.setName(t1);
                nameField.setText(frieze.getName());
            }
        });
        //
        leftAccordion.setExpandedPane(propertiesPane);
        //
        freemapListView.setEditable(true);
        freemapListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        freemapListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends FriezeFreeMap> ov, FriezeFreeMap t, FriezeFreeMap t1) -> {
            deleteFreemapButton.setDisable(t1 == null);
            if (t1 != null) {
                friezeTabPane.getSelectionModel().select(freemapTabs.get(t1));
            }
        });
        freemapListView.setCellFactory((ListView<FriezeFreeMap> p) -> {
            return new FreeMapListCellImpl(this);
        });
        deleteFreemapButton.setDisable(true);
        //
        friezeTabPane.getSelectionModel().selectedItemProperty().addListener((var ov, var t, var t1) -> {
            if (t1 != null) {
                for (Map.Entry<FriezeFreeMap, Tab> entry : freemapTabs.entrySet()) {
                    if (entry.getValue() == t1) {
                        freemapListView.getSelectionModel().select(entry.getKey());
                        break;
                    }
                }
            }
        });
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

    protected void setFrieze(Frieze aFrieze) {
        //
        frieze = aFrieze;
        if (frieze != null) {
            project = frieze.getProject();
            project.addListener(projectListener);
        }
        updatePropertiesTab();
        updatePersonTab();
        updatePlacesTab();
        //
        spatialViewController.setFrieze(frieze);
        peopleViewController.setFrieze(frieze);
        //
        frieze.getFriezeFreeMaps().forEach(this::createFreeMapView);
    }

    public FriezeFreeMap getSelectedFreeMap() {
        return freemapListView.getSelectionModel().getSelectedItem();
    }

    private void updatePropertiesTab() {
        nameField.setText(frieze.getName());
    }

    private void updatePersonTab() {
        List<Person> persons = frieze.getPersons();
        //TODO remove old listeners
        personCheckListView.getItems().setAll(PersonFactory.getPERSONS());
        // Not Optimal...
        persons.forEach(p -> personCheckListView.getCheckModel().check(p));
        personCheckListView.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends Person> change) -> {
            while (change.next()) {
                change.getAddedSubList().forEach(p -> frieze.updatePersonSelection(p, true));
                change.getRemoved().forEach(p -> frieze.updatePersonSelection(p, false));
            }
        });
    }

    private void updatePlacesTab() {
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
        List<Place> placesInFrieze = frieze.getPlaces();
        allTreeItems.forEach(treeItem -> treeItem.setSelected(placesInFrieze.contains(treeItem.getValue())));
    }

    private CheckBoxTreeItem<Place> createRootPlaceItem() {
        var rootPlace = PlaceFactory.createPlace("Universe", PlaceLevel.UNIVERSE, null);
        var rootPlaceItem = new CheckBoxTreeItem<>(rootPlace);
        rootPlaceItem.setIndependent(true);
        allTreeItems.add(rootPlaceItem);
        rootPlaceItem.selectedProperty().addListener((ov, t, t1) -> {
            frieze.updatePlaceSelection(rootPlace, t1);
        });
        return rootPlaceItem;
    }

    private CheckBoxTreeItem<Place> createTreeItemPlace(Place place) {
        var placeItem = new CheckBoxTreeItem<>(place);
        placeItem.setIndependent(true);
        placeItem.setExpanded(true);
        allTreeItems.add(placeItem);
        //
        placeItem.selectedProperty().addListener((ov, t, t1) -> {
            frieze.updatePlaceSelection(place, t1);
        });
        //
        place.getPlaces().forEach(childPlace -> placeItem.getChildren().add(createTreeItemPlace(childPlace)));
        return placeItem;
    }

    private void createSpatialView() {
        try {
            var loader = new FXMLLoader(FriezePlaceViewController.class.getResource("FriezePlaceView.fxml"));
            spatialViewRootPane = loader.load();
            spatialViewController = loader.getController();
            var tab = new Tab("Space", spatialViewRootPane);
            tab.setClosable(false);
            friezeTabPane.getTabs().add(tab);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
    }

    private void createPeopleView() {
        try {
            var loader = new FXMLLoader(IFriezeView.class.getResource("/fxml/FriezePeopleView.fxml"));
            peopleViewRootPane = loader.load();
            peopleViewController = loader.getController();
            var tab = new Tab("People", peopleViewRootPane);
            tab.setClosable(false);
            friezeTabPane.getTabs().add(tab);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
    }

    private void createFreeMapView(FriezeFreeMap aFreeMap) {
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
        friezeTabPane.getTabs().add(tab);
    }

    private void deleteFreeMap(FriezeFreeMap aFreeMap) {
        frieze.removeFriezeFreeMap(aFreeMap);
        freemapListView.getItems().remove(aFreeMap);
        var tab = freemapTabs.remove(aFreeMap);
        friezeTabPane.getTabs().remove(tab);
    }

    private void handleProjectChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.PERSON_ADDED, TimeLineProject.PERSON_REMOVED ->
                updatePersonTab();
            case TimeLineProject.PLACE_ADDED, TimeLineProject.PLACE_REMOVED ->
                updatePlacesTab();
            case TimeLineProject.STAY_ADDED, TimeLineProject.STAY_REMOVED -> {
                // ignored
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
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
