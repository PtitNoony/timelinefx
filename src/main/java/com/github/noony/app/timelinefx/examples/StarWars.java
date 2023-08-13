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
import com.github.noony.app.timelinefx.core.PortraitFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        "darth_maul.png", "gunray.png", "padme.png", "yoda.png", "ahsoka.png"};

    private static Person obiWanKenobi;
    private static Person quiGon;
    private static Person yoda;
    private static Person padme;
    private static Person panaka;
    private static Person r2d2;
    private static Person c3po;
    private static Person anakin;
    private static Person maceWindu;
    private static Person gunray;
    private static Person darthMaul;
    private static Person darthSidious;
    private static Person ahsokaTano;
    //
    private static Place galaxy;
    private static Place spaceTravel;
    private static Place nabooSystem;
    private static Place nabooOrbit;
    private static Place naboo;
    private static Place tatooine;
    private static Place coruscant;
    private static Place kamino;
    private static Place geonosis;
    private static Place mandalore;

    private StarWars() {
        // private utility constructor
    }

    public static TimeLineProject createStartWars() {
        var projectFolderPath = Configuration.getProjectsParentFolder() + File.separator + "Star Wars " + LocalDate.now();
        Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_FOLDER_KEY, projectFolderPath
        );
        var timeLineProject = TimeLineProjectFactory.createProject("Star Wars", configParams);
        //
        var pictureFolder = timeLineProject.getPortraitsAbsoluteFolder();
        for (var picName : RESOURCES) {
            var picExportPath = pictureFolder.getAbsolutePath() + File.separator + picName;
            var file = new File(picExportPath);
            if (!file.exists()) {
                var link = (StarWars.class.getResourceAsStream(picName));
                try {
                    Files.copy(link, file.getAbsoluteFile().toPath());
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        //
        var portraitFolder = Configuration.getPortraitsFolder() + File.separator;
        //
        createPlaces();
        createPeople(timeLineProject, portraitFolder);
        //
        createEpisode01(timeLineProject);
        long startEpisode2 = 125;
        createEpisode02(timeLineProject, startEpisode2);
        //
        return timeLineProject;
    }

    private static void createPlaces() {
        galaxy = PlaceFactory.createPlace("Galaxy", PlaceLevel.GALAXY, null, Color.WHEAT);
        spaceTravel = PlaceFactory.createPlace("Space travel", PlaceLevel.INTER_SYSTEM_SPACE, galaxy, Color.LIGHTSTEELBLUE);
        nabooSystem = PlaceFactory.createPlace("Naboo System", PlaceLevel.SYSTEM, galaxy, Color.LIGHTGREEN);
        nabooOrbit = PlaceFactory.createPlace("Naboo Orbit", PlaceLevel.ORBIT, nabooSystem, Color.GREEN);
        naboo = PlaceFactory.createPlace("Naboo", PlaceLevel.PLANET, nabooSystem, Color.CHARTREUSE);
        tatooine = PlaceFactory.createPlace("Tatooine", PlaceLevel.PLANET, galaxy, Color.GOLD);
        coruscant = PlaceFactory.createPlace("Coruscant", PlaceLevel.PLANET, galaxy, Color.SLATEGRAY);
        kamino = PlaceFactory.createPlace("Kamino", PlaceLevel.PLANET, galaxy, Color.AQUAMARINE);
        geonosis = PlaceFactory.createPlace("Geonosis", PlaceLevel.PLANET, galaxy, Color.CHOCOLATE);
        mandalore = PlaceFactory.createPlace("Mandalore", PlaceLevel.PLANET, galaxy, Color.SILVER);
    }

    private static void createPeople(TimeLineProject timeLineProject, String portraitFolder) {
        //
        obiWanKenobi = PersonFactory.createPerson(timeLineProject, "Obi Wan Kenobi", Color.AQUAMARINE);
        var obiWanKenobiPortrait = PortraitFactory.createPortrait(obiWanKenobi, portraitFolder + "obi_wan.png");
        obiWanKenobi.setDefaultPortrait(obiWanKenobiPortrait);
        //
        quiGon = PersonFactory.createPerson(timeLineProject, "Qui_Gon Jinn", Color.AQUA);
        var quiGonPortrait = PortraitFactory.createPortrait(quiGon, portraitFolder + "quigon.png");
        quiGon.setDefaultPortrait(quiGonPortrait);
        //
        yoda = PersonFactory.createPerson(timeLineProject, "Yoda", Color.CHARTREUSE);
        var yodaPortrait = PortraitFactory.createPortrait(yoda, portraitFolder + "yoda.png");
        yoda.setDefaultPortrait(yodaPortrait);
        //
        maceWindu = PersonFactory.createPerson(timeLineProject, "Mace Windu", Color.DARKMAGENTA);
        var maceWinduPortrait = PortraitFactory.createPortrait(maceWindu, portraitFolder + "mace_windu.png");
        maceWindu.setDefaultPortrait(maceWinduPortrait);
        //
        padme = PersonFactory.createPerson(timeLineProject, "Padmé Amidala", Color.FORESTGREEN);
        var padmePortrait = PortraitFactory.createPortrait(padme, portraitFolder + "padme.png");
        padme.setDefaultPortrait(padmePortrait);
        //
        anakin = PersonFactory.createPerson(timeLineProject, "Anakin S. / Darth Vador", Color.ORANGE);
        var anakinPortrait = PortraitFactory.createPortrait(anakin, portraitFolder + "darth_vader.png");
        anakin.setDefaultPortrait(anakinPortrait);
        //
        r2d2 = PersonFactory.createPerson(timeLineProject, "R2-D2", Color.WHITESMOKE);
        var r2d2Portrait = PortraitFactory.createPortrait(r2d2, portraitFolder + "r2d2.png");
        r2d2.setDefaultPortrait(r2d2Portrait);
        //
        c3po = PersonFactory.createPerson(timeLineProject, "C_3PO", Color.GOLD);
        var c3poPortrait = PortraitFactory.createPortrait(c3po, portraitFolder + "c3po.png");
        c3po.setDefaultPortrait(c3poPortrait);
        //
        panaka = PersonFactory.createPerson(timeLineProject, "Cap. Panaki", Color.AZURE);
        var panakaPortrait = PortraitFactory.createPortrait(panaka, portraitFolder + "cpt_panaka.png");
        panaka.setDefaultPortrait(panakaPortrait);
        //
        gunray = PersonFactory.createPerson(timeLineProject, "Nute Gunray", Color.DARKSLATEGRAY);
        var gunrayPortrait = PortraitFactory.createPortrait(gunray, portraitFolder + "gunray.png");
        gunray.setDefaultPortrait(gunrayPortrait);
        //
        darthMaul = PersonFactory.createPerson(timeLineProject, "Darth Maul", Color.RED);
        var darthMaulPortrait = PortraitFactory.createPortrait(darthMaul, portraitFolder + "darth_maul.png");
        darthMaul.setDefaultPortrait(darthMaulPortrait);
        //
        darthSidious = PersonFactory.createPerson(timeLineProject, "Darth Sidious", Color.DARKRED);
        var darthSidiousPortrait = PortraitFactory.createPortrait(darthSidious, portraitFolder + "darth_sidius.png");
        darthSidious.setDefaultPortrait(darthSidiousPortrait);
        darthMaul.setDefaultPortrait(darthMaulPortrait);
        //
        ahsokaTano = PersonFactory.createPerson(timeLineProject, "Ahsoka Tano", Color.SILVER);
        var ahsokaTanoPortrait = PortraitFactory.createPortrait(ahsokaTano, portraitFolder + "ahsoka.png");
        ahsokaTano.setDefaultPortrait(ahsokaTanoPortrait);
    }

    private static void createEpisode01(TimeLineProject timeLineProject) {
        var episode01 = FriezeFactory.createFrieze(timeLineProject, "Episode I");
        //
        var padmeNaboo1 = StayFactory.createStayPeriodSimpleTime(padme, 0, 20, naboo);
        var padmeSpace1 = StayFactory.createStayPeriodSimpleTime(padme, 20, 25, spaceTravel);
        var padmeTatooine1 = StayFactory.createStayPeriodSimpleTime(padme, 25, 70, tatooine);
        var padmeSpace2 = StayFactory.createStayPeriodSimpleTime(padme, 70, 75, spaceTravel);
        var padmeCor1 = StayFactory.createStayPeriodSimpleTime(padme, 75, 90, coruscant);
        var padmeSpace3 = StayFactory.createStayPeriodSimpleTime(padme, 90, 95, spaceTravel);
        var padmeNaboo2 = StayFactory.createStayPeriodSimpleTime(padme, 95, 125, naboo);
        var padmeStaysE01 = Arrays.asList(padmeNaboo1, padmeSpace1, padmeTatooine1, padmeSpace2, padmeCor1, padmeSpace3, padmeNaboo2);
        timeLineProject.addAllStays(padmeStaysE01);
        episode01.addAllStays(padmeStaysE01);
        // Obi Wan
        var obiWanNaboo1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 0, 10, nabooOrbit);
        var obiWanNaboo2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 10, 20, naboo);
        var obiWanSpace1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 20, 25, spaceTravel);
        var obiWanTatooine1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 25, 70, tatooine);
        var obiWanSpace2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 70, 75, spaceTravel);
        var obiWanCor1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 75, 90, coruscant);
        var obiWanSpace3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 90, 95, spaceTravel);
        var obiWanNaboo3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, 95, 125, naboo);
        var obiWanStaysE01 = Arrays.asList(obiWanNaboo1, obiWanNaboo2, obiWanSpace1, obiWanTatooine1, obiWanSpace2,
                obiWanCor1, obiWanSpace3, obiWanNaboo3);
        timeLineProject.addAllStays(obiWanStaysE01);
        episode01.addAllStays(obiWanStaysE01);
        // Anakin / Dath Vader
        var anakinTatooine1 = StayFactory.createStayPeriodSimpleTime(anakin, 0, 70, tatooine);
        var anakinSpace1 = StayFactory.createStayPeriodSimpleTime(anakin, 70, 75, spaceTravel);
        var anakinCor1 = StayFactory.createStayPeriodSimpleTime(anakin, 75, 90, coruscant);
        var anakinSpace2 = StayFactory.createStayPeriodSimpleTime(anakin, 90, 95, spaceTravel);
        var anakinNaboo1 = StayFactory.createStayPeriodSimpleTime(anakin, 95, 110, naboo);
        var anakinNabooO1 = StayFactory.createStayPeriodSimpleTime(anakin, 110, 120, nabooOrbit);
        var anakinNaboo2 = StayFactory.createStayPeriodSimpleTime(anakin, 120, 125, naboo);
        var anakinStaysE01 = Arrays.asList(anakinTatooine1, anakinSpace1, anakinCor1, anakinSpace2, anakinNaboo1,
                anakinNabooO1, anakinNaboo2);
        timeLineProject.addAllStays(anakinStaysE01);
        episode01.addAllStays(anakinStaysE01);
        // C-3P0
        var c3poTatooine = StayFactory.createStayPeriodSimpleTime(c3po, 0, 125, tatooine);
        timeLineProject.addStay(c3poTatooine);
        episode01.addAllStays(c3poTatooine);
        // Panaka
        var panakaNaboo1 = StayFactory.createStayPeriodSimpleTime(panaka, 0, 20, naboo);
        var panakaSpace1 = StayFactory.createStayPeriodSimpleTime(panaka, 20, 25, spaceTravel);
        var panakaTatooine1 = StayFactory.createStayPeriodSimpleTime(panaka, 25, 70, tatooine);
        var panakaSpace2 = StayFactory.createStayPeriodSimpleTime(panaka, 70, 75, spaceTravel);
        var panakaCor1 = StayFactory.createStayPeriodSimpleTime(panaka, 75, 90, coruscant);
        var panakaSpace3 = StayFactory.createStayPeriodSimpleTime(panaka, 90, 95, spaceTravel);
        var panakaNaboo2 = StayFactory.createStayPeriodSimpleTime(panaka, 95, 125, naboo);
        var panakaStaysE01 = Arrays.asList(panakaNaboo1, panakaSpace1, panakaTatooine1, panakaSpace2,
                panakaCor1, panakaSpace3, panakaNaboo2);
        timeLineProject.addAllStays(panakaStaysE01);
        episode01.addAllStays(panakaStaysE01);
        // R2-D2
        var r2d2Naboo1 = StayFactory.createStayPeriodSimpleTime(r2d2, 0, 20, naboo);
        var r2d2Space1 = StayFactory.createStayPeriodSimpleTime(r2d2, 20, 25, spaceTravel);
        var r2d2Tatooine1 = StayFactory.createStayPeriodSimpleTime(r2d2, 25, 70, tatooine);
        var r2d2Space2 = StayFactory.createStayPeriodSimpleTime(r2d2, 70, 75, spaceTravel);
        var r2d2Cor1 = StayFactory.createStayPeriodSimpleTime(r2d2, 75, 90, coruscant);
        var r2d2Space3 = StayFactory.createStayPeriodSimpleTime(r2d2, 90, 95, spaceTravel);
        var r2d2Naboo2 = StayFactory.createStayPeriodSimpleTime(r2d2, 95, 125, naboo);
        var r2d2StaysE01 = Arrays.asList(r2d2Naboo1, r2d2Space1, r2d2Tatooine1, r2d2Space2, r2d2Cor1, r2d2Space3, r2d2Naboo2);
        timeLineProject.addAllStays(r2d2StaysE01);
        episode01.addAllStays(r2d2StaysE01);
        // Yoda
        var yodaCor1 = StayFactory.createStayPeriodSimpleTime(yoda, 0, 120, coruscant);
        var yodaNaboo1 = StayFactory.createStayPeriodSimpleTime(yoda, 120, 125, naboo);
        timeLineProject.addStay(yodaCor1);
        timeLineProject.addStay(yodaNaboo1);
        episode01.addStay(yodaCor1);
        episode01.addStay(yodaNaboo1);
        // Qui-Gon
        var quiGonNaboo1 = StayFactory.createStayPeriodSimpleTime(quiGon, 0, 10, nabooOrbit);
        var quiGonNaboo2 = StayFactory.createStayPeriodSimpleTime(quiGon, 10, 20, naboo);
        var quiGonSpace1 = StayFactory.createStayPeriodSimpleTime(quiGon, 20, 25, spaceTravel);
        var quiGonTatooine1 = StayFactory.createStayPeriodSimpleTime(quiGon, 25, 70, tatooine);
        var quiGonSpace2 = StayFactory.createStayPeriodSimpleTime(quiGon, 70, 75, spaceTravel);
        var quiGonCor1 = StayFactory.createStayPeriodSimpleTime(quiGon, 75, 90, coruscant);
        var quiGonSpace3 = StayFactory.createStayPeriodSimpleTime(quiGon, 90, 95, spaceTravel);
        var quiGonNaboo3 = StayFactory.createStayPeriodSimpleTime(quiGon, 95, 115, naboo);
        var quiGonStaysE01 = Arrays.asList(quiGonNaboo1, quiGonNaboo2, quiGonSpace1, quiGonTatooine1, quiGonSpace2,
                quiGonCor1, quiGonSpace3, quiGonNaboo3);
        timeLineProject.addAllStays(quiGonStaysE01);
        episode01.addAllStays(quiGonStaysE01);
        // Windu
        var winduCor1 = StayFactory.createStayPeriodSimpleTime(maceWindu, 0, 120, coruscant);
        var winduNaboo1 = StayFactory.createStayPeriodSimpleTime(maceWindu, 120, 125, naboo);
        timeLineProject.addStay(winduCor1);
        timeLineProject.addStay(winduNaboo1);
        episode01.addStay(winduCor1);
        episode01.addStay(winduNaboo1);
        // Maul
        var maulCor1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 00, 40, coruscant);
        var maulSpace1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 40, 45, spaceTravel);
        var maulTatooine1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 45, 75, tatooine);
        var maulSpace2 = StayFactory.createStayPeriodSimpleTime(darthMaul, 90, 95, spaceTravel);
        var maulNaboo1 = StayFactory.createStayPeriodSimpleTime(darthMaul, 95, 120, naboo);
        var maulStaysE01 = Arrays.asList(maulCor1, maulSpace1, maulTatooine1, maulSpace2, maulNaboo1);
        timeLineProject.addAllStays(maulStaysE01);
        episode01.addAllStays(maulStaysE01);
        // Sidious
        var sidiousCor1 = StayFactory.createStayPeriodSimpleTime(darthSidious, 00, 120, coruscant);
        var sidiousNaboo1 = StayFactory.createStayPeriodSimpleTime(darthSidious, 120, 125, naboo);
        timeLineProject.addAllStays(sidiousCor1, sidiousNaboo1);
        episode01.addAllStays(sidiousCor1, sidiousNaboo1);
        // Gunray
        var gunrayNO1 = StayFactory.createStayPeriodSimpleTime(gunray, 00, 20, nabooOrbit);
        var gunrayNaboo1 = StayFactory.createStayPeriodSimpleTime(gunray, 20, 25, naboo);
        var gunrayNO2 = StayFactory.createStayPeriodSimpleTime(gunray, 25, 30, nabooOrbit);
        var gunrayNaboo2 = StayFactory.createStayPeriodSimpleTime(gunray, 70, 125, naboo);
        var gunrayStaysE01 = Arrays.asList(gunrayNO1, gunrayNaboo1, gunrayNO2, gunrayNaboo2);
        timeLineProject.addAllStays(gunrayStaysE01);
        episode01.addAllStays(gunrayStaysE01);
    }

    private static void createEpisode02(TimeLineProject timeLineProject, double startEpisode2) {
        List<StayPeriod> e02Stays = new LinkedList<>();
        // Padme
        var padmeSpace4 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2, startEpisode2 + 5, spaceTravel);
        var padmeCor2 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 5, startEpisode2 + 30, coruscant);
        var padmeSpace5 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        var padmeNaboo3 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 35, startEpisode2 + 60, naboo);
        var padmeSpace6 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        var padmeTatooine2 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        var padmeSpace7 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        var padmeGeonosis1 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        var padmeNaboo4 = StayFactory.createStayPeriodSimpleTime(padme, startEpisode2 + 125, startEpisode2 + 130, naboo);
        var padmeStaysE02 = Arrays.asList(padmeSpace4, padmeCor2, padmeSpace5, padmeNaboo3, padmeSpace6, padmeTatooine2,
                padmeSpace7, padmeGeonosis1, padmeNaboo4);
        timeLineProject.addAllStays(padmeStaysE02);
        e02Stays.addAll(padmeStaysE02);
        // Obi Wan
        var obiWanCor2 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 0, startEpisode2 + 35, coruscant);
        var obiWanSpace4 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 35, startEpisode2 + 40, spaceTravel);
        var obiWanKamino1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 40, startEpisode2 + 60, kamino);
        var obiWanSpace5 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        var obiWanGeonosis1 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 65, startEpisode2 + 125, geonosis);
        var obiWanCor3 = StayFactory.createStayPeriodSimpleTime(obiWanKenobi, startEpisode2 + 125, startEpisode2 + 130, geonosis);
        var obiWanStaysE02 = Arrays.asList(obiWanCor2, obiWanSpace4, obiWanKamino1, obiWanSpace5, obiWanGeonosis1, obiWanCor3);
        timeLineProject.addAllStays(obiWanStaysE02);
        e02Stays.addAll(obiWanStaysE02);
        //// Anakin
        var anakinCor2 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2, startEpisode2 + 30, coruscant);
        var anakinSpace5 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        var anakinNaboo3 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 35, startEpisode2 + 60, naboo);
        var anakinSpace6 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        var anakinTatooine2 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        var anakinSpace7 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        var anakinGeonosis1 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        var anakinNaboo4 = StayFactory.createStayPeriodSimpleTime(anakin, startEpisode2 + 125, startEpisode2 + 130, naboo);
        var anakinStaysE02 = Arrays.asList(anakinCor2, anakinSpace5, anakinNaboo3, anakinSpace6, anakinTatooine2, anakinSpace7,
                anakinGeonosis1, anakinNaboo4);
        timeLineProject.addAllStays(anakinStaysE02);
        e02Stays.addAll(anakinStaysE02);
        // R2D2
        var r2d2Space4 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2, startEpisode2 + 5, spaceTravel);
        var r2d2Cor2 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 5, startEpisode2 + 30, coruscant);
        var r2d2Space5 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 30, startEpisode2 + 35, spaceTravel);
        var r2d2Naboo3 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 35, startEpisode2 + 60, naboo);
        var r2d2Space6 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 60, startEpisode2 + 65, spaceTravel);
        var r2d2Tatooine2 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 65, startEpisode2 + 85, tatooine);
        var r2d2Space7 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        var r2d2Geonosis1 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        var r2d2Naboo4 = StayFactory.createStayPeriodSimpleTime(r2d2, startEpisode2 + 125, startEpisode2 + 130, naboo);
        var r2d2StaysE02 = Arrays.asList(r2d2Space4, r2d2Cor2, r2d2Space5, r2d2Naboo3, r2d2Space6, r2d2Tatooine2,
                r2d2Space7,r2d2Geonosis1, r2d2Naboo4 );
        timeLineProject.addAllStays(r2d2StaysE02);
        e02Stays.addAll(r2d2StaysE02);
        // C3PO
        var c3poTatooine2 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2, startEpisode2 + 85, tatooine);
        var c3poSpace1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 85, startEpisode2 + 90, spaceTravel);
        var c3poGeonosis1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 90, startEpisode2 + 125, geonosis);
        var c3poNaboo1 = StayFactory.createStayPeriodSimpleTime(c3po, startEpisode2 + 125, startEpisode2 + 130, naboo);
        var c3poStaysE02 = Arrays.asList(c3poTatooine2, c3poSpace1, c3poGeonosis1, c3poNaboo1);
        timeLineProject.addAllStays(c3poStaysE02);
        e02Stays.addAll(c3poStaysE02);
        // Gunray
        var gunrayGeonosis1 = StayFactory.createStayPeriodSimpleTime(gunray, startEpisode2 + 70, startEpisode2 + 115, geonosis);
        var gunraySpace1 = StayFactory.createStayPeriodSimpleTime(gunray, startEpisode2 + 115, startEpisode2 + 120, spaceTravel);
        timeLineProject.addAllStays(gunrayGeonosis1, gunraySpace1);
        e02Stays.add(gunrayGeonosis1);
        e02Stays.add(gunraySpace1);
        //
        var sidiousCor2 = StayFactory.createStayPeriodSimpleTime(darthSidious, startEpisode2, startEpisode2 + 130, coruscant);
        timeLineProject.addStay(sidiousCor2);
        e02Stays.add(sidiousCor2);
        //
        var episode02 = FriezeFactory.createFrieze(timeLineProject, "Episode II", e02Stays);
        LOG.log(Level.INFO, "Created frieze: {0}", new Object[]{episode02});
    }

}
