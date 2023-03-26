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
package com.github.noony.app.timelinefx.save.v1;

import com.github.noony.app.timelinefx.core.Frieze;
import com.github.noony.app.timelinefx.core.FriezeFactory;
import com.github.noony.app.timelinefx.core.IDateObject;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.Place;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.PlaceLevel;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.StayPeriodLocalDate;
import com.github.noony.app.timelinefx.core.StayPeriodSimpleTime;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import com.github.noony.app.timelinefx.core.TimeLineProjectFactory;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import com.github.noony.app.timelinefx.core.freemap.connectors.PlotType;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapSimpleLink;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapStayLink;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapTravelLink;
import com.github.noony.app.timelinefx.save.TimelineProjectProvider;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TimeProjectProviderV1 implements TimelineProjectProvider {

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
    //
    public static final String PLACE_ELEMENT = "place";
    public static final String PLACE_REF_ELEMENT = "placeRef";
    public static final String PERSON_ELEMENT = "person";
    public static final String PERSON_REF_ELEMENT = "personRef";
    public static final String PICTURE_ELEMENT = "picture";
    public static final String FRIEZE_ELEMENT = "frieze";
    public static final String STAY_ELEMENT = "stay";
    public static final String STAY_ELEMENT_REF = "stayRef";
    public static final String FREEMAP_ELEMENT = "freeMap";
    public static final String PORTRAIT_ELEMENT = "portrait";
    public static final String PLOT_ELEMENT = "plot";
    public static final String LINK_ELEMENT = "link";
    public static final String FREEMAP_PLACE_ELEMENT = "freeMapPlace";
    //
    public static final String PICTURES_LOCATION_ATR = "picsLoc";
    //
    public static final String ID_ATR = "id";
    public static final String NAME_ATR = "name";
    public static final String TYPE_ATR = "type";
    public static final String PATH_ATR = "path";
    public static final String DATE_ATR = "date";
    public static final String PLACE_LEVEL_ATR = "level";
    public static final String COLOR_ATR = "color";
    public static final String PERSON_ATR = "person";
    public static final String PICTURE_ATR = "picture";
    public static final String START_DATE_ATR = "startDate";
    public static final String END_DATE_ATR = "endDate";
    public static final String TIME_FORMAT_ATR = "timeFormat";
    public static final String STAY_ID_ATR = "stayID";
    public static final String START_ID_ATR = "startID";
    public static final String END_ID_ATR = "endID";
    public static final String PLACE_ID_ATR = "placeID";
    //
    public static final String WIDTH_ATR = "width";
    public static final String HEIGHT_ATR = "height";
    public static final String X_POS_ATR = "xPos";
    public static final String Y_POS_ATR = "yPos";
    public static final String RADIUS_ATR = "radius";
    public static final String FREEMAP_PERSON_WIDTH_ATR = "personWidth";
    public static final String FREEMAP_PLACE_NAME_WIDTH_ATR = "placeNameWidth";
    public static final String FREEMAP_FONT_SIZE_ATR = "fontSize";
    public static final String FREEMAP_PLOT_SEPARATION_ATR = "plotSeparation";
    public static final String FREEMAP_PLOT_VISIBILITY_ATR = "plotVisibility";
    public static final String FREEMAP_PLOT_SIZE_ATR = "plotSize";

    private static final Logger LOG = Logger.getGlobal();

    @Override
    public List<String> getSupportedVersions() {
        return Arrays.asList("1");
    }

    @Override
    public TimeLineProject load(File projectFile, Element e) {
        String projectName = e.getAttribute(NAME_ATR);
        // This version does not support configuration properties
        TimeLineProject project = TimeLineProjectFactory.createProject(projectName, Collections.EMPTY_MAP);
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
                        List<Person> persons = parsePersons(element, project);
                        persons.forEach(p -> project.addPerson(p));
                    }
                    case PICTURES_GROUP ->
                        parsePictures(element, project);
                    case STAYS_GROUP -> {
                        List<StayPeriod> stays = parseStays(element);
                        stays.forEach(s -> project.addStay(s));
                    }
                    case FRIEZES_GROUP -> {
                        parseFriezes(project, element);
                    }
                    default ->
                        throw new UnsupportedOperationException("Unknown element :: " + element.getTagName());
                }
            }
        }
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

    private static List<Person> parsePersons(Element personsRootElement, TimeLineProject project) {
        List<Person> persons = new LinkedList<>();
        NodeList personElements = personsRootElement.getChildNodes();
        for (int i = 0; i < personElements.getLength(); i++) {
            if (personElements.item(i).getNodeName().equals(PERSON_ELEMENT)) {
                Element e = (Element) personElements.item(i);
                Person p = parsePerson(e, project);
                persons.add(p);
            }
        }
        return persons;
    }

    private static Person parsePerson(Element personElement, TimeLineProject project) {
        // <person color="0x7fffd4ff" id="1" name="Obi Wan Kenobi"/>
        Color color = Color.valueOf(personElement.getAttribute(COLOR_ATR));
        long id = Long.parseLong(personElement.getAttribute(ID_ATR));
        String name = personElement.getAttribute(NAME_ATR);
        String pictureName = personElement.getAttribute(PICTURE_ATR);
        Person person = PersonFactory.createPerson(project, id, name, color);
        LOG.log(Level.WARNING, "Portraits are not supported in this file format..");
//        person.setPictureName(pictureName);
        return person;
    }

    private static List<Picture> parsePictures(Element picturesRootElement, TimeLineProject project) {
        List<Picture> pictures = new LinkedList<>();
        NodeList picturesElements = picturesRootElement.getChildNodes();
        for (int i = 0; i < picturesElements.getLength(); i++) {
            if (picturesElements.item(i).getNodeName().equals(PICTURE_ELEMENT)) {
                Element e = (Element) picturesElements.item(i);
                Picture p = parsePicture(e, project);
                pictures.add(p);
            }
        }
        return pictures;
    }

    private static Picture parsePicture(Element pictureElement, TimeLineProject project) {
        var id = Long.parseLong(pictureElement.getAttribute(ID_ATR));
        var name = pictureElement.getAttribute(NAME_ATR);
        var path = pictureElement.getAttribute(PATH_ATR);
        var dateTime = LocalDateTime.parse(pictureElement.getAttribute(DATE_ATR), IDateObject.DEFAULT_DATE_TIME_FORMATTER);
        var width = Integer.parseInt(pictureElement.getAttribute(WIDTH_ATR));
        var height = Integer.parseInt(pictureElement.getAttribute(HEIGHT_ATR));
        //
        Picture picture = PictureFactory.createPicture(project, id, name, dateTime, path, width, height);
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
        double start = Double.parseDouble(stayElement.getAttribute(START_DATE_ATR));
        double end = Double.parseDouble(stayElement.getAttribute(END_DATE_ATR));
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
        FriezeFreeMap freeMap = frieze.createFriezeFreeMap();
        //
        NodeList portraitsGroups = freemapElement.getElementsByTagName(PORTRAITS_GROUP);
        if (portraitsGroups.getLength() < 1) {
            throw new IllegalStateException();
        }
        parsePortraits((Element) portraitsGroups.item(0), freeMap);
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

    }

    private void parsePortraits(Element plotsRootElement, FriezeFreeMap freeMap) {
        // <portrait person="21" xPos="690.0" yPos="179.8"/>
        NodeList plotElements = plotsRootElement.getChildNodes();
        for (int i = 0; i < plotElements.getLength(); i++) {
            if (plotElements.item(i).getNodeName().equals(PORTRAIT_ELEMENT)) {
                Element e = (Element) plotElements.item(i);
                long personID = Long.parseLong(e.getAttribute(PERSON_ATR));
                double xPos = Double.parseDouble(e.getAttribute(X_POS_ATR));
                double yPos = Double.parseDouble(e.getAttribute(Y_POS_ATR));
                double radius = Double.parseDouble(e.getAttribute(RADIUS_ATR));
//                FreeMapPortrait portrait = freeMap.getPortrait(personID);
                FreeMapPortrait portrait = null;
                System.err.println(" TODO parsePortraits");
                if (portrait == null) {
                    throw new IllegalStateException("Cannot find portrait with personID=" + personID);
                }
                portrait.setX(xPos);
                portrait.setY(yPos);
                portrait.setRadius(radius);
            }
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
                FreeMapPlot plot;
                PlotType type;
                // TODO use values
                if (typeS.equals(PlotType.START.name())) {
                    type = PlotType.START;
                } else if (typeS.equals(PlotType.END.name())) {
                    type = PlotType.END;
                } else {
                    throw new UnsupportedOperationException();
                }
                System.err.println(" save plot");
//                plot = freeMap.getPlot(stayID, type);
//                if (plot == null) {
//                    throw new IllegalStateException("Cannot find plot with stayID=" + stayID + " and of type " + typeS);
//                }
//                plot.setX(xPos);
//                plot.setY(yPos);
            }
        }
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
        Element personElement = doc.createElement(PERSON_ELEMENT);
        personElement.setAttribute(ID_ATR, Long.toString(person.getId()));
        personElement.setAttribute(NAME_ATR, person.getName());
