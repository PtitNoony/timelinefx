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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class Link implements Selectable {

    private static final Color DEFAULT_COLOR = Color.BLUEVIOLET;
    private static final String COLOR_CHANGED = "colorChanged";
    private static final String LINK_SHAPE_CHANGED = "linkShapeChanged";

    private final PropertyChangeSupport propertyChangeSupport;

    private final Plot beginPlot;
    private final Plot endPlot;
    private final LinkType linkType;
    //
    private LinkShape linkShape;
    private Color color;
    private boolean isSelected = false;

    public Link(Plot aBeginPlot, Plot aEndPlot, LinkType type, Color aColor, LinkShape aLinkShape) {
        propertyChangeSupport = new PropertyChangeSupport(Link.this);
        //
        beginPlot = aBeginPlot;
        endPlot = aEndPlot;
        linkType = type;
        color = aColor;
        linkShape = aLinkShape;
    }

    public Link(Plot aBeginPlot, Plot aEndPlot, LinkType type, Color aColor) {
        this(aBeginPlot, aEndPlot, type, aColor, LinkShape.QUAD_LINE);
    }

    public Link(Plot beginPlot, Plot endPlot, LinkType linkType) {
        this(beginPlot, endPlot, linkType, DEFAULT_COLOR);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        propertyChangeSupport.firePropertyChange(SELECTION_CHANGED, this, this.isSelected);
    }

    public Plot getBeginPlot() {
        return beginPlot;
    }

    public Plot getEndPlot() {
        return endPlot;
    }

    public LinkType getType() {
        return linkType;
    }

    public LinkShape getLinkShape() {
        return linkShape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color aColor) {
        color = aColor;
        propertyChangeSupport.firePropertyChange(COLOR_CHANGED, this, color);
    }

    public void setLinkShape(LinkShape aLinkShape) {
        linkShape = aLinkShape;
        propertyChangeSupport.firePropertyChange(LINK_SHAPE_CHANGED, this, linkShape);
    }

    @Override
    public String getInfo() {
        return "Link " + linkType + " from: " + beginPlot + "  to:" + endPlot;
    }

}
