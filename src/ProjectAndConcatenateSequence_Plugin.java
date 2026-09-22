/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.IJ
 *  ij.ImagePlus
 *  ij.ImageStack
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

    public void run(String args) {
        String imageName;
        int ind = -1;
        int first = -1;
        int last = -1;
        IJ.redirectErrorMessages();

        // OpenDialog od = new OpenDialog("Select a file in the folder to process ...", this.currDir);
        // this.currDir = od.getDirectory();
        // if (this.currDir == null) {
        //     return;
        // }
        String options = Macro.getOptions();

        if (options != null) {
            this.currDir = Macro.getValue(options, "directory", "");
        } else {
            OpenDialog od = new OpenDialog(
                "Select a file in the folder to process ...",
                this.currDir
            );
            this.currDir = od.getDirectory();
        }

        if (this.currDir == null || this.currDir.equals("")) {
            return;
        }

        boolean projectAll = true;
        String sliceRange = IJ.getString((String)"Slices to project (first(min is 1)-last):", (String)"");
        if (sliceRange != null && !sliceRange.equals("")) {
            ind = sliceRange.indexOf("-");
            first = Integer.valueOf(sliceRange.substring(0, ind));
            last = Integer.valueOf(sliceRange.substring(ind + 1));
            projectAll = false;
        }
        if ((imageName = IJ.getString((String)"Output image base name:", (String)"")) == null) {
            return;
        }
        this.readMovieInfo();
        System.out.println("There are " + this.nMovies + " movies in this folder.");
        System.out.println("There are " + this.nWavelengths + " wavelengths per movie.");
        for (int ii = 0; ii < MAX_MOVIES; ++ii) {
            if (!this.theMovies[ii]) continue;
            System.out.println("Movie #" + (ii + 1) + " starts at " + this.theMinTP[ii] + " and ends at " + this.theMaxTP[ii]);
        }
        for (int iw = 0; iw < MAX_WAVELENGTHS; ++iw) {
            if (!this.theWavelengths[iw]) continue;
            ImageStack finalStack = null;
            int finalcount = 0;
            for (int im = 0; im < MAX_MOVIES; ++im) {
                if (!this.theMovies[im]) continue;
                System.out.print("Processing movie #" + (im + 1) + ", wavelength #" + (iw + 1) + " ... ");
                ImageStack stack = null;
                int width = 0;
                int height = 0;
                int depth = 0;
                int bitDepth = 0;
                int count = 0;
                ColorModel cm = null;
                for (int it = this.theMinTP[im]; it <= this.theMaxTP[im]; ++it) {
                    String fileName = null;
                    fileName = !this.channelNames[iw].equals("") ? new String(this.baseName + "n" + (im + 1) + "_w" + (iw + 1) + this.channelNames[iw] + "_t" + it + ".TIF") : new String(this.baseName + "n" + (im + 1) + "_t" + it + ".TIF");
                    try {
                        Opener opener = new Opener();
                        opener.setSilentMode(true);
                        ImagePlus imp = opener.openImage(this.currDir, fileName);
                        if (imp != null && stack == null) {
                            width = imp.getWidth();
                            height = imp.getHeight();
                            depth = imp.getStackSize();
                            bitDepth = imp.getBitDepth();
                            cm = imp.getProcessor().getColorModel();
                            stack = new ImageStack(width, height, cm);
                        }
                        if (imp == null) {
                            if (fileName.startsWith(".")) continue;
                            IJ.log((String)(fileName + ": unable to open"));
                            continue;
                        }
                        if (imp.getWidth() != width || imp.getHeight() != height) {
                            IJ.log((String)(fileName + ": wrong size; " + width + "x" + height + " expected, " + imp.getWidth() + "x" + imp.getHeight() + " found"));
                            continue;
                        }
                        ImageStack inputStack = imp.getStack();
                        if (projectAll) {
                            first = 1;
                            last = inputStack.getSize();
                        } else {
                            if (first < 1) {
                                first = 1;
                            }
                            if (first > inputStack.getSize()) {
                                last = first = inputStack.getSize();
                            }
                            if (last < 1) {
                                first = last = 1;
                            }
                            if (last > inputStack.getSize()) {
                                last = inputStack.getSize();
                            }
                            depth = last - first + 1;
                        }
                        for (int slice = first; slice <= last; ++slice) {
                            ImageProcessor ip = inputStack.getProcessor(slice);
                            int bitDepth2 = imp.getBitDepth();
                            if (bitDepth2 != bitDepth) {
                                if (bitDepth == 8) {
                                    ip = ip.convertToByte(true);
                                    bitDepth2 = 8;
                                } else if (bitDepth == 24) {
                                    ip = ip.convertToRGB();
                                    bitDepth2 = 24;
                                }
                            }
                            if (bitDepth2 != bitDepth) {
                                IJ.log((String)(fileName + ": wrong bit depth; " + bitDepth + " expected, " + bitDepth2 + " found"));
                                break;
                            }
                            stack.addSlice("(" + ++count + ")", ip);
                        }
                        if (!IJ.escapePressed()) continue;
                        IJ.beep();
                        break;
                    }
                    catch (Exception ex) {
                        System.out.println("\nError loading " + fileName + ".\n");
                        ex.printStackTrace();
                        return;
                    }
                }
                ImagePlus projected = this.computeProjection(new ImagePlus("Projection of movie #" + (im - 1) + " - " + this.channelNames[iw], stack), depth);
                ImageStack projStack = projected.getImageStack();
                if (finalcount == 0) {
                    finalStack = new ImageStack(width, height, cm);
                }
                for (int slice = 1; slice <= projStack.getSize(); ++slice) {
                    ImageProcessor ip = projStack.getProcessor(slice);
                    finalStack.addSlice("(" + ++finalcount + ")", ip);
                }
                stack = null;
                projected = null;
                projStack = null;
                System.gc();
                System.out.println("done!");
            }
            this.writeTIFF(new ImagePlus("Concatenated movie - " + this.channelNames[iw], finalStack), imageName + "_" + this.channelNames[iw] + ".tif", this.currDir);
            finalStack = null;
            System.gc();
        }
    }

    private boolean readMovieInfo() {
        int ii;
        if (this.currDir == null) {
            return false;
        }
        File dir = new File(this.currDir);
        String fileName = null;
        boolean boolNoWavelengthName = false;
        for (ii = 0; ii < MAX_WAVELENGTHS; ++ii) {
            this.theWavelengths[ii] = false;
        }
        for (ii = 0; ii < MAX_MOVIES; ++ii) {
            this.theMovies[ii] = false;
            this.theMinTP[ii] = Integer.MAX_VALUE;
            this.theMaxTP[ii] = Integer.MIN_VALUE;
        }
        this.baseName = null;
        if (!dir.isDirectory()) {
            return false;
        }
        File[] theFiles = dir.listFiles();
        this.nMovies = 0;
        int lastMovieVisited = -1;
        int lastWavelengthVisited = -1;
        for (int iF = 0; iF < theFiles.length; ++iF) {
            int currentTP;
            int currentWavelength;
            int currentMovie;
            int i2;
            int i1;
            block17: {
                fileName = theFiles[iF].getName();
                i1 = fileName.lastIndexOf("_w");
                if (i1 < 0) {
                    i1 = fileName.lastIndexOf("_t");
                    if (i1 < 0) continue;
                    boolNoWavelengthName = true;
                }
                if ((i2 = fileName.substring(0, i1).lastIndexOf("n")) < 0) continue;
                if (this.baseName == null) {
                    this.baseName = fileName.substring(0, i2);
                }
                try {
                    currentMovie = Integer.valueOf(fileName.substring(i2 + 1, i1));
                }
                catch (Exception e) {
                    continue;
                }
                if (!this.theMovies[currentMovie - 1]) {
                    this.theMovies[currentMovie - 1] = true;
                    ++this.nMovies;
                }
                if (!boolNoWavelengthName) {
                    i2 = (i1 += 2) + 1;
                    try {
                        currentWavelength = Integer.valueOf(fileName.substring(i1, i2));
                        break block17;
                    }
                    catch (Exception e) {
                        continue;
                    }
                }
                currentWavelength = 1;
            }
            if (!this.theWavelengths[currentWavelength - 1]) {
                this.theWavelengths[currentWavelength - 1] = true;
                ++this.nWavelengths;
            }
            i1 = fileName.lastIndexOf("_t") + 2;
            this.channelNames[currentWavelength - 1] = !boolNoWavelengthName ? fileName.substring(i2, i1 - 2) : new String("");
            i2 = fileName.lastIndexOf(".");
            try {
                currentTP = Integer.valueOf(fileName.substring(i1, i2));
            }
            catch (Exception e) {
                continue;
            }
            if (currentTP < this.theMinTP[currentMovie - 1]) {
                this.theMinTP[currentMovie - 1] = currentTP;
            }
            if (currentTP <= this.theMaxTP[currentMovie - 1]) continue;
            this.theMaxTP[currentMovie - 1] = currentTP;
        }
        return true;
    }

    public ImagePlus computeProjection(ImagePlus imp, int gs) {
        IJ.redirectErrorMessages();
        int imp_size = imp.getStackSize();
        if (imp_size == 0) {
            IJ.log((String)"Empty stack.");
        }
        ImageStack out_stack = imp.createEmptyStack();
        ZProjector zproj = new ZProjector(imp);
        for (int t = 0; t < imp_size / gs; ++t) {
            zproj.setStartSlice(t * gs + 1);
            zproj.setStopSlice((t + 1) * gs);
            zproj.setMethod(this.defaultMethod);
            zproj.doProjection();
            ImagePlus projection = zproj.getProjection();
            ImageProcessor improc = projection.getProcessor();
            out_stack.addSlice("Proj. " + (t + 1), improc);
        }
        ImagePlus out_image = new ImagePlus("Projection of " + imp.getTitle(), out_stack);
        return out_image;
    }

    public boolean writeTIFF(ImagePlus imp, String name, String directory) {
        if (imp == null) {
            return false;
        }
        if (!directory.endsWith(File.separator)) {
            directory = new String(directory + File.separator);
        }
        String path = directory + name;
        FileInfo fi = imp.getFileInfo();
        Object info = imp.getProperty("Info");
        if (info != null && info instanceof String) {
            fi.info = (String)info;
        }
        String description = new FileSaver(imp).getDescriptionString();
        fi.sliceLabels = imp.getImageStack().getSliceLabels();
        try {
            TiffEncoder file = new TiffEncoder(fi);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
            file.write(out);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
