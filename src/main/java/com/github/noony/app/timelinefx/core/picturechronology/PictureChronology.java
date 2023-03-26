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
package com.github.noony.app.timelinefx.core.picturechronology;

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.IDrawableObject;
import com.github.noony.app.timelinefx.core.IPicture;
import com.github.noony.app.timelinefx.core.Person;
import com.github.noony.app.timelinefx.core.PersonFactory;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.util.Pair;

/**
 *
 * @author hamon
 */
public class PictureChronology implements FriezeObject, IDrawableObject {

    public static final String DEFAULT_NAME = "PictureChronologyNoName";

    public static final String LAYOUT_CHANGED = "layoutChanged";
    public static final String NAME_CHANGED = "nameChanged";
    public static final String PICTURE_ADDED = "pictureAdded";
    public static final String PICTURE_REMOVED = "pictureRemoved";
    public static final String LINK_ADDED = "linkedAdded";
    public static final String LINK_REMOVED = "linkedRemoved";

    private static final double DEFAULT_WIDTH = 1600;
    private static final double DEFAULT_HEIGHT = 1200;

    public static final double PERSON_CONTOUR_WIDTH = 6;

    private final PropertyChangeSupport propertyChangeSupport;
    private final long id;

    private final ChronologyLinkType chronologyLinkType = ChronologyLink.DEFAULT_LINK_TYPE;

    private final TimeLineProject project;
    private final List<ChronologyPictureMiniature> chronologyPictures;
    private final Map<String, ChronologyLink> chronologyLinks;
    private final List<Person> persons;
    //
    private final TimeFormat timeFormat;
    //
    private String name;
    private double width;
    private double height;

    protected PictureChronology(long anID, TimeLineProject aProject, String aName, TimeFormat aTimeFormat, List<ChronologyPictureMiniature> exisitingMiniatures, List<ChronologyLink> existingLinks) {
        id = anID;
        propertyChangeSupport = new PropertyChangeSupport(PictureChronology.this);
        project = aProject;
        project.addPictureChronology(PictureChronology.this);
        chronologyPictures = new LinkedList<>();
        chronologyLinks = new HashMap<>();
        persons = new LinkedList<>();
        timeFormat = aTimeFormat;
        name = aName;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        //
        exisitingMiniatures.forEach(picture -> {
            chronologyPictures.add(picture);
            picture.addListener(PictureChronology.this::handleChronologyPictureChanges);
            propertyChangeSupport.firePropertyChange(PICTURE_ADDED, this, picture);
        });
        existingLinks.forEach(link -> {
            var linkKey = link.getPerson().getId() + "__" + link.getStartMiniature().getId() + "__" + link.getEndMiniature().getId();
            chronologyLinks.put(linkKey, link);
            propertyChangeSupport.firePropertyChange(LINK_ADDED, this, link);
        });
        persons.addAll(exisitingMiniatures.stream().flatMap(m -> m.getPersons().stream()).filter(p -> !persons.contains(p)).toList());
        updateLinks();
    }

