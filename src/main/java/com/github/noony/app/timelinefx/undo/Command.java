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

/**
 * A single reversible action, to be executed and tracked through {@link UndoManager}.
 *
 * @author hamon
 */
public interface Command {

    /**
     * Performs the action. Called once when the command is first run through
     * {@link UndoManager#execute(Command)}, and again on every {@link UndoManager#redo()}.
     */
    void execute();

    /**
     * Reverts the action performed by {@link #execute()}. Called on {@link UndoManager#undo()}.
     */
    void undo();

    /**
     * A short, human-readable description of the action (e.g. "Move portrait"), for display next to
     * "Undo"/"Redo" in the UI.
     *
     * @return the description, or an empty string by default
     */
    default String getDescription() {
        return "";
    }

}
