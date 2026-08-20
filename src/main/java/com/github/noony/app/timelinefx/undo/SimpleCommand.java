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
 * A {@link Command} built from two closures, for actions that boil down to "call a setter with the new value; to
 * undo, call it again with the old value" and don't warrant a dedicated class.
 *
 * @author solun
 */
public final class SimpleCommand implements Command {

    private final String description;

    private final Runnable doAction;

    private final Runnable undoAction;

    /**
     * Creates a command from two closures.
     *
     * @param description a short, human-readable description of the action
     * @param doAction applies the action; called once now and again on every redo
     * @param undoAction reverts the action
     */
    public SimpleCommand(final String description, final Runnable doAction, final Runnable undoAction) {
        this.description = description;
        this.doAction = doAction;
        this.undoAction = undoAction;
    }

    @Override
    public void execute() {
        doAction.run();
    }

    @Override
    public void undo() {
        undoAction.run();
    }

    @Override
    public String getDescription() {
        return description;
    }

}
