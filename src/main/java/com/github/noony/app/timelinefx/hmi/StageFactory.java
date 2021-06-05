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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

/**
 *
 * @author hamon
 */
public class StageFactory {

    public static final double DEFAULT_SCENE_WIDTH = 1600;
    public static final double DEFAULT_SCENE_HEIGHT = 900;
    //
    private static final Logger LOG = Logger.getGlobal();
    //
    private static final boolean DEFAULT_LAF = false;
    private static final Map<Scene, JMetro> SCENE_STYLES = new HashMap<>();

    public static Stage createStage(Window aParentWindow, StageStyle stageStyle) {
        var stage = new Stage();
        stage.initStyle(stageStyle);
        if (aParentWindow != null) {
            stage.initOwner(aParentWindow);
        }
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    public static Scene createScene(Stage stage, Parent root, String title, double width, double height) {
        var scene = new Scene(root, width, height);
        stage.setTitle(title);
        stage.setScene(scene);
        if (!DEFAULT_LAF) {
            JMetro jmetro = new JMetro(scene, Style.DARK);
            SCENE_STYLES.put(scene, jmetro);
//            jmetro.setStyle(Style.LIGHT);
        } else {
            scene.getStylesheets().add("/styles/Styles.css");
        }
        return scene;
    }

    public static Scene createScene(Stage stage, Parent root, String title) {
        var scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        if (!DEFAULT_LAF) {
            JMetro jmetro = new JMetro(scene, Style.DARK);
            SCENE_STYLES.put(scene, jmetro);
//            jmetro.setStyle(Style.LIGHT);
        } else {
            scene.getStylesheets().add("/styles/Styles.css");
        }
        return scene;
    }

    public static JMetro getJMetro(Scene aScene) {
        return SCENE_STYLES.get(aScene);
    }
}
