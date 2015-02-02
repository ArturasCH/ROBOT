
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/* ImageFileView.java is used by FileChooserDemo2.java. */
public class ImageFileView extends FileView {

    @Override
    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    @Override
    public String getTypeDescription(File f) {
        String extension = getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("jpg")) {
                type = "JPEG Image";
            } else if (extension.equalsIgnoreCase("gif")){
                type = "GIF Image";
            } else if (extension.equalsIgnoreCase("tiff") ||
                       extension.equalsIgnoreCase("tif")) {
                type = "TIFF Image";
            } else if (extension.equalsIgnoreCase("png")){
                type = "PNG Image";
            }
        }
        return type;
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * @param path Returns an ImageIcon, or null if the path was invalid.
     * @return  */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ImageFileView.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
