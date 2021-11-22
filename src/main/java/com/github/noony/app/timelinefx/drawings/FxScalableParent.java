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
package com.github.noony.app.timelinefx.drawings;

import com.github.noony.app.timelinefx.core.DrawableObject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class FxScalableParent implements IFxScalableNode {
    
    
    public static final double MIN_SCALE = 0.2;
    public static final double MAX_SCALE = 20;
    public static final double SCALE_STEP = 0.1;

    public static final double BORDER = 0;//todo investigate why size does not take this into account
    public static final double PADDING = 8;

    private final PropertyChangeSupport propertyChangeSupport;

    private final Pane mainNode;
    private final Group mainGroup;
    //
    private final Rectangle background;
    //
    private final List<IFxScalableNode> scalableNodes;
    //
    private final DrawableObject drawableObject;
    //
    private double drawingWidth;
    private double drawingHeight;
    //
    private double viewingScale = 1.0;

    public FxScalableParent(DrawableObject aDrawableObject) {
        drawableObject = aDrawableObject;
        //
        scalableNodes = new LinkedList<>();
        //
        drawingWidth = drawableObject.getWidth() + 2 * (BORDER + PADDING);
        drawingHeight = drawableObject.getHeight() + 2 * (BORDER + PADDING);
        //
        propertyChangeSupport = new PropertyChangeSupport(FxScalableParent.this);
        //
        mainNode = new Pane();
        mainGroup = new Group();
        //
        background = new Rectangle(500, 500);
        background.setFill(Color.BLACK);
        //
        initFx();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    protected final void addNode(Object anObject){
        if (anObject instanceof Node node){
            mainGroup.getChildren().add(node);
        }else if(anObject instanceof IFxScalableNode scalableNode){
            registerScalableNode(scalableNode);
            mainGroup.getChildren().add(scalableNode.getNode());
        }
    }

    protected void registerScalableNode(IFxScalableNode scalableNode){
        scalableNodes.add(scalableNode);
    }

    @Override
    public Node getNode() {
        return mainNode;
    }

    @Override
    public double getScale() {
        return viewingScale;
    }

    @Override
    public void updateScale(double newScale) {
        if (newScale > MAX_SCALE) {
            viewingScale = MAX_SCALE;
        } else if (newScale < MIN_SCALE) {
            viewingScale = MIN_SCALE;
        } else {
            viewingScale = newScale;
        }
        updateLayout();
    }

    public void zoomIn() {
        viewingScale = Math.min(MAX_SCALE, viewingScale + SCALE_STEP);
        updateLayout();
    }

    public void zoomOut() {
        viewingScale = Math.max(MIN_SCALE, viewingScale - SCALE_STEP);
        updateLayout();
    }

    protected double getDrawingWidth() {
        return drawingWidth;
    }

    protected double getDrawingHeight() {
        return drawingHeight;
    }

    private void updateWidth() {
        drawingWidth = (drawableObject.getWidth() + 2 * (PADDING)) * viewingScale;
        background.setWidth(drawingWidth);
    }

    private void updateHeight() {
        drawingHeight = (drawableObject.getHeight() + 2 * (PADDING)) * viewingScale;
        background.setHeight(drawingHeight);
    }

    public double getWidth() {
        return (drawingWidth + 2.0 * BORDER) * viewingScale;
    }

    public double getHeight() {
        return (drawingHeight + 2.0 * BORDER) * viewingScale;
    }

    private void initFx() {
        background.setFill(Color.BLACK);
        background.setArcWidth(PADDING);
        background.setArcHeight(PADDING);
        //
        mainNode.getChildren().addAll(mainGroup);
        mainGroup.getChildren().addAll(background);
        //
//        updateLayout();
    }

    public void updateLayout() {
        updateWidth();
        updateHeight();
        scalableNodes.forEach(node -> node.updateScale(viewingScale));
        //
        mainGroup.setTranslateX(BORDER * viewingScale);
        mainGroup.setTranslateY(BORDER * viewingScale);
    }

}
