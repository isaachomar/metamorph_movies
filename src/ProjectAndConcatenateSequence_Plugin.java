/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.IJ
 *  ij.ImagePlus
 *  ij.ImageStack
 *  ij.Macro
 *  ij.io.FileInfo
 *  ij.io.FileSaver
 *  ij.io.OpenDialog
 *  ij.io.Opener
 *  ij.io.TiffEncoder
 *  ij.plugin.PlugIn
 *  ij.plugin.ZProjector
 *  ij.process.ImageProcessor
 */
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.io.TiffEncoder;
import ij.plugin.PlugIn;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProjectAndConcatenateSequence_Plugin
implements PlugIn {
    String currDir = null;
    boolean[] theMovies = new boolean[MAX_MOVIES];
    boolean[] theWavelengths = new boolean[MAX_WAVELENGTHS];
    int[] theMinTP = new int[MAX_MOVIES];
    int[] theMaxTP = new int[MAX_MOVIES];
    int nMovies = 0;
    int nWavelengths = 0;
    static int MAX_WAVELENGTHS = 3;
    static int MAX_MOVIES = 100;
    String baseName = null;
    String[] channelNames = new String[MAX_WAVELENGTHS];
    int defaultMethod = 1;

    public void run(String string) {
        int n;
        String string2;
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        IJ.redirectErrorMessages();
        String string3 = Macro.getOptions();
        if (string3 != null) {
            this.currDir = Macro.getValue((String)string3, (String)"directory", (String)"");
        } else {
            OpenDialog openDialog = new OpenDialog("Select a file in the folder to process ...", this.currDir);
            this.currDir = openDialog.getDirectory();
        }
        if (this.currDir == null || this.currDir.equals("")) {
            return;
        }
        boolean bl = true;
        String string4 = IJ.getString((String)"Slices to project (first(min is 1)-last):", (String)"");
        if (string4 != null && !string4.equals("")) {
            n2 = string4.indexOf("-");
            n3 = Integer.valueOf(string4.substring(0, n2));
            n4 = Integer.valueOf(string4.substring(n2 + 1));
            bl = false;
        }
        if ((string2 = IJ.getString((String)"Output image base name:", (String)"")) == null) {
            return;
        }
        this.readMovieInfo();
        System.out.println("There are " + this.nMovies + " movies in this folder.");
        System.out.println("There are " + this.nWavelengths + " wavelengths per movie.");
        for (n = 0; n < MAX_MOVIES; ++n) {
            if (!this.theMovies[n]) continue;
            System.out.println("Movie #" + (n + 1) + " starts at " + this.theMinTP[n] + " and ends at " + this.theMaxTP[n]);
        }
        for (n = 0; n < MAX_WAVELENGTHS; ++n) {
            if (!this.theWavelengths[n]) continue;
            ImageStack imageStack = null;
            int n5 = 0;
            for (int i = 0; i < MAX_MOVIES; ++i) {
                ImageProcessor imageProcessor;
                Object object;
                if (!this.theMovies[i]) continue;
                System.out.print("Processing movie #" + (i + 1) + ", wavelength #" + (n + 1) + " ... ");
                ImageStack imageStack2 = null;
                int n6 = 0;
                int n7 = 0;
                int n8 = 0;
                int n9 = 0;
                int n10 = 0;
                ColorModel colorModel = null;
                for (int j = this.theMinTP[i]; j <= this.theMaxTP[i]; ++j) {
                    object = null;
                    object = !this.channelNames[n].equals("") ? new String(this.baseName + "n" + (i + 1) + "_w" + (n + 1) + this.channelNames[n] + "_t" + j + ".TIF") : new String(this.baseName + "n" + (i + 1) + "_t" + j + ".TIF");
                    try {
                        Opener opener = new Opener();
                        opener.setSilentMode(true);
                        imageProcessor = opener.openImage(this.currDir, (String)object);
                        if (imageProcessor != null && imageStack2 == null) {
                            n6 = imageProcessor.getWidth();
                            n7 = imageProcessor.getHeight();
                            n8 = imageProcessor.getStackSize();
                            n9 = imageProcessor.getBitDepth();
                            colorModel = imageProcessor.getProcessor().getColorModel();
                            imageStack2 = new ImageStack(n6, n7, colorModel);
                        }
                        if (imageProcessor == null) {
                            if (((String)object).startsWith(".")) continue;
                            IJ.log((String)((String)object + ": unable to open"));
                            continue;
                        }
                        if (imageProcessor.getWidth() != n6 || imageProcessor.getHeight() != n7) {
                            IJ.log((String)((String)object + ": wrong size; " + n6 + "x" + n7 + " expected, " + imageProcessor.getWidth() + "x" + imageProcessor.getHeight() + " found"));
                            continue;
                        }
                        ImageStack imageStack3 = imageProcessor.getStack();
                        if (bl) {
                            n3 = 1;
                            n4 = imageStack3.getSize();
                        } else {
                            if (n3 < 1) {
                                n3 = 1;
                            }
                            if (n3 > imageStack3.getSize()) {
                                n4 = n3 = imageStack3.getSize();
                            }
                            if (n4 < 1) {
                                n4 = 1;
                                n3 = 1;
                            }
                            if (n4 > imageStack3.getSize()) {
                                n4 = imageStack3.getSize();
                            }
                            n8 = n4 - n3 + 1;
                        }
                        for (int k = n3; k <= n4; ++k) {
                            ImageProcessor imageProcessor2 = imageStack3.getProcessor(k);
                            int n11 = imageProcessor.getBitDepth();
                            if (n11 != n9) {
                                if (n9 == 8) {
                                    imageProcessor2 = imageProcessor2.convertToByte(true);
                                    n11 = 8;
                                } else if (n9 == 24) {
                                    imageProcessor2 = imageProcessor2.convertToRGB();
                                    n11 = 24;
                                }
                            }
                            if (n11 != n9) {
                                IJ.log((String)((String)object + ": wrong bit depth; " + n9 + " expected, " + n11 + " found"));
                                break;
                            }
                            imageStack2.addSlice("(" + ++n10 + ")", imageProcessor2);
                        }
                        if (!IJ.escapePressed()) continue;
                        IJ.beep();
                        break;
                    }
                    catch (Exception exception) {
                        System.out.println("\nError loading " + (String)object + ".\n");
                        exception.printStackTrace();
                        return;
                    }
                }
                ImagePlus imagePlus = this.computeProjection(new ImagePlus("Projection of movie #" + (i - 1) + " - " + this.channelNames[n], imageStack2), n8);
                object = imagePlus.getImageStack();
                if (n5 == 0) {
                    imageStack = new ImageStack(n6, n7, colorModel);
                }
                for (int j = 1; j <= object.getSize(); ++j) {
                    imageProcessor = object.getProcessor(j);
                    imageStack.addSlice("(" + ++n5 + ")", imageProcessor);
                }
                imageStack2 = null;
                imagePlus = null;
                object = null;
                System.gc();
                System.out.println("done!");
            }
            this.writeTIFF(new ImagePlus("Concatenated movie - " + this.channelNames[n], imageStack), string2 + "_" + this.channelNames[n] + ".tif", this.currDir);
            imageStack = null;
            System.gc();
        }
    }

    private boolean readMovieInfo() {
        int n;
        if (this.currDir == null) {
            return false;
        }
        File file = new File(this.currDir);
        String string = null;
        boolean bl = false;
        for (n = 0; n < MAX_WAVELENGTHS; ++n) {
            this.theWavelengths[n] = false;
        }
        for (n = 0; n < MAX_MOVIES; ++n) {
            this.theMovies[n] = false;
            this.theMinTP[n] = Integer.MAX_VALUE;
            this.theMaxTP[n] = Integer.MIN_VALUE;
        }
        this.baseName = null;
        if (!file.isDirectory()) {
            return false;
        }
        File[] fileArray = file.listFiles();
        this.nMovies = 0;
        int n2 = -1;
        int n3 = -1;
        for (int i = 0; i < fileArray.length; ++i) {
            int n4;
            int n5;
            int n6;
            int n7;
            int n8;
            block17: {
                string = fileArray[i].getName();
                n8 = string.lastIndexOf("_w");
                if (n8 < 0) {
                    n8 = string.lastIndexOf("_t");
                    if (n8 < 0) continue;
                    bl = true;
                }
                if ((n7 = string.substring(0, n8).lastIndexOf("n")) < 0) continue;
                if (this.baseName == null) {
                    this.baseName = string.substring(0, n7);
                }
                try {
                    n6 = Integer.valueOf(string.substring(n7 + 1, n8));
                }
                catch (Exception exception) {
                    continue;
                }
                if (!this.theMovies[n6 - 1]) {
                    this.theMovies[n6 - 1] = true;
                    ++this.nMovies;
                }
                if (!bl) {
                    n7 = (n8 += 2) + 1;
                    try {
                        n5 = Integer.valueOf(string.substring(n8, n7));
                        break block17;
                    }
                    catch (Exception exception) {
                        continue;
                    }
                }
                n5 = 1;
            }
            if (!this.theWavelengths[n5 - 1]) {
                this.theWavelengths[n5 - 1] = true;
                ++this.nWavelengths;
            }
            n8 = string.lastIndexOf("_t") + 2;
            this.channelNames[n5 - 1] = !bl ? string.substring(n7, n8 - 2) : new String("");
            n7 = string.lastIndexOf(".");
            try {
                n4 = Integer.valueOf(string.substring(n8, n7));
            }
            catch (Exception exception) {
                continue;
            }
            if (n4 < this.theMinTP[n6 - 1]) {
                this.theMinTP[n6 - 1] = n4;
            }
            if (n4 <= this.theMaxTP[n6 - 1]) continue;
            this.theMaxTP[n6 - 1] = n4;
        }
        return true;
    }

    public ImagePlus computeProjection(ImagePlus imagePlus, int n) {
        IJ.redirectErrorMessages();
        int n2 = imagePlus.getStackSize();
        if (n2 == 0) {
            IJ.log((String)"Empty stack.");
        }
        ImageStack imageStack = imagePlus.createEmptyStack();
        ZProjector zProjector = new ZProjector(imagePlus);
        for (int i = 0; i < n2 / n; ++i) {
            zProjector.setStartSlice(i * n + 1);
            zProjector.setStopSlice((i + 1) * n);
            zProjector.setMethod(this.defaultMethod);
            zProjector.doProjection();
            ImagePlus imagePlus2 = zProjector.getProjection();
            ImageProcessor imageProcessor = imagePlus2.getProcessor();
            imageStack.addSlice("Proj. " + (i + 1), imageProcessor);
        }
        ImagePlus imagePlus3 = new ImagePlus("Projection of " + imagePlus.getTitle(), imageStack);
        return imagePlus3;
    }

    public boolean writeTIFF(ImagePlus imagePlus, String string, String string2) {
        if (imagePlus == null) {
            return false;
        }
        if (!string2.endsWith(File.separator)) {
            string2 = new String(string2 + File.separator);
        }
        String string3 = string2 + string;
        FileInfo fileInfo = imagePlus.getFileInfo();
        Object object = imagePlus.getProperty("Info");
        if (object != null && object instanceof String) {
            fileInfo.info = (String)object;
        }
        String string4 = new FileSaver(imagePlus).getDescriptionString();
        fileInfo.sliceLabels = imagePlus.getImageStack().getSliceLabels();
        try {
            TiffEncoder tiffEncoder = new TiffEncoder(fileInfo);
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(string3)));
            tiffEncoder.write(dataOutputStream);
            dataOutputStream.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return false;
        }
        return true;
    }
}

