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

import com.github.noony.app.timelinefx.core.Picture;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author hamon
 */
public class ExecutorUtils {

    private static final int THREAD_POOL_SIZE = 3;

    private static ThreadPoolExecutor executor;
    private static List<Picture> picturesToBeLoaded;

    public static void init() {
        picturesToBeLoaded = Collections.synchronizedList(new LinkedList<>());
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static void foo() {
//        java.util.concurrent.ConcurrentLinkedQueue pics = new ConcurrentLinkedQueue();

//        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 0, 0, TimeUnit.DAYS, workQueue);
    }
}
