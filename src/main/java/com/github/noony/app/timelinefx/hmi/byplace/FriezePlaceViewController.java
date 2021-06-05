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
package com.github.noony.app.timelinefx.hmi.byplace;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.utils.TimeFormatToString;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.RangeSlider;

/**
 *
 * @author hamon
 */
public class FriezePlaceViewController implements Initializable {

    private final double barPadding = 15;
    private final double noBarPadding = 5;

    @FXML
    private AnchorPane appRootPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane friezePane;

    @FXML
    private RangeSlider timeSlider;
    @FXML
    private Label minDateLabel, maxDateLabel;

    private double friezePaneWidth = 100;
    private double friezePaneHeight = 100;
    private boolean vBarVisible = true;

    private Frieze frieze = null;
    private FriezeSpaceLinearDrawing friezeSpaceLinearDrawing = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            friezePaneWidth = newValue.doubleValue();
            updateFriezeWidth();
        });
        //
        scrollPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            friezePaneHeight = newValue.doubleValue();
            if (friezeSpaceLinearDrawing != null) {
                friezeSpaceLinearDrawing.setHeight(friezePaneHeight);
            }
        });
        // !! TOD fix since not working
        Set<Node> scrollBars = scrollPane.lookupAll(".scroll-bar");
        Optional<Node> horizontalScrollBar = scrollBars.stream()
                .filter(node
                        -> ((ScrollBar) node).getOrientation().equals(Orientation.HORIZONTAL))
                .findAny();
        horizontalScrollBar.ifPresent(node
                -> node.visibleProperty().addListener((observable, oldValue, newValue) -> {
                    vBarVisible = newValue;
                    System.err.println(" VISIBILITY " + newValue);
                    updateFriezeWidth();
                })
        );
        //
        timeSlider.lowValueProperty().addListener(o -> {
            // TODO LOG
            updateLowerTimeValue();
            // If the high value is also changing simultaneously, don't call setValue() twice
            if (timeSlider.isHighValueChanging()) {
                updateUpperTimeValue();
            }
        });

        timeSlider.highValueProperty().addListener(o -> {
            if (!timeSlider.isLowValueChanging()) {
                updateUpperTimeValue();
            }
        });
        minDateLabel.setText("");
        maxDateLabel.setText("");
    }

    private void updateFriezeWidth() {
        if (friezeSpaceLinearDrawing != null) {
            if (vBarVisible) {
                friezeSpaceLinearDrawing.setWidth(friezePaneWidth - barPadding);
            } else {
                friezeSpaceLinearDrawing.setWidth(friezePaneWidth - noBarPadding);
            }
        }
    }

    //TODO protect ?
    public void setFrieze(Frieze f) {
        frieze = f;
        friezeSpaceLinearDrawing = new FriezeSpaceLinearDrawing(frieze);
        friezePane.getChildren().setAll(friezeSpaceLinearDrawing.getNode());
        friezeSpaceLinearDrawing.setWidth(friezePaneWidth);
        friezeSpaceLinearDrawing.setHeight(friezePaneHeight);
        timeSlider.setMin(frieze.getMinDate());
        timeSlider.setMax(frieze.getMaxDate());
        timeSlider.setLowValue(frieze.getMinDate());
        timeSlider.setHighValue(frieze.getMaxDate());
    }

    private void updateLowerTimeValue() {
        long minT = (long) timeSlider.getLowValue();
        frieze.setMinDateWindow(minT);
        minDateLabel.setText(TimeFormatToString.timeToString(minT, frieze.getTimeFormat()));
    }

    private void updateUpperTimeValue() {
        long maxT = (long) timeSlider.getHighValue();
        frieze.setMaxDateWindow(maxT);
        maxDateLabel.setText(TimeFormatToString.timeToString(maxT, frieze.getTimeFormat()));
    }

}
