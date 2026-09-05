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

package com.github.noony.app.timelinefx.save.v4;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.IDateObject;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Messages;
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
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javax.xml.XMLConstants;
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
import static com.github.noony.app.timelinefx.core.TimeFormat.LOCAL_TIME;

/**
 * Reads and writes {@link TimeLineProject} objects for save format version 4.
 *
 * @author hamon
 */
@ServiceProvider(service = TimelineProjectProvider.class)
public class TimeProjectProviderV4 implements TimelineProjectProvider {

    /**
     * XML root element name for a project.
     */
    public static final String PROJECT_GROUP = "PROJECT";

    /**
     * XML group element name for the project's places.
     */
    public static final String PLACES_GROUP = "PLACES";

    /**
     * XML group element name for the project's persons.
     */
    public static final String PERSONS_GROUP = "PERSONS";

    /**
     * XML group element name for the project's pictures.
     */
    public static final String PICTURES_GROUP = "PICTURES";

    /**
     * XML group element name for the project's friezes.
     */
    public static final String FRIEZES_GROUP = "FRIEZES";

    /**
     * XML group element name for the project's stays.
     */
    public static final String STAYS_GROUP = "STAYS";

    /**
     * XML group element name for a frieze's stay references.
     */
    public static final String STAYS_REF_GROUP = "stays";

    /**
     * XML group element name for a person's portraits.
     */
    public static final String PORTRAITS_GROUP = "portraits";

    /**
     * XML group element name for the project's picture chronologies.
     */
    public static final String PICTURE_CHRONOLOGIES_GROUP = "PICTURE_CHRONOLOGIES";

    /**
     * XML element name for a place.
     */
    public static final String PLACE_ELEMENT = "place";

    /**
     * XML element name for a reference to a place.
     */
    public static final String PLACE_REF_ELEMENT = "placeRef";

    /**
     * XML element name for a person.
     */
    public static final String PERSON_ELEMENT = "person";

    /**
     * XML element name for a reference to a person.
     */
    public static final String PERSON_REF_ELEMENT = "personRef";

    /**
     * XML element name for a picture.
     */
    public static final String PICTURE_ELEMENT = "picture";

    /**
     * XML element name for a reference to a picture.
     */
    public static final String PICTURE_REF_ELEMENT = "pictureRef";

    /**
     * XML element name for a frieze.
     */
    public static final String FRIEZE_ELEMENT = "frieze";

    /**
     * XML element name for a stay.
     */
    public static final String STAY_ELEMENT = "stay";

    /**
     * XML element name for a reference to a stay.
     */
    public static final String STAY_ELEMENT_REF = "stayRef";

    /**
     * XML element name for a portrait.
     */
    public static final String PORTRAIT_ELEMENT = "portrait";

    /**
     * XML element name for a picture chronology.
     */
    public static final String PICTURE_CHRONOLOGY_ELEMENT = "pictureChronology";

    /**
     * XML element name for a picture chronology miniature.
     */
    public static final String PICTURE_CHRONOLOGY_MINIATURE_ELEMENT = "pictureChronologyMiniature";

    /**
     * XML element name for a picture chronology link.
     */
    public static final String PICTURE_CHRONOLOGY_LINK_ELEMENT = "pictureChronologyLink";

    /**
     * XML attribute name for the pictures folder location.
     */
    public static final String PICTURES_LOCATION_ATR = "picsLoc";

    /**
     * XML attribute name for the portraits folder location.
     */
    public static final String PORTRAIT_FOLDER_ATR = "portraitsFolder";

    /**
     * XML attribute name for the pictures folder location.
     */
    public static final String PICTURES_FOLDER_ATR = "picturesFolder";

    /**
     * XML attribute name for the miniatures folder location.
     */
    public static final String MINIATURES_FOLDER_ATR = "miniaturesFolder";

    /**
     * XML attribute name for an element's id.
     */
    public static final String ID_ATR = "id";

    /**
     * XML attribute name for an element's name.
     */
    public static final String NAME_ATR = "name";

    /**
     * XML attribute name for an element's type.
     */
    public static final String TYPE_ATR = "type";

    /**
     * XML attribute name for a file path.
     */
    public static final String PATH_ATR = "path";

    /**
     * XML attribute name for a date/time value.
     */
    public static final String DATE_ATR = "date";

    /**
     * XML attribute name for a place's level.
     */
    public static final String PLACE_LEVEL_ATR = "level";

    /**
     * XML attribute name for a color value.
     */
    public static final String COLOR_ATR = "color";

    /**
     * XML attribute name for a reference to a stay's person.
     */
    public static final String PERSON_ATR = "person";

    /**
     * XML attribute name for a person's default portrait reference.
     */
    public static final String DEFAULT_PORTRAIT_REF_ATR = "defaultPortraitRef";

    /**
     * XML attribute name for a person's date of birth.
     */
    public static final String DATE_OF_BIRTH_ATR = "dateOfBirth";

    /**
     * XML attribute name for a person's date of death.
     */
    public static final String DATE_OF_DEATH_ATR = "dateOfDeath";

    /**
     * XML attribute name for a stay's start date.
     */
    public static final String START_DATE_ATR = "startDate";

    /**
     * XML attribute name for a stay's end date.
     */
    public static final String END_DATE_ATR = "endDate";

    /**
     * XML attribute name for a time format value.
     */
    public static final String TIME_FORMAT_ATR = "timeFormat";

    /**
     * XML attribute name for a reference to a stay period.
     */
    public static final String STAY_ID_ATR = "stayID";

    /**
     * XML attribute name for a stay's start plot id.
     */
    public static final String START_ID_ATR = "startID";

