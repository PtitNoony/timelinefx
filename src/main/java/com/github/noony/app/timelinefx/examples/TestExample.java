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
package com.github.noony.app.timelinefx.examples;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.ProjectConfiguration;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class TestExample {

    public static final Place GALAXY = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);

    public static final Place PLACE_A = PlaceFactory.createPlace("PLACE_A", PlaceLevel.INTER_SYSTEM_SPACE, GALAXY, Color.LIGHTSTEELBLUE);

    public static final Place PLACE_B = PlaceFactory.createPlace("PLACE_B", PlaceLevel.SYSTEM, GALAXY, Color.LIGHTGREEN);
    //
    public static final Person PERSON_A = PersonFactory.createPerson("PERSON_A", Color.AQUAMARINE);
    public static final Person PERSON_B = PersonFactory.createPerson("PERSON_B", Color.AQUA);
    public static final Person PERSON_C = PersonFactory.createPerson("PERSON_C", Color.CHARTREUSE);

    public static TimeLineProject createExample() {
        TimeLineProject timeLineProject = ProjectConfiguration.createProject("Test Project");
        Frieze frieze = new Frieze(timeLineProject, "Test 1");
        //
        StayPeriodSimpleTime stay1_A = StayFactory.createStayPeriodSimpleTime(PERSON_A, 0, 20, PLACE_A);
        StayPeriodSimpleTime stay2_A = StayFactory.createStayPeriodSimpleTime(PERSON_A, 21, 40, PLACE_B);
        timeLineProject.addStay(stay1_A);
        timeLineProject.addStay(stay2_A);
        //
        StayPeriodSimpleTime stay1_B = StayFactory.createStayPeriodSimpleTime(PERSON_B, 0, 25, PLACE_A);
        StayPeriodSimpleTime stay2_B = StayFactory.createStayPeriodSimpleTime(PERSON_B, 26, 45, PLACE_B);
//        timeLineProject.addStay(stay1_B);
//        timeLineProject.addStay(stay2_B);
        //
        StayPeriodSimpleTime stay1_C = StayFactory.createStayPeriodSimpleTime(PERSON_C, 0, 15, PLACE_B);
        StayPeriodSimpleTime stay2_C = StayFactory.createStayPeriodSimpleTime(PERSON_C, 16, 30, PLACE_A);
        StayPeriodSimpleTime stay3_C = StayFactory.createStayPeriodSimpleTime(PERSON_C, 31, 40, PLACE_B);
//        timeLineProject.addStay(stay1_C);
//        timeLineProject.addStay(stay2_C);
//        timeLineProject.addStay(stay3_C);

//        timeLineProject.addFrieze(frieze);
        return timeLineProject;
    }

}
