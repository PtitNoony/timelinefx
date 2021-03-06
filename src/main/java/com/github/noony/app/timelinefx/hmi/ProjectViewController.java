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
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.examples.StarWars;
import com.github.noony.app.timelinefx.examples.TestExample;
import com.github.noony.app.timelinefx.save.XMLHandler;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;

/**
 *
 * @author hamon
 */
public final class ProjectViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    private enum ACTION_ON_HOLD {
        NEW_PROJECT, NONE
    }

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private HBox toolbarHBox;

    @FXML
    private RadioMenuItem contentViewMI;
    @FXML
    private RadioMenuItem timelineViewMI;
    @FXML
    private RadioMenuItem galleryViewMI;
    @FXML
    private RadioMenuItem picturesChronologyViewMI;

    private TimeLineProject timeLineProject;
    private FileChooser fileChooser;
    //
    private Stage modalStage;
    private Scene modalScene;
    //
    private Parent contentEditionView = null;
    private Parent timelineView = null;
    private Parent galleryView = null;
    private Parent configurationView = null;
    private Parent projectCreationWizardView = null;
    private Parent pictureLoaderView = null;
    private Parent picturesChronologyView = null;
    //
    private ContentEditionViewController contentController = null;
    private TimelineViewController timelineController = null;
    private GalleryViewController galleryController = null;
    private ConfigurationViewController configurationController = null;
    private ProjectCreationWizardController projectCreationWizardController = null;
    private PictureLoaderViewController pictureLoaderViewController = null;
    private PicturesChronologyViewController pictureChronologyViewController = null;
    //
    private SaveWindow saveWindow = null;
    // toolbarToggleGroup does not seem to work : TODO: fix
    private ToggleGroup toolbarToggleGroup;
    private ToggleGroup viewToggleGroup;
    private ACTION_ON_HOLD actionOnHold = ACTION_ON_HOLD.NONE;
    //
    private ToggleButton contentEditionToggle;
    private ToggleButton timeLineToggle;
    private ToggleButton galleryToggle;
    private ToggleButton picturesChronologyToggle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.log(Level.INFO, "Loading ProjectViewController");
        fileChooser = new FileChooser();
        //
        createToolbar();
        //
        loadContentEditionView();
        loadTimelineView();
        //
        viewToggleGroup = new ToggleGroup();
        contentViewMI.setToggleGroup(viewToggleGroup);
        timelineViewMI.setToggleGroup(viewToggleGroup);
        galleryViewMI.setToggleGroup(viewToggleGroup);
        picturesChronologyViewMI.setToggleGroup(viewToggleGroup);
        //
        toolbarToggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> {
            if (t1 == contentEditionToggle) {
                displayContentEditionView();
            } else if (t1 == timeLineToggle) {
                displayTimelineView();
            } else if (t1 == galleryToggle) {
                displayGalleryView();
            } else if (t1 == picturesChronologyToggle) {
                displayEventChronologyView();
            } else {
//                throw new UnsupportedOperationException("View not supported, action on " + t1);
            }
        });
        //
        viewToggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> {
            if (t1 == contentViewMI) {
                displayContentEditionView();
            } else if (t1 == timelineViewMI) {
                displayTimelineView();
            } else if (t1 == galleryViewMI) {
                displayGalleryView();
            } else if (t1 == picturesChronologyViewMI) {
                displayEventChronologyView();
            } else {
                throw new UnsupportedOperationException("View not supported, action on " + t1);
            }
        });
        //
        StageFactory.reApplyTheme();
        //
