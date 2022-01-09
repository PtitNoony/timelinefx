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
 * An IPicture is a drawable object which has date attributes and is linked to an image * file.
 *
 * @author hamon
 */
public interface IPicture extends IDateObject, IFileObject, IDrawableObject {

    /**
     * Name of the property change event for name change
     */
    String NAME_CHANGED = "pictureNameChanged";
    /**
     * Name of the property change event when a person is added
     */
    String PERSON_ADDED = "picturePersonAdded";
    /**
     * Name of the property change event when a person is removed
     */
    String PERSON_REMOVED = "picturePersonRemoved";
    /**
     * Name of the property change event when persons are reordered
     */
    String PERSONS_REORDED = "picturePersonsReordered";
    /**
     * Name of the property change event when a place is added
     */
    String PLACE_ADDED = "picturePlaceAdded";
    /**
     * Name of the property change event when a place is removed
     */
    String PLACE_REMOVED = "picturePlaceRemoved";

    /**
     * Searches the various factories to find the corresponding object
     *
     * @param pictureID an object's id
     * @return the IPicture instance with the same id, null if none exists
     */
    static IPicture getPicture(long pictureID) {
        var picture = PictureFactory.getPicture(pictureID);
        if (picture != null) {
            return picture;
        }
        return PortraitFactory.getPortrait(pictureID);
    }

    /**
     *
     * @return an unmodifiable list of the persons on the IPicture
     */
    List<Person> getPersons();

    /**
     *
     * @param aPerson the person to be added
     * @return true if the person was added successfully
     */
    boolean addPerson(Person aPerson);

    /**
     *
     * @param aPerson the person to be removed
     * @return true if the person was removed successfully
     */
    boolean removePerson(Person aPerson);

    /**
     *
     * @return the places on the IPicture
     */
    List<Place> getPlaces();

    /**
     *
     * @param aPlace the place to be added
     * @return true if the place was added successfully
     */
    boolean addPlace(Place aPlace);

    /**
     *
     * @param aPlace the place to be removed
     * @return true if the place was removed successfully
     */
    boolean removePlace(Place aPlace);

}
