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

import com.github.noony.app.timelinefx.core.IDateObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.PictureInfo;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.utils.MetadataParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.CheckListView;

/**
 *
 * @author arnaud
 */
public class PictureLoaderViewController implements Initializable, ViewController {

    public static final String CANCEL_EVENT = "cancel";
    public static final String OK_EVENT = "ok";
    public static final String FILE_REQUEST_EVENT = "fileChooserRequest";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(PictureLoaderViewController.this);

    @FXML
    private TextField fileField;
    @FXML
    private TextField pictureNameField;
    @FXML
    private ChoiceBox<TimeFormat> timeformatCB;
    @FXML
    private TextField pictureDateField;
    @FXML
    private CheckListView<Person> peopleCheckListView;
    @FXML
    private CheckListView<Place> placesCheckListView;
    @FXML
    private ImageView imageView;

    @FXML
    private Button okButton;

    private EditionMode editionMode;

    private TimeLineProject project = null;
    private File pictureFile = null;
    private String pictureName = null;

    private Picture picture = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.log(Level.INFO, "Loading PictureLoaderViewController");
        peopleCheckListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        placesCheckListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        timeformatCB.setItems(FXCollections.observableArrayList(TimeFormat.values()));
        editionMode = EditionMode.CREATION;
        reset();
    }

    public void reset() {
        // TODO manage order and selection
        PersonFactory.getPERSONS().stream()
                .filter(p -> !peopleCheckListView.getItems().contains(p))
                .forEach(p -> peopleCheckListView.getItems().add(p));
        //
        PlaceFactory.getPlaces().stream()
                .filter(p -> !placesCheckListView.getItems().contains(p))
                .forEach(p -> placesCheckListView.getItems().add(p));
        //
        peopleCheckListView.getCheckModel().clearChecks();
        placesCheckListView.getCheckModel().clearChecks();
        // Todo; create a dummy Picture
        project = null;
        pictureFile = null;
        pictureName = null;
        picture = null;
        fileField.setText("");
        pictureNameField.setText("");
        updateActions();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @FXML
    protected void handleFileAction(ActionEvent event) {
        propertyChangeSupport.firePropertyChange(FILE_REQUEST_EVENT, null, this);
    }

    @FXML
    protected void handleCancelAction(ActionEvent event) {
        propertyChangeSupport.firePropertyChange(CANCEL_EVENT, null, this);
    }

    @FXML
    protected void handleOkAction(ActionEvent event) {
        switch (editionMode) {
            case CREATION ->
                createPicture();
            case EDITION ->
                applyChanges();
            default ->
                throw new UnsupportedOperationException();
        }
        //
        propertyChangeSupport.firePropertyChange(OK_EVENT, this, picture);
    }

    protected void setMode(EditionMode mode) {
        editionMode = mode;
    }

    protected void setProject(TimeLineProject aProject) {
        project = aProject;
    }

    protected void setPicture(Picture aPicture) {
        reset();
        picture = aPicture;
        project = aPicture.getProject();
        picture.getPersons().forEach(person -> peopleCheckListView.getCheckModel().check(person));
        picture.getPlaces().forEach(place -> placesCheckListView.getCheckModel().check(place));
        pictureFile = new File(picture.getProjectRelativePath());
        pictureName = pictureFile.getName();
        fileField.setText(pictureFile.getAbsolutePath());
        pictureDateField.setText(picture.getAbsoluteTimeAsString());
        pictureNameField.setText(pictureName);
        updateActions();
    }

    protected void setFile(File file) {
        if (file != null) {
            pictureFile = file;
            PictureInfo info = MetadataParser.parseMetadata(project, file);
            pictureNameField.setText(pictureFile.getName());
            pictureDateField.setText(info.getCreationDate().format(IDateObject.DEFAULT_DATE_TIME_FORMATTER));
            fileField.setText(pictureFile.getAbsolutePath());
            updateActions();
            displayImage();
        }
    }

    private void createPicture() {
        pictureFile = new File(fileField.getText());
        pictureName = pictureNameField.getText();
        //
        picture = PictureFactory.createPicture(project, pictureFile, pictureName);
        peopleCheckListView.getCheckModel().getCheckedItems().forEach(p -> picture.addPerson(p));
        placesCheckListView.getCheckModel().getCheckedItems().forEach(p -> picture.addPlace(p));
        switch (timeformatCB.getSelectionModel().getSelectedItem()) {
            case LOCAL_TIME -> {
                try {
                    var localDate = LocalDate.parse(pictureDateField.getText(), IDateObject.DEFAULT_DATE_TIME_FORMATTER);
                    picture.setDate(localDate);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Could not parse date :: {0} because of {1}", new Object[]{pictureDateField.getText(), e.getMessage()});
                    picture.setDate(LocalDate.now());
                }
            }
            case TIME_MIN -> {
                try {
                    var localTimestamp = Long.parseLong(pictureDateField.getText().trim());
                    picture.setTimestamp(localTimestamp);
                } catch (NumberFormatException e) {
                    LOG.log(Level.SEVERE, "Could not parse timestamp :: {0} because of {1}", new Object[]{pictureDateField.getText(), e.getMessage()});
                    picture.setTimestamp(0);
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeformatCB.getSelectionModel().getSelectedItem());
        }
    }

    private void applyChanges() {
        ObservableList<Person> checkedPersons = peopleCheckListView.getCheckModel().getCheckedItems();
        checkedPersons.forEach(p -> picture.addPerson(p));
        List<Person> personsToRemove = picture.getPersons().stream().filter(p -> !checkedPersons.contains(p)).collect(Collectors.toList());
        personsToRemove.forEach(p -> picture.removePerson(p));
        placesCheckListView.getCheckModel().getCheckedItems().forEach(p -> picture.addPlace(p));
        picture.setName(pictureNameField.getText());
        switch (timeformatCB.getSelectionModel().getSelectedItem()) {
            case LOCAL_TIME -> {
                try {
                    var localDate = LocalDate.parse(pictureDateField.getText(), IDateObject.DEFAULT_DATE_TIME_FORMATTER);
                    picture.setDate(localDate);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Could not parse date :: {0} because of {1}", new Object[]{pictureDateField.getText(), e.getMessage()});
                    picture.setDate(LocalDate.now());
                }
            }
            case TIME_MIN -> {
                try {
                    var localTimestamp = Long.parseLong(pictureDateField.getText().trim());
                    picture.setTimestamp(localTimestamp);
                } catch (NumberFormatException e) {
                    LOG.log(Level.SEVERE, "Could not parse timestamp :: {0} because of {1}", new Object[]{pictureDateField.getText(), e.getMessage()});
                    picture.setTimestamp(0);
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeformatCB.getSelectionModel().getSelectedItem());
        }
    }

    private void updateActions() {
        var ready = pictureFile != null & pictureNameField.getText().trim().length() > 0;
        okButton.setDisable(!ready);
    }

    private void displayImage() {
        String localUrl;
        try {
            localUrl = pictureFile.toURI().toURL().toString();
            Image image = new Image(localUrl);
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Error while loading picture {0} : {1}", new Object[]{pictureFile, ex});
        }
    }
}
