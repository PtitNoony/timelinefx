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
import com.github.noony.app.timelinefx.core.StayPeriod;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPerson;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPlace;
import com.github.noony.app.timelinefx.core.freemap.FreeMapPortrait;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMap;
import com.github.noony.app.timelinefx.core.freemap.FriezeFreeMapFactory;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapPlot;
import com.github.noony.app.timelinefx.core.freemap.connectors.PlotType;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapSimpleLink;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapStayLink;
import com.github.noony.app.timelinefx.core.freemap.links.FreeMapTravelLink;
import static com.github.noony.app.timelinefx.save.v3.TimeProjectProviderV3.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author hamon
 */
public class FreeMapProviderV3_TMP {

    protected static final String FREEMAPS_GROUP = "freeMaps";
    private static final String FREEMAP_PLACES_GROUP = "freeMapPlaces";
    private static final String FREEMAP_PERSONS_GROUP = "freeMapPersons";
    private static final String FREEMAP_STAYS_GROUP = "freeMapStays";
    private static final String PARAMETERS_GROUP = "freeMapParameters";
    private static final String PLOTS_GROUP = "plots";
    private static final String LINKS_GROUP = "links";
    private static final String FREEMAP_ELEMENT = "freeMap";
    private static final String PARAMETER_ELEMENT = "parameter";
    private static final String FREEMAP_PERSON_ELEMENT = "freeMapPerson";
    private static final String FREEMAP_PLACE_ELEMENT = "freeMapPlace";
    private static final String FREEMAP_STAY_ELEMENT = "freeMapStay";
    private static final String PLOT_ELEMENT = "plot";
    private static final String LINK_ELEMENT = "link";
    //
    private static final String FREEMAP_PERSON_WIDTH_ATR = "personWidth";
    private static final String FREEMAP_PLACE_NAME_WIDTH_ATR = "placeNameWidth";
    private static final String FREEMAP_FONT_SIZE_ATR = "fontSize";
    private static final String FREEMAP_PLOT_SEPARATION_ATR = "plotSeparation";
    private static final String FREEMAP_PLOT_VISIBILITY_ATR = "plotVisibility";
    private static final String FREEMAP_PLOT_SIZE_ATR = "plotSize";
    //
    private static final String FREEMAP_STAY_TYPE_ATTRIBUTE = "freeMapStayType";
    private static final String FREEMAP_STAY_TYPE_SIMPLE_VALIE = "simple";
    private static final String FREEMAP_STAY_TYPE_COMPLEX_VALUE = "complex";

    private static final Logger LOG = Logger.getGlobal();

    protected static void parseFreeMaps(Element freemapsRootElement, Frieze frieze) {
        var freemapElements = freemapsRootElement.getChildNodes();
        for (int i = 0; i < freemapElements.getLength(); i++) {
            if (freemapElements.item(i).getNodeName().equals(FREEMAP_ELEMENT)) {
                Element e = (Element) freemapElements.item(i);
                parseFreeMap(e, frieze);
            }
        }
    }

