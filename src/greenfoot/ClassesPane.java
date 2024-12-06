package greenfoot;
import snap.gfx.Image;
import snap.view.*;
import java.util.Objects;

/**
 * This tool class shows the project source files in class hierarchy.
 */
public class ClassesPane extends ViewOwner {

    // The root class node
    private ClassNode _rootClassNode;

    // The selected class node
    private ClassNode _selClassNode;

    // The current greenfoot project (if available)
    private GreenfootProject _greenfootProject;

    // The TreeView to show classes
    private TreeView<ClassNode> _treeView;

    // Constants for properties
    public static final String SelClassNode_Prop = "SelClassNode";

    /**
     * Constructor.
     */
    public ClassesPane()
    {
        super();
    }

    /**
     * Returns the GreenfootProject.
     */
    public GreenfootProject getGreenfootProject()
    {
        if (_greenfootProject != null) return _greenfootProject;
        return _greenfootProject = Greenfoot.env().getGreenfootProject();
    }

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
     * Returns the selected class node.
     */
    public ClassNode getSelClassNode()  { return _selClassNode; }

    /**
     * Sets the selected class node.
     */
    public void setSelClassNode(ClassNode classNode)
    {
        if (Objects.equals(classNode, getSelClassNode())) return;
        firePropChange(SelClassNode_Prop, _selClassNode, _selClassNode);
    }

    /**
     * Resets the Classes tree.
     */
    protected void resetClassTree()
    {
        // Clear RootClassNode and reset GreenfootProject
        _rootClassNode = null;
        _greenfootProject = null;
        getGreenfootProject();

        // Get RootClassNode and reset treeview
        ClassNode rootClassNode = getRootClassNode();
        _treeView.setItems(new ClassNode[] { rootClassNode });
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

        // Rebuild classes view
        resetClassTree();
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

        // If Greenfoot Project is set, check for image
        if (_greenfootProject != null) {
            Image classImage = _greenfootProject.getImageForClass(nodeClass);
            if (classImage != null) {
                ImageView classImageView = new ImageView(classImage);
                classImageView.setKeepAspect(true);
                classImageView.setPrefSize(48, 20);
                label.setGraphicAfter(classImageView);
            }
        }

        // Return
        return label;
    }

    /**
     * A TreeResolver for ClassNode.
     */
    private static class ClassTreeResolver extends TreeResolver<ClassNode> {

        @Override
        public ClassNode getParent(ClassNode anItem)  { return anItem.getParentNode(); }

        @Override
        public boolean isParent(ClassNode anItem)  { return !anItem.getChildNodes().isEmpty(); }

        @Override
        public ClassNode[] getChildren(ClassNode aParent)  { return aParent.getChildNodes().getArray(); }

        @Override
        public String getText(ClassNode anItem)  { return ""; }
    }
}
