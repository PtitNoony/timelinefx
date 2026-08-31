/*
 * Copyright (C) 2023 NoOnY
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
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.PlaceFactory;
import com.github.noony.app.timelinefx.core.StayFactory;
import com.github.noony.app.timelinefx.core.freemap.FreeMapDateHandle;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortraitFactory;
import com.github.noony.app.timelinefx.core.freemap.FreeMapStay;
import com.github.noony.app.timelinefx.core.freemap.FreeMapStayFactory;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapFactory;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapProperties;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import com.github.noony.app.timelinefx.core.freemap.links.PortraitLink;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static com.github.noony.app.timelinefx.save.v4.TimeProjectProviderV4.*;

/**
 * Reads and writes {@link FriezeFreeMap} objects for save format version 4.
 *
 * @author hamon
 */
public class FreeMapProviderV4 {

    /**
     * XML group element name for the free maps section.
     */
    protected static final String FREEMAPS_GROUP = "freeMaps";

    /**
     * XML group element name for a free map's places.
     */
    private static final String FREEMAP_PLACES_GROUP = "freeMapPlaces";

    /**
     * XML group element name for a free map's persons.
     */
    private static final String FREEMAP_PERSONS_GROUP = "freeMapPersons";

    /**
     * XML group element name for a free map's date handles.
     */
    private static final String FREEMAP_DATE_HANDLES_GROUP = "freeMapDateHandles";

    /**
     * XML group element name for a person's portraits within a free map.
     */
    private static final String FREEMAP_PORTRAITS_GROUP = "freeMapPortraits";

    /**
     * XML group element name for a person's stays within a free map.
     */
    private static final String FREEMAP_STAYS_GROUP = "freeMapStays";

    /**
     * XML group element name for a free map's layout parameters.
     */
    private static final String FREEMAP_PARAMETERS_GROUP = "freeMapParameters";

    /**
     * XML element name for a single free map.
     */
    private static final String FREEMAP_ELEMENT = "freeMap";

    /**
     * XML element name for a single free map layout parameter.
     */
    private static final String PARAMETER_ELEMENT = "parameter";

    /**
     * XML attribute name for a parameter's name.
     */
    private static final String PARAMETER_NAME_ATR = "name";

    /**
     * XML attribute name for a parameter's value.
     */
    private static final String PARAMETER_VALUE_ATR = "value";

    /**
     * XML element name for a free map person.
     */
    private static final String FREEMAP_PERSON_ELEMENT = "freeMapPerson";

    /**
     * XML element name for a free map place.
     */
    private static final String FREEMAP_PLACE_ELEMENT = "freeMapPlace";

    /**
     * XML element name for a free map stay.
     */
    private static final String FREEMAP_STAY_ELEMENT = "freeMapStay";

    /**
     * XML element name for a free map date handle.
     */
    private static final String FREEMAP_DATE_HANDLE_ELEMENT = "freeMapDateHandle";

    /**
     * XML element name for a free map connector.
     */
    private static final String FREEMAP_CONNECTOR_ELEMENT = "connector";

    /**
     * XML element name for a portrait's link to its stay connector.
     */
    private static final String FREEMAP_PORTRAIT_LINK_ELEMENT = "portraitLink";

    /**
     * XML attribute name for a place's name width.
     */
    private static final String FREEMAP_PLACE_NAME_WIDTH_ATR = "placeNameWidth";

    /**
     * XML attribute name for a place's font size.
     */
    private static final String FREEMAP_FONT_SIZE_ATR = "fontSize";

    /**
     * XML attribute name for the id of the element a connector is linked to.
     */
    private static final String FREEMAP_LINKED_ELEMENT_ID_ATR = "linkedElementID";

    /**
     * XML attribute name for whether a free map stay is a merge of several stays.
     */
    private static final String FREEMAP_IS_MERGED_ATR = "isMerged";

