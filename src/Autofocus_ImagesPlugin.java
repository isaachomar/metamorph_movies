/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.plugin.PlugIn
 */
import ij.plugin.PlugIn;

public class Autofocus_ImagesPlugin
implements PlugIn {
    public void run(String args) {
        AutofocusJFrame njf = new AutofocusJFrame();
        njf.setVisible(true);
    }
}

