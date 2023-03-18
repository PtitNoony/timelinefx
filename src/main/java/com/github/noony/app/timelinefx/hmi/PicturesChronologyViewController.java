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

import com.github.noony.app.timelinefx.core.IFileObject;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.Portrait;
import com.github.noony.app.timelinefx.core.PortraitFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronologyFactory;
import com.github.noony.app.timelinefx.drawings.GalleryTiles;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import com.github.noony.app.timelinefx.hmi.picturechronology.PictureChronologyDrawing;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PicturesChronologyViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final PropertyChangeListener galleryTilesListener = PicturesChronologyViewController.this::handleGalleryTilesChanges;
    private final PropertyChangeListener portraitTilesListener = PicturesChronologyViewController.this::handlePortraitTilesChanges;

    private TimeLineProject project;
    private GalleryTiles picturesGalleryTiles = null;
    private GalleryTiles portraitGalleryTiles = null;
    private PictureChronology currentPictureChronology = null;
    private PictureChronologyDrawing pictureChronologyDrawing = null;
    //

    @FXML
    private ScrollPane viewScrollPane;
    @FXML
    private TabPane propertiesTabPane;
    @FXML
    private ListView<PictureChronology> chronologiesListView;
    @FXML
    private SplitPane leftSplitPane;
    @FXML
    private ScrollPane picturesPane, portraitsPane;
    @FXML
    private TextField chronologyNameField;
    @FXML
    private Button insertPictureB, insertPortraitB;

    private AnchorPane configuratorView;
    private AnchorPane miniaturePropertyRootView;
    private AnchorPane linkPropertyRootView;

    private PicturesChronologyConfiguratorController configuratorController;
    private ChronologyMiniatureConfiguratorController miniaturePropertyController;
    private ChronologyLinkConfiguratorController linkPropertyController;

    private Tab configuratorTab = null;
    private Tab itemPropertyTab = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PictureFactory.addPropertyChangeListener(this::handlePictureFactoryChanges);
        PortraitFactory.addPropertyChangeListener(this::handlePortraitFactoryChanges);
        loadPictureChronologyConfiguratorView();
        //
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

    @FXML
    protected void handleInsertPortrait(ActionEvent event) {
        System.err.println(" handleInsertPortrait");
    }

    protected void setProject(TimeLineProject aProject) {
        project = aProject;
        if (picturesGalleryTiles != null) {
            picturesGalleryTiles.removePropertyChangeListener(galleryTilesListener);
        }
        List<IFileObject> pictures = new LinkedList<>();
        pictures.addAll(PictureFactory.getPictures());
        picturesGalleryTiles = new GalleryTiles(pictures);
        picturesGalleryTiles.addPropertyChangeListener(galleryTilesListener);
        picturesPane.setContent(picturesGalleryTiles.getNode());
        //
        if (portraitGalleryTiles != null) {
            portraitGalleryTiles.removePropertyChangeListener(portraitTilesListener);
        }
        List<IFileObject> portraits = new LinkedList<>();
        portraits.addAll(PortraitFactory.getPortraits());
        portraitGalleryTiles = new GalleryTiles(portraits);
        portraitGalleryTiles.addPropertyChangeListener(portraitTilesListener);
        portraitsPane.setContent(portraitGalleryTiles.getNode());
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void displayPictureChronology(PictureChronology aPictureChronology) {
        currentPictureChronology = aPictureChronology;
        //
        if (pictureChronologyDrawing != null) {
            // WHAT ???
//            viewScrollPane.setContent(pictureChronologyDrawing.getNode());
            pictureChronologyDrawing.removePropertyChangeListener(this::handleChronologyChanges);
        }
        //
        pictureChronologyDrawing = new PictureChronologyDrawing(currentPictureChronology);
        pictureChronologyDrawing.addPropertyChangeListener(this::handleChronologyChanges);
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

    private void handlePictureFactoryChanges(PropertyChangeEvent event) {
        if (picturesGalleryTiles != null) {
            switch (event.getPropertyName()) {
                case PictureFactory.PICTURE_ADDED -> {
                    var picture = (Picture) event.getNewValue();
                    picturesGalleryTiles.addFileObject(picture);
                }
                default ->
                    throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
            }
        }
    }

    private void handlePortraitFactoryChanges(PropertyChangeEvent event) {
        if (portraitGalleryTiles != null) {
            switch (event.getPropertyName()) {
                case PortraitFactory.PORTRAIT_CREATED -> {
                    var portrait = (Portrait) event.getNewValue();
                    portraitGalleryTiles.addFileObject(portrait);
                }
                default ->
                    throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
            }
        }
    }

    private void handleGalleryTilesChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryTiles.TILE_CLICKED -> {
                Picture pic = (Picture) event.getNewValue();
                var chronologyPictureMiniature = currentPictureChronology.createChronologyPicture(pic);
                LOG.log(Level.FINE, "Created picture minitature : {0}.", new Object[]{chronologyPictureMiniature});
            }
            case GalleryTiles.TILE_SELECTED -> {
                // nothing to do for now
            }
            default ->
                throw new UnsupportedOperationException("While handleGalleryTilesChanges :: " + event.getPropertyName());
        }
    }

    private void handlePortraitTilesChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryTiles.TILE_CLICKED -> {
                Portrait portrait = (Portrait) event.getNewValue();
                var chronologyPictureMiniature = currentPictureChronology.createChronologyPicture(portrait);
                LOG.log(Level.FINE, "Created picture minitature : {0}.", new Object[]{chronologyPictureMiniature});
            }
            case GalleryTiles.TILE_SELECTED -> {
                // nothing to do for now
            }
            default ->
                throw new UnsupportedOperationException("While handlePortraitTilesChanges :: " + event.getPropertyName());
        }
    }

    private void handleChronologyChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronologyDrawing.LINK_SELECTED -> {
                var link = (ChronologyLink) event.getNewValue();
                displayLinkPropertyTab(link);
            }
            case PictureChronologyDrawing.LINK_UNSELECTED -> {
                hideItemPropertyTab();
            }
            case PictureChronologyDrawing.MINIATURE_SELECTED -> {
                var miniature = (ChronologyPictureMiniature) event.getNewValue();
                displayMiniaturePropertyTab(miniature);
            }
            case PictureChronologyDrawing.MINIATURE_UNSELECTED -> {
                hideItemPropertyTab();
            }
            case IFxScalableNode.ZOOM_LEVEL_CHANGED -> {
                // nothing to do
            }
            default ->
                throw new UnsupportedOperationException("While handlePortraitTilesChanges :: " + event.getPropertyName());
        }
    }

    private void loadPictureChronologyConfiguratorView() {
        FXMLLoader loader = new FXMLLoader(PictureChronologyDrawing.class.getResource("PictureChronologyConfigurator.fxml"));
        try {
            configuratorView = loader.load();
            configuratorTab = new Tab("Chronology", configuratorView);
            propertiesTabPane.getTabs().add(configuratorTab);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load PictureChronologyConfigurator ::  {0}", new Object[]{ex});
        }
        configuratorController = loader.getController();
    }

    private void loadMiniatureConfiguratorView() {
        FXMLLoader loader = new FXMLLoader(PictureChronologyDrawing.class.getResource("MiniatureConfigurator.fxml"));
        try {
            miniaturePropertyRootView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load MiniatureConfigurator ::  {0}", new Object[]{ex});
        }
        miniaturePropertyController = loader.getController();
    }

    private void loadLinkConfiguratorView() {
        FXMLLoader loader = new FXMLLoader(PictureChronologyDrawing.class.getResource("LinkConfigurator.fxml"));
        try {
            linkPropertyRootView = loader.load();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not load LinkConfigurator ::  {0}", new Object[]{ex});
        }
        linkPropertyController = loader.getController();
    }

    private void displayMiniaturePropertyTab(ChronologyPictureMiniature miniature) {
        if (miniaturePropertyRootView == null) {
            loadMiniatureConfiguratorView();
        }
        setPropertyTabContent(miniaturePropertyRootView, "Miniature");
        miniaturePropertyController.setChronologyMiniature(currentPictureChronology, miniature);
    }

    private void displayLinkPropertyTab(ChronologyLink link) {
        if (linkPropertyRootView == null) {
            loadLinkConfiguratorView();
        }
        setPropertyTabContent(linkPropertyRootView, "Link");
        linkPropertyController.setChronologyLink(link);
    }

    private void setPropertyTabContent(Node aNode, String title) {
        if (itemPropertyTab == null) {
            itemPropertyTab = new Tab(title, aNode);
//            itemPropertyTab.
            itemPropertyTab.setClosable(false);
            propertiesTabPane.getTabs().add(itemPropertyTab);
        } else {
            itemPropertyTab.setContent(aNode);
            itemPropertyTab.setText(title);
            if (!propertiesTabPane.getTabs().contains(itemPropertyTab)) {
                propertiesTabPane.getTabs().add(itemPropertyTab);
            }
        }
        propertiesTabPane.getSelectionModel().select(itemPropertyTab);
    }

    private void hideItemPropertyTab() {
        if (miniaturePropertyController != null && itemPropertyTab != null) {
            propertiesTabPane.getTabs().remove(itemPropertyTab);
        }
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
