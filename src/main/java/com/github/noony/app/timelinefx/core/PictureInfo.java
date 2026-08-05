/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.noony.app.timelinefx.core;

import java.time.LocalDateTime;

/**
 * Metadata extracted from a picture file (name, location, creation date and dimensions).
 *
 * @author hamon
 */
public class PictureInfo {

    /**
     * The picture's name.
     */
    private final String name;

    /**
     * The picture file's path.
     */
    private final String path;

    /**
     * The picture's creation date.
     */
    private final LocalDateTime creationDate;

    /**
     * The picture's width.
     */
    private final int width;

    /**
     * The picture's height.
     */
    private final int height;

    /**
     * @param name the picture's name
     * @param path the picture file's path
     * @param creationDate the picture's creation date
     * @param width the picture's width
     * @param height the picture's height
     */
    public PictureInfo(final String name, final String path, LocalDateTime creationDate, int width, int height) {
        this.name = name;
        this.path = path;
        this.creationDate = creationDate;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the picture's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the picture file's path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the picture's creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @return the picture's width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the picture's height
     */
    public int getHeight() {
        return height;
    }

}
