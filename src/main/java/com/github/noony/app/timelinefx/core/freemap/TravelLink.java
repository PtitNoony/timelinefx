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

import com.github.noony.app.timelinefx.core.Person;

/**
 *
 * @author hamon
 */
public class TravelLink extends Link {

    private final Person person;

    public TravelLink(Person aPerson, Plot aBeginPlot, Plot anEndPlot) {
        super(aBeginPlot, anEndPlot, LinkType.TRAVEL, aPerson.getColor());
        person = aPerson;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public String getInfo() {
        return "Travel of " + person.getName();
    }

    @Override
    public String toString() {
        return "Travel of " + person.getName() + " from: " + getBeginPlot() + "  to:" + getEndPlot();
    }

}
