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
package com.github.noony.app.timelinefx.hmi;

import com.github.noony.app.timelinefx.core.Portrait;
import com.github.noony.app.timelinefx.drawings.GalleryTiles;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;

/**
 *
 * @author hamon
 */
public class PortraitSelectionViewController implements Initializable {

    public static final String PORTRAIT_SELECTION_UPDATED = "portraitSelectionUpdated";
    public static final String HIDE_REQUESTED = "hideRequested";

    @FXML
    private ScrollPane galleryScrollPane;

    private Portrait portraitSelected = null;
    private List<Portrait> portraits;
    private PropertyChangeSupport propertyChangeSupport;
    //
    private GalleryTiles galleryTiles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        galleryTiles = new GalleryTiles();
        galleryScrollPane.setContent(galleryTiles.getNode());
        galleryTiles.addPropertyChangeListener(this::handleGalleryTilesChanges);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @FXML
    protected void handleCancelAction(ActionEvent event) {
        propertyChangeSupport.firePropertyChange(HIDE_REQUESTED, null, this);
    }

    @FXML
    protected void handleOKAction(ActionEvent event) {
        if (portraitSelected != null) {
            propertyChangeSupport.firePropertyChange(PORTRAIT_SELECTION_UPDATED, null, portraitSelected);
        }
        propertyChangeSupport.firePropertyChange(HIDE_REQUESTED, null, this);
    }

    public void setPortraits(List<Portrait> aPortraitList, Portrait aSelectedPortrait) {
        portraits = aPortraitList;
        portraitSelected = aSelectedPortrait;
        var portraitsCollection = FXCollections.observableArrayList(portraits);
        galleryTiles.removeAllFileObjects();
        portraitsCollection.forEach(galleryTiles::addFileObject);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void handleGalleryTilesChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case GalleryTiles.TILE_CLICKED -> {
                // nothing to do
            }
            case GalleryTiles.TILE_SELECTED -> {
                var tile = event.getNewValue();
                if (tile != null) {
                    portraitSelected = (Portrait) event.getOldValue();
                }
            }
            default ->
                throw new UnsupportedOperationException("While handleGalleryTilesChanges :: " + event.getPropertyName());
        }
    }

}
