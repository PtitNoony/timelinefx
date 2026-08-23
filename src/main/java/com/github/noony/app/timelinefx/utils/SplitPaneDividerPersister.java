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

package com.github.noony.app.timelinefx.utils;

import com.github.noony.app.timelinefx.Configuration;
import javafx.animation.PauseTransition;
import javafx.scene.control.SplitPane;
import javafx.util.Duration;

/**
 * Restores a {@link SplitPane}'s divider positions from {@link Configuration}, then keeps them in sync as the
 * user drags them.
 *
 * @author hamon
 */
public final class SplitPaneDividerPersister {

    /**
     * How long to wait after the last divider move before persisting, so a drag gesture writes once.
     */
    private static final Duration SAVE_DELAY = Duration.millis(300);

    private SplitPaneDividerPersister() {
        // private utility constructor
    }

    /**
     * Applies any previously saved divider positions to {@code splitPane}, then persists further changes under
     * {@code key} (debounced, so a drag gesture writes to disk once, not on every pixel moved).
     *
     * @param splitPane the split pane to restore and track
     * @param key a key unique across the whole application
     */
    public static void bind(final SplitPane splitPane, final String key) {
        final var savedPositions = Configuration.getSplitPaneDividerPositions(key);
        if (savedPositions != null) {
            splitPane.setDividerPositions(savedPositions);
        }
        final var saveDelay = new PauseTransition(SAVE_DELAY);
        saveDelay.setOnFinished(event -> Configuration.setSplitPaneDividerPositions(key, splitPane.getDividerPositions()));
        splitPane.getDividers().forEach(divider -> divider.positionProperty().addListener((ov, oldValue, newValue) -> saveDelay.playFromStart()));
    }

}
