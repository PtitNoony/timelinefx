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
package com.github.noony.app.timelinefx.core;

import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public final class PersonFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<Person> FACTORY = new Factory<>();

    private PersonFactory() {
        // private utility constructor
    }

    public static void reset() {
        FACTORY.reset();
    }

    public static Person getPerson(long id) {
        return FACTORY.get(id);
    }

    public static Person createPerson(TimeLineProject project, String personName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating person with personName={0}  ", new Object[]{personName});
        var person = new Person(project, FACTORY.getNextID(), personName);
        FACTORY.addObject(person);
        return person;
    }

    public static Person createPerson(TimeLineProject project, String personName, Color color) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating person with personName={0} color={1} ", new Object[]{personName, color});
        var person = new Person(project, FACTORY.getNextID(), personName, color, null, null);
        FACTORY.addObject(person);
        return person;
    }

    public static Person createPerson(TimeLineProject project, long id, String personName, Color color) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating person with id={0} personName={1} color={2}", new Object[]{id, personName, color});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("trying to create person " + personName + " with existing id=" + id + " (exists : " + FACTORY.get(id) + "[" + FACTORY.get(id).getId() + "])");
        }
        var person = new Person(project, id, personName, color, null, null);
        FACTORY.addObject(person);
        return person;
    }

    public static List< Person> getPERSONS() {
        return FACTORY.getObjects().stream().sorted(Person.COMPARATOR).toList();
    }

}
