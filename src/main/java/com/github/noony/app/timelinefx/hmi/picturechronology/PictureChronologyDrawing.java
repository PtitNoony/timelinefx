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
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.drawings.FxScalableParent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import static javafx.application.Platform.runLater;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hamon
 */
public class PictureChronologyDrawing extends FxScalableParent {

    private final PictureChronology pictureChronology;
    private final Map<ChronologyPictureMiniature, ChronologyPictureMiniatureDrawing> miniatureDrawings;
    private final Map<Person, PersonChronologyPicturesDrawing> personsDrawings;
    //
    private final PropertyChangeListener personListener;
    //
    private final Group drawingGroup;
    private final Rectangle drawingBackground;
    private final Group picturesGroup;
    private final Group personsGroup;
    //
    private ChronologyLinkDrawing selectedLink = null;

    public PictureChronologyDrawing(PictureChronology aPictureChronology) {
        super(aPictureChronology);
        pictureChronology = aPictureChronology;
        miniatureDrawings = new HashMap<>();
        personsDrawings = new HashMap<>();
        //
        pictureChronology.addListener(PictureChronologyDrawing.this::handlePictureChronologyChanges);
        personListener = PictureChronologyDrawing.this::handlePersonEvents;
        //
        drawingGroup = new Group();
        //
        drawingBackground = new Rectangle();
        drawingBackground.setFill(Color.BLACK);
        drawingBackground.setOnScroll(event -> {
            if (event.isAltDown()) {
                if (event.getDeltaY() < 0) {
                    zoomOut();
                } else {
                    zoomIn();
                }
                event.consume();
            }
        });
        //
        picturesGroup = new Group();
        personsGroup = new Group();
        //
        drawingGroup.getChildren().addAll(drawingBackground, picturesGroup, personsGroup);
        addNode(drawingGroup);
        //
        pictureChronology.getChronologyPictures().forEach(miniature -> {
            addChronologyPictureMiniature(miniature);
        });
        //
        runLater(() -> updateLayout());
    }

    private void addChronologyPictureMiniature(ChronologyPictureMiniature aChronologyPictureMiniature) {
        ChronologyPictureMiniatureDrawing miniatureDrawing = new ChronologyPictureMiniatureDrawing(aChronologyPictureMiniature, this);
        registerScalableNode(miniatureDrawing);
        miniatureDrawings.put(aChronologyPictureMiniature, miniatureDrawing);
        picturesGroup.getChildren().add(miniatureDrawing.getNode());
        //
        aChronologyPictureMiniature.getPicture().getPersons().stream()
                .forEach(p -> {
                    PersonChronologyPicturesDrawing personDrawing;
                    if (!personsDrawings.containsKey(p)) {
                        personDrawing = new PersonChronologyPicturesDrawing(this, p);
                        registerScalableNode(personDrawing);
                        personsDrawings.put(p, personDrawing);
                        personsGroup.getChildren().add(personDrawing.getNode());
                        personDrawing.addListener(personListener);
                    }
                });
    }

    private void removeChronologyPictureMiniature(ChronologyPictureMiniature aChronologyPictureMiniature) {
        var miniatureDrawing = miniatureDrawings.remove(aChronologyPictureMiniature);
        unregisterScalableNode(miniatureDrawing);
        picturesGroup.getChildren().remove(miniatureDrawing.getNode());
        // IMPR in the future, rely on the PictureChronology calculations?
        var existingPersons = miniatureDrawings.keySet().stream()
                .flatMap(c -> c.getPersons().stream()).toList();
        var personInExcedent = personsDrawings.keySet().stream()
                .filter(p -> !existingPersons.contains(p)).toList();
        personInExcedent.forEach(p -> {
            var personDrawing = personsDrawings.remove(p);
            unregisterScalableNode(personDrawing);
            personsGroup.getChildren().remove(personDrawing.getNode());
        });
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        drawingBackground.setWidth(pictureChronology.getWidth() * getScale());
        drawingBackground.setHeight(pictureChronology.getHeight() * getScale());
        drawingGroup.setTranslateX(PADDING * getScale());
        drawingGroup.setTranslateY(PADDING * getScale());
    }

    public void setPicturesVisibility(boolean visibility) {
        miniatureDrawings.values().forEach(miniature -> miniature.setPictureVisibility(visibility));
    }

    protected PictureChronology getPictureChronology() {
        return pictureChronology;
    }

    private void handlePictureChronologyChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronology.NAME_CHANGED -> {
                // nothing to do
            }
            case PictureChronology.PICTURE_ADDED ->
                addChronologyPictureMiniature((ChronologyPictureMiniature) event.getNewValue());
            case PictureChronology.PICTURE_REMOVED ->
                removeChronologyPictureMiniature((ChronologyPictureMiniature) event.getNewValue());
            case PictureChronology.LAYOUT_CHANGED ->
                updateLayout();
            case PictureChronology.LINK_ADDED, PictureChronology.LINK_REMOVED -> {
                // nothing to do links are managed by the PersonChronologyPictureDrawing class
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
        }
    }

    private void handlePersonEvents(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyLinkDrawing.LINK_CLICKED -> {
                var link = (ChronologyLinkDrawing) event.getNewValue();
                selectLink(link);
            }
            default ->
                throw new UnsupportedOperationException();
        }
    }

    private void selectLink(ChronologyLinkDrawing linkDrawing) {
        if (selectedLink == linkDrawing) {
            selectedLink.displayControls(false);
            selectedLink = null;
        } else {
            if (selectedLink != null) {
                selectedLink.displayControls(false);
            }
            selectedLink = linkDrawing;
            selectedLink.displayControls(true);
        }
    }

}
