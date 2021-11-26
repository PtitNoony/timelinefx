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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

/**
 *
 * @author hamon
 */
public class CustomModalWindow {

    private final Window parentWindow;
    private final Stage modalStage;
    private final Scene modalScene;
    private final JMetro jMetro;

    public CustomModalWindow(Window aParentWindow, Parent content) {
        parentWindow = aParentWindow;
        if (content != null) {
            content.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        }
        modalStage = StageFactory.createStage(parentWindow, StageStyle.UTILITY);
        modalStage.setResizable(true);
        modalScene = StageFactory.createScene(modalStage, content, "");
        jMetro = StageFactory.getJMetro(modalScene);
    }

    public void setRoot(Parent content) {
        content.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        modalScene.setRoot(content);
        jMetro.setParent(content);
        // Not nice code....
        jMetro.setScene(modalScene);
        jMetro.setStyle(Style.DARK);
        jMetro.reApplyTheme();
    }

    public void show() {
        modalStage.show();
        jMetro.reApplyTheme();
    }

    public void hide() {
        modalStage.hide();
    }
}
