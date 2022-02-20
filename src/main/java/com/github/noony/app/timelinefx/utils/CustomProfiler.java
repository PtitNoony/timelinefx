/*
 * Copyright (C) 2022 NoOnY
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Based on https://stackoverflow.com/a/14558802 .
 *
 * @author hamon
 */
public class CustomProfiler {

    private static final int THEORETICAL_MAX_NAME_LENGTH = 50;

    private static final boolean WITH_PROFILE = true;

//    private static CustomProfiler singletonInstance = null;
    private static final Map<String, Profile> profiles = new HashMap<>(); // Fast access to profiles by name
    private static final List<Profile> profilesStack = new LinkedList<>();// Profiles as created chronologically

//    /**
//     * Get access to the singleton instance (create it if necessary).
//     *
//     * @return
//     */
//    public static CustomProfiler getInstance() {
//        if (singletonInstance == null) {
//            singletonInstance = new CustomProfiler();
//        }
//        return singletonInstance;
//    }
    /**
     * Protected constructor for singleton
     */
//    protected CustomProfiler() {
//        profiles = new HashMap<>();
//        profilesStack = new ArrayList<>();
//    }
    /**
     * Start a profile. If the profile does not exist, it will be created. If it exists, a new round of measure is
     * taken.
     *
     * @param name The name of the profile. If possible, less than Profiler.THEORETICAL_MAX_NAME_LENGTH characters
     *
     * @see Profiler.THEORETICAL_MAX_NAME_LENGTH
     */
    public static synchronized void start(String name) {
        if (WITH_PROFILE) {
            var p = profiles.get(name);
            if (p == null) {
                p = new Profile(name);
                profiles.put(name, p);
                profilesStack.add(p);
            }
            p.start();
        }
    }

    /**
     * Stop a profile and compute some statistics about it.
     *
     * @param name The name of the profile as declared in the corresponding start method
     */
    public static synchronized void stop(String name) {
        if (WITH_PROFILE) {
            var p = profiles.get(name);
            if (p == null) {
                throw new RuntimeException("The profile " + name + " has not been created by a call to the start() method!");
            }
            p.stop();
        }
    }

    /**
     * Clear all the current measures. Not to be called within any start/stop pair.
     */
    public void reset() {
        profiles.clear();
    }

    /**
     * Build a string containing all the information about the measures we have taken so far.
     *
     * @return
     */
    public static synchronized String toStringValue() {
        final StringBuffer sb = new StringBuffer();
        profilesStack.stream().map(p -> {
            sb.append(p.toString());
            return p;
        }).forEachOrdered(_item -> {
            sb.append("\n");
        });
        return sb.toString();
    }

    /**
     * Output the measures to an output string
     *
     * @param os
     * @throws java.io.IOException
     */
    public void toCsvFile(OutputStream os) throws IOException {
        Profile.writeCsvHeader(os);
        for (Profile p : profilesStack) {
            p.writeCsvLine(os);
        }
    }

    /**
     * Profile information. It stores statistics per named profile.
     *
     * @author Vincent Prat @ MarvinLabs
     */
    private static class Profile {

        private static final String CSV_HEADERS = "Name, Call Count, Total Time (ms), Average Time (ms), Min Time (ms), Max Time (ms), Delta Time (ms), Delta Ratio (%)\n";

        private static final String FORMAT_STRING = "%-" + THEORETICAL_MAX_NAME_LENGTH + "."
                + THEORETICAL_MAX_NAME_LENGTH
                + "s: %3d calls, total %5d ms, avg %5d ms, min %5d ms, max %5d ms, delta %5d ms (%d%%)";

        private static final String CSV_FORMAT_STRING = "%s,%d,%d,%d,%d,%d,%d,%d\n";

        private final String name;
        private long startTime;
        private long callCount;
        private long totalTime;
        private long minTime;
        private long maxTime;

        public Profile(String aName) {
            name = aName;
            callCount = 0;
            totalTime = 0;
            startTime = 0;
            minTime = Long.MAX_VALUE;
            maxTime = Long.MIN_VALUE;
        }

        public void start() {
            startTime = System.currentTimeMillis();
        }

        public void stop() {
            final long elapsed = (System.currentTimeMillis() - startTime);
            if (elapsed < minTime) {
                minTime = elapsed;
            }
            if (elapsed > maxTime) {
                maxTime = elapsed;
            }
            totalTime += elapsed;
            callCount++;
        }

        private String getFormattedStats(String format) {
            final long avgTime = callCount == 0 ? 0 : (long) totalTime / callCount;
            final long delta = maxTime - minTime;
            final double deltaRatio = avgTime == 0 ? 0 : 100.0 * ((double) 0.5 * delta / (double) avgTime);

            return String
                    .format(format, name, callCount, totalTime, avgTime, minTime, maxTime, delta, (int) deltaRatio);
        }

        @Override
        public String toString() {
            return getFormattedStats(FORMAT_STRING);
        }

        public static void writeCsvHeader(OutputStream os) throws IOException {
            os.write(CSV_HEADERS.getBytes());
        }

        public void writeCsvLine(OutputStream os) throws IOException {
            os.write(getFormattedStats(CSV_FORMAT_STRING).getBytes());
        }
    }

}
