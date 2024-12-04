package greenfoot;

/**
 * MouseInfo.
 */
public class MouseInfo {

    // The last mouse x/y
    protected double _mouseX, _mouseY;

    /**
     * Constructor.
     */
    public MouseInfo()
    {

    }

    public int getX()  { return (int) _mouseX; }

    public int getY()  { return (int) _mouseY; }
}