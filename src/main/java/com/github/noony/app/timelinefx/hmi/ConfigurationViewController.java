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

import com.github.noony.app.timelinefx.Configuration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.runLater;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author hamon
 */
public class ConfigurationViewController implements Initializable {

    public static final String CLOSE_REQUESTED = "closeRequested1";

    private static final Logger LOG = Logger.getGlobal();

    @FXML
    private TextField timelinesLocationField;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(ConfigurationViewController.this);
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Configuration.addPropertyChangeListener(this::handleGlobalConfigurationChanged);
        timelinesLocationField.setText(Configuration.getProjectsParentFolder());
    }

    @FXML
    protected void handlePicLocationAction(ActionEvent event) {
        LOG.log(Level.FINE, "handlePicLocationAction with event {0}", new Object[]{event});
        directoryChooser.setInitialDirectory(new File(Configuration.getProjectsParentFolder()));
        directoryChooser.setTitle("Select Timelines Parent Directory");
        File directory = directoryChooser.showDialog(timelinesLocationField.getScene().getWindow());
        if (directory != null) {
            String newLocation = directory.getAbsolutePath();
            Configuration.setProjectsParentFolder(newLocation);
        }
    }

    @FXML
    protected void handleConfigurationViewOK(ActionEvent event) {
        LOG.log(Level.FINE, "handleConfigurationViewOK with event {0}", new Object[]{event});
        propertyChangeSupport.firePropertyChange(CLOSE_REQUESTED, null, this);
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void handleGlobalConfigurationChanged(PropertyChangeEvent event) {
        LOG.log(Level.FINE, "handleGlobalConfigurationChanged with event {0}", new Object[]{event});
        switch (event.getPropertyName()) {
            case Configuration.TIMELINES_FOLDER_LOCATION_CHANGED ->
                runLater(() -> timelinesLocationField.setText((String) event.getNewValue()));
            default ->
                throw new UnsupportedOperationException("Unsupported configuration change : " + event);
        }
    }
}