    /**
     * XML attribute name for a stay's end plot id.
     */
    public static final String END_ID_ATR = "endID";

    /**
     * XML attribute name for a reference to a place.
     */
    public static final String PLACE_ID_ATR = "placeID";

    /**
     * XML attribute name for a chronology link's origin.
     */
    public static final String FROM_ATR = "from";

    /**
     * XML attribute name for a chronology link's destination.
     */
    public static final String TO_ATR = "to";

    /**
     * XML attribute name for a reference to a person.
     */
    public static final String PERSON_REF_ATR = "personRef";

    /**
     * XML attribute name for a reference to a place.
     */
    public static final String PLACE_REF_ATR = "placeRef";

    /**
     * XML attribute name for a reference to a portrait.
     */
    public static final String PORTRAIT_REF_ATR = "portraitRef";

    /**
     * XML attribute name for a chronology link's parameters.
     */
    public static final String PARAMETERS_ATR = "params";

    /**
     * XML attribute name for the id of a linked object.
     */
    public static final String LINKED_OBJECT_ID_ATR = "linkedObjectID";

    /**
     * XML attribute name for a width value.
     */
    public static final String WIDTH_ATR = "width";

    /**
     * XML attribute name for a height value.
     */
    public static final String HEIGHT_ATR = "height";

    /**
     * XML attribute name for an X position.
     */
    public static final String X_POS_ATR = "xPos";

    /**
     * XML attribute name for a Y position.
     */
    public static final String Y_POS_ATR = "yPos";

    /**
     * XML attribute name for a radius value.
     */
    public static final String RADIUS_ATR = "radius";

    /**
     * XML attribute name for a scale value.
     */
    public static final String SCALE_ATR = "scale";

    /**
     * XML attribute name for a person's index within a place.
     */
    public static final String INDEX_ATR = "index";

    /**
     * XML attribute name for a connector's plot size.
     */
    public static final String PLOT_SIZE_ATR = "plotSize";

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * The save format version this provider reads and writes.
     */
    private static final String TARGET_VERSION = "4";

    /**
     * Regex matching any file name, used when scanning the portraits/pictures folders for unused files.
     */
    private static final String ANY_FILE_REGEX = "^(.*?)";

    /**
     * Default constructor, required for this provider to be discoverable as a service.
     */
    public TimeProjectProviderV4() {
    }

    @Override
    public List<String> getSupportedVersions() {
        return Arrays.asList(TARGET_VERSION);
    }

    @Override
    public TimeLineProject load(final File projectFile, final Element e) {
        final var loadMethodName = getClass().getSimpleName() + "__load";
        CustomProfiler.start(loadMethodName);
        final String projectName = e.getAttribute(NAME_ATR);
        // Load project properties
        final var portraitsFolderValue = attributeOrDefault(e, PORTRAIT_FOLDER_ATR, TimeLineProject.DEFAULT_PORTRAIT_FOLDER);
        final var picturesFolderValue = attributeOrDefault(e, PICTURES_FOLDER_ATR, TimeLineProject.DEFAULT_PICTURES_FOLDER);
        final var miniaturesFolderValue = attributeOrDefault(e, MINIATURES_FOLDER_ATR, TimeLineProject.DEFAULT_MINIATURES_FOLDER);
        //
        final Map<String, String> configParams = Map.of(
                TimeLineProject.PROJECT_FOLDER_KEY, projectFile.getParent(),
                TimeLineProject.PORTRAIT_FOLDER_KEY, portraitsFolderValue,
                TimeLineProject.PICTURES_FOLDER_KEY, picturesFolderValue,
                TimeLineProject.MINIATURES_FOLDER_KEY, miniaturesFolderValue
        );
        final var timeFormatValue = timeFormatOrDefault(e, TimeFormat.LOCAL_TIME);
        final TimeLineProject project = TimeLineProjectFactory.createProject(projectName, configParams, timeFormatValue);
        //
        final List<String> relativePathLoaded = new LinkedList<>();
        //
        final NodeList rootChildren = e.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            final Node node = rootChildren.item(i);
            if (node instanceof Element element) {
                switch (element.getTagName()) {
                    case PLACES_GROUP -> {
                        final List<Place> places = parsePlaces(element, null);
                        places.stream().filter(p -> p.getParent() == null).forEach(p -> project.addHighLevelPlace(p));
                    }
                    case PERSONS_GROUP -> {
                        final List<Person> persons = parsePersons(element, project, relativePathLoaded);
                        persons.forEach(p -> project.addPerson(p));
                    }
                    case PICTURES_GROUP -> {
                        parsePictures(element, project, relativePathLoaded);
                    }
                    case STAYS_GROUP -> {
                        final List<StayPeriod> stays = parseStays(element, timeFormatValue);
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
        warnAboutFileIssues(project, relativePathLoaded);
        //
        // FUTURE IMPROVMENT : ENABLE AUTO IMPORT => in config
        //
        CustomProfiler.stop(loadMethodName);
        return project;
    }

    @Override
    public boolean save(final TimeLineProject project, final File destFile) {
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            final Document doc = docBuilder.newDocument();
            final Element rootElement = doc.createElement(PROJECT_GROUP);
            rootElement.setAttribute(NAME_ATR, project.getName());
            rootElement.setAttribute(PROJECT_VERSION_ATR, TARGET_VERSION);
            //TODO can use other method
            final var portraitsFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getPortraitsAbsoluteFolder());
            final var picturesFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getPicturesFolder());
            final var miniaturesFolderName = CustomFileUtils.fromAbsoluteToProjectRelative(project, project.getMiniaturesFolder());
            rootElement.setAttribute(PORTRAIT_FOLDER_ATR, portraitsFolderName);
            rootElement.setAttribute(PICTURES_LOCATION_ATR, picturesFolderName);
            rootElement.setAttribute(MINIATURES_FOLDER_ATR, miniaturesFolderName);
            rootElement.setAttribute(TIME_FORMAT_ATR, project.getTimeFormat().name());
            doc.appendChild(rootElement);
            // save places
            final Element placesGroupElement = doc.createElement(PLACES_GROUP);
            rootElement.appendChild(placesGroupElement);
            project.getHighLevelPlaces().forEach(place -> placesGroupElement.appendChild(createPlaceElement(doc, place, "root")));
            // save persons
            final Element personsGroupElement = doc.createElement(PERSONS_GROUP);
            rootElement.appendChild(personsGroupElement);
            project.getPersons().forEach(person -> personsGroupElement.appendChild(createPersonElement(doc, person)));
            // save pictures
            final Element picturesGroupElement = doc.createElement(PICTURES_GROUP);
            rootElement.appendChild(picturesGroupElement);
            PictureFactory.getPictures().forEach(picture -> picturesGroupElement.appendChild(createPictureElement(doc, picture)));
            // save stays
            final Element staysGroupElement = doc.createElement(STAYS_GROUP);
            rootElement.appendChild(staysGroupElement);
            project.getStays().forEach(stay -> staysGroupElement.appendChild(createStayElement(doc, stay)));
            // save friezes
            final Element friezesGroupElement = doc.createElement(FRIEZES_GROUP);
            rootElement.appendChild(friezesGroupElement);
            project.getFriezes().forEach(frieze -> friezesGroupElement.appendChild(createFriezeElement(doc, frieze)));
            // save picture chronologies
            final Element pictureChronologiesGroupElement = doc.createElement(PICTURE_CHRONOLOGIES_GROUP);
            rootElement.appendChild(pictureChronologiesGroupElement);
            project.getPictureChronologies().forEach(picChronology -> pictureChronologiesGroupElement.appendChild(createPictureChronologyElement(doc, picChronology)));
            //
            rootElement.normalize();
            // write the content into xml file
            final TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(destFile);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException ex) {
            LOG.log(Level.SEVERE, " Exception while exporting timeline :: {0}", new Object[]{ex});
            return false;
        }
        return true;
    }

