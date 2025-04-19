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
package com.github.noony.app.timelinefx.save;

import com.github.noony.app.timelinefx.core.FriezeObjectFactory;
import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author hamon
 */
public final class XMLHandler {

    public static final String CONFIGURATION_ELEMENT = "configuration";

    private static final Logger LOG = Logger.getGlobal();

    private static final XMLHandler INSTANCE = new XMLHandler();

    private final List<TimelineProjectProvider> providers;
    private TimelineProjectProvider saveProvider = null;
    private String saveVersion = "-1";

    private XMLHandler() {
        //private utility constructor
        providers = new LinkedList<>(Lookup.getDefault().lookupAll(TimelineProjectProvider.class));
        saveProvider = providers.get(0);
        saveVersion = saveProvider.getSupportedVersions().get(0);
        providers.forEach(candidateparser -> {
            String mostRecentVersion = candidateparser.getSupportedVersions().stream()
                    .sorted((v1, v2) -> compareVersions(v1, v2))
                    .findFirst().get();
            int versionComparison = compareVersions(saveVersion, mostRecentVersion);
            if (versionComparison > 0) {
                saveProvider = candidateparser;
                saveVersion = mostRecentVersion;
            }
        });
    }

    public static TimeLineProject loadFile(File file) {
        TimeLineProject project = null;
        if (file != null) {
            FriezeObjectFactory.reset();
            //
            Document document;
            DocumentBuilderFactory builderFactory;
            builderFactory = DocumentBuilderFactory.newDefaultInstance();
            try {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                InputSource source = new InputSource(file.getAbsolutePath());
                document = builder.parse(source);
                Element e = document.getDocumentElement();
                String version = getVersion(e);
                //
                boolean foundProvider = false;
                for (TimelineProjectProvider candidateprovider : INSTANCE.providers) {
                    if (candidateprovider.getSupportedVersions().contains(version)) {
                        project = candidateprovider.load(file, e);
                        foundProvider = true;
                        break;
                    }
                }
                if (!foundProvider) {
                    LOG.log(Level.SEVERE, "Could not find a parser for version {0}.\n Available parsers={1}.", new Object[]{version, INSTANCE.providers});
                }
                //
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                LOG.log(Level.SEVERE, "Exception while loading file {0} :: {1}", new Object[]{file, ex});
            }
        }
        //
        return project;
    }

    public static boolean save(TimeLineProject project, File file) {
        LOG.log(Level.INFO, "Using saveProvider {0}", new Object[]{INSTANCE.saveProvider});
        return INSTANCE.saveProvider.save(project, file);
    }

    private static String getVersion(Element rootElement) {
        if (rootElement.hasAttribute(TimelineProjectProvider.PROJECT_VERSION_ATR)) {
            return rootElement.getAttribute(TimelineProjectProvider.PROJECT_VERSION_ATR);
        }
        return "1";
    }

    public static int compareVersions(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] v1Split = v1.split("\\.");
        String[] v2Split = v2.split("\\.");
        int l1 = v1Split.length;
        int l2 = v2Split.length;
        int maxSplit = Math.max(l1, l2);
        for (int i = 1; i < maxSplit; i++) {
            String i1 = i < l1 ? v1Split[i] : "-1";
            String i2 = i < l2 ? v2Split[i] : "-2";
            int comp = i1.compareTo(i2);
            if (comp != 0) {
                return comp;
            }
        }
        return 1;
    }
}
