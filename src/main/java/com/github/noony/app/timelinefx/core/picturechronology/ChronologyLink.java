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
import static com.github.noony.app.timelinefx.core.picturechronology.PictureChronology.PERSON_CONTOUR_WIDTH;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class ChronologyLink extends FriezeObject {

    public static final ChronologyLinkType DEFAULT_LINK_TYPE = ChronologyLinkType.CUBIC;

    public static final String PLOTS_UPDATED = "plotUpdated";

    private static final Logger LOG = Logger.getGlobal();
    private static final double PLOT_SEPARATION = 15;

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
    //
    private double[] linkParameters;

    protected ChronologyLink(long anId, Person aPerson, ChronologyPictureMiniature aStartMiniature, ChronologyPictureMiniature anEndMiniature, ChronologyLinkType aLinkType, double[] allLinkParameters) {
        super(anId);
        propertyChangeSupport = new PropertyChangeSupport(ChronologyLink.this);
        person = aPerson;
        startMiniature = aStartMiniature;
        endMiniature = anEndMiniature;
        startIndex = startMiniature.getPersonIndex(person);
        endIndex = endMiniature.getPersonIndex(person);
        linkType = aLinkType;
        // create a copy ?
        linkParameters = allLinkParameters;
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

    public double[] getLinkParameters() {
        return Arrays.copyOf(linkParameters, linkParameters.length);
    }

    public void updateLinkParameters(double[] newParameters) {
        switch (linkType) {
            case CUBIC -> {
                if (newParameters.length != linkType.getNbParameters()) {
                    LOG.log(Level.SEVERE, "In ChronologyLink, wrong number of parameters: expected {0} for type {1} be found {2}", new Object[]{linkType.getNbParameters(), linkType, newParameters.length});
                    return;
                }
                linkParameters = Arrays.copyOf(newParameters, newParameters.length);
                var startAngle = linkParameters[0];
                var startDistance = linkParameters[1];
                startPosition = new Point2D(startDistance * Math.cos(startAngle), startDistance * Math.sin(startAngle));
                var endAngle = linkParameters[6];
                var endDistance = linkParameters[7];
                endPosition = new Point2D(endDistance * Math.cos(endAngle), endDistance * Math.sin(endAngle));
            }
            default ->
                throw new UnsupportedOperationException("Link type not supported :: " + linkType);
        }
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, newParameters);
    }

    public void setStartPosition(Point2D newStartPosition) {
        startPosition = newStartPosition;
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, startPosition);
    }

    public void setEndPosition(Point2D newEndPosition) {
        endPosition = newEndPosition;
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, endPosition);
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

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public String getName() {
        return "Link: " + person.getName() + ":: " + startMiniature.getPicture().getName() + " -> " + endMiniature.getPicture().getName();
    }

    private void updateStartPosition() {
        startPosition = calculateDefaultStartPosition(startMiniature, startIndex);
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, startPosition);
    }

    private void updateEndPosition() {
        endPosition = calculateDefaultEndPosition(endMiniature, endIndex);
        propertyChangeSupport.firePropertyChange(PLOTS_UPDATED, this, endPosition);
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

    public static Point2D calculateDefaultStartPosition(ChronologyPictureMiniature aMiniature, int anIndex) {
        return new Point2D(
                aMiniature.getPosition().getX() + calculateDeltaXPosition(aMiniature, anIndex),
                calculateYPosition(aMiniature, anIndex));
    }

    public static Point2D calculateDefaultEndPosition(ChronologyPictureMiniature aMiniature, int anIndex) {
        return new Point2D(
                aMiniature.getPosition().getX() - calculateDeltaXPosition(aMiniature, anIndex),
                calculateYPosition(aMiniature, anIndex));
    }

    private static double calculateYPosition(ChronologyPictureMiniature aMiniature, int anIndex) {
        return aMiniature.getPosition().getY() - aMiniature.getHeight() / 3.0 * aMiniature.getScale() + (anIndex + 1) * PLOT_SEPARATION;
    }

    private static double calculateDeltaXPosition(ChronologyPictureMiniature aMiniature, int anIndex) {
        double scale = aMiniature.getScale();
        var deltaPos = -(1 + anIndex) * PERSON_CONTOUR_WIDTH;
        var deltaSize = - 2 * deltaPos;
        return (aMiniature.getPicture().getWidth() * scale + deltaSize) / 2.0;
    }

    public static double calculateDeltaXPosition(ChronologyPictureMiniature aMiniature, Person aPerson) {
        var deltaPos = -(1 + aMiniature.getPersonIndex(aPerson)) * PERSON_CONTOUR_WIDTH;
        return -deltaPos;
    }

}
