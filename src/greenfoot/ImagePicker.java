package greenfoot;
import snap.geom.Insets;
import snap.gfx.Image;
import snap.util.ArrayUtils;
import snap.view.ListView;
import snap.view.View;
import snap.view.ViewOwner;
import snap.viewx.DialogBox;
import snap.web.WebURL;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class shows the Greenfoot image picker.
 */
public class ImagePicker extends ViewOwner {

    // The index file paths
    private File[] _files;

    // The categories
    private List<String> _categoryNames;

    // The categories list
    private ListView<String> _categoryList;

    // The images list
    private ListView<ImageEntry> _imageList;

    // The URL for image lib index file
    private static final String INDEX_FILE_URL = "https://reportmill.com/images/greenfoot/index.txt";

    /**
     * Constructor.
     */
    public ImagePicker()
    {

    }

    /**
     * Shows the image picker.
     */
    public Image showImagePicker(View aView)
    {
        DialogBox dialogBox = new DialogBox("Select class image");
        dialogBox.setContent(getUI());
        dialogBox.showConfirmDialog(aView);
        return null;
    }

    /**
     * Returns the list of category names.
     */
    public List<String> getCategoryNames()
    {
        if (_categoryNames != null) return _categoryNames;
        File[] files = getFiles();
        List<String> categoryNames = Stream.of(files).map(file -> file.getParentFile().getName()).distinct().collect(Collectors.toList());
        return _categoryNames = categoryNames;
    }

    /**
     * Returns the image files.
     */
    public File[] getFiles()
    {
        if (_files != null) return _files;
        return _files = getFilesImpl();
    }

    /**
     * Returns the image files.
     */
    private File[] getFilesImpl()
    {
        // Path to the input file containing UNIX paths
        WebURL indexFileUrl = WebURL.getURL(INDEX_FILE_URL);
        if (indexFileUrl == null)
            return new File[0];

        // Get file lines and return Files
        String inputFilePaths = indexFileUrl.getText();
        String[] lines = inputFilePaths.split("\n");
        return ArrayUtils.map(lines, line -> new File(line.trim()), File.class);
    }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        _categoryList = getView("CategoryList", ListView.class);
        _categoryList.setCellPadding(new Insets(5));
        _categoryList.setItemsList(getCategoryNames());
        _imageList = getView("ImageList", ListView.class);
    }

    /**
     * A class to hold an image.
     */
    private class ImageEntry {

    }
}