//        contentViewMI.setSelected(true);
        displayContentEditionView();
    }

    @FXML
    protected void handleConfigurationAction(ActionEvent event) {
        if (configurationView == null) {
            loadConfigurationView();
        }
        showModalStage(configurationView);
    }

    @FXML
    protected void handleNewProject(ActionEvent event) {
        LOG.log(Level.INFO, "handleNewProject {0}", event);
        //
        actionOnHold = ACTION_ON_HOLD.NEW_PROJECT;
        if (timeLineProject != null) {
            showSaveWindow();
        } else {
            executeActionOnHold();
        }
    }

    @FXML
    protected void handleProjectSave(ActionEvent event) {
        LOG.log(Level.INFO, "handleProjectSave {0}", event);
        var targetFile = timeLineProject.getTimelineFile();
        if (targetFile != null) {
            LOG.log(Level.INFO, "> Saving to file {0}", targetFile);
            XMLHandler.save(timeLineProject, timeLineProject.getTimelineFile());
        } else {
            LOG.log(Level.SEVERE, "> Could not save file : project file not set");
        }
    }

    @FXML
    protected void handleProjectLoad(ActionEvent event) {
        LOG.log(Level.INFO, "handleProjectLoad {0}", event);
        var initialFolder = new File(Configuration.getProjectsParentFolder());
        fileChooser.setInitialDirectory(initialFolder);
        var inputFile = fileChooser.showOpenDialog(mainAnchorPane.getScene().getWindow());
        if (inputFile != null) {
            LOG.log(Level.INFO, "Loading project {0}", inputFile);
            TimeLineProject timeline = TimeLineProjectFactory.loadProject(inputFile);
            if (timeline != null) {
                loadProject(timeline);
            } else {
                LOG.log(Level.SEVERE, "Could not load project {0}", inputFile);
            }
        }
    }

    @FXML
    protected void handleFriezeCreation(ActionEvent event) {
        LOG.log(Level.INFO, "handleFriezeCreation {0}", event);
        var frieze = FriezeFactory.createFrieze(timeLineProject, "New Frieze", timeLineProject.getStays());
        System.err.println("TODO handle freize creation");
        // TODO use a property change instead ?
        timelineController.loadFreize(frieze);
    }

    @FXML
    protected void handleCreateBasicExample(ActionEvent event) {
        LOG.log(Level.INFO, "handleCreateBasicExample {0}", event);
        loadProject(TestExample.createExample());
    }

    @FXML
    protected void handleCreateStarWars(ActionEvent event) {
        LOG.log(Level.INFO, "handleCreateStarWars {0}", event);
        loadProject(StarWars.createStartWars());
    }

    public void loadProject(TimeLineProject aTimeLineProject) {
        timeLineProject = aTimeLineProject;
        if (timeLineProject == null) {
            contentEditionView.setDisable(true);
            timelineView.setDisable(true);
        } else {
            contentEditionView.setDisable(false);
            timelineView.setDisable(false);
        }
        //
        contentController.setTimeLineProject(timeLineProject);
        if (pictureLoaderViewController != null) {
            pictureChronologyViewController.setProject(timeLineProject);
        }
        if (galleryController != null) {
            galleryController.setProject(timeLineProject);
        }
        timelineController.reset();
        timeLineProject.getFriezes().forEach(timelineController::loadFreize);
    }

    private void executeActionOnHold() {
        switch (actionOnHold) {
            case NEW_PROJECT -> {
                if (projectCreationWizardView == null) {
                    loadProjectCreationWizardView();
                }
                showModalStage(projectCreationWizardView);
            }
            case NONE -> {
            }
            default ->
                throw new UnsupportedOperationException("Unsupported action type :: " + actionOnHold);
        }
        // nothing to do
        actionOnHold = ACTION_ON_HOLD.NONE;
    }

    private void showModalStage(Parent content) {
        if (modalStage == null) {
            modalStage = new Stage();
            modalStage.setAlwaysOnTop(true);
            modalScene = new Scene(content);
            modalStage.setScene(modalScene);
            modalStage.setOnCloseRequest(e -> {
                hideModalStage();
            });
            modalStage.setOnHiding(e -> {
                hideModalStage();
            });
        } else {
            modalScene.setRoot(content);
        }
        modalStage.show();
        mainAnchorPane.setDisable(true);
    }

    private void hideModalStage() {
        if (modalStage != null) {
            modalStage.hide();
        }
        mainAnchorPane.setDisable(false);
    }

    private void displayContentEditionView() {
        if (contentEditionView == null) {
            loadContentEditionView();
            contentEditionView.setDisable(true);
        }
        setMainPaneContent(contentEditionView);
        toolbarToggleGroup.selectToggle(contentEditionToggle);
        viewToggleGroup.selectToggle(contentViewMI);
        //
        contentEditionToggle.setSelected(true);
        timeLineToggle.setSelected(false);
        galleryToggle.setSelected(false);
        picturesChronologyToggle.setSelected(false);
    }

    private void displayTimelineView() {
        if (timelineView == null) {
            loadTimelineView();
            timelineView.setDisable(true);
        }
        setMainPaneContent(timelineView);
        toolbarToggleGroup.selectToggle(timeLineToggle);
        viewToggleGroup.selectToggle(timelineViewMI);
        //
        contentEditionToggle.setSelected(false);
        timeLineToggle.setSelected(true);
        galleryToggle.setSelected(false);
        picturesChronologyToggle.setSelected(false);
    }

    private void displayGalleryView() {
        if (galleryView == null) {
            loadGalleryView();
        }
        setMainPaneContent(galleryView);
        toolbarToggleGroup.selectToggle(galleryToggle);
        viewToggleGroup.selectToggle(galleryViewMI);
        //
        contentEditionToggle.setSelected(false);
        timeLineToggle.setSelected(false);
        galleryToggle.setSelected(true);
        picturesChronologyToggle.setSelected(false);
    }

    private void displayEventChronologyView() {
        if (picturesChronologyView == null) {
            loadEventChronologyView();
        }
        setMainPaneContent(picturesChronologyView);
        toolbarToggleGroup.selectToggle(picturesChronologyToggle);
        viewToggleGroup.selectToggle(picturesChronologyViewMI);
        //
        contentEditionToggle.setSelected(false);
        timeLineToggle.setSelected(false);
        galleryToggle.setSelected(false);
        picturesChronologyToggle.setSelected(true);
    }

    private void loadContentEditionView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("ContentEditionView.fxml"));
        try {
            contentEditionView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load ContentEditionView ::  {0}", new Object[]{ex});
        }
        contentController = loader.getController();
