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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author arnaud
 */
public class TimelineViewController implements Initializable {

    @FXML
    private TabPane friezeTabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nothing to do
    }

    protected void reset() {
        friezeTabPane.getTabs().clear();
    }

    protected void loadFreize(Frieze frieze) {
        Tab tab = createFriezeTab(frieze);
        tab.setClosable(false);
        friezeTabPane.getTabs().add(tab);
    }

    private Tab createFriezeTab(Frieze frieze) {
        Parent friezeRoot = new AnchorPane();
        FXMLLoader loader = new FXMLLoader(FriezeViewController.class.getResource("FriezeView.fxml"));
        try {
            friezeRoot = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ProjectViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        FriezeViewController controller = loader.getController();
        controller.setFrieze(frieze);
        var tab = new Tab(frieze.getName(), friezeRoot);
        frieze.addListener(e -> {
            switch (e.getPropertyName()) {
                case Frieze.NAME_CHANGED:
                    tab.setText(frieze.getName());
                default:
                //nothing to do
            }
        });
        return tab;
    }
}
