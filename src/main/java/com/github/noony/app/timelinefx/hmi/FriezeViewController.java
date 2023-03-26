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

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckTreeView;

@Deprecated
public class FriezeViewController implements Initializable {


    @FXML
    private CheckListView<Person> personCheckListView;
    @FXML
    private CheckTreeView<Place> placesCheckTreeView;

    @FXML
    private TabPane friezeTabPane;

    @FXML
    private Accordion leftAccordion;
    //
    @FXML
    private TitledPane propertiesPane;
    @FXML
    private TextField nameField;
    @FXML
    private ListView<FriezeFreeMap> freemapListView;
    @FXML
    private Button deleteFreemapButton;
    //
    //

    //

    @Override
    public void initialize(URL url, ResourceBundle rb) {


    }

    @FXML
    protected void handleFreeMapCreation(ActionEvent event) {
    }

    @FXML
    protected void handleDeleteFreeMap(ActionEvent event) {
    }








}