    /**
     * Logger used by this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    private FreeMapProviderV4() {
        // private utility constructor
    }

    protected static Element saveFreeMapElement(final Document doc, final FriezeFreeMap friezeFreeMap) {
        LOG.log(Level.INFO, "Saving FriezeFreeMap: {0}", new Object[]{friezeFreeMap});
        //
        final var friezeFreeMapElement = doc.createElement(FREEMAP_ELEMENT);
        friezeFreeMapElement.setAttribute(NAME_ATR, friezeFreeMap.getName());
        friezeFreeMapElement.setAttribute(ID_ATR, Long.toString(friezeFreeMap.getId()));
        //
        final var configGroupElement = doc.createElement(FREEMAP_PARAMETERS_GROUP);
        friezeFreeMap.getParemeters().forEach((pName, pString) -> configGroupElement.appendChild(saveParameter(doc, pName, pString)));
        friezeFreeMapElement.appendChild(configGroupElement);
        // Places
        final var placeGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
        friezeFreeMap.getPlaces().forEach(p -> placeGroupElement.appendChild(saveFreeMapPlaceElement(doc, p)));
        friezeFreeMapElement.appendChild(placeGroupElement);
        // Person
        final var personsGroupElement = doc.createElement(FREEMAP_PERSONS_GROUP);
        friezeFreeMap.getPersons().forEach(p -> personsGroupElement.appendChild(saveFreeMapPersonElement(doc, p)));
        friezeFreeMapElement.appendChild(personsGroupElement);
        // Date handles
        final var dateHandlesGroupElement = doc.createElement(FREEMAP_DATE_HANDLES_GROUP);
        friezeFreeMap.getStartDateHandles().forEach(s -> dateHandlesGroupElement.appendChild(saveFreeMapDateHandleElement(doc, s)));
        friezeFreeMap.getEndDateHandles().forEach(e -> dateHandlesGroupElement.appendChild(saveFreeMapDateHandleElement(doc, e)));
        friezeFreeMapElement.appendChild(dateHandlesGroupElement);
        //
        return friezeFreeMapElement;
    }

    private static Element saveParameter(final Document doc, final String aName, final String aValueString) {
        final var paramElement = doc.createElement(PARAMETER_ELEMENT);
        paramElement.setAttribute(PARAMETER_NAME_ATR, aName);
        paramElement.setAttribute(PARAMETER_VALUE_ATR, aValueString);
        return paramElement;
    }

    private static Element saveFreeMapPersonElement(final Document doc, final FreeMapPerson freeMapPerson) {
        final var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
        personElement.setAttribute(ID_ATR, Long.toString(freeMapPerson.getId()));
        // stays
        final var staysGroupElement = doc.createElement(FREEMAP_STAYS_GROUP);
        personElement.appendChild(staysGroupElement);
        freeMapPerson.getFreeMapStays().forEach(fmStay -> {
            final var fmStayElement = saveFreeMapStayElement(doc, fmStay);
            staysGroupElement.appendChild(fmStayElement);
        });
        final var portraitsGroupElement = doc.createElement(FREEMAP_PORTRAITS_GROUP);
        personElement.appendChild(portraitsGroupElement);
        freeMapPerson.getFreeMapPortraits().forEach(fmPortrait -> {
            final var fmPortraitElement = saveFreeMapPortraitElement(doc, fmPortrait);
            portraitsGroupElement.appendChild(fmPortraitElement);
        });
        return personElement;
    }

    private static Element saveFreeMapPlaceElement(final Document doc, final FreeMapPlace freeMapPlace) {
        final var placeElement = doc.createElement(FREEMAP_PLACE_ELEMENT);
        placeElement.setAttribute(PLACE_ID_ATR, Long.toString(freeMapPlace.getPlace().getId()));
        placeElement.setAttribute(HEIGHT_ATR, Double.toString(freeMapPlace.getHeight()));
        placeElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPlace.getYPos()));
        placeElement.setAttribute(FREEMAP_FONT_SIZE_ATR, Double.toString(freeMapPlace.getFontSize()));
        placeElement.setAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR, Double.toString(freeMapPlace.getNameWidth()));
        final var personsAtPlace = freeMapPlace.getPersons();
        for (int i = 0; i < personsAtPlace.size(); i++) {
            final var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
            personElement.setAttribute(INDEX_ATR, Integer.toString(i));
            personElement.setAttribute(ID_ATR, Long.toString(personsAtPlace.get(i).getId()));
            placeElement.appendChild(personElement);
        }
        return placeElement;
    }

    private static Element saveFreeMapDateHandleElement(final Document doc, final FreeMapDateHandle freeMapDateHandle) {
        final var freeMapDateHandleElement = doc.createElement(FREEMAP_DATE_HANDLE_ELEMENT);
        freeMapDateHandleElement.setAttribute(DATE_ATR, Double.toString(freeMapDateHandle.getDate()));
        freeMapDateHandleElement.setAttribute(X_POS_ATR, Double.toString(freeMapDateHandle.getXPos()));
        freeMapDateHandleElement.setAttribute(Y_POS_ATR, Double.toString(freeMapDateHandle.getYPos()));
        // is a bit redundant but keeping it for now since save format is not frozen
        freeMapDateHandleElement.setAttribute(TYPE_ATR, freeMapDateHandle.getTimeType().name());
        return freeMapDateHandleElement;
    }

    private static Element saveFreeMapStayElement(final Document doc, final FreeMapStay freeMapStay) {
        // TODO, evaluate not saving stays inside persons but at freemap level ?
        final var stayElement = doc.createElement(FREEMAP_STAY_ELEMENT);
        stayElement.setAttribute(ID_ATR, Long.toString(freeMapStay.getId()));
        stayElement.setAttribute(START_ID_ATR, Long.toString(freeMapStay.getStartPlot().getId()));
        stayElement.setAttribute(END_ID_ATR, Long.toString(freeMapStay.getEndPlot().getId()));
        stayElement.setAttribute(PERSON_REF_ATR, Long.toString(freeMapStay.getPerson().getId()));
        stayElement.setAttribute(PLACE_REF_ATR, Long.toString(freeMapStay.getPlace().getId()));
        final var subStays = freeMapStay.getFreeMapStayPeriods();
        // made it more readable and easy to update
        final var allStaysIncluded = freeMapStay.getStayPeriods();
        if (subStays.isEmpty()) {
            stayElement.setAttribute(FREEMAP_IS_MERGED_ATR, Boolean.FALSE.toString());
            stayElement.setAttribute(STAY_ID_ATR, Long.toString(allStaysIncluded.get(0).getId()));

        } else {
            stayElement.setAttribute(FREEMAP_IS_MERGED_ATR, Boolean.TRUE.toString());
            subStays.forEach(subStay -> stayElement.appendChild(saveFreeMapStayElement(doc, subStay)));
            // TODO: improve, but will require to update stay management in merge stays
            allStaysIncluded.forEach(includedStay -> {
                final var includedStaylement = doc.createElement(STAY_ELEMENT_REF);
                includedStaylement.setAttribute(ID_ATR, Long.toString(includedStay.getId()));
                stayElement.appendChild(includedStaylement);
            });
        }
        //
        freeMapStay.getIntermediateConnectors().forEach(interConnector -> stayElement.appendChild(saveConnectorElement(doc, interConnector)));
        return stayElement;
    }

    private static Element saveFreeMapPortraitElement(final Document doc, final FreeMapPortrait freeMapPortrait) {
        final var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
        portraitElement.setAttribute(ID_ATR, Long.toString(freeMapPortrait.getId()));
        portraitElement.setAttribute(PERSON_ATR, Long.toString(freeMapPortrait.getPerson().getId()));
        portraitElement.setAttribute(X_POS_ATR, Double.toString(freeMapPortrait.getX()));
        portraitElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPortrait.getY()));
        portraitElement.setAttribute(RADIUS_ATR, Double.toString(freeMapPortrait.getRadius()));
        portraitElement.setAttribute(PORTRAIT_REF_ATR, Long.toString(freeMapPortrait.getPortrait().getId()));
        //
        final var portraitLink = freeMapPortrait.getPerson().getPortraitLink(freeMapPortrait);
        //
        final var portraitLinkElement = savePortraitLinkElement(doc, portraitLink);
        portraitElement.appendChild(portraitLinkElement);
        return portraitElement;
    }

    private static Element savePortraitLinkElement(final Document doc, final PortraitLink aPortraitLink) {
        final var portraitLinkElement = doc.createElement(FREEMAP_PORTRAIT_LINK_ELEMENT);
        portraitLinkElement.setAttribute(ID_ATR, Long.toString(aPortraitLink.getId()));
        //
        final var stayConnetorElement = saveConnectorElement(doc, aPortraitLink.getEndConnector());
        portraitLinkElement.appendChild(stayConnetorElement);

        return portraitLinkElement;
    }

    private static Element saveConnectorElement(final Document doc, final FreeMapConnector connector) {
        final var connectorElement = doc.createElement(FREEMAP_CONNECTOR_ELEMENT);
        connectorElement.setAttribute(ID_ATR, Long.toString(connector.getId()));
        connectorElement.setAttribute(FREEMAP_LINKED_ELEMENT_ID_ATR, Long.toString(connector.getLinkedElementID()));
        connectorElement.setAttribute(COLOR_ATR, connector.getColor().toString());
        connectorElement.setAttribute(DATE_ATR, Double.toString(connector.getDate()));
        connectorElement.setAttribute(X_POS_ATR, Double.toString(connector.getX()));
        connectorElement.setAttribute(Y_POS_ATR, Double.toString(connector.getY()));
        connectorElement.setAttribute(PLOT_SIZE_ATR, Double.toString(connector.getPlotSize()));
        return connectorElement;
    }

    //
    //
    //
    protected static void parseFreeMaps(final Element freemapsRootElement, final Frieze frieze) {
        final var freemapElements = freemapsRootElement.getChildNodes();
        for (int i = 0; i < freemapElements.getLength(); i++) {
            if (freemapElements.item(i).getNodeName().equals(FREEMAP_ELEMENT)) {
                final var e = (Element) freemapElements.item(i);
                parseFreeMap(e, frieze);
            }
        }
    }

    private static void parseFreeMap(final Element freemapElement, final Frieze frieze) {
        final var freeMapID = Long.parseLong(freemapElement.getAttribute(ID_ATR));
        final var freeMapName = freemapElement.getAttribute(NAME_ATR);
        // parse parameters
        final var freeMapParameters = parseFreeMapParameters(freemapElement);
        final var freeMapProperties = FriezeFreeMapProperties.fromParameterMap(freeMapParameters, FriezeFreeMap.DEFAULT_PROPERTIES);
        LOG.log(Level.INFO, "Parsed Freemap {0}_{1} parameters: {2}", new Object[]{freeMapID, freeMapName, freeMapProperties});
        // parse date handles
        final var dateHandles = parseFreeMapDateHandles(freemapElement, freeMapID);
        // parse FreeMapPersons, step 01: only creation and portrait loading (no stays)
        final var freeMapPersonsAndElements = parseFreeMapPersons(freemapElement, freeMapID);
        final var freeMapPersons = freeMapPersonsAndElements.stream().map(aPair -> aPair.getKey()).collect(Collectors.toList());
        final var freeMapPersonsById = freeMapPersons.stream().collect(Collectors.toMap(FreeMapPerson::getId, p -> p));
        // parse FreemapPlaces
        final var freeMapPlaces = parseFreeMapPlaces(freemapElement, freeMapID, freeMapProperties, freeMapPersonsById);
        final var freeMapPlacesById = freeMapPlaces.stream().collect(Collectors.toMap(FreeMapPlace::getId, p -> p));
        //
        // parse FreeMapStays
        final List<FreeMapStay> freeMapStays = new LinkedList<>();
        freeMapPersonsAndElements.forEach(
                pair -> freeMapStays.addAll(
                        parseFreeMapPersonStep02(pair.getValue(), pair.getKey(), freeMapPlacesById))
        );
        final var freeMapStaysById = freeMapStays.stream().collect(Collectors.toMap(FreeMapStay::getId, s -> s));
        // create FreeMap
        final var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(freeMapID, frieze, freeMapProperties, dateHandles, freeMapPersons, freeMapPlaces, freeMapStays);
        freeMap.setName(freeMapName);
        // create Portraits
        freeMapPersonsAndElements.forEach(pair -> parseFreeMapPersonStep03(pair.getValue(), pair.getKey(), freeMapStaysById));
        //
        frieze.addFriezeFreeMap(freeMap);
    }

    private static Map<String, String> parseFreeMapParameters(final Element freemapElement) {
        final Map<String, String> parameters = new HashMap<>();
        //
        final var freemapParametersElements = freemapElement.getElementsByTagName(PARAMETER_ELEMENT);
        for (int i = 0; i < freemapParametersElements.getLength(); i++) {
            final var paramElement = (Element) freemapParametersElements.item(i);
            final var paramName = paramElement.getAttribute(PARAMETER_NAME_ATR);
            final var paramValue = paramElement.getAttribute(PARAMETER_VALUE_ATR);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

    private static List<FreeMapDateHandle> parseFreeMapDateHandles(final Element freemapElement, final long parentFreeMapID) {
        final var freemapDateHandleGroupElement = firstDirectChildByTagName(freemapElement, FREEMAP_DATE_HANDLES_GROUP);
        if (freemapDateHandleGroupElement == null) {
            throw new IllegalStateException("Error while parsing FreeMapDateHandles: " + FREEMAP_DATE_HANDLES_GROUP + " count = 0");
        }

        final List<FreeMapDateHandle> freeMapDateHandles = new LinkedList<>();
        final var freemapDateHandlesElements = freemapDateHandleGroupElement.getElementsByTagName(FREEMAP_DATE_HANDLE_ELEMENT);
        for (int i = 0; i < freemapDateHandlesElements.getLength(); i++) {
            final var dateHandleElement = (Element) freemapDateHandlesElements.item(i);
            final var date = parseDoubleAttribute(dateHandleElement, DATE_ATR);
            final var xPos = parseDoubleAttribute(dateHandleElement, X_POS_ATR);
            final var yPos = parseDoubleAttribute(dateHandleElement, Y_POS_ATR);
            final var type = FreeMapDateHandle.TimeType.valueOf(dateHandleElement.getAttribute(TYPE_ATR));
            final var freemapDateHandle = FreeMapDateHandle.createFreeMapDateHandle(parentFreeMapID, date, type, new Point2D(xPos, yPos));
            freeMapDateHandles.add(freemapDateHandle);
        }
        return freeMapDateHandles;
    }

    private static List<Pair<FreeMapPerson, Element>> parseFreeMapPersons(final Element freemapElement, final long parentFreeMapID) {
        final var freemapPersonsElement = firstDirectChildByTagName(freemapElement, FREEMAP_PERSONS_GROUP);
        if (freemapPersonsElement == null) {
            throw new IllegalStateException("Error while parsing FreeMapPersons: " + FREEMAP_PERSONS_GROUP + " count = 0");
        }
        //
        final List<Pair<FreeMapPerson, Element>> freeMapPersons = new LinkedList<>();
        final var freemapPersonsElements = freemapPersonsElement.getElementsByTagName(FREEMAP_PERSON_ELEMENT);
        for (int i = 0; i < freemapPersonsElements.getLength(); i++) {
            final var personElement = (Element) freemapPersonsElements.item(i);
            final var freeMapPerson = parseFreeMapPersonStep01(personElement, parentFreeMapID);
            freeMapPersons.add(new Pair<>(freeMapPerson, personElement));
        }
        return freeMapPersons;
    }

    private static FreeMapPerson parseFreeMapPersonStep01(final Element freemapPersonElement, final long parentFreeMapID) {
        final var personID = Long.parseLong(freemapPersonElement.getAttribute(ID_ATR));
        // a FreeMapPerson shall always have the same id as the person it represents
        final var person = PersonFactory.getPerson(personID);
        final var freeMapPerson = FreeMapPerson.createFreeMapPerson(parentFreeMapID, person);
        LOG.log(Level.FINE, "Created {0}", new Object[]{freeMapPerson});
        return freeMapPerson;
    }

    private static List<FreeMapStay> parseFreeMapPersonStep02(final Element freemapPersonElement, final FreeMapPerson freeMapPerson, final Map<Long, FreeMapPlace> freeMapPlacesById) {
        // parse stays
        final var freemapStaysGroupElement = firstDirectChildByTagName(freemapPersonElement, FREEMAP_STAYS_GROUP);
        if (freemapStaysGroupElement == null) {
            throw new IllegalStateException("Error while parsing FreemapStays: " + FREEMAP_STAYS_GROUP + " count = 0");
        }
        //
        final List<FreeMapStay> stays = new LinkedList<>();
        final var freemapStaysElements = freemapStaysGroupElement.getElementsByTagName(FREEMAP_STAY_ELEMENT);
        for (int i = 0; i < freemapStaysElements.getLength(); i++) {
            final var freemapStayElement = (Element) freemapStaysElements.item(i);
            final var freemapStay = parseFreeMapStay(freemapStayElement, freeMapPerson, freeMapPlacesById);
            stays.add(freemapStay);
        }
        return stays;
    }

    private static FreeMapStay parseFreeMapStay(final Element freemapStayElement, final FreeMapPerson freeMapPerson, final Map<Long, FreeMapPlace> freeMapPlacesById) {
        final var freeMapStayID = Long.parseLong(freemapStayElement.getAttribute(ID_ATR));
        final var isMerged = Boolean.parseBoolean(freemapStayElement.getAttribute(FREEMAP_IS_MERGED_ATR));
        if (isMerged) {
            throw new UnsupportedOperationException("TODO: handle merged freemap stays");
        }
        //
        final var startID = Long.parseLong(freemapStayElement.getAttribute(START_ID_ATR));
        final var endID = Long.parseLong(freemapStayElement.getAttribute(END_ID_ATR));
        final var personID = Long.parseLong(freemapStayElement.getAttribute(PERSON_REF_ATR));
        final var placeID = Long.parseLong(freemapStayElement.getAttribute(PLACE_REF_ATR));
        //
        if (freeMapPerson.getId() != personID) {
            throw new IllegalStateException("Could not parse FreeMapStay: could not find corresponding FreeMapPerson " + personID + " since was give " + freeMapPerson);
        }
        final var place = freeMapPlacesById.get(placeID);
        if (place == null) {
            throw new IllegalStateException("Could not parse FreeMapStay: could not find corresponding FreeMapPlace " + placeID);
        }
        // this is only the case since I do not handle yet merged stays
        final var stayPeriodID = Long.parseLong(freemapStayElement.getAttribute(STAY_ID_ATR));
        final var stayPeriod = StayFactory.getStay(stayPeriodID);
        if (stayPeriod == null) {
            /*
            var allStaysIncluded = freeMapStay.getStayPeriods();
            allStaysIncluded.forEach(includedStay -> {
            final var includedStaylement = doc.createElement(STAY_ELEMENT_REF);
                includedStaylement.setAttribute(ID_ATR, Long.toString(includedStay.getId()));
                stayElement.appendChild(includedStaylement);
            });
             */

