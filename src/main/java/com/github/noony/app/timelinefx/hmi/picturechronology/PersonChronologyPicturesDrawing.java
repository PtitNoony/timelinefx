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
package com.github.noony.app.timelinefx.hmi.picturechronology;

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 *
 * @author hamon
 */
public class PersonChronologyPicturesDrawing implements IFxScalableNode {

    private final PictureChronology chronology;
    private final Map<ChronologyLink, ChronologyLinkDrawing> linkDrawings;
    //
    private final Group mainGroup;
    //
    private double viewingScale = 1.0;

    public PersonChronologyPicturesDrawing(PictureChronology aChronology, Person aPerson) {
        chronology = aChronology;
        chronology.addListener(PersonChronologyPicturesDrawing.this::handleChronologyChange);
        linkDrawings = new HashMap<>();
        mainGroup = new Group();
        chronology.getLinks().stream()
                .filter(s -> s.getPerson() == aPerson)
                .forEach(PersonChronologyPicturesDrawing.this::addLinkDrawing);
    }

    @Override
    public Node getNode() {
        return mainGroup;
    }

    @Override
    public double getScale() {
        return viewingScale;
    }

    @Override
    public void updateScale(double newScale) {
        viewingScale = newScale;
        updateLayout();
    }

    private void addLinkDrawing(ChronologyLink aLink) {
        var linkDrawing = new ChronologyLinkDrawing(aLink);
        linkDrawings.put(aLink, linkDrawing);
        mainGroup.getChildren().add(linkDrawing.getNode());
    }

    private void handleChronologyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronology.LINK_ADDED -> {
                var link = (ChronologyLink) event.getNewValue();
                addLinkDrawing(link);
            }
            case PictureChronology.LINK_REMOVED -> {
                var link = (ChronologyLink) event.getNewValue();
                var linkDrawing = linkDrawings.get(link);
                linkDrawings.remove(link);
                if (linkDrawing != null) {
                    mainGroup.getChildren().remove(linkDrawing.getNode());
                }
            }
            default -> {
            }
        }
    }

    private void updateLayout() {
        linkDrawings.values().forEach(linkDrawing -> linkDrawing.updateScale(viewingScale));
    }

}
