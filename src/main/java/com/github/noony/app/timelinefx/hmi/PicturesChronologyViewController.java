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

import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronologyFactory;
import com.github.noony.app.timelinefx.drawings.GalleryTiles;
import com.github.noony.app.timelinefx.hmi.picturechronology.PictureChronologyDrawing;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PicturesChronologyViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final PropertyChangeListener galleryTilesListener = PicturesChronologyViewController.this::handleGalleryTilesChanges;

    private TimeLineProject project;
    private GalleryTiles galleryTiles = null;
    private PictureChronology currentPictureChronology = null;
    private PictureChronologyDrawing pictureChronologyDrawing = null;
    //
    private PropertyChangeListener projectListener;

    @FXML
    private ScrollPane viewScrollPane;
//    @FXML
//    private AnchorPane chronologyMainPane;
    @FXML
    private ListView<PictureChronology> chronologiesListView;
    @FXML
    private SplitPane leftSplitPane;
    @FXML
    private ScrollPane picturesPane;
    @FXML
    private TextField chronologyNameField;
    @FXML
    private Button insertPictureB;

    private AnchorPane configuratorView;

    private PicturesChronologyConfiguratorController configuratorController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPictureChronologyConfiguratorView();
        //
        projectListener = this::handleProjectChanges;
        chronologiesListView.setCellFactory((ListView<PictureChronology> p) -> {
            return new PictureChronologyListCellImpl();
        });
        chronologiesListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends PictureChronology> ov, PictureChronology t, PictureChronology t1) -> {
            if (t1 != null & t1 != currentPictureChronology) {
                displayPictureChronology(t1);
            }
        });
        updateChronologiesTab();
        chronologyNameField.setText("");
    }

    @FXML
    protected void handleNewPictureChronology(ActionEvent event) {
        LOG.log(Level.INFO, "handleNewPictureChronology on :: {0}", new Object[]{event});
        displayPictureChronology(PictureChronologyFactory.createPictureChronology(project));
        updateChronologiesTab();
    }

    @FXML
    protected void handleInsertPicture(ActionEvent event) {
        System.err.println(" handleInsertPicture");
    }

    protected void setProject(TimeLineProject aProject) {
        project = aProject;
        if (galleryTiles != null) {
            galleryTiles.removePropertyChangeListener(galleryTilesListener);
        }
        galleryTiles = new GalleryTiles(PictureFactory.getPictures());
        galleryTiles.addPropertyChangeListener(galleryTilesListener);
        picturesPane.setContent(galleryTiles.getNode());
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void displayPictureChronology(PictureChronology aPictureChronology) {
        currentPictureChronology = aPictureChronology;
        //
        if (pictureChronologyDrawing != null) {
//            chronologyMainPane.getChildren().remove(pictureChronologyDrawing.getNode());
            viewScrollPane.setContent(pictureChronologyDrawing.getNode());
        }
        pictureChronologyDrawing = new PictureChronologyDrawing(currentPictureChronology);
//        chronologyMainPane.getChildren().add(pictureChronologyDrawing.getNode());
        viewScrollPane.setContent(pictureChronologyDrawing.getNode());
        chronologyNameField.setText(currentPictureChronology.getName());
        //
        configuratorController.setPictureChronology(currentPictureChronology, pictureChronologyDrawing);
        //
        chronologyNameField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (currentPictureChronology != null) {
                currentPictureChronology.setName(t1);
                chronologiesListView.refresh();
            }
        });

    }

    private void handleProjectChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TimeLineProject.PERSON_ADDED, TimeLineProject.PERSON_REMOVED,
                    TimeLineProject.PLACE_ADDED, TimeLineProject.PLACE_REMOVED,
                    TimeLineProject.STAY_ADDED, TimeLineProject.STAY_REMOVED -> {
            }
            default ->
                throw new UnsupportedOperationException(this.getClass().getSimpleName() + " :: " + event);
        }
        // ignored
    }

    private void updateChronologiesTab() {
        chronologiesListView.getItems().setAll(PictureChronologyFactory.getPicturesChronologies());
        if (currentPictureChronology != null) {
            chronologiesListView.getSelectionModel().select(currentPictureChronology);
        }
    }

    private void handleGalleryTilesChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryTiles.TILE_CLICKED:
                Picture pic = (Picture) event.getNewValue();
                ChronologyPictureMiniature chronologyPictureMiniature = currentPictureChronology.createChronologyPicture(pic);
                break;
            default:
                throw new UnsupportedOperationException("While handleGalleryTilesChanges :: " + event.getPropertyName());
        }
    }

    private void loadPictureChronologyConfiguratorView() {
        FXMLLoader loader = new FXMLLoader(PictureChronologyDrawing.class.getResource("PictureChronologyConfigurator.fxml"));
        try {
            configuratorView = loader.load();
            leftSplitPane.getItems().add(configuratorView);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load ContentEditionView ::  {0}", new Object[]{ex});
        }
        configuratorController = loader.getController();
//        configurationController.addPropertyChangeListener(this::handleConfigurationControllerChanges);
    }

    private static class PictureChronologyListCellImpl extends ListCell<PictureChronology> {

        private PictureChronologyListCellImpl() {
            //
        }

        @Override
        public void updateItem(PictureChronology item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getString());
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem().getName();
        }
    }
}