            throw new IllegalStateException("Could not parse FreeMapStay: could not find corresponding StayPeriod " + stayPeriodID);
        }
        final var freeMapStay = FreeMapStayFactory.createFreeMapStay(freeMapStayID, stayPeriod, startID, endID, freeMapPerson, place);
        //
        return freeMapStay;
    }

    private static void parseFreeMapPersonStep03(final Element freemapPersonElement, final FreeMapPerson freeMapPerson, final Map<Long, FreeMapStay> freeMapStaysById) {
        // parse portrais
        final var freemapPortraitsGroupElement = firstDirectChildByTagName(freemapPersonElement, FREEMAP_PORTRAITS_GROUP);
        if (freemapPortraitsGroupElement == null) {
            throw new IllegalStateException("Error while parsing FreemapPortraits: " + FREEMAP_PORTRAITS_GROUP + " count = 0");
        }
        //
        final var freemapPortraitsElements = freemapPortraitsGroupElement.getElementsByTagName(PORTRAIT_ELEMENT);
        for (int i = 0; i < freemapPortraitsElements.getLength(); i++) {
            final var freemapPortraitElement = (Element) freemapPortraitsElements.item(i);
            final var freemapPortrait = parseFreeMapPortrait(freemapPortraitElement, freeMapPerson, freeMapStaysById);
            LOG.log(Level.FINE, "Created FreeMapPortrait: {0}", new Object[]{freemapPortrait});
        }
    }

    private static FreeMapPortrait parseFreeMapPortrait(final Element freemapPortraitElement, final FreeMapPerson freeMapPerson, final Map<Long, FreeMapStay> freeMapStaysById) {
        final var freeMapPortraitID = Long.parseLong(freemapPortraitElement.getAttribute(ID_ATR));
        final var personID = Long.parseLong(freemapPortraitElement.getAttribute(PERSON_ATR));
        final var portraitRef = Long.parseLong(freemapPortraitElement.getAttribute(PORTRAIT_REF_ATR));
        final var xPos = parseDoubleAttribute(freemapPortraitElement, X_POS_ATR);
        final var yPos = parseDoubleAttribute(freemapPortraitElement, Y_POS_ATR);
        final var radius = parseDoubleAttribute(freemapPortraitElement, RADIUS_ATR);
        //
        final var person = PersonFactory.getPerson(personID);
        final var portrait = person.getPortrait(portraitRef);
        //
        final var freemapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(freeMapPortraitID, portrait, freeMapPerson, radius);
        //
        final var aPortraitLink = parsePortraitLink(freemapPortraitElement, freemapPortrait, freeMapStaysById);
        //
        freeMapPerson.addFreeMapPortrait(freemapPortrait, aPortraitLink);
        //
        freemapPortrait.setRadius(radius);
        freemapPortrait.setX(xPos);
        freemapPortrait.setY(yPos);
        //
        return freemapPortrait;
    }

    private static PortraitLink parsePortraitLink(final Element freeMapPortraitElement, final FreeMapPortrait aFreeMapPortrait, final Map<Long, FreeMapStay> freeMapStaysById) {
        final var portraitLinkElement = firstDirectChildByTagName(freeMapPortraitElement, FREEMAP_PORTRAIT_LINK_ELEMENT);
        if (portraitLinkElement == null) {
            throw new IllegalStateException("Not 1 " + FREEMAP_PORTRAIT_LINK_ELEMENT + " in freeMapPortraitElement but 0.");
        }
        final var anID = Long.parseLong(portraitLinkElement.getAttribute(ID_ATR));
        //
        //
        final var stayConnectorElement = firstDirectChildByTagName(portraitLinkElement, FREEMAP_CONNECTOR_ELEMENT);
        if (stayConnectorElement == null) {
            throw new IllegalStateException("Not 1 " + FREEMAP_CONNECTOR_ELEMENT + " in freeMapPortraitElement but 0.");
        }
        final var stayConnector = parseConnectorElement(stayConnectorElement, freeMapStaysById);
        //
        final var portraitLink = FreeMapLinkFactory.createPortraitLink(anID, aFreeMapPortrait, stayConnector);
        LOG.log(Level.INFO, "Created protrait link: {0}.", new Object[]{portraitLink});
        return portraitLink;
    }

    private static List<FreeMapPlace> parseFreeMapPlaces(final Element freemapElement, final long parentFreeMapID, final FriezeFreeMapProperties freeMapProperties, final Map<Long, FreeMapPerson> freeMapPersonsById) {
        final var freemapPlacesElement = firstDirectChildByTagName(freemapElement, FREEMAP_PLACES_GROUP);
        if (freemapPlacesElement == null) {
            throw new IllegalStateException("Error while parsing FreeMapPlaces: " + FREEMAP_PERSONS_GROUP + " count = 0");
        }
        //
        final List<FreeMapPlace> freeMapPlaces = new LinkedList<>();
        final var freemapPlacesElements = freemapPlacesElement.getElementsByTagName(FREEMAP_PLACE_ELEMENT);
        for (int i = 0; i < freemapPlacesElements.getLength(); i++) {
            final var placeElement = (Element) freemapPlacesElements.item(i);
            final var freeMapPlace = parseFreeMapPlace(placeElement, parentFreeMapID, freeMapProperties, freeMapPersonsById);
            freeMapPlaces.add(freeMapPlace);
        }
        return freeMapPlaces;
    }

    private static FreeMapPlace parseFreeMapPlace(final Element freemapPlaceElement, final long parentFreeMapID, final FriezeFreeMapProperties freeMapProperties, final Map<Long, FreeMapPerson> freeMapPersonsById) {
        final var placeID = Long.parseLong(freemapPlaceElement.getAttribute(PLACE_ID_ATR));
        final var height = parseDoubleAttribute(freemapPlaceElement, HEIGHT_ATR);
        final var yPos = parseDoubleAttribute(freemapPlaceElement, Y_POS_ATR);
        final var fontSize = parseDoubleAttribute(freemapPlaceElement, FREEMAP_FONT_SIZE_ATR);
        final var nameWidth = parseDoubleAttribute(freemapPlaceElement, FREEMAP_PLACE_NAME_WIDTH_ATR);
        //
        final var place = PlaceFactory.getPlace(placeID);
        //
        final var freeMapPlace = FreeMapPlace.createFreeMapPlace(parentFreeMapID, place, freeMapProperties.plotSeparation(), nameWidth, fontSize);
        //
        freeMapPlace.setHeight(height);
        freeMapPlace.setY(yPos);
        //
        final var freemapPersonsElements = freemapPlaceElement.getElementsByTagName(FREEMAP_PERSON_ELEMENT);
        final FreeMapPerson[] sortedPersonsInPlace = new FreeMapPerson[freemapPersonsElements.getLength()];
        for (int i = 0; i < freemapPersonsElements.getLength(); i++) {
            final var personElement = (Element) freemapPersonsElements.item(i);
            final var personId = Long.parseLong(personElement.getAttribute(ID_ATR));
            final var personIndex = Integer.parseInt(personElement.getAttribute(INDEX_ATR));
            final var person = freeMapPersonsById.get(personId);
            sortedPersonsInPlace[personIndex] = person;
        }
        freeMapPlace.setPersonOrder(sortedPersonsInPlace);
        return freeMapPlace;
    }

    private static FreeMapConnector parseConnectorElement(final Element freeMapConnectorElement, final Map<Long, FreeMapStay> freeMapStaysById) {
        final var connectorID = Long.parseLong(freeMapConnectorElement.getAttribute(ID_ATR));
        final var date = parseDoubleAttribute(freeMapConnectorElement, DATE_ATR);
        final var colorS = freeMapConnectorElement.getAttribute(COLOR_ATR);
        final var xPos = parseDoubleAttribute(freeMapConnectorElement, X_POS_ATR);
        final var yPos = parseDoubleAttribute(freeMapConnectorElement, Y_POS_ATR);
        LOG.log(Level.FINE, "parseConnectorElement ignoring field color: {0}.", new Object[]{colorS});
        LOG.log(Level.FINE, "parseConnectorElement ignoring field xPos: {0}.", new Object[]{xPos});
        LOG.log(Level.FINE, "parseConnectorElement ignoring field yPos: {0}.", new Object[]{yPos});
        final var plotSize = parseDoubleAttribute(freeMapConnectorElement, PLOT_SIZE_ATR);
        final var linkedElementID = Long.parseLong(freeMapConnectorElement.getAttribute(FREEMAP_LINKED_ELEMENT_ID_ATR));
        //
        final var stayLink = freeMapStaysById.get(linkedElementID);
        if (stayLink == null) {
            throw new IllegalStateException("Could not find stay with id=" + linkedElementID + "for connectorID=" + connectorID);
        }
        final var stayLinkConnector = FreeMapConnectorFactory.createFreeMapLinkConnector(connectorID, stayLink, date, plotSize);
        LOG.log(Level.INFO, "Created freemapConnector {0}.", new Object[]{stayLinkConnector});
        //
        return stayLinkConnector;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
