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

import com.github.noony.app.timelinefx.core.FriezeFactory;
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
        FriezeFactory.createFrieze(timeLineProject, "SW 1-2");
        //
        long startEpisode2 = 125;
        // Padme
        StayPeriodSimpleTime padmeNaboo1 = StayFactory.createStayPeriodSimpleTime(PADME, 0, 20, NABOO);
        StayPeriodSimpleTime padmeSpace1 = StayFactory.createStayPeriodSimpleTime(PADME, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeTatooine1 = StayFactory.createStayPeriodSimpleTime(PADME, 25, 70, TATOOINE);
        StayPeriodSimpleTime padmeSpace2 = StayFactory.createStayPeriodSimpleTime(PADME, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeCor1 = StayFactory.createStayPeriodSimpleTime(PADME, 75, 90, CORUSCANT);
        StayPeriodSimpleTime padmeSpace3 = StayFactory.createStayPeriodSimpleTime(PADME, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeNaboo2 = StayFactory.createStayPeriodSimpleTime(PADME, 95, 125, NABOO);
        timeLineProject.addStay(padmeNaboo1);
        timeLineProject.addStay(padmeSpace1);
        timeLineProject.addStay(padmeTatooine1);
        timeLineProject.addStay(padmeSpace2);
        timeLineProject.addStay(padmeCor1);
        timeLineProject.addStay(padmeSpace3);
        timeLineProject.addStay(padmeNaboo2);

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
        timeLineProject.addStay(padmeSpace4);
        timeLineProject.addStay(padmeCor2);
        timeLineProject.addStay(padmeSpace5);
        timeLineProject.addStay(padmeNaboo3);
        timeLineProject.addStay(padmeSpace6);
        timeLineProject.addStay(padmeTatooine2);
        timeLineProject.addStay(padmeSpace7);
        timeLineProject.addStay(padmeGeonosis1);
        timeLineProject.addStay(padmeNaboo4);

        // Panaka
        StayPeriodSimpleTime panakaNaboo1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 0, 20, NABOO);
        StayPeriodSimpleTime panakaSpace1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaTatooine1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 25, 70, TATOOINE);
        StayPeriodSimpleTime panakaSpace2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaCor1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 75, 90, CORUSCANT);
        StayPeriodSimpleTime panakaSpace3 = StayFactory.createStayPeriodSimpleTime(PANAKA, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaNaboo2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 95, 125, NABOO);
        timeLineProject.addStay(panakaNaboo1);
        timeLineProject.addStay(panakaSpace1);
        timeLineProject.addStay(panakaTatooine1);
        timeLineProject.addStay(panakaSpace2);
        timeLineProject.addStay(panakaCor1);
        timeLineProject.addStay(panakaSpace3);
        timeLineProject.addStay(panakaNaboo2);

        // R2-D2
        StayPeriodSimpleTime r2d2Naboo1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 0, 20, NABOO);
        StayPeriodSimpleTime r2d2Space1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Tatooine1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 25, 70, TATOOINE);
        StayPeriodSimpleTime r2d2Space2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Cor1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 75, 90, CORUSCANT);
        StayPeriodSimpleTime r2d2Space3 = StayFactory.createStayPeriodSimpleTime(R2_D2, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Naboo2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 95, 125, NABOO);
        timeLineProject.addStay(r2d2Naboo1);
        timeLineProject.addStay(r2d2Space1);
        timeLineProject.addStay(r2d2Tatooine1);
        timeLineProject.addStay(r2d2Space2);
        timeLineProject.addStay(r2d2Cor1);
        timeLineProject.addStay(r2d2Space3);
        timeLineProject.addStay(r2d2Naboo2);
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
        timeLineProject.addStay(r2d2Space4);
        timeLineProject.addStay(r2d2Cor2);
        timeLineProject.addStay(r2d2Space5);
        timeLineProject.addStay(r2d2Naboo3);
        timeLineProject.addStay(r2d2Space6);
        timeLineProject.addStay(r2d2Tatooine2);
        timeLineProject.addStay(r2d2Space7);
        timeLineProject.addStay(r2d2Geonosis1);
        timeLineProject.addStay(r2d2Naboo4);

        // C-3P0
        StayPeriodSimpleTime c3poTatooine = StayFactory.createStayPeriodSimpleTime(C_3PO, 0, 125, TATOOINE);
        timeLineProject.addStay(c3poTatooine);
        //// Episode 2
        StayPeriodSimpleTime c3poTatooine2 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime c3poSpace1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime c3poGeonosis1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime c3poNaboo1 = StayFactory.createStayPeriodSimpleTime(C_3PO, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        timeLineProject.addStay(c3poTatooine2);
        timeLineProject.addStay(c3poSpace1);
        timeLineProject.addStay(c3poGeonosis1);
        timeLineProject.addStay(c3poNaboo1);

        // Anakin / Dath Vader
        StayPeriodSimpleTime anakinTatooine1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 0, 70, TATOOINE);
        StayPeriodSimpleTime anakinSpace1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinCor1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 75, 90, CORUSCANT);
        StayPeriodSimpleTime anakinSpace2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinNaboo1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 95, 110, NABOO);
        StayPeriodSimpleTime anakinNabooO1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 110, 120, NABOO_ORBIT);
        StayPeriodSimpleTime anakinNaboo2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 120, 125, NABOO);
        timeLineProject.addStay(anakinTatooine1);
        timeLineProject.addStay(anakinSpace1);
        timeLineProject.addStay(anakinCor1);
        timeLineProject.addStay(anakinSpace2);
        timeLineProject.addStay(anakinNaboo1);
        timeLineProject.addStay(anakinNabooO1);
        timeLineProject.addStay(anakinNaboo2);
        //// Episode 2
        StayPeriodSimpleTime anakinCor2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2, startEpisode2 + 30, CORUSCANT);
        StayPeriodSimpleTime anakinSpace5 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 30, startEpisode2 + 35, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinNaboo3 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 35, startEpisode2 + 60, NABOO);
        StayPeriodSimpleTime anakinSpace6 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinTatooine2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 65, startEpisode2 + 85, TATOOINE);
        StayPeriodSimpleTime anakinSpace7 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 85, startEpisode2 + 90, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinGeonosis1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 90, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime anakinNaboo4 = StayFactory.createStayPeriodSimpleTime(ANAKIN, startEpisode2 + 125, startEpisode2 + 130, NABOO);
        timeLineProject.addStay(anakinCor2);
        timeLineProject.addStay(anakinSpace5);
        timeLineProject.addStay(anakinNaboo3);
        timeLineProject.addStay(anakinSpace6);
        timeLineProject.addStay(anakinTatooine2);
        timeLineProject.addStay(anakinSpace7);
        timeLineProject.addStay(anakinGeonosis1);
        timeLineProject.addStay(anakinNaboo4);

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
        timeLineProject.addStay(obiWanNaboo1);
        timeLineProject.addStay(obiWanNaboo2);
        timeLineProject.addStay(obiWanSpace1);
        timeLineProject.addStay(obiWanTatooine1);
        timeLineProject.addStay(obiWanSpace2);
        timeLineProject.addStay(obiWanCor1);
        timeLineProject.addStay(obiWanSpace3);
        timeLineProject.addStay(obiWanNaboo3);
        //// Episode 2
        StayPeriodSimpleTime obiWanCor2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 0, startEpisode2 + 35, CORUSCANT);
        StayPeriodSimpleTime obiWanSpace4 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 35, startEpisode2 + 40, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanKamino1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 40, startEpisode2 + 60, KAMINO);
        StayPeriodSimpleTime obiWanSpace5 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 60, startEpisode2 + 65, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanGeonosis1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 65, startEpisode2 + 125, GEONOSIS);
        StayPeriodSimpleTime obiWanCor3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, startEpisode2 + 125, startEpisode2 + 130, GEONOSIS);
        timeLineProject.addStay(obiWanCor2);
        timeLineProject.addStay(obiWanSpace4);
        timeLineProject.addStay(obiWanKamino1);
        timeLineProject.addStay(obiWanSpace5);
        timeLineProject.addStay(obiWanGeonosis1);
        timeLineProject.addStay(obiWanCor3);

        StayPeriodSimpleTime quiGonNaboo1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 0, 10, NABOO_ORBIT);
        StayPeriodSimpleTime quiGonNaboo2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 10, 20, NABOO);
        StayPeriodSimpleTime quiGonSpace1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonTatooine1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 25, 70, TATOOINE);
        StayPeriodSimpleTime quiGonSpace2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonCor1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 75, 90, CORUSCANT);
        StayPeriodSimpleTime quiGonSpace3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonNaboo3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 95, 115, NABOO);
        timeLineProject.addStay(quiGonNaboo1);
        timeLineProject.addStay(quiGonNaboo2);
        timeLineProject.addStay(quiGonSpace1);
        timeLineProject.addStay(quiGonTatooine1);
        timeLineProject.addStay(quiGonSpace2);
        timeLineProject.addStay(quiGonCor1);
        timeLineProject.addStay(quiGonSpace3);
        timeLineProject.addStay(quiGonNaboo3);

        StayPeriodSimpleTime yodaCor1 = StayFactory.createStayPeriodSimpleTime(YODA, 0, 120, CORUSCANT);
        StayPeriodSimpleTime yodaNaboo1 = StayFactory.createStayPeriodSimpleTime(YODA, 120, 125, NABOO);
        timeLineProject.addStay(yodaCor1);
        timeLineProject.addStay(yodaNaboo1);

        StayPeriodSimpleTime winduCor1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 0, 120, CORUSCANT);
        StayPeriodSimpleTime winduNaboo1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 120, 125, NABOO);
        timeLineProject.addStay(winduCor1);
        timeLineProject.addStay(winduNaboo1);

