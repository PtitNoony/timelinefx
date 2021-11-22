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
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.drawings.FxScalableParent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    private final List<ChronologyPictureMiniatureDrawing> miniatureDrawings;
    private final Map<Person, PersonChronologyPicturesDrawing> personsDrawings;
    //
    private final Group drawingGroup;
    private final Rectangle drawingBackground;
    private final Group picturesGroup;
    private final Group personsGroup;
    //

    public PictureChronologyDrawing(PictureChronology aPictureChronology) {
        super(aPictureChronology);
        pictureChronology = aPictureChronology;
        miniatureDrawings = new LinkedList<>();
        personsDrawings = new HashMap<>();
        //
        pictureChronology.addListener(PictureChronologyDrawing.this::handlePictureChronologyChanges);
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
        drawingGroup.getChildren().addAll(drawingBackground, personsGroup, picturesGroup);
        addNode(drawingGroup);
        //
        pictureChronology.getChronologyPictures().forEach(miniature -> {
            addChronologyPictureMiniature(miniature);
        });
        //
        runLater(() -> updateLayout());
    }

    private void addChronologyPictureMiniature(ChronologyPictureMiniature aChronologyPictureMiniature) {
        ChronologyPictureMiniatureDrawing miniatureDrawing = new ChronologyPictureMiniatureDrawing(aChronologyPictureMiniature);
        registerScalableNode(miniatureDrawing);
        miniatureDrawings.add(miniatureDrawing);
        picturesGroup.getChildren().add(miniatureDrawing.getNode());
        //
        aChronologyPictureMiniature.getPicture().getPersons().stream()
                .forEach(p -> {
                    PersonChronologyPicturesDrawing personDrawing;
                    if (!personsDrawings.containsKey(p)) {
                        personDrawing = new PersonChronologyPicturesDrawing(pictureChronology, p);
                        registerScalableNode(personDrawing);
                        personsDrawings.put(p, personDrawing);
                        personsGroup.getChildren().add(personDrawing.getNode());
            }
                });
    }

    private void addChronologyLink(ChronologyLink aChronologyLink) {
        System.err.println(" addChronologyLink ::" + aChronologyLink);
    }

    private void removeChronologyLink(ChronologyLink aChronologyLink) {
        System.err.println(" removeChronologyLink ::" + aChronologyLink);
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        drawingBackground.setWidth(pictureChronology.getWidth() * getScale());
        drawingBackground.setHeight(pictureChronology.getHeight() * getScale());
        drawingGroup.setTranslateX(PADDING * getScale());
        drawingGroup.setTranslateY(PADDING * getScale());
    }

    private void handlePictureChronologyChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureChronology.NAME_CHANGED -> {
                // nothing to do
            }
            case PictureChronology.PICTURE_ADDED ->
                addChronologyPictureMiniature((ChronologyPictureMiniature) event.getNewValue());
            case PictureChronology.LAYOUT_CHANGED ->
                updateLayout();
            case PictureChronology.LINK_ADDED ->
                addChronologyLink((ChronologyLink) event.getNewValue());
            case PictureChronology.LINK_REMOVED ->
                removeChronologyLink((ChronologyLink) event.getNewValue());
            default ->
                throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
        }
    }

}
