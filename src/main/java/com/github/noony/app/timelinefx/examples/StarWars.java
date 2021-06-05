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

import com.github.noony.app.timelinefx.core.ProjectConfiguration;
import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class StarWars {

    public static TimeLineProject createStartWars() {
        TimeLineProject timeLineProject = ProjectConfiguration.createProject("Star Wars");
        //

        Place GALAXY = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);
        Place SPACE_TRAVEL = PlaceFactory.createPlace("Space travel", PlaceLevel.INTER_SYSTEM_SPACE, GALAXY, Color.LIGHTSTEELBLUE);
        Place NABOO_SYSTEM = PlaceFactory.createPlace("Naboo System", PlaceLevel.SYSTEM, GALAXY, Color.LIGHTGREEN);
        Place NABOO_ORBIT = PlaceFactory.createPlace("Naboo Orbit", PlaceLevel.ORBIT, NABOO_SYSTEM, Color.GREEN);
        Place NABOO = PlaceFactory.createPlace("Naboo", PlaceLevel.PLANET, NABOO_SYSTEM, Color.CHARTREUSE);
        Place TATOOINE = PlaceFactory.createPlace("Tatooine", PlaceLevel.PLANET, GALAXY, Color.GOLD);
        Place CORUSCANT = PlaceFactory.createPlace("Coruscant", PlaceLevel.PLANET, GALAXY, Color.SLATEGRAY);
        Place KAMINO = PlaceFactory.createPlace("Kamino", PlaceLevel.PLANET, GALAXY, Color.AQUAMARINE);
        Place GEONOSIS = PlaceFactory.createPlace("Geonosis", PlaceLevel.PLANET, GALAXY, Color.CHOCOLATE);
        //
        Person OBI_WAN = PersonFactory.createPerson("Obi Wan Kenobi", Color.AQUAMARINE);
        Person QUI_GON = PersonFactory.createPerson("Qui_Gon Jinn", Color.AQUA);
        Person YODA = PersonFactory.createPerson("Yoda", Color.CHARTREUSE);
        Person MACE_WINDU = PersonFactory.createPerson("Mace Windu", Color.DARKMAGENTA);
        Person PADME = PersonFactory.createPerson("Padmé Amidala", Color.FORESTGREEN);
        Person ANAKIN = PersonFactory.createPerson("Anakin S. / Darth Vador", Color.ORANGE);
        Person R2_D2 = PersonFactory.createPerson("R2-D2", Color.WHITESMOKE);
        Person C_3PO = PersonFactory.createPerson("C_3PO", Color.GOLD);
        Person PANAKA = PersonFactory.createPerson("Cap. Panaki", Color.AZURE);
        Person GUNRAY = PersonFactory.createPerson("Nute Gunray", Color.DARKSLATEGRAY);
        Person VALORUM = PersonFactory.createPerson("Valorum", Color.GREY);
        Person DARTH_MAUL = PersonFactory.createPerson("Darth Maul", Color.RED);
        Person DARTH_SIDIOUS = PersonFactory.createPerson("Darth Sidious", Color.DARKRED);
        //
        Frieze frieze = new Frieze(timeLineProject, "SW 1-2");
        Map<Integer, StayPeriodSimpleTime> times = new HashMap<>();
        //
        long startEpisode2 = 125;
//        LocalDate today =
        // Padme
        StayPeriodSimpleTime padmeNaboo1 = StayFactory.createStayPeriodSimpleTime(PADME, 0, 20, NABOO);
        StayPeriodSimpleTime padmeSpace1 = StayFactory.createStayPeriodSimpleTime(PADME, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeTatooine1 = StayFactory.createStayPeriodSimpleTime(PADME, 25, 70, TATOOINE);
        StayPeriodSimpleTime padmeSpace2 = StayFactory.createStayPeriodSimpleTime(PADME, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeCor1 = StayFactory.createStayPeriodSimpleTime(PADME, 75, 90, CORUSCANT);
        StayPeriodSimpleTime padmeSpace3 = StayFactory.createStayPeriodSimpleTime(PADME, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeNaboo2 = StayFactory.createStayPeriodSimpleTime(PADME, 95, 125, NABOO);
        frieze.addStayPeriod(padmeNaboo1);
        frieze.addStayPeriod(padmeSpace1);
        frieze.addStayPeriod(padmeTatooine1);
        frieze.addStayPeriod(padmeSpace2);
        frieze.addStayPeriod(padmeCor1);
        frieze.addStayPeriod(padmeSpace3);
        frieze.addStayPeriod(padmeNaboo2);

        //// Episode 2
        StayPeriodSimpleTime padmeSpace4 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2, startEpisode2 + 5, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeCor2 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 5, startEpisode2 + 30, CORUSCANT);
        StayPeriodSimpleTime padmeSpace5 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 30, startEpisode2 + 35, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeNaboo3 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 35, startEpisode2 + 60, NABOO);
        StayPeriodSimpleTime padmeSpace6 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeTatooine2 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 65, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime padmeSpace7 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeGeonosis1 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime padmeNaboo4 = StayFactory.createStayPeriodSimpleTime(PADME, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        frieze.addStayPeriod(padmeSpace4);
        frieze.addStayPeriod(padmeCor2);
        frieze.addStayPeriod(padmeSpace5);
        frieze.addStayPeriod(padmeNaboo3);
        frieze.addStayPeriod(padmeSpace6);
        frieze.addStayPeriod(padmeTatooine2);
        frieze.addStayPeriod(padmeSpace7);
        frieze.addStayPeriod(padmeGeonosis1);
        frieze.addStayPeriod(padmeNaboo4);

        // Panaka
        StayPeriodSimpleTime panakaNaboo1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 0, 20, NABOO);
        StayPeriodSimpleTime panakaSpace1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaTatooine1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 25, 70, TATOOINE);
        StayPeriodSimpleTime panakaSpace2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaCor1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 75, 90, CORUSCANT);
        StayPeriodSimpleTime panakaSpace3 = StayFactory.createStayPeriodSimpleTime(PANAKA, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaNaboo2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 95, 125, NABOO);
        frieze.addStayPeriod(panakaNaboo1);
        frieze.addStayPeriod(panakaSpace1);
        frieze.addStayPeriod(panakaTatooine1);
        frieze.addStayPeriod(panakaSpace2);
        frieze.addStayPeriod(panakaCor1);
        frieze.addStayPeriod(panakaSpace3);
        frieze.addStayPeriod(panakaNaboo2);

        // R2-D2
        StayPeriodSimpleTime r2d2Naboo1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 0, 20, NABOO);
        StayPeriodSimpleTime r2d2Space1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Tatooine1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 25, 70, TATOOINE);
        StayPeriodSimpleTime r2d2Space2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Cor1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 75, 90, CORUSCANT);
        StayPeriodSimpleTime r2d2Space3 = StayFactory.createStayPeriodSimpleTime(R2_D2, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Naboo2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 95, 125, NABOO);
        frieze.addStayPeriod(r2d2Naboo1);
        frieze.addStayPeriod(r2d2Space1);
        frieze.addStayPeriod(r2d2Tatooine1);
        frieze.addStayPeriod(r2d2Space2);
        frieze.addStayPeriod(r2d2Cor1);
        frieze.addStayPeriod(r2d2Space3);
        frieze.addStayPeriod(r2d2Naboo2);
        //// Episode 2
        StayPeriodSimpleTime r2d2Space4 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2, startEpisode2 + 5, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Cor2 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 5, startEpisode2 + 30, CORUSCANT);
        StayPeriodSimpleTime r2d2Space5 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 30, startEpisode2 + 35, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Naboo3 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 35, startEpisode2 + 60, NABOO);
        StayPeriodSimpleTime r2d2Space6 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Tatooine2 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 65, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime r2d2Space7 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Geonosis1 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime r2d2Naboo4 = StayFactory.createStayPeriodSimpleTime(R2_D2, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        frieze.addStayPeriod(r2d2Space4);
        frieze.addStayPeriod(r2d2Cor2);
        frieze.addStayPeriod(r2d2Space5);
        frieze.addStayPeriod(r2d2Naboo3);
        frieze.addStayPeriod(r2d2Space6);
        frieze.addStayPeriod(r2d2Tatooine2);
        frieze.addStayPeriod(r2d2Space7);
        frieze.addStayPeriod(r2d2Geonosis1);
        frieze.addStayPeriod(r2d2Naboo4);

        // C-3P0
        StayPeriodSimpleTime c3poTatooine = StayFactory.createStayPeriodSimpleTime(C_3PO, 0, 125, TATOOINE);
        frieze.addStayPeriod(c3poTatooine);
        //// Episode 2
        StayPeriodSimpleTime c3poTatooine2 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime c3poSpace1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime c3poGeonosis1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime c3poNaboo1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        frieze.addStayPeriod(c3poTatooine2);
        frieze.addStayPeriod(c3poSpace1);
        frieze.addStayPeriod(c3poGeonosis1);
        frieze.addStayPeriod(c3poNaboo1);

        // Anakin / Dath Vader
        StayPeriodSimpleTime anakinTatooine1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 0, 70, TATOOINE);
        StayPeriodSimpleTime anakinSpace1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinCor1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 75, 90, CORUSCANT);
        StayPeriodSimpleTime anakinSpace2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinNaboo1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 95, 110, NABOO);
        StayPeriodSimpleTime anakinNabooO1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 110, 120, NABOO_ORBIT);
        StayPeriodSimpleTime anakinNaboo2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 120, 125, NABOO);
        frieze.addStayPeriod(anakinTatooine1);
        frieze.addStayPeriod(anakinSpace1);
        frieze.addStayPeriod(anakinCor1);
        frieze.addStayPeriod(anakinSpace2);
        frieze.addStayPeriod(anakinNaboo1);
        frieze.addStayPeriod(anakinNabooO1);
        frieze.addStayPeriod(anakinNaboo2);
        //// Episode 2
        StayPeriodSimpleTime anakinCor2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2, startEpisode2 + 30, CORUSCANT);
        StayPeriodSimpleTime anakinSpace5 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 30, startEpisode2 + 35, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinNaboo3 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 35, startEpisode2 + 60, NABOO);
        StayPeriodSimpleTime anakinSpace6 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinTatooine2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 65, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime anakinSpace7 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinGeonosis1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime anakinNaboo4 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        frieze.addStayPeriod(anakinCor2);
        frieze.addStayPeriod(anakinSpace5);
        frieze.addStayPeriod(anakinNaboo3);
        frieze.addStayPeriod(anakinSpace6);
        frieze.addStayPeriod(anakinTatooine2);
        frieze.addStayPeriod(anakinSpace7);
        frieze.addStayPeriod(anakinGeonosis1);
        frieze.addStayPeriod(anakinNaboo4);

        // Obi Wan
        // Episode 1
        StayPeriodSimpleTime obiWanNaboo1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 0, 10, NABOO_ORBIT);
        StayPeriodSimpleTime obiWanNaboo2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 10, 20, NABOO);
        StayPeriodSimpleTime obiWanSpace1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanTatooine1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 25, 70, TATOOINE);
        StayPeriodSimpleTime obiWanSpace2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanCor1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 75, 90, CORUSCANT);
        StayPeriodSimpleTime obiWanSpace3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanNaboo3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 95, 125, NABOO);
        frieze.addStayPeriod(obiWanNaboo1);
        frieze.addStayPeriod(obiWanNaboo2);
        frieze.addStayPeriod(obiWanSpace1);
        frieze.addStayPeriod(obiWanTatooine1);
        frieze.addStayPeriod(obiWanSpace2);
        frieze.addStayPeriod(obiWanCor1);
        frieze.addStayPeriod(obiWanSpace3);
        frieze.addStayPeriod(obiWanNaboo3);
        //// Episode 2
        StayPeriodSimpleTime obiWanCor2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 0, startEpisode2 + 35, CORUSCANT);
        StayPeriodSimpleTime obiWanSpace4 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 35, startEpisode2 + 40, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanKamino1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 40, startEpisode2 + 60, KAMINO);
        StayPeriodSimpleTime obiWanSpace5 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanGeonosis1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 65, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime obiWanCor3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 125, startEpisode2 + 130, GEONOSIS);
        frieze.addStayPeriod(obiWanCor2);
        frieze.addStayPeriod(obiWanSpace4);
        frieze.addStayPeriod(obiWanKamino1);
        frieze.addStayPeriod(obiWanSpace5);
        frieze.addStayPeriod(obiWanGeonosis1);
        frieze.addStayPeriod(obiWanCor3);

        StayPeriodSimpleTime quiGonNaboo1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 0, 10, NABOO_ORBIT);
        StayPeriodSimpleTime quiGonNaboo2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 10, 20, NABOO);
        StayPeriodSimpleTime quiGonSpace1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonTatooine1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 25, 70, TATOOINE);
        StayPeriodSimpleTime quiGonSpace2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonCor1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 75, 90, CORUSCANT);
        StayPeriodSimpleTime quiGonSpace3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonNaboo3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 95, 115, NABOO);
        frieze.addStayPeriod(quiGonNaboo1);
        frieze.addStayPeriod(quiGonNaboo2);
        frieze.addStayPeriod(quiGonSpace1);
        frieze.addStayPeriod(quiGonTatooine1);
        frieze.addStayPeriod(quiGonSpace2);
        frieze.addStayPeriod(quiGonCor1);
        frieze.addStayPeriod(quiGonSpace3);
        frieze.addStayPeriod(quiGonNaboo3);

        StayPeriodSimpleTime yodaCor1 = StayFactory.createStayPeriodSimpleTime(YODA, 0, 120, CORUSCANT);
        StayPeriodSimpleTime yodaNaboo1 = StayFactory.createStayPeriodSimpleTime(YODA, 120, 125, NABOO);
        frieze.addStayPeriod(yodaCor1);
        frieze.addStayPeriod(yodaNaboo1);

        StayPeriodSimpleTime winduCor1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 0, 120, CORUSCANT);
        StayPeriodSimpleTime winduNaboo1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 120, 125, NABOO);
        frieze.addStayPeriod(winduCor1);
        frieze.addStayPeriod(winduNaboo1);