//        StayPeriodSimpleTime valorumCor1 = StayFactory.createStayPeriodSimpleTime(VALORUM, 00, 125, CORUSCANT);
        // Gunray
        StayPeriodSimpleTime gunrayNO1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 00, 20, NABOO_ORBIT);
        StayPeriodSimpleTime gunrayNaboo1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 20, 25, NABOO);
        StayPeriodSimpleTime gunrayNO2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 25, 30, NABOO_ORBIT);
        StayPeriodSimpleTime gunrayNaboo2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 70, 125, NABOO);
        timeLineProject.addStay(gunrayNO1);
        timeLineProject.addStay(gunrayNaboo1);
        timeLineProject.addStay(gunrayNO2);
        timeLineProject.addStay(gunrayNaboo2);
        //// Episode 2
        StayPeriodSimpleTime gunrayGeonosis1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, startEpisode2 + 70, startEpisode2 + 115, GEONOSIS);
        StayPeriodSimpleTime gunraySpace1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, startEpisode2 + 115, startEpisode2 + 120, SPACE_TRAVEL);
        timeLineProject.addStay(gunrayGeonosis1);
        timeLineProject.addStay(gunraySpace1);

        StayPeriodSimpleTime maulCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 00, 40, CORUSCANT);
        StayPeriodSimpleTime maulSpace1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 40, 45, SPACE_TRAVEL);
        StayPeriodSimpleTime maulTatooine1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 45, 75, TATOOINE);
        StayPeriodSimpleTime maulSpace2 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime maulNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 95, 120, NABOO);
        timeLineProject.addStay(maulCor1);
        timeLineProject.addStay(maulSpace1);
        timeLineProject.addStay(maulTatooine1);
        timeLineProject.addStay(maulSpace2);
        timeLineProject.addStay(maulNaboo1);

        StayPeriodSimpleTime sidiousCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 00, 120, CORUSCANT);
        StayPeriodSimpleTime sidiousNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 120, 125, NABOO);
        timeLineProject.addStay(sidiousCor1);
        timeLineProject.addStay(sidiousNaboo1);
        //// Episode 2
        StayPeriodSimpleTime sidiousCor2 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, startEpisode2, startEpisode2 + 130, CORUSCANT);
        timeLineProject.addStay(sidiousCor2);

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
