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
package com.github.noony.app.timelinefx.core.freemap.links;

import com.github.noony.app.timelinefx.core.freemap.FreeMapStay;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;

/**
 *
 * @author hamon
 */
public class FreeMapStayLink extends FreeMapSimpleLink {

    private final FreeMapStay period;

    protected FreeMapStayLink(long id, FreeMapStay aStay, FreeMapPlot aBeginPlot, FreeMapPlot aEndPlot) {
        super(id, aStay.getPerson(),aBeginPlot, aEndPlot, LinkType.STAY, aStay.getPerson().getPerson().getColor(),LinkShape.SIMPLE_LINE);
        period = aStay;
    }

    public FreeMapStay getStayPeriod() {
        return period;
    }

    @Override
    public String getInfo() {
        return "Link for " + period.getPerson().getName() + " @ " + period.getPlace().getName();
    }

}
