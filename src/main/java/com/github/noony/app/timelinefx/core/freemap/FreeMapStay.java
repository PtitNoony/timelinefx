/*
 * Copyright (C) 2023 NoOnY
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

import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;

/**
 *
 * @author hamon
 */
public interface FreeMapStay extends FreeMapLink {

    public static final Comparator<? super FreeMapStay> STAY_START_COMPARATOR = comparingDouble(FreeMapStay::getStartDate);

    public static final Comparator<? super FreeMapStay> STAY_END_COMPARATOR = comparingDouble(FreeMapStay::getEndDate);

    double getStartDate();

    double getEndDate();

    FreeMapPlot getStartPlot();

    FreeMapPlot getEndPlot();

    FreeMapPlace getPlace();

    boolean containsStay(StayPeriod anotherStay);

    void setPlotVisibility(boolean visibility);

    List<StayPeriod> getStayPeriods();

    List<FreeMapStay> getFreeMapStayPeriods();

}
