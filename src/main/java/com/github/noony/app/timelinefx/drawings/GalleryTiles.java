/*
 * Copyright (C) 2019 PtitNoOny and Co.
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
package com.github.noony.app.timelinefx.drawings;

import com.github.noony.app.timelinefx.MainApp;
import com.github.noony.app.timelinefx.core.Picture;
import com.github.noony.app.timelinefx.core.PictureFactory;
import com.github.noony.app.timelinefx.core.ProjectConfiguration;
import com.github.noony.app.timelinefx.utils.PngExporter;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author hamon
 */
public class GalleryTiles implements IFxNode {

    public static final double TILE_WIDTH = 250;
    public static final double TILE_HEIGHT = 200;

    public static final String TILE_CLICKED = "tileClicked";

    private static final Logger LOG = Logger.getGlobal();

    private static final double IMAGE_RATIO = 0.9;
    private static final double IMAGE_WIDTH = TILE_WIDTH * IMAGE_RATIO;
    private static final double IMAGE_HEIGHT = TILE_HEIGHT * IMAGE_RATIO;
    private static final Dimension IMAGE_DIMENSION = new Dimension((int) IMAGE_WIDTH, (int) IMAGE_HEIGHT);

    private FlowGridPane tilesPane;

    private final List<Picture> pictures;
    private final PropertyChangeSupport propertyChangeSupport;
    //
    private List<Tile> tiles;

    public GalleryTiles(List<Picture> pictureList) {
        pictures = pictureList;
        pictures.stream()
                .sorted((p1, p2) -> p1.getCreationDate().compareTo(p2.getCreationDate()))
                .collect(Collectors.toList());
        propertyChangeSupport = new PropertyChangeSupport(GalleryTiles.this);
        PictureFactory.addPropertyChangeListener(this::handlePictureFactoryChanges);
        //
        init();
    }

    @Override
    public Node getNode() {
        return tilesPane;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void init() {
        //
        tiles = pictures.stream().map(s -> createSetTile(s)).collect(Collectors.toList());
        int nbColumns = 2;
        int nbRows = tiles.size() / nbColumns + 1;
        tilesPane = new FlowGridPane(nbRows, nbColumns);
        tilesPane.setHgap(16);
        tilesPane.setVgap(16);
        tilesPane.setAlignment(Pos.CENTER);
        tilesPane.setCenterShape(true);
        tilesPane.setPadding(new Insets(16));
        tilesPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        tilesPane.getChildren().setAll(tiles);
        tilesPane.setPrefSize(TILE_WIDTH * 2.5, TILE_HEIGHT * 2.5);

    }

    private Tile createSetTile(Picture picture) {
        String smallImageFilePath = ProjectConfiguration.getMiniaturesFolder() + File.separator + picture.getId() + "_small.jpg";
        File imageFile = new File(smallImageFilePath);
        InputStream imageStream;
        Image smallImage = null;
        if (imageFile.exists()) {
            try {
                imageStream = new FileInputStream(smallImageFilePath);
                smallImage = new Image(imageStream);
            } catch (FileNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            String largeImagePath = picture.getAbsolutePath();
            File pictureFile = new File(largeImagePath);
            // TODO nice excpetion handling
            String localUrl = "";
            try {
                localUrl = pictureFile.toURI().toURL().toString();
            } catch (MalformedURLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            Image largeImage = new Image(localUrl);
            Dimension largeDimension = new Dimension((int) largeImage.getWidth(), (int) largeImage.getHeight());
            Dimension d = PngExporter.getScaledDimension(largeDimension, IMAGE_DIMENSION);
            smallImage = PngExporter.resize(largeImage, d);
            PngExporter.saveImage(smallImage, smallImageFilePath);
        }

        Tile result = TileBuilder.create()
                .skinType(SkinType.IMAGE)
                .title(picture.getName())
                .image(smallImage != null ? smallImage : new Image(MainApp.class.getResourceAsStream("LegoHead.png")))
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .imageMask(Tile.ImageMask.NONE)
                .text(picture.getName())
                .backgroundColor(Color.DARKGREY)
                .foregroundBaseColor(Color.BLACK)
                .foregroundColor(Color.BLACK)
                .textAlignment(TextAlignment.CENTER)
                .textColor(Color.BLACK)
                .borderColor(Color.DARKGRAY)
                .borderWidth(2.0)
                .build();
        result.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                propertyChangeSupport.firePropertyChange(TILE_CLICKED, e, picture);
            } else if (e.getClickCount() == 2) {
                propertyChangeSupport.firePropertyChange(TILE_CLICKED, e, picture);
            }
        });
        return result;
    }

    private void handlePictureFactoryChanges(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case PictureFactory.PICTURE_ADDED -> {
                var picture = (Picture) event.getNewValue();
                if (!pictures.contains(picture)) {
                    pictures.add(picture);
                    var pictureTile = createSetTile(picture);
                    tiles.add(pictureTile);
                    tilesPane.getChildren().add(pictureTile);
                }
            }
            default ->
                throw new UnsupportedOperationException("Unsupported property changed :: " + event.getPropertyName());
        }
    }

}
