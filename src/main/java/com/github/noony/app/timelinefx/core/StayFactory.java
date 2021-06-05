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

import static com.github.noony.app.timelinefx.core.FriezeObjectFactory.CREATION_LOGGING_LEVEL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author hamon
 */
public class StayFactory {

    private static final Logger LOG = Logger.getGlobal();
    private static final Map<Long, StayPeriod> STAY_PERIOD_SIMPLE_TIMES = new HashMap<>();
    private static final Map<Long, StayPeriod> STAY_PERIOD_LOCAL_DATES = new HashMap<>();

    private StayFactory() {
        // private utility constructor
    }

    public static final void reset() {
        STAY_PERIOD_SIMPLE_TIMES.clear();
        STAY_PERIOD_LOCAL_DATES.clear();
    }

    public static StayPeriodSimpleTime createStayPeriodSimpleTime(Person person, long startDate, long endDate, Place aPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating StayPeriodSimpleTime with person={0} startDate={1} endDate={2} aPlace={3}", new Object[]{person, startDate, endDate, aPlace});
        var stay = new StayPeriodSimpleTime(FriezeObjectFactory.getNextID(), person, startDate, endDate, aPlace);
        STAY_PERIOD_SIMPLE_TIMES.put(stay.getId(), stay);
        FriezeObjectFactory.addObject(stay);
        return stay;
    }

    public static StayPeriodSimpleTime createStayPeriodSimpleTime(long id, Person person, long startDate, long endDate, Place aPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating StayPeriodSimpleTime with id={0} person={1} startDate={2} endDate={3} aPlace={4}", new Object[]{id, person, startDate, endDate, aPlace});
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create stay for " + person.getName() + " from " + startDate + " to " + endDate + " with existing id=" + id + " (exists : " + id + ")");
//            throw new IllegalArgumentException("Trying to create stay for " + person.getName() + " from " + startDate + " to " + endDate + " with existing id=" + id + " (exists : " + STAY_PERIOD_SIMPLE_TIMES.get(id).getDisplayString() + ")");
        }
        var stay = new StayPeriodSimpleTime(id, person, startDate, endDate, aPlace);
        STAY_PERIOD_SIMPLE_TIMES.put(stay.getId(), stay);
        FriezeObjectFactory.addObject(stay);
        return stay;
    }

    public static StayPeriodLocalDate createStayPeriodLocalDate(Person person, LocalDate startDate, LocalDate endDate, Place aPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createStayPeriodLocalDate with person={0} startDate={1} endDate={2} aPlace={3}", new Object[]{person, startDate, endDate, aPlace});
        var stay = new StayPeriodLocalDate(FriezeObjectFactory.getNextID(), person, startDate, endDate, aPlace);
        STAY_PERIOD_LOCAL_DATES.put(stay.getId(), stay);
        FriezeObjectFactory.addObject(stay);
        return stay;
    }

    public static StayPeriodLocalDate createStayPeriodLocalDate(long id, Person person, LocalDate startDate, LocalDate endDate, Place aPlace) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createStayPeriodLocalDate with id={0} person={1} startDate={2} endDate={3} aPlace={4}", new Object[]{id, person, startDate, endDate, aPlace});
        if (!FriezeObjectFactory.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create stay for " + person.getName() + " with existing id=" + id + " (exists : " + STAY_PERIOD_LOCAL_DATES.get(id).getDisplayString() + ")");
        }
        var stay = new StayPeriodLocalDate(id, person, startDate, endDate, aPlace);
        STAY_PERIOD_LOCAL_DATES.put(stay.getId(), stay);
        FriezeObjectFactory.addObject(stay);
        return stay;
    }

    public static StayPeriod getStay(long id) {
        StayPeriod stay = STAY_PERIOD_SIMPLE_TIMES.get(id);
        if (stay != null) {
            return stay;
        }
        return STAY_PERIOD_LOCAL_DATES.get(id);
    }

}
