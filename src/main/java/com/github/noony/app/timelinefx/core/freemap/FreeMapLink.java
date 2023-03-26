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
package com.github.noony.app.timelinefx.core.freemap;

import com.github.noony.app.timelinefx.core.FriezeObject;
import com.github.noony.app.timelinefx.core.freemap.connectors.FreeMapConnector;
import com.github.noony.app.timelinefx.core.freemap.links.LinkType;
import com.github.noony.app.timelinefx.hmi.freemap.LinkShape;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author hamon
 */
public interface FreeMapLink extends Selectable, FriezeObject {

    String INTERMEDIATE_CONNECTOR_ADDED = "FreeMapLinkIntermediateConnectorAdded";

    String INTERMEDIATE_CONNECTOR_REMOVED = "FreeMapLinkIntermediateConnectorRemoved";

    FreeMapConnector getBeginConnector();

    FreeMapConnector getEndConnector();

    FreeMapPerson getPerson();

    LinkType getType();

    LinkShape getLinkShape();

    Color getColor();

    void setColor(Color aColor);

    void setConnectorsVisibility(boolean visibility);

    void setLinkShape(LinkShape aLinkShape);

    void addIntermediateConnector(FreeMapConnector aConnector);

    void removeIntermediateConnector(FreeMapConnector aConnector);

    List<FreeMapConnector> getIntermediateConnectors();

}