//        StayPeriodSimpleTime valorumCor1 = StayFactory.createStayPeriodSimpleTime(VALORUM, 00, 125, CORUSCANT);
        // Gunray
        StayPeriodSimpleTime gunrayNO1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 00, 20, NABOO_ORBIT);
        StayPeriodSimpleTime gunrayNaboo1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 20, 25, NABOO);
        StayPeriodSimpleTime gunrayNO2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 25, 30, NABOO_ORBIT);
        StayPeriodSimpleTime gunrayNaboo2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 70, 125, NABOO);
        frieze.addStayPeriod(gunrayNO1);
        frieze.addStayPeriod(gunrayNaboo1);
        frieze.addStayPeriod(gunrayNO2);
        frieze.addStayPeriod(gunrayNaboo2);
        //// Episode 2
        StayPeriodSimpleTime gunrayGeonosis1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, startEpisode2 + 70, startEpisode2 + 115, GEONOSIS);
        StayPeriodSimpleTime gunraySpace1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, startEpisode2 + 115, startEpisode2 + 120, SPACE_TRAVEL);
        frieze.addStayPeriod(gunrayGeonosis1);
        frieze.addStayPeriod(gunraySpace1);

        StayPeriodSimpleTime maulCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 00, 40, CORUSCANT);
        StayPeriodSimpleTime maulSpace1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 40, 45, SPACE_TRAVEL);
        StayPeriodSimpleTime maulTatooine1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 45, 75, TATOOINE);
        StayPeriodSimpleTime maulSpace2 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime maulNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 95, 120, NABOO);
        frieze.addStayPeriod(maulCor1);
        frieze.addStayPeriod(maulSpace1);
        frieze.addStayPeriod(maulTatooine1);
        frieze.addStayPeriod(maulSpace2);
        frieze.addStayPeriod(maulNaboo1);

        StayPeriodSimpleTime sidiousCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 00, 120, CORUSCANT);
        StayPeriodSimpleTime sidiousNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 120, 125, NABOO);
        frieze.addStayPeriod(sidiousCor1);
        frieze.addStayPeriod(sidiousNaboo1);
        //// Episode 2
        StayPeriodSimpleTime sidiousCor2 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, startEpisode2, startEpisode2 + 130, CORUSCANT);
        frieze.addStayPeriod(sidiousCor2);

//        frieze.addPlace(GALAXY);
//        frieze.addPlace(NABOO);
//        frieze.addPlace(NABOO_ORBIT);
//        frieze.addPlace(TATOOINE);
//        frieze.addPlace(CORUSCANT);
//        frieze.addPlace(SPACE_TRAVEL);
//        frieze.addPlace(KAMINO);
//        frieze.addPlace(GEONOSIS);
//        timeLineProject.addFrieze(frieze);
        return timeLineProject;
    }

}
