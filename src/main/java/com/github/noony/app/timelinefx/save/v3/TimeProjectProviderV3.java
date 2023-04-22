/*
 * Copyright (C) 2021 NoOnY
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
package com.github.noony.app.timelinefx.save.v3;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.IDateObject;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.PortraitFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.StayPeriodLocalDate;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.FreeMapLink;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapFactory;
import com.github.noony.app.timelinefx.core.freemap.Plot;
import com.github.noony.app.timelinefx.core.freemap.PlotType;
import com.github.noony.app.timelinefx.core.freemap.StayLink;
import com.github.noony.app.timelinefx.core.freemap.TravelLink;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLink;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyLinkType;
import com.github.noony.app.timelinefx.core.picturechronology.ChronologyPictureMiniature;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronology;
import com.github.noony.app.timelinefx.core.picturechronology.PictureChronologyFactory;
import com.github.noony.app.timelinefx.save.TimelineProjectProvider;
import com.github.noony.app.timelinefx.utils.CustomFileUtils;
import com.github.noony.app.timelinefx.utils.CustomProfiler;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author hamon
 */
@ServiceProvider(service = TimelineProjectProvider.class)
public class TimeProjectProviderV3 implements TimelineProjectProvider {

    public static final String PROJECT_GROUP = "PROJECT";
    public static final String PLACES_GROUP = "PLACES";
    public static final String PERSONS_GROUP = "PERSONS";
    public static final String PICTURES_GROUP = "PICTURES";
    public static final String FRIEZES_GROUP = "FRIEZES";
    public static final String STAYS_GROUP = "STAYS";
    public static final String STAYS_REF_GROUP = "stays";
    public static final String FREEMAPS_GROUP = "freeMaps";
    public static final String PORTRAITS_GROUP = "portraits";
    public static final String PLOTS_GROUP = "plots";
    public static final String LINKS_GROUP = "links";
    public static final String FREEMAP_PLACES_GROUP = "freeMapPlaces";
    public static final String PICTURE_CHRONOLOGIES_GROUP = "PICTURE_CHRONOLOGIES";
    //
    public static final String PLACE_ELEMENT = "place";
    public static final String PLACE_REF_ELEMENT = "placeRef";
    public static final String PERSON_ELEMENT = "person";
    public static final String PERSON_REF_ELEMENT = "personRef";
    public static final String PICTURE_ELEMENT = "picture";
    public static final String PICTURE_REF_ELEMENT = "pictureRef";
    public static final String FRIEZE_ELEMENT = "frieze";
    public static final String STAY_ELEMENT = "stay";
    public static final String STAY_ELEMENT_REF = "stayRef";
    public static final String FREEMAP_ELEMENT = "freeMap";
    public static final String PORTRAIT_ELEMENT = "portrait";
    public static final String PLOT_ELEMENT = "plot";
    public static final String LINK_ELEMENT = "link";
    public static final String FREEMAP_PLACE_ELEMENT = "freeMapPlace";
    public static final String PICTURE_CHRONOLOGY_ELEMENT = "pictureChronology";
    public static final String PICTURE_CHRONOLOGY_MINIATURE_ELEMENT = "pictureChronologyMiniature";
    public static final String PICTURE_CHRONOLOGY_LINK_ELEMENT = "pictureChronologyLink";
    //
    public static final String PICTURES_LOCATION_ATR = "picsLoc";
    //
    public static final String PORTRAIT_FOLDER_ATR = "portraitsFolder";
    public static final String PICTURES_FOLDER_ATR = "picturesFolder";
    public static final String MINIATURES_FOLDER_ATR = "miniaturesFolder";
    //
    public static final String ID_ATR = "id";
    public static final String NAME_ATR = "name";
    public static final String TYPE_ATR = "type";
    public static final String PATH_ATR = "path";
    public static final String DATE_ATR = "date";
    public static final String PLACE_LEVEL_ATR = "level";
    public static final String COLOR_ATR = "color";
    public static final String PERSON_ATR = "person";
    public static final String DEFAULT_PORTRAIT_REF_ATR = "defaultPortraitRef";
    public static final String DATE_OF_BIRTH_ATR = "dateOfBirth";
    public static final String DATE_OF_DEATH_ATR = "dateOfDeath";
    public static final String START_DATE_ATR = "startDate";
    public static final String END_DATE_ATR = "endDate";
    public static final String TIME_FORMAT_ATR = "timeFormat";
    public static final String STAY_ID_ATR = "stayID";
    public static final String START_ID_ATR = "startID";
    public static final String END_ID_ATR = "endID";
    public static final String PLACE_ID_ATR = "placeID";
    public static final String FROM_ATR = "from";
    public static final String TO_ATR = "to";
    public static final String PERSON_REF_ATR = "personRef";
    public static final String PARAMETERS_ATR = "params";
    //
    public static final String WIDTH_ATR = "width";
    public static final String HEIGHT_ATR = "height";
    public static final String X_POS_ATR = "xPos";
    public static final String Y_POS_ATR = "yPos";
    public static final String RADIUS_ATR = "radius";
    public static final String SCALE_ATR = "scale";
    public static final String FREEMAP_PERSON_WIDTH_ATR = "personWidth";
    public static final String FREEMAP_PLACE_NAME_WIDTH_ATR = "placeNameWidth";
    public static final String FREEMAP_FONT_SIZE_ATR = "fontSize";
    public static final String FREEMAP_PLOT_SEPARATION_ATR = "plotSeparation";
    public static final String FREEMAP_PLOT_VISIBILITY_ATR = "plotVisibility";
    public static final String FREEMAP_PLOT_SIZE_ATR = "plotSize";

    private static final Logger LOG = Logger.getGlobal();

    private static final String TARGET_VERSION = "3";

    @Override
    public List<String> getSupportedVersions() {
        return Arrays.asList(TARGET_VERSION);
    }

