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

import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;

/**
 *
 * @author hamon
 */
public class PortraitLink extends FreeMapSimpleLink {

    private final FreeMapPortrait freeMapPortrait;

    protected PortraitLink(long anID, FreeMapPortrait aFreeMapPortrait, FreeMapConnector aStayConnector) {
        super(anID, aFreeMapPortrait.getPerson(), aFreeMapPortrait.getConnector(), aStayConnector, LinkType.PORTRAIT,
                aFreeMapPortrait.getColor(), LinkShape.SIMPLE_LINE);
        freeMapPortrait = aFreeMapPortrait;
        getBeginConnector().setColor(freeMapPortrait.getColor());
        getEndConnector().setColor(freeMapPortrait.getColor());
    }

    public FreeMapPortrait getFreeMapPortrait() {
        return freeMapPortrait;
    }

    @Override
    public String getInfo() {
        return "ConnectorLink for " + freeMapPortrait.getPerson().getName() + ":: " + freeMapPortrait.getPortrait().getName();
    }

}