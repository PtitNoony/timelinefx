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
package com.github.noony.app.timelinefx.save.v3;

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
import static com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap.getPlotSeparationParamerter;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnectorFactory;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapLinkFactory;
import com.github.noony.app.timelinefx.core.freemap.links.PortraitLink;
import static com.github.noony.app.timelinefx.save.v3.TimeProjectProviderV3.*;
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

/**
 *
 * @author hamon
 */
public class FreeMapProviderV3 {

    protected static final String FREEMAPS_GROUP = "freeMaps";
    private static final String FREEMAP_PLACES_GROUP = "freeMapPlaces";
    private static final String FREEMAP_PERSONS_GROUP = "freeMapPersons";
    private static final String FREEMAP_DATE_HANDLES_GROUP = "freeMapDateHandles";
    private static final String FREEMAP_PORTRAITS_GROUP = "freeMapPortraits";
    private static final String FREEMAP_STAYS_GROUP = "freeMapStays";
    private static final String FREEMAP_PARAMETERS_GROUP = "freeMapParameters";
//    private static final String PLOTS_GROUP = "plots";
//    private static final String LINKS_GROUP = "links";
    private static final String FREEMAP_ELEMENT = "freeMap";
    private static final String PARAMETER_ELEMENT = "parameter";
    private static final String PARAMETER_NAME_ATR = "name";
    private static final String PARAMETER_VALUE_ATR = "value";
    private static final String FREEMAP_PERSON_ELEMENT = "freeMapPerson";
    private static final String FREEMAP_PLACE_ELEMENT = "freeMapPlace";
    private static final String FREEMAP_STAY_ELEMENT = "freeMapStay";
    private static final String FREEMAP_DATE_HANDLE_ELEMENT = "freeMapDateHandle";
    private static final String FREEMAP_CONNECTOR_ELEMENT = "connector";
    private static final String FREEMAP_PORTRAIT_LINK_ELEMENT = "portraitLink";
    private static final String PLOT_ELEMENT = "plot";
    private static final String LINK_ELEMENT = "link";
    //
//    private static final String FREEMAP_PERSON_WIDTH_ATR = "personWidth";
    private static final String FREEMAP_PLACE_NAME_WIDTH_ATR = "placeNameWidth";
    private static final String FREEMAP_FONT_SIZE_ATR = "fontSize";
//    private static final String FREEMAP_PLOT_SEPARATION_ATR = "plotSeparation";
//    private static final String FREEMAP_PLOT_VISIBILITY_ATR = "plotVisibility";
//    private static final String FREEMAP_PLOT_SIZE_ATR = "plotSize";
    private static final String FREEMAP_LINKED_ELEMENT_ID_ATR = "linkedElementID";
    private static final String FREEMAP_IS_MERGED_ATR = "isMerged";
    //
//    private static final String FREEMAP_STAY_TYPE_ATTRIBUTE = "freeMapStayType";
//    private static final String FREEMAP_STAY_TYPE_SIMPLE_VALIE = "simple";
//    private static final String FREEMAP_STAY_TYPE_COMPLEX_VALUE = "complex";

    private static final Logger LOG = Logger.getGlobal();

    protected static Element saveFreeMapElement(Document doc, FriezeFreeMap friezeFreeMap) {
        LOG.log(Level.INFO, "Saving FriezeFreeMap: {0}", new Object[]{friezeFreeMap});
        //
        var friezeFreeMapElement = doc.createElement(FREEMAP_ELEMENT);
        friezeFreeMapElement.setAttribute(NAME_ATR, friezeFreeMap.getName());
        friezeFreeMapElement.setAttribute(ID_ATR, Long.toString(friezeFreeMap.getId()));
        //
        var configGroupElement = doc.createElement(FREEMAP_PARAMETERS_GROUP);
        friezeFreeMap.getParemeters().forEach((pName, pString) -> configGroupElement.appendChild(saveParameter(doc, pName, pString)));
        friezeFreeMapElement.appendChild(configGroupElement);
        // Places
        var placeGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
        friezeFreeMap.getPlaces().forEach(p -> placeGroupElement.appendChild(saveFreeMapPlaceElement(doc, p)));
        friezeFreeMapElement.appendChild(placeGroupElement);
        // Person
        var personsGroupElement = doc.createElement(FREEMAP_PERSONS_GROUP);
        friezeFreeMap.getPersons().forEach(p -> personsGroupElement.appendChild(saveFreeMapPersonElement(doc, p)));
        friezeFreeMapElement.appendChild(personsGroupElement);
        // Date handles
        var dateHandlesGroupElement = doc.createElement(FREEMAP_DATE_HANDLES_GROUP);
        friezeFreeMap.getStartDateHandles().forEach(s -> dateHandlesGroupElement.appendChild(saveFreeMapDateHandleElement(doc, s)));
        friezeFreeMap.getEndDateHandles().forEach(e -> dateHandlesGroupElement.appendChild(saveFreeMapDateHandleElement(doc, e)));
        friezeFreeMapElement.appendChild(dateHandlesGroupElement);
        //
        return friezeFreeMapElement;
    }