//        configurationController.addPropertyChangeListener(this::handleConfigurationControllerChanges);
    }

    private void loadTimelineView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("TimelineView.fxml"));
        try {
            timelineView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load TimelineView ::  {0}", new Object[]{ex});
        }
        timelineController = loader.getController();
//        configurationController.addPropertyChangeListener(this::handleConfigurationControllerChanges);
    }

    private void loadGalleryView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("GalleryView.fxml"));
        try {
            galleryView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load GalleryView ::  {0}", new Object[]{ex});
        }
        galleryController = loader.getController();
        galleryController.addPropertyChangeListener(this::handleGalleryViewControllerChanges);
    }

    private void loadConfigurationView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("ConfigurationView.fxml"));
        try {
            configurationView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load ConfigurationView ::  {0}", new Object[]{ex});
        }
        configurationController = loader.getController();
        configurationController.addPropertyChangeListener(this::handleConfigurationControllerChanges);
    }

    private void loadProjectCreationWizardView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("ProjectCreationWizard.fxml"));
        try {
            projectCreationWizardView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load ProjectCreationWizardView ::  {0}", new Object[]{ex});
        }
        projectCreationWizardController = loader.getController();
        projectCreationWizardController.addPropertyChangeListener(this::handleProjectCreationControllerChanges);
    }

    private void loadEventChronologyView() {
        FXMLLoader loader = new FXMLLoader(PlaceCreationViewController.class.getResource("PicturesChronologyView.fxml"));
        try {
            picturesChronologyView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load PicturesChronologyView ::  {0}", new Object[]{ex});
        }
        pictureChronologyViewController = loader.getController();
        pictureChronologyViewController.addPropertyChangeListener(this::handleEventChronologyViewControllerChanges);
        if (timeLineProject != null) {
            pictureChronologyViewController.setProject(timeLineProject);
        }
    }

    private void setMainPaneContent(Node node) {
        mainAnchorPane.getChildren().setAll(node);
        // TODO use a constant
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    private void showSaveWindow() {
        if (saveWindow == null) {
            saveWindow = new SaveWindow(mainAnchorPane.getScene().getWindow());
            saveWindow.addListener(this::handleSaveWindowEvents);
        }
        saveWindow.showSaveAndContinue(timeLineProject);
    }

    private void handleConfigurationControllerChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ConfigurationViewController.CLOSE_REQUESTED ->
                hideModalStage();
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void handleProjectCreationControllerChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ProjectCreationWizardController.CANCEL ->
                hideModalStage();
            case ProjectCreationWizardController.CREATE -> {
                hideModalStage();
                var configParams = (Map<String, String>) event.getNewValue();
                String projectName = configParams.get(TimeLineProject.PROJECT_NAME_KEY);
                TimeLineProject project = TimeLineProjectFactory.createProject(projectName, configParams);
                loadProject(project);
                contentEditionView.setDisable(false);
                timelineView.setDisable(false);
                displayContentEditionView();
            }
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void handleEventChronologyViewControllerChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void handleSaveWindowEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case SaveWindow.CANCEL ->
                actionOnHold = ACTION_ON_HOLD.NONE;
            case SaveWindow.SAVE -> {
                handleProjectSave(new ActionEvent());
                executeActionOnHold();
            }
            case SaveWindow.DISCARD ->
                executeActionOnHold();
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void handlePictureLoaderWindowEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureLoaderViewController.CANCEL_EVENT ->
                hideModalStage();
            case PictureLoaderViewController.FILE_REQUEST_EVENT -> // TODO : split code ?
                pictureLoaderViewController.setFile(fileChooser.showOpenDialog(modalStage));
            case PictureLoaderViewController.OK_EVENT -> {
                galleryController.update();
                hideModalStage();
            }
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void handleGalleryViewControllerChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryViewController.DISPLAY_PICTURE_LOADER -> {
                if (pictureLoaderView == null) {
                    pictureLoaderView = (Parent) event.getNewValue();
                    pictureLoaderViewController = (PictureLoaderViewController) event.getOldValue();
                    pictureLoaderViewController.setProject(timeLineProject);
                    pictureLoaderViewController.addPropertyChangeListener(this::handlePictureLoaderWindowEvents);
                }
                showModalStage(pictureLoaderView);
            }
            default ->
                throw new UnsupportedOperationException("" + event);
        }
    }

    private void createToolbar() {
        toolbarToggleGroup = new ToggleGroup();
        toolbarHBox.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        createContentEditionToggle();
        createTimelineToggle();
        createGalleryToggle();
        createPicturesChronologyToggleToggle();
    }

    private void createContentEditionToggle() {
        // https://iconarchive.com/show/outline-icons-by-iconsmind/Pencil-icon.html
        contentEditionToggle = new ToggleButton();
        final Image unselected = new Image(PlaceCreationViewController.class.getResourceAsStream("Pencil-icon.png"));
        final Image selected = new Image(PlaceCreationViewController.class.getResourceAsStream("Pencil-icon_selected.png"));
        final ImageView toggleImage = new ImageView();
        contentEditionToggle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                .when(contentEditionToggle.selectedProperty())
                .then(selected)
                .otherwise(unselected)
        );
        toolbarHBox.getChildren().add(contentEditionToggle);
        contentEditionToggle.setOnAction(e -> contentViewMI.setSelected(true));
    }

    private void createTimelineToggle() {
        // https://iconarchive.com/show/windows-8-icons-by-icons8/Data-Timeline-icon.html
        timeLineToggle = new ToggleButton();
        final Image unselected = new Image(PlaceCreationViewController.class.getResourceAsStream("Data-Timeline-icon.png"));
        final Image selected = new Image(PlaceCreationViewController.class.getResourceAsStream("Data-Timeline-icon_selected.png"));
        final ImageView toggleImage = new ImageView();
        timeLineToggle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                .when(timeLineToggle.selectedProperty())
                .then(selected)
                .otherwise(unselected)
        );
        timeLineToggle.setToggleGroup(toolbarToggleGroup);
        toolbarHBox.getChildren().add(timeLineToggle);
    }

    private void createGalleryToggle() {
        https://iconarchive.com/show/windows-8-icons-by-icons8/Photo-Video-Gallery-icon.html
        galleryToggle = new ToggleButton();
        final Image unselected = new Image(PlaceCreationViewController.class.getResourceAsStream("Photo-Video-Gallery-icon.png"));
        final Image selected = new Image(PlaceCreationViewController.class.getResourceAsStream("Photo-Video-Gallery-icon_selected.png"));
        final ImageView toggleImage = new ImageView();
        galleryToggle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                .when(galleryToggle.selectedProperty())
                .then(selected)
                .otherwise(unselected)
        );
        galleryToggle.setToggleGroup(toolbarToggleGroup);
        toolbarHBox.getChildren().add(galleryToggle);
    }

    private void createPicturesChronologyToggleToggle() {
        // https://iconarchive.com/show/outline-icons-by-iconsmind/Photo-Album-icon.html
        picturesChronologyToggle = new ToggleButton();
        final Image unselected = new Image(PlaceCreationViewController.class.getResourceAsStream("Photo-Album-icon.png"));
        final Image selected = new Image(PlaceCreationViewController.class.getResourceAsStream("Photo-Album-icon_selected.png"));
        final ImageView toggleImage = new ImageView();
        picturesChronologyToggle.setGraphic(toggleImage);
        toggleImage.imageProperty().bind(Bindings
                .when(picturesChronologyToggle.selectedProperty())
                .then(selected)
                .otherwise(unselected)
        );
        picturesChronologyToggle.setToggleGroup(toolbarToggleGroup);
        toolbarHBox.getChildren().add(picturesChronologyToggle);
    }

}
