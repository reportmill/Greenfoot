package greenfoot;
import java.util.*;
import snap.gfx.*;

/**
 * A custom class.
 */
public class GreenfootImage {
    
    // The encapsulated snap Image
    Image         _image;
    
    // The current color
    Color         _color;
    
    // The current font
    Font          _font = Font.Arial12;
    
    // The image alpha
    int           _alpha = 255;
    
    // Set of actors that are using this image
    Set <Actor>   _actors = new HashSet();
    
    // Loaded images
    static Map <String,Image> _images = new HashMap();
    
/**
 * Creates a new Greenfoot image for size.
 */
public GreenfootImage(int aWidth, int aHeight)  { _image = Image.get(aWidth, aHeight, true); }

/**
 * Creates a new greenfoot image for file name.
 */
public GreenfootImage(String aName)
{
    _image = _images.get(aName); if(_image!=null) return;
    _image = Image.get(Greenfoot.getWorld().getClass(), "images/" + aName);
    if(_image==null) _image = Image.get(Greenfoot.getWorld().getClass(), aName);
    if(_image==null) System.err.println("Image not found: " + aName);
    if(_image!=null) _images.put(aName, _image);
}

/**
 * Creates a new greenfoot image for greenfoot image.
 */
public GreenfootImage(GreenfootImage anImage)
{
    this(anImage.getWidth(), anImage.getHeight());
    Painter pntr = _image.getPainter();
    pntr.drawImage(anImage._image, 0, 0); pntr.flush();
}

/**
 * Creates a new greenfoot image for greenfoot image.
 */
public GreenfootImage(String aString, int aSize, Color fg, Color bg)
{
    _font = _font.deriveFont(aSize);
    int sw = (int)Math.ceil(_font.getStringAdvance(aString)), sh = (int)Math.ceil(_font.getLineHeight());
    _image = Image.get(sw+8,sh+8,true);
    setColor(bg); fill();
    setColor(fg); drawString(aString, 4, (int)Math.round(_font.getAscent()+4));
}

/**
 * Returns the image width.
 */
public int getWidth()  { return (int)_image.getWidth(); }

/**
 * Returns the image height.
 */
public int getHeight()  { return (int)_image.getHeight(); }

/**
 * Returns the color.
 */
public Color getColor()  { return _color; }

/**
 * Sets the color.
 */
public void setColor(Color aColor)  { _color = aColor; }

/**
 * Returns the AWT Font.
 */
public Font getFont()  { return _font; }

/** Sets the font. */
public void setFont(Font aFont)  { _font = aFont; }

/** Fill image. */
public void fill()  { fillRect(0, 0, getWidth(), getHeight()); }

/** Fill rect. */
public void fillRect(int x, int y, int w, int h)  { fillShape(new Rect(x,y,w,h)); }

/** Fill oval. */
public void fillOval(int x, int y, int w, int h)  { fillShape(new Ellipse(x,y,w,h)); }

/**
 * Fills a shape.
 */
public void fillShape(Shape aShape)
{
    Painter pntr = _image.getPainter();
    pntr.setColor(_color); pntr.fill(aShape); pntr.flush();
}

/** Fill Polygon (int). */
public void fillPolygon(int xPnts[], int yPnts[], int nPnts)
{
    double pnts[] = new double[nPnts*2]; for(int i=0;i<nPnts;i++) { pnts[i*2] = xPnts[i]; pnts[i*2+1] = yPnts[i]; }
    fillShape(new Polygon(pnts));
}

/** Draw line. */
public void drawLine(int x, int y, int w, int h)  { drawShape(new Line(x,y,w,h)); }

/** Draw rect. */
public void drawRect(int x, int y, int w, int h)  { drawShape(new Rect(x,y,w,h)); }

/** Draw oval. */
public void drawOval(int x, int y, int w, int h)  { drawShape(new Ellipse(x,y,w,h)); }

/** Draw Polygon (int). */
public void drawPolygon(int xPnts[], int yPnts[], int nPnts)
{
    double pnts[] = new double[nPnts*2]; for(int i=0;i<nPnts;i++) { pnts[i*2] = xPnts[i]; pnts[i*2+1] = yPnts[i]; }
    drawShape(new Polygon(pnts));
}

/**
 * Strokes a shape.
 */
public void drawShape(java.awt.Shape aShape)  { drawShape(snap.swing.AWT.get(aShape)); }

/**
 * Strokes a shape.
 */
public void drawShape(Shape aShape)
{
    Painter pntr = _image.getPainter();
    pntr.setColor(_color); pntr.draw(aShape); pntr.flush();
}

/** Draw string. */
public void drawString(String aString, int anX, int aY)
{
    Painter pntr = _image.getPainter();
    pntr.setColor(_color); pntr.setFont(_font); pntr.drawString(aString, anX, aY); pntr.flush();
}

/** Scales the image. */
public void scale(int aW, int aH)
{
    Image img = _image;
    _image = Image.get(aW, aH, true);
    Painter pntr = _image.getPainter();
    pntr.drawImage(img, 0, 0, aW, aH); pntr.flush();
    imageChanged(img.getPixWidth(), img.getPixHeight());
}

/** Flips the image so that it points left instead of right. */
public void mirrorHorizontally()  { System.err.println("GreenfootImage.setColorAt: Not Impl"); }

/** Flips the image so that it point down instead of up. */
public void mirrorVertically()  { System.err.println("GreenfootImage.setColorAt: Not Impl"); }

/** Rotates image around center. */
public void rotate(int theDeg)  { System.err.println("GreenfootImage.setColorAt: Not Impl"); }

/** Returns the AWT color at given x/y. */
public java.awt.Color getColorAt(int anX, int aY)  { return new java.awt.Color(_image.getRGB(anX, aY)); }

/** Sets the AWT color at given x/y. */
public void setColorAt(int aX, int aY, java.awt.Color aColor)  { fillRect(aX, aY, 1, 1); }

/** Returns the image opacity. */
public int getTransparency()  { return _alpha; }

/** Makes the image semi transparent. */
public void setTransparency(int aValue)  { _alpha = aValue; }

/**
 * Clears the image.
 */
public void clear()
{
    Painter pntr = _image.getPainter();
    pntr.setComposite(Painter.Composite.SRC_IN);
    pntr.setColor(Color.CLEAR); pntr.fillRect(0,0,getWidth(),getHeight());
    pntr.flush();
}

/**
 * Notifies actors of image change.
 */
void imageChanged(int oldW, int oldH)  { for(Actor a : _actors) a.imageChanged(oldW, oldH); }

}