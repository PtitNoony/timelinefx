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

import com.github.noony.app.timelinefx.Configuration;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Portrait;
import com.github.noony.app.timelinefx.core.PortraitFactory;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.drawings.GalleryTiles;
import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 *
 * @author hamon
 */
public class PersonCreationViewController implements Initializable {

    public static final String CANCEL_PERSON_CREATION = "cancelPersonCreation";
    public static final String PERSON_CREATED = "personCreated";
    public static final String PERSON_EDITIED = "personEdited";

    private static final Logger LOG = Logger.getGlobal();

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(PersonCreationViewController.this);

    @FXML
    private GridPane propertiesGrid;
    @FXML
    private TextField nameField;
    @FXML
    private Button createButton;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ImageView imageView;
    @FXML
    private ChoiceBox<TimeFormat> timeFormatCB;
    @FXML
    private ChoiceBox<Portrait> defaultPortraitCB;
    @FXML
    private ScrollPane galleryScrollPane;
    @FXML
    private Button addPortraitButton, removePortraitButton;
    @FXML
    private HBox portraitTimeHB;
    //
    private DatePicker birthDatePicker, deathDatePicker, portraitDatePicker;
    private TextField birthTimeField, deathTimeField, portraitTimeField;
    //
    private Image image;
    private GalleryTiles galleryTiles;
    private FileChooser fileChooser;
    //
    private EditionMode editionMode;
    //
    private TimeLineProject currentProject = null;
    private Person currentEditedPerson = null;
    private List<Portrait> currentPortaitsList = null;
    //
    private String personName = "";
    private Portrait defaultPortrait = null;
    private Color personColor = null;
    private LocalDate dateOfBirth = null;
    private LocalDate dateOfDeath = null;
    //
    private Portrait portraitSelected = null;
    private Map<Portrait, LocalDate> updatedPortraitDates;
    private Map<Portrait, String> updatedPortraitTimes;

