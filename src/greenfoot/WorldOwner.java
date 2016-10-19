package greenfoot;
import snap.gfx.*;
import snap.view.*;

/**
 * A custom class.
 */
public class WorldOwner extends ViewOwner {

    // The current world
    World         _world;
    
    // The box that holds the World View
    Box           _worldViewBox;

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
    _worldViewBox = new Box(_world.getView()); _worldViewBox.setPadding(8,8,8,8);
    ScrollView sview = new ScrollView(_worldViewBox); sview.setBorder(null);
    setFirstFocus(_world.getView());

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
    // Handle ActButton
    if(anEvent.equals("ActButton")) {
        _world._sw.doAct();
    }
    
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
    if(anEvent.equals("ResetButton")) {
        Greenfoot.stop();
        View pauseBtn = getView("PauseButton");
        if(pauseBtn!=null) {
            pauseBtn.setText("Run"); pauseBtn.setName("RunButton");
            getView("ActButton").setDisabled(false);
        }
        
        try { _world = _world.getClass().newInstance(); }
        catch(Exception e) { new RuntimeException(e); }
        _worldViewBox.setContent(_world.getView());
        Greenfoot.setWorld(_world);
    }
    
    // Handle SpeedSlider
    if(anEvent.equals("SpeedSlider")) {
        int val = Math.round(anEvent.getFloatValue()*100);
        Greenfoot.setSpeed(val);
    }
    
    // Shouldn't need this
    _world.getView().requestFocus();
}

}