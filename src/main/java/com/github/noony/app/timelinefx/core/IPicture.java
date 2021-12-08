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
package com.github.noony.app.timelinefx.core;

import java.util.List;

/**
 *
 * @author hamon
 */
public interface IPicture extends IDateObject, IFileObject {

    String NAME_CHANGED = "pictureNameChanged";
    String DATE_CHANGED = "pictureDateChanged";
    String PERSON_ADDED = "picturePersonAdded";
    String PERSON_REMOVED = "picturePersonRemoved";
    String PERSONS_REORDED = "picturePersonsReordered";
    String PLACE_ADDED = "picturePlaceAdded";
    String PLACE_REMOVED = "picturePlaceRemoved";

    static IPicture getPicture(long pictureID) {
        var picture = PictureFactory.getPicture(pictureID);
        if (picture != null) {
            return picture;
        }
        return PortraitFactory.getPortrait(pictureID);
    }

    List<Person> getPersons();

    boolean addPerson(Person aPerson);

    boolean removePerson(Person aPerson);

    List<Place> getPlaces();

    boolean addPlace(Place aPlace);

    boolean removePlace(Place aPlace);

    int getWidth();

    int getHeight();
}