    @Override
    public TimeLineProject load(File projectFile, Element e) {
        var loadMethodName = this.getClass().getSimpleName() + "__load";
        CustomProfiler.start(loadMethodName);
        String projectName = e.getAttribute(NAME_ATR);
        // Load project properties
        var portraitsFolderValue = e.hasAttribute(PORTRAIT_FOLDER_ATR) ? e.getAttribute(PORTRAIT_FOLDER_ATR) : TimeLineProject.DEFAULT_PORTRAIT_FOLDER;
        var picturesFolderValue = e.hasAttribute(PICTURES_FOLDER_ATR) ? e.getAttribute(PICTURES_FOLDER_ATR) : TimeLineProject.DEFAULT_PICTURES_FOLDER;
        var miniaturesFolderValue = e.hasAttribute(MINIATURES_FOLDER_ATR) ? e.getAttribute(MINIATURES_FOLDER_ATR) : TimeLineProject.DEFAULT_MINIATURES_FOLDER;
        //
        Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_FOLDER_KEY, projectFile.getParent(),
                TimeLineProject.PORTRAIT_FOLDER_KEY, portraitsFolderValue,
                TimeLineProject.PICTURES_FOLDER_KEY, picturesFolderValue,
                TimeLineProject.MINIATURES_FOLDER_KEY, miniaturesFolderValue
        );
        TimeLineProject project = TimeLineProjectFactory.createProject(projectName, configParams);
        //
        List<String> relativePathLoaded = new LinkedList<>();
        //
        NodeList rootChildren = e.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node node = rootChildren.item(i);
            if (node instanceof Element element) {
                switch (element.getTagName()) {
                    case PLACES_GROUP -> {
                        List<Place> places = parsePlaces(element, null);
                        places.stream().filter(p -> p.getParent() == null).forEach(p -> project.addHighLevelPlace(p));
                    }
                    case PERSONS_GROUP -> {
                        List<Person> persons = parsePersons(element, project, relativePathLoaded);
                        persons.forEach(p -> project.addPerson(p));
                    }
                    case PICTURES_GROUP -> {
                        parsePictures(element, project, relativePathLoaded);
                    }
                    case STAYS_GROUP -> {
                        List<StayPeriod> stays = parseStays(element);
                        stays.forEach(s -> project.addStay(s));
                    }
                    case FRIEZES_GROUP -> {
                        parseFriezes(project, element);
                    }
                    case PICTURE_CHRONOLOGIES_GROUP -> {
                        parsePictureChronologies(project, element);
                    }
                    default ->
                        throw new UnsupportedOperationException("Unknown element :: " + element.getTagName());
                }
            }
        }
        // check every file exists
        relativePathLoaded.forEach(path -> {
            var absolutePath = CustomFileUtils.fromProjectRelativeToAbsolute(project, path);
            // not optimal ...
            File file = new File(absolutePath);
            if (!file.exists()) {
                LOG.log(Level.SEVERE, "The file {0} does not exists. (saved as '{1}')", new Object[]{absolutePath, path});
            }
        });
        //
        List<Path> absolutePathsLoaded = relativePathLoaded
                .stream()
                .map(p -> Paths.get(CustomFileUtils.fromProjectRelativeToAbsolute(project, p)))
                .map(p -> p.normalize())
                .collect(Collectors.toList());
        // * Portraits
        File portraitFolder = project.getPortraitsAbsoluteFolder();
        FileUtils.listFiles(portraitFolder, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY)
                .stream()
                .map(portraitFile -> Paths.get(portraitFile.toURI()))
                .filter(portraitAbsolutePath -> !absolutePathsLoaded.stream().anyMatch(loaded -> portraitAbsolutePath.compareTo(loaded) == 0))
                .forEach(portraitAbsolutePath -> {
                    // FUTURE IMPROVMENT : create actions;
                    LOG.log(Level.WARNING, "Found unused portrait file: {0}", new Object[]{portraitAbsolutePath});
                });
        // * Pictures
        File picturesFolder = project.getPicturesFolder();
        FileUtils.listFiles(picturesFolder, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY)
                .stream()
                .map(pictureFile -> Paths.get(pictureFile.toURI()))
                .filter(pictureAbsolutePath -> !absolutePathsLoaded.stream().anyMatch(loaded -> pictureAbsolutePath.compareTo(loaded) == 0))
                .forEach(pictureAbsolutePath -> {
                    // FUTURE IMPROVMENT : create actions;
                    LOG.log(Level.WARNING, "Found unused picture file: {0}", new Object[]{pictureAbsolutePath});
                });
        //
        // FUTURE IMPROVMENT : ENABLE AUTO IMPORT => in config
        //
        CustomProfiler.stop(loadMethodName);
        return project;
    }

    @Override
    public boolean save(TimeLineProject project, File destFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(PROJECT_GROUP);
            rootElement.setAttribute(NAME_ATR, project.getName());
            rootElement.setAttribute(PROJECT_VERSION_ATR, TARGET_VERSION);
            //TODO can use other method
            var portraitsFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getPortraitsAbsoluteFolder());
            var picturesFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getPicturesFolder());
            var miniaturesFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getMiniaturesFolder());
            rootElement.setAttribute(PORTRAIT_FOLDER_ATR, portraitsFolderName);
            rootElement.setAttribute(PICTURES_LOCATION_ATR, picturesFolderName);
            rootElement.setAttribute(MINIATURES_FOLDER_ATR, miniaturesFolderName);
            doc.appendChild(rootElement);
            // save places
            Element placesGroupElement = doc.createElement(PLACES_GROUP);
            rootElement.appendChild(placesGroupElement);
            project.getHighLevelPlaces().forEach(place -> placesGroupElement.appendChild(createPlaceElement(doc, place, "root")));
            // save persons
            Element personsGroupElement = doc.createElement(PERSONS_GROUP);
            rootElement.appendChild(personsGroupElement);
            project.getPersons().forEach(person -> personsGroupElement.appendChild(createPersonElement(doc, person)));
            // save pictures
            Element picturesGroupElement = doc.createElement(PICTURES_GROUP);
            rootElement.appendChild(picturesGroupElement);
            PictureFactory.getPictures().forEach(picture -> picturesGroupElement.appendChild(createPictureElement(doc, picture)));
            // save stays
            Element staysGroupElement = doc.createElement(STAYS_GROUP);
            rootElement.appendChild(staysGroupElement);
            project.getStays().forEach(stay -> staysGroupElement.appendChild(createStayElement(doc, stay)));
            // save friezes
            Element friezesGroupElement = doc.createElement(FRIEZES_GROUP);
            rootElement.appendChild(friezesGroupElement);
            project.getFriezes().forEach(frieze -> friezesGroupElement.appendChild(createFriezeElement(doc, frieze)));
            // save picture chronologies
            Element pictureChronologiesGroupElement = doc.createElement(PICTURE_CHRONOLOGIES_GROUP);
            rootElement.appendChild(pictureChronologiesGroupElement);
            project.getPictureChronologies().forEach(picChronology -> pictureChronologiesGroupElement.appendChild(createPictureChronologyElement(doc, picChronology)));
            //
            rootElement.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(destFile);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting timeline :: {0}", ex);
            return false;
        }
        return true;
    }

    private static List<Place> parsePlaces(Element placesRootElement, Place parentPlace) {
        List<Place> places = new LinkedList<>();
        NodeList placeElements = placesRootElement.getChildNodes();
        for (int i = 0; i < placeElements.getLength(); i++) {
            if (placeElements.item(i).getNodeName().equals(PLACE_ELEMENT)) {
                Element e = (Element) placeElements.item(i);
                Place p = parsePlace(e, parentPlace);
                places.add(p);
            }
        }
        return places;
    }

    private static Place parsePlace(Element placeElement, Place parentPlace) {
        // <place color="0xf5deb3ff" id="1" level="GALAXY" name="Galaxy">
        Color color = Color.valueOf(placeElement.getAttribute(COLOR_ATR));
        long id = Long.parseLong(placeElement.getAttribute(ID_ATR));
        PlaceLevel level = PlaceLevel.valueOf(placeElement.getAttribute(PLACE_LEVEL_ATR));
        String name = placeElement.getAttribute(NAME_ATR);
        Place place = PlaceFactory.createPlace(id, name, level, parentPlace, color);
        parsePlaces(placeElement, place);
        return place;
    }

    private static List<Person> parsePersons(Element personsRootElement, TimeLineProject project, List<String> relativePathLoaded) {
        List<Person> persons = new LinkedList<>();
        NodeList personElements = personsRootElement.getChildNodes();
        for (int i = 0; i < personElements.getLength(); i++) {
            if (personElements.item(i).getNodeName().equals(PERSON_ELEMENT)) {
                Element e = (Element) personElements.item(i);
                Person p = parsePerson(e, project, relativePathLoaded);
                persons.add(p);
            }
        }
        return persons;
    }

    private static Person parsePerson(Element personElement, TimeLineProject project, List<String> relativePathLoaded) {
        // <person color="0x7fffd4ff" id="1" name="Obi Wan Kenobi"/>
        var color = Color.valueOf(personElement.getAttribute(COLOR_ATR));
        var id = Long.parseLong(personElement.getAttribute(ID_ATR));
        var name = personElement.getAttribute(NAME_ATR);
        var defaultPortraitRef = Long.MIN_VALUE;
        if (personElement.hasAttribute(DEFAULT_PORTRAIT_REF_ATR)) {
            defaultPortraitRef = Long.parseLong(personElement.getAttribute(DEFAULT_PORTRAIT_REF_ATR));
        }
        var person = PersonFactory.createPerson(project, id, name, color);
        var childrenElements = personElement.getChildNodes();
        for (int i = 0; i < childrenElements.getLength(); i++) {
            if (childrenElements.item(i).getNodeName().equals(PORTRAIT_ELEMENT)) {
                // <portrait id="147" path="portraits\obi_wan.png"/>
                var portraitElement = (Element) childrenElements.item(i);
                var portraitID = Long.parseLong(portraitElement.getAttribute(ID_ATR));
                var portraitPath = portraitElement.getAttribute(PATH_ATR);
                var portrait = PortraitFactory.createPortrait(portraitID, person, portraitPath);
                if (portrait.getId() == defaultPortraitRef) {
                    person.setDefaultPortrait(portrait);
                } else {
                    person.addPortrait(portrait);
                }
                parseObjectTimeValue(portraitElement, portrait);
                relativePathLoaded.add(portraitPath);
            }
        }
        //
        if (personElement.hasAttribute(TIME_FORMAT_ATR)) {
            var timeFormat = TimeFormat.valueOf(personElement.getAttribute(TIME_FORMAT_ATR));
            person.setTimeFormat(timeFormat);
            switch (timeFormat) {
                case LOCAL_TIME -> {
                    if (personElement.hasAttribute(DATE_OF_BIRTH_ATR)) {
                        var dateOfBirthS = personElement.getAttribute(DATE_OF_BIRTH_ATR);
                        var dateOfBirth = LocalDate.parse(dateOfBirthS);
                        person.setDateOfBirth(dateOfBirth);
                    }
                    if (personElement.hasAttribute(DATE_OF_DEATH_ATR)) {
                        var dateOfDeathS = personElement.getAttribute(DATE_OF_DEATH_ATR);
                        var dateOfDeath = LocalDate.parse(dateOfDeathS);
                        person.setDateOfDeath(dateOfDeath);
                    }
                }
                case TIME_MIN -> {
                    if (personElement.hasAttribute(DATE_OF_BIRTH_ATR)) {
                        var timeOfBirthS = personElement.getAttribute(DATE_OF_BIRTH_ATR);
                        var timeOfBirth = Long.parseLong(timeOfBirthS);
                        person.setTimeOfBirth(timeOfBirth);
                    }
                    if (personElement.hasAttribute(DATE_OF_DEATH_ATR)) {
                        var timeOfDeathS = personElement.getAttribute(DATE_OF_DEATH_ATR);
                        var timeOfDeath = Long.parseLong(timeOfDeathS);
                        person.setTimeOfDeath(timeOfDeath);
                    }
                }
                default ->
                    throw new UnsupportedOperationException("Unsupported timefomat : " + timeFormat);
            }
        }
        return person;
    }

    private static void parseObjectTimeValue(Element sourceElement, IDateObject aDateObject) {
        if (sourceElement.hasAttribute(TIME_FORMAT_ATR)) {
            var timeFormat = TimeFormat.valueOf(sourceElement.getAttribute(TIME_FORMAT_ATR));
            aDateObject.setTimeFormat(timeFormat);
            switch (timeFormat) {
                case LOCAL_TIME -> {
                    if (sourceElement.hasAttribute(DATE_ATR)) {
                        var dateS = sourceElement.getAttribute(DATE_ATR);
                        var date = LocalDate.parse(dateS);
                        aDateObject.setDate(date);
                    }
                }
                case TIME_MIN -> {
                    if (sourceElement.hasAttribute(DATE_ATR)) {
                        var timeS = sourceElement.getAttribute(DATE_ATR);
                        var time = Double.parseDouble(timeS);
                        aDateObject.setTimestamp(time);
                    }
                }
                default ->
                    throw new UnsupportedOperationException("Unsupported timefomat : " + timeFormat);
            }
        } else {
            throw new IllegalStateException("No time format specified for: " + aDateObject);
        }
    }

    private static List<Picture> parsePictures(Element picturesRootElement, TimeLineProject project, List<String> relativePathLoaded) {
        List<Picture> pictures = new LinkedList<>();
        NodeList picturesElements = picturesRootElement.getChildNodes();
        for (int i = 0; i < picturesElements.getLength(); i++) {
            if (picturesElements.item(i).getNodeName().equals(PICTURE_ELEMENT)) {
                Element e = (Element) picturesElements.item(i);
                Picture p = parsePicture(e, project, relativePathLoaded);
                pictures.add(p);
            }
        }
        return pictures;
    }

    private static Picture parsePicture(Element pictureElement, TimeLineProject project, List<String> relativePathLoaded) {
        Long milliIn = System.currentTimeMillis();
        var id = Long.parseLong(pictureElement.getAttribute(ID_ATR));
        var name = pictureElement.getAttribute(NAME_ATR);
        var path = pictureElement.getAttribute(PATH_ATR);
        relativePathLoaded.add(path);
        var width = Integer.parseInt(pictureElement.getAttribute(WIDTH_ATR));
        var height = Integer.parseInt(pictureElement.getAttribute(HEIGHT_ATR));
        //
        Picture picture = PictureFactory.createPicture(project, id, name, LocalDateTime.MIN, path, width, height);
        parseObjectTimeValue(pictureElement, picture);
        //
        var pictureChildrenElements = pictureElement.getChildNodes();
        for (int i = 0; i < pictureChildrenElements.getLength(); i++) {
            Node n = pictureChildrenElements.item(i);
            switch (n.getNodeName()) {
                case PERSON_REF_ELEMENT -> {
                    Element e = (Element) n;
                    long personID = Long.parseLong(e.getAttribute(ID_ATR));
                    Person person = PersonFactory.getPerson(personID);
                    picture.addPerson(person);
                }
                case PLACE_REF_ELEMENT -> {
                    Element e = (Element) n;
                    long placeID = Long.parseLong(e.getAttribute(ID_ATR));
                    Place place = PlaceFactory.getPlace(placeID);
                    picture.addPlace(place);
                }
                case "#text" ->
                    LOG.log(Level.FINE, "Ignoring text element");
                default ->
                    throw new UnsupportedOperationException("Could not parse child element of picture " + name + " :: " + n);
            }
        }
        //
        var milliOut = System.currentTimeMillis();
        var time = milliOut - milliIn;
        if (time > 1) {
            LOG.log(Level.SEVERE, "Parsed picture: {0}\n > took {1}ms.", new Object[]{name, Long.toString(time)});
        }
        return picture;
    }

    private List<Frieze> parseFriezes(TimeLineProject project, Element friezesRootElement) {
        List<Frieze> friezes = new LinkedList<>();
        NodeList friezeElements = friezesRootElement.getChildNodes();
        for (int i = 0; i < friezeElements.getLength(); i++) {
            if (friezeElements.item(i).getNodeName().equals(FRIEZE_ELEMENT)) {
                Element e = (Element) friezeElements.item(i);
                Frieze f = parseFrieze(project, e);
                friezes.add(f);
            }
        }
        return friezes;
    }

    private Frieze parseFrieze(TimeLineProject project, Element friezeElement) {
        // <frieze name="SW 1-2">
        var name = friezeElement.getAttribute(NAME_ATR);
        var id = Long.parseLong(friezeElement.getAttribute(ID_ATR));
        NodeList stayGroups = friezeElement.getElementsByTagName(STAYS_REF_GROUP);
        List<StayPeriod> stays = parseStaysInFreize((Element) stayGroups.item(0));
        var frieze = FriezeFactory.createFrieze(id, project, name, stays);
        if (stayGroups.getLength() != 1) {
            throw new IllegalStateException("Wrong number of STAYS_GROUP : " + stayGroups.getLength());
        }
        //
        NodeList freemapGroups = friezeElement.getElementsByTagName(FREEMAPS_GROUP);
        if (freemapGroups.getLength() != 1) {
            throw new IllegalStateException("Wrong number of FREEMAPS_GROUP : " + freemapGroups.getLength());
        }
        parseFreeMaps((Element) freemapGroups.item(0), frieze);
        //
        return frieze;
    }

    private List<StayPeriod> parseStays(Element staysRootElement) {
        List<StayPeriod> stayPeriods = new LinkedList<>();
        NodeList stayElements = staysRootElement.getChildNodes();
        for (int i = 0; i < stayElements.getLength(); i++) {
            if (stayElements.item(i).getNodeName().equals(STAY_ELEMENT)) {
                Element e = (Element) stayElements.item(i);
                if (e.getAttribute(TIME_FORMAT_ATR).equals(TimeFormat.LOCAL_TIME.name())) {
                    stayPeriods.add(parseStayPeriodLocalTime(e));
                } else if (e.getAttribute(TIME_FORMAT_ATR).equals(TimeFormat.TIME_MIN.name())) {
                    stayPeriods.add(parseStayPeriodSimpleTime(e));
                } else {
                    throw new UnsupportedOperationException("Time format not recognized: " + e.getAttribute(TIME_FORMAT_ATR));
                }
            }
        }
        return stayPeriods;
    }

    private List<StayPeriod> parseStaysInFreize(Element staysRootElement) {
        List<StayPeriod> stayPeriods = new LinkedList<>();
        NodeList stayElements = staysRootElement.getChildNodes();
        for (int i = 0; i < stayElements.getLength(); i++) {
            if (stayElements.item(i).getNodeName().equals(STAY_ELEMENT_REF)) {
                Element e = (Element) stayElements.item(i);
                long id = Long.parseLong(e.getAttribute(ID_ATR));
                var stay = StayFactory.getStay(id);
                if (stay == null) {
                    throw new UnsupportedOperationException("StayPerido reference does not exist " + id);
                }
                stayPeriods.add(stay);
            }
        }
        return stayPeriods;
    }

    private StayPeriodLocalDate parseStayPeriodLocalTime(Element stayElement) {
        // <stay endDate="20" id="1" person="5" startDate="0" timeFormat="LOCAL_TIME"/>
        long id = Long.parseLong(stayElement.getAttribute(ID_ATR));
        long personID = Long.parseLong(stayElement.getAttribute(PERSON_ATR));
        Person person = PersonFactory.getPerson(personID);
        if (person == null) {
            throw new IllegalStateException();
        }
        long placeID = Long.parseLong(stayElement.getAttribute(PLACE_ID_ATR));
        Place place = PlaceFactory.getPlace(placeID);
        if (place == null) {
            throw new IllegalStateException();
        }
        String startS = stayElement.getAttribute(START_DATE_ATR);
        String endS = stayElement.getAttribute(END_DATE_ATR);
        LocalDate start = LocalDate.parse(startS);
        LocalDate end = LocalDate.parse(endS);
        StayPeriodLocalDate stay = StayFactory.createStayPeriodLocalDate(id, person, start, end, place);
        return stay;
    }

    private StayPeriodSimpleTime parseStayPeriodSimpleTime(Element stayElement) {
        // <stay endDate="20" id="1" person="5" startDate="0" timeFormat="TIME_MIN"/>
        long id = Long.parseLong(stayElement.getAttribute(ID_ATR));
        long personID = Long.parseLong(stayElement.getAttribute(PERSON_ATR));
        Person person = PersonFactory.getPerson(personID);
        if (person == null) {
            throw new IllegalStateException("Could not load StayPeriodSimpleTime id=" + id + " with personID=" + personID);
        }
        long placeID = Long.parseLong(stayElement.getAttribute(PLACE_ID_ATR));
        Place place = PlaceFactory.getPlace(placeID);
        if (place == null) {
            throw new IllegalStateException("Could not load StayPeriodSimpleTime id=" + id + " with placeID=" + placeID);
        }
        var start = Double.parseDouble(stayElement.getAttribute(START_DATE_ATR));
        var end = Double.parseDouble(stayElement.getAttribute(END_DATE_ATR));
        StayPeriodSimpleTime stay = StayFactory.createStayPeriodSimpleTime(id, person, start, end, place);
        return stay;
    }

    private void parseFreeMaps(Element freemapsRootElement, Frieze frieze) {
        NodeList freemapElements = freemapsRootElement.getChildNodes();
        for (int i = 0; i < freemapElements.getLength(); i++) {
            if (freemapElements.item(i).getNodeName().equals(FREEMAP_ELEMENT)) {
                Element e = (Element) freemapElements.item(i);
                parseFreeMap(e, frieze);
            }
        }
    }

    private void parseFreeMap(Element freemapElement, Frieze frieze) {
        long freeMapID = Long.parseLong(freemapElement.getAttribute(ID_ATR));
        FriezeFreeMap freeMap = FriezeFreeMapFactory.createFriezeFreeMap(freeMapID, frieze);
        if (freemapElement.hasAttribute(NAME_ATR)) {
            freeMap.setName(freemapElement.getAttribute(NAME_ATR));
        }
        // !! IMPORTANT : set all the properties before updating plots, places...
        // parsing parameters
        double width = Double.parseDouble(freemapElement.getAttribute(WIDTH_ATR));
        double height = Double.parseDouble(freemapElement.getAttribute(HEIGHT_ATR));
        freeMap.setWidth(width);
        freeMap.setHeight(height);
        //
        double personWidth = Double.parseDouble(freemapElement.getAttribute(FREEMAP_PERSON_WIDTH_ATR));
        freeMap.setPersonWidth(personWidth);
        //
        double placeNameWidth = Double.parseDouble(freemapElement.getAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR));
        freeMap.setPlaceNameWidth(placeNameWidth);
        //
        double fontSize = Double.parseDouble(freemapElement.getAttribute(FREEMAP_FONT_SIZE_ATR));
        freeMap.setFontSize(fontSize);
        //
        double plotSeparation = Double.parseDouble(freemapElement.getAttribute(FREEMAP_PLOT_SEPARATION_ATR));
        freeMap.setPlotSeparation(plotSeparation);
        //
        boolean plotVisible = Boolean.parseBoolean(freemapElement.getAttribute(FREEMAP_PLOT_VISIBILITY_ATR));
        freeMap.setPlotVisibility(plotVisible);
        //
        double plotSize = Double.parseDouble(freemapElement.getAttribute(FREEMAP_PLOT_SIZE_ATR));
        freeMap.setPlotSize(plotSize);
        //
        NodeList portraitsGroups = freemapElement.getElementsByTagName(PORTRAITS_GROUP);
