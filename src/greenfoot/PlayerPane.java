package greenfoot;
import snap.gfx.Color;
import snap.props.PropChange;
import snap.view.*;

/**
 * A ViewOwner for Greenfoot to create wrapper UI and show manage worlds.
 */
public class PlayerPane extends ViewOwner {

    // The Greenfoot Env
    private GreenfootEnv _greenfootEnv;

    // The current world
    private World _world;

    // The World View
    private WorldView _worldView;

    // The box that holds the World View
    private BoxView _worldViewBox;

    // The ClassesPane
    private ClassesPane _classesPane;

    // The actor pressed by last mouse
    private Actor _mouseActor;

    // The animation timer    
    private ViewTimer _timer = new ViewTimer(() -> _worldView.doAct(), 40);

    /**
     * Constructor.
     */
    public PlayerPane(GreenfootEnv greenfootEnv)
    {
        super();
        _greenfootEnv = greenfootEnv;
        _classesPane = new ClassesPane();
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

        _world = aWorld;
        _worldView = aWorld.getWorldView();

        // Set in UI
        getUI();
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
     * Returns the classes pane.
     */
    public ClassesPane getClassesPane()  { return _classesPane; }

    /**
     * Returns whether player is showing ClassesPane.
     */
    public boolean isShowClasses()  { return _classesPane.isUISet() && _classesPane.getUI().getParent() != null; }

    /**
     * Sets whether player is showing ClassesPane.
     */
    public void setShowClasses(boolean aValue)
    {
        if (aValue == isShowClasses()) return;

        SplitView splitView = getUI(SplitView.class);
        if (aValue)
            splitView.addItem(_classesPane.getUI());
        else splitView.removeItem(_classesPane.getUI());
    }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        _worldViewBox = getView("WorldViewBox", BoxView.class);
        Label buildingLabel = (Label) _worldViewBox.getContent();
        buildingLabel.setTextColor(Color.get("#E0"));
    }

    /**
     * Override to show classes if available.
     */
    @Override
    protected void initShowing()
    {
        GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject();
        ClassNode rootClassNode = greenfootProject.getRootClassNode();
        setShowClasses(!rootClassNode.getChildNodes().isEmpty());
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
        _greenfootEnv.resetWorldToDefault();

        View pauseBtn = getView("PauseButton");
        if (pauseBtn != null) {
            pauseBtn.setText("Run");
            pauseBtn.setName("RunButton");
            getView("ActButton").setDisabled(false);
        }
    }

    /**
     * Called when GreenfootProject has prop change.
     */
    protected void handleGreenfootProjectRootClassNodeChange(PropChange propChange)
    {
        // Handle RootClassNode
        if (propChange.getPropName() == GreenfootProject.RootClassNode_Prop) {
            GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject();
            ClassNode rootClassNode = greenfootProject.getRootClassNode();
            setShowClasses(!rootClassNode.getChildNodes().isEmpty());
            if (isShowClasses())
                _classesPane.resetClassTree();
        }
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