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
package com.github.noony.app.timelinefx.hmi.freemap;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.Selectable;
import com.github.noony.app.timelinefx.utils.PngExporter;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author hamon
 */
public class FreeMapViewController implements Initializable {

    @FXML
    private ScrollPane viewScrollPane;

    @FXML
    private AnchorPane viewRootPane;
    @FXML
    private CheckBox timeHandleVisibilityCB;
    @FXML
    private CheckBox plotsVisibilityCB;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField plotWidthField;
    @FXML
    private TextField portraitRadiusField;
    @FXML
    private TextField fontSizeField;
    @FXML
    private ColorPicker handleColorPicker;
    @FXML
    private Button linearTimeButton, constantTimeButton;

    private FriezeFreeFormDrawing friezeFreeFormDrawing;
    private Frieze frieze;
    private FriezeFreeMap friezeFreeMap;
    //
    private FileChooser fileChooser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        timeHandleVisibilityCB.setSelected(true);
        timeHandleVisibilityCB.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (friezeFreeFormDrawing != null) {
                friezeFreeFormDrawing.setTimeHandlesVisible(timeHandleVisibilityCB.isSelected());
            }
        });
        //
        plotsVisibilityCB.setSelected(true);
        plotsVisibilityCB.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (friezeFreeMap != null) {
                friezeFreeMap.setPlotVisibility(plotsVisibilityCB.isSelected());
            }
        });
        //
        widthField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var width = Double.parseDouble(t1);
                if (friezeFreeMap != null) {
                    friezeFreeMap.setWidth(width);
                }
            }
        });
        //
        heightField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var height = Double.parseDouble(t1);
                if (friezeFreeMap != null) {
                    friezeFreeMap.setHeight(height);
                }
            }
        });
        //
        plotWidthField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var plotSize = Double.parseDouble(t1);
                if (friezeFreeMap != null) {
                    friezeFreeMap.setPlotSize(plotSize);
                }
            }
        });
        //
        fontSizeField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var fontSize = Double.parseDouble(t1);
                if (friezeFreeMap != null) {
                    friezeFreeMap.setFontSize(fontSize);
                }
            }
        });
        //
        portraitRadiusField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            if (t1.isBlank() | t1.isEmpty()) {
                // nothing
            } else {
                var portraitRadius = Double.parseDouble(t1);
                if (friezeFreeMap != null) {
                    friezeFreeMap.setPortraitRadius(portraitRadius);
                }
            }
        });
        //
        handleColorPicker.setValue(DateHandleDrawing.DEFAULT_COLOR);
        handleColorPicker.valueProperty().addListener((ObservableValue<? extends Color> ov, Color t, Color t1) -> {
            friezeFreeFormDrawing.setDateHandlesColor(handleColorPicker.getValue());
        });
        fileChooser = new FileChooser();
    }

    @FXML
    protected void handleDistributePlacesAction(ActionEvent event) {
        if (friezeFreeMap != null) {
            friezeFreeMap.distributePlaces();
        }
    }

    @FXML
    protected void handleConstantTimeAction(ActionEvent event) {
        if (friezeFreeFormDrawing != null) {
            System.err.println(" :: displayTimeAsEqualSplit");
            friezeFreeFormDrawing.getFriezeFreeMap().displayTimeAsEqualSplit();
        }
    }

    @FXML
    protected void handleUpdateLayout(ActionEvent event) {
        if (friezeFreeFormDrawing != null) {
            System.err.println(" :: handleUpdateLayout");
            friezeFreeFormDrawing.updateLayout();
        }
    }

    @FXML
    protected void handleLinearTimeAction(ActionEvent event) {
        if (friezeFreeFormDrawing != null) {
            friezeFreeFormDrawing.getFriezeFreeMap().displayTimeAsProportional();
        }
    }

    @FXML
    protected void handleSaveAsPicture(ActionEvent event) {
        if (friezeFreeFormDrawing != null) {
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
            File pngFile = fileChooser.showSaveDialog(linearTimeButton.getScene().getWindow());
            if (pngFile != null) {
                PngExporter.exportToPNG(friezeFreeFormDrawing.getNode(), pngFile);
            }
        }
    }

    @FXML
    protected void handleZoomInAction(ActionEvent event) {
        friezeFreeFormDrawing.zoomIn();
    }

    @FXML
    protected void handleZoomOutAction(ActionEvent event) {
        friezeFreeFormDrawing.zoomOut();
    }

    public void setFriezeFreeMap(FriezeFreeMap aFriezeFreeMap) {
        friezeFreeMap = aFriezeFreeMap;
        frieze = friezeFreeMap.getFrieze();
        createDrawing();
        widthField.setText(Double.toString(friezeFreeMap.getWidth()));
        heightField.setText(Double.toString(friezeFreeMap.getHeight()));
        plotWidthField.setText(Double.toString(friezeFreeMap.getPlotSize()));
        fontSizeField.setText(Double.toString(friezeFreeMap.getFontSize()));

    }

    private void handleSelectedItemChange(PropertyChangeEvent event) {
        Selectable selectable = (Selectable) event.getOldValue();
    }

    private void createDrawing() {
        friezeFreeFormDrawing = new FriezeFreeFormDrawing(friezeFreeMap);
        friezeFreeFormDrawing.addPropertyChangeListener(this::handleSelectedItemChange);
        Node node = friezeFreeFormDrawing.getNode();
        viewScrollPane.setContent(node);
    }

}