    /**
     * Finds the first direct child element with the given tag name, without recursing into
     * descendants (unlike {@link Element#getElementsByTagName(String)}, which walks the whole subtree).
     *
     * @param parent the element whose direct children are searched
     * @param tagName the tag name to look for
     * @return the first matching direct child, or {@code null} if none exists
     */
    protected static Element firstDirectChildByTagName(final Element parent, final String tagName) {
        final var children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element child && child.getTagName().equals(tagName)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Parses the given attribute as a double, failing with a message identifying the element, attribute and
     * raw value instead of the bare {@link NumberFormatException} {@link Double#parseDouble(String)} would throw.
     *
     * @param element the element carrying the attribute
     * @param attributeName the attribute to parse
     * @return the parsed value
     */
    protected static double parseDoubleAttribute(final Element element, final String attributeName) {
        final var value = element.getAttribute(attributeName);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Could not parse attribute '" + attributeName + "' of <" + element.getTagName() + "> as a double: '" + value + "'", e);
        }
    }

    /**
     * Returns the given attribute's value, or a default value if the attribute is absent.
     *
     * @param element the element carrying the attribute
     * @param attributeName the attribute to look up
     * @param defaultValue the value to return if the attribute is absent
     * @return the attribute's value, or {@code defaultValue}
     */
    private static String attributeOrDefault(final Element element, final String attributeName, final String defaultValue) {
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return defaultValue;
    }

    /**
     * Returns the {@link TimeFormat} held by the {@link #TIME_FORMAT_ATR} attribute, or a default value if the
     * attribute is absent.
     *
     * @param element the element possibly carrying the {@link #TIME_FORMAT_ATR} attribute
     * @param defaultValue the value to return if the attribute is absent
     * @return the parsed time format, or {@code defaultValue}
     */
    private static TimeFormat timeFormatOrDefault(final Element element, final TimeFormat defaultValue) {
        if (element.hasAttribute(TIME_FORMAT_ATR)) {
            return TimeFormat.valueOf(element.getAttribute(TIME_FORMAT_ATR));
        }
        return defaultValue;
    }

    /**
     * Checks that every loaded file still exists, and warns about portrait/picture files present on disk but
     * referenced by nothing that was loaded.
     *
     * @param project the project being loaded
     * @param relativePathLoaded the project-relative file paths that were referenced while loading
     */
    private static void warnAboutFileIssues(final TimeLineProject project, final List<String> relativePathLoaded) {
        // check every file exists
        relativePathLoaded.forEach(path -> {
            final var absolutePath = CustomFileUtils.fromProjectRelativeToAbsolute(project, path);
            // not optimal ...
            final File file = new File(absolutePath);
            if (!file.exists()) {
                LOG.log(Level.SEVERE, "The file {0} does not exists. (saved as {1})", new Object[]{absolutePath, path});
            }
        });
        //
        final Set<Path> absolutePathsLoaded = relativePathLoaded.stream().
                map(p -> Paths.get(CustomFileUtils.fromProjectRelativeToAbsolute(project, p))).
                map(p -> p.normalize()).
                collect(Collectors.toSet());
        // * Portraits
        final File portraitFolder = project.getPortraitsAbsoluteFolder();
        // FUTURE IMPROVMENT : create actions for unused portrait files instead of just warning
        FileUtils.listFiles(portraitFolder, new RegexFileFilter(ANY_FILE_REGEX), DirectoryFileFilter.DIRECTORY).stream().
                map(portraitFile -> Paths.get(portraitFile.toURI())).
                filter(portraitAbsolutePath -> !absolutePathsLoaded.contains(portraitAbsolutePath)).
                forEach(portraitAbsolutePath -> LOG.log(Level.WARNING, "Found unused portrait file: {0}", new Object[]{portraitAbsolutePath}));
        // * Pictures
        final File picturesFolder = project.getPicturesFolder();
        // FUTURE IMPROVMENT : create actions for unused picture files instead of just warning
        FileUtils.listFiles(picturesFolder, new RegexFileFilter(ANY_FILE_REGEX), DirectoryFileFilter.DIRECTORY).stream().
                map(pictureFile -> Paths.get(pictureFile.toURI())).
                filter(pictureAbsolutePath -> !absolutePathsLoaded.contains(pictureAbsolutePath)).
                forEach(pictureAbsolutePath -> LOG.log(Level.WARNING, "Found unused picture file: {0}", new Object[]{pictureAbsolutePath}));
    }

    private static List<Place> parsePlaces(final Element placesRootElement, final Place parentPlace) {
        final List<Place> places = new LinkedList<>();
        final NodeList placeElements = placesRootElement.getChildNodes();
        for (int i = 0; i < placeElements.getLength(); i++) {
            if (placeElements.item(i).getNodeName().equals(PLACE_ELEMENT)) {
                final Element e = (Element) placeElements.item(i);
                final Place p = parsePlace(e, parentPlace);
                places.add(p);
            }
        }
        return places;
    }

    private static Place parsePlace(final Element placeElement, final Place parentPlace) {
        // <place color="0xf5deb3ff" id="1" level="GALAXY" name="Galaxy">
        final Color color = Color.valueOf(placeElement.getAttribute(COLOR_ATR));
        final long id = Long.parseLong(placeElement.getAttribute(ID_ATR));
        final PlaceLevel level = PlaceLevel.valueOf(placeElement.getAttribute(PLACE_LEVEL_ATR));
        final String name = placeElement.getAttribute(NAME_ATR);
        final Place place = PlaceFactory.createPlace(id, name, level, parentPlace, color);
        parsePlaces(placeElement, place);
        return place;
    }

    private static List<Person> parsePersons(final Element personsRootElement, final TimeLineProject project, final List<String> relativePathLoaded) {
        final List<Person> persons = new LinkedList<>();
        final NodeList personElements = personsRootElement.getChildNodes();
        for (int i = 0; i < personElements.getLength(); i++) {
            if (personElements.item(i).getNodeName().equals(PERSON_ELEMENT)) {
                final Element e = (Element) personElements.item(i);
                final Person p = parsePerson(e, project, relativePathLoaded);
                persons.add(p);
            }
        }
        return persons;
    }

    private static Person parsePerson(final Element personElement, final TimeLineProject project, final List<String> relativePathLoaded) {
        // <person color="0x7fffd4ff" id="1" name="Obi Wan Kenobi"/>
        final var color = Color.valueOf(personElement.getAttribute(COLOR_ATR));
        final var id = Long.parseLong(personElement.getAttribute(ID_ATR));
        final var name = personElement.getAttribute(NAME_ATR);
        var defaultPortraitRef = Long.MIN_VALUE;
        if (personElement.hasAttribute(DEFAULT_PORTRAIT_REF_ATR)) {
            defaultPortraitRef = Long.parseLong(personElement.getAttribute(DEFAULT_PORTRAIT_REF_ATR));
        }
        final var person = PersonFactory.createPerson(project, id, name, color);
        parsePersonPortraits(personElement, person, defaultPortraitRef, relativePathLoaded, project.getTimeFormat());
        //
        if (personElement.hasAttribute(TIME_FORMAT_ATR)) {
            final var timeFormat = TimeFormat.valueOf(personElement.getAttribute(TIME_FORMAT_ATR));
            person.setTimeFormat(timeFormat);
            parsePersonDates(personElement, person, timeFormat);
        }
        return person;
    }

    /**
     * Parses a person's {@code <portrait>} children, registering each one and its default-portrait status.
     *
     * @param personElement the {@code <person>} element being parsed
     * @param person the person the portraits belong to
     * @param defaultPortraitRef the id of the portrait to use as the default one, or {@code Long.MIN_VALUE} if none was specified
     * @param relativePathLoaded accumulator of project-relative file paths referenced while loading
     * @param timeFormat the project's time format, used to parse each portrait's own time value
     */
    private static void parsePersonPortraits(final Element personElement, final Person person, final long defaultPortraitRef,
            final List<String> relativePathLoaded, final TimeFormat timeFormat) {
        final var childrenElements = personElement.getChildNodes();
        for (int i = 0; i < childrenElements.getLength(); i++) {
            if (childrenElements.item(i).getNodeName().equals(PORTRAIT_ELEMENT)) {
                // <portrait id="147" path="portraits\obi_wan.png"/>
                final var portraitElement = (Element) childrenElements.item(i);
                final var portraitID = Long.parseLong(portraitElement.getAttribute(ID_ATR));
                final var portraitPath = portraitElement.getAttribute(PATH_ATR);
                final var portrait = PortraitFactory.createPortrait(portraitID, person, portraitPath);
                if (portrait.getId() == defaultPortraitRef) {
                    person.setDefaultPortrait(portrait);
                } else {
                    person.addPortrait(portrait);
                }
                parseObjectTimeValue(portraitElement, portrait, timeFormat);
                relativePathLoaded.add(portraitPath);
            }
        }
    }

    /**
     * Parses a person's birth/death date or time value, in whichever form matches the given time format.
     *
     * @param personElement the {@code <person>} element being parsed
     * @param person the person the dates belong to
     * @param timeFormat the time format the dates are expressed in
     */
    private static void parsePersonDates(final Element personElement, final Person person, final TimeFormat timeFormat) {
        switch (timeFormat) {
            case LOCAL_TIME -> {
                if (personElement.hasAttribute(DATE_OF_BIRTH_ATR)) {
                    final var dateOfBirthS = personElement.getAttribute(DATE_OF_BIRTH_ATR);
                    final var dateOfBirth = LocalDate.parse(dateOfBirthS);
                    person.setDateOfBirth(dateOfBirth);
                }
                if (personElement.hasAttribute(DATE_OF_DEATH_ATR)) {
                    final var dateOfDeathS = personElement.getAttribute(DATE_OF_DEATH_ATR);
                    final var dateOfDeath = LocalDate.parse(dateOfDeathS);
                    person.setDateOfDeath(dateOfDeath);
                }
            }
            case TIME_MIN -> {
                if (personElement.hasAttribute(DATE_OF_BIRTH_ATR)) {
                    final var timeOfBirthS = personElement.getAttribute(DATE_OF_BIRTH_ATR);
                    final var timeOfBirth = Long.parseLong(timeOfBirthS);
                    person.setTimeOfBirth(timeOfBirth);
                }
                if (personElement.hasAttribute(DATE_OF_DEATH_ATR)) {
                    final var timeOfDeathS = personElement.getAttribute(DATE_OF_DEATH_ATR);
                    final var timeOfDeath = Long.parseLong(timeOfDeathS);
                    person.setTimeOfDeath(timeOfDeath);
                }
            }
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + timeFormat);
        }
    }

    protected static void parseObjectTimeValue(final Element sourceElement, final IDateObject aDateObject, final TimeFormat aTimeFormat) {
        aDateObject.setTimeFormat(aTimeFormat);
        switch (aTimeFormat) {
            case LOCAL_TIME -> {
                if (sourceElement.hasAttribute(DATE_ATR)) {
                    final var dateS = sourceElement.getAttribute(DATE_ATR);
                    try {
                        final var date = LocalDate.parse(dateS);
                        aDateObject.setDate(date);
                    } catch (DateTimeParseException e) {
                        LOG.log(Level.SEVERE, "Could not parse date {0}, for element {1}, using default date.", new Object[]{e.getMessage(), sourceElement});
                        aDateObject.setDate(LocalDate.EPOCH);
                    }
                }
            }
            case TIME_MIN -> {
                if (sourceElement.hasAttribute(DATE_ATR)) {
                    final var time = parseDoubleAttribute(sourceElement, DATE_ATR);
                    aDateObject.setTimestamp(time);
                }
            }
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + aTimeFormat);
        }
    }

    private static List<Picture> parsePictures(final Element picturesRootElement, final TimeLineProject project, final List<String> relativePathLoaded) {
        final List<Picture> pictures = new LinkedList<>();
        final NodeList picturesElements = picturesRootElement.getChildNodes();
        for (int i = 0; i < picturesElements.getLength(); i++) {
            if (picturesElements.item(i).getNodeName().equals(PICTURE_ELEMENT)) {
                final Element e = (Element) picturesElements.item(i);
                final Picture p = parsePicture(e, project, relativePathLoaded);
                pictures.add(p);
            }
        }
        return pictures;
    }

    private static Picture parsePicture(final Element pictureElement, final TimeLineProject project, final List<String> relativePathLoaded) {
        final Long milliIn = System.currentTimeMillis();
        final var id = Long.parseLong(pictureElement.getAttribute(ID_ATR));
        final var name = pictureElement.getAttribute(NAME_ATR);
        final var path = pictureElement.getAttribute(PATH_ATR);
        relativePathLoaded.add(path);
        final var width = Integer.parseInt(pictureElement.getAttribute(WIDTH_ATR));
        final var height = Integer.parseInt(pictureElement.getAttribute(HEIGHT_ATR));
        //
        final Picture picture = PictureFactory.createPicture(project, id, name, LocalDateTime.MIN, path, width, height);
        parseObjectTimeValue(pictureElement, picture, project.getTimeFormat());
        //
        final var pictureChildrenElements = pictureElement.getChildNodes();
        for (int i = 0; i < pictureChildrenElements.getLength(); i++) {
            final Node n = pictureChildrenElements.item(i);
            switch (n.getNodeName()) {
                case PERSON_REF_ELEMENT -> {
                    final Element e = (Element) n;
                    final long personID = Long.parseLong(e.getAttribute(ID_ATR));
                    final Person person = PersonFactory.getPerson(personID);
                    picture.addPerson(person);
                }
                case PLACE_REF_ELEMENT -> {
                    final Element e = (Element) n;
                    final long placeID = Long.parseLong(e.getAttribute(ID_ATR));
                    final Place place = PlaceFactory.getPlace(placeID);
                    picture.addPlace(place);
                }
                case "#text" ->
                    LOG.log(Level.FINE, "Ignoring text element");
                default ->
                    throw new UnsupportedOperationException("Could not parse child element of picture " + name + " :: " + n);
            }
        }
        //
        final var milliOut = System.currentTimeMillis();
        final var time = milliOut - milliIn;
        if (time > 1) {
            LOG.log(Level.SEVERE, "Parsed picture: {0}\n > took {1}ms.", new Object[]{name, Long.toString(time)});
        }
        return picture;
    }

    private List<Frieze> parseFriezes(final TimeLineProject project, final Element friezesRootElement) {
        final List<Frieze> friezes = new LinkedList<>();
        final NodeList friezeElements = friezesRootElement.getChildNodes();
        for (int i = 0; i < friezeElements.getLength(); i++) {
            if (friezeElements.item(i).getNodeName().equals(FRIEZE_ELEMENT)) {
                final Element e = (Element) friezeElements.item(i);
                final Frieze f = parseFrieze(project, e);
                friezes.add(f);
            }
        }
        return friezes;
    }

    private Frieze parseFrieze(final TimeLineProject project, final Element friezeElement) {
        // <frieze name="SW 1-2">
        final var name = friezeElement.getAttribute(NAME_ATR);
        final var id = Long.parseLong(friezeElement.getAttribute(ID_ATR));
        final var staysElement = firstDirectChildByTagName(friezeElement, STAYS_REF_GROUP);
        if (staysElement == null) {
            throw new IllegalStateException("Wrong number of STAYS_GROUP : 0");
        }
        final var stays = parseStaysInFreize(staysElement);
        final var frieze = FriezeFactory.createFrieze(id, project, name, stays);
        //
        final var freemapsElement = firstDirectChildByTagName(friezeElement, FreeMapProviderV4.FREEMAPS_GROUP);
        if (freemapsElement == null) {
            throw new IllegalStateException("Wrong number of FREEMAPS_GROUP : 0");
        }
        FreeMapProviderV4.parseFreeMaps(freemapsElement, frieze);
        //
        return frieze;
    }

    private List<StayPeriod> parseStays(final Element staysRootElement, final TimeFormat aTimeFormat) {
        final List<StayPeriod> stayPeriods = new LinkedList<>();
        final NodeList stayElements = staysRootElement.getChildNodes();
        for (int i = 0; i < stayElements.getLength(); i++) {
            if (stayElements.item(i).getNodeName().equals(STAY_ELEMENT)) {
                final Element e = (Element) stayElements.item(i);
                // a stay keeps the time format it was created with, which may predate a later change to the
                // project's own time format, so prefer its own attribute over the project's current default
                final var stayTimeFormat = timeFormatOrDefault(e, aTimeFormat);
                switch (stayTimeFormat) {
                    case LOCAL_TIME ->
                        stayPeriods.add(parseStayPeriodLocalTime(e));
                    case TIME_MIN ->
                        stayPeriods.add(parseStayPeriodSimpleTime(e));
                    default ->
                        throw new UnsupportedOperationException("Time format not recognized: " + stayTimeFormat);
                }
            }
        }
        return stayPeriods;
    }

    private List<StayPeriod> parseStaysInFreize(final Element staysRootElement) {
        final List<StayPeriod> stayPeriods = new LinkedList<>();
        final NodeList stayElements = staysRootElement.getChildNodes();
        for (int i = 0; i < stayElements.getLength(); i++) {
            if (stayElements.item(i).getNodeName().equals(STAY_ELEMENT_REF)) {
                final Element e = (Element) stayElements.item(i);
                final long id = Long.parseLong(e.getAttribute(ID_ATR));
                final var stay = StayFactory.getStay(id);
                if (stay == null) {
                    throw new UnsupportedOperationException("StayPeriod reference does not exist " + id);
                }
                stayPeriods.add(stay);
            }
        }
        return stayPeriods;
    }

    private StayPeriodLocalDate parseStayPeriodLocalTime(final Element stayElement) {
        // <stay endDate="20" id="1" person="5" startDate="0" timeFormat="LOCAL_TIME"/>
        final long id = Long.parseLong(stayElement.getAttribute(ID_ATR));
        final long personID = Long.parseLong(stayElement.getAttribute(PERSON_ATR));
        final Person person = PersonFactory.getPerson(personID);
        if (person == null) {
            throw new IllegalStateException();
        }
        final long placeID = Long.parseLong(stayElement.getAttribute(PLACE_ID_ATR));
        final Place place = PlaceFactory.getPlace(placeID);
        if (place == null) {
            throw new IllegalStateException();
        }
        final String startS = stayElement.getAttribute(START_DATE_ATR);
        final String endS = stayElement.getAttribute(END_DATE_ATR);
        final LocalDate start = LocalDate.parse(startS);
        final LocalDate end = LocalDate.parse(endS);
        return StayFactory.createStayPeriodLocalDate(id, person, start, end, place);
    }

    private StayPeriodSimpleTime parseStayPeriodSimpleTime(final Element stayElement) {
        // <stay endDate="20" id="1" person="5" startDate="0" timeFormat="TIME_MIN"/>
        final long id = Long.parseLong(stayElement.getAttribute(ID_ATR));
        final long personID = Long.parseLong(stayElement.getAttribute(PERSON_ATR));
        final Person person = PersonFactory.getPerson(personID);
        if (person == null) {
            throw new IllegalStateException("Could not load StayPeriodSimpleTime id=" + id + " with personID=" + personID);
        }
        final long placeID = Long.parseLong(stayElement.getAttribute(PLACE_ID_ATR));
        final Place place = PlaceFactory.getPlace(placeID);
        if (place == null) {
            throw new IllegalStateException("Could not load StayPeriodSimpleTime id=" + id + " with placeID=" + placeID);
        }
        final var start = parseDoubleAttribute(stayElement, START_DATE_ATR);
        final var end = parseDoubleAttribute(stayElement, END_DATE_ATR);
        return StayFactory.createStayPeriodSimpleTime(id, person, start, end, place);
    }

    private List<PictureChronology> parsePictureChronologies(final TimeLineProject project, final Element pictureChronologiesRootElement) {
        final List<PictureChronology> pictureChronologys = new LinkedList<>();
        final NodeList pictureChronologiesElements = pictureChronologiesRootElement.getChildNodes();
        for (int i = 0; i < pictureChronologiesElements.getLength(); i++) {
            if (pictureChronologiesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_ELEMENT)) {
                final Element e = (Element) pictureChronologiesElements.item(i);
                final PictureChronology pC = parsePictureChronology(project, e);
                pictureChronologys.add(pC);
            }
        }
        return pictureChronologys;
    }

    private PictureChronology parsePictureChronology(final TimeLineProject project, final Element pictureChronologyElement) {
        final long id = Long.parseLong(pictureChronologyElement.getAttribute(ID_ATR));
        final String name = pictureChronologyElement.getAttribute(NAME_ATR);
        final double width = parseDoubleAttribute(pictureChronologyElement, WIDTH_ATR);
        final double height = parseDoubleAttribute(pictureChronologyElement, HEIGHT_ATR);
        //
        final List<ChronologyPictureMiniature> miniatures = new LinkedList<>();
        final List<ChronologyLink> links = new LinkedList<>();
        //
        final NodeList miniaturesElements = pictureChronologyElement.getChildNodes();
        for (int i = 0; i < miniaturesElements.getLength(); i++) {
            if (miniaturesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_MINIATURE_ELEMENT)) {
                final Element e = (Element) miniaturesElements.item(i);
                final var miniature = parseChronologyPictureMiniature(e, project.getTimeFormat());
                miniatures.add(miniature);
            } else if (miniaturesElements.item(i).getNodeName().equals(PICTURE_CHRONOLOGY_LINK_ELEMENT)) {
                final Element e = (Element) miniaturesElements.item(i);
                links.add(parsePictureChronologyLink(e));
            }
        }
        //
        final var pictureChronology = PictureChronologyFactory.createPictureChronology(id, project, name, miniatures, links);
        pictureChronology.setWidth(width);
        pictureChronology.setHeight(height);
        //
        return pictureChronology;
    }

    private ChronologyPictureMiniature parseChronologyPictureMiniature(final Element miniatureElement, final TimeFormat aTimeFormat) {
        // <pictureChronologyMiniature id="138" pictureRef="125" xPos="897.0" yPos="329.0" scale="0.5"/>
        final long id = Long.parseLong(miniatureElement.getAttribute(ID_ATR));
        final long pictureRef = Long.parseLong(miniatureElement.getAttribute(PICTURE_REF_ELEMENT));
        final double xPos = parseDoubleAttribute(miniatureElement, X_POS_ATR);
        final double yPos = parseDoubleAttribute(miniatureElement, Y_POS_ATR);
        final double scale = parseDoubleAttribute(miniatureElement, SCALE_ATR);
        final var miniature = PictureChronologyFactory.createChronologyPictureMiniature(id, IPicture.getPicture(pictureRef), new Point2D(xPos, yPos), scale);
        miniature.setUseCustomTime(!miniature.isInSyncWithPicture());
        if (!miniature.isInSyncWithPicture()) {
            parseObjectTimeValue(miniatureElement, miniature.getDateObject(), aTimeFormat);
        }
        return miniature;
    }

    private static ChronologyLink parsePictureChronologyLink(final Element linkElement) {
        final var id = Long.parseLong(linkElement.getAttribute(ID_ATR));
        final var type = ChronologyLinkType.valueOf(linkElement.getAttribute(TYPE_ATR));
        final var fromID = Long.parseLong(linkElement.getAttribute(FROM_ATR));
        final var from = PictureChronologyFactory.getChronologyPictureMiniature(fromID);
        final var toID = Long.parseLong(linkElement.getAttribute(TO_ATR));
        final var to = PictureChronologyFactory.getChronologyPictureMiniature(toID);
        final var personRef = Long.parseLong(linkElement.getAttribute(PERSON_REF_ATR));
        final var person = PersonFactory.getPerson(personRef);
        final var paramsAsString = linkElement.getAttribute(PARAMETERS_ATR);
        final var parameters = CustomFileUtils.toDoubleArray(paramsAsString);
        return PictureChronologyFactory.createChronologyLink(id, person, from, to, type, parameters);
    }

    private static Element createPlaceElement(final Document doc, final Place place, final String fromPlace) {
        LOG.log(Level.FINE, "> Creating place {0} from {1}", new Object[]{place.getName(), fromPlace});
        final var placeElement = doc.createElement(PLACE_ELEMENT);
        placeElement.setAttribute(ID_ATR, Long.toString(place.getId()));
        placeElement.setAttribute(NAME_ATR, place.getName());
        placeElement.setAttribute(PLACE_LEVEL_ATR, place.getLevel().name());
        placeElement.setAttribute(COLOR_ATR, place.getColor().toString());
        place.getPlaces().forEach(p -> placeElement.appendChild(createPlaceElement(doc, p, place.getName())));
        return placeElement;
    }

    private static Element createPersonElement(final Document doc, final Person person) {
        LOG.log(Level.FINE, "> Creating person {0}", new Object[]{person.getName()});
        final var personElement = doc.createElement(PERSON_ELEMENT);
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
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + person.getTimeFormat());
        }
        person.getPortraits().forEach(portrait -> {
            final var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
            portraitElement.setAttribute(ID_ATR, Long.toString(portrait.getId()));
            portraitElement.setAttribute(PATH_ATR, portrait.getProjectRelativePath());
            saveObjectTime(portraitElement, portrait);
            personElement.appendChild(portraitElement);
        });
        return personElement;
    }

    private static void saveObjectTime(final Element targetElement, final IDateObject aDateObject) {
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
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + aDateObject.getTimeFormat());
        }
    }

    private static Element createPictureElement(final Document doc, final Picture picture) {
        final Element pictureElement = doc.createElement(PICTURE_ELEMENT);
        pictureElement.setAttribute(ID_ATR, Long.toString(picture.getId()));
        pictureElement.setAttribute(NAME_ATR, picture.getName());
        pictureElement.setAttribute(PATH_ATR, picture.getProjectRelativePath());
        pictureElement.setAttribute(WIDTH_ATR, Integer.toString((int) picture.getWidth()));
        pictureElement.setAttribute(HEIGHT_ATR, Integer.toString((int) picture.getHeight()));
        picture.getPersons().forEach(person -> {
            final Element personElement = doc.createElement(PERSON_REF_ELEMENT);
            personElement.setAttribute(ID_ATR, Long.toString(person.getId()));
            pictureElement.appendChild(personElement);
        });
        picture.getPlaces().forEach(place -> {
            final Element placeElement = doc.createElement(PLACE_REF_ELEMENT);
            placeElement.setAttribute(ID_ATR, Long.toString(place.getId()));
            pictureElement.appendChild(placeElement);
        });
        saveObjectTime(pictureElement, picture);
        return pictureElement;
    }

    private static Element createFriezeElement(final Document doc, final Frieze frieze) {
        LOG.log(Level.INFO, "Saving Frieze {0}", new Object[]{frieze.getName()});
        final var friezeElement = doc.createElement(FRIEZE_ELEMENT);
        friezeElement.setAttribute(NAME_ATR, frieze.getName());
        friezeElement.setAttribute(ID_ATR, Long.toString(frieze.getId()));
        // Stays
        final var staysElement = doc.createElement(STAYS_REF_GROUP);
        friezeElement.appendChild(staysElement);
        frieze.getStayPeriods().forEach(stay -> staysElement.appendChild(createStayElementInFreize(doc, stay)));
        // FreeMaps
        final var freemapsElement = doc.createElement(FreeMapProviderV4.FREEMAPS_GROUP);
        friezeElement.appendChild(freemapsElement);
        frieze.getFriezeFreeMaps().forEach(freeMap -> freemapsElement.appendChild(FreeMapProviderV4.saveFreeMapElement(doc, freeMap)));
        return friezeElement;
    }

    private static Element createStayElement(final Document doc, final StayPeriod stay) {
        final Element stayElement = doc.createElement(STAY_ELEMENT);
        stayElement.setAttribute(ID_ATR, Long.toString(stay.getId()));
        stayElement.setAttribute(PERSON_ATR, Long.toString(stay.getPerson().getId()));
        switch (stay.getTimeFormat()) {
            case LOCAL_TIME -> {
                final LocalDate startDate = LocalDate.ofEpochDay((long) stay.getStartDate());
                final LocalDate endDate = LocalDate.ofEpochDay((long) stay.getEndDate());
                stayElement.setAttribute(START_DATE_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(startDate));
                stayElement.setAttribute(END_DATE_ATR, IDateObject.DEFAULT_DATE_FORMATTER.format(endDate));
            }
            case TIME_MIN -> {
                stayElement.setAttribute(START_DATE_ATR, Double.toString(stay.getStartDate()));
                stayElement.setAttribute(END_DATE_ATR, Double.toString(stay.getEndDate()));
            }
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + stay.getTimeFormat());
        }
        stayElement.setAttribute(TIME_FORMAT_ATR, stay.getTimeFormat().name());
        stayElement.setAttribute(PLACE_ID_ATR, Long.toString(stay.getPlace().getId()));
        return stayElement;
    }

    private static Element createStayElementInFreize(final Document doc, final StayPeriod stay) {
        final Element stayElement = doc.createElement(STAY_ELEMENT_REF);
        stayElement.setAttribute(ID_ATR, Long.toString(stay.getId()));
        return stayElement;
    }

    private static Element createPictureChronologyElement(final Document doc, final PictureChronology pictureChronology) {
        final var pictureChronologyElement = doc.createElement(PICTURE_CHRONOLOGY_ELEMENT);
        pictureChronologyElement.setAttribute(ID_ATR, Long.toString(pictureChronology.getId()));
        pictureChronologyElement.setAttribute(NAME_ATR, pictureChronology.getName());
        pictureChronologyElement.setAttribute(WIDTH_ATR, Double.toString(pictureChronology.getWidth()));
        pictureChronologyElement.setAttribute(HEIGHT_ATR, Double.toString(pictureChronology.getHeight()));
        //
        pictureChronology.getChronologyPictures().forEach(miniature -> pictureChronologyElement.appendChild(createPictureChronologyMiniature(doc, miniature)));
        pictureChronology.getLinks().forEach(link -> pictureChronologyElement.appendChild(createPictureChronologyLink(doc, link)));
        return pictureChronologyElement;
    }

    private static Element createPictureChronologyMiniature(final Document doc, final ChronologyPictureMiniature miniature) {
        final var pictureChronologyMiniatureElement = doc.createElement(PICTURE_CHRONOLOGY_MINIATURE_ELEMENT);
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

    private static Element createPictureChronologyLink(final Document doc, final ChronologyLink link) {
        final var pictureChronologyLinkElement = doc.createElement(PICTURE_CHRONOLOGY_LINK_ELEMENT);
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
