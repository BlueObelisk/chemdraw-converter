/**
 * Copyright (C) 2001 Peter Murray-Rust (pm286@cam.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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