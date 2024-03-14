package greenfoot;
import java.util.*;

import snap.geom.Ellipse;
import snap.geom.Polygon;
import snap.geom.Rect;
import snap.geom.Shape;
import snap.gfx.*;

/**
 * A custom class.
 */
public class GreenfootImage {

    // The image name
    private String _name = "GreenfootImage";

    // The encapsulated snap Image
    protected Image _image;

    // The current color
    private Color _color;

    // The current font
    private Font _font = Font.Arial12;

    // The image alpha
    private int _alpha = 255;

    // Set of actors that are using this image
    protected Set<Actor> _actors = new HashSet<>();

    // The world that is using this image
    protected World _world;

    // Loaded images
    private static Map<String, Image> _images = new HashMap<>();

    /**
     * Constructor for size.
     */
    public GreenfootImage(int aWidth, int aHeight)
    {
        _image = Image.getImageForSize(aWidth, aHeight, true);
    }

    /**
     * Constructor for file name.
     */
    public GreenfootImage(String aName)
    {
        _name = aName;
        _image = _images.get(aName);
        if (_image != null) return;
        Class cls = Greenfoot.getWorld().getClass();
        _image = Image.getImageForClassResource(cls, "images/" + aName);
        if (_image == null)
            _image = Image.getImageForClassResource(cls, aName);
        if (_image == null) {
            System.err.println("Image not found: " + aName);
            _image = Image.getImageForSize(100, 20, false);
        }

        // If image has non-standard DPI, resize to pix width/height
        if (_image.getWidth() != _image.getPixWidth()) {
            int pw = _image.getPixWidth(), ph = _image.getPixHeight();
            Image img = _image;
            _image = Image.getImageForSize(pw, ph, true);
            Painter pntr = _image.getPainter();
            pntr.drawImage(img, 0, 0, pw, ph);
            pntr.flush();
        }

        _images.put(aName, _image);
    }

    /**
     * Constructor for greenfoot image.
     */
    public GreenfootImage(GreenfootImage anImage)
    {
        this(anImage.getWidth(), anImage.getHeight());
        Painter pntr = _image.getPainter();
        pntr.drawImage(anImage._image, 0, 0);
        pntr.flush();
        imagePainted();
    }

    /**
     * Constructor for string, size, color.
     */
    public GreenfootImage(String aString, int aSize, Color fg, Color bg)
    {
        _font = _font.copyForSize(aSize);
        int sw = (int) Math.ceil(_font.getStringAdvance(aString));
        int sh = (int) Math.ceil(_font.getLineHeight());
        _image = Image.getImageForSize(sw + 8, sh + 8, true);
        if (bg.getAlpha() > 0) {
            setColor(bg);
            fill();
        }
        setColor(fg);
        drawString(aString, 4, (int) Math.round(_font.getAscent() + 4));
    }

    /**
     * Returns the image width.
     */
    public int getWidth()  { return _image.getPixWidth(); }

    /**
     * Returns the image height.
     */
    public int getHeight()  { return _image.getPixHeight(); }

    /**
     * Returns the color.
     */
    public Color getColor()  { return _color; }

    /**
     * Sets the color.
     */
    public void setColor(Color aColor)  { _color = aColor; }

    /**
     * Returns the Font.
     */
    public Font getFont()  { return _font; }

    /**
     * Sets the font.
     */
    public void setFont(Font aFont)  { _font = aFont; }

