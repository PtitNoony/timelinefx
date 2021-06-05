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
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class StarWars1 {

    public static final Place GALAXY = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);

    public static final Place NABOO = PlaceFactory.createPlace("Naboo", PlaceLevel.PLANET, GALAXY, Color.CHARTREUSE);
    public static final Place NABOO_ORBIT = PlaceFactory.createPlace("Naboo Orbit", PlaceLevel.ORBIT, GALAXY, Color.GREEN);
    public static final Place SPACE_TRAVEL = PlaceFactory.createPlace("Space travel", PlaceLevel.INTER_SYSTEM_SPACE, GALAXY, Color.LIGHTSTEELBLUE);
    public static final Place TATOOINE = PlaceFactory.createPlace("Tatooine", PlaceLevel.PLANET, GALAXY, Color.GOLD);
    public static final Place CORUSCANT = PlaceFactory.createPlace("Coruscant", PlaceLevel.PLANET, GALAXY, Color.SLATEGRAY);
    //
    public static final Person OBI_WAN = PersonFactory.createPerson("Obi Wan Kenobi", Color.AQUAMARINE);
    public static final Person QUI_GON = PersonFactory.createPerson("Qui_Gon Jinn", Color.AQUA);
    public static final Person YODA = PersonFactory.createPerson("Yoda", Color.CHARTREUSE);
    public static final Person MACE_WINDU = PersonFactory.createPerson("Mace Windu", Color.DARKMAGENTA);

    public static final Person PADME = PersonFactory.createPerson("Padmé Amidala", Color.FORESTGREEN);

    public static final Person ANAKIN = PersonFactory.createPerson("Anakin S. / Darth Vador", Color.ORANGE);

    public static final Person R2_D2 = PersonFactory.createPerson("R2-D2", Color.WHITESMOKE);
    public static final Person C_3PO = PersonFactory.createPerson("C_3PO", Color.GOLD);

    public static final Person PANAKA = PersonFactory.createPerson("Cap. Panaki", Color.AZURE);

    public static final Person GUNRAY = PersonFactory.createPerson("Nute Gunray", Color.DARKSLATEGRAY);

    public static final Person VALORUM = PersonFactory.createPerson("Valorum", Color.GREY);

    public static final Person DARTH_MAUL = PersonFactory.createPerson("Darth Maul", Color.RED);
    public static final Person DARTH_SIDIOUS = PersonFactory.createPerson("Darth Sidious", Color.DARKRED);

    private static Frieze creatStartWars() {
        Frieze frieze = new Frieze(null, "");
        Map<Integer, StayPeriodSimpleTime> times = new HashMap<>();
//        LocalDate today =
        // Padme
        StayPeriodSimpleTime padmeNaboo1 = StayFactory.createStayPeriodSimpleTime(PADME, 0, 20, NABOO);
        StayPeriodSimpleTime padmeSpace1 = StayFactory.createStayPeriodSimpleTime(PADME, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeTatooine1 = StayFactory.createStayPeriodSimpleTime(PADME, 25, 70, TATOOINE);
        StayPeriodSimpleTime padmeSpace2 = StayFactory.createStayPeriodSimpleTime(PADME, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeCor1 = StayFactory.createStayPeriodSimpleTime(PADME, 75, 90, CORUSCANT);
        StayPeriodSimpleTime padmeSpace3 = StayFactory.createStayPeriodSimpleTime(PADME, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime padmeNaboo2 = StayFactory.createStayPeriodSimpleTime(PADME, 95, 125, NABOO);

        // Panaka
        StayPeriodSimpleTime panakaNaboo1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 0, 20, NABOO);
        StayPeriodSimpleTime panakaSpace1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaTatooine1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 25, 70, TATOOINE);
        StayPeriodSimpleTime panakaSpace2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaCor1 = StayFactory.createStayPeriodSimpleTime(PANAKA, 75, 90, CORUSCANT);
        StayPeriodSimpleTime panakaSpace3 = StayFactory.createStayPeriodSimpleTime(PANAKA, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime panakaNaboo2 = StayFactory.createStayPeriodSimpleTime(PANAKA, 95, 125, NABOO);

        // R2-D2
        StayPeriodSimpleTime r2d2Naboo1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 0, 20, NABOO);
        StayPeriodSimpleTime r2d2Space1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Tatooine1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 25, 70, TATOOINE);
        StayPeriodSimpleTime r2d2Space2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Cor1 = StayFactory.createStayPeriodSimpleTime(R2_D2, 75, 90, CORUSCANT);
        StayPeriodSimpleTime r2d2Space3 = StayFactory.createStayPeriodSimpleTime(R2_D2, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime r2d2Naboo2 = StayFactory.createStayPeriodSimpleTime(R2_D2, 95, 125, NABOO);

        // C-3P0
        StayPeriodSimpleTime c3poTatooine = StayFactory.createStayPeriodSimpleTime(C_3PO, 0, 125, TATOOINE);

        // C-3P0
        StayPeriodSimpleTime anakinTatooine1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 0, 70, TATOOINE);
        StayPeriodSimpleTime anakinSpace1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinCor1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 75, 90, CORUSCANT);
        StayPeriodSimpleTime anakinSpace2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime anakinNaboo1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 95, 110, NABOO);
        StayPeriodSimpleTime anakinNabooO1 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 110, 120, NABOO_ORBIT);
        StayPeriodSimpleTime anakinNaboo2 = StayFactory.createStayPeriodSimpleTime(ANAKIN, 120, 125, NABOO);

        // Obi Wan
        StayPeriodSimpleTime obiWanNaboo1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 0, 10, NABOO_ORBIT);
        StayPeriodSimpleTime obiWanNaboo2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 10, 20, NABOO);
        StayPeriodSimpleTime obiWanSpace1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanTatooine1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 25, 70, TATOOINE);
        StayPeriodSimpleTime obiWanSpace2 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanCor1 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 75, 90, CORUSCANT);
        StayPeriodSimpleTime obiWanSpace3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime obiWanNaboo3 = StayFactory.createStayPeriodSimpleTime(OBI_WAN, 95, 125, NABOO);

        StayPeriodSimpleTime quiGonNaboo1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 0, 10, NABOO_ORBIT);
        StayPeriodSimpleTime quiGonNaboo2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 10, 20, NABOO);
        StayPeriodSimpleTime quiGonSpace1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 20, 25, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonTatooine1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 25, 70, TATOOINE);
        StayPeriodSimpleTime quiGonSpace2 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 70, 75, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonCor1 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 75, 90, CORUSCANT);
        StayPeriodSimpleTime quiGonSpace3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime quiGonNaboo3 = StayFactory.createStayPeriodSimpleTime(QUI_GON, 95, 115, NABOO);

        StayPeriodSimpleTime yodaCor1 = StayFactory.createStayPeriodSimpleTime(YODA, 0, 120, CORUSCANT);
        StayPeriodSimpleTime yodaNaboo1 = StayFactory.createStayPeriodSimpleTime(YODA, 120, 125, NABOO);

        StayPeriodSimpleTime winduCor1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 0, 120, CORUSCANT);
        StayPeriodSimpleTime winduNaboo1 = StayFactory.createStayPeriodSimpleTime(MACE_WINDU, 120, 125, NABOO);