    private static void parseFreeMap(Element freemapElement, Frieze frieze) {
        long freeMapID = Long.parseLong(freemapElement.getAttribute(ID_ATR));
        FriezeFreeMap freeMap = FriezeFreeMapFactory.createFriezeFreeMap(freeMapID, frieze, false);
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
        if (portraitsGroups.getLength() < 1) {
            throw new IllegalStateException();
        }
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

    private static void parsePortraits(Element plotsRootElement, FriezeFreeMap freeMap) {
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
//                FreeMapPortrait portrait = freeMap.getPortrait(personID);
                FreeMapPortrait portrait = null;
                System.err.println(" TODO la aussi");
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

    private static void parseFreemapPlaces(Element freemapPlacesRootElement, FriezeFreeMap freeMap) {
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

    private static void parsePlots(Element plotsRootElement, Frieze frieze, FriezeFreeMap freeMap) {
        // <plot stayID="1" type="START" xPos="10.350877192982455" yPos="23.0"/>
        NodeList plotElements = plotsRootElement.getChildNodes();
        List<FreeMapPlot> existingPlots = freeMap.getPlots();
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
                plot = existingPlots.stream().filter(p -> p.getLinkedElementID() == stayID && p.getType() == type).findFirst().orElse(null);
                if (plot == null) {
                    throw new IllegalStateException("Cannot find plot with stayID=" + stayID + " and of type " + typeS);
                }
                plot.setX(xPos);
                plot.setY(yPos);
            }
        }
    }

//
    protected static Element createFreeMapElement(Document doc, FriezeFreeMap friezeFreeMap) {
        var friezeFreeMapElement = doc.createElement(FREEMAP_ELEMENT);
        friezeFreeMapElement.setAttribute(NAME_ATR, friezeFreeMap.getName());
        friezeFreeMapElement.setAttribute(ID_ATR, Long.toString(friezeFreeMap.getId()));
        //
        var configGroupElement = doc.createElement(PARAMETERS_GROUP);
        friezeFreeMap.getParemeters().forEach((pName, pString) -> configGroupElement.appendChild(createParameter(doc, pName, pString)));
        friezeFreeMapElement.appendChild(configGroupElement);
        // Person
        var personsGroupElement = doc.createElement(FREEMAP_PERSONS_GROUP);
        friezeFreeMap.getPersons().forEach(p -> personsGroupElement.appendChild(createFreeMapPersonElement(doc, p)));
        friezeFreeMapElement.appendChild(personsGroupElement);
        // Places
        var placeGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
        friezeFreeMap.getPlaces().forEach(p -> placeGroupElement.appendChild(createFreeMapPlaceElement(doc, p)));
        friezeFreeMapElement.appendChild(placeGroupElement);
        // Stays
        var staysGroupElement = doc.createElement(FREEMAP_STAYS_GROUP);
        friezeFreeMapElement.appendChild(staysGroupElement);
        // Portraits
//        //
//        var plotsGroupElement = doc.createElement(PLOTS_GROUP);
//        friezeFreeMap.getPlots().forEach(plot -> plotsGroupElement.appendChild(createPlotElement(doc, plot)));
//        friezeFreeMapElement.appendChild(plotsGroupElement);
//        //
//        var placesGroupElement = doc.createElement(FREEMAP_PLACES_GROUP);
//        friezeFreeMap.getPlaces().forEach(place -> placesGroupElement.appendChild(createFreeMapPlaceElement(doc, place)));
//        friezeFreeMapElement.appendChild(placesGroupElement);
//        //
//        var linksGroupElement = doc.createElement(LINKS_GROUP);
//        friezeFreeMap.getStayLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
//        friezeFreeMap.getTravelLinks().forEach(link -> linksGroupElement.appendChild(createLinkElement(doc, link)));
//        friezeFreeMapElement.appendChild(linksGroupElement);
        //
        return friezeFreeMapElement;
    }

    private static Element createParameter(Document doc, String aName, String aValueString) {
        var paramElement = doc.createElement(PARAMETER_ELEMENT);
        paramElement.setAttribute(aName, aValueString);
        return paramElement;
    }

    private static Element createFreeMapPersonElement(Document doc, FreeMapPerson freeMapPerson) {
        var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
        personElement.setAttribute(ID_ATR, Long.toString(freeMapPerson.getId()));
        freeMapPerson.getFreeMapPortraits().forEach(fmPortrait -> {
            var fmPortraitElement = createFreeMapPortraitElement(doc, fmPortrait);
            personElement.appendChild(fmPortraitElement);
        });
        return personElement;
    }

    private static Element createFreeMapPlaceElement(Document doc, FreeMapPlace freeMapPlace) {
        var placeElement = doc.createElement(FREEMAP_PLACE_ELEMENT);
        placeElement.setAttribute(PLACE_ID_ATR, Long.toString(freeMapPlace.getPlace().getId()));
        placeElement.setAttribute(HEIGHT_ATR, Double.toString(freeMapPlace.getHeight()));
        placeElement.setAttribute(Y_POS_ATR, Double.toString(freeMapPlace.getYPos()));
        var personInfoElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
        var personsAtPlace = freeMapPlace.getPersons();
        for (int i = 0; i < personsAtPlace.size(); i++) {
            final var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
            personElement.setAttribute(INDEX_ATR, Integer.toString(i));
            personElement.setAttribute(ID_ATR, Long.toString(personsAtPlace.get(i).getId()));
            personInfoElement.appendChild(personElement);
        }
        placeElement.appendChild(personInfoElement);
        return placeElement;
    }

    private static Element createFreeMapStayElement(Document doc, FreeMapPerson freeMapPerson) {
//        var personElement = doc.createElement(FREEMAP_PERSON_ELEMENT);
//        personElement.setAttribute(ID_ATR, Long.toString(freeMapPerson.getId()));
        return null;
    }

    ///
    ///
    ///
    ////
    private static Element createFreeMapPortraitElement(Document doc, FreeMapPortrait portrait) {
        var portraitElement = doc.createElement(PORTRAIT_ELEMENT);
        portraitElement.setAttribute(ID_ATR, Long.toString(portrait.getId()));
        portraitElement.setAttribute(PERSON_ATR, Long.toString(portrait.getPerson().getId()));
        portraitElement.setAttribute(X_POS_ATR, Double.toString(portrait.getX()));
        portraitElement.setAttribute(Y_POS_ATR, Double.toString(portrait.getY()));
        portraitElement.setAttribute(RADIUS_ATR, Double.toString(portrait.getRadius()));
        return portraitElement;
    }

    private static Element createPlotElement(Document doc, FreeMapPlot plot) {
        var plotElement = doc.createElement(PLOT_ELEMENT);
        plotElement.setAttribute(TYPE_ATR, plot.getType().name());
        plotElement.setAttribute(STAY_ID_ATR, Long.toString(plot.getLinkedElementID()));
        plotElement.setAttribute(X_POS_ATR, Double.toString(plot.getX()));
        plotElement.setAttribute(Y_POS_ATR, Double.toString(plot.getY()));
        return plotElement;
    }

    private static Element createLinkElement(Document doc, FreeMapSimpleLink link) {
        var linkElement = doc.createElement(LINK_ELEMENT);
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
