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

import com.github.noony.app.timelinefx.Configuration;
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class TestExample {

    public static TimeLineProject createExample() {
        var projectFolderPath = Configuration.getProjectsParentFolder() + File.separator + "Test Project " + LocalDate.now();
        Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_FOLDER_KEY, projectFolderPath
        );
        TimeLineProject timeLineProject = TimeLineProjectFactory.createProject("Test Project", configParams);
        //
        Place galaxy = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);
        Place placeA = PlaceFactory.createPlace("PLACE_A", PlaceLevel.INTER_SYSTEM_SPACE, galaxy, Color.LIGHTSTEELBLUE);
        Place placeB = PlaceFactory.createPlace("PLACE_B", PlaceLevel.SYSTEM, galaxy, Color.LIGHTGREEN);
        //
        Person personA = PersonFactory.createPerson(timeLineProject, "PERSON_A", Color.RED);
        Person personB = PersonFactory.createPerson(timeLineProject, "PERSON_B", Color.AQUA);
        Person personC = PersonFactory.createPerson(timeLineProject, "PERSON_C", Color.CHARTREUSE);
        FriezeFactory.createFrieze(timeLineProject, "Test 1");
        //
        StayPeriodSimpleTime stay1_A = StayFactory.createStayPeriodSimpleTime(personA, 0, 20, placeA);
        StayPeriodSimpleTime stay2_A = StayFactory.createStayPeriodSimpleTime(personA, 21, 40, placeB);
        timeLineProject.addStay(stay1_A);
        timeLineProject.addStay(stay2_A);
        //
        StayPeriodSimpleTime stay1_B = StayFactory.createStayPeriodSimpleTime(personB, 0, 25, placeA);
        StayPeriodSimpleTime stay2_B = StayFactory.createStayPeriodSimpleTime(personB, 26, 45, placeB);
        timeLineProject.addStay(stay1_B);
        timeLineProject.addStay(stay2_B);
        //
        StayPeriodSimpleTime stay1_C = StayFactory.createStayPeriodSimpleTime(personC, 0, 15, placeB);
        StayPeriodSimpleTime stay2_C = StayFactory.createStayPeriodSimpleTime(personC, 16, 30, placeA);
        StayPeriodSimpleTime stay3_C = StayFactory.createStayPeriodSimpleTime(personC, 31, 40, placeB);
        timeLineProject.addStay(stay1_C);
        timeLineProject.addStay(stay2_C);
        timeLineProject.addStay(stay3_C);
        //
        return timeLineProject;
    }

}
