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
 * @author arnaud
 */
public class PersonInitLink {

    private final Person person;
    private final Plot firstPlot;

    public PersonInitLink(Person aPerson, FriezeFreeMap freeMap) {
        person = aPerson;
        firstPlot = freeMap.getPlots().stream().filter(plot -> plot.getPerson().equals(person)).sorted((p1, p2) -> Long.compare(p1.getDate(), p2.getDate())).findFirst().orElse(null);
        if (firstPlot == null) {
            throw new IllegalStateException();
        }
    }

    public Plot getFirstPlot() {
        return firstPlot;
    }

    public Person getPerson() {
        return person;
    }

}
