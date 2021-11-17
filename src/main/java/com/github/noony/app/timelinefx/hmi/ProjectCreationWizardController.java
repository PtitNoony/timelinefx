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
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author hamon
 */
public class ProjectCreationWizardController implements Initializable {

    public static final String CANCEL = "cancel";
    public static final String CREATE = "create";

    private DirectoryChooser directoryChooser = null;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @FXML
    private Button createB;
    @FXML
    private TextField nameField;
    @FXML
    private TextField directoryField;
    @FXML
    private TextField portraitsField;
    @FXML
    private TextField picturesField;
    @FXML
    private TextField miniaturesField;

    private String name = "";
    private String projectFolderPath = "";
    private String portraitsFolderName = "";
    private String picturesFolderName = "";
    private String miniaturesFolderName = "";
    //
    private File incompleteProjectFolder = null;
    private boolean isOK = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            name = t1.trim();
            if (incompleteProjectFolder != null) {
                projectFolderPath = incompleteProjectFolder.getAbsolutePath() + File.separator + name;
            }
            directoryField.setText(projectFolderPath);
            update();
        });
        portraitsField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            portraitsFolderName = t1.trim();
            update();
        });
        picturesField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            picturesFolderName = t1.trim();
            update();
        });
        miniaturesField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            miniaturesFolderName = t1.trim();
            update();
        });
        reset();
    }

    protected void reset() {
        nameField.setText("");
        incompleteProjectFolder = new File(Configuration.getProjectsParentFolder());
        projectFolderPath = Configuration.getProjectsParentFolder();
        portraitsFolderName = Configuration.getPortraitsFolder();
        picturesFolderName = Configuration.getPicturesFolder();
        miniaturesFolderName = Configuration.getMiniaturesFolder();
        //
        directoryField.setText(projectFolderPath);
        portraitsField.setText(portraitsFolderName);
        picturesField.setText(picturesFolderName);
        miniaturesField.setText(miniaturesFolderName);
        update();
    }

    @FXML
    protected void handleDirectoryButton(ActionEvent event) {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
        }
        directoryChooser.setInitialDirectory(new File(directoryField.getText()));
        File newDirectory = directoryChooser.showDialog(directoryField.getScene().getWindow());
        if (newDirectory != null) {
            incompleteProjectFolder = newDirectory;
            projectFolderPath = incompleteProjectFolder.getAbsolutePath();
            directoryField.setText(projectFolderPath);
        }
        update();
    }

    @FXML
    protected void handleCancel(ActionEvent event) {
        reset();
        propertyChangeSupport.firePropertyChange(CANCEL, null, this);
    }

    @FXML
    protected void handleCreate(ActionEvent event) {
        Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_NAME_KEY, name,
                TimeLineProject.PROJECT_FOLDER_KEY, projectFolderPath,
                TimeLineProject.PORTRAIT_FOLDER_KEY, portraitsFolderName,
                TimeLineProject.PICTURES_FOLDER_KEY, picturesFolderName,
                TimeLineProject.MINIATURES_FOLDER_KEY, miniaturesFolderName
        );
        propertyChangeSupport.firePropertyChange(CREATE, this, configParams);
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void update() {
        isOK = !name.isEmpty()
                && !projectFolderPath.isEmpty()
                && !portraitsFolderName.isEmpty() && !picturesFolderName.isEmpty() && !miniaturesFolderName.isEmpty();
        createB.setDisable(!isOK);
    }
}
