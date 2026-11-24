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
import ij.gui.GenericDialog;
import java.awt.image.ColorModel;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    // new fields
    String parentDir;
    String incrementMode;
    String outputBaseName;
    int projFirst = -1;
    int projLast = -1;
    boolean projectAll = true;

    Map<String, List<Integer>> movieGroups = new LinkedHashMap<>();
    private List<Integer> ignoredMovies = new ArrayList<>();
    // boolean deleteRawFiles = false;

    // new run method
    @Override
    public void run(String args) {

        // TODO: delete before release
        // IJ.showMessage("NEW VERSION");

        if (!showFirstDialog())
            return;

        if(!showSecondDialog())
            return;

        sortFiles();

        for (int groupNum = 1; groupNum <= movieGroups.size(); groupNum++) {

            String groupDir = parentDir + "movie" + groupNum + File.separator;

            File dir = new File(groupDir);

            if (!dir.exists())
                continue;

            IJ.log("Processing group " + groupNum);

            processEmbryo(groupDir, groupNum);
        }

        // if (deleteRawFiles) {
        deleteRawData();
        // }
        
        // TODO: delete before release
        // IJ.showMessage("Finished");
    }

    private boolean showFirstDialog() {

        // prompt user for processing settings
        GenericDialog gd = new GenericDialog("Processing Settings");

        // user enters parent directory
        gd.addDirectoryField("Parent folder", "");

        String[] choices = {"By 2 (Image and pulse line and go)", "By 1 (Pulse line and go)"};

        gd.addChoice("Increment", choices, choices[0]);

        gd.addStringField("Slices to project (blank = all)", "");

        // to do: change this and actually make it work
        // gd.addStringField("Output image base name", "projection");

        // gd.addCheckbox("Delete raw data after processing", true);

        gd.showDialog();

        if (gd.wasCanceled())
            return false;

        parentDir = gd.getNextString();

        if (!parentDir.endsWith(File.separator))
            parentDir += File.separator;

        incrementMode = gd.getNextChoice();

        String sliceRange = gd.getNextString();

        // outputBaseName = gd.getNextString();

        // deleteRawFiles = gd.getNextBoolean();

        if (sliceRange != null && !sliceRange.trim().equals("")) {
            int dash = sliceRange.indexOf("-");
            projFirst = Integer.parseInt(sliceRange.substring(0, dash));
            projLast = Integer.parseInt(sliceRange.substring(dash + 1));
            projectAll = false;
        }

        return true;
    }

    private boolean showSecondDialog() {

        GenericDialog gd = new GenericDialog("Group Selection");

        List<String> movieNamesList = new ArrayList<>();

        File parent = new File(parentDir);
        File[] files = parent.listFiles();

        if (files == null)
            return false;

        for (File f : files) {

            String name = f.getName();

            if (!name.toLowerCase().endsWith(".tif"))
                continue;

            int underscore = name.indexOf("_");

            if (underscore < 0)
                continue;

            String prefix = name.substring(0, underscore);

            if (prefix.matches("n\\d+") && !movieNamesList.contains(prefix)) {

                movieNamesList.add(prefix);
            }
        }

        movieNamesList.sort((a, b) -> {
            int numA = Integer.parseInt(a.substring(1));
            int numB = Integer.parseInt(b.substring(1));
            return Integer.compare(numA, numB);
        });

        String[] movieNames = movieNamesList.toArray(new String[0]);
        String[] groups = new String[movieNames.length + 1];
        
        for (int i = 0; i < movieNames.length; i++) {
            groups[i] = "Group " + (i + 1);
        }

        groups[movieNames.length] = "Ignore";
        
        // create one dropdown per movie and set the default option based on the increment mode selected
        for (int i = 0; i < movieNames.length; i++) {

            String defaultGroup;

            if (incrementMode.equals("By 2 (Image and pulse line and go)")) {
                // group defaults are set to increment by 2
                defaultGroup = "Group " + ((i / 2) + 1);
            } else {
                // group defaults are set to increment by 1
                defaultGroup = "Group " + (i + 1);
            }

            gd.addChoice(movieNames[i], groups, defaultGroup);
        }

        gd.showDialog();

        if (gd.wasCanceled()) {
            return false;
        }

        movieGroups.clear();
        ignoredMovies.clear();

        // read the user's group selections
        for (String movie : movieNames) {

            String group = gd.getNextChoice();
            int movieNumber = Integer.parseInt(movie.substring(1));

            if (group.equals("Ignore")) {
                ignoredMovies.add(movieNumber);
                continue;
            }

            movieGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(movieNumber);
        }

        return true;
    }

    private void sortFiles() {

        IJ.log("Sorting files...");

        File parent = new File(parentDir);

        File[] files = parent.listFiles();

        if (files == null)
            return;

        int embryoNum = 1;

        for (List<Integer> groupMovies : movieGroups.values()) {

            File target = new File(parentDir + "movie" + embryoNum);

            if (!target.exists())
                target.mkdirs();

            for (File f : files) {

                String curr = f.getName();

                for (Integer movieNum : groupMovies) {

                    String prefix = "n" + movieNum;

                    if (curr.startsWith(prefix + "_") || curr.startsWith(prefix + ".")) {

                        f.renameTo(new File(target, curr));

                        break;
                    }
                }
            }

            embryoNum++;
        }

        // move ignored movies
        if (!ignoredMovies.isEmpty()) {
            File ignoredDir = new File(parent, "ignored files");
            ignoredDir.mkdirs();

            for (File f : files) {

                if (!f.isFile())
                    continue;

                String curr = f.getName();

                for (Integer movieNum : ignoredMovies) {

                    String prefix = "n" + movieNum;

                    if (curr.startsWith(prefix + "_") ||
                        curr.startsWith(prefix + ".")) {

                        f.renameTo(new File(ignoredDir, curr));
                        break;
                    }
                }
            }
        }



        // if (incrementMode.equals("Pulse line and go")) {

        //     for (File f : files) {

        //         String curr = f.getName();

        //         for (int embryo = startAt; embryo < startAt + numFolders; embryo++) {

        //             String prefix = "n" + embryo;

        //             if (curr.startsWith(prefix + "_") || curr.startsWith(prefix + ".")) {

        //                 File target = new File(parentDir + "movie" + embryo);

        //                 if (!target.exists())
        //                     target.mkdirs();

        //                 f.renameTo(new File(target,curr));

        //                 break;
        //             }
        //         }
        //     }
        // }
        // else {
        //     int embryoNum = 1;

        //     for (int movie = startAt; movie < numFolders * 2; movie += 2) {

        //         File target = new File(parentDir + "movie" + embryoNum);

        //         target.mkdirs();

        //         final String prefix1 = "n" + movie;

        //         final String prefix2 = "n" + (movie + 1);

        //         for (File f : files) {

        //             String curr = f.getName();

        //             if (curr.startsWith(prefix1 + "_") || curr.startsWith(prefix1 + ".")||
        //                 curr.startsWith(prefix2 + "_") || curr.startsWith(prefix2 + ".")) {
                        
        //                 f.renameTo(new File(target, curr));
        //             }
        //         }

        //         embryoNum++;
        //     }
        // }

        IJ.log("Sorting complete");
    }


    private void processEmbryo(String embryoDir, int movieNum) {
        this.currDir = embryoDir;

        readMovieInfo();

        for (int iw = 0; iw < MAX_WAVELENGTHS; ++iw) {

            if (!theWavelengths[iw])
                continue;

            ImageStack finalStack = null;
            int finalcount = 0;
            for (int im = 0; im < MAX_MOVIES; ++im) {
                if (!this.theMovies[im]) 
                    continue;
                //System.out.print("Processing movie #" + (im + 1) + ", wavelength #" + (iw + 1) + " ... ");
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
                        // added next two lines
                        int first = projFirst;
                        int last = projLast;
                        ImageStack inputStack = imp.getStack();
                        if (projectAll) {
                            first = 1;
                            last = inputStack.getSize();
                            depth = last - first + 1;
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
                        // System.out.println("\nError loading " + fileName + ".\n");
                        ex.printStackTrace();
                        return;
                    }
                }
                if (stack == null) {
                    IJ.log("No images found for movie " + (im + 1));
                    continue;
                }
                ImagePlus projected = this.computeProjection(new ImagePlus("Projection of movie #" + (im + 1) + " - " + this.channelNames[iw], stack), depth);
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
                //System.gc();
                //System.out.println("done!");
            }
            if (finalStack == null) continue;
            this.writeTIFF(new ImagePlus("Concatenated movie - " + this.channelNames[iw], finalStack), movieNum + "_" + this.channelNames[iw] + ".tif", this.currDir);
            finalStack = null;
            //System.gc();
        }
    }

    private void deleteRawData() {
        IJ.log("Moving raw files...");

        int numGroups = movieGroups.size();

        for (int group = 1; group <= numGroups; group++) {

            File movieDir = new File(parentDir + "movie" + group);

            if (!movieDir.exists())
                continue;

            File rawDir = new File(parentDir, "raw files");

            if (!rawDir.exists())
                rawDir.mkdirs();

            File[] files = movieDir.listFiles();

            if (files == null)
                continue;

            for (File f : files) {

                String name = f.getName();

                if (name.matches("^n\\d+.*")) {
                    File destination = new File(rawDir, name);
              
                    if (!f.renameTo(destination)) {
                        IJ.log("Could not move: " + name);
                    }
                }
            }
        }

        /*
        IJ.log("Deleting raw files...");

        int numGroups = movieGroups.size();

        for (int group = 1; group <= numGroups; group++) {

            File movieDir = new File(parentDir + "movie" + group);

            if (!movieDir.exists())
                continue;

            File[] files = movieDir.listFiles();

            if (files == null)
                continue;

            for (File f : files) {

                String name = f.getName();

                if (name.matches("^n\\d+.*")) {

                    if (!f.delete()) {
                        IJ.log("Could not delete: " + name);
                    }
                }
            }
        }
        */

        IJ.log("Cleanup complete");
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
