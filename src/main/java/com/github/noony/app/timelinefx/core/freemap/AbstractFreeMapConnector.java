/*
 * Copyright (C) 2023 NoOnY
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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.IFriezeObject;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public abstract class AbstractFreeMapConnector implements GridPositionable, Selectable, IFriezeObject {

    public static final Color DEFAULT_COLOR = Color.BLACK;

    private final long id;
    private Color color;

    public AbstractFreeMapConnector() {
        id = FriezeObjectFactory.getNextID();
        FriezeObjectFactory.addObject(AbstractFreeMapConnector.this);
        color = DEFAULT_COLOR;
    }

    public AbstractFreeMapConnector(long andId) {
        id = andId;
        if (!FriezeObjectFactory.isIdAvailable(andId)) {
            throw new IllegalStateException("Cannot create Abstract connector with existing id=" + Long.toString(andId));
        }
    }

    protected void setColor(Color aColor) {
        color = aColor;
    }

    public Color getColor() {
        return color;
    }

    public abstract double getDate();

    public abstract double getPlotSize();

    public abstract long getLinkedElementID();

    /**
     * {@inheritdoc}
     */
    @Override
    public final long getId() {
        return id;
    }

}
