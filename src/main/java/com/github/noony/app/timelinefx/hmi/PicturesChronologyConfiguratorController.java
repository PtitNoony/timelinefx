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

import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.hmi.picturechronology.PictureChronologyDrawing;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
    //
    private PictureChronology pictureChronology;
    private PictureChronologyDrawing pictureChronologyDrawing;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configuratorRoot.setDisable(true);
        //
        widthField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var width = Double.parseDouble(t1);
                if (pictureChronology != null) {
                    pictureChronology.setWidth(width);
                }
            }
        });
        //
        heightField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var height = Double.parseDouble(t1);
                if (pictureChronology != null) {
                    pictureChronology.setHeight(height);
                }
            }
        });
        zoomField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            try {
                var newScale = Double.parseDouble(t1);
                if (pictureChronologyDrawing != null && newScale != pictureChronologyDrawing.getViewingScale()) {
                    pictureChronologyDrawing.setViewingScale(newScale);
                }
            } catch (NumberFormatException e) {
                LOG.log(Level.FINEST, "The following value is not a valid zoom level {0}. {1}", new Object[]{t1, e});
            }
        });
    }

    @FXML
    protected void handleZoomIn(ActionEvent event) {
        pictureChronologyDrawing.zoomIn();
    }

    @FXML
    protected void handleZoomOut(ActionEvent event) {
        pictureChronologyDrawing.zoomOut();
    }

    protected void setPictureChronology(PictureChronology aPictureChronology, PictureChronologyDrawing aPictureChronologyDrawing) {
        clear();
        pictureChronology = aPictureChronology;
        pictureChronologyDrawing = aPictureChronologyDrawing;
        if (pictureChronology != null) {
            widthField.setText(Double.toString(pictureChronology.getWidth()));
            heightField.setText(Double.toString(pictureChronology.getHeight()));
            zoomField.setText(Double.toString(pictureChronologyDrawing.getViewingScale()));
        }
        configuratorRoot.setDisable(pictureChronology == null);
    }

    private void clear() {
        widthField.setText("");
        heightField.setText("");
        zoomField.setText("");
    }
}
