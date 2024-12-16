package greenfoot;
import snap.gfx.Image;
import snap.view.*;
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
     * Resets the Classes tree.
     */
    protected void resetClassTree()
    {
        // Clear RootClassNode
        _rootClassNode = null;

        // Get RootClassNode and reset treeview
        ClassNode rootClassNode = getRootClassNode();
        _treeView.setItemsList(rootClassNode.getChildNodes());
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
                classImage = classImage.cloneForScale(.5);
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
        public ClassNode[] getChildren(ClassNode aParent)  { return aParent.getChildNodes().getArray(); }

        @Override
        public String getText(ClassNode anItem)  { return ""; }
    }
}
