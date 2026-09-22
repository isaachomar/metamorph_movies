/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.IJ
 *  ij.ImagePlus
 *  ij.Prefs
 *  ij.gui.GenericDialog
 *  ij.gui.ImageCanvas
 *  ij.gui.ImageWindow
 *  ij.io.OpenDialog
 *  ij.io.Opener
 *  ij.plugin.PlugIn
 */
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.plugin.PlugIn;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JFileChooser;

public class Open_MoviePlugin
implements PlugIn,
KeyListener {
    File dir = null;
    File file = null;
    ImagePlus img = null;
    int t = 1;
    double dispMax = 0.0;
    double dispMin = 0.0;

    public void run(String arg0) {
        this.openImages(arg0);
    }

    public void openImages(String arg0) {
        String path = null;
        if (arg0 == null || arg0.equals("")) {
            int returnVal;
            String sdir;
            JFileChooser fc = null;
            try {
                fc = new JFileChooser();
            }
            catch (Throwable e) {
                IJ.error((String)"This plugin requires Java 2 or Swing.");
                return;
            }
            if (this.dir == null && (sdir = OpenDialog.getDefaultDirectory()) != null) {
                this.dir = new File(sdir);
            }
            if (this.dir != null) {
                fc.setCurrentDirectory(this.dir);
            }
            if ((returnVal = fc.showOpenDialog((Component)IJ.getInstance())) != 0) {
                return;
            }
            this.file = fc.getSelectedFile();
            path = fc.getCurrentDirectory().getPath() + Prefs.getFileSeparator();
            this.dir = fc.getCurrentDirectory();
            arg0 = new String(path + this.file);
        } else {
            int ind1 = arg0.lastIndexOf(File.separator);
            path = arg0.substring(0, ind1);
            this.file = new File(arg0);
            this.dir = new File(path);
        }
        Opener opener = new Opener();
        this.img = opener.openImage(path, this.file.getName());
        if (this.img != null) {
            this.img.show();
            this.dispMax = this.img.getDisplayRangeMax();
            this.dispMin = this.img.getDisplayRangeMin();
            this.img.setDisplayRange(Math.max(0.0, this.dispMin - 0.1 * this.dispMin), this.dispMax + 0.1 * this.dispMax);
            ImageWindow win = this.img.getWindow();
            ImageCanvas canvas = win.getCanvas();
            KeyListener[] kl = canvas.getKeyListeners();
            if (kl.length > 0) {
                canvas.removeKeyListener(kl[0]);
            }
            if ((kl = win.getKeyListeners()).length > 0) {
                win.removeKeyListener(kl[0]);
            }
            canvas.addKeyListener((KeyListener)this);
            win.addKeyListener((KeyListener)this);
        }
    }

    public void sett(int t) {
        this.t = t;
    }

    public int gett() {
        return this.t;
    }

    public void keyPressed(KeyEvent e) {
        int z = 0;
        String fileName = null;
        int ind = 0;
        int extind = 0;
        String ext = null;
        Opener opener = null;
        ImagePlus imp = null;
        switch (e.getKeyCode()) {
            case 40: {
                this.dispMin = this.img.getDisplayRangeMin();
                this.dispMax = this.img.getDisplayRangeMax();
                this.img.setSlice(this.img.getCurrentSlice() - 1);
                this.img.setDisplayRange(Math.max(0.0, this.dispMin), this.dispMax);
                break;
            }
            case 38: {
                this.dispMin = this.img.getDisplayRangeMin();
                this.dispMax = this.img.getDisplayRangeMax();
                this.img.setSlice(this.img.getCurrentSlice() + 1);
                this.img.setDisplayRange(Math.max(0.0, this.dispMin), this.dispMax);
                break;
            }
            case 39: {
                ++this.t;
                z = this.img.getCurrentSlice();
                fileName = this.file.getName();
                ind = fileName.lastIndexOf("_t");
                extind = fileName.lastIndexOf(".");
                ext = fileName.substring(extind);
                fileName = fileName.substring(0, ind + 2).concat(String.valueOf(this.t)).concat(ext);
                this.dispMin = this.img.getDisplayRangeMin();
                this.dispMax = this.img.getDisplayRangeMax();
                opener = new Opener();
                imp = opener.openImage(this.dir.getPath(), fileName);
                if (imp != null) {
                    this.img.setStack(fileName, imp.getImageStack());
                    this.img.setDisplayRange(Math.max(0.0, this.dispMin), this.dispMax);
                    this.img.updateAndDraw();
                    ImageWindow win = this.img.getWindow();
                    ImageCanvas canvas = win.getCanvas();
                    KeyListener[] kl = canvas.getKeyListeners();
                    if (kl.length > 0) {
                        canvas.removeKeyListener(kl[0]);
                    }
                    if ((kl = win.getKeyListeners()).length > 0) {
                        win.removeKeyListener(kl[0]);
                    }
                    canvas.addKeyListener((KeyListener)this);
                    win.addKeyListener((KeyListener)this);
                    this.img.setSlice(z);
                    break;
                }
                --this.t;
                break;
            }
            case 37: {
                --this.t;
                z = this.img.getCurrentSlice();
                fileName = this.file.getName();
                ind = fileName.lastIndexOf("_t");
                extind = fileName.lastIndexOf(".");
                ext = fileName.substring(extind);
                fileName = fileName.substring(0, ind + 2).concat(String.valueOf(this.t)).concat(ext);
                this.dispMin = this.img.getDisplayRangeMin();
                this.dispMax = this.img.getDisplayRangeMax();
                opener = new Opener();
                imp = opener.openImage(this.dir.getPath(), fileName);
                if (imp != null) {
                    this.img.setStack(fileName, imp.getImageStack());
                    this.img.setDisplayRange(Math.max(0.0, this.dispMin), this.dispMax);
                    this.img.updateAndDraw();
                    ImageWindow win = this.img.getWindow();
                    ImageCanvas canvas = win.getCanvas();
                    KeyListener[] kl = canvas.getKeyListeners();
                    if (kl.length > 0) {
                        canvas.removeKeyListener(kl[0]);
                    }
                    if ((kl = win.getKeyListeners()).length > 0) {
                        win.removeKeyListener(kl[0]);
                    }
                    canvas.addKeyListener((KeyListener)this);
                    win.addKeyListener((KeyListener)this);
                    this.img.setSlice(z);
                    break;
                }
                ++this.t;
                break;
            }
            case 71: {
                GenericDialog gd = new GenericDialog("Go to time point");
                gd.addNumericField("Jump to: ", 1.0, 0, 3, "");
                gd.showDialog();
                if (gd.wasCanceled()) {
                    return;
                }
                int oldt = this.t;
                this.t = (int)gd.getNextNumber();
                z = this.img.getCurrentSlice();
                fileName = this.file.getName();
                ind = fileName.lastIndexOf("_t");
                extind = fileName.lastIndexOf(".");
                ext = fileName.substring(extind);
                fileName = fileName.substring(0, ind + 2).concat(String.valueOf(this.t)).concat(ext);
                this.dispMin = this.img.getDisplayRangeMin();
                this.dispMax = this.img.getDisplayRangeMax();
                opener = new Opener();
                imp = opener.openImage(this.dir.getPath(), fileName);
                if (imp != null) {
                    this.img.setStack(fileName, imp.getImageStack());
                    this.img.setDisplayRange(Math.max(0.0, this.dispMin), this.dispMax);
                    this.img.updateAndDraw();
                    ImageWindow win = this.img.getWindow();
                    ImageCanvas canvas = win.getCanvas();
                    KeyListener[] kl = canvas.getKeyListeners();
                    if (kl.length > 0) {
                        canvas.removeKeyListener(kl[0]);
                    }
                    if ((kl = win.getKeyListeners()).length > 0) {
                        win.removeKeyListener(kl[0]);
                    }
                    canvas.addKeyListener((KeyListener)this);
                    win.addKeyListener((KeyListener)this);
                    this.img.setSlice(z);
                    break;
                }
                this.t = oldt;
            }
        }
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
    }
}

