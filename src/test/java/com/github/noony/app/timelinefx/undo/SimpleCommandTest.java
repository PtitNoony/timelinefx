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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SimpleCommand}.
 *
 * @author solun
 */
public final class SimpleCommandTest {

    /**
     * Description shared by the commands in this test that only ever apply a single fixed value.
     */
    private static final String SET_TO_ONE_DESCRIPTION = "Set to 1";

    /**
     * Default constructor.
     */
    public SimpleCommandTest() {
    }

    /**
     * Resets the shared {@link UndoManager} history before each test.
     */
    @BeforeEach
    public void setUp() {
        UndoManager.reset();
    }

    /**
     * Resets the shared {@link UndoManager} history after each test.
     */
    @AfterEach
    public void tearDown() {
        UndoManager.reset();
    }

    /**
     * Checks that {@link SimpleCommand#execute()} runs the {@code doAction} closure.
     */
    @Test
    public void testExecuteRunsDoAction() {
        final var value = new int[]{0};
        final var command = new SimpleCommand(SET_TO_ONE_DESCRIPTION, () -> value[0] = 1, () -> value[0] = 0);
        command.execute();
        assertEquals(1, value[0]);
    }

    /**
     * Checks that {@link SimpleCommand#undo()} runs the {@code undoAction} closure.
     */
    @Test
    public void testUndoRunsUndoAction() {
        final var value = new int[]{0};
        final var command = new SimpleCommand(SET_TO_ONE_DESCRIPTION, () -> value[0] = 1, () -> value[0] = 0);
        command.execute();
        command.undo();
        assertEquals(0, value[0]);
    }

    /**
     * Checks that {@link SimpleCommand#getDescription()} returns the description passed to the constructor.
     */
    @Test
    public void testGetDescription() {
        final var command = new SimpleCommand(SET_TO_ONE_DESCRIPTION, () -> {
        }, () -> {
        });
        assertEquals(SET_TO_ONE_DESCRIPTION, command.getDescription());
    }

    /**
     * Checks a full {@link UndoManager#execute(Command)}, {@link UndoManager#undo()}, {@link UndoManager#redo()}
     * cycle.
     */
    @Test
    public void testUndoManagerRoundTrip() {
        final var value = new int[]{0};
        final var target = 42;
        final var command = new SimpleCommand("Set to target", () -> value[0] = target, () -> value[0] = 0);
        //
        UndoManager.execute(command);
        assertEquals(target, value[0]);
        assertTrue(UndoManager.canUndo());
        assertFalse(UndoManager.canRedo());
        //
        UndoManager.undo();
        assertEquals(0, value[0]);
        assertFalse(UndoManager.canUndo());
        assertTrue(UndoManager.canRedo());
        //
        UndoManager.redo();
        assertEquals(target, value[0]);
        assertTrue(UndoManager.canUndo());
        assertFalse(UndoManager.canRedo());
    }

}