    //
    private boolean nameOK = false;
    private boolean colorOK = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        galleryTiles = new GalleryTiles();
        fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpeg", "*.jpg"));
        birthDatePicker = new DatePicker();
        deathDatePicker = new DatePicker();
        portraitDatePicker = new DatePicker();
        birthTimeField = new TextField();
        deathTimeField = new TextField();
        portraitTimeField = new TextField();
        galleryScrollPane.setContent(galleryTiles.getNode());
        galleryTiles.addPropertyChangeListener(this::handleGalleryTilesChanges);
        // TODO ? check if size is OK
        //
        reset();
        //
        nameField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            updatePersonName(t1.trim());
        });
        timeFormatCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TimeFormat> ov, TimeFormat t, TimeFormat t1) -> {
            if (t1 != null) {
                setTimeFormat(t1);
            }
        });
        defaultPortraitCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Portrait> ov, Portrait t, Portrait t1) -> {
            if (t1 != null) {
                defaultPortrait = t1;
                updateImage();
            }
        });
        colorPicker.valueProperty().addListener((ObservableValue<? extends Color> ov, Color t, Color t1) -> {
            updateColor(t1);
        });
        birthDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            dateOfBirth = t1;
            updateStatus();
        });
        deathDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            dateOfDeath = t1;
            updateStatus();
        });
        // Updating the temporary values for the portraits dates/times
        portraitDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            updatedPortraitDates.put(portraitSelected, t1);
        });
        portraitTimeField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            updatedPortraitTimes.put(portraitSelected, t1);
        });
    }

    @FXML
    protected void handlePortraitAddAction(ActionEvent event) {
        var projectFolder = currentProject.getProjectFolder();
        File initialDirectory;
        if (projectFolder == null) {
            initialDirectory = new File(Configuration.getProjectsParentFolder());
        } else {
            initialDirectory = currentProject.getProjectFolder();
        }
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (selectedFile != null) {
            var newPortrait = PortraitFactory.createPortrait(currentEditedPerson, CustomFileUtils.fromAbsoluteToProjectRelative(currentProject, selectedFile));
            currentPortaitsList.add(newPortrait);
            updatePortraitCB();
            galleryTiles.addFileObject(newPortrait);
        }
    }

    @FXML
    protected void handlePortraitRemoveAction(ActionEvent event) {
        currentPortaitsList.remove(portraitSelected);
        galleryTiles.removeFileObject(portraitSelected);
        updatePortraitCB();
    }

    @FXML
    protected void handleCancelAction(ActionEvent event) {
        propertyChangeSupport.firePropertyChange(CANCEL_PERSON_CREATION, null, null);
    }

    @FXML
    protected void handleCreateAction(ActionEvent event) {
        switch (editionMode) {
            case CREATION -> {
                updatePerson(currentEditedPerson);
                propertyChangeSupport.firePropertyChange(PERSON_CREATED, null, currentEditedPerson);
            }
            case EDITION -> {
                updatePerson(currentEditedPerson);
                propertyChangeSupport.firePropertyChange(PERSON_EDITIED, null, currentEditedPerson);
                reset();
            }
            default ->
                throw new UnsupportedOperationException("handleCreateAction in mode " + editionMode);
        }
    }

    protected void setTimelineProject(TimeLineProject aProject) {
        currentProject = aProject;
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected void setPerson(Person aPerson) {
        currentEditedPerson = aPerson;
        currentPortaitsList.clear();
        currentPortaitsList.addAll(currentEditedPerson.getPortraits());
        var portraitsCollection = FXCollections.observableArrayList(currentPortaitsList);
        defaultPortraitCB.setItems(portraitsCollection);
        defaultPortrait = currentEditedPerson.getDefaultPortrait();
        portraitsCollection.forEach(galleryTiles::addFileObject);
        if (defaultPortrait != null) {
            defaultPortraitCB.getSelectionModel().select(defaultPortrait);
        }
        nameField.setText(currentEditedPerson.getName());
        colorPicker.setValue(currentEditedPerson.getColor());
        timeFormatCB.getSelectionModel().select(currentEditedPerson.getTimeFormat());
        switch (currentEditedPerson.getTimeFormat()) {
            case LOCAL_TIME -> {
                birthDatePicker.setValue(currentEditedPerson.getDateOfBirth());
            }
            case TIME_MIN -> {
                birthTimeField.setText(Long.toString(currentEditedPerson.getTimeOfBirth()));
                deathTimeField.setText(Long.toString(currentEditedPerson.getTimeOfDeath()));
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + currentEditedPerson.getTimeFormat());

        }
        updateStatus();
    }

    protected final void reset() {
        currentEditedPerson = null;
        updatedPortraitDates = new HashMap<>();
        updatedPortraitTimes = new HashMap<>();
        //
        nameOK = false;
        colorOK = false;
        //
        personColor = null;
        nameField.setText("");
        defaultPortrait = null;
        defaultPortraitCB.setItems(FXCollections.emptyObservableList());
        galleryTiles.removeAllFileObjects();
        //
        setTimeFormat(TimeFormat.LOCAL_TIME);
        colorPicker.setValue(null);
        timeFormatCB.setItems(FXCollections.observableArrayList(TimeFormat.values()));
        timeFormatCB.setValue(TimeFormat.LOCAL_TIME);
        birthDatePicker.setValue(null);
        deathDatePicker.setValue(null);
//        setEditionMode(EditionMode.CREATION);
        //
        currentPortaitsList = new LinkedList<>();
        addPortraitButton.setDisable(false);
        removePortraitButton.setDisable(true);
        portraitTimeHB.setDisable(true);
        portraitDatePicker.setDisable(true);
        portraitTimeField.setDisable(true);
        portraitDatePicker.setValue(null);
        portraitTimeField.setText("");
        //
        updateStatus();
    }

    protected void setEditionMode(EditionMode mode) {
        editionMode = mode;
        switch (editionMode) {
            case CREATION -> {
                createButton.setText("Create");
                // TODO maybe refactor method and group them
                setPerson(PersonFactory.createPerson(currentProject, "NoName"));
            }
            case EDITION ->
                createButton.setText("Validate");
            default ->
                throw new UnsupportedOperationException("Unsupported edition mode " + editionMode);
        }
    }

    private void updatePersonName(String name) {
        personName = name;
        nameOK = !personName.isBlank() && !personName.isEmpty();
        updateStatus();
    }

    private void updateColor(Color aColor) {
        personColor = aColor;
        colorOK = personColor != null;
        updateStatus();
    }

    private void setTimeFormat(TimeFormat timeFormat) {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                propertiesGrid.getChildren().remove(birthTimeField);
                propertiesGrid.getChildren().remove(deathTimeField);
                portraitTimeHB.getChildren().remove(portraitTimeField);
                updatedPortraitTimes.clear();
                if (!propertiesGrid.getChildren().contains(birthDatePicker)) {
                    propertiesGrid.add(birthDatePicker, 1, 3);
                    propertiesGrid.add(deathDatePicker, 1, 4);
                    portraitTimeHB.getChildren().add(portraitDatePicker);
                }
            }
            case TIME_MIN -> {
                propertiesGrid.getChildren().remove(birthDatePicker);
                propertiesGrid.getChildren().remove(deathDatePicker);
                portraitTimeHB.getChildren().remove(portraitDatePicker);
                updatedPortraitDates.clear();
                if (!propertiesGrid.getChildren().contains(birthTimeField)) {
                    propertiesGrid.add(birthTimeField, 1, 3);
                    propertiesGrid.add(deathTimeField, 1, 4);
                    portraitTimeHB.getChildren().add(portraitTimeField);
                }
            }
        }
        if (timeFormat != timeFormatCB.getValue()) {
            timeFormatCB.setValue(timeFormat);
        }
    }

    private void updatePortraitCB() {
        defaultPortraitCB.setItems(FXCollections.observableArrayList(currentPortaitsList));
        if (portraitSelected == defaultPortrait) {
            if (currentPortaitsList.isEmpty()) {
                defaultPortrait = null;
                defaultPortraitCB.getSelectionModel().clearSelection();
            } else {
                defaultPortrait = currentPortaitsList.get(0);
                defaultPortraitCB.getSelectionModel().select(defaultPortrait);
            }
        }
        portraitSelected = null;
    }

    private void updateStatus() {
        // we are not forcing birth and death dates to be set
        createButton.setDisable(!(nameOK && colorOK));
        updateImage();
    }

    private void updateImage() {
        if (defaultPortrait != null) {
            String picturePath = currentProject.getProjectFolder().getAbsolutePath() + File.separatorChar + defaultPortrait.getProjectRelativePath();
            File pictureFile = new File(picturePath);
            if (Files.exists(pictureFile.toPath())) {
                FileInputStream inputstream;
                try {
                    inputstream = new FileInputStream(picturePath);
                    image = new Image(inputstream);
                    imageView.setImage(image);
                } catch (FileNotFoundException ex) {
                    LOG.log(Level.SEVERE, "Exception while updating image for {0} : {1}", new Object[]{defaultPortrait, ex});
                    LOG.log(Level.SEVERE, "> file name was: {0}", new Object[]{picturePath});
                }
            }
        } else if (currentProject != null) {
            var path = currentProject.getPortraitsAbsoluteFolder().getAbsolutePath() + File.separatorChar + Person.DEFAULT_PICTURE_NAME;
            try {
                image = new Image(path);
                imageView.setImage(image);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception while updating image foPicturer {0} : {1}", new Object[]{defaultPortrait, e});
                LOG.log(Level.SEVERE, "> file name was: {0}", new Object[]{path});
            }
        }
    }

    private void updatePerson(Person person) {
        person.setName(personName);
        LinkedList<Portrait> existingPortraits = new LinkedList<>();
        existingPortraits.addAll(person.getPortraits());
        currentPortaitsList.forEach(person::addPortrait);
        existingPortraits.stream().filter(p -> !currentPortaitsList.contains(p)).forEach(person::removePortrait);
        person.setDefaultPortrait(defaultPortrait);
        person.setColor(personColor);
        var timeFormat = timeFormatCB.getSelectionModel().getSelectedItem();
        person.setTimeFormat(timeFormat);
        switch (timeFormat) {
            case LOCAL_TIME -> {
                person.setDateOfBirth(dateOfBirth);
                person.setDateOfDeath(dateOfDeath);
                updatedPortraitDates.forEach((portrait, date) -> portrait.setDate(date));
            }
            case TIME_MIN -> {
                try {
                    person.setTimeOfBirth(Long.parseLong(birthTimeField.getText().trim()));
                } catch (NumberFormatException e) {
                    LOG.log(Level.INFO, "Birth time for {0} is not a valid timestamp: {1} :: {2}", new Object[]{person, birthTimeField.getText(), e.getMessage()});
                    person.setTimeOfBirth(Portrait.DEFAULT_TIMESTAMP);
                }
                try {
                    person.setTimeOfDeath(Long.parseLong(deathTimeField.getText().trim()));
                } catch (NumberFormatException e) {
                    LOG.log(Level.INFO, "Death time for {0} is not a valid timestamp: {1} :: {2}", new Object[]{person, deathTimeField.getText(), e.getMessage()});
                    person.setTimeOfDeath(Portrait.DEFAULT_TIMESTAMP);
                }
                updatedPortraitTimes.forEach((portrait, date) -> {
                    try {
                        portrait.setTimestamp(Long.parseLong(date.trim()));
                    } catch (NumberFormatException e) {
                        LOG.log(Level.INFO, "Time for {0} is not a valid timestamp: {1} :: {2}", new Object[]{portrait, date, e.getMessage()});
                        portrait.setTimestamp(Portrait.DEFAULT_TIMESTAMP);
                    }
                });
            }
            default ->
                throw new UnsupportedOperationException("Unsupported time format: " + timeFormat);
        }
    }

    private void handleGalleryTilesChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryTiles.TILE_CLICKED -> {
                var portrait = (Portrait) event.getNewValue();
                defaultPortraitCB.getSelectionModel().select(portrait);
            }
            case GalleryTiles.TILE_SELECTED -> {
                var tile = event.getNewValue();
                if (tile != null) {
                    portraitSelected = (Portrait) event.getOldValue();
                    removePortraitButton.setDisable(false);
                    portraitTimeHB.setDisable(false);
                    portraitDatePicker.setDisable(false);
                    portraitTimeField.setDisable(false);
                    switch (timeFormatCB.getSelectionModel().getSelectedItem()) {
                        case LOCAL_TIME -> {
                            var portraitDate = updatedPortraitDates.get(portraitSelected);
                            if (portraitDate == null) {
                                portraitDate = portraitSelected.getDate();
                            }
                            if (portraitDate != null) {
                                portraitDatePicker.setValue(portraitDate);
                            } else {
                                portraitDatePicker.setValue(null);
                            }
                            portraitTimeField.setText("");
                        }
                        case TIME_MIN -> {
                            var portraitTime = updatedPortraitTimes.get(portraitSelected);
                            if (portraitTime != null) {
                                portraitTimeField.setText(portraitTime);
                            } else if (portraitSelected.getTimestamp() != Portrait.DEFAULT_TIMESTAMP) {
                                portraitTimeField.setText(MathUtils.doubleToString(portraitSelected.getTimestamp()));
                            } else {
                                portraitTimeField.setText("");
                            }
                            portraitDatePicker.setValue(LocalDate.now());
                        }
                        default ->
                            throw new UnsupportedOperationException("Unsupported time mode : " + event.getPropertyName());
                    }
                } else {
                    removePortraitButton.setDisable(true);
                    portraitTimeHB.setDisable(true);
                    portraitDatePicker.setDisable(true);
                    portraitTimeField.setDisable(true);
                    portraitDatePicker.setValue(LocalDate.MIN);
                    portraitTimeField.setText("");
                }
            }
            default ->
                throw new UnsupportedOperationException("While handleGalleryTilesChanges :: " + event.getPropertyName());
        }
    }
}
