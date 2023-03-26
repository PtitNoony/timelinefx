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
package com.github.noony.app.timelinefx.core.freemap.connectors;

import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapStay;

/**
 *
 * @author hamon
 */
public class EndPlot extends FreeMapPlot {

    private final FreeMapStay period;

    protected EndPlot(long anID, FreeMapStay stayPeriod, FreeMapPerson freeMapPerson, FreeMapPlace freeMapPlace, double plotSize) {
        super(anID, freeMapPerson, freeMapPlace, stayPeriod.getEndDate(), PlotType.END, stayPeriod.getId(), plotSize);
        period = stayPeriod;
    }

//    public StayPeriod getStayPeriod() {
//        return period;
//    }

    @Override
    public String getInfo() {
        return "end";
    }
}
