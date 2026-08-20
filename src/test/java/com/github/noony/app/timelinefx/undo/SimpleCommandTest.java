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

package com.github.noony.app.timelinefx.undo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author solun
 */
public class SimpleCommandTest {

    @BeforeEach
    public void setUp() {
        UndoManager.reset();
    }

    @AfterEach
    public void tearDown() {
        UndoManager.reset();
    }

    @Test
    public void testExecuteRunsDoAction() {
        var value = new int[]{0};
        var command = new SimpleCommand("Set to 1", () -> value[0] = 1, () -> value[0] = 0);
        command.execute();
        assertEquals(1, value[0]);
    }

    @Test
    public void testUndoRunsUndoAction() {
        var value = new int[]{0};
        var command = new SimpleCommand("Set to 1", () -> value[0] = 1, () -> value[0] = 0);
        command.execute();
        command.undo();
        assertEquals(0, value[0]);
    }

    @Test
    public void testGetDescription() {
        var command = new SimpleCommand("Set to 1", () -> {
        }, () -> {
        });
        assertEquals("Set to 1", command.getDescription());
    }

    @Test
    public void testUndoManagerRoundTrip() {
        var value = new int[]{0};
        var command = new SimpleCommand("Set to 42", () -> value[0] = 42, () -> value[0] = 0);
        //
        UndoManager.execute(command);
        assertEquals(42, value[0]);
        assertTrue(UndoManager.canUndo());
        assertFalse(UndoManager.canRedo());
        //
        UndoManager.undo();
        assertEquals(0, value[0]);
        assertFalse(UndoManager.canUndo());
        assertTrue(UndoManager.canRedo());
        //
        UndoManager.redo();
        assertEquals(42, value[0]);
        assertTrue(UndoManager.canUndo());
        assertFalse(UndoManager.canRedo());
    }

}
