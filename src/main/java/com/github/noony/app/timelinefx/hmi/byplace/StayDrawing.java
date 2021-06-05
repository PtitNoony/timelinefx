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
package com.github.noony.app.timelinefx.hmi.byplace;

import com.github.noony.app.timelinefx.core.StayPeriod;
import java.beans.PropertyChangeEvent;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author hamon
 */
public class StayDrawing {

    public static final double STROKE_WIDTH = 6;
    public static final double SELECTED_STROKE_WIDTH = 12;
    public static final double DEFAULT_SEPARATION = 6;

    private final StayPeriod stayPeriod;

    private final Line line;

    public StayDrawing(StayPeriod stay) {
        stayPeriod = stay;
        line = new Line();
        line.setStroke(stayPeriod.getPerson().getColor());
//        line.setFill(stayPeriod.getPerson().getColor());
//        line.set
        line.setStrokeWidth(STROKE_WIDTH);
        line.setStrokeType(StrokeType.CENTERED);
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        //
        stayPeriod.getPerson().addPropertyChangeListener(StayDrawing.this::handlePersonChanged);
        //
        line.setOnMouseEntered(event -> {
            stayPeriod.getPerson().setSelected(true);
        });
        line.setOnMouseExited(event -> {
            stayPeriod.getPerson().setSelected(false);
        });
    }

    public Node getLine() {
        return line;
    }

    public StayPeriod getStayPeriod() {
        return stayPeriod;
    }

    public void updateDateRatio(long minDate, double ratio) {
        double startX = (stayPeriod.getStartDate() - minDate) * ratio;
        line.setStartX(startX);
        double endX = (stayPeriod.getEndDate() - minDate) * ratio;
        endX = Math.max(startX + 1, endX);
        line.setEndX(endX);
    }

    public void setY(double y) {
        line.setStartY(y);
        line.setEndY(y);
    }

    private void handlePersonChanged(PropertyChangeEvent event) {
        if (stayPeriod.getPerson().isSelected()) {
            line.setStrokeWidth(SELECTED_STROKE_WIDTH);
        } else {
            line.setStrokeWidth(STROKE_WIDTH);
        }
    }
}
