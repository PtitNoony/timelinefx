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
package com.github.noony.app.timelinefx.save;

import com.github.noony.app.timelinefx.core.TimeLineProject;
import java.io.File;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author hamon
 */
public interface TimelineProjectProvider {

    String PROJECT_VERSION_ATR = "version";

    List<String> getSupportedVersions();

    TimeLineProject load(Element e);

    boolean save(TimeLineProject project, File file);
}