    /**
     * Fill image.
     */
    public void fill()
    {
        fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Fill rect.
     */
    public void fillRect(int x, int y, int w, int h)
    {
        fillShape(new Rect(x, y, w, h));
    }

    /**
     * Fill oval.
     */
    public void fillOval(int x, int y, int w, int h)
    {
        fillShape(new Ellipse(x, y, w, h));
    }

    /**
     * Fills a shape.
     */
    public void fillShape(Shape aShape)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color);
        pntr.fill(aShape);
        pntr.flush();
        imagePainted();
    }

    /**
     * Fill Polygon (int).
     */
    public void fillPolygon(int[] xPnts, int[] yPnts, int nPnts)
    {
        double[] pnts = new double[nPnts * 2];
        for (int i = 0; i < nPnts; i++) {
            pnts[i * 2] = xPnts[i];
            pnts[i * 2 + 1] = yPnts[i];
        }
        fillShape(new Polygon(pnts));
    }

    /**
     * Draw line.
     */
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color);
        pntr.setStrokeWidth(1);
        pntr.drawLine(x1 + .5, y1 + .5, x2 + .5, y2 + .5);
        pntr.flush();
        imagePainted();
    }

    /**
     * Draw rect.
     */
    public void drawRect(int x, int y, int w, int h)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color);
        pntr.setStrokeWidth(1);
        pntr.drawRect(x + .5, y + .5, w, h);
        pntr.flush();
        imagePainted();
    }

    /**
     * Draw oval.
     */
    public void drawOval(int x, int y, int w, int h)
    {
        drawShape(new Ellipse(x + .5, y + .5, w, h));
    }

    /**
     * Draw Polygon (int).
     */
    public void drawPolygon(int[] xPnts, int[] yPnts, int nPnts)
    {
        double[] pnts = new double[nPnts * 2];
        for (int i = 0; i < nPnts; i++) {
            pnts[i * 2] = xPnts[i];
            pnts[i * 2 + 1] = yPnts[i];
        }
        drawShape(new Polygon(pnts));
    }

    /**
     * Strokes a shape.
     */
    public void drawShape(Shape aShape)
    {
        Painter pntr = _image.getPainter();
        pntr.setAntialiasing(false);
        pntr.setColor(_color);
        pntr.draw(aShape);
        pntr.flush();
        pntr.setAntialiasing(true);
        imagePainted();
    }

    /**
     * Draw string.
     */
    public void drawString(String aString, int anX, int aY)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color);
        pntr.setFont(_font);
        pntr.drawString(aString, anX, aY);
        pntr.flush();
        imagePainted();
    }

    /**
     * Draw image.
     */
    public void drawImage(GreenfootImage anImg, int aX, int aY)
    {
        Painter pntr = _image.getPainter();
        pntr.drawImage(anImg._image, aX, aY);
        imagePainted();
    }

    /**
     * Scales the image.
     */
    public void scale(int aW, int aH)
    {
        Image img = _image;
        _image = Image.getImageForSize(aW, aH, true);
        Painter pntr = _image.getPainter();
        pntr.setImageQuality(0);
        pntr.drawImage(img, 0, 0, aW, aH);
        pntr.flush();
        pntr.setImageQuality(1);
        imageChanged();
    }

    /**
     * Flips the image so that it points left instead of right.
     */
    public void mirrorHorizontally()
    {
        Image img = _image;
        int w = getWidth(), h = getHeight();
        _image = Image.getImageForSize(w, h, true);
        Painter pntr = _image.getPainter();
        pntr.translate(w / 2, 0);
        pntr.scale(-1, 1);
        pntr.translate(-w / 2, 0);
        pntr.drawImage(img, 0, 0);
        pntr.flush();
        imageChanged();
    }

    /**
     * Flips the image so that it point down instead of up.
     */
    public void mirrorVertically()
    {
        Image img = _image;
        int w = getWidth(), h = getHeight();
        _image = Image.getImageForSize(w, h, true);
        Painter pntr = _image.getPainter();
        pntr.translate(0, h / 2);
        pntr.scale(1, -1);
        pntr.translate(0, -h / 2);
        pntr.drawImage(img, 0, 0);
        pntr.flush();
        imageChanged();
    }

    /**
     * Rotates image around center.
     */
    public void rotate(int theDeg)
    {
        Image img = _image;
        int w = getWidth(), h = getHeight();
        _image = Image.getImageForSize(w, h, true);
        Painter pntr = _image.getPainter();
        pntr.translate(w / 2, h / 2);
        pntr.rotate(theDeg);
        pntr.translate(-w / 2, -h / 2);
        pntr.drawImage(img, 0, 0);
        pntr.flush();
        imageChanged();
    }

    /**
     * Returns the color at given x/y.
     */
    public Color getColorAt(int anX, int aY)
    {
        return new Color(_image.getRGB(anX, aY));
    }

    /**
     * Sets the color at given x/y.
     */
    public void setColorAt(int aX, int aY, Color aColor)
    {
        setColor(aColor);
        fillRect(aX, aY, 1, 1);
    }

    /**
     * Returns the image opacity.
     */
    public int getTransparency()
    {
        return _alpha;
    }

    /**
     * Makes the image semi transparent.
     */
    public void setTransparency(int aValue)
    {
        _alpha = aValue;
    }

    /**
     * Clears the image.
     */
    public void clear()
    {
        Painter pntr = _image.getPainter();
        pntr.setComposite(Painter.Composite.SRC_IN);
        pntr.setColor(Color.CLEAR);
        pntr.fillRect(0, 0, getWidth(), getHeight());
        pntr.flush();
    }

    /**
     * Notifies actors of image change.
     */
    void imageChanged()
    {
        for (Actor a : _actors) a.imageChanged();
        if (_world != null) _world.repaint();
    }

    /**
     * Notifies that image was painted.
     */
    void imagePainted()
    {
        if (_world != null) _world.repaint();
    }

    /**
     * Standard toString implementation.
     */
    public String toString()
    {
        return _name + " " + getWidth() + "x" + getHeight();
    }
}