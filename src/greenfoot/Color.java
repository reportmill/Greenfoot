/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2016,2017  Poul Henriksen and Michael Kolling
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package greenfoot;

/**
 * Greenfoot Color.
 */
public class Color
{
    // Common colors
    public final static Color WHITE = new Color(255, 255, 255);
    public final static Color LIGHT_GRAY = new Color(192, 192, 192);
    public final static Color GRAY = new Color(128, 128, 128);
    public final static Color DARK_GRAY = new Color(64, 64, 64);
    public final static Color BLACK = new Color(0, 0, 0);
    public final static Color RED = new Color(255, 0, 0);
    public final static Color PINK = new Color(255, 175, 175);
    public final static Color ORANGE = new Color(255, 200, 0);
    public final static Color YELLOW = new Color(255, 255, 0);
    public final static Color GREEN = new Color(0, 255, 0);
    public final static Color MAGENTA = new Color(255, 0, 255);
    public final static Color CYAN = new Color(0, 255, 255);
    public final static Color BLUE = new Color(0, 0, 255);
    private final snap.gfx.Color color;

    /**
     * Constructor.
     */
    Color(snap.gfx.Color c)
    {
        this.color = c;
    }

    /**
     * Constructor.
     */
    public Color(int r, int g, int b)
    {
        this.color = new snap.gfx.Color(r, g, b);
    }

    /**
     * Constructor.
     */
    public Color(int r, int g, int b, int a)
    {
        this.color = new snap.gfx.Color(r, g, b, a);
    }

    /**
     * Return red component.
     */
    public int getRed()  { return this.color.getRedInt(); }

    /**
     * Return green component.
     */
    public int getGreen()  { return this.color.getGreenInt(); }

    /**
     * Return blue component.
     */
    public int getBlue()  { return this.color.getBlueInt(); }

    /**
     * Return alpha component.
     */
    public int getAlpha()  { return this.color.getAlphaInt(); }

    /**
     * Returns a brighter color.
     */
    public Color brighter()  { return new Color(this.color.brighter()); }

    /**
     * Returns a darker color.
     */
    public Color darker()  { return new Color(this.color.darker()); }

    /**
     * Standard equals implementation.
     */
    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Color && ((Color) obj).getColorObject().equals(this.color);
    }

    /**
     * Standard hashCode implementation.
     */
    public int hashCode()  { return this.color.hashCode(); }

    /**
     * Standard toString implementation.
     */
    @Override
    public String toString()  { return "Color{" + "color=" + color + '}'; }

    /**
     * Returns the SnapColor.
     */
    snap.gfx.Color getColorObject()  { return this.color; }
}
