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

import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author hamon
 */
public class ChronologyMiniatureConfiguratorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private Label nameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private TextField xPosField;
    @FXML
    private TextField yPosField;
    @FXML
    private TextField scaleField;
    @FXML
    private TextField pictureDateField;
    @FXML
    private TextField customDateField;
    @FXML
    private CheckBox customDateCB;

    private ChronologyPictureMiniature currentMiniature = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.log(Level.INFO, "Initializing ChronologyMiniatureConfiguratorController");
        xPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentMiniature != null) {
                try {
                    var newXPos = Double.parseDouble(xPosField.getText().trim());
                    currentMiniature.setPosition(new Point2D(newXPos, currentMiniature.getPosition().getY()));
                } catch (NumberFormatException e) {
                }
            }
        });
        yPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentMiniature != null) {
                try {
                    var newYPos = Double.parseDouble(yPosField.getText().trim());
                    currentMiniature.setPosition(new Point2D(currentMiniature.getPosition().getX(), newYPos));
                } catch (NumberFormatException e) {
                }
            }
        });
        scaleField.setOnKeyTyped((KeyEvent t) -> {
            if (currentMiniature != null) {
                try {
                    var newScale = Double.parseDouble(scaleField.getText().trim());
                    currentMiniature.setScale(newScale);
                } catch (NumberFormatException e) {
                }
            }
        });
        customDateField.setOnKeyTyped((KeyEvent t) -> {
            if (currentMiniature != null) {
                try {
                    currentMiniature.setCurrenltyUsedTimeValue(customDateField.getText().trim());
                } catch (NumberFormatException e) {
                }
            }
        });
        customDateCB.selectedProperty().addListener((ov, t, t1) -> {
            if (currentMiniature != null) {
                currentMiniature.setUseCustomTime(t1);
            }
        });
    }

    public void setChronologyMiniature(ChronologyPictureMiniature chronologyPictureMiniature) {
        if (currentMiniature != null) {
            currentMiniature.removeListener(this::handleMiniatureChanges);
        }
        currentMiniature = chronologyPictureMiniature;
        if (currentMiniature != null) {
            nameLabel.setText(currentMiniature.getPicture().getName());
            idLabel.setText(Long.toString(currentMiniature.getId()));
            xPosField.setText(MathUtils.doubleToString(currentMiniature.getPosition().getX()));
            yPosField.setText(MathUtils.doubleToString(currentMiniature.getPosition().getY()));
            scaleField.setText(MathUtils.doubleToString(currentMiniature.getScale()));
            pictureDateField.setText(currentMiniature.getPicture().getAbsoluteTimeAsString());
            customDateField.setText(currentMiniature.getDateObject().getAbsoluteTimeAsString());
            customDateCB.setSelected(currentMiniature.usesCustomTime());
            currentMiniature.addListener(this::handleMiniatureChanges);
        } else {
            clear();
        }
    }

    public void clear() {
        nameLabel.setText("");
        idLabel.setText("");
        xPosField.setText("");
        yPosField.setText("");
        scaleField.setText("");
        pictureDateField.setText("");
        customDateField.setText("");
        customDateCB.setSelected(false);
    }

    private void handleMiniatureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED -> {
                var newPosition = (Point2D) event.getNewValue();
                xPosField.setText(Double.toString(newPosition.getX()));
                yPosField.setText(Double.toString(newPosition.getY()));
            }
            case ChronologyPictureMiniature.SCALE_CHANGED ->
                scaleField.setText(Double.toString((double) event.getNewValue()));
            case ChronologyPictureMiniature.TIME_CHANGED -> {
                pictureDateField.setText(currentMiniature.getPicture().getAbsoluteTimeAsString());
                customDateField.setText(currentMiniature.getDateObject().getAbsoluteTimeAsString());
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property change:: " + event);
        }
    }

}
