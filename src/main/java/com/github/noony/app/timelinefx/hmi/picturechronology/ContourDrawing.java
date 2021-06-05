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
import static com.github.noony.app.timelinefx.core.picturechronology.PictureChronology.PERSON_CONTOUR_WIDTH;
import com.github.noony.app.timelinefx.drawings.IFxScalableNode;
import java.beans.PropertyChangeEvent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author hamon
 */
public class ContourDrawing implements IFxScalableNode {

    private final ChronologyPictureMiniature miniature;
    private final Person person;
    private Rectangle contourDrawing;
    //
    private double viewingScale = 1.0;

    protected ContourDrawing(ChronologyPictureMiniature aMiniature, Person aPerson) {
        miniature = aMiniature;
        person = aPerson;
        createMiniatureContour();
        miniature.addListener(ContourDrawing.this::handlePictureMiniatureChanges);
    }

    @Override
    public Node getNode() {
        return contourDrawing;
    }

    @Override
    public void updateScale(double newScale) {
        viewingScale = newScale;
        update();
    }

    private void createMiniatureContour() {
        contourDrawing = new Rectangle();
        contourDrawing.setFill(person.getColor());
        contourDrawing.setStroke(person.getColor());
        contourDrawing.setStroke(Color.WHITESMOKE);
        contourDrawing.setStrokeType(StrokeType.CENTERED.CENTERED);
        update();
    }

    private void update() {
        double scale = miniature.getScale();
        int index = miniature.getPersonIndex(person);
//        var deltaSize = (1 + index * 2) * PERSON_CONTOUR_WIDTH;
        var deltaPos = -(1 + index) * PERSON_CONTOUR_WIDTH;
        var deltaSize = - 2 * deltaPos;
        double width = (miniature.getPicture().getWidth() * scale + deltaSize) * viewingScale;
        double height = (miniature.getPicture().getHeight() * scale + deltaSize) * viewingScale;
//        double centerX = miniature.getPosition().getX() - width / 2.0;
//        double centerY = miniature.getPosition().getY() - height / 2.0;
        double centerX = deltaPos * viewingScale;
        double centerY = deltaPos * viewingScale;
        contourDrawing.setWidth(width);
        contourDrawing.setHeight(height);
        contourDrawing.setX(centerX);
        contourDrawing.setY(centerY);
//            contourDrawing.setArcWidth(ChronologyPictureMiniatureDrawing.ARC_WIDTH * index * 0.8);
//            contourDrawing.setArcHeight(ChronologyPictureMiniatureDrawing.ARC_WIDTH * index * 0.8);
        contourDrawing.setArcWidth(ChronologyPictureMiniatureDrawing.ARC_WIDTH * viewingScale);
        contourDrawing.setArcHeight(ChronologyPictureMiniatureDrawing.ARC_WIDTH * viewingScale);
        contourDrawing.setStrokeWidth(1.5 * viewingScale);
    }

    private void handlePictureMiniatureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED, ChronologyPictureMiniature.SCALE_CHANGED ->
                update();
            default ->
                throw new UnsupportedOperationException("handlePictureMiniatureChanges :: " + event.getPropertyName());
        }
    }
}
