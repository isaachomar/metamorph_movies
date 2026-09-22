/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ImageFilter
extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        int i = f.getName().lastIndexOf(46);
        if (i == -1) {
            return false;
        }
        String extension = f.getName().substring(i).toLowerCase();
        if (extension != null) {
            return extension.equals(".tiff") || extension.equals(".tif") || extension.equals(".gif") || extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".ics") || extension.equals(".ids");
        }
        return false;
    }

    public String getDescription() {
        return "Just Images";
    }
}