//        personElement.setAttribute(PICTURE_ATR, person.getPictureName());
        LOG.log(Level.WARNING, "Portraits are not supported in this file format..");
        personElement.setAttribute(COLOR_ATR, person.getColor().toString());
        return personElement;
    }

    private static Element createPictureElement(Document doc, Picture picture) {
        Element pictureElement = doc.createElement(PICTURE_ELEMENT);
        pictureElement.setAttribute(ID_ATR, Long.toString(picture.getId()));
        pictureElement.setAttribute(NAME_ATR, picture.getName());
        pictureElement.setAttribute(PATH_ATR, picture.getProjectRelativePath());
        pictureElement.setAttribute(DATE_ATR, picture.getAbsoluteTimeAsString());
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
        return pictureElement;
    }

    private static Element createFriezeElement(Document doc, Frieze frieze) {
        LOG.log(Level.INFO, "Saving Frieze {0}", new Object[]{frieze.getName()});
        Element friezeElement = doc.createElement(FRIEZE_ELEMENT);
        friezeElement.setAttribute(NAME_ATR, frieze.getName());
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
        stayElement.setAttribute(START_DATE_ATR, Double.toString(stay.getStartDate()));
        stayElement.setAttribute(END_DATE_ATR, Double.toString(stay.getEndDate()));
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
        Element friezeFreeMapElement = doc.createElement(FREEMAP_ELEMENT);
        friezeFreeMapElement.setAttribute(NAME_ATR, friezeFreeMap.getName());
        friezeFreeMapElement.setAttribute(WIDTH_ATR, Double.toString(friezeFreeMap.getFreeMapWidth()));
        friezeFreeMapElement.setAttribute(HEIGHT_ATR, Double.toString(friezeFreeMap.getFreeMapHeight()));
        friezeFreeMapElement.setAttribute(FREEMAP_PERSON_WIDTH_ATR, Double.toString(friezeFreeMap.getPersonWidth()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR, Double.toString(friezeFreeMap.getPlaceNamesWidth()));
        friezeFreeMapElement.setAttribute(FREEMAP_FONT_SIZE_ATR, Double.toString(friezeFreeMap.getFontSize()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_SEPARATION_ATR, Double.toString(friezeFreeMap.getPlotSeparation()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_VISIBILITY_ATR, Boolean.toString(friezeFreeMap.getPlotVisibility()));
        friezeFreeMapElement.setAttribute(FREEMAP_PLOT_SIZE_ATR, Double.toString(friezeFreeMap.getPlotSize()));
        //
        Element portraitsGroupElement = doc.createElement(PORTRAITS_GROUP);
//        friezeFreeMap.getPortraits().forEach(portrait -> portraitsGroupElement.appendChild(createPortraitElement(doc, portrait)));
        System.err.println(" TODO createPortraitElement");
        friezeFreeMapElement.appendChild(portraitsGroupElement);
        //
        Element plotsGroupElement = doc.createElement(PLOTS_GROUP);
        System.err.println(" save plots");
//        friezeFreeMap.getPlots().forEach(plot -> plotsGroupElement.appendChild(createPlotElement(doc, plot)));
        friezeFreeMapElement.appendChild(plotsGroupElement);
        //
        Element placesGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
        friezeFreeMap.getPlaces().forEach(place -> placesGroupElement.appendChild(createFreeMapPlaceElement(doc, place)));
        friezeFreeMapElement.appendChild(placesGroupElement);
        //
        Element linksGroupElement = doc.createElement(LINKS_GROUP);
        System.err.println(" save links");
//        friezeFreeMap.getStayLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
//        friezeFreeMap.getTravelLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
        friezeFreeMapElement.appendChild(linksGroupElement);
        //
        return friezeFreeMapElement;
    }

    private static Element createPortraitElement(Document doc, FreeMapPortrait portrait) {
        Element portraitElement = doc.createElement(PORTRAIT_ELEMENT);
        portraitElement.setAttribute(PERSON_ATR, Long.toString(portrait.getPerson().getId()));
        portraitElement.setAttribute(X_POS_ATR, Double.toString(portrait.getX()));
        portraitElement.setAttribute(Y_POS_ATR, Double.toString(portrait.getY()));
        portraitElement.setAttribute(RADIUS_ATR, Double.toString(portrait.getRadius()));
        return portraitElement;
    }

    private static Element createPlotElement(Document doc, FreeMapPlot plot) {
        Element plotElement = doc.createElement(PLOT_ELEMENT);
        plotElement.setAttribute(TYPE_ATR, plot.getType().name());
        plotElement.setAttribute(STAY_ID_ATR, Long.toString(plot.getLinkedElementID()));
        plotElement.setAttribute(X_POS_ATR, Double.toString(plot.getX()));
        plotElement.setAttribute(Y_POS_ATR, Double.toString(plot.getY()));
        return plotElement;
    }

    private static Element createLinkElement(Document doc, FreeMapSimpleLink link) {
        Element linkElement = doc.createElement(LINK_ELEMENT);
        linkElement.setAttribute(TYPE_ATR, link.getType().name());
        linkElement.setAttribute(START_ID_ATR, Long.toString(link.getBeginConnector().getLinkedElementID()));
        linkElement.setAttribute(END_ID_ATR, Long.toString(link.getEndConnector().getLinkedElementID()));
        if (link instanceof FreeMapStayLink stayLink) {
            linkElement.setAttribute(END_ID_ATR, Long.toString(stayLink.getStayPeriod().getId()));
        } else if (link instanceof FreeMapTravelLink travelLink) {
            linkElement.setAttribute(END_ID_ATR, Long.toString(travelLink.getPerson().getId()));
        }
        linkElement.setAttribute(STAY_ID_ATR, Long.toString(link.getEndConnector().getLinkedElementID()));
        return linkElement;
    }

    private static Element createFreeMapPlaceElement(Document doc, FreeMapPlace freeMapPlace) {
        Element placeElement = doc.createElement(FREEMAP_PLACE_ELEMENT);
        placeElement.setAttribute(HEIGHT_ATR, Double.toString(freeMapPlace.getHeight()));
        placeElement.setAttribute(PLACE_ID_ATR, Long.toString(freeMapPlace.getPlace().getId()));
        placeElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPlace.getYPos()));
        return placeElement;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
