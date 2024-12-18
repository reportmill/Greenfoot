package greenfoot;
import snap.geom.Insets;
import snap.gfx.Image;
import snap.util.ArrayUtils;
import snap.view.*;
import snap.viewx.DialogBox;
import snap.web.WebURL;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class shows the Greenfoot image picker.
 */
public class ImagePicker extends ViewOwner {

    // The image files listed in index file
    private File[] _imageFiles;

    // The category files list
    private List<File> _categoryFiles;

    // The category names list
    private List<String> _categoryNames;

    // The selected category name
    private String _selCategoryName;

    // The categories list
    private ListView<String> _categoryListView;

    // The images list
    private ListView<File> _imageListView;

    // The URL for image lib index file
    private static final String IMAGE_ROOT = "https://reportmill.com/images/greenfoot";
    private static final String INDEX_FILE_URL = IMAGE_ROOT + "/index.txt";

    /**
     * Constructor.
     */
    public ImagePicker()
    {
        super();
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
     * Returns the list of category files.
     */
    private List<File> getCategoryFiles()
    {
        if (_categoryFiles != null) return _categoryFiles;
        File[] imageFiles = getImageFiles();
        return _categoryFiles = Stream.of(imageFiles).map(file -> file.getParentFile()).distinct().collect(Collectors.toList());
    }

    /**
     * Returns the list of category names.
     */
    private List<String> getCategoryNames()
    {
        if (_categoryNames != null) return _categoryNames;
        List<File> categoryFiles = getCategoryFiles();
        return _categoryNames = categoryFiles.stream().map(file -> file.getName()).collect(Collectors.toList());
    }

    /**
     * Returns the image files.
     */
    private File[] getImageFiles()
    {
        if (_imageFiles != null) return _imageFiles;
        return _imageFiles = getImageFilesImpl();
    }

    /**
     * Returns the image files.
     */
    private File[] getImageFilesImpl()
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
     * Returns the selected category name.
     */
    private String getSelCategoryName()  { return _selCategoryName; }

    /**
     * Sets the selected category name.
     */
    private void setSelCategoryName(String categoryName)
    {
        if (Objects.equals(categoryName, getSelCategoryName())) return;
        _selCategoryName = categoryName;

        // Reset ImageListView items
        File[] imageFiles = getImageFiles();
        File[] categoryFiles = new File[0];
        if (categoryName != null)
            categoryFiles = ArrayUtils.filter(imageFiles, file -> file.getPath().startsWith('/' + categoryName));
        _imageListView.setItems(categoryFiles);
    }

    /**
     * Initialize UI.
     */
    @Override
    protected void initUI()
    {
        _categoryListView = getView("CategoryListView", ListView.class);
        _categoryListView.setCellPadding(new Insets(5));
        _categoryListView.setItemsList(getCategoryNames());

        // Configure ImageListView
        _imageListView = getView("ImageListView", ListView.class);
        _imageListView.setCellConfigure(this::configureImageListViewCell);
    }

    /**
     * Respond UI.
     */
    @Override
    protected void respondUI(ViewEvent anEvent)
    {
        String eventName = anEvent.getName();

        // Handle CategoryListView
        if (eventName.equals("CategoryListView"))
            setSelCategoryName(anEvent.getStringValue());
    }

    /**
     * Called to configure ImageListView cells.
     */
    private void configureImageListViewCell(ListCell<File> aCell)
    {
        File imageFile = aCell.getItem();
        if (imageFile == null)
            return;

        String imageFileAddr = IMAGE_ROOT + imageFile.getPath();
        WebURL imageFileUrl = WebURL.getURL(imageFileAddr);
        ImageView imageView = new ImageView(imageFileUrl);
        imageView.setMaxHeight(50);
        aCell.setGraphic(imageView);
    }
}
