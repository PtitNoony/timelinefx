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
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static javafx.application.Platform.runLater;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author arnaud
 */
public class GalleryViewController implements Initializable, ViewController {

    // TODO find a place to store that
    public static final double MARGN = 8.0;

    public static final String DISPLAY_PICTURE_LOADER = "displayPictureLoader";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(GalleryViewController.this);

    private TimeLineProject project = null;

    private Parent pictureLoaderView = null;
    private PictureLoaderViewController pictureLoaderController = null;

    @FXML
    private TableView<Picture> picturesTableView;
    @FXML
    private TableColumn<Picture, String> pictureNameColumn, pictureDateColumn, picturePeopleColumn, picturePlacesColumn;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private ImageView imageView;

    @FXML
    private CheckBox displayBordersCB;
    @FXML
    private Button personUpButton, personDownButton;
    @FXML
    private ListView<Person> picturePersonsList;

    private Picture currentPicture;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.log(Level.INFO, "Loading GalleryViewController");
        PictureFactory.addPropertyChangeListener(this::handlePictureFactoryChanges);
        init();
    }

    @FXML
    protected void handleAddPicture(ActionEvent event) {
        LOG.log(Level.INFO, "handleAddPicture on event {0}", new Object[]{event});
        displayPictureLoaderView(EditionMode.CREATION);
    }

    @FXML
    protected void handleEditPicture(ActionEvent event) {
        LOG.log(Level.INFO, "handleEditPicture on event {0}", new Object[]{event});
        displayPictureLoaderView(EditionMode.EDITION);
    }

    @FXML
    protected void handleUpPerson(ActionEvent event) {
        LOG.log(Level.INFO, "handleUpPerson on event {0}", new Object[]{event});
        currentPicture.movePersonUp(picturePersonsList.getSelectionModel().getSelectedItem());
        picturePersonsList.setItems(FXCollections.observableArrayList(currentPicture.getPersons()));
    }

    @FXML
    protected void handleDownPerson(ActionEvent event) {
        LOG.log(Level.INFO, "handleDownPerson on event {0}", new Object[]{event});
        currentPicture.movePersonDown(picturePersonsList.getSelectionModel().getSelectedItem());
        picturePersonsList.setItems(FXCollections.observableArrayList(currentPicture.getPersons()));
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void setProject(TimeLineProject aProject) {
        project = aProject;
        pictureLoaderController.setProject(project);
    }

    protected void reset() {
//        friezeTabPane.getTabs().clear();
    }

    protected void update() {
        //
    }

    private void init() {
        pictureNameColumn.setPrefWidth(250);
        pictureNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Picture, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getName()));
        //
        pictureDateColumn.setPrefWidth(100);
        pictureDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<Picture, String> param) -> new ReadOnlyStringWrapper("" + param.getValue().getAbsoluteTimeAsString()));
        //
        picturePeopleColumn.setPrefWidth(150);
        picturePeopleColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Picture, String> param)
                -> new ReadOnlyStringWrapper("" + param.getValue().getPersons().stream().map(Person::getName).collect(Collectors.joining(";"))));
        //
        picturePlacesColumn.setPrefWidth(150);
        picturePlacesColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Picture, String> param)
                -> new ReadOnlyStringWrapper("" + param.getValue().getPlaces().stream().map(Place::getName).collect(Collectors.joining(";"))));
        //
        picturesTableView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Picture> ov, Picture t, Picture t1) -> {
            if (t1 != null) {
                currentPicture = t1;
                displayImage(currentPicture);
                displayPictureInfos(currentPicture);
            }
        });
        //
        rightPane.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            imageView.setFitWidth(t1.intValue() - 2 * MARGN);
        });
        rightPane.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            imageView.setFitHeight(t1.intValue() - 2 * MARGN);
        });
        //
        picturesTableView.getItems().addAll(PictureFactory.getPictures());
        //
        picturePersonsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        picturePersonsList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> ov, Person t, Person t1) -> {
            personUpButton.setDisable(t1 == null);
            personDownButton.setDisable(t1 == null);

        });
    }

    private void displayPictureLoaderView(EditionMode mode) {
        if (pictureLoaderView == null) {
            loadPictureLoaderView();
        }
        pictureLoaderController.setMode(mode);
        if (currentPicture != null) {
            pictureLoaderController.setPicture(currentPicture);
        }
        propertyChangeSupport.firePropertyChange(DISPLAY_PICTURE_LOADER, pictureLoaderController, pictureLoaderView);
    }

    private void loadPictureLoaderView() {
        var loader = new FXMLLoader(PlaceCreationViewController.class.getResource("PictureLoaderView.fxml"));
        try {
            pictureLoaderView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load PictureLoaderView ::  {0}", new Object[]{ex});
        }
        pictureLoaderController = loader.getController();
        pictureLoaderController.addPropertyChangeListener(this::handlePictureLoaderControllerChanges);
    }

    private void handlePictureLoaderControllerChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureLoaderViewController.OK_EVENT:
                final Picture newPicture = (Picture) event.getNewValue();
                runLater(() -> {
                    if (!picturesTableView.getItems().contains(newPicture)) {
                        picturesTableView.getItems().add(newPicture);
                    } else {
                        picturesTableView.refresh();
                    }
                });
                break;
        }
    }

    //TODO factorize
    private void displayImage(Picture picture) {
        String localUrl;
        try {
            var pictureFile = new File(CustomFileUtils.fromProjectRelativeToAbsolute(picture.getProject(), picture.getProjectRelativePath()));
            localUrl = pictureFile.toURI().toURL().toString();
            Image image = new Image(localUrl);
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Error while loading picture {0} : {1}", new Object[]{picture, ex});
        }
    }

    private void displayPictureInfos(Picture picture) {
        picturePersonsList.setItems(FXCollections.observableArrayList(picture.getPersons()));
    }

    private void handlePictureFactoryChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureFactory.PICTURE_ADDED -> {
                var picture = (Picture) event.getNewValue();
                if (!picturesTableView.getItems().contains(picture)) {
                    picturesTableView.getItems().add(picture);
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
        }
    }
}
