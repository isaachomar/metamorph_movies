/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.plugin.PlugIn
 */
import ij.plugin.PlugIn;

public class Export_ImagesPlugin
implements PlugIn {
    public void run(String args) {
        ExportJFrame njf = new ExportJFrame();
        njf.setVisible(true);
    }
}

