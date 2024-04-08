package greenfoot;
import snap.geom.HPos;
import snap.geom.Pos;
import snap.gfx.Color;
import snap.view.*;

/**
 * A ViewOwner for Greenfoot to create wrapper UI and show manage worlds.
 */
public class GreenfootOwner extends ViewOwner {

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

    // The shared GreenfootOwner
    private static GreenfootOwner _shared;

    /**
     * Constructor for given World.
     */
    public GreenfootOwner(World aWorld)
    {
        setWorld(aWorld);
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
        _world = aWorld;
        _worldView = aWorld.getWorldView();
        _worldViewBox.setContent(_worldView);
        setFirstFocus(_worldView);
        enableEvents(_worldView, MouseEvents);
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
     * Create UI.
     */
    protected View createUI()
    {
        // Add ActButton
        Button actButton = new Button("Act");
        actButton.setName("ActButton");
        actButton.setPrefSize(70, 20);

        // Add RunButton
        Button runButton = new Button("Run");
        runButton.setName("RunButton");
        runButton.setPrefSize(70, 20);

        // Add ResetButton
        Button resetButton = new Button("Reset");
        resetButton.setName("ResetButton");
        resetButton.setPrefSize(70, 20);

        // Add separator
        Separator sep = new Separator();
        sep.setPrefWidth(40);
        sep.setVisible(false);

        // Add Speed label and slider
        Label speedLabel = new Label("Speed:");
        speedLabel.setLeanX(HPos.CENTER);
        speedLabel.setFont(snap.gfx.Font.Arial14);
        Slider speedSlider = new Slider();
        speedSlider.setName("SpeedSlider");
        speedSlider.setPrefWidth(180);

        // Create toolbar
        RowView toolBar = new RowView();
        toolBar.setAlign(Pos.CENTER);
        toolBar.setPadding(18, 25, 18, 25);
        toolBar.setSpacing(15);
        toolBar.setChildren(actButton, runButton, resetButton, sep, speedLabel, speedSlider);

        // Configure World View
        _worldViewBox = new BoxView();
        _worldViewBox.setPadding(8, 8, 8, 8);
        ScaleBox scaleBox = new ScaleBox(_worldViewBox, true, true);
        scaleBox.setGrowHeight(true);

        // Create col view and add world, toolBar
        ColView colView = new ColView();
        colView.setFillWidth(true);
        colView.setFont(snap.gfx.Font.Arial12);
        colView.setBorder(Color.GRAY, 1);
        colView.setChildren(scaleBox, toolBar);

        // Return
        return colView;
    }

    /**
     * Reset UI.
     */
    protected void resetUI()
    {
        setViewValue("SpeedSlider", Greenfoot.getSpeed() / 100f);
    }

    /**
     * Respond to UI changes.
     */
    protected void respondUI(ViewEvent anEvent)
    {
        // Handle MouseEvent on WorldView
        if (anEvent.isMouseEvent() && !isPlaying())
            handleMouseEvent(anEvent);

        // Handle ActButton
        if (anEvent.equals("ActButton"))
            _world._worldView.doAct();

        // Handle RunButton
        if (anEvent.equals("RunButton")) {
            View runBtn = anEvent.getView();
            runBtn.setText("Pause");
            runBtn.setName("PauseButton");
            getView("ActButton").setDisabled(true);
            Greenfoot.start();
        }

        // Handle PauseButton
        if (anEvent.equals("PauseButton")) {
            Greenfoot.stop();
            View pauseBtn = anEvent.getView();
            pauseBtn.setText("Run");
            pauseBtn.setName("RunButton");
            getView("ActButton").setDisabled(false);
        }

        // Handle ResetButton
        if (anEvent.equals("ResetButton"))
            resetWorld();

        // Handle SpeedSlider
        if (anEvent.equals("SpeedSlider")) {
            int val = Math.round(anEvent.getFloatValue() * 100);
            Greenfoot.setSpeed(val);
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
        catch (Exception e) { new RuntimeException(e); }
        setWorld(world);
    }

    /**
     * Handles MouseEvent.
     */
    protected void handleMouseEvent(ViewEvent anEvent)
    {
        // Get x/y
        int mouseX = (int) Math.round(anEvent.getX());
        int mouseY = (int) Math.round(anEvent.getY());

        // Handle MousePressed
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

        // Handle MouseDraggged
        if (anEvent.isMouseDrag() && _mouseActor != null)
            _mouseActor.setLocation(mouseX, mouseY);
    }

    /**
     * Returns the shared GreenfootOwner.
     */
    public static GreenfootOwner getShared()
    {
        if (_shared != null) return _shared;
        return _shared = new GreenfootOwner(null);
    }
}