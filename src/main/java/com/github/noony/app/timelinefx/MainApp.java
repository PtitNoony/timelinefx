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
package com.github.noony.app.timelinefx;

import com.github.noony.app.timelinefx.hmi.AppInstanceConfiguration;
import com.github.noony.app.timelinefx.hmi.ProjectViewController;
import com.github.noony.app.timelinefx.hmi.StageFactory;
import com.github.noony.app.timelinefx.utils.CustomProfiler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

    private static final String STAGE_TITLE = "Timeline";
    private static final Logger LOG = Logger.getGlobal();

    static {
        var stream = MainApp.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
            LOG.setLevel(Level.FINEST);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not load loader properties :: {0}", e.getCause());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        CustomProfiler.start("MainApp_Start");
        var loader = new FXMLLoader(ProjectViewController.class.getResource("ProjectView.fxml"));
        Parent root = loader.load();
        var controller = loader.getController();
        LOG.log(Level.FINE, "ProjectViewController {0}", controller);
        //
        StageFactory.createScene(stage, root, STAGE_TITLE, StageFactory.DEFAULT_SCENE_WIDTH, StageFactory.DEFAULT_SCENE_HEIGHT);
        //
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image("icon.png"));
        stage.show();
        stage.setMaximized(true);
        stage.setOnCloseRequest((WindowEvent t) -> {
            LOG.log(Level.INFO, "! *************");
            LOG.log(Level.INFO, "! > {0}", new Object[]{CustomProfiler.toStringValue()});
            LOG.log(Level.INFO, "! *************");
        });
//        stage.onCloseRequestProperty().addListener((ObservableValue<? extends EventHandler<WindowEvent>> ov, EventHandler<WindowEvent> t, EventHandler<WindowEvent> t1) -> {
//            System.err.println(" *************");
//            System.err.println(" > " + CustomProfiler.toStringValue());
//            System.err.println(" *************");
//        });
        //
        LOG.log(Level.INFO, "java.version: {0}", System.getProperty("java.version"));
        LOG.log(Level.INFO, "javafx.version: {0}", System.getProperty("javafx.version"));
        //
        AppInstanceConfiguration.addPropertyChangeListener(e -> {
            if ((e.getPropertyName() == null ? AppInstanceConfiguration.FRIEZE_SELECTED_CHANGED == null : e.getPropertyName().equals(AppInstanceConfiguration.FRIEZE_SELECTED_CHANGED))
                    || (e.getPropertyName() == null ? AppInstanceConfiguration.TIMELINE_SELECTED_CHANGED == null : e.getPropertyName().equals(AppInstanceConfiguration.TIMELINE_SELECTED_CHANGED))) {
                StringBuilder sb = new StringBuilder(STAGE_TITLE);
                if (AppInstanceConfiguration.getSelectedProject() != null) {
                    sb.append(" / ").append(AppInstanceConfiguration.getSelectedProject().getName());
                    if (AppInstanceConfiguration.getSelectedFrieze() != null) {
                        sb.append(":: ").append(AppInstanceConfiguration.getSelectedFrieze().getName());
                    }
                }
                stage.setTitle(sb.toString());
            }
        });
        //
        CustomProfiler.stop("MainApp_Start");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be launched through deployment
     * artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
