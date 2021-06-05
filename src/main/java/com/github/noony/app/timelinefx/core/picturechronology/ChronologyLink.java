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
package com.github.noony.app.timelinefx.core.picturechronology;

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.Person;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class ChronologyLink extends FriezeObject {

    public static final ChronologyLinkType DEFAULT_LINK_TYPE = ChronologyLinkType.CUBIC;

    public static final String PLOTS_UPDATED = "plotUpdated";

    private static final double PLOT_SEPARATION = 25;

    private final PropertyChangeSupport propertyChangeSupport;

    private final Person person;
    private final ChronologyPictureMiniature startMiniature;
    private final ChronologyPictureMiniature endMiniature;
    //
    private ChronologyLinkType linkType;
    //
    private int startIndex;
    private int endIndex;
    //
    private Point2D startPosition;
    private Point2D endPosition;

    protected ChronologyLink(long anId, Person aPerson, ChronologyPictureMiniature aStartMiniature, ChronologyPictureMiniature anEndMiniature, ChronologyLinkType aLinkType) {
        super(anId);
        propertyChangeSupport = new PropertyChangeSupport(ChronologyLink.this);
        person = aPerson;
        startMiniature = aStartMiniature;
        endMiniature = anEndMiniature;
        startIndex = startMiniature.getPersonIndex(person);
        endIndex = endMiniature.getPersonIndex(person);
        linkType = aLinkType;
        //
        updateStartPosition();
        updateEndPosition();
        //
        startMiniature.addListener(ChronologyLink.this::handleStartMiniatureChanges);
        endMiniature.addListener(ChronologyLink.this::handleEndMiniatureChanges);
    }

    public Person getPerson() {
        return person;
    }

    public ChronologyPictureMiniature getStartMiniature() {
        return startMiniature;
    }

    public ChronologyPictureMiniature getEndMiniature() {
        return endMiniature;
    }

    public Point2D getStartPosition() {
        return startPosition;
    }

    public Point2D getEndPosition() {
        return endPosition;
    }

    public ChronologyLinkType getLinkType() {
        return linkType;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void updateStartPosition() {
        var startMiniaturePosition = startMiniature.getPosition();
        startPosition = new Point2D(
                startMiniaturePosition.getX() + startMiniature.getWidth() / 2.0,
                startMiniaturePosition.getY() - startMiniature.getHeight() / 2.0 + (startIndex + 1) * PLOT_SEPARATION);
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, startMiniaturePosition);
    }

    private void updateEndPosition() {
        var endMiniaturePosition = endMiniature.getPosition();
        endPosition = new Point2D(
                endMiniaturePosition.getX() - endMiniature.getWidth() / 2.0,
                endMiniaturePosition.getY() - endMiniature.getHeight() / 2.0 + (endIndex + 1) * PLOT_SEPARATION);
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, endMiniaturePosition);
    }

    private void handleStartMiniatureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED, ChronologyPictureMiniature.SCALE_CHANGED -> {
                updateStartPosition();
            }
        }
    }

    private void handleEndMiniatureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED, ChronologyPictureMiniature.SCALE_CHANGED -> {
                updateEndPosition();
            }
        }

    }

}