    protected PictureChronology(long anID, TimeLineProject aProject, String aName) {
        this(anID, aProject, aName, TimeFormat.LOCAL_TIME, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public long getId() {
        return id;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void addChronologyPicture(ChronologyPictureMiniature aChronologyPicture) {
        chronologyPictures.add(aChronologyPicture);
        aChronologyPicture.addListener(this::handleChronologyPictureChanges);
        propertyChangeSupport.firePropertyChange(PICTURE_ADDED, this, aChronologyPicture);
        //
        persons.addAll(aChronologyPicture.getPersons().stream().filter(p -> !persons.contains(p)).toList());
        //
        updateLinks();
    }

    public ChronologyPictureMiniature createChronologyPicture(IPicture aPicture) {
        var chronologyPictureMiniature = PictureChronologyFactory.createChronologyPictureMiniature(aPicture, new Point2D(width / 2.0, height / 2.0), 0.2);
        addChronologyPicture(chronologyPictureMiniature);
        return chronologyPictureMiniature;
    }

    public void addPicture(Picture aPicture, Point2D aPosition, double aScale) {
        var chronologyPicture = PictureChronologyFactory.createChronologyPictureMiniature(aPicture, aPosition, aScale);
        addChronologyPicture(chronologyPicture);
    }

    public void removeChronologyPicture(ChronologyPictureMiniature aChronologyPicture) {
        var removed = chronologyPictures.remove(aChronologyPicture);
        if (removed) {
            aChronologyPicture.removeListener(this::handleChronologyPictureChanges);
            propertyChangeSupport.firePropertyChange(PICTURE_REMOVED, this, aChronologyPicture);
            //
            var existingPersons = chronologyPictures.stream()
                    .flatMap(c -> c.getPersons().stream())
                    .collect(Collectors.toList());
            var personInExcedent = persons.stream()
                    .filter(p -> !existingPersons.contains(p))
                    .collect(Collectors.toList());
            persons.removeAll(personInExcedent);
            //
            updateLinks();
        }
    }

    public void setName(String aName) {
        name = aName;
        propertyChangeSupport.firePropertyChange(NAME_CHANGED, this, name);
    }

    public void setWidth(double aWidth) {
        width = aWidth;
        propertyChangeSupport.firePropertyChange(LAYOUT_CHANGED, this, name);
    }

    public void setHeight(double aHeight) {
        height = aHeight;
        propertyChangeSupport.firePropertyChange(LAYOUT_CHANGED, this, name);
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    public List<ChronologyPictureMiniature> getChronologyPictures() {
        return Collections.unmodifiableList(chronologyPictures);
    }

    public List<ChronologyLink> getLinks() {
        return Collections.unmodifiableList(chronologyLinks.values().stream().toList());
    }

    private void updateLinks() {
        chronologyPictures.sort(ChronologyPictureMiniature.COMPARATOR);
        List<String> existingLinkKeys = chronologyLinks.keySet().stream().collect(Collectors.toList());
        Map<String, Pair<ChronologyPictureMiniature, ChronologyPictureMiniature>> linksNeeded = new HashMap<>();
        // looping all persons and miniatures to calculate all the links needed
        persons.forEach(person -> {
            List<ChronologyPictureMiniature> personMiniatures = chronologyPictures.stream().filter(pic -> pic.getPersonIndex(person) >= 0).toList();
            for (int i = 0; i < personMiniatures.size() - 1; i++) {
                var startMiniature = personMiniatures.get(i);
                var endMiniature = personMiniatures.get(i + 1);
                var linkKey = person.getId() + "__" + startMiniature.getId() + "__" + endMiniature.getId();
                linksNeeded.put(linkKey, new Pair(startMiniature, endMiniature));
            }
        });
        // remove useless links
        existingLinkKeys.stream().filter(existingKey -> (!linksNeeded.containsKey(existingKey))).forEachOrdered(existingKey -> {
            var removedLink = chronologyLinks.get(existingKey);
            chronologyLinks.remove(existingKey);
            propertyChangeSupport.firePropertyChange(LINK_REMOVED, this, removedLink);
        });
        // create new links
        linksNeeded.forEach((neededKey, miniatures) -> {
            if (!existingLinkKeys.contains(neededKey)) {
                int personID = Integer.parseInt(neededKey.split("__")[0]);
                var person = PersonFactory.getPerson(personID);
                var startMiniature = miniatures.getKey();
                var endMiniature = miniatures.getValue();
                var linkParameters = ChronologyLinkType.getDefaultParameters(chronologyLinkType, startMiniature, endMiniature, person);
                var chronologyLink = PictureChronologyFactory.createChronologyLink(person, startMiniature, endMiniature, chronologyLinkType, linkParameters);
                chronologyLinks.put(neededKey, chronologyLink);
                propertyChangeSupport.firePropertyChange(LINK_ADDED, this, chronologyLink);
            }
        });
    }

    private void updateLinksConnectedTo(ChronologyPictureMiniature aPictureMiniature) {
        chronologyLinks.values().stream()
                .filter(link -> link.getStartMiniature() == aPictureMiniature || link.getEndMiniature() == aPictureMiniature)
                .forEach(link -> {
                    var linkParameters = ChronologyLinkType.getDefaultParameters(chronologyLinkType, link);
                    link.updateLinkParameters(linkParameters);
                });
    }

    private void handleChronologyPictureChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case ChronologyPictureMiniature.POSITION_CHANGED, ChronologyPictureMiniature.SCALE_CHANGED -> {
                // Nothing to do
            }
            case ChronologyPictureMiniature.TIME_CHANGED -> {
                updateLinks();
            }
            case ChronologyPictureMiniature.REQUEST_LINKS_UPDATE -> {
                updateLinksConnectedTo((ChronologyPictureMiniature) event.getNewValue());
            }
            default ->
                throw new UnsupportedOperationException("handlePictureMiniatureChanges :: " + event.getPropertyName());
        }
    }

}
