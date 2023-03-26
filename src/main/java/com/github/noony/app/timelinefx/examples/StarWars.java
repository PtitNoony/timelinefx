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
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.PortraitFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public class StarWars {

    private static final Logger LOG = Logger.getGlobal();
    private static final String[] RESOURCES = {
        "c3po.png", "darth_sidius.png", "mace_windu.png", "quigon.png",
        "cpt_panaka.png", "darth_vader.png", "obi_wan.png", "r2d2.png",
        "darth_maul.png", "gunray.png", "padme.png", "yoda.png"};

    public static TimeLineProject createStartWars() {
        var projectFolderPath = Configuration.getProjectsParentFolder() + File.separator + "Star Wars " + LocalDate.now();
        Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_FOLDER_KEY, projectFolderPath
        );
        TimeLineProject timeLineProject = TimeLineProjectFactory.createProject("Star Wars", configParams);
        //
        File pictureFolder = timeLineProject.getPortraitsAbsoluteFolder();
        for (var picName : RESOURCES) {
            var picExportPath = pictureFolder.getAbsolutePath() + File.separator + picName;
            File file = new File(picExportPath);
            if (!file.exists()) {
                InputStream link = (StarWars.class.getResourceAsStream(picName));
                try {
                    Files.copy(link, file.getAbsoluteFile().toPath());
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        //
        Place galaxy = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);
        Place spaceTravel = PlaceFactory.createPlace("Space travel", PlaceLevel.INTER_SYSTEM_SPACE, galaxy, Color.LIGHTSTEELBLUE);
        Place nabooSystem = PlaceFactory.createPlace("Naboo System", PlaceLevel.SYSTEM, galaxy, Color.LIGHTGREEN);
        Place nabooOrbit = PlaceFactory.createPlace("Naboo Orbit", PlaceLevel.ORBIT, nabooSystem, Color.GREEN);
        Place naboo = PlaceFactory.createPlace("Naboo", PlaceLevel.PLANET, nabooSystem, Color.CHARTREUSE);
        Place tatooine = PlaceFactory.createPlace("Tatooine", PlaceLevel.PLANET, galaxy, Color.GOLD);
        Place coruscant = PlaceFactory.createPlace("Coruscant", PlaceLevel.PLANET, galaxy, Color.SLATEGRAY);
        Place kamino = PlaceFactory.createPlace("Kamino", PlaceLevel.PLANET, galaxy, Color.AQUAMARINE);
        Place geonosis = PlaceFactory.createPlace("Geonosis", PlaceLevel.PLANET, galaxy, Color.CHOCOLATE);
        //
        var portraitFolder = Configuration.getPortraitsFolder() + File.separator;
        var obiWanKenobi = PersonFactory.createPerson(timeLineProject, "Obi Wan Kenobi", Color.AQUAMARINE);
        var obiWanKenobiPortrait = PortraitFactory.createPortrait(obiWanKenobi, portraitFolder + "obi_wan.png");
        obiWanKenobi.setDefaultPortrait(obiWanKenobiPortrait);
        //
        var quiGon = PersonFactory.createPerson(timeLineProject, "Qui_Gon Jinn", Color.AQUA);
        var quiGonPortrait = PortraitFactory.createPortrait(quiGon, portraitFolder + "quigon.png");
        quiGon.setDefaultPortrait(quiGonPortrait);
        //
        var yoda = PersonFactory.createPerson(timeLineProject, "Yoda", Color.CHARTREUSE);
        var yodaPortrait = PortraitFactory.createPortrait(yoda, portraitFolder + "yoda.png");
        yoda.setDefaultPortrait(yodaPortrait);
        //
        var maceWindu = PersonFactory.createPerson(timeLineProject, "Mace Windu", Color.DARKMAGENTA);
        var maceWinduPortrait = PortraitFactory.createPortrait(maceWindu, portraitFolder + "mace_windu.png");
        maceWindu.setDefaultPortrait(maceWinduPortrait);
        //
        var padme = PersonFactory.createPerson(timeLineProject, "Padmé Amidala", Color.FORESTGREEN);
        var padmePortrait = PortraitFactory.createPortrait(padme, portraitFolder + "padme.png");
        padme.setDefaultPortrait(padmePortrait);
        //
        var anakin = PersonFactory.createPerson(timeLineProject, "Anakin S. / Darth Vador", Color.ORANGE);
        var anakinPortrait = PortraitFactory.createPortrait(anakin, portraitFolder + "darth_vader.png");
        anakin.setDefaultPortrait(anakinPortrait);
        //
        var r2d2 = PersonFactory.createPerson(timeLineProject, "R2-D2", Color.WHITESMOKE);
        var r2d2Portrait = PortraitFactory.createPortrait(r2d2, portraitFolder + "r2d2.png");
        r2d2.setDefaultPortrait(r2d2Portrait);
        //
        var c3po = PersonFactory.createPerson(timeLineProject, "C_3PO", Color.GOLD);
        var c3poPortrait = PortraitFactory.createPortrait(c3po, portraitFolder + "c3po.png");
        c3po.setDefaultPortrait(c3poPortrait);
        //
        var panaka = PersonFactory.createPerson(timeLineProject, "Cap. Panaki", Color.AZURE);
        var panakaPortrait = PortraitFactory.createPortrait(panaka, portraitFolder + "cpt_panaka.png");
        panaka.setDefaultPortrait(panakaPortrait);
        //
        var gunray = PersonFactory.createPerson(timeLineProject, "Nute Gunray", Color.DARKSLATEGRAY);
        var gunrayPortrait = PortraitFactory.createPortrait(gunray, portraitFolder + "gunray.png");
        gunray.setDefaultPortrait(gunrayPortrait);
        //
        var darthMaul = PersonFactory.createPerson(timeLineProject, "Darth Maul", Color.RED);
        var darthMaulPortrait = PortraitFactory.createPortrait(darthMaul, portraitFolder + "darth_maul.png");
        darthMaul.setDefaultPortrait(darthMaulPortrait);
        //
        var darthSidious = PersonFactory.createPerson(timeLineProject, "Darth Sidious", Color.DARKRED);
        var darthSidiousPortrait = PortraitFactory.createPortrait(darthSidious, portraitFolder + "darth_sidius.png");
        darthSidious.setDefaultPortrait(darthSidiousPortrait);
        //
        FriezeFactory.createFrieze(timeLineProject, "SW 1-2");
        //
        long startEpisode2 = 125;
        // Padme
        StayPeriodSimpleTime padmeNaboo1 = StayFactory.createStayPeriodSimpleTime(padme, 0, 20, naboo);
        StayPeriodSimpleTime padmeSpace1 = StayFactory.createStayPeriodSimpleTime(padme, 20, 25, spaceTravel);
        StayPeriodSimpleTime padmeTatooine1 = StayFactory.createStayPeriodSimpleTime(padme, 25, 70, tatooine);
        StayPeriodSimpleTime padmeSpace2 = StayFactory.createStayPeriodSimpleTime(padme, 70, 75, spaceTravel);
        StayPeriodSimpleTime padmeCor1 = StayFactory.createStayPeriodSimpleTime(padme, 75, 90, coruscant);
        StayPeriodSimpleTime padmeSpace3 = StayFactory.createStayPeriodSimpleTime(padme, 90, 95, spaceTravel);
        StayPeriodSimpleTime padmeNaboo2 = StayFactory.createStayPeriodSimpleTime(padme, 95, 125, naboo);
        timeLineProject.addStay(padmeNaboo1);
        timeLineProject.addStay(padmeSpace1);
        timeLineProject.addStay(padmeTatooine1);
        timeLineProject.addStay(padmeSpace2);
        timeLineProject.addStay(padmeCor1);
        timeLineProject.addStay(padmeSpace3);
        timeLineProject.addStay(padmeNaboo2);

        //// Episode 2
        StayPeriodSimpleTime padmeSpace4 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2, startEpisode2 + 5, spaceTravel);
        StayPeriodSimpleTime padmeCor2 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 5, startEpisode2 + 30, coruscant);
        StayPeriodSimpleTime padmeSpace5 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        StayPeriodSimpleTime padmeNaboo3 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 35, startEpisode2 + 60, naboo);
        StayPeriodSimpleTime padmeSpace6 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        StayPeriodSimpleTime padmeTatooine2 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        StayPeriodSimpleTime padmeSpace7 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        StayPeriodSimpleTime padmeGeonosis1 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        StayPeriodSimpleTime padmeNaboo4 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 125, startEpisode2 + 130, naboo);
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
        StayPeriodSimpleTime panakaNaboo1 = StayFactory.createStayPeriodSimpleTime(panaka, 0, 20, naboo);
        StayPeriodSimpleTime panakaSpace1 = StayFactory.createStayPeriodSimpleTime(panaka, 20, 25, spaceTravel);
        StayPeriodSimpleTime panakaTatooine1 = StayFactory.createStayPeriodSimpleTime(panaka, 25, 70, tatooine);
        StayPeriodSimpleTime panakaSpace2 = StayFactory.createStayPeriodSimpleTime(panaka, 70, 75, spaceTravel);
        StayPeriodSimpleTime panakaCor1 = StayFactory.createStayPeriodSimpleTime(panaka, 75, 90, coruscant);
        StayPeriodSimpleTime panakaSpace3 = StayFactory.createStayPeriodSimpleTime(panaka, 90, 95, spaceTravel);
        StayPeriodSimpleTime panakaNaboo2 = StayFactory.createStayPeriodSimpleTime(panaka, 95, 125, naboo);
        timeLineProject.addStay(panakaNaboo1);
        timeLineProject.addStay(panakaSpace1);
        timeLineProject.addStay(panakaTatooine1);
        timeLineProject.addStay(panakaSpace2);
        timeLineProject.addStay(panakaCor1);
        timeLineProject.addStay(panakaSpace3);
        timeLineProject.addStay(panakaNaboo2);

        // R2-D2
        StayPeriodSimpleTime r2d2Naboo1 = StayFactory.createStayPeriodSimpleTime(r2d2, 0, 20, naboo);
        StayPeriodSimpleTime r2d2Space1 = StayFactory.createStayPeriodSimpleTime(r2d2, 20, 25, spaceTravel);
        StayPeriodSimpleTime r2d2Tatooine1 = StayFactory.createStayPeriodSimpleTime(r2d2, 25, 70, tatooine);
        StayPeriodSimpleTime r2d2Space2 = StayFactory.createStayPeriodSimpleTime(r2d2, 70, 75, spaceTravel);
        StayPeriodSimpleTime r2d2Cor1 = StayFactory.createStayPeriodSimpleTime(r2d2, 75, 90, coruscant);
        StayPeriodSimpleTime r2d2Space3 = StayFactory.createStayPeriodSimpleTime(r2d2, 90, 95, spaceTravel);
        StayPeriodSimpleTime r2d2Naboo2 = StayFactory.createStayPeriodSimpleTime(r2d2, 95, 125, naboo);
        timeLineProject.addStay(r2d2Naboo1);
        timeLineProject.addStay(r2d2Space1);
        timeLineProject.addStay(r2d2Tatooine1);
        timeLineProject.addStay(r2d2Space2);
        timeLineProject.addStay(r2d2Cor1);
        timeLineProject.addStay(r2d2Space3);
        timeLineProject.addStay(r2d2Naboo2);
        //// Episode 2
        StayPeriodSimpleTime r2d2Space4 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2, startEpisode2 + 5, spaceTravel);
        StayPeriodSimpleTime r2d2Cor2 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 5, startEpisode2 + 30, coruscant);
        StayPeriodSimpleTime r2d2Space5 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        StayPeriodSimpleTime r2d2Naboo3 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 35, startEpisode2 + 60, naboo);
        StayPeriodSimpleTime r2d2Space6 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        StayPeriodSimpleTime r2d2Tatooine2 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        StayPeriodSimpleTime r2d2Space7 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        StayPeriodSimpleTime r2d2Geonosis1 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        StayPeriodSimpleTime r2d2Naboo4 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 125, startEpisode2 + 130, naboo);
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
        StayPeriodSimpleTime c3poTatooine = StayFactory.createStayPeriodSimpleTime(c3po, 0, 125, tatooine);
        timeLineProject.addStay(c3poTatooine);
        //// Episode 2
        StayPeriodSimpleTime c3poTatooine2 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2, startEpisode2 + 85, tatooine);
        StayPeriodSimpleTime c3poSpace1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        StayPeriodSimpleTime c3poGeonosis1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        StayPeriodSimpleTime c3poNaboo1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 125, startEpisode2 + 130, naboo);
        timeLineProject.addStay(c3poTatooine2);
        timeLineProject.addStay(c3poSpace1);
        timeLineProject.addStay(c3poGeonosis1);
        timeLineProject.addStay(c3poNaboo1);

        // Anakin / Dath Vader
        StayPeriodSimpleTime anakinTatooine1 = StayFactory.createStayPeriodSimpleTime(anakin, 0, 70, tatooine);
        StayPeriodSimpleTime anakinSpace1 = StayFactory.createStayPeriodSimpleTime(anakin, 70, 75, spaceTravel);
        StayPeriodSimpleTime anakinCor1 = StayFactory.createStayPeriodSimpleTime(anakin, 75, 90, coruscant);
        StayPeriodSimpleTime anakinSpace2 = StayFactory.createStayPeriodSimpleTime(anakin, 90, 95, spaceTravel);
        StayPeriodSimpleTime anakinNaboo1 = StayFactory.createStayPeriodSimpleTime(anakin, 95, 110, naboo);
        StayPeriodSimpleTime anakinNabooO1 = StayFactory.createStayPeriodSimpleTime(anakin, 110, 120, nabooOrbit);
        StayPeriodSimpleTime anakinNaboo2 = StayFactory.createStayPeriodSimpleTime(anakin, 120, 125, naboo);
        timeLineProject.addStay(anakinTatooine1);
        timeLineProject.addStay(anakinSpace1);
        timeLineProject.addStay(anakinCor1);
        timeLineProject.addStay(anakinSpace2);
        timeLineProject.addStay(anakinNaboo1);
        timeLineProject.addStay(anakinNabooO1);
        timeLineProject.addStay(anakinNaboo2);
        //// Episode 2
        StayPeriodSimpleTime anakinCor2 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2, startEpisode2 + 30, coruscant);
        StayPeriodSimpleTime anakinSpace5 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        StayPeriodSimpleTime anakinNaboo3 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 35, startEpisode2 + 60, naboo);
        StayPeriodSimpleTime anakinSpace6 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        StayPeriodSimpleTime anakinTatooine2 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        StayPeriodSimpleTime anakinSpace7 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        StayPeriodSimpleTime anakinGeonosis1 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        StayPeriodSimpleTime anakinNaboo4 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 125, startEpisode2 + 130, naboo);
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
        StayPeriodSimpleTime obiWanNaboo1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 0, 10, nabooOrbit);
        StayPeriodSimpleTime obiWanNaboo2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 10, 20, naboo);
        StayPeriodSimpleTime obiWanSpace1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 20, 25, spaceTravel);
        StayPeriodSimpleTime obiWanTatooine1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 25, 70, tatooine);
        StayPeriodSimpleTime obiWanSpace2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 70, 75, spaceTravel);
        StayPeriodSimpleTime obiWanCor1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 75, 90, coruscant);
        StayPeriodSimpleTime obiWanSpace3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 90, 95, spaceTravel);
        StayPeriodSimpleTime obiWanNaboo3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 95, 125, naboo);
        timeLineProject.addStay(obiWanNaboo1);
        timeLineProject.addStay(obiWanNaboo2);
        timeLineProject.addStay(obiWanSpace1);
        timeLineProject.addStay(obiWanTatooine1);
        timeLineProject.addStay(obiWanSpace2);
        timeLineProject.addStay(obiWanCor1);
        timeLineProject.addStay(obiWanSpace3);
        timeLineProject.addStay(obiWanNaboo3);
        //// Episode 2
        StayPeriodSimpleTime obiWanCor2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 0, startEpisode2 + 35, coruscant);
        StayPeriodSimpleTime obiWanSpace4 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 35, startEpisode2 + 40, spaceTravel);
        StayPeriodSimpleTime obiWanKamino1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 40, startEpisode2 + 60, kamino);
        StayPeriodSimpleTime obiWanSpace5 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        StayPeriodSimpleTime obiWanGeonosis1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 65, startEpisode2 + 125, geonosis);
        StayPeriodSimpleTime obiWanCor3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 125, startEpisode2 + 130, geonosis);
        timeLineProject.addStay(obiWanCor2);
        timeLineProject.addStay(obiWanSpace4);
        timeLineProject.addStay(obiWanKamino1);
        timeLineProject.addStay(obiWanSpace5);
        timeLineProject.addStay(obiWanGeonosis1);
        timeLineProject.addStay(obiWanCor3);

        StayPeriodSimpleTime quiGonNaboo1 = StayFactory.createStayPeriodSimpleTime(quiGon, 0, 10, nabooOrbit);
        StayPeriodSimpleTime quiGonNaboo2 = StayFactory.createStayPeriodSimpleTime(quiGon, 10, 20, naboo);
        StayPeriodSimpleTime quiGonSpace1 = StayFactory.createStayPeriodSimpleTime(quiGon, 20, 25, spaceTravel);
        StayPeriodSimpleTime quiGonTatooine1 = StayFactory.createStayPeriodSimpleTime(quiGon, 25, 70, tatooine);
        StayPeriodSimpleTime quiGonSpace2 = StayFactory.createStayPeriodSimpleTime(quiGon, 70, 75, spaceTravel);
        StayPeriodSimpleTime quiGonCor1 = StayFactory.createStayPeriodSimpleTime(quiGon, 75, 90, coruscant);
        StayPeriodSimpleTime quiGonSpace3 = StayFactory.createStayPeriodSimpleTime(quiGon, 90, 95, spaceTravel);
        StayPeriodSimpleTime quiGonNaboo3 = StayFactory.createStayPeriodSimpleTime(quiGon, 95, 115, naboo);
        timeLineProject.addStay(quiGonNaboo1);
        timeLineProject.addStay(quiGonNaboo2);
        timeLineProject.addStay(quiGonSpace1);
        timeLineProject.addStay(quiGonTatooine1);
        timeLineProject.addStay(quiGonSpace2);
        timeLineProject.addStay(quiGonCor1);
        timeLineProject.addStay(quiGonSpace3);
        timeLineProject.addStay(quiGonNaboo3);

        StayPeriodSimpleTime yodaCor1 = StayFactory.createStayPeriodSimpleTime(yoda, 0, 120, coruscant);
        StayPeriodSimpleTime yodaNaboo1 = StayFactory.createStayPeriodSimpleTime(yoda, 120, 125, naboo);
        timeLineProject.addStay(yodaCor1);
        timeLineProject.addStay(yodaNaboo1);

        StayPeriodSimpleTime winduCor1 = StayFactory.createStayPeriodSimpleTime(maceWindu, 0, 120, coruscant);
        StayPeriodSimpleTime winduNaboo1 = StayFactory.createStayPeriodSimpleTime(maceWindu, 120, 125, naboo);
        timeLineProject.addStay(winduCor1);
        timeLineProject.addStay(winduNaboo1);

        // Gunray
        StayPeriodSimpleTime gunrayNO1 = StayFactory.createStayPeriodSimpleTime(gunray, 00, 20, nabooOrbit);
        StayPeriodSimpleTime gunrayNaboo1 = StayFactory.createStayPeriodSimpleTime(gunray, 20, 25, naboo);
        StayPeriodSimpleTime gunrayNO2 = StayFactory.createStayPeriodSimpleTime(gunray, 25, 30, nabooOrbit);
        StayPeriodSimpleTime gunrayNaboo2 = StayFactory.createStayPeriodSimpleTime(gunray, 70, 125, naboo);
        timeLineProject.addStay(gunrayNO1);
        timeLineProject.addStay(gunrayNaboo1);
        timeLineProject.addStay(gunrayNO2);
        timeLineProject.addStay(gunrayNaboo2);
        //// Episode 2
        StayPeriodSimpleTime gunrayGeonosis1 = StayFactory.createStayPeriodSimpleTime(gunray, startEpisode2 + 70, startEpisode2 + 115, geonosis);
        StayPeriodSimpleTime gunraySpace1 = StayFactory.createStayPeriodSimpleTime(gunray, startEpisode2 + 115, startEpisode2 + 120, spaceTravel);
        timeLineProject.addStay(gunrayGeonosis1);
        timeLineProject.addStay(gunraySpace1);

        StayPeriodSimpleTime maulCor1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 00, 40, coruscant);
        StayPeriodSimpleTime maulSpace1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 40, 45, spaceTravel);
        StayPeriodSimpleTime maulTatooine1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 45, 75, tatooine);
        StayPeriodSimpleTime maulSpace2 = StayFactory.createStayPeriodSimpleTime(darthMaul, 90, 95, spaceTravel);
        StayPeriodSimpleTime maulNaboo1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 95, 120, naboo);
        timeLineProject.addStay(maulCor1);
        timeLineProject.addStay(maulSpace1);
        timeLineProject.addStay(maulTatooine1);
        timeLineProject.addStay(maulSpace2);
        timeLineProject.addStay(maulNaboo1);

        StayPeriodSimpleTime sidiousCor1 = StayFactory.createStayPeriodSimpleTime(darthSidious, 00, 120, coruscant);
        StayPeriodSimpleTime sidiousNaboo1 = StayFactory.createStayPeriodSimpleTime(darthSidious, 120, 125, naboo);
        timeLineProject.addStay(sidiousCor1);
        timeLineProject.addStay(sidiousNaboo1);
        //// Episode 2
        StayPeriodSimpleTime sidiousCor2 = StayFactory.createStayPeriodSimpleTime(darthSidious, startEpisode2, startEpisode2 + 130, coruscant);
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