    private static Element saveParameter(Document doc, String aName, String aValueString) {
        var paramElement = doc.createElement(PARAMETER_ELEMENT);
        paramElement.setAttribute(PARAMETER_NAME_ATR, aName);
        paramElement.setAttribute(PARAMETER_VALUE_ATR, aValueString);
        return paramElement;
    }

    private static Element saveFreeMapPersonElement(Document doc, FreeMapPerson freeMapPerson) {
        var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
        personElement.setAttribute(ID_ATR, Long.toString(freeMapPerson.getId()));
        // stays
        var staysGroupElement = doc.createElement(FREEMAP_STAYS_GROUP);
        personElement.appendChild(staysGroupElement);
        freeMapPerson.getFreeMapStays().forEach(fmStay -> {
            final var fmStayElement = saveFreeMapStayElement(doc, fmStay);
            staysGroupElement.appendChild(fmStayElement);
        });
        var portraitsGroupElement = doc.createElement(FREEMAP_PORTRAITS_GROUP);
        personElement.appendChild(portraitsGroupElement);
        freeMapPerson.getFreeMapPortraits().forEach(fmPortrait -> {
            var fmPortraitElement = saveFreeMapPortraitElement(doc, fmPortrait);
            portraitsGroupElement.appendChild(fmPortraitElement);
        });
        return personElement;
    }

