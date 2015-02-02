
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
 
/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {
 
    //Accept all directories and all gif, jpg, tiff, or png files.
    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        if(extension(f).equalsIgnoreCase("GIF"))
            return true;
        if(extension(f).equalsIgnoreCase("JPG"))
            return true;    
        if(extension(f).equalsIgnoreCase("PNG"))
            return true;
        if(extension(f).equalsIgnoreCase("JPEG"))
            return true;
        if(extension(f).equalsIgnoreCase("TIF"))
            return true;
        if(extension(f).equalsIgnoreCase("TIFF"))
            return true;
        return false;
    }
    
    String extension(File f) {
        String fileName = f.getName();
        int indexFile = fileName.lastIndexOf('.');
        if(indexFile > 0 && indexFile < fileName.length() - 1)
            return fileName.substring(indexFile + 1);
        return "";
    }
    
    //The description of this filter
    @Override
    public String getDescription() {
        return "Images Only";
    }
}