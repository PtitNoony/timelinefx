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
package com.github.noony.app.timelinefx.core.freemap;

/**
 *
 * @author hamon
 */
public class PortraitLink extends Link {

    private final FreeMapPortrait freeMapPortrait;

    public PortraitLink(FreeMapPortrait aFreeMapPortrait, Plot aBeginPlot, Plot aEndPlot) {
        super(aBeginPlot, aEndPlot, LinkType.PORTRAIT, aFreeMapPortrait.getPerson().getColor());
        freeMapPortrait = aFreeMapPortrait;
    }

    public FreeMapPortrait getFreeMapPortrait() {
        return freeMapPortrait;
    }

    @Override
    public String getInfo() {
        return "Link for " + freeMapPortrait.getPerson().getName() + ":: " + freeMapPortrait.getPortrait().getName();
    }

}