//        StayPeriodSimpleTime valorumCor1 = StayFactory.createStayPeriodSimpleTime(VALORUM, 00, 125, CORUSCANT);
        // Gunray
        StayPeriodSimpleTime gunrayNO1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 00, 20, NABOO_ORBIT);
        StayPeriodSimpleTime gunrayNaboo1 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 20, 25, NABOO);
        StayPeriodSimpleTime gunrayNO2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 25, 30, NABOO_ORBIT);
        //
        StayPeriodSimpleTime gunrayNaboo2 = StayFactory.createStayPeriodSimpleTime(GUNRAY, 70, 125, NABOO);

        StayPeriodSimpleTime maulCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 00, 40, CORUSCANT);
        StayPeriodSimpleTime maulSpace1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 40, 45, SPACE_TRAVEL);
        StayPeriodSimpleTime maulTatooine1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 45, 75, TATOOINE);
        StayPeriodSimpleTime maulSpace2 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 90, 95, SPACE_TRAVEL);
        StayPeriodSimpleTime maulNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_MAUL, 95, 120, NABOO);

        StayPeriodSimpleTime sidiousCor1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 00, 120, CORUSCANT);
        StayPeriodSimpleTime sidiousNaboo1 = StayFactory.createStayPeriodSimpleTime(DARTH_SIDIOUS, 120, 125, NABOO);

//        frieze.addPlace(GALAXY);
//        frieze.addPlace(NABOO);
//        frieze.addPlace(NABOO_ORBIT);
//        frieze.addPlace(TATOOINE);
//        frieze.addPlace(CORUSCANT);
//        frieze.addPlace(SPACE_TRAVEL);
        return frieze;
    }

}