    private static Element saveFreeMapPlaceElement(Document doc, FreeMapPlace freeMapPlace) {
        var placeElement = doc.createElement(FREEMAP_PLACE_ELEMENT);
        placeElement.setAttribute(PLACE_ID_ATR, Long.toString(freeMapPlace.getPlace().getId()));
        placeElement.setAttribute(HEIGHT_ATR, Double.toString(freeMapPlace.getHeight()));
        placeElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPlace.getYPos()));
        placeElement.setAttribute(FREEMAP_FONT_SIZE_ATR, Double.toString(freeMapPlace.getFontSize()));
        placeElement.setAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR, Double.toString(freeMapPlace.getNameWidth()));
        var personsAtPlace = freeMapPlace.getPersons();
        for (int i = 0; i < personsAtPlace.size(); i++) {
            final var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
            personElement.setAttribute(INDEX_ATR, Integer.toString(i));
            personElement.setAttribute(ID_ATR, Long.toString(personsAtPlace.get(i).getId()));
            placeElement.appendChild(personElement);
        }
        return placeElement;
    }

    private static Element saveFreeMapDateHandleElement(Document doc, FreeMapDateHandle freeMapDateHandle) {
        var freeMapDateHandleElement = doc.createElement(FREEMAP_DATE_HANDLE_ELEMENT);
        freeMapDateHandleElement.setAttribute(DATE_ATR, Double.toString(freeMapDateHandle.getDate()));
        freeMapDateHandleElement.setAttribute(X_POS_ATR, Double.toString(freeMapDateHandle.getXPos()));
        freeMapDateHandleElement.setAttribute(Y_POS_ATR, Double.toString(freeMapDateHandle.getYPos()));
        // is a bit redundant but keeping it for now since save format is not frozen
        freeMapDateHandleElement.setAttribute(TYPE_ATR, freeMapDateHandle.getTimeType().name());
        return freeMapDateHandleElement;
    }

    private static Element saveFreeMapStayElement(Document doc, FreeMapStay freeMapStay) {
        // TODO, evaluate not saving stays inside persons but at freemap level ?
        final var stayElement = doc.createElement(FREEMAP_STAY_ELEMENT);
        stayElement.setAttribute(ID_ATR, Long.toString(freeMapStay.getId()));
        stayElement.setAttribute(START_ID_ATR, Long.toString(freeMapStay.getStartPlot().getId()));
        stayElement.setAttribute(END_ID_ATR, Long.toString(freeMapStay.getEndPlot().getId()));
        stayElement.setAttribute(PERSON_REF_ATR, Long.toString(freeMapStay.getPerson().getId()));
        stayElement.setAttribute(PLACE_REF_ATR, Long.toString(freeMapStay.getPlace().getId()));
        var subStays = freeMapStay.getFreeMapStayPeriods();
        // made it more readable and easy to update
        var allStaysIncluded = freeMapStay.getStayPeriods();
        if (subStays.isEmpty()) {
            stayElement.setAttribute(FREEMAP_IS_MERGED_ATR, Boolean.FALSE.toString());
            stayElement.setAttribute(STAY_ID_ATR, Long.toString(allStaysIncluded.getFirst().getId()));

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

    private static Element saveFreeMapPortraitElement(Document doc, FreeMapPortrait freeMapPortrait) {
        var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
        portraitElement.setAttribute(ID_ATR, Long.toString(freeMapPortrait.getId()));
        portraitElement.setAttribute(PERSON_ATR, Long.toString(freeMapPortrait.getPerson().getId()));
        portraitElement.setAttribute(X_POS_ATR, Double.toString(freeMapPortrait.getX()));
        portraitElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPortrait.getY()));
        portraitElement.setAttribute(RADIUS_ATR, Double.toString(freeMapPortrait.getRadius()));
        portraitElement.setAttribute(PORTRAIT_REF_ATR, Long.toString(freeMapPortrait.getPortrait().getId()));
        //
        var portraitLink = freeMapPortrait.getPerson().getPortraitLink(freeMapPortrait);
        //
        var portraitLinkElement = savePortraitLinkElement(doc, portraitLink);
        portraitElement.appendChild(portraitLinkElement);
        return portraitElement;
    }

    private static Element savePortraitLinkElement(Document doc, PortraitLink aPortraitLink) {
        var portraitLinkElement = doc.createElement(FREEMAP_PORTRAIT_LINK_ELEMENT);
        portraitLinkElement.setAttribute(ID_ATR, Long.toString(aPortraitLink.getId()));
        //
        var stayConnetorElement = saveConnectorElement(doc, aPortraitLink.getEndConnector());
        portraitLinkElement.appendChild(stayConnetorElement);

        return portraitLinkElement;
    }

    private static Element saveConnectorElement(Document doc, FreeMapConnector connector) {
        var connectorElement = doc.createElement(FREEMAP_CONNECTOR_ELEMENT);
        connectorElement.setAttribute(ID_ATR, Long.toString(connector.getId()));
        connectorElement.setAttribute(FREEMAP_LINKED_ELEMENT_ID_ATR, Long.toString(connector.getLinkedElementID()));
        connectorElement.setAttribute(COLOR_ATR, connector.getColor().toString());
        connectorElement.setAttribute(DATE_ATR, Double.toString(connector.getDate()));
        // doublon
//        connectorElement.setAttribute(LINKED_OBJECT_ID_ATR, Long.toString(connector.getLinkedElementID()));
        connectorElement.setAttribute(X_POS_ATR, Double.toString(connector.getX()));
        connectorElement.setAttribute(Y_POS_ATR, Double.toString(connector.getY()));
        connectorElement.setAttribute(PLOT_SIZE_ATR, Double.toString(connector.getPlotSize()));
        return connectorElement;
    }

    ///
    ///
    ///
    ////
