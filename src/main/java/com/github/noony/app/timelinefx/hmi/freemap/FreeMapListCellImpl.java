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
package com.github.noony.app.timelinefx.hmi.freemap;
//package javafx.scene.control.cell;

import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.hmi.FriezeViewController;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

/**
 *
 * @author hamon
 */
public class FreeMapListCellImpl extends TextFieldListCell<FriezeFreeMap> {

    private class FreeMapStringConverter extends StringConverter<FriezeFreeMap> {

        private final FriezeViewController controller;

        public FreeMapStringConverter(FriezeViewController aController) {
            controller = aController;
        }

        @Override
        public String toString(FriezeFreeMap t) {
            return t.getName();
        }

        @Override
        public FriezeFreeMap fromString(String string) {
            var freeMap = controller.getSelectedFreeMap();
            freeMap.setName(string);
            return freeMap;
        }

    }

    public FreeMapListCellImpl(FriezeViewController controller) {
        super();
        setConverter(new FreeMapStringConverter(controller));
    }

}
