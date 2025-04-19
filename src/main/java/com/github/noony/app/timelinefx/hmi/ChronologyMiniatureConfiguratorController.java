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

import com.github.noony.app.timelinefx.core.AnchorSide;
import static com.github.noony.app.timelinefx.core.TimeFormat.LOCAL_TIME;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
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
    // Date
    @FXML
    private CheckBox customDateCB;
    @FXML
    private DatePicker customDatePicker;
    @FXML
    private TextField customDateField;
    @FXML
    private TableView<ChronologyLink> linksTable;
    @FXML
    private TableColumn<ChronologyLink, String> linksTypeColumn, linksPersonColumn;
    @FXML
    private TableColumn<ChronologyLink, AnchorSide> linksSideColumn;

    private ChronologyPictureMiniature currentMiniature = null;
    private PictureChronology pictureChronology = null;

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
        // custom date part
        customDateField.setOnKeyTyped((KeyEvent t) -> {
            if (currentMiniature != null) {
                try {
                    currentMiniature.setCurrenltyUsedTimeValue(customDateField.getText().trim());
                } catch (NumberFormatException e) {
                }
            }
        });
        customDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> ov, LocalDate t, LocalDate t1) -> {
            if (currentMiniature != null) {
                currentMiniature.setCurrenltyUsedTimeValue(t1);
            }
        });
        customDateCB.selectedProperty().addListener((ov, t, t1) -> {
            if (currentMiniature != null) {
                currentMiniature.setUseCustomTime(t1);
            }
        });
        //
        linksTable.setEditable(true);
        linksTypeColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<ChronologyLink, String> param)
                -> new ReadOnlyStringWrapper(currentMiniature == param.getValue().getStartMiniature() ? "OUT" : "IN"));
        linksPersonColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<ChronologyLink, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getPerson().getName()));
//        linksSideColumn.setCellValueFactory(
//                (TableColumn.CellDataFeatures<ChronologyLink, String> param)
//                -> new ReadOnlyStringWrapper(currentMiniature == param.getValue().getStartMiniature() ? param.getValue().getStartAnchorSide().name() : param.getValue().getEndAnchorSide().name()));
//        linksSideColumn.setCellFactory(param -> new ComboBoxTableCell<>(FXCollections.observableList(Arrays.asList(AnchorSide.values()))) {
//            @Override
//            public void updateItem(AnchorSide t, boolean bln) {
//                ChronologyLink link;
////                link=
//                super.updateItem(t, bln); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
//            }
//
//        });
        linksSideColumn.setCellValueFactory(new PropertyValueFactory<>("startAnchorSide"));
        linksSideColumn.setCellFactory(ComboBoxTableCell.<ChronologyLink, AnchorSide>forTableColumn(FXCollections.observableList(Arrays.asList(AnchorSide.values()))));
        linksSideColumn.setEditable(true);
    }

    @FXML
    public void handleRecalculateLinksAction(ActionEvent event) {
        if (currentMiniature != null) {
            currentMiniature.requestAssociatedLinksUpdate();
        }
    }

    public void setChronologyMiniature(PictureChronology aPictureChronology, ChronologyPictureMiniature chronologyPictureMiniature) {
        if (currentMiniature != null) {
            currentMiniature.removeListener(this::handleMiniatureChanges);
        }
        currentMiniature = chronologyPictureMiniature;
        pictureChronology = aPictureChronology;
        if (currentMiniature != null) {
            nameLabel.setText(currentMiniature.getPicture().getName());
            idLabel.setText(Long.toString(currentMiniature.getId()));
            xPosField.setText(MathUtils.doubleToString(currentMiniature.getPosition().getX()));
            yPosField.setText(MathUtils.doubleToString(currentMiniature.getPosition().getY()));
            scaleField.setText(MathUtils.doubleToString(currentMiniature.getScale()));
            pictureDateField.setText(currentMiniature.getPicture().getAbsoluteTimeAsString());
            customDateCB.setSelected(currentMiniature.usesCustomTime());
            currentMiniature.addListener(this::handleMiniatureChanges);
            switch (pictureChronology.getTimeFormat()) {
                case LOCAL_TIME:
                    customDatePicker.setDisable(false);
                    customDateField.setDisable(true);
                    customDatePicker.setValue(chronologyPictureMiniature.getCurrenltyUsedTimeValue());
                    customDateField.setText("");
                    break;
                case TIME_MIN:
                    customDatePicker.setDisable(true);
                    customDateField.setDisable(false);
                    customDateField.setText(currentMiniature.getDateObject().getAbsoluteTimeAsString());
                    customDatePicker.setValue(LocalDate.now());
                    break;
                default:
                    throw new AssertionError();
            }
            //
            addLinks();
        } else {
            clear();
        }
    }

    private void addLinks() {
        linksTable.getItems().setAll(
                pictureChronology.getLinks().stream()
                        .filter(l -> l.getStartMiniature() == currentMiniature || l.getEndMiniature() == currentMiniature)
                        .collect(Collectors.toList()));
    }

    private void clear() {
        nameLabel.setText("");
        idLabel.setText("");
        xPosField.setText("");
        yPosField.setText("");
        scaleField.setText("");
        pictureDateField.setText("");
        customDateCB.setSelected(false);
        customDatePicker.setValue(LocalDate.now());
        customDateField.setText("");
        customDatePicker.setDisable(true);
        customDateField.setDisable(true);
        linksTable.getItems().clear();
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
            case ChronologyPictureMiniature.REQUEST_LINKS_UPDATE -> {
                //Nothing to do: links are only getting repositioned
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property change:: " + event);
        }
    }

    private static class MyCustom extends ComboBoxTableCell<ChronologyLink, AnchorSide> {

//        private MyCustom(){
//            super(ol);
//        }
        @Override
        public void updateItem(AnchorSide t, boolean bln) {
            super.updateItem(t, bln); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

    }

    /*
    private static class WordListCell extends ComboBoxListCell<AnchorSide> {
//    private final Label title = new Label();
//    private final Label detail = new Label();
//    private final VBox layout = new VBox(title, detail);

    public WordListCell() {
        super();
        title.setStyle("-fx-font-size: 20px;");
    }

    @Override
    public void updateItem(AnchorSide item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
get
        if (empty || item == null || item.getWord() == null) {
            title.setText(null);
            detail.setText(null);
            setGraphic(null);
        } else {
            title.setText(item.getWord());
            detail.setText(
                    item.getDefinition() != null
                            ? item.getDefinition()
                            : "Undefined"
            );
            setGraphic(layout);
        }
    }
}*/
}
