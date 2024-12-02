/*
 This file is part of the Greenfoot program. 
 Copyright (C) 2016  Poul Henriksen and Michael Kolling
 
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
 * Greenfoot Font.
 */
public class Font
{
    // Snap font
    private snap.gfx.Font font;

    /**
     * Constructor.
     */
    Font(snap.gfx.Font font)
    {
        this.font = font;
    }

    /**
     * Constructor.
     */
    public Font(String name, boolean bold, boolean italic, int size)
    {
        this.font = new snap.gfx.Font(name, size);
        if (bold)
            this.font = this.font.getBold();
        if (italic)
            this.font = this.font.getItalic();
    }

    /**
     * Constructor.
     */
    public Font(boolean bold, boolean italic, int size)
    {
        this("SansSerif", bold, italic, size);
    }

    /**
     * Constructor.
     */
    public Font(String name, int size)
    {
        this(name, false, false, size);
    }

    /**
     * Constructor.
     */
    public Font(int size)
    {
        this(false, false, size);
    }

    /**
     * Returns whether font is plain.
     */
    public boolean isPlain()  { return !(isBold() || isItalic()); }

    /**
     * Returns whether font is bold.
     */
    public boolean isBold()  { return this.font.isBold(); }

    /**
     * Returns whether font is italic.
     */
    public boolean isItalic()  { return this.font.isItalic(); }

    /**
     * Returns font name..
     */
    public String getName()  { return this.font.getName(); }

    /**
     * Returns font size.
     */
    public int getSize()  { return (int) this.font.getSize(); }

    /**
     * Returns font at new size.
     */
    public Font deriveFont(float size)  { return new Font(font.copyForSize(size)); }

    /**
     * Standard equals implementation.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        return obj instanceof Font && ((Font) obj).font.equals(this.font);
    }

    /**
     * Standard hashcode implementation.
     */
    @Override
    public int hashCode()  { return font.hashCode(); }

    /**
     * Standard toString implementation.
     */
    @Override
    public String toString()  { return "Font{" + "font=" + font + '}'; }

    /**
     * Return Snap font.
     */
    snap.gfx.Font getFontObject()
    {
        return this.font;
    }
}
