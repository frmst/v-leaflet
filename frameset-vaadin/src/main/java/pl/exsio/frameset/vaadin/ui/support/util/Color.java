/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.frameset.vaadin.ui.support.util;

/**
 *
 * @author exsio
 */
public class Color extends com.vaadin.shared.ui.colorpicker.Color {

    public Color(int red, int green, int blue) {
        super(red, green, blue);
    }

    public Color(int red, int green, int blue, int alpha) {
        super(red, green, blue, alpha);
    }

    public Color(int rgb) {
        super(rgb);
    }

    public static Color colorFromHex(String hex) {
        try {
            int[] ret = new int[3];
            for (int i = 0; i < 3; i++) {

                String str = hex.substring((i * 2) + 1, (i * 2) + 3);
                ret[i] = Integer.parseInt(str, 16);

            }
            return new Color(ret[0], ret[1], ret[2]);
        } catch(NullPointerException ex) {
            return new Color(0,0,0);
        }

    }

    public String hexString() {

        String hex = String.format("#%02x%02x%02x", this.getRed(), this.getGreen(), this.getBlue());
        return hex;
    }

}
