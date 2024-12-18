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

    /**
     * Constructor.
     */
    public PlayerPane(GreenfootEnv greenfootEnv)
    {
        super();
        _greenfootEnv = greenfootEnv;
        _classesPane = new ClassesPane(greenfootEnv);
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
     * Returns the world view box.
     */
    public View getWorldViewBox()  { return _worldViewBox; }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        _worldViewBox = getView("WorldViewBox", BoxView.class);
        Label buildingLabel = (Label) _worldViewBox.getContent();
        buildingLabel.setTextColor(Color.get("#E0"));
        _worldViewBox.addEventHandler(this::handleWorldViewBoxDragEvent, DragEvents);
    }

    /**
     * Override to show classes if available.
     */
    @Override
    protected void initShowing()
    {
        GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject();
        ClassNode rootClassNode = greenfootProject.getRootClassNode();
        if (rootClassNode != null && !rootClassNode.getChildNodes().isEmpty())
            setShowClasses(true);
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
            case "ActButton": _greenfootEnv.act(); break;

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
     * Handles WorldView MouseEvent.
     */
    protected void handleWorldViewMouseEvent(ViewEvent anEvent)
    {
        if (_greenfootEnv.isPlaying())
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

    /**
     * Called when WorldViewBox gets drag event.
     */
    private void handleWorldViewBoxDragEvent(ViewEvent anEvent)
    {
        if (anEvent.isDragDrop())
            handleWorldViewBoxDragDropEvent(anEvent);

        // Just accept all drags for now
        else anEvent.acceptDrag();
    }

    /**
     * Called when WorldViewBox gets drag drop event.
     */
    private void handleWorldViewBoxDragDropEvent(ViewEvent anEvent)
    {
        // If clipboard not loaded, come back
        Clipboard clipboard = anEvent.getClipboard();
        if (!clipboard.isLoaded()) {
            clipboard.addLoadListener(() -> handleWorldViewBoxDragDropEvent(anEvent));
            return;
        }

        // Get drag string - just return if not from classes tree
        String dragString = clipboard.hasString() ? clipboard.getString() : null;
        if (dragString == null || !dragString.startsWith("Drag:"))
            return;

        // Get drag class - just return if not found
        String className = dragString.substring("Drag:".length());
        GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject();
        Class<?> dragClass = greenfootProject.getClassForName(className);
        if (dragClass == null)
            return;

        // Add instance for class
        boolean didAdd = addInstanceForClassAndXY(dragClass, anEvent.getX(), anEvent.getY());
        if (didAdd)
            anEvent.acceptDrag();

        // Complete drop
        anEvent.dropComplete();
    }

    /**
     * Adds instance of actor/world for given class at given point.
     */
    protected boolean addInstanceForClassAndXY(Class<?> aClass, double aX, double aY)
    {
        // Create drag object - just return if failed
        Object dragObj;
        try { dragObj = aClass.getConstructor().newInstance(); }
        catch (Exception e) { _greenfootEnv.handleException(e); return false; }

        // If Actor, add to world
        if (dragObj instanceof Actor) {
            try {
                World world = getWorld();
                world.addObject((Actor) dragObj, (int) aX, (int) aY);
            }
            catch (Exception e) { _greenfootEnv.handleException(e); }
            return true;
        }

        // If World, set world
        else if (dragObj instanceof World) {
            try { setWorld((World) dragObj); }
            catch (Exception e) { _greenfootEnv.handleException(e); }
            return true;
        }

        // Return nothing added
        return false;
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
            if (rootClassNode != null && !rootClassNode.getChildNodes().isEmpty()) {
                setShowClasses(true);
                _classesPane.resetClassTree();
            }
        }
    }
}