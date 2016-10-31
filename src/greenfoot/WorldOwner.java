package greenfoot;
import snap.gfx.*;
import snap.view.*;

/**
 * A custom class.
 */
public class WorldOwner extends ViewOwner {

    // The current world
    World         _world;
    
    // The World View
    WorldView     _worldView;
    
    // The box that holds the World View
    Box           _worldViewBox;
    
    // The actor pressed by last mouse
    Actor         _mouseActor;

/**
 * Creates a new WorldOwner for given World.
 */
public WorldOwner(World aWorld)  { _world = aWorld; }

/**
 * Create UI.
 */
protected View createUI()
{
    // Create tool bar items
    Button actBtn = new Button("Act"); actBtn.setName("ActButton"); actBtn.setPrefSize(70,20);
    Button runBtn = new Button("Run"); runBtn.setName("RunButton"); runBtn.setPrefSize(70,20);
    Button resetBtn = new Button("Reset"); resetBtn.setName("ResetButton"); resetBtn.setPrefSize(70,20);
    Separator sep = new Separator(); sep.setPrefWidth(40); sep.setVisible(false);
    Label speedLbl = new Label("Speed:"); speedLbl.setLeanX(HPos.CENTER); speedLbl.setFont(Font.Arial14);
    Slider speedSldr = new Slider(); speedSldr.setName("SpeedSlider"); speedSldr.setPrefWidth(180);

    // Create toolbar
    HBox toolBar = new HBox(); toolBar.setAlign(Pos.CENTER); toolBar.setPadding(18,25,18,25); toolBar.setSpacing(15);
    toolBar.setChildren(actBtn, runBtn, resetBtn, sep, speedLbl, speedSldr);
    
    // Configure World View
    _worldView = _world.getView();
    _worldViewBox = new Box(_worldView); _worldViewBox.setPadding(8,8,8,8);
    ScrollView sview = new ScrollView(_worldViewBox); sview.setBorder(null);
    setFirstFocus(_worldView);
    enableEvents(_worldView, MouseEvents);

    // Create border view and add world, toolBar
    BorderView bview = new BorderView(); bview.setFont(Font.Arial12); bview.setFill(ViewUtils.getBackFill());
    bview.setCenter(sview); bview.setBottom(toolBar); bview.setBorder(Color.GRAY, 1);
    return bview;
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    setViewValue("SpeedSlider", Greenfoot.getSpeed()/100f);
}

/**
 * Respond to UI changes.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle MouseEvent on WorldView
    if(anEvent.isMouseEvent() && !_world.getView().isPlaying())
        handleMouseEvent(anEvent);

    // Handle ActButton
    if(anEvent.equals("ActButton"))
        _world._wv.doAct();
    
    // Handle RunButton
    if(anEvent.equals("RunButton")) {
        View runBtn = anEvent.getView();
        runBtn.setText("Pause"); runBtn.setName("PauseButton");
        getView("ActButton").setDisabled(true);
        Greenfoot.start();
    }
    
    // Handle PauseButton
    if(anEvent.equals("PauseButton")) {
        Greenfoot.stop();
        View pauseBtn = anEvent.getView();
        pauseBtn.setText("Run"); pauseBtn.setName("RunButton");
        getView("ActButton").setDisabled(false);
    }
    
    // Handle ResetButton
    if(anEvent.equals("ResetButton"))
        resetWorld();
    
    // Handle SpeedSlider
    if(anEvent.equals("SpeedSlider")) {
        int val = Math.round(anEvent.getFloatValue()*100);
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
    if(pauseBtn!=null) {
        pauseBtn.setText("Run"); pauseBtn.setName("RunButton");
        getView("ActButton").setDisabled(false);
    }
    
    try { _world = _world.getClass().newInstance(); }
    catch(Exception e) { new RuntimeException(e); }
    _worldView = _world.getView();
    _worldViewBox.setContent(_worldView);
    enableEvents(_worldView, MouseEvents);
    Greenfoot.setWorld(_world);
}

/**
 * Handles MouseEvent.
 */
protected void handleMouseEvent(ViewEvent anEvent)
{
    // Get x/y
    int x = (int)Math.round(anEvent.getX()), y = (int)Math.round(anEvent.getY());
    
    // Handle MousePressed
    if(anEvent.isMousePress()) {
        if((anEvent.isAltDown() || anEvent.isShortcutDown()) && _mouseActor!=null)
            try { _world.addObject(_mouseActor = _mouseActor.getClass().newInstance(), x, y); }
            catch(Exception e) { }
        else _mouseActor = _world.getActorAt(x, y, null);
    }
    
    // Handle MouseDraggged
    if(anEvent.isMouseDrag() && _mouseActor!=null)
        _mouseActor.setLocation(x, y);
}

}