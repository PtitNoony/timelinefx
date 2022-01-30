/*
 * Copyright (C) 2021 NoOnY
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
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import com.github.noony.app.timelinefx.hmi.picturechronology.PictureChronologyDrawing;
import com.github.noony.app.timelinefx.utils.MathUtils;
import com.github.noony.app.timelinefx.utils.PngExporter;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 *
 * @author hamon
 */
public class PicturesChronologyConfiguratorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private AnchorPane configuratorRoot;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField zoomField;
    @FXML
    private CheckBox picturesVisibilityCB;
    //
    private PictureChronology pictureChronology;
    private PictureChronologyDrawing pictureChronologyDrawing;
    //
    private FileChooser fileChooser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configuratorRoot.setDisable(true);
        picturesVisibilityCB.setSelected(true);
        picturesVisibilityCB.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (pictureChronologyDrawing != null) {
                pictureChronologyDrawing.setPicturesVisibility(t1);
            }
        });
        //
        widthField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (!t1.isBlank() && !t1.isEmpty()) {
                var width = Double.parseDouble(t1);
                if (pictureChronology != null) {
                    pictureChronology.setWidth(width);
                }
            }
        });
        //
        heightField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (!t1.isBlank() && !t1.isEmpty()) {
                var height = Double.parseDouble(t1);
                if (pictureChronology != null) {
                    pictureChronology.setHeight(height);
                }
            }
        });
        zoomField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            try {
                var newScale = Double.parseDouble(t1);
                if (pictureChronologyDrawing != null && newScale != pictureChronologyDrawing.getScale()) {
                    pictureChronologyDrawing.updateScale(newScale);
                }
            } catch (NumberFormatException e) {
                LOG.log(Level.FINEST, "The following value is not a valid zoom level {0}. {1}", new Object[]{t1, e});
            }
        });
    }

    @FXML
    protected void handleZoomIn(ActionEvent event) {
        LOG.log(Level.FINE, "handleZoomIn on event {0}.", new Object[]{event});
        pictureChronologyDrawing.zoomIn();
    }

    @FXML
    protected void handleZoomOut(ActionEvent event) {
        LOG.log(Level.FINE, "handleZoomOut on event {0}.", new Object[]{event});
        pictureChronologyDrawing.zoomOut();
    }

    @FXML
    protected void handleOnSaveAsPic(ActionEvent event) {
        LOG.log(Level.INFO, "Saving Picture Chronology as picture on event {0}", new Object[]{event});
        if (pictureChronology != null) {
            File initFolder = new File(Configuration.getProjectsParentFolder());
            if (fileChooser == null) {
                fileChooser = new FileChooser();
            }
            fileChooser.setInitialDirectory(initFolder);
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
            File pngFile = fileChooser.showSaveDialog(configuratorRoot.getScene().getWindow());
            if (pngFile != null) {
                PngExporter.exportToPNG(pictureChronologyDrawing.getNode(), pngFile);
            }
        }

    }

    protected void setPictureChronology(PictureChronology aPictureChronology, PictureChronologyDrawing aPictureChronologyDrawing) {
        clear();
        pictureChronology = aPictureChronology;
        pictureChronologyDrawing = aPictureChronologyDrawing;
        pictureChronologyDrawing.setPicturesVisibility(picturesVisibilityCB.isSelected());
        pictureChronologyDrawing.addPropertyChangeListener(this::handlePictureChronologyDrawingChanges);
        if (pictureChronology != null) {
            widthField.setText(MathUtils.doubleToString(pictureChronology.getWidth()));
            heightField.setText(MathUtils.doubleToString(pictureChronology.getHeight()));
            zoomField.setText(MathUtils.doubleToString(pictureChronologyDrawing.getScale()));
        }
        configuratorRoot.setDisable(pictureChronology == null);
    }

    private void clear() {
        widthField.setText("");
        heightField.setText("");
        zoomField.setText("");
    }

    private void handlePictureChronologyDrawingChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case IFxScalableNode.ZOOM_LEVEL_CHANGED -> {
                if (pictureChronologyDrawing != null) {
                    zoomField.setText(MathUtils.doubleToString(pictureChronologyDrawing.getScale()));
                }
            }
            default ->
                throw new UnsupportedOperationException("Could not handle PictureChronologyDrawing event in " + this.getClass().getSimpleName() + ":: " + event.getPropertyName());
        }
    }
}
