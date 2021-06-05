/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.noony.app.timelinefx.core;

import java.time.LocalDateTime;

/**
 *
 * @author hamon
 */
public class PictureInfo {

    private final String name;
    private final String path;
    private final LocalDateTime creationDate;
    private final int width;
    private final int height;

    public PictureInfo(String name, String path, LocalDateTime creationDate, int width, int height) {
        this.name = name;
        this.path = path;
        this.creationDate = creationDate;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
