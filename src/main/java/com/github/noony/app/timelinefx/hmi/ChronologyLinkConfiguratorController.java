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
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author hamon
 */
public class ChronologyLinkConfiguratorController implements Initializable {

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private VBox contentVBox;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField startXPosField;
    @FXML
    private TextField startYPosField;
    @FXML
    private TextField endXPosField;
    @FXML
    private TextField endYPosField;

    private ChronologyLink currentLink = null;

    private GridPane controlPointsGridPane;
    // controls for cubic link
    private Label controlPoint1Label = null;
    private TextField controlPoint1AngleField = null;
    private TextField controlPoint1DistanceField = null;
    private Label controlPoint2Label = null;
    private TextField controlPoint2AngleField = null;
    private TextField controlPoint2DistanceField = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOG.log(Level.INFO, "Initializing ChronologyLinkConfiguratorController");
        //
        controlPointsGridPane = new GridPane();
        controlPointsGridPane.setHgap(Configuration.DEFAULT_FXML_GAP);
        controlPointsGridPane.setVgap(Configuration.DEFAULT_FXML_GAP);
        //
        startXPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentLink != null) {
                try {
                    var newXPos = Double.parseDouble(startXPosField.getText().trim());
                    currentLink.setStartPosition(new Point2D(newXPos, currentLink.getStartPosition().getY()));
                } catch (NumberFormatException e) {
                }
            }
        });
        startYPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentLink != null) {
                try {
                    var newYPos = Double.parseDouble(startYPosField.getText().trim());
                    currentLink.setStartPosition(new Point2D(currentLink.getStartPosition().getX(), newYPos));
                } catch (NumberFormatException e) {
                }
            }
        });
        //
        endXPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentLink != null) {
                try {
                    var newXPos = Double.parseDouble(endXPosField.getText().trim());
                    currentLink.setEndPosition(new Point2D(newXPos, currentLink.getEndPosition().getY()));
                } catch (NumberFormatException e) {
                }
            }
        });
        endYPosField.setOnKeyTyped((KeyEvent t) -> {
            if (currentLink != null) {
                try {
                    var newYPos = Double.parseDouble(endYPosField.getText().trim());
                    currentLink.setEndPosition(new Point2D(currentLink.getEndPosition().getX(), newYPos));
                } catch (NumberFormatException e) {
                }
            }
        });

    }

    public void setChronologyLink(ChronologyLink chronologyLink) {
        if (currentLink != null) {
            currentLink.removeListener(this::handleLinkChanges);
        }
        currentLink = chronologyLink;
        if (currentLink != null) {
            currentLink.addListener(this::handleLinkChanges);
            createControlPointsGrid();
        }
        updateProperties();
    }

    public void clear() {
        nameLabel.setText("");
        idLabel.setText("");
        startXPosField.setText("");
        startYPosField.setText("");
        endXPosField.setText("");
        endYPosField.setText("");
    }

    private void createControlPointsGrid() {
        controlPointsGridPane.getChildren().clear();
        switch (currentLink.getLinkType()) {
            case LINE -> {
                // no control points needed
            }
            case CUBIC -> {
                if (controlPoint1Label == null) {
                    controlPoint1Label = new Label("Control Point 1 (a,d):");
                    controlPoint1AngleField = new TextField();
                    controlPoint1DistanceField = new TextField();
                    controlPoint2Label = new Label("Control Point 2 (a,d):");
                    controlPoint2AngleField = new TextField();
                    controlPoint2DistanceField = new TextField();
                    //
                    controlPoint1AngleField.setOnKeyTyped((KeyEvent t) -> {
                        if (currentLink != null) {
                            try {
                                var parameters = currentLink.getLinkParameters();
                                parameters[2] = MathUtils.toRadian(Double.parseDouble(controlPoint1AngleField.getText().trim()));
                                currentLink.updateLinkParameters(parameters);
                            } catch (NumberFormatException e) {
                            }
                        }
                    });
                    controlPoint1DistanceField.setOnKeyTyped((KeyEvent t) -> {
                        if (currentLink != null) {
                            try {
                                var parameters = currentLink.getLinkParameters();
                                parameters[3] = Double.parseDouble(controlPoint1DistanceField.getText().trim());
                                currentLink.updateLinkParameters(parameters);
                            } catch (NumberFormatException e) {
                            }
                        }
                    });
                    controlPoint2AngleField.setOnKeyTyped((KeyEvent t) -> {
                        if (currentLink != null) {
                            try {
                                var parameters = currentLink.getLinkParameters();
                                parameters[4] = MathUtils.toRadian(Double.parseDouble(controlPoint2AngleField.getText().trim()));
                                currentLink.updateLinkParameters(parameters);
                            } catch (NumberFormatException e) {
                            }
                        }
                    });
                    controlPoint2DistanceField.setOnKeyTyped((KeyEvent t) -> {
                        if (currentLink != null) {
                            try {
                                var parameters = currentLink.getLinkParameters();
                                parameters[5] = Double.parseDouble(controlPoint2DistanceField.getText().trim());
                                currentLink.updateLinkParameters(parameters);
                            } catch (NumberFormatException e) {
                            }
                        }
                    });
                }
                controlPointsGridPane.add(controlPoint1Label, 0, 0);
                controlPointsGridPane.add(controlPoint1AngleField, 1, 0);
                controlPointsGridPane.add(controlPoint1DistanceField, 2, 0);
                controlPointsGridPane.add(controlPoint2Label, 0, 1);
                controlPointsGridPane.add(controlPoint2AngleField, 1, 1);
                controlPointsGridPane.add(controlPoint2DistanceField, 2, 1);
            }
            default ->
                throw new UnsupportedOperationException("Link type not supported :: " + currentLink.getLinkType());
        }
        if (!contentVBox.getChildren().contains(controlPointsGridPane)) {
            contentVBox.getChildren().add(controlPointsGridPane);

        }
    }

    private void updateProperties() {
        if (currentLink != null) {
            nameLabel.setText(currentLink.getName());
            idLabel.setText(Long.toString(currentLink.getId()));
            startXPosField.setText(MathUtils.doubleToString(currentLink.getStartPosition().getX()));
            startYPosField.setText(MathUtils.doubleToString(currentLink.getStartPosition().getY()));
            endXPosField.setText(MathUtils.doubleToString(currentLink.getEndPosition().getX()));
            endYPosField.setText(MathUtils.doubleToString(currentLink.getEndPosition().getY()));
            switch (currentLink.getLinkType()) {
                case LINE -> {
                    // no control points needed
                }
                case CUBIC -> {
                    var values = currentLink.getLinkParameters();
                    controlPoint1AngleField.setText(MathUtils.doubleToString(MathUtils.toDegree(values[2])));
                    controlPoint1DistanceField.setText(MathUtils.doubleToString(values[3]));
                    controlPoint2AngleField.setText(MathUtils.doubleToString(MathUtils.toDegree(values[4])));
                    controlPoint2DistanceField.setText(MathUtils.doubleToString(values[5]));
                }
                default ->
                    throw new UnsupportedOperationException("Link type not supported :: " + currentLink.getLinkType());
            }
        } else {
            clear();
        }
    }

    private void handleLinkChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyLink.PLOTS_UPDATED -> {
                updateProperties();
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property change:: " + event);
        }
    }

}
