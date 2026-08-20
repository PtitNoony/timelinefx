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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application-wide undo/redo history, following the same static-singleton style as
 * {@link com.github.noony.app.timelinefx.hmi.AppInstanceConfiguration}. No {@link Command} is currently pushed
 * through this manager anywhere in the app; it is wired up (Edit menu, keyboard shortcuts, enablement) and ready
 * for individual actions to be made undoable one at a time.
 *
 * @author solun
 */
public final class UndoManager {

    /**
     * Fired whenever {@link #canUndo()} or {@link #canRedo()} may have changed, so the UI can refresh its
     * Undo/Redo controls. Carries no old/new value; listeners should re-read {@link #canUndo()}/{@link #canRedo()}.
     */
    public static final String UNDO_STATE_CHANGED = "undoStateChanged";

    /**
     * Logger for this class.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Support for {@link #UNDO_STATE_CHANGED} notifications.
     */
    private static final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(UndoManager.class);

    /**
     * Executed (or redone) commands, most recent first.
     */
    private static final Deque<Command> UNDO_STACK = new ArrayDeque<>();

    /**
     * Undone commands, most recent first.
     */
    private static final Deque<Command> REDO_STACK = new ArrayDeque<>();

    private UndoManager() {
        // private utility constructor
    }

    /**
     * Registers a listener to be notified after every {@link #execute(Command)}, {@link #undo()}, {@link #redo()}
     * or {@link #reset()}.
     *
     * @param listener notified with {@link #UNDO_STATE_CHANGED}
     */
    public static void addPropertyChangeListener(final PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    /**
     * Stops notifying a previously registered listener.
     *
     * @param listener the listener to stop notifying
     */
    public static void removePropertyChangeListener(final PropertyChangeListener listener) {
        PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

    /**
     * Runs {@code command}, then pushes it onto the undo history. Clears the redo history, since redoing past
     * actions no longer makes sense once a new command has been executed.
     *
     * @param command the command to run and track
     */
    public static void execute(final Command command) {
        command.execute();
        UNDO_STACK.push(command);
        REDO_STACK.clear();
        LOG.log(Level.INFO, "Executed command: {0}", new Object[]{command.getDescription()});
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(UNDO_STATE_CHANGED, null, null);
    }

    /**
     * Reverts the most recently executed (or redone) command, moving it onto the redo history. Does nothing if
     * {@link #canUndo()} is {@code false}.
     */
    public static void undo() {
        if (UNDO_STACK.isEmpty()) {
            return;
        }
        final var command = UNDO_STACK.pop();
        command.undo();
        REDO_STACK.push(command);
        LOG.log(Level.INFO, "Undone command: {0}", new Object[]{command.getDescription()});
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(UNDO_STATE_CHANGED, null, null);
    }

    /**
     * Re-runs the most recently undone command, moving it back onto the undo history. Does nothing if
     * {@link #canRedo()} is {@code false}.
     */
    public static void redo() {
        if (REDO_STACK.isEmpty()) {
            return;
        }
        final var command = REDO_STACK.pop();
        command.execute();
        UNDO_STACK.push(command);
        LOG.log(Level.INFO, "Redone command: {0}", new Object[]{command.getDescription()});
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(UNDO_STATE_CHANGED, null, null);
    }

    /**
     * Reports whether there is a command to undo.
     *
     * @return {@code true} if {@link #undo()} would revert a command
     */
    public static boolean canUndo() {
        return !UNDO_STACK.isEmpty();
    }

    /**
     * Reports whether there is a command to redo.
     *
     * @return {@code true} if {@link #redo()} would re-run a command
     */
    public static boolean canRedo() {
        return !REDO_STACK.isEmpty();
    }

    /**
     * Clears both the undo and redo history, e.g. when switching to a different project.
     */
    public static void reset() {
        UNDO_STACK.clear();
        REDO_STACK.clear();
        PROPERTY_CHANGE_SUPPORT.firePropertyChange(UNDO_STATE_CHANGED, null, null);
    }

}