//        if (portraitsGroups.getLength() < 1) {
//            throw new IllegalStateException();
//        }
        if (portraitsGroups.getLength() > 0) {
            parsePortraits((Element) portraitsGroups.item(0), freeMap);
        }
        //
        NodeList freemapPlacesGroups = freemapElement.getElementsByTagName(FREEMAP_PLACES_GROUP);
        if (freemapPlacesGroups.getLength() < 1) {
            throw new IllegalStateException();
        }
        parseFreemapPlaces((Element) freemapPlacesGroups.item(0), freeMap);
        //
        NodeList plotsGroups = freemapElement.getElementsByTagName(PLOTS_GROUP);
        if (plotsGroups.getLength() < 1) {
            throw new IllegalStateException();
        }
        parsePlots((Element) plotsGroups.item(0), frieze, freeMap);
        //
        frieze.addFriezeFreeMap(freeMap);
    }

    private void parsePortraits(Element plotsRootElement, FriezeFreeMap freeMap) {
        var milliIn = System.currentTimeMillis();
        // <portrait person="21" xPos="690.0" yPos="179.8"/>
        NodeList plotElements = plotsRootElement.getChildNodes();
        for (int i = 0; i < plotElements.getLength(); i++) {
            if (plotElements.item(i).getNodeName().equals(PORTRAIT_ELEMENT)) {
                Element e = (Element) plotElements.item(i);
                long personID = Long.parseLong(e.getAttribute(PERSON_ATR));
                double xPos = Double.parseDouble(e.getAttribute(X_POS_ATR));
                double yPos = Double.parseDouble(e.getAttribute(Y_POS_ATR));
                double radius = Double.parseDouble(e.getAttribute(RADIUS_ATR));
                FreeMapPortrait portrait = freeMap.getPortrait(personID);
                if (portrait == null) {
                    System.err.println(PersonFactory.getPERSONS());
                    throw new IllegalStateException("Cannot find portrait with personID=" + personID);
                }
                portrait.setX(xPos);
                portrait.setY(yPos);
                portrait.setRadius(radius);
            }
        }
        //
        var milliOut = System.currentTimeMillis();
        var time = milliOut - milliIn;
        if (time > 1) {
            LOG.log(Level.SEVERE, "Parsed portrairs for freemap {0}\n > took {1}ms.", new Object[]{freeMap.getName(), Long.toString(time)});
        }
    }

    private void parseFreemapPlaces(Element freemapPlacesRootElement, FriezeFreeMap freeMap) {
        //  <freeMapPlace height="72.0" placeID="6" yPos="439.2"/>
        NodeList freemapPlaceElements = freemapPlacesRootElement.getChildNodes();
        for (int i = 0; i < freemapPlaceElements.getLength(); i++) {
            if (freemapPlaceElements.item(i).getNodeName().equals(FREEMAP_PLACE_ELEMENT)) {
                Element e = (Element) freemapPlaceElements.item(i);
                long placeID = Long.parseLong(e.getAttribute(PLACE_ID_ATR));
                double height = Double.parseDouble(e.getAttribute(HEIGHT_ATR));
                double yPos = Double.parseDouble(e.getAttribute(Y_POS_ATR));
                FreeMapPlace freeMapPlace = freeMap.getFreeMapPlace(placeID);
                if (freeMapPlace == null) {
                    throw new IllegalStateException("Cannot find freeMapPlace with personID=" + placeID);
                }
                freeMapPlace.setHeight(height);
                freeMapPlace.setY(yPos);
            }
        }
    }

    private void parsePlots(Element plotsRootElement, Frieze frieze, FriezeFreeMap freeMap) {
        // <plot stayID="1" type="START" xPos="10.350877192982455" yPos="23.0"/>
        NodeList plotElements = plotsRootElement.getChildNodes();
        List<Plot> existingPlots = freeMap.getPlots();
        for (int i = 0; i < plotElements.getLength(); i++) {
            if (plotElements.item(i).getNodeName().equals(PLOT_ELEMENT)) {
                Element e = (Element) plotElements.item(i);
                String typeS = e.getAttribute(TYPE_ATR);
                long stayID = Long.parseLong(e.getAttribute(STAY_ID_ATR));
                double xPos = Double.parseDouble(e.getAttribute(X_POS_ATR));
                double yPos = Double.parseDouble(e.getAttribute(Y_POS_ATR));
                Optional<StayPeriod> stayOp = frieze.getStayPeriods().stream().filter(s -> s.getId() == stayID).findAny();
                if (stayOp.isEmpty()) {
                    throw new IllegalStateException();
                }
                Plot plot;
                PlotType type;
                // TODO use values
                if (typeS.equals(PlotType.START.name())) {
                    type = PlotType.START;
                } else if (typeS.equals(PlotType.END.name())) {
                    type = PlotType.END;
                } else {
                    throw new UnsupportedOperationException();
                }
                plot = existingPlots.stream().filter(p -> p.getLinkedElementID() == stayID && p.getType() == type).findFirst().orElse(null);
                if (plot == null) {
                    throw new IllegalStateException("Cannot find plot with stayID=" + stayID + " and of type " + typeS);
                }
                plot.setX(xPos);
                plot.setY(yPos);
            }
        }
    }

    private List<PictureChronology> parsePictureChronologies(TimeLineProject project, Element pictureChronologiesRootElement) {
        List<PictureChronology> pictureChronologys = new LinkedList<>();
        NodeList pictureChronologiesElements = pictureChronologiesRootElement.getChildNodes();
        for (int i = 0; i < pictureChronologiesElements.getLength(); i++) {
            if (pictureChronologiesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_ELEMENT)) {
                Element e = (Element) pictureChronologiesElements.item(i);
                PictureChronology pC = parsePictureChronology(project, e);
                pictureChronologys.add(pC);
            }
        }
        return pictureChronologys;
    }

    private PictureChronology parsePictureChronology(TimeLineProject project, Element pictureChronologyElement) {
        long id = Long.parseLong(pictureChronologyElement.getAttribute(ID_ATR));
        String name = pictureChronologyElement.getAttribute(NAME_ATR);
        double width = Double.parseDouble(pictureChronologyElement.getAttribute(WIDTH_ATR));
        double height = Double.parseDouble(pictureChronologyElement.getAttribute(HEIGHT_ATR));
        //
        List<ChronologyPictureMiniature> miniatures = new LinkedList<>();
        List<ChronologyLink> links = new LinkedList<>();
        //
        NodeList miniaturesElements = pictureChronologyElement.getChildNodes();
        for (int i = 0; i < miniaturesElements.getLength(); i++) {
            if (miniaturesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_MINIATURE_ELEMENT)) {
                Element e = (Element) miniaturesElements.item(i);
                var miniature = parseChronologyPictureMiniature(project, e);
                miniatures.add(miniature);
            } else if (miniaturesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_LINK_ELEMENT)) {
                Element e = (Element) miniaturesElements.item(i);
                links.add(parsePictureChronologyLink(e));
            }
        }
        //
        var pictureChronology = PictureChronologyFactory.createPictureChronology(id, project, name, miniatures, links);
        pictureChronology.setWidth(width);
        pictureChronology.setHeight(height);
        //
        return pictureChronology;
    }

    private ChronologyPictureMiniature parseChronologyPictureMiniature(TimeLineProject project, Element miniatureElement) {
//        <pictureChronologyMiniature id="138" pictureRef="125" xPos="897.0" yPos="329.0" scale="0.5"/>
        long id = Long.parseLong(miniatureElement.getAttribute(ID_ATR));
        long pictureRef = Long.parseLong(miniatureElement.getAttribute(PICTURE_REF_ELEMENT));
        double xPos = Double.parseDouble(miniatureElement.getAttribute(X_POS_ATR));
        double yPos = Double.parseDouble(miniatureElement.getAttribute(Y_POS_ATR));
        double scale = Double.parseDouble(miniatureElement.getAttribute(SCALE_ATR));
        var miniature = PictureChronologyFactory.createChronologyPictureMiniature(id, IPicture.getPicture(pictureRef), new Point2D(xPos, yPos), scale);
        miniature.setUseCustomTime(!miniature.isInSyncWithPicture());
        if (!miniature.isInSyncWithPicture()) {
            parseObjectTimeValue(miniatureElement, miniature.getDateObject());
        }
        return miniature;
    }

    private static ChronologyLink parsePictureChronologyLink(Element linkElement) {
        var id = Long.parseLong(linkElement.getAttribute(ID_ATR));
        var type = ChronologyLinkType.valueOf(linkElement.getAttribute(TYPE_ATR));
        var fromID = Long.parseLong(linkElement.getAttribute(FROM_ATR));
        var from = PictureChronologyFactory.getChronologyPictureMiniature(fromID);
        var toID = Long.parseLong(linkElement.getAttribute(TO_ATR));
        var to = PictureChronologyFactory.getChronologyPictureMiniature(toID);
        var personRef = Long.parseLong(linkElement.getAttribute(PERSON_REF_ATR));
        var person = PersonFactory.getPerson(personRef);
        var paramsAsString = linkElement.getAttribute(PARAMETERS_ATR);
        var parameters = CustomFileUtils.toDoubleArray(paramsAsString);
        var link = PictureChronologyFactory.createChronologyLink(id, person, from, to, type, parameters);
        return link;
    }

    private static Element createPlaceElement(Document doc, Place place, String fromPlace) {
        LOG.log(Level.FINE, "> Creating place {0} from {1}", new Object[]{place.getName(), fromPlace});
        Element placeElement = doc.createElement(PLACE_ELEMENT);
        placeElement.setAttribute(ID_ATR, Long.toString(place.getId()));
        placeElement.setAttribute(NAME_ATR, place.getName());
        placeElement.setAttribute(PLACE_LEVEL_ATR, place.getLevel().name());
        placeElement.setAttribute(COLOR_ATR, place.getColor().toString());
        place.getPlaces().forEach(p -> placeElement.appendChild(createPlaceElement(doc, p, place.getName())));
        return placeElement;
    }

    private static Element createPersonElement(Document doc, Person person) {
        LOG.log(Level.FINE, "> Creating person {0}", new Object[]{person.getName()});
        var personElement = doc.createElement(PERSON_ELEMENT);
        personElement.setAttribute(ID_ATR, Long.toString(person.getId()));
        personElement.setAttribute(NAME_ATR, person.getName());
        if (person.getDefaultPortrait() != null) {
            personElement.setAttribute(DEFAULT_PORTRAIT_REF_ATR, Long.toString(person.getDefaultPortrait().getId()));
        }
        personElement.setAttribute(COLOR_ATR, person.getColor().toString());
        personElement.setAttribute(TIME_FORMAT_ATR, person.getTimeFormat().name());
        switch (person.getTimeFormat()) {
            case LOCAL_TIME -> {
                if (person.getDateOfBirth() != null) {
                    personElement.setAttribute(DATE_OF_BIRTH_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(person.getDateOfBirth()));
                }
                if (person.getDateOfDeath() != null) {
                    personElement.setAttribute(DATE_OF_DEATH_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(person.getDateOfDeath()));
                }
            }
            case TIME_MIN -> {
                personElement.setAttribute(DATE_OF_BIRTH_ATR, Long.toString(person.getTimeOfBirth()));
                personElement.setAttribute(DATE_OF_DEATH_ATR, Long.toString(person.getTimeOfDeath()));
            }
            default ->
                throw new UnsupportedOperationException("Unsupported timefomat : " + person.getTimeFormat());
        }
        person.getPortraits().forEach(portrait -> {
            var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
            portraitElement.setAttribute(ID_ATR, Long.toString(portrait.getId()));
            portraitElement.setAttribute(PATH_ATR, portrait.getProjectRelativePath());
            saveObjectTime(portraitElement, portrait);
            personElement.appendChild(portraitElement);
        });
        return personElement;
    }

    private static void saveObjectTime(Element targetElement, IDateObject aDateObject) {
        targetElement.setAttribute(TIME_FORMAT_ATR, aDateObject.getTimeFormat().name());
        switch (aDateObject.getTimeFormat()) {
            case LOCAL_TIME -> {
                if (aDateObject.getDate() != null) {
                    targetElement.setAttribute(DATE_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(aDateObject.getDate()));
                }
            }
            case TIME_MIN -> {
                targetElement.setAttribute(DATE_ATR, Double.toString(aDateObject.getTimestamp()));
            }
            default ->
                throw new UnsupportedOperationException("Unsupported timefomat : " + aDateObject.getTimeFormat());
        }
    }

    private static Element createPictureElement(Document doc, Picture picture) {
        Element pictureElement = doc.createElement(PICTURE_ELEMENT);
        pictureElement.setAttribute(ID_ATR, Long.toString(picture.getId()));
        pictureElement.setAttribute(NAME_ATR, picture.getName());
        pictureElement.setAttribute(PATH_ATR, picture.getProjectRelativePath());
        pictureElement.setAttribute(WIDTH_ATR, Integer.toString((int) picture.getWidth()));
        pictureElement.setAttribute(HEIGHT_ATR, Integer.toString((int) picture.getHeight()));
        picture.getPersons().forEach(person -> {
            Element personElement = doc.createElement(PERSON_REF_ELEMENT);
            personElement.setAttribute(ID_ATR, Long.toString(person.getId()));
            pictureElement.appendChild(personElement);
        });
        picture.getPlaces().forEach(place -> {
            Element placeElement = doc.createElement(PLACE_REF_ELEMENT);
            placeElement.setAttribute(ID_ATR, Long.toString(place.getId()));
            pictureElement.appendChild(placeElement);
        });
        saveObjectTime(pictureElement, picture);
        return pictureElement;
    }

    private static Element createFriezeElement(Document doc, Frieze frieze) {
        LOG.log(Level.INFO, "Saving Frieze {0}", new Object[]{frieze.getName()});
        Element friezeElement = doc.createElement(FRIEZE_ELEMENT);
        friezeElement.setAttribute(NAME_ATR, frieze.getName());
        friezeElement.setAttribute(ID_ATR, Long.toString(frieze.getId()));
        // Stays
        Element staysElement = doc.createElement(STAYS_REF_GROUP);
        friezeElement.appendChild(staysElement);
        frieze.getStayPeriods().forEach(stay -> staysElement.appendChild(createStayElementInFreize(doc, stay)));
        // FreeMaps
        Element freemapsElement = doc.createElement(FREEMAPS_GROUP);
        friezeElement.appendChild(freemapsElement);
        frieze.getFriezeFreeMaps().forEach(freeMap -> freemapsElement.appendChild(createFreeMapElement(doc, freeMap)));
        return friezeElement;
    }

    private static Element createStayElement(Document doc, StayPeriod stay) {
        Element stayElement = doc.createElement(STAY_ELEMENT);
        stayElement.setAttribute(ID_ATR, Long.toString(stay.getId()));
        stayElement.setAttribute(PERSON_ATR, Long.toString(stay.getPerson().getId()));
        switch (stay.getTimeFormat()) {
            case LOCAL_TIME -> {
                LocalDate startDate = LocalDate.ofEpochDay((long) stay.getStartDate());
                LocalDate endDate = LocalDate.ofEpochDay((long) stay.getEndDate());
                stayElement.setAttribute(START_DATE_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(startDate));
                stayElement.setAttribute(END_DATE_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(endDate));
            }
            case TIME_MIN -> {
                stayElement.setAttribute(START_DATE_ATR, Double.toString(stay.getStartDate()));
                stayElement.setAttribute(END_DATE_ATR, Double.toString(stay.getEndDate()));
            }
            default ->
                throw new UnsupportedOperationException("Unknown time format : " + stay.getTimeFormat());
        }
        stayElement.setAttribute(TIME_FORMAT_ATR, stay.getTimeFormat().name());
        stayElement.setAttribute(PLACE_ID_ATR, Long.toString(stay.getPlace().getId()));
        return stayElement;
    }

    private static Element createStayElementInFreize(Document doc, StayPeriod stay) {
        Element stayElement = doc.createElement(STAY_ELEMENT_REF);
        stayElement.setAttribute(ID_ATR, Long.toString(stay.getId()));
        return stayElement;
    }

    private static Element createFreeMapElement(Document doc, FriezeFreeMap friezeFreeMap) {
        var friezeFreeMapElement = doc.createElement(FREEMAP_ELEMENT);
        friezeFreeMapElement.setAttribute(NAME_ATR, friezeFreeMap.getName());
        friezeFreeMapElement.setAttribute(ID_ATR, Long.toString(friezeFreeMap.getId()));
        friezeFreeMapElement.setAttribute(WIDTH_ATR, Double.toString(friezeFreeMap.getFreeMapWidth()));
        friezeFreeMapElement.setAttribute(HEIGHT_ATR, Double.toString(friezeFreeMap.getFreeMapHeight()));
        friezeFreeMapElement.setAttribute(FREEMAP_PERSON_WIDTH_ATR, Double.toString(friezeFreeMap.getPersonWidth()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR, Double.toString(friezeFreeMap.getPlaceNamesWidth()));
        friezeFreeMapElement.setAttribute(FREEMAP_FONT_SIZE_ATR, Double.toString(friezeFreeMap.getFontSize()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_SEPARATION_ATR, Double.toString(friezeFreeMap.getPlotSeparation()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_VISIBILITY_ATR, Boolean.toString(friezeFreeMap.getPlotVisibility()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_SIZE_ATR, Double.toString(friezeFreeMap.getPlotSize()));
        //
        var portraitsGroupElement = doc.createElement(PORTRAITS_GROUP);
        friezeFreeMap.getPortraits().forEach(portrait -> portraitsGroupElement.appendChild(createFreeMapPortraitElement(doc, portrait)));
        friezeFreeMapElement.appendChild(portraitsGroupElement);
        //
        var plotsGroupElement = doc.createElement(PLOTS_GROUP);
        friezeFreeMap.getPlots().forEach(plot -> plotsGroupElement.appendChild(createPlotElement(doc, plot)));
        friezeFreeMapElement.appendChild(plotsGroupElement);
        //
        var placesGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
        friezeFreeMap.getPlaces().forEach(place -> placesGroupElement.appendChild(createFreeMapPlaceElement(doc, place)));
        friezeFreeMapElement.appendChild(placesGroupElement);
        //
        var linksGroupElement = doc.createElement(LINKS_GROUP);
        friezeFreeMap.getStayLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
        friezeFreeMap.getTravelLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
        friezeFreeMapElement.appendChild(linksGroupElement);
        //
        return friezeFreeMapElement;
    }

    private static Element createFreeMapPortraitElement(Document doc, FreeMapPortrait portrait) {
        var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
        portraitElement.setAttribute(PERSON_ATR, Long.toString(portrait.getPerson().getId()));
        portraitElement.setAttribute(X_POS_ATR, Double.toString(portrait.getX()));
        portraitElement.setAttribute(Y_POS_ATR, Double.toString(portrait.getY()));
        portraitElement.setAttribute(RADIUS_ATR, Double.toString(portrait.getRadius()));
        return portraitElement;
    }

    private static Element createPlotElement(Document doc, Plot plot) {
        var plotElement = doc.createElement(PLOT_ELEMENT);
        plotElement.setAttribute(TYPE_ATR, plot.getType().name());
        plotElement.setAttribute(STAY_ID_ATR, Long.toString(plot.getLinkedElementID()));
        plotElement.setAttribute(X_POS_ATR, Double.toString(plot.getX()));
        plotElement.setAttribute(Y_POS_ATR, Double.toString(plot.getY()));
        return plotElement;
    }

    private static Element createLinkElement(Document doc, FreeMapLink link) {
        var linkElement = doc.createElement(LINK_ELEMENT);
        linkElement.setAttribute(TYPE_ATR, link.getType().name());
        linkElement.setAttribute(START_ID_ATR, Long.toString(link.getBeginPlot().getLinkedElementID()));
        linkElement.setAttribute(END_ID_ATR, Long.toString(link.getEndPlot().getLinkedElementID()));
        if (link instanceof StayLink stayLink) {
            linkElement.setAttribute(END_ID_ATR, Long.toString(stayLink.getStayPeriod().getId()));
        } else if (link instanceof TravelLink travelLink) {
            linkElement.setAttribute(END_ID_ATR, Long.toString(travelLink.getPerson().getId()));
        }
        linkElement.setAttribute(STAY_ID_ATR, Long.toString(link.getEndPlot().getLinkedElementID()));
        return linkElement;
    }

    private static Element createFreeMapPlaceElement(Document doc, FreeMapPlace freeMapPlace) {
        var placeElement = doc.createElement(FREEMAP_PLACE_ELEMENT);
        placeElement.setAttribute(HEIGHT_ATR, Double.toString(freeMapPlace.getHeight()));
        placeElement.setAttribute(PLACE_ID_ATR, Long.toString(freeMapPlace.getPlace().getId()));
        placeElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPlace.getYPos()));
        return placeElement;
    }

    private static Element createPictureChronologyElement(Document doc, PictureChronology pictureChronology) {
        var pictureChronologyElement = doc.createElement(PICTURE_CHRONOLOGY_ELEMENT);
        pictureChronologyElement.setAttribute(ID_ATR, Long.toString(pictureChronology.getId()));
        pictureChronologyElement.setAttribute(NAME_ATR, pictureChronology.getName());
        pictureChronologyElement.setAttribute(WIDTH_ATR, Double.toString(pictureChronology.getWidth()));
        pictureChronologyElement.setAttribute(HEIGHT_ATR, Double.toString(pictureChronology.getHeight()));
        //
        pictureChronology.getChronologyPictures().forEach(miniature -> pictureChronologyElement.appendChild(createPictureChronologyMiniature(doc, miniature)));
        pictureChronology.getLinks().forEach(link -> pictureChronologyElement.appendChild(createPictureChronologyLink(doc, link)));
        return pictureChronologyElement;
    }

    private static Element createPictureChronologyMiniature(Document doc, ChronologyPictureMiniature miniature) {
        var pictureChronologyMiniatureElement = doc.createElement(PICTURE_CHRONOLOGY_MINIATURE_ELEMENT);
        pictureChronologyMiniatureElement.setAttribute(ID_ATR, Long.toString(miniature.getId()));
        pictureChronologyMiniatureElement.setAttribute(X_POS_ATR, Double.toString(miniature.getPosition().getX()));
        pictureChronologyMiniatureElement.setAttribute(Y_POS_ATR, Double.toString(miniature.getPosition().getY()));
        pictureChronologyMiniatureElement.setAttribute(PICTURE_REF_ELEMENT, Long.toString(miniature.getPicture().getId()));
        pictureChronologyMiniatureElement.setAttribute(SCALE_ATR, Double.toString(miniature.getScale()));
        if (!miniature.isInSyncWithPicture()) {
            saveObjectTime(pictureChronologyMiniatureElement, miniature.getDateObject());
        }
        return pictureChronologyMiniatureElement;
    }

    private static Element createPictureChronologyLink(Document doc, ChronologyLink link) {
        var pictureChronologyLinkElement = doc.createElement(PICTURE_CHRONOLOGY_LINK_ELEMENT);
        pictureChronologyLinkElement.setAttribute(ID_ATR, Long.toString(link.getId()));
        pictureChronologyLinkElement.setAttribute(TYPE_ATR, link.getLinkType().name());
        pictureChronologyLinkElement.setAttribute(FROM_ATR, Long.toString(link.getStartMiniature().getId()));
        pictureChronologyLinkElement.setAttribute(TO_ATR, Long.toString(link.getEndMiniature().getId()));
        pictureChronologyLinkElement.setAttribute(PERSON_REF_ATR, Long.toString(link.getPerson().getId()));
        pictureChronologyLinkElement.setAttribute(PARAMETERS_ATR, Arrays.toString(link.getLinkParameters()));
        return pictureChronologyLinkElement;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
