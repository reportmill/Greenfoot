package greenfoot;
import snap.gfx.Image;
import snap.util.ListUtils;
import snap.view.*;
import snap.web.WebFile;
import java.util.List;
import java.util.Objects;

/**
 * This tool class shows the project source files in class hierarchy.
 */
public class ClassesPane extends ViewOwner {

    // The Greenfoot Env
    private GreenfootEnv _greenfootEnv;

    // The root class node
    private ClassNode _rootClassNode;

    // The selected class node
    private ClassNode _selClassNode;

    // A callback to get class pane action events (like NewSubclassMenuItem)
    private EventListener _actionListener;

    // The java file contents for new subclass
    private String _newClassString;

    // The TreeView to show classes
    private TreeView<ClassNode> _treeView;

    // Constants for properties
    public static final String SelClassNode_Prop = "SelClassNode";

    /**
     * Constructor.
     */
    public ClassesPane(GreenfootEnv greenfootEnv)
    {
        super();
        _greenfootEnv = greenfootEnv;
    }

    /**
     * Returns the GreenfootProject.
     */
    public GreenfootProject getGreenfootProject()  { return _greenfootEnv.getGreenfootProject(); }

    /**
     * Returns the root class node.
     */
    private ClassNode getRootClassNode()
    {
        if (_rootClassNode != null) return _rootClassNode;
        GreenfootProject greenfootProject = getGreenfootProject();
        ClassNode rootClassNode = greenfootProject != null ? greenfootProject.getRootClassNode() : null;
        if (rootClassNode == null)
            rootClassNode = new ClassNode(Object.class, null);
        return _rootClassNode = rootClassNode;
    }

    /**
     * Returns the selected class.
     */
    public Class<?> getSelClass()  { return _selClassNode != null ? _selClassNode.getNodeClass() : null; }

    /**
     * Returns the selected class node.
     */
    public ClassNode getSelClassNode()  { return _selClassNode; }

    /**
     * Sets the selected class node.
     */
    public void setSelClassNode(ClassNode classNode)
    {
        if (Objects.equals(classNode, getSelClassNode())) return;
        firePropChange(SelClassNode_Prop, _selClassNode, _selClassNode = classNode);
    }

    /**
     * Returns the action listener.
     */
    public EventListener getActionListener()  { return _actionListener; }

    /**
     * Sets the action listener.
     */
    public void setActionListener(EventListener actionListener)
    {
        _actionListener = actionListener;
    }

    /**
     * Returns the new class string.
     */
    public String getNewClassString()  { return _newClassString; }

    /**
     * Resets the Classes tree.
     */
    protected void resetClassTree()
    {
        // Clear RootClassNode
        _rootClassNode = null;

        // Get RootClassNode and reset treeview
        ClassNode rootClassNode = getRootClassNode();
        _treeView.setItems(rootClassNode.getChildNodes());
        _treeView.expandAll();

        // Reset SelClassNode
        _selClassNode = _treeView.getSelItem();
    }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        // Calculate TreeView RowHeight
        //Label sampleLabel = createLabelForClassNode(new ClassNode(Object.class, null));
        int treeViewRowHeight = 30; //(int) Math.ceil(sampleLabel.getPrefHeight() + 10);

