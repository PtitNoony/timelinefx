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

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.hmi.byperson.FriezePeopleLinearDrawing;
import com.github.noony.app.timelinefx.utils.TimeFormatToString;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author arnaud
 */
public class FriezePeopleViewController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private AnchorPane peopleViewRootPane;
    @FXML
    private ScrollPane peopleViewScrollPane;
    @FXML
    private AnchorPane peopleViewPane;
    @FXML
    private RangeSlider peopleViewTimeSlider;
    @FXML
    private Label peopleViewMinDateLabel, peopleViewMaxDateLabel;

    /// DOUBLONS !!
    private final double barPadding = 15;
    private final double noBarPadding = 5;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");

    private double friezePaneWidth = 100;
    private double friezePaneHeight = 100;
    private boolean vBarVisible = true;

    private Frieze frieze = null;
    private FriezePeopleLinearDrawing friezePeopleLinearDrawing = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peopleViewScrollPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            friezePaneWidth = newValue.doubleValue();
            updateFriezeWidth();
        });
        //
        peopleViewScrollPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            friezePaneHeight = newValue.doubleValue();
            if (friezePeopleLinearDrawing != null) {
                friezePeopleLinearDrawing.setHeight(friezePaneHeight);
            }
        });
        // !! TODO fix since not working
        Set<Node> scrollBars = peopleViewScrollPane.lookupAll(".scroll-bar");
        Optional<Node> horizontalScrollBar = scrollBars.stream()
                .filter(node
                        -> ((ScrollBar) node).getOrientation().equals(Orientation.HORIZONTAL))
                .findAny();
        horizontalScrollBar.ifPresent(node
                -> node.visibleProperty().addListener((observable, oldValue, newValue) -> {
                    vBarVisible = newValue;
                    updateFriezeWidth();
                })
        );
        //
        peopleViewTimeSlider.lowValueProperty().addListener(o -> {
            LOG.log(Level.FINE, "peopleViewTimeSlider low value changed {0}", o);
            updateLowerTimeValue();
            // If the high value is also changing simultaneously, don't call setValue() twice
            if (peopleViewTimeSlider.isHighValueChanging()) {
                updateUpperTimeValue();
            }
        });

        peopleViewTimeSlider.highValueProperty().addListener(o -> {
            if (!peopleViewTimeSlider.isLowValueChanging()) {
                updateUpperTimeValue();
            }
        });
        peopleViewMinDateLabel.setText("");
        peopleViewMaxDateLabel.setText("");
    }

    private void updateFriezeWidth() {
        if (friezePeopleLinearDrawing != null) {
            if (vBarVisible) {
                friezePeopleLinearDrawing.setWidth(friezePaneWidth - barPadding);
            } else {
                friezePeopleLinearDrawing.setWidth(friezePaneWidth - noBarPadding);
            }
        }
    }

    public void setFrieze(Frieze f) {
        frieze = f;
        friezePeopleLinearDrawing = new FriezePeopleLinearDrawing(frieze);
        peopleViewPane.getChildren().setAll(friezePeopleLinearDrawing.getNode());
        friezePeopleLinearDrawing.setWidth(friezePaneWidth);
        friezePeopleLinearDrawing.setHeight(friezePaneHeight);
        peopleViewTimeSlider.setMin(frieze.getMinDate());
        peopleViewTimeSlider.setMax(frieze.getMaxDate());
        peopleViewTimeSlider.setLowValue(frieze.getMinDate());
        peopleViewTimeSlider.setHighValue(frieze.getMaxDate());
    }

    private void updateLowerTimeValue() {
        var minT = (long) peopleViewTimeSlider.getLowValue();
        frieze.setMinDateWindow(minT);
        peopleViewMinDateLabel.setText(TimeFormatToString.timeToString(minT, frieze.getTimeFormat()));
    }

    private void updateUpperTimeValue() {
        var maxT = (long) peopleViewTimeSlider.getHighValue();
        frieze.setMaxDateWindow(maxT);
        peopleViewMaxDateLabel.setText(TimeFormatToString.timeToString(maxT, frieze.getTimeFormat()));
    }

}
