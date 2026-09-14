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
package com.github.noony.app.timelinefx.hmi;

import com.github.noony.app.timelinefx.core.Date;
import com.github.noony.app.timelinefx.core.Messages;
import com.github.noony.app.timelinefx.core.TimeFormat;
import com.github.noony.app.timelinefx.utils.DateUtils;
import com.github.noony.app.timelinefx.utils.MathUtils;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * A JavaFX component displaying and editing a {@link Date} from the core module: a {@link DatePicker} when the date is backed by a {@code LocalDate}, or a {@link TextField} when
 * it is backed by a raw numeric value.
 *
 * @author hamon
 */
public final class DateViewer {

    /**
     * Prefix used to namespace this class's property change event names.
     */
    public static final String CLASS_NAME = "DateViewer";

    /**
     * Name of the property change event fired when the date is edited through this viewer.
     */
    public static final String DATE_CHANGED = CLASS_NAME + "__dateChanged";

    /**
     * Support object used to fire property change events.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * The date represented by this viewer.
     */
    private Date date;

    private TimeFormat timeFormat;

    /**
     * The control rendering this viewer's date.
     */
    private final Node node;

    /**
     * The date picker rendering this viewer, when the date is backed by a {@code LocalDate}; {@code null} otherwise.
     */
    private DatePicker datePicker;

    /**
     * The text field rendering this viewer, when the date is backed by a raw numeric value; {@code null} otherwise.
     */
    private TextField textField;

    /**
     * Guard used to avoid re-entering change handling while a value is being applied programmatically.
     */
    private boolean updating = false;

    private boolean isDisabled = false;

    /**
     * Creates a viewer for the given date, rendered as a {@link DatePicker} or a {@link TextField} depending on how the date is represented.
     *
     * @param aTimeFormat the TimeFormat used to represent the date / times
     */
    public DateViewer(TimeFormat aTimeFormat) {
        date = null;
        timeFormat = aTimeFormat;
        node = switch (timeFormat) {
            case LOCAL_TIME ->
                createDatePicker();
            case TIME_MIN ->
                createTextField();
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + date.getTimeFormat());
        };
    }

    /**
     * Creates a viewer for the given date, rendered as a {@link DatePicker} or a {@link TextField} depending on how the date is represented.
     *
     * @param aDate the date to represent
     */
    public DateViewer(final Date aDate) {
        date = aDate;
        timeFormat = date.getTimeFormat();
        node = switch (date.getTimeFormat()) {
            case LOCAL_TIME ->
                createDatePicker();
            case TIME_MIN ->
                createTextField();
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + date.getTimeFormat());
        };
    }

    /**
     * @return the node rendering this viewer, to be added to a scene graph
     */
    public Node getNode() {
        return node;
    }

    public void setDate(Date newDate) {
        date = newDate;
        updateValue();
    }

    /**
     * @return the date represented by this viewer
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param disable whether this viewer's control should be disabled
     */
    public void setDisable(final boolean disable) {
        node.setDisable(disable);
    }

    /**
     * Programmatically updates the displayed value. Only valid when this viewer's date is backed by a {@code LocalDate}.
     *
     * @param newValue the new date value
     */
    public void setValue(final LocalDate newValue) {
        if (date.getTimeFormat() != TimeFormat.LOCAL_TIME) {
            throw new IllegalStateException("setValue(LocalDate) called on a " + date.getTimeFormat() + " DateViewer.");
        }
        updating = true;
        datePicker.setValue(newValue);
        date.setDateAsLocal(newValue);
        updating = false;
    }

    /**
     * Programmatically updates the displayed value. Only valid when this viewer's date is backed by a raw numeric value.
     *
     * @param newValue the new timestamp value
     */
    public void setValue(final double newValue) {
        if (date.getTimeFormat() != TimeFormat.TIME_MIN) {
            throw new IllegalStateException("setValue(double) called on a " + date.getTimeFormat() + " DateViewer.");
        }
        updating = true;
        textField.setText(MathUtils.doubleToString(newValue));
        date.setDateAsDouble(newValue);
        updating = false;
    }

    /**
     * @param listener the listener to add, notified with {@link #DATE_CHANGED} whenever the user edits the date
     */
    public void addListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public void removeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setDisabled(boolean newIsDisabled) {
        isDisabled = newIsDisabled;
        node.setDisable(isDisabled);
    }

    private DatePicker createDatePicker() {
        datePicker = new DatePicker();
        if (date != null) {
            datePicker.setValue(date.getDateAsLocal());
        }
        datePicker.setConverter(DateUtils.CONVERTER);
        datePicker.setPromptText("dd-MM-yyyy");
        datePicker.valueProperty().addListener((var ov, final var oldValue, final var newValue) -> {
            if (updating) {
                return;
            }
            date.setDateAsLocal(newValue);
            propertyChangeSupport.firePropertyChange(DATE_CHANGED, oldValue, newValue);
        });
        return datePicker;
    }

    private TextField createTextField() {
        textField = new TextField(MathUtils.doubleToString(date.getDateAsDouble()));
        textField.textProperty().addListener((var ov, final var oldValue, final var newValue) -> {
            if (updating) {
                return;
            }
            try {
                date.setDateAsDouble(Double.parseDouble(newValue));
                propertyChangeSupport.firePropertyChange(DATE_CHANGED, oldValue, newValue);
            } catch (NumberFormatException e) {
                // ignore unparsable intermediate input, keep the last valid value
            }
        });
        return textField;
    }

    private void updateValue() {
        switch (timeFormat) {
            case LOCAL_TIME ->
                datePicker.setValue(date.getDateAsLocal());
            case TIME_MIN ->
                textField.setText(MathUtils.doubleToString(date.getDateAsDouble()));
            default ->
                throw new UnsupportedOperationException(Messages.UNSUPPORTED_TIME_FORMAT + date.getTimeFormat());
        }
    }

}
