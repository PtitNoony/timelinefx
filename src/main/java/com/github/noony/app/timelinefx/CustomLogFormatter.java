/*
 * Copyright (C) 2025 NoOnY
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
package com.github.noony.app.timelinefx;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author solun
 */
public class CustomLogFormatter extends Formatter {

    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        // Convert log record time to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(record.getMillis()),
                ZoneId.systemDefault()
        );

        String formattedTime = dateTime.format(DATE_FORMATTER);

        String fullClassName = record.getSourceClassName();
        String simpleClassName = fullClassName != null
                ? fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
                : "UnknownClass";

        // Properly format the message with parameters
        String message;
        if (record.getParameters() != null) {
            try {
                message = MessageFormat.format(record.getMessage(), record.getParameters());
            } catch (IllegalArgumentException e) {
                // Fallback if formatting fails
                message = record.getMessage();
            }
        } else {
            message = record.getMessage();
        }

        return String.format("[%s] [%-7s] [%-20s] [%-20s] %s%n",
                formattedTime,
                record.getLevel(),
                simpleClassName,
                record.getSourceMethodName(),
                message
        );
    }
}
