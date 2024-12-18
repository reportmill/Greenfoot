package greenfoot;
import snap.geom.Insets;
import snap.geom.Pos;
import snap.gfx.Image;
import snap.util.ArrayUtils;
import snap.view.*;
import snap.viewx.DialogBox;
import snap.web.WebURL;
import java.io.*;
import java.util.Arrays;
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

    // The selected image view.
    private ImageView _selImageView;

    // The dialog box
    private DialogBox _dialogBox;

    // The categories list
    private ListView<String> _categoryListView;

    // The images list
    private ColView _imageListColView;

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
        _dialogBox = new DialogBox("Select class image");
        _dialogBox.setContent(getUI());
        _dialogBox.setConfirmEnabled(getSelImageView() != null);
        boolean confirmed = _dialogBox.showConfirmDialog(aView);
        return confirmed ? getSelImage() : null;
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
        File[] imageFiles = ArrayUtils.map(lines, line -> new File(line.trim()), File.class);
        Arrays.sort(imageFiles);
        return imageFiles;
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

        // Reset ImageListColView children
        File[] imageFiles = getImageFiles();
        File[] categoryFiles = new File[0];
        if (categoryName != null)
            categoryFiles = ArrayUtils.filter(imageFiles, file -> file.getPath().startsWith('/' + categoryName));

        // Get Images and set in ImageListColView
        ImageView[] imageViews = ArrayUtils.map(categoryFiles, this::createImageViewForFile, ImageView.class);
        for (int i = 1; i < imageViews.length; i += 2)
            imageViews[i].setFill(ViewTheme.get().getContentAltColor());
        _imageListColView.setChildren(imageViews);
        setSelImageView(null);
    }

    /**
     * Returns the selected Image.
     */
    public Image getSelImage()  { return _selImageView != null ? _selImageView.getImage() : null; }

    /**
     * Returns the selected ImageView.
     */
    public ImageView getSelImageView()  { return _selImageView; }

    /**
     * Sets the selected ImageView.
     */
    private void setSelImageView(ImageView imageView)
    {
        if (imageView == _selImageView) return;

        if (_selImageView != null)
            _selImageView.setFill(null);
        _selImageView = imageView;
        if (_selImageView != null)
            _selImageView.setFill(ViewTheme.get().getSelectedFill());

        // Update DialogBox ConfirmEnabled
        if (_dialogBox != null)
            _dialogBox.setConfirmEnabled(_selImageView != null);
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
        _imageListColView = getView("ImageListColView", ColView.class);
    }

    /**
     * Override to select default category.
     */
    @Override
    protected void initShowing()
    {
        setSelCategoryName("animals");
    }

    /**
     * Reset UI.
     */
    @Override
    protected void resetUI()
    {
        _categoryListView.setSelItem(getSelCategoryName());
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
     * Returns an ImageView for given file.
     */
    private ImageView createImageViewForFile(File imageFile)
    {
        String imageFileAddr = IMAGE_ROOT + imageFile.getPath();
        WebURL imageFileUrl = WebURL.getURL(imageFileAddr);
        ImageView imageView = new ImageView(imageFileUrl);
        imageView.setAlign(Pos.CENTER);
        imageView.setPadding(5, 5, 5, 5);
        imageView.setMaxHeight(80);
        imageView.setKeepAspect(true);
        imageView.setToolTip(imageFile.getName());
        imageView.addEventHandler(this::handleImageViewMousePress, MousePress);
        return imageView;
    }

    /**
     * Called when image view gets mouse press.
     */
    private void handleImageViewMousePress(ViewEvent anEvent)
    {
        ImageView imageView = (ImageView) anEvent.getView();
        if (anEvent.isShortcutDown() && imageView == getSelImageView())
            imageView = null;
        setSelImageView(imageView);
    }
}
