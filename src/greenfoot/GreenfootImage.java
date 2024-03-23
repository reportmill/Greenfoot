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
    private Font _font = new Font("Arial", false, false, 12); //Font.Arial12;

    // The image alpha
    private int _alpha = 255;

    // Set of actors that are using this image
    protected Set<Actor> _actors = new HashSet<>();

    // The world that is using this image
    protected World _world;

    // Loaded images
    private static Map<String, Image> _images = new HashMap<>();

    // Shared image
    protected static GreenfootImage SHARED = new GreenfootImage(48, 48);

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
        if (_image != null)
            return;

        Class<?> cls = Greenfoot.getWorld().getClass();
        _image = Image.getImageForClassResource(cls, "images/" + aName);
        if (_image == null)
            _image = Image.getImageForClassResource(cls, aName);
        if (_image == null) {
            System.err.println("Image not found: " + aName);
            _image = Image.getImageForSize(100, 20, false);
        }

        // Wait for image load, since GF apps regularly use image info (or do image transform) immediately after loading
        if (!_image.isLoaded())
            _image.waitForImageLoad();

        // If image has non-standard DPI, resize to pix width/height
        if (_image.getWidth() != _image.getPixWidth()) {
            int pixW = _image.getPixWidth();
            int pixH = _image.getPixHeight();
            _image = _image.cloneForSizeAndDpiScale(pixW, pixH, 1);
        }

        _images.put(aName, _image);
    }

    /**
     * Constructor for greenfoot image.
     */
    public GreenfootImage(GreenfootImage anImage)
    {
        _image = anImage._image.cloneForScale(1);
    }

    /**
     * Constructor for string, size, color.
     */
    public GreenfootImage(String aString, int fontSize, Color foregroundColor, Color backgroundColor)
    {
        this(aString, fontSize, foregroundColor, backgroundColor, null);
    }

    /**
     * Constructor for string, size, color.
     */
    public GreenfootImage(String aString, int fontSize, Color foregroundColor, Color bakcgroundColor, Color lineColor)
    {
        // Create image
        _font = _font.deriveFont(fontSize);
        int strW = (int) Math.ceil(_font.getFontObject().getStringAdvance(aString));
        int strH = (int) Math.ceil(_font.getFontObject().getLineHeight());
        _image = Image.getImageForSize(strW + 8, strH + 8, true);

        // Fill background color
        if (bakcgroundColor != null && bakcgroundColor.getAlpha() > 0) {
            setColor(bakcgroundColor);
            fill();
        }

        // Draw string
        setColor(foregroundColor);
        drawString(aString, 4, (int) Math.round(_font.getFontObject().getAscent() + 4));
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
        pntr.setColor(_color.getColorObject());
        pntr.fill(aShape);
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
        pntr.setColor(_color.getColorObject());
        pntr.setStrokeWidth(1);
        pntr.drawLine(x1 + .5, y1 + .5, x2 + .5, y2 + .5);
        imagePainted();
    }

    /**
     * Draw rect.
     */
    public void drawRect(int x, int y, int w, int h)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color.getColorObject());
        pntr.setStrokeWidth(1);
        pntr.drawRect(x + .5, y + .5, w, h);
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
        pntr.setColor(_color.getColorObject());
        pntr.draw(aShape);
        pntr.setAntialiasing(true);
        imagePainted();
    }

    /**
     * Draw string.
     */
    public void drawString(String aString, int anX, int aY)
    {
        Painter pntr = _image.getPainter();
        pntr.setColor(_color.getColorObject());
        pntr.setFont(_font.getFontObject());
        pntr.drawString(aString, anX, aY);
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
        imageChanged();
    }

    /**
     * Returns the color at given x/y.
     */
    public Color getColorAt(int anX, int aY)
    {
        return new Color(new snap.gfx.Color(_image.getRGB(anX, aY)));
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
        pntr.setColor(snap.gfx.Color.CLEAR);
        pntr.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Notifies actors of image change.
     */
    void imageChanged()
    {
        for (Actor actor : _actors)
            actor.imageChanged();
        if (_world != null)
            _world.repaint();
    }

    /**
     * Notifies that image was painted.
     */
    void imagePainted()
    {
        if (_world != null)
            _world.repaint();
    }

    /**
     * Standard toString implementation.
     */
    public String toString()
    {
        return _name + " " + getWidth() + "x" + getHeight();
    }
}