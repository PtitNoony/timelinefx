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

import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Entry point for creating and retrieving {@link StayPeriod} instances.
 *
 * @author hamon
 */
public final class StayFactory {

    /**
     * Logger used by this factory.
     */
    private static final Logger LOG = Logger.getGlobal();


    /**
     * Registry of created stays.
     */
    private static final Factory<StayPeriod> FACTORY = new Factory<>();

    /**
     * Prefix of the exception message thrown when creating a stay with an already-used id.
     */
    private static final String TRYING_TO_CREATE_STAY_MESSAGE = "Trying to create stay for ";

    /**
     * Fragment used to render the conflicting id in exception messages.
     */
    private static final String EXISTING_ID_MESSAGE_FRAGMENT = " with existing id=";

    /**
     * Fragment used to render the exixiting conflicting id in exception messages.
     */
    private static final String EXISTING_ID_MESSAGE_FRAGMENT_PART_2 = " (exists : ";

    /**
     * Closing parenthesis used at the end of exception messages.
     */
    private static final String CLOSING_PARENTHESIS = ")";

    private StayFactory() {
        // private utility constructor
    }

    /**
     * Resets the factory, discarding all created stays.
     */
    public static void reset() {
        FACTORY.reset();
    }

    /**
     * Creates a new stay with numeric start/end times.
     *
     * @param person the person staying
     * @param startDate the stay's start time
     * @param endDate the stay's end time
     * @param aPlace the place stayed at
     * @return the created stay
     */
    public static StayPeriodSimpleTime createStayPeriodSimpleTime(final Person person, final double startDate, final double endDate, final Place aPlace) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating StayPeriodSimpleTime with person={0} startDate={1} endDate={2} aPlace={3}", new Object[]{person, startDate, endDate, aPlace});
        final var stay = new StayPeriodSimpleTime(FACTORY.getNextID(), person, startDate, endDate, aPlace);
        FACTORY.addObject(stay);
        return stay;
    }

    /**
     * Creates a new stay with numeric start/end times and a specific id.
     *
     * @param id the id to assign to the new stay
     * @param person the person staying
     * @param startDate the stay's start time
     * @param endDate the stay's end time
     * @param aPlace the place stayed at
     * @return the created stay
     */
    public static StayPeriodSimpleTime createStayPeriodSimpleTime(final long id, final Person person, final double startDate, final double endDate, final Place aPlace) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating StayPeriodSimpleTime with id={0} person={1} startDate={2} endDate={3} aPlace={4}", new Object[]{id, person, startDate, endDate, aPlace});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException(TRYING_TO_CREATE_STAY_MESSAGE + person.getName() + " from " + startDate + " to " + endDate + EXISTING_ID_MESSAGE_FRAGMENT + id + EXISTING_ID_MESSAGE_FRAGMENT_PART_2 + id + CLOSING_PARENTHESIS);
        }
        final var stay = new StayPeriodSimpleTime(id, person, startDate, endDate, aPlace);
        FACTORY.addObject(stay);
        return stay;
    }

    /**
     * Creates a new stay with calendar start/end dates.
     *
     * @param person the person staying
     * @param startDate the stay's start date
     * @param endDate the stay's end date
     * @param aPlace the place stayed at
     * @return the created stay
     */
    public static StayPeriodLocalDate createStayPeriodLocalDate(final Person person, final LocalDate startDate, final LocalDate endDate, final Place aPlace) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating createStayPeriodLocalDate with person={0} startDate={1} endDate={2} aPlace={3}", new Object[]{person, startDate, endDate, aPlace});
        final var stay = new StayPeriodLocalDate(FACTORY.getNextID(), person, startDate, endDate, aPlace);
        FACTORY.addObject(stay);
        return stay;
    }

    /**
     * Creates a new stay with calendar start/end dates and a specific id.
     *
     * @param id the id to assign to the new stay
     * @param person the person staying
     * @param startDate the stay's start date
     * @param endDate the stay's end date
     * @param aPlace the place stayed at
     * @return the created stay
     */
    public static StayPeriodLocalDate createStayPeriodLocalDate(final long id, final Person person, final LocalDate startDate, final LocalDate endDate, final Place aPlace) {
        LOG.log(Factory.CREATION_LOGGING_LEVEL, "Creating createStayPeriodLocalDate with id={0} person={1} startDate={2} endDate={3} aPlace={4}", new Object[]{id, person, startDate, endDate, aPlace});
        if (!FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException(TRYING_TO_CREATE_STAY_MESSAGE + person.getName() + EXISTING_ID_MESSAGE_FRAGMENT + id + EXISTING_ID_MESSAGE_FRAGMENT_PART_2 + FACTORY.get(id).getDisplayString() + CLOSING_PARENTHESIS);
        }
        final var stay = new StayPeriodLocalDate(id, person, startDate, endDate, aPlace);
        FACTORY.addObject(stay);
        return stay;
    }

    /**
     * Returns the stay with the given id.
     *
     * @param id a stay's id
     * @return the stay with the given id, or null if none exists
     */
    public static StayPeriod getStay(final long id) {
        return FACTORY.get(id);
    }

}