//    private static Element createPlotElement(Document doc, FreeMapPlot plot) {
//        var plotElement = doc.createElement(PLOT_ELEMENT);
//        plotElement.setAttribute(TYPE_ATR, plot.getType().name());
//        plotElement.setAttribute(STAY_ID_ATR, Long.toString(plot.getLinkedElementID()));
//        plotElement.setAttribute(X_POS_ATR, Double.toString(plot.getX()));
//        plotElement.setAttribute(Y_POS_ATR, Double.toString(plot.getY()));
//        return plotElement;
//    }
//    private static Element createLinkElement(Document doc, FreeMapSimpleLink link) {
//        var linkElement = doc.createElement(LINK_ELEMENT);
//        linkElement.setAttribute(TYPE_ATR, link.getType().name());
//        linkElement.setAttribute(START_ID_ATR, Long.toString(link.getBeginConnector().getLinkedElementID()));
//        linkElement.setAttribute(END_ID_ATR, Long.toString(link.getEndConnector().getLinkedElementID()));
//        if (link instanceof FreeMapStayLink stayLink) {
//            linkElement.setAttribute(END_ID_ATR, Long.toString(stayLink.getStayPeriod().getId()));
//        } else if (link instanceof FreeMapTravelLink travelLink) {
//            linkElement.setAttribute(END_ID_ATR, Long.toString(travelLink.getPerson().getId()));
//        }
//        linkElement.setAttribute(STAY_ID_ATR, Long.toString(link.getEndConnector().getLinkedElementID()));
//        return linkElement;
//    }
    //
    //
    //
    protected static void parseFreeMaps(Element freemapsRootElement, Frieze frieze) {
        var freemapElements = freemapsRootElement.getChildNodes();
        for (int i = 0; i < freemapElements.getLength(); i++) {
            if (freemapElements.item(i).getNodeName().equals(FREEMAP_ELEMENT)) {
                var e = (Element) freemapElements.item(i);
                parseFreeMap(e, frieze);
            }
        }
    }

    private static void parseFreeMap(Element freemapElement, Frieze frieze) {
        var freeMapID = Long.parseLong(freemapElement.getAttribute(ID_ATR));
        var freeMapName = freemapElement.getAttribute(NAME_ATR);
        // parse parameters
        var freeMapParameters = parseFreeMapParameters(freemapElement);
        LOG.log(Level.INFO, "Parsed Freemap {0}_{1} parameters: {2}", new Object[]{freeMapID, freeMapName, freeMapParameters});
        // parse date handles
        var dateHandles = parseFreeMapDateHandles(freemapElement, freeMapID);
        // parse FreeMapPersons, step 01: only creation and portrait loading (no stays)
        var freeMapPersonsAndElements = parseFreeMapPersons(freemapElement, freeMapID);
        var freeMapPersons = freeMapPersonsAndElements.stream().map(aPair -> aPair.getKey()).collect(Collectors.toList());
        // parse FreemapPlaces
        var freeMapPlaces = parseFreeMapPlaces(freemapElement, freeMapID, freeMapParameters, freeMapPersons);
        //
        // parse FreeMapStays
        List<FreeMapStay> freeMapStays = new LinkedList<>();
        freeMapPersonsAndElements.forEach(
                pair -> freeMapStays.addAll(
                        parseFreeMapPersonStep02(pair.getValue(), pair.getKey(), freeMapPlaces))
        );

        // create FreeMap
        var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(freeMapID, frieze, freeMapParameters, dateHandles, freeMapPersons, freeMapPlaces, freeMapStays);
        freeMap.setName(freeMapName);
        // create Portraits
        freeMapPersonsAndElements.forEach(pair -> parseFreeMapPersonStep03(pair.getValue(), pair.getKey(), freeMapPlaces, freeMapStays));

//        //
//        var freeMap = FriezeFreeMapFactory.createFriezeFreeMap(freeMapID, frieze, false);
//        if (freemapElement.hasAttribute(NAME_ATR)) {
//            freeMap.setName(freemapElement.getAttribute(NAME_ATR));
//        }
//        // !! IMPORTANT : set all the properties before updating plots, places...
//        //
//        NodeList portraitsGroups = freemapElement.getElementsByTagName(PORTRAITS_GROUP);
//        if (portraitsGroups.getLength() < 1) {
//            throw new IllegalStateException();
//        }
//        if (portraitsGroups.getLength() > 0) {
//            parsePortraits((Element) portraitsGroups.item(0), freeMap);
//        }
//        //
//        NodeList freemapPlacesGroups = freemapElement.getElementsByTagName(FREEMAP_PLACES_GROUP);
//        if (freemapPlacesGroups.getLength() < 1) {
//            throw new IllegalStateException();
//        }
//        parseFreemapPlaces((Element) freemapPlacesGroups.item(0), freeMap);
//        //
//        NodeList plotsGroups = freemapElement.getElementsByTagName(PLOTS_GROUP);
//        if (plotsGroups.getLength() < 1) {
//            throw new IllegalStateException();
//        }
//        parsePlots((Element) plotsGroups.item(0), frieze, freeMap);
//        //
        frieze.addFriezeFreeMap(freeMap);
    }

    private static Map<String, String> parseFreeMapParameters(Element freemapElement) {
        Map<String, String> parameters = new HashMap<>();
        //
        var freemapParametersElements = freemapElement.getElementsByTagName(PARAMETER_ELEMENT);
        for (int i = 0; i < freemapParametersElements.getLength(); i++) {
            var paramElement = (Element) freemapParametersElements.item(i);
            var paramName = paramElement.getAttribute(PARAMETER_NAME_ATR);
            var paramValue = paramElement.getAttribute(PARAMETER_VALUE_ATR);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

    private static List<FreeMapDateHandle> parseFreeMapDateHandles(Element freemapElement, long parentFreeMapID) {
        var freemapDateHandleGrouptList = freemapElement.getElementsByTagName(FREEMAP_DATE_HANDLES_GROUP);
        if (freemapDateHandleGrouptList.getLength() != 1) {
            throw new IllegalStateException("Error while parsing FreeMapDateHandles: " + FREEMAP_DATE_HANDLES_GROUP + " count = " + freemapDateHandleGrouptList.getLength());
        }
        var freemapDateHandleGroupElement = (Element) freemapDateHandleGrouptList.item(0);

        List<FreeMapDateHandle> freeMapDateHandles = new LinkedList<>();
        var freemapDateHandlesElements = freemapDateHandleGroupElement.getElementsByTagName(FREEMAP_DATE_HANDLE_ELEMENT);
        for (int i = 0; i < freemapDateHandlesElements.getLength(); i++) {
            var dateHandleElement = (Element) freemapDateHandlesElements.item(i);
            var date = Double.parseDouble(dateHandleElement.getAttribute(DATE_ATR));
            var xPos = Double.parseDouble(dateHandleElement.getAttribute(X_POS_ATR));
            var yPos = Double.parseDouble(dateHandleElement.getAttribute(Y_POS_ATR));
            var type = FreeMapDateHandle.TimeType.valueOf(dateHandleElement.getAttribute(TYPE_ATR));
            var freemapDateHandle = FreeMapDateHandle.createFreeMapDateHandle(parentFreeMapID, date, type, new Point2D(xPos, yPos));
            freeMapDateHandles.add(freemapDateHandle);
        }
        return freeMapDateHandles;
    }

    private static List<Pair<FreeMapPerson, Element>> parseFreeMapPersons(Element freemapElement, long parentFreeMapID) {
        var freemapPersonsElementList = freemapElement.getElementsByTagName(FREEMAP_PERSONS_GROUP);
        if (freemapPersonsElementList.getLength() != 1) {
            throw new IllegalStateException("Error while parsing FreeMapPersons: " + FREEMAP_PERSONS_GROUP + " count = " + freemapPersonsElementList.getLength());
        }
        var freemapPersonsElement = (Element) freemapPersonsElementList.item(0);
        //
        List<Pair<FreeMapPerson, Element>> freeMapPersons = new LinkedList<>();
        var freemapPersonsElements = freemapPersonsElement.getElementsByTagName(FREEMAP_PERSON_ELEMENT);
        for (int i = 0; i < freemapPersonsElements.getLength(); i++) {
            var personElement = (Element) freemapPersonsElements.item(i);
            var freeMapPerson = parseFreeMapPersonStep01(personElement, parentFreeMapID);
            freeMapPersons.add(new Pair(freeMapPerson, personElement));
        }
        return freeMapPersons;
    }

    private static FreeMapPerson parseFreeMapPersonStep01(Element freemapPersonElement, long parentFreeMapID) {
        var personID = Long.parseLong(freemapPersonElement.getAttribute(ID_ATR));
        // a FreeMapPerson shall always have the same id as the person it represents
        var person = PersonFactory.getPerson(personID);
        var freeMapPerson = FreeMapPerson.createFreeMapPerson(parentFreeMapID, person);
        LOG.log(Level.FINE, "Created {0}", new Object[]{freeMapPerson});
        return freeMapPerson;
    }

    private static List<FreeMapStay> parseFreeMapPersonStep02(Element freemapPersonElement, FreeMapPerson freeMapPerson, List<FreeMapPlace> freeMapPlaces) {
        // parse stays
        var freemapStaysGrouptList = freemapPersonElement.getElementsByTagName(FREEMAP_STAYS_GROUP);
        if (freemapStaysGrouptList.getLength() != 1) {
            throw new IllegalStateException("Error while parsing FreemapStays: " + FREEMAP_STAYS_GROUP + " count = " + freemapStaysGrouptList.getLength());
        }
        var freemapStaysGroupElement = (Element) freemapStaysGrouptList.item(0);
        //
        List<FreeMapStay> stays = new LinkedList<>();
        var freemapStaysElements = freemapStaysGroupElement.getElementsByTagName(FREEMAP_STAY_ELEMENT);
        for (int i = 0; i < freemapStaysElements.getLength(); i++) {
            var freemapStayElement = (Element) freemapStaysElements.item(i);
            var freemapStay = parseFreeMapStay(freemapStayElement, freeMapPerson, freeMapPlaces);
            stays.add(freemapStay);
        }
        return stays;
    }

    private static FreeMapStay parseFreeMapStay(Element freemapStayElement, FreeMapPerson freeMapPerson, List<FreeMapPlace> places) {
        var freeMapStayID = Long.parseLong(freemapStayElement.getAttribute(ID_ATR));
        var isMerged = Boolean.parseBoolean(freemapStayElement.getAttribute(FREEMAP_IS_MERGED_ATR));
        if (isMerged) {
            throw new UnsupportedOperationException("TODO: handle merged freemap stays");
        }
        //
        var startID = Long.parseLong(freemapStayElement.getAttribute(START_ID_ATR));
        var endID = Long.parseLong(freemapStayElement.getAttribute(END_ID_ATR));
        var personID = Long.parseLong(freemapStayElement.getAttribute(PERSON_REF_ATR));
        var placeID = Long.parseLong(freemapStayElement.getAttribute(PLACE_REF_ATR));
        //
        if (freeMapPerson.getId() != personID) {
            throw new IllegalStateException("Could not parse FreeMapStay: could not find corresponding FreeMapPerson " + personID + " since was give " + freeMapPerson);
        }
        var place = places.stream().filter(p -> p.getId() == placeID).findFirst().orElse(null);
        if (place == null) {
            throw new IllegalStateException("Could not parse FreeMapStay: could not find corresponding FreeMapPlace " + placeID);
        }
        // this is only the case since I do not handle yet merged stays
        var stayPeriodID = Long.parseLong(freemapStayElement.getAttribute(STAY_ID_ATR));
        var stayPeriod = StayFactory.getStay(stayPeriodID);
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
        var freeMapStay = FreeMapStayFactory.createFreeMapStay(freeMapStayID, stayPeriod, startID, endID, freeMapPerson, place);

        /*
       //
        freeMapStay.getIntermediateConnectors().forEach(interConnector -> stayElement.appendChild(saveConnectorElement(doc, interConnector)));

         */
        return freeMapStay;
    }

    private static void parseFreeMapPersonStep03(Element freemapPersonElement, FreeMapPerson freeMapPerson, List<FreeMapPlace> freeMapPlaces, List<FreeMapStay> links) {
        // parse portrais
        var freemapPortraitsGrouptList = freemapPersonElement.getElementsByTagName(FREEMAP_PORTRAITS_GROUP);
        if (freemapPortraitsGrouptList.getLength() != 1) {
            throw new IllegalStateException("Error while parsing FreemapPortraits: " + FREEMAP_PORTRAITS_GROUP + " count = " + freemapPortraitsGrouptList.getLength());
        }
        var freemapPortraitsGroupElement = (Element) freemapPortraitsGrouptList.item(0);
        //
        var freemapPortraitsElements = freemapPortraitsGroupElement.getElementsByTagName(PORTRAIT_ELEMENT);
        for (int i = 0; i < freemapPortraitsElements.getLength(); i++) {
            var freemapPortraitElement = (Element) freemapPortraitsElements.item(i);
            var freemapPortrait = parseFreeMapPortrait(freemapPortraitElement, freeMapPerson, links);
            LOG.log(Level.FINE, "Created FreeMapPortrait: {0}", new Object[]{freemapPortrait});
        }
    }

    private static FreeMapPortrait parseFreeMapPortrait(Element freemapPortraitElement, FreeMapPerson freeMapPerson, List<FreeMapStay> links) {
        var freeMapPortraitID = Long.parseLong(freemapPortraitElement.getAttribute(ID_ATR));
        var personID = Long.parseLong(freemapPortraitElement.getAttribute(PERSON_ATR));
        var portraitRef = Long.parseLong(freemapPortraitElement.getAttribute(PORTRAIT_REF_ATR));
        var xPos = Double.parseDouble(freemapPortraitElement.getAttribute(X_POS_ATR));
        var yPos = Double.parseDouble(freemapPortraitElement.getAttribute(Y_POS_ATR));
        var radius = Double.parseDouble(freemapPortraitElement.getAttribute(RADIUS_ATR));
        //
        var person = PersonFactory.getPerson(personID);
        var portrait = person.getPortrait(portraitRef);
        //
        var freemapPortrait = FreeMapPortraitFactory.createFreeMapPortrait(freeMapPortraitID, portrait, freeMapPerson, radius);
        //
        var aPortraitLink = parsePortraitLink(freemapPortraitElement, freemapPortrait, links);
        //
        freeMapPerson.addFreeMapPortrait(freemapPortrait, aPortraitLink);
        //
        freemapPortrait.setRadius(radius);
        freemapPortrait.setX(xPos);
        freemapPortrait.setY(yPos);
        //
        return freemapPortrait;
    }

    private static PortraitLink parsePortraitLink(Element freeMapPortraitElement, FreeMapPortrait aFreeMapPortrait, List<FreeMapStay> links) {
        var portraitLinkElements = freeMapPortraitElement.getElementsByTagName(FREEMAP_PORTRAIT_LINK_ELEMENT);
        if (portraitLinkElements.getLength() != 1) {
            throw new IllegalStateException("Not 1 " + FREEMAP_PORTRAIT_LINK_ELEMENT + " in freeMapPortraitElement but" + portraitLinkElements.getLength() + ".");
        }
        var portraitLinkElement = (Element) portraitLinkElements.item(0);
        var anID = Long.parseLong(portraitLinkElement.getAttribute(ID_ATR));
        //
        //
        var connectorElements = freeMapPortraitElement.getElementsByTagName(FREEMAP_CONNECTOR_ELEMENT);
        if (connectorElements.getLength() != 1) {
            throw new IllegalStateException("Not 1 " + FREEMAP_CONNECTOR_ELEMENT + " in freeMapPortraitElement but" + connectorElements.getLength() + ".");
        }
        var stayConnectorElement = (Element) connectorElements.item(0);
        var stayConnector = parseConnectorElement(stayConnectorElement, links);
        //
        var portraitLink = FreeMapLinkFactory.createPortraitLink(anID, aFreeMapPortrait, stayConnector);
        LOG.log(Level.INFO, "Created protrait link: {0}.", new Object[]{portraitLink});
        return portraitLink;
    }

    private static List<FreeMapPlace> parseFreeMapPlaces(Element freemapElement, long parentFreeMapID, Map<String, String> freeMapParameters, List<FreeMapPerson> freeMapPersons) {
        var freemapPlacesElementList = freemapElement.getElementsByTagName(FREEMAP_PLACES_GROUP);
        if (freemapPlacesElementList.getLength() != 1) {
            throw new IllegalStateException("Error while parsing FreeMapPlaces: " + FREEMAP_PERSONS_GROUP + " count = " + freemapPlacesElementList.getLength());
        }
        var freemapPlacesElement = (Element) freemapPlacesElementList.item(0);
        //
        List<FreeMapPlace> freeMapPlaces = new LinkedList<>();
        var freemapPlacesElements = freemapPlacesElement.getElementsByTagName(FREEMAP_PLACE_ELEMENT);
        for (int i = 0; i < freemapPlacesElements.getLength(); i++) {
            var placeElement = (Element) freemapPlacesElements.item(i);
            var freeMapPlace = parseFreeMapPlace(placeElement, parentFreeMapID, freeMapParameters, freeMapPersons);
            freeMapPlaces.add(freeMapPlace);
        }
        return freeMapPlaces;
    }

    private static FreeMapPlace parseFreeMapPlace(Element freemapPlaceElement, long parentFreeMapID, Map<String, String> freeMapParameters, List<FreeMapPerson> freeMapPersons) {
        var placeID = Long.parseLong(freemapPlaceElement.getAttribute(PLACE_ID_ATR));
        var height = Double.parseDouble(freemapPlaceElement.getAttribute(HEIGHT_ATR));
        var yPos = Double.parseDouble(freemapPlaceElement.getAttribute(Y_POS_ATR));
        var fontSize = Double.parseDouble(freemapPlaceElement.getAttribute(FREEMAP_FONT_SIZE_ATR));
        var nameWidth = Double.parseDouble(freemapPlaceElement.getAttribute(FREEMAP_PLACE_NAME_WIDTH_ATR));
        //
        var place = PlaceFactory.getPlace(placeID);
        //
        var freeMapPlace = FreeMapPlace.createFreeMapPlace(parentFreeMapID, place, getPlotSeparationParamerter(freeMapParameters), nameWidth, fontSize);
        //
        freeMapPlace.setHeight(height);
        freeMapPlace.setY(yPos);
        //
        var freemapPersonsElements = freemapPlaceElement.getElementsByTagName(FREEMAP_PERSON_ELEMENT);
        FreeMapPerson[] sortedPersonsInPlace = new FreeMapPerson[freemapPersonsElements.getLength()];
        for (int i = 0; i < freemapPersonsElements.getLength(); i++) {
            var personElement = (Element) freemapPersonsElements.item(i);
            var personId = Long.parseLong(personElement.getAttribute(ID_ATR));
            var personIndex = Integer.parseInt(personElement.getAttribute(INDEX_ATR));
            var person = freeMapPersons.stream().filter(p -> p.getId() == personId).findFirst().orElse(null);
            sortedPersonsInPlace[personIndex] = person;
        }
        freeMapPlace.setPersonOrder(sortedPersonsInPlace);
        return freeMapPlace;
    }

    private static FreeMapConnector parseConnectorElement(Element freeMapConnectorElement, List<FreeMapStay> links) {
        var connectorID = Long.parseLong(freeMapConnectorElement.getAttribute(ID_ATR));
        var date = Double.parseDouble(freeMapConnectorElement.getAttribute(DATE_ATR));
        var colorS = freeMapConnectorElement.getAttribute(COLOR_ATR);
        var xPos = Double.parseDouble(freeMapConnectorElement.getAttribute(X_POS_ATR));
        var yPos = Double.parseDouble(freeMapConnectorElement.getAttribute(Y_POS_ATR));
        var plotSize = Double.parseDouble(freeMapConnectorElement.getAttribute(PLOT_SIZE_ATR));
        var linkedElementID = Long.parseLong(freeMapConnectorElement.getAttribute(FREEMAP_LINKED_ELEMENT_ID_ATR));
        //
        var stayLink = links.stream().filter(l -> l.getId() == linkedElementID).findAny().orElse(null);
        if (stayLink == null) {
            throw new IllegalStateException("Could not find stay with id=" + linkedElementID + "for connectorID=" + connectorID);
        }
        var stayLinkConnector = FreeMapConnectorFactory.createFreeMapLinkConnector(connectorID, stayLink, date, plotSize);
        LOG.log(Level.INFO, "Created freemapConnector {0}.", new Object[]{stayLinkConnector});
        //
        return stayLinkConnector;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
