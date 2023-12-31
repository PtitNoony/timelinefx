/**
 *
 * @author Zunayed Hassan
 * @email zunayedhassan@appiomatic.com
 */
package com.appiomatic.SvgExporter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * SvgExporter: Converts JavaFX to SVG
 *
 * Version 1.0
 *
 * Date: 18th August, 2018
 *
 */
public class SvgExporter {

    private static final Logger LOG = Logger.getGlobal();

    private static final ArrayList<String> TEMP_FILE_LIST = new ArrayList<>();

    private static double canvasX = 0;
    private static double canvasY = 0;
    private static String outputFileName = "";

    private SvgExporter() {
        // utility constructor
    }

    public static void export(@NotNull Node srcPane, String anOutputFileName) {
        TEMP_FILE_LIST.clear();
        Bounds canvasBounds = srcPane.localToScene(srcPane.getBoundsInLocal());
        canvasX = canvasBounds.getMinX();
        canvasY = canvasBounds.getMinY();
        outputFileName = anOutputFileName;

        File outputFile = new File(outputFileName);

        try {
            Document document = ((DocumentBuilderFactory.newInstance()).newDocumentBuilder()).newDocument();

            Element svgElement = document.createElement("svg");

            svgElement.setAttribute("version", "1.1");
            svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            svgElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
            document.appendChild(svgElement);

            Element mainGroup = document.createElement("g");
            svgElement.appendChild(mainGroup);

            readElement(srcPane, document, mainGroup);

            svgElement.setAttribute("width", canvasBounds.getWidth() + "px");
            svgElement.setAttribute("height", canvasBounds.getHeight() + "px");

            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException parserConfigurationException) {
            LOG.log(Level.SEVERE, "Exception while exporting to file {0} :: {1}", new Object[]{outputFileName, parserConfigurationException.getMessage()});
        }

        TEMP_FILE_LIST.stream().map(File::new).filter(File::exists).forEachOrdered(File::delete);
    }

    private static void readElement(Object object, Document document, Element element) {
        if (object instanceof Pane pane) {
            pane.getChildren().forEach(node -> readElement(node, document, element));
        } else if ((object instanceof Circle circleShape) && (((Circle) object).getOpacity() != 0)) {

            Bounds bounds = circleShape.localToScene(circleShape.getBoundsInLocal());

            String cx = Double.toString(bounds.getMinX() + circleShape.getRadius() - canvasX);
            String cy = Double.toString(bounds.getMinY() + circleShape.getRadius() - canvasY);
            String r = Double.toString(circleShape.getRadius());
            String fill = getHexColorCodeFromFxColor((Color) circleShape.getFill());
            String stroke = getHexColorCodeFromFxColor((Color) circleShape.getStroke());
            String strokeWidth = Double.toString(circleShape.getStrokeWidth());
            String fillOpacity = Double.toString(((Color) circleShape.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) circleShape.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(circleShape.getStrokeDashArray());

            Element circle = document.createElement("ellipse");
            circle.setAttribute("cx", cx);
            circle.setAttribute("cy", cy);
            circle.setAttribute("rx", r);
            circle.setAttribute("ry", r);
            circle.setAttribute("fill", fill);
            circle.setAttribute("stroke", stroke);
            circle.setAttribute("stroke-width", strokeWidth);
            circle.setAttribute("fill-opacity", fillOpacity);
            circle.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                circle.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(circle);
        } else if ((object instanceof Ellipse ellipseShape) && (((Ellipse) object).getOpacity() != 0)) {

            Bounds bounds = ellipseShape.localToScene(ellipseShape.getBoundsInLocal());

            String cx = Double.toString(bounds.getMinX() + ellipseShape.getRadiusX() - canvasX);
            String cy = Double.toString(bounds.getMinY() + ellipseShape.getRadiusY() - canvasY);
            String rx = Double.toString(ellipseShape.getRadiusX());
            String ry = Double.toString(ellipseShape.getRadiusY());
            String fill = getHexColorCodeFromFxColor((Color) ellipseShape.getFill());
            String stroke = getHexColorCodeFromFxColor((Color) ellipseShape.getStroke());
            String strokeWidth = Double.toString(ellipseShape.getStrokeWidth());
            String fillOpacity = Double.toString(((Color) ellipseShape.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) ellipseShape.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(ellipseShape.getStrokeDashArray());

            Element ellipse = document.createElement("ellipse");
            ellipse.setAttribute("cx", cx);
            ellipse.setAttribute("cy", cy);
            ellipse.setAttribute("rx", rx);
            ellipse.setAttribute("ry", ry);
            ellipse.setAttribute("fill", fill);
            ellipse.setAttribute("stroke", stroke);
            ellipse.setAttribute("stroke-width", strokeWidth);
            ellipse.setAttribute("fill-opacity", fillOpacity);
            ellipse.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                ellipse.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(ellipse);
        } else if ((object instanceof Rectangle rectangleShape) && (((Rectangle) object).getOpacity() != 0)) {

            Bounds rectangleSceneBounds = rectangleShape.localToScene(rectangleShape.getBoundsInLocal());

            String x = Double.toString(rectangleSceneBounds.getMinX() - canvasX);
            String y = Double.toString(rectangleSceneBounds.getMinY() - canvasY);
            String ry = Double.toString(rectangleShape.getArcHeight());
            String width = Double.toString(rectangleShape.getWidth());
            String height = Double.toString(rectangleShape.getHeight());
            String stroke = getHexColorCodeFromFxColor((Color) rectangleShape.getStroke());
            String strokeWidth = Double.toString(rectangleShape.getStrokeWidth());
            String fill = getHexColorCodeFromFxColor((Color) rectangleShape.getFill());
            String fillOpacity = Double.toString(((Color) rectangleShape.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) rectangleShape.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(rectangleShape.getStrokeDashArray());

            Element rectangle = document.createElement("rect");
            rectangle.setAttribute("x", x);
            rectangle.setAttribute("y", y);
            rectangle.setAttribute("ry", ry);
            rectangle.setAttribute("width", width);
            rectangle.setAttribute("height", height);
            rectangle.setAttribute("fill", fill);
            rectangle.setAttribute("stroke", stroke);
            rectangle.setAttribute("stroke-width", strokeWidth);
            rectangle.setAttribute("fill-opacity", fillOpacity);
            rectangle.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                rectangle.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(rectangle);
        } else if ((object instanceof Polygon) && (((Polygon) object).getOpacity() != 0)) {
            Polygon polygonShape = (Polygon) object;
            Bounds bounds = polygonShape.localToScene(polygonShape.getBoundsInLocal());

            String d = "M ";
            double minX = Integer.MAX_VALUE;
            double minY = Integer.MAX_VALUE;

            for (int i = 0; i < polygonShape.getPoints().size(); i += 2) {
                minX = Math.min(minX, polygonShape.getPoints().get(i));
                minY = Math.min(minY, polygonShape.getPoints().get(i + 1));
            }

            for (int i = 0; i < polygonShape.getPoints().size(); i += 2) {
                d += (polygonShape.getPoints().get(i) + bounds.getMinX() - canvasX - minX) + "," + (polygonShape.getPoints().get(i + 1) + bounds.getMinY() - canvasY - minY) + " ";
            }

            d += "Z";

            String fill = getHexColorCodeFromFxColor((Color) polygonShape.getFill());
            String stroke = getHexColorCodeFromFxColor((Color) polygonShape.getStroke());
            String strokeWidth = Double.toString(polygonShape.getStrokeWidth());
            String fillOpacity = Double.toString(((Color) polygonShape.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) polygonShape.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(polygonShape.getStrokeDashArray());

            Element path = document.createElement("path");
            path.setAttribute("d", d);
            path.setAttribute("fill", fill);
            path.setAttribute("stroke", stroke);
            path.setAttribute("stroke-width", strokeWidth);
            path.setAttribute("fill-opacity", fillOpacity);
            path.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                path.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(path);
        } else if ((object instanceof Polyline polylineShape) && (((Polyline) object).getOpacity() != 0)) {
            Bounds bounds = polylineShape.localToScene(polylineShape.getBoundsInLocal());

            String points = "";
            double minX = Integer.MAX_VALUE;
            double minY = Integer.MAX_VALUE;

            for (int i = 0; i < polylineShape.getPoints().size(); i += 2) {
                minX = Math.min(minX, polylineShape.getPoints().get(i));
                minY = Math.min(minY, polylineShape.getPoints().get(i + 1));
            }

            for (int i = 0; i < polylineShape.getPoints().size(); i += 2) {
                points += (polylineShape.getPoints().get(i) + bounds.getMinX() - canvasX - minX) + "," + (polylineShape.getPoints().get(i + 1) + bounds.getMinY() - canvasY - minY) + " ";
            }

            String fill = getHexColorCodeFromFxColor((Color) polylineShape.getFill());
            String stroke = getHexColorCodeFromFxColor((Color) polylineShape.getStroke());
            String strokeWidth = Double.toString(polylineShape.getStrokeWidth());
            String fillOpacity = Double.toString(((Color) polylineShape.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) polylineShape.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(polylineShape.getStrokeDashArray());

            Element path = document.createElement("polyline");
            path.setAttribute("points", points);
            path.setAttribute("fill", fill);
            path.setAttribute("stroke", stroke);
            path.setAttribute("stroke-width", strokeWidth);
            path.setAttribute("fill-opacity", fillOpacity);
            path.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                path.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(path);
        } else if ((object instanceof Path) && (((Path) object).getOpacity() != 0)) {
            Path starPath = (Path) object;
            Bounds bounds = starPath.localToScene(starPath.getBoundsInLocal());

            String d = "";
            double x = bounds.getMinX() - canvasX;
            double y = bounds.getMinY() - canvasY;

            for (PathElement pathElement : starPath.getElements()) {
                if (pathElement instanceof MoveTo moveTo) {
                    d += "M " + (moveTo.getX() + x) + ", " + (moveTo.getY() + y) + " ";
                } else if (pathElement instanceof LineTo lineTo) {
                    d += "L " + (lineTo.getX() + x) + ", " + (lineTo.getY() + y) + " ";
                } else if (pathElement instanceof ClosePath) {
                    d += "Z";
                } else if (pathElement != null) {
                    QuadCurveTo quadCurveTo = (QuadCurveTo) pathElement;
                    double controlX = (quadCurveTo.getControlX() + x);
                    double controlY = (quadCurveTo.getControlY() + y);
                    double cx = (quadCurveTo.getX() + x);
                    double cy = (quadCurveTo.getY() + y);

                    d += "Q " + controlX + ", " + controlY + ", " + cx + ", " + cy + " ";
                }
            }

            String fill = getHexColorCodeFromFxColor((Color) starPath.getFill());
            String stroke = getHexColorCodeFromFxColor((Color) starPath.getStroke());
            String strokeWidth = Double.toString(starPath.getStrokeWidth());
            String fillOpacity = Double.toString(((Color) starPath.getFill()).getOpacity());
            String strokeOpacity = Double.toString(((Color) starPath.getStroke()).getOpacity());
            String strokeDashArray = getStringFromArray(starPath.getStrokeDashArray());

            Element path = document.createElement("path");
            path.setAttribute("d", d);
            path.setAttribute("fill", fill);
            path.setAttribute("stroke", stroke);
            path.setAttribute("stroke-width", strokeWidth);
            path.setAttribute("fill-opacity", fillOpacity);
            path.setAttribute("stroke-opacity", strokeOpacity);

            if (!strokeDashArray.trim().equals("")) {
                path.setAttribute("stroke-dasharray", strokeDashArray);
            }

            element.appendChild(path);
        } else if ((object instanceof Text) && (((Text) object).getOpacity() != 0)) {
            Text text = (Text) object;
            Bounds textSceneBounds = text.localToScene(text.getBoundsInLocal());

            double pivotX = textSceneBounds.getMinX() + (textSceneBounds.getWidth() / 2.0) - canvasX;
            double pivotY = textSceneBounds.getMinY() + (textSceneBounds.getHeight() / 2.0) - canvasY;

            String textContent = text.getText();
            String x = Double.toString(textSceneBounds.getMinX() - canvasX);
            String y = Double.toString(textSceneBounds.getMaxY() - canvasY - 3);
            String transform = "rotate(" + text.getRotate() + ", " + pivotX + ", " + pivotY + ")";
            String fontSize = Double.toString(text.getFont().getSize()) + "px";
            String fontFamily = text.getFont().getFamily();
            String fill = getHexColorCodeFromFxColor((Color) text.getFill());
            String fontWeight = text.getFont().getStyle().toLowerCase().contains("bold") ? "bold" : "normal";
            String fontStyle = text.getFont().getStyle().toLowerCase().contains("italic") ? "italic" : "normal";
            String textDecoration = text.isUnderline() ? "underline" : "none";

            Element textElement = document.createElement("text");
            textElement.setAttribute("x", x);
            textElement.setAttribute("y", y);
            textElement.setAttribute("transform", transform);
            textElement.appendChild(document.createTextNode(textContent));
            textElement.setAttribute("font-size", fontSize);
            textElement.setAttribute("font-family", fontFamily);
            textElement.setAttribute("fill", fill);
            textElement.setAttribute("font-weight", fontWeight);
            textElement.setAttribute("font-style", fontStyle);
            textElement.setAttribute("text-decoration", textDecoration);

            element.appendChild(textElement);
        } else if ((object instanceof ImageView) && (((ImageView) object).getOpacity() != 0)) {
            ImageView imageView = (ImageView) object;
            Bounds bounds = imageView.localToScene(imageView.getBoundsInLocal());

            String imagePath = new File(outputFileName).getParent() + "/" + UUID.randomUUID().toString() + ".png";
            TEMP_FILE_LIST.add(imagePath);

            if (imageView.getImage() != null) {
                writeImageToFile(imageView.getImage(), imagePath);

                String pivotX = Double.toString(bounds.getMinX() + (bounds.getWidth() / 2.0) - canvasX);
                String pivotY = Double.toString(bounds.getMinY() + (bounds.getHeight() / 2.0) - canvasY);

                String xlinkHref = "data:image/png;base64," + getBase64StringFromImage(imagePath);
                String x = Double.toString(bounds.getMinX() - canvasX);
                String y = Double.toString(bounds.getMinY() - canvasY);
                String preserveAspectRatio = "none";
                String width = Double.toString(imageView.getBoundsInLocal().getWidth());
                String height = Double.toString(imageView.getBoundsInLocal().getHeight());
                String transform = "rotate(" + imageView.getRotate() + ", " + pivotX + ", " + pivotY + ")";

                Element imageElement = document.createElement("image");
                imageElement.setAttribute("xlink:href", xlinkHref);
                imageElement.setAttribute("x", x);
                imageElement.setAttribute("y", y);
                imageElement.setAttribute("preserveAspectRatio", preserveAspectRatio);
                imageElement.setAttribute("width", width);
                imageElement.setAttribute("height", height);
                imageElement.setAttribute("transform", transform);

                element.appendChild(imageElement);
            }
        }
    }

    public static String getHexColorCodeFromFxColor(Color color) {
        return ("#" + color.toString().substring(2, color.toString().length() - 2));
    }

    public static String getStringFromArray(ObservableList<Double> points) {
        String output = "";
        for (int i = 0; i < points.size(); i++) {
            output += points.get(i);
            if (i != points.size() - 1) {
                output += ",";
            }
        }
        return output;
    }

    public static String getBase64StringFromImage(String srcPath) {
        String output = "";
        java.nio.file.Path path = Paths.get(srcPath);
        try {
            byte[] bytes = Files.readAllBytes(path);
            output = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException exception) {
            LOG.log(Level.SEVERE, "Exception while getting base64 string from image {0} :: {1}", new Object[]{srcPath, exception.getMessage()});
        }

        return output;
    }

    public static void writeImageToFile(Image image, String outputName) {
        try {
            BufferedImage outputImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(outputImage, "png", new File(outputName));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception while writting image to file {0} :: {1}", new Object[]{outputName, ex.getMessage()});
        }
    }
}
