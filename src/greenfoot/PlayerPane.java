package greenfoot;
import snap.view.*;

/**
 * A ViewOwner for Greenfoot to create wrapper UI and show manage worlds.
 */
public class PlayerPane extends ViewOwner {

    // The current world
    private World _world;

    // The first world
    private World _firstWorld;

    // The World View
    private WorldView _worldView;

    // The box that holds the World View
    private BoxView _worldViewBox;

    // The actor pressed by last mouse
    private Actor _mouseActor;

    // The animation timer    
    private ViewTimer _timer = new ViewTimer(() -> _worldView.doAct(), 40);

    /**
     * Constructor.
     */
    public PlayerPane()
    {
        super();
    }

    /**
     * Returns the world.
     */
    public World getWorld()  { return _world; }

    /**
     * Sets the world.
     */
    public void setWorld(World aWorld)
    {
        if (aWorld == _world) return;
        if (_world == null)
            _firstWorld = aWorld;

        getUI();
        assert (_worldViewBox != null);

        _world = aWorld;
        _worldView = aWorld.getWorldView(); assert (_worldView != null);
        _worldViewBox.setContent(_worldView);
        setFirstFocus(_worldView);
        _worldView.addEventHandler(this::handleWorldViewMouseEvent, MouseEvents);
        _worldView.requestFocus();
    }

    /**
     * Starts the animation.
     */
    public void start()  { _timer.start(); }

    /**
     * Stops the animation.
     */
    public void stop()  { _timer.stop(); }

    /**
     * Whether scene is playing.
     */
    public boolean isPlaying()  { return _timer.isRunning(); }

    /**
     * Returns the frame rate.
     */
    public int getTimerPeriod()  { return _timer.getPeriod(); }

    /**
     * Sets the frame rate.
     */
    public void setTimerPeriod(int aValue)
    {
        if (aValue < 1)
            aValue = 1;
        if (aValue > 1000)
            aValue = 1000;
        _timer.setPeriod(aValue);
    }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        _worldViewBox = getView("WorldViewBox", BoxView.class);
    }

    /**
     * Reset UI.
     */
    @Override
    protected void resetUI()
    {
        setViewValue("SpeedSlider", Greenfoot.getSpeed());
    }

    /**
     * Respond to UI changes.
     */
    @Override
    protected void respondUI(ViewEvent anEvent)
    {
        switch (anEvent.getName()) {

            // Handle ActButton
            case "ActButton": _world._worldView.doAct(); break;

            // Handle RunButton
            case "RunButton":
                View runBtn = anEvent.getView();
                runBtn.setText("Pause");
                runBtn.setName("PauseButton");
                getView("ActButton").setDisabled(true);
                Greenfoot.start();
                break;

            // Handle PauseButton
            case "PauseButton":
                Greenfoot.stop();
                View pauseBtn = anEvent.getView();
                pauseBtn.setText("Run");
                pauseBtn.setName("RunButton");
                getView("ActButton").setDisabled(false);
                break;

            // Handle ResetButton
            case "ResetButton": resetWorld(); break;

            // Handle SpeedSlider
            case "SpeedSlider":
                Greenfoot.setSpeed(anEvent.getIntValue());
                break;

            // Handle FullSizeButton
            case "FullSizeButton":
                ScaleBox scaleBox = getView("ScaleBox", ScaleBox.class);
                scaleBox.setFillWidth(anEvent.getBoolValue());
                scaleBox.setFillHeight(anEvent.getBoolValue());
                break;
        }

        // Shouldn't need this
        _worldView.requestFocus();
    }

    /**
     * Reset World.
     */
    protected void resetWorld()
    {
        Greenfoot.stop();
        View pauseBtn = getView("PauseButton");
        if (pauseBtn != null) {
            pauseBtn.setText("Run");
            pauseBtn.setName("RunButton");
            getView("ActButton").setDisabled(false);
        }

        World world = null;
        try { world = _firstWorld.getClass().newInstance(); }
        catch (Exception e) { e.printStackTrace(); }
        setWorld(world);
    }

    /**
     * Handles WorldView MouseEvent.
     */
    protected void handleWorldViewMouseEvent(ViewEvent anEvent)
    {
        if (isPlaying())
            return;

        // Get x/y
        int mouseX = (int) Math.round(anEvent.getX());
        int mouseY = (int) Math.round(anEvent.getY());

        // Handle MousePress
        if (anEvent.isMousePress()) {

            // If alt or shortcut down, create new actor
            if ((anEvent.isAltDown() || anEvent.isShortcutDown()) && _mouseActor != null) {
                try {
                    _mouseActor = _mouseActor.getClass().newInstance();
                    _world.addObject(_mouseActor, mouseX, mouseY);
                }
                catch (Exception ignore) { }
            }

            // Otherwise get actor at event
            else _mouseActor = _world.getActorAt(null, mouseX, mouseY, null);
        }

        // Handle MouseDrag
        if (anEvent.isMouseDrag() && _mouseActor != null)
            _mouseActor.setLocation(mouseX, mouseY);
    }
}