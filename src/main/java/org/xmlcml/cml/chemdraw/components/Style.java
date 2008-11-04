package org.xmlcml.cml.chemdraw.components;

public class Style {

    String stroke = "blue";
    String fill = "none";
    String opacity = "0.8";
    double width = 0.5;

    /**
     */
    public Style() {
    }

    /**
     * @param style
     */
    public Style(Style style) {
        this.stroke = style.stroke;
        this.fill = style.fill;
        this.opacity = style.opacity;
        this.width = style.width;
    }

    /**
     * @return string
     */
    public String toString() {
        return
            "fill:"+fill+";"+
            "stroke:"+stroke+";"+
            "opacity:"+opacity+";"+
            "stroke-width:"+width+";";
    }
}