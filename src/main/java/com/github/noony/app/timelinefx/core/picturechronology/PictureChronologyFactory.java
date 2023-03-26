/*
 * Copyright (C) 2020 NoOnY
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
package com.github.noony.app.timelinefx.core.picturechronology;

import com.github.noony.app.timelinefx.core.Factory;
import static com.github.noony.app.timelinefx.core.Factory.CREATION_LOGGING_LEVEL;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.util.List;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

/**
 *
 * @author hamon
 */
public class PictureChronologyFactory {

    private static final Logger LOG = Logger.getGlobal();

    private static final Factory<PictureChronology> CHROLOGY_FACTORY = new Factory<>();
    private static final Factory<ChronologyPictureMiniature> PICTURE_FACTORY = new Factory<>();
    private static final Factory<ChronologyLink> LINK_FACTORY = new Factory<>();

    private PictureChronologyFactory() {
        // private utility constructor
    }

    public static final void reset() {
        CHROLOGY_FACTORY.reset();
        PICTURE_FACTORY.reset();
        LINK_FACTORY.reset();
    }

    public static List<PictureChronology> getPicturesChronologies() {
        return CHROLOGY_FACTORY.getObjects();
    }

    public static PictureChronology getPictureChronology(long pictureChronologyID) {
        return CHROLOGY_FACTORY.get(pictureChronologyID);
    }

    public static PictureChronology createPictureChronology(long id, TimeLineProject aProject, String pictureChronologyName) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createPictureChronology with id={0} pictureChronologyName={1}", new Object[]{id, pictureChronologyName});
        if (!CHROLOGY_FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create createPictureChronology " + pictureChronologyName + " with existing id=" + id);
        }
        var pictureChronology = new PictureChronology(id, aProject, pictureChronologyName);
        CHROLOGY_FACTORY.addObject(pictureChronology);
        return pictureChronology;
    }

    public static PictureChronology createPictureChronology(long id, TimeLineProject aProject, String pictureChronologyName, TimeFormat aTimeFormat, List<ChronologyPictureMiniature> miniatures, List<ChronologyLink> links) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createPictureChronology with id={0} pictureChronologyName={1} nbMiniatures={2} nbLinks={3}", new Object[]{id, pictureChronologyName, miniatures.size(), links.size()});
        if (!CHROLOGY_FACTORY.isIdAvailable(id)) {
            throw new IllegalArgumentException("Trying to create createPictureChronology " + pictureChronologyName + " with existing id=" + id);
        }
        var pictureChronology = new PictureChronology(id, aProject, pictureChronologyName, aTimeFormat, miniatures, links);
        CHROLOGY_FACTORY.addObject(pictureChronology);
        return pictureChronology;
    }

    public static PictureChronology createPictureChronology(long id, TimeLineProject aProject, String pictureChronologyName, List<ChronologyPictureMiniature> miniatures, List<ChronologyLink> links) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createPictureChronology with id={0} pictureChronologyName={1} nbMiniatures={2} nbLinks={3}", new Object[]{id, pictureChronologyName, miniatures.size(), links.size()});
        long nbTimeFormat = miniatures.stream().map(m -> m.getDateObject().getTimeFormat()).distinct().count();
        if (nbTimeFormat > 1) {
            throw new IllegalArgumentException("Trying to create createPictureChronology " + pictureChronologyName + " with more than 1 time format :: " + nbTimeFormat);
        }
        TimeFormat timeFormat = miniatures.stream().map(m -> m.getDateObject().getTimeFormat()).findAny().orElse(TimeFormat.TIME_MIN);
        return createPictureChronology(id, aProject, pictureChronologyName, timeFormat, miniatures, links);
    }

    public static PictureChronology createPictureChronology(TimeLineProject aProject) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating createPictureChronology with default parameters");
        var pictureChronology = new PictureChronology(CHROLOGY_FACTORY.getNextID(), aProject, PictureChronology.DEFAULT_NAME);
        CHROLOGY_FACTORY.addObject(pictureChronology);
        return pictureChronology;
    }

    public static List<ChronologyPictureMiniature> getChronologyPictureMiniatures() {
        return PICTURE_FACTORY.getObjects();
    }

    public static ChronologyPictureMiniature getChronologyPictureMiniature(long chronologyPictureMiniatureID) {
        return PICTURE_FACTORY.get(chronologyPictureMiniatureID);
    }

    public static ChronologyPictureMiniature createChronologyPictureMiniature(IPicture aPicture, Point2D aPosition, double aScale) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating ChronologyPictureMiniature with picture={0} position={1} scale={2}", new Object[]{aPicture, aPosition, aScale});
        var chronologyPictureMiniature = new ChronologyPictureMiniature(PICTURE_FACTORY.getNextID(), aPicture, aPosition, aScale);
        PICTURE_FACTORY.addObject(chronologyPictureMiniature);
        return chronologyPictureMiniature;
    }

    public static ChronologyPictureMiniature createChronologyPictureMiniature(long anID, IPicture aPicture, Point2D aPosition, double aScale) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating ChronologyPictureMiniature with picture={0} position={1} scale={2}", new Object[]{aPicture, aPosition, aScale});
        if (!PICTURE_FACTORY.isIdAvailable(anID)) {
            var conflitingObject = PICTURE_FACTORY.get(anID);
            throw new IllegalArgumentException("Trying to create ChronologyPictureMiniature for " + aPicture.getName() + " with existing id=" + anID + " existing Object: " + conflitingObject);
        }
        var chronologyPictureMiniature = new ChronologyPictureMiniature(anID, aPicture, aPosition, aScale);
        PICTURE_FACTORY.addObject(chronologyPictureMiniature);
        return chronologyPictureMiniature;
    }

    public static List<ChronologyLink> getChronologyLinks() {
        return LINK_FACTORY.getObjects();
    }

    public static ChronologyLink getChronologyLink(long chronologyLinkID) {
        return LINK_FACTORY.get(chronologyLinkID);
    }

    public static ChronologyLink createChronologyLink(Person aPerson, ChronologyPictureMiniature aStartMiniature, ChronologyPictureMiniature anEndMiniature, ChronologyLinkType aLinkType, double[] linkParameters) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating ChronologyLink with person={0} start={1} end={2}", new Object[]{aPerson, aStartMiniature, anEndMiniature});
        var chronologyLink = new ChronologyLink(LINK_FACTORY.getNextID(), aPerson, aStartMiniature, anEndMiniature, aLinkType, linkParameters);
        LINK_FACTORY.addObject(chronologyLink);
        return chronologyLink;
    }

    public static ChronologyLink createChronologyLink(long anID, Person aPerson, ChronologyPictureMiniature aStartMiniature, ChronologyPictureMiniature anEndMiniature, ChronologyLinkType aLinkType, double[] linkParameters) {
        LOG.log(CREATION_LOGGING_LEVEL, "Creating ChronologyLink with id={0} person={1} start={2} end={3}", new Object[]{anID, aPerson, aStartMiniature, anEndMiniature});
        if (!LINK_FACTORY.isIdAvailable(anID)) {
            var conflitingObject = LINK_FACTORY.get(anID);
            throw new IllegalArgumentException("Trying to create ChronologyLink for " + aPerson.getName() + " with existing id=" + anID + " existing Object: " + conflitingObject);
        }
        var chronologyLink = new ChronologyLink(anID, aPerson, aStartMiniature, anEndMiniature, aLinkType, linkParameters);
        LINK_FACTORY.addObject(chronologyLink);
        return chronologyLink;
    }
}
