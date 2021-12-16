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

import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.utils.MathUtils;

/**
 *
 * @author hamon
 */
public enum ChronologyLinkType {

    LINE(4), QUAD(-1), CUBIC(8);

    private int nbParameters;

    private ChronologyLinkType(int numberOfParameters) {
        nbParameters = numberOfParameters;
    }

    public int getNbParameters() {
        return nbParameters;
    }

    public static final double DEFAULT_CUBIC_RADIUS_PERCENTAGE = 0.75;

    public static double[] getDefaultParameters(ChronologyLinkType aType, ChronologyPictureMiniature startMiniature, ChronologyPictureMiniature endMiniature, Person person) {
        var startPoint = ChronologyLink.calculateDefaultStartPosition(startMiniature, startMiniature.getPersonIndex(person));
        var endPoint = ChronologyLink.calculateDefaultEndPosition(endMiniature, endMiniature.getPersonIndex(person));
        var startAngle = MathUtils.getAngle(startMiniature.getPosition(), startPoint);
        var startDistance = startPoint.distance(startMiniature.getPosition());
        var endAngle = MathUtils.getAngle(endMiniature.getPosition(), endPoint);
        var endDistance = endPoint.distance(endMiniature.getPosition());
        var sX = startPoint.getX();
        var eX = endPoint.getX();
        switch (aType) {
            case LINE -> {
                return new double[]{startAngle, startDistance, endAngle, endDistance};
            }
            case QUAD -> {
            }
            case CUBIC -> {
                var rX = (eX - sX) * DEFAULT_CUBIC_RADIUS_PERCENTAGE;
                return new double[]{startAngle, startDistance, 0, rX, -Math.PI, rX, endAngle, endDistance};
            }
            default ->
                throw new UnsupportedOperationException();
        }
        return null;
    }

}
