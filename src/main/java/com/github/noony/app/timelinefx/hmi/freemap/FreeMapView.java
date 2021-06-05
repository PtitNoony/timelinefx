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
package com.github.noony.app.timelinefx.hmi.freemap;

import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author hamon
 */
public class FreeMapView {

    private final FriezeFreeMap freeMap;
    //
    private Node mainNode;
    private FreeMapViewController freeMapViewController;

    public FreeMapView(FriezeFreeMap aFreeMap) {
        freeMap = aFreeMap;
        init();
    }

    public Node getNode() {
        return mainNode;
    }

    private void init() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("FreeMapView.fxml"));
            mainNode = loader.load();
            freeMapViewController = loader.getController();
            freeMapViewController.setFriezeFreeMap(freeMap);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "{0}", ex);
        }
    }
}
