package greenfoot;
import snap.util.ListUtils;
import snap.util.ObjectArray;
import snap.web.WebFile;
import java.util.Collections;
import java.util.List;

/**
 * A class to represent the class hierarchy.
 */
public class ClassNode implements Comparable<ClassNode> {

    // The parent node
    private ClassNode _parentNode;

    // The node class
    private Class<?> _nodeClass;

    // The node file
    private WebFile _nodeFile;

    // The child nodes
    protected ObjectArray<ClassNode> _childNodes = new ObjectArray<>(ClassNode.class, 0);

    /**
     * Constructor.
     */
    public ClassNode(Class<?> nodeClass, WebFile nodeFile)
    {
        _nodeClass = nodeClass;
        _nodeFile = nodeFile;
    }

    /**
     * Returns the node class.
     */
    public Class<?> getNodeClass()  { return _nodeClass; }

    /**
     * Returns the node file.
     */
    public WebFile getNodeFile()  { return _nodeFile; }

    /**
     * Returns the parent node.
     */
    public ClassNode getParentNode()  { return _parentNode; }

    /**
     * Returns the child nodes.
     */
    public ObjectArray<ClassNode> getChildNodes()  { return _childNodes; }

    /**
     * Returns a child node for given class.
     */
    public ClassNode getChildNodeForClassDeep(Class<?> aClass)
    {
        // Search for child that is superclass, return if not found or exact match
        ClassNode childNode = ListUtils.findMatch(_childNodes, node -> node.getNodeClass().isAssignableFrom(aClass));
        if (childNode == null || childNode._nodeClass == aClass)
            return childNode;

        // Forward to child to look for node class
        return childNode.getChildNodeForClassDeep(aClass);
    }

    /**
     * Adds a child node for given class and file.
     */
    public ClassNode addChildNodeForClassAndFile(Class<?> nodeClass, WebFile nodeFile)
    {
        // Get superclass node - add if missing
        Class<?> superClass = nodeClass.getSuperclass();
        ClassNode superClassNode = superClass == null || superClass == Object.class ? this : getChildNodeForClassDeep(superClass);
        if (superClassNode == null)
            superClassNode = addChildNodeForClassAndFile(superClass, null);

        // Get class node from superclass node - add if missing
        ClassNode classNode = superClassNode.getChildNodeForClassDeep(nodeClass);
        if (classNode == null)
            classNode = superClassNode.addChildNodeForClassAndFileImpl(nodeClass, nodeFile);
        else if (nodeFile != null)
            classNode._nodeFile = nodeFile;

        // Return
        return classNode;
    }

    /**
     * Adds a child node for given class and file.
     */
    private ClassNode addChildNodeForClassAndFileImpl(Class<?> nodeClass, WebFile nodeFile)
    {
        ClassNode classNode = new ClassNode(nodeClass, nodeFile);
        classNode._parentNode = this;
        int insertIndex = -Collections.binarySearch(_childNodes, classNode) - 1;
        _childNodes.add(insertIndex, classNode);
        return classNode;
    }

    /**
     * Standard compareTo implementation.
     */
    @Override
    public int compareTo(ClassNode classNode)
    {
        if (_nodeFile != null)
            return classNode._nodeFile != null ? _nodeFile.compareTo(classNode._nodeFile) : -1;
        if (classNode._nodeFile != null)
            return 1;
        return _nodeClass.getSimpleName().compareTo(classNode._nodeClass.getSimpleName());
    }

    /**
     * Standard toString implementation.
     */
    @Override
    public String toString()
    {
        return "ClassNode: " + _nodeClass.getSimpleName();
    }

    /**
     * Orders child nodes for greenfoot root class node (moves World/Actor classes to front).
     */
    public static void orderGreenfootRootNode(ClassNode rootNode)
    {
        // Handle Greenfoot stuff
        List<ClassNode> childNodes = rootNode._childNodes;
        ClassNode actorClassNode = ListUtils.findMatch(childNodes, clsNode -> clsNode.getNodeClass().getName().equals("greenfoot.Actor"));
        if (actorClassNode != null)
            ListUtils.moveToFront(childNodes, actorClassNode);
        ClassNode worldClassNode = ListUtils.findMatch(childNodes, clsNode -> clsNode.getNodeClass().getName().equals("greenfoot.World"));
        if (worldClassNode != null)
            ListUtils.moveToFront(childNodes, worldClassNode);
    }
}
