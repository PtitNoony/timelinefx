/*
 * Copyright (C) 2026 NoOnY
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

package com.github.noony.app.timelinefx.core;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link PictureInfo}.
 *
 * @author hamon
 */
public final class PictureInfoTest {

    /**
     * Default constructor.
     */
    public PictureInfoTest() {
    }

    /**
     * Test of the getters, of class PictureInfo.
     */
    @Test
    public void testGetters() {
        final var name = "Foo.png";
        final var path = "pictures/Foo.png";
        final var creationDate = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        final var width = 640;
        final var height = 480;
        final var instance = new PictureInfo(name, path, creationDate, width, height);
        assertEquals(name, instance.getName());
        assertEquals(path, instance.getPath());
        assertEquals(creationDate, instance.getCreationDate());
        assertEquals(width, instance.getWidth());
        assertEquals(height, instance.getHeight());
    }

}