        // Configure TreeView
        _treeView = getView("TreeView", TreeView.class);
        _treeView.setRowHeight(treeViewRowHeight);
        _treeView.setResolver(new ClassTreeResolver());
        _treeView.setCellConfigure(this::configureClassTreeCell);
        _treeView.addEventHandler(this::handleTreeViewDragGestureEvent, DragGesture);
        _treeView.addEventFilter(this::handleTreeViewMouseEvent, MousePress, MouseRelease);
    }

    /**
     * Respond UI.
     */
    @Override
    protected void respondUI(ViewEvent anEvent)
    {
        switch (anEvent.getName()) {

            // Handle ReloadButton
            case "ReloadButton": resetClassTree(); break;

            // Handle TreeView
            case "TreeView":
                ClassNode selClassNode = _treeView.getSelItem();
                setSelClassNode(selClassNode);
                break;

            // Handle NewInstanceMenuItem, SetImageMenuItem, NewSubclassMenuItem
            case "NewInstanceMenuItem": handleNewInstanceMenuItem(); break;
            case "SetImageMenuItem": runLater(this::handleSetImageMenuItem); break;
            case "NewSubclassMenuItem": runLater(() -> handleNewSubclassMenuItem(anEvent)); break;

            // Do normal version
            default: super.respondUI(anEvent); break;
        }
    }

    /**
     * Called to configure a ClassTree cell.
     */
    private void configureClassTreeCell(ListCell<ClassNode> classTreeCell)
    {
        ClassNode classNode = classTreeCell.getItem();
        if (classNode == null) return;

        Label classNodeLabel = createLabelForClassNode(classNode);
        classTreeCell.setGraphicAfter(classNodeLabel);
    }

    /**
     * Creates a label for class node.
     */
    private Label createLabelForClassNode(ClassNode classNode)
    {
        // Create label for node class
        Class<?> nodeClass = classNode.getNodeClass();
        Label label = new Label(nodeClass.getSimpleName());
        label.setPropsString("Fill:#F5CC9B; Border:#66 1; MinWidth:60; MinHeight:24; Padding:2,4,2,8; BorderRadius:2;");

        // If image available for class, add to label
        Image classImage = _greenfootEnv.getImageForClass(nodeClass);
        if (classImage != null) {
            ImageView classImageView = new ImageView(classImage);
            classImageView.setKeepAspect(true);
            classImageView.setPrefSize(40, 18);
            label.setGraphicAfter(classImageView);
        }

        // Return
        return label;
    }

    /**
     * Called when user selects NewInstanceMenuItem.
     */
    private void handleNewInstanceMenuItem()
    {
        PlayerPane playerPane = _greenfootEnv.getPlayerPane();
        Class<?> selClass = getSelClass();
        World world = _greenfootEnv.getWorld();
        playerPane.addInstanceForClassAndXY(selClass, world.getWidth() - 100, world.getHeight() / 2d);
    }

    /**
     * Called when user selects SetImageMenuItem.
     */
    private void handleSetImageMenuItem()
    {
        // Show image picker to select image (just return if none selected)
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.setScenarioImages(getScenarioImages());
        View parentView = _greenfootEnv.getPlayerPane().getWorldViewBox();
        String imagePickerTitle = "Select class image: " + getSelClass().getSimpleName();
        Image image = imagePicker.showImagePicker(parentView, imagePickerTitle);
        if (image == null)
            return;

        // Set image for class
        Class<?> selClass = getSelClass();
        setImageForClassName(selClass.getName(), image);
    }

    /**
     * Sets a new image for a class.
     */
    private void setImageForClassName(String className, Image image)
    {
        // Set image in actor class
        GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject();
        if (greenfootProject == null)
            return;

        // Set image
        String imageName = image.getName();
        greenfootProject.setImageNameForClassName(className, imageName);

        // Save image
        WebFile projectFile = greenfootProject.getProjectFile();
        WebFile imageDir = projectFile.getParent().getFileForName("images");
        if (imageDir == null) {
            System.err.println("ClassesPane.handleSetImageMenuItem: Can't find image dir");
            return;
        }

        // Save image to project images
        String imagePath = imageDir.getDirPath() + imageName;
        WebFile imageFile = imageDir.getSite().createFileForPath(imagePath, false);
        imageFile.setBytes(image.getBytes());
        imageFile.save();

        // Try to save to build images
        WebFile buildImagesDir = imageDir.getSite().getFileForPath("/bin/images");
        if (buildImagesDir != null) {
            String buildImagePath = buildImagesDir.getDirPath() + imageName;
            WebFile buildImageFile = imageDir.getSite().createFileForPath(buildImagePath, false);
            buildImageFile.setBytes(image.getBytes());
            buildImageFile.save();
        }

        // Reset class tree
        _treeView.updateItems();
    }

    /**
     * Called when user selects NewSubclassMenuItem.
     */
    private void handleNewSubclassMenuItem(ViewEvent anEvent)
    {
        // Show image picker to select image (just return if none selected)
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.setScenarioImages(getScenarioImages());
        View parentView = _greenfootEnv.getPlayerPane().getWorldViewBox();
        String newClassName = imagePicker.showNewClassPanel(parentView);
        if (newClassName == null)
            return;

        // Create NewClassString
        Class<?> selClass = getSelClass();
        String newClassTemplate = getClassTemplateForClass(selClass);
        String selClassName = selClass != null ? selClass.getSimpleName() : "Object";
        _newClassString = newClassTemplate.replaceAll("@Class@", newClassName);
        _newClassString = _newClassString.replace("@Superclass@", selClassName);

        // Call actionListener
        EventListener actionListener = getActionListener();
        if (actionListener != null)
            actionListener.listenEvent(anEvent);

        // If image was selected, set for new class
        Image image = imagePicker.getSelImage();
        if (image != null)
            setImageForClassName(newClassName, image);
    }

    /**
     * Returns the scenario images.
     */
    private List<Image> getScenarioImages()
    {
        GreenfootProject greenfootProject = _greenfootEnv.getGreenfootProject(); if (greenfootProject == null) return null;
        WebFile projectFile = greenfootProject.getProjectFile();
        WebFile imageDir = projectFile.getParent().getFileForName("images");
        if (imageDir == null)
            return null;

        List<WebFile> imageFiles = imageDir.getFiles();
        return ListUtils.mapNonNull(imageFiles, file -> _greenfootEnv.getImageForName(file.getName()));
    }

    /**
     * Called when TreeView gets mouse event.
     */
    private void handleTreeViewMouseEvent(ViewEvent anEvent)
    {
        if (anEvent.isPopupTrigger())
            runLater(() -> showContextMenu(anEvent));
    }

    /**
     * Shows the context menu.
     */
    private void showContextMenu(ViewEvent anEvent)
    {
        if (getSelClass() == null)
            return;
        Menu contextMenu = createContextMenu();
        contextMenu.showMenuAtXY(_treeView, anEvent.getX(), anEvent.getY());
    }

    /**
     * Creates the ContextMenu.
     */
    protected Menu createContextMenu()
    {
        ViewBuilder<MenuItem> menuBuilder = new ViewBuilder<>(MenuItem.class);

        // If not World, add NewInstanceMenuItem, SetImageMenuItem
        Class<?> selClass = getSelClass();
        if (selClass != World.class && selClass != Actor.class) {

            // Add NewInstanceMenuItem
            String newInstanceText = "New " + getSelClass().getSimpleName() + "()";
            menuBuilder.name("NewInstanceMenuItem").text(newInstanceText).save();
            menuBuilder.save(); // Separator

            // Add SetImageMenuItem
            menuBuilder.name("SetImageMenuItem").text("Set Image...").save();
            menuBuilder.save(); // Separator
        }

        // Add NewSubclassMenuItem
        menuBuilder.name("NewSubclassMenuItem").text("New Subclass...").save();

        // Create context menu
        Menu contextMenu = menuBuilder.buildMenu("ContextMenu", null);
        contextMenu.setOwner(this);

        // Return
        return contextMenu;
    }

    /**
     * Called when TreeView gets DragGesture event.
     */
    private void handleTreeViewDragGestureEvent(ViewEvent anEvent)
    {
        // Get the class under mouse (just return if not found)
        ListCell<ClassNode> dragCell = _treeView.getCol(0).getCellForY(anEvent.getY());
        ClassNode dragNode = dragCell != null ? dragCell.getItem() : null;
        Class<?> dragClass = dragNode != null ? dragNode.getNodeClass() : null;
        if (dragClass == null)
            return;

        // Add to clipboard
        Clipboard clipboard = anEvent.getClipboard();
        clipboard.addData("Drag:" + dragClass.getName());
        Image classImage = _greenfootEnv.getImageForClass(dragClass);
        if (classImage != null) {
            if (classImage.getWidth() > 400 && classImage.getHeight() > 400)
                classImage = classImage.copyForScale(.5);
            clipboard.setDragImage(classImage);
        }

        // Start drag
        clipboard.startDrag();
    }

    /**
     * Override to reset class tree when shown.
     */
    @Override
    protected void setShowing(boolean aValue)
    {
        if (aValue == isShowing()) return;
        super.setShowing(aValue);

        if (aValue)
            resetClassTree();
    }

    /**
     * A TreeResolver for ClassNode.
     */
    private class ClassTreeResolver extends TreeResolver<ClassNode> {

        @Override
        public boolean isParent(ClassNode anItem)  { return !anItem.getChildNodes().isEmpty(); }

        @Override
        public ClassNode getParent(ClassNode anItem)
        {
            ClassNode parentNode = anItem.getParentNode();
            if (parentNode == _rootClassNode)
                return null;
            return parentNode;
        }

        @Override
        public List<ClassNode> getChildren(ClassNode aParent)  { return aParent.getChildNodes(); }

        @Override
        public String getText(ClassNode anItem)  { return ""; }
    }

    /**
     * Returns the class template for given class.
     */
    private static String getClassTemplateForClass(Class<?> aClass)
    {
        if (World.class == aClass)
            return NEW_WORLD_SUBCLASS_TEMPLATE;
        if (World.class.isAssignableFrom(aClass))
            return NEW_WORLD_SUBCLASS_TEMPLATE2;
        if (Actor.class.isAssignableFrom(aClass))
            return NEW_ACTOR_SUBCLASS_TEMPLATE;
        return NEW_OBJECT_SUBCLASS_TEMPLATE;
    }

    // Template for new subclass
    private static String NEW_WORLD_SUBCLASS_TEMPLATE = """
        import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
        
        /**
         * Write a description of class @Class@ here.
         *
         * @author (your name)
         * @version (a version number or a date)
         */
        public class @Class@ extends World
        {
        
            /**
             * Constructor for objects of class @Class@.
             *
             */
            public @Class@()
            {
                // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
                super(600, 400, 1);
            }
        }
        """;

    // Template for new subclass
    private static String NEW_WORLD_SUBCLASS_TEMPLATE2 = """
        import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
        
        /**
         * Write a description of class @Class@ here.
         *
         * @author (your name)
         * @version (a version number or a date)
         */
        public class @Class@ extends @Superclass@
        {
        
            /**
             * Constructor for objects of class @Class@.
             *
             */
            public @Class@()
            {
            }
        }
        """;

    // Template for new subclass
    private static String NEW_ACTOR_SUBCLASS_TEMPLATE = """
        import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
        
        /**
         * Write a description of class @Class@ here.
         *
         * @author (your name)
         * @version (a version number or a date)
         */
        public class @Class@ extends @Superclass@
        {
            /**
             * Act - do whatever the @Class@ wants to do. This method is called whenever
             * the 'Act' or 'Run' button gets pressed in the environment.
             */
            public void act()
            {
                // Add your action code here.
            }
        }
        """;

    // Template for new subclass
    private static String NEW_OBJECT_SUBCLASS_TEMPLATE = """
        /**
         * Write a description of class @Class@ here.
         *
         * @author (your name)
         * @version (a version number or a date)
         */
        public class @Class@ extends @Superclass@
        {
            // instance variables - replace the example below with your own
            private int x;
        
            /**
             * Constructor for objects of class @Class@
             */
            public @Class@()
            {
            }
        
            /**
             * An example of a method - replace this comment with your own
             *
             * @param  y   a sample parameter for a method
             * @return     the sum of x and y
             */
            public int sampleMethod(int y)
            {
                // put your code here
                return x + y;
            }
        }
        """;
}
