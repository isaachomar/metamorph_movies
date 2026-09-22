/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ij.ImagePlus
 *  ij.io.FileInfo
 *  ij.io.FileSaver
 *  ij.io.OpenDialog
 *  ij.io.Opener
 *  ij.io.TiffEncoder
 *  ij.plugin.ZProjector
 */
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.io.TiffEncoder;
import ij.plugin.ZProjector;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

public class ProjectJFrame
extends JFrame {
    String currentDirectory = null;
    String aFileName = null;
    String baseName = null;
    int[] parameters = null;
    Open_MoviePlugin mov = null;
    private JButton coolButton;
    private JButton dirButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JTextField jTextField2;
    private JButton nowayButton;
    private JTextField tfFirstTP;
    private JTextField tfFirstZ;
    private JTextField tfLastTP;
    private JTextField tfLastZ;

    public ProjectJFrame() {
        this.initComponents();
        this.setTitle("A small tool to project Metamorph movies");
        this.parameters = this.readDataInfo();
        this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
        this.tfLastTP.setText(String.valueOf(this.parameters[1]));
        this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
        this.tfLastZ.setText(String.valueOf(this.parameters[3]));
    }

    public ProjectJFrame(boolean showWindow) {
        if (showWindow) {
            this.initComponents();
            this.setTitle("A small tool to project Metamorph movies");
        }
        this.parameters = this.readDataInfo();
        this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
        this.tfLastTP.setText(String.valueOf(this.parameters[1]));
        this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
        this.tfLastZ.setText(String.valueOf(this.parameters[3]));
    }

    private void initComponents() {
        this.dirButton = new JButton();
        this.tfFirstTP = new JTextField();
        this.jTextField2 = new JTextField();
        this.tfFirstZ = new JTextField();
        this.tfLastTP = new JTextField();
        this.tfLastZ = new JTextField();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.coolButton = new JButton();
        this.nowayButton = new JButton();
        this.setDefaultCloseOperation(3);
        this.dirButton.setText("Select movie file ...");
        this.dirButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.dirButtonActionPerformed(evt);
            }
        });
        this.tfFirstTP.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.tfFirstTPActionPerformed(evt);
            }
        });
        this.tfFirstTP.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent evt) {
                ProjectJFrame.this.tfFirstTPFocusGained(evt);
            }

            public void focusLost(FocusEvent evt) {
                ProjectJFrame.this.tfFirstTPFocusLost(evt);
            }
        });
        this.jTextField2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.jTextField2ActionPerformed(evt);
            }
        });
        this.jTextField2.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent evt) {
                ProjectJFrame.this.jTextField2FocusGained(evt);
            }

            public void focusLost(FocusEvent evt) {
                ProjectJFrame.this.jTextField2FocusLost(evt);
            }
        });
        this.tfFirstZ.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.tfFirstZActionPerformed(evt);
            }
        });
        this.tfFirstZ.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent evt) {
                ProjectJFrame.this.tfFirstZFocusGained(evt);
            }

            public void focusLost(FocusEvent evt) {
                ProjectJFrame.this.tfFirstZFocusLost(evt);
            }
        });
        this.tfLastTP.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.tfLastTPActionPerformed(evt);
            }
        });
        this.tfLastTP.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent evt) {
                ProjectJFrame.this.tfLastTPFocusGained(evt);
            }

            public void focusLost(FocusEvent evt) {
                ProjectJFrame.this.tfLastTPFocusLost(evt);
            }
        });
        this.tfLastZ.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.tfLastZActionPerformed(evt);
            }
        });
        this.tfLastZ.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent evt) {
                ProjectJFrame.this.tfLastZFocusGained(evt);
            }

            public void focusLost(FocusEvent evt) {
                ProjectJFrame.this.tfLastZFocusLost(evt);
            }
        });
        this.jLabel1.setText("First time point");
        this.jLabel2.setText("First plane");
        this.jLabel3.setText("Last time point");
        this.jLabel4.setText("Last plane");
        this.coolButton.setText("Cool");
        this.coolButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.coolButtonActionPerformed(evt);
            }
        });
        this.nowayButton.setText("No way");
        this.nowayButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                ProjectJFrame.this.nowayButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(43, 43, 43).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jTextField2, -2, 328, -2).addGap(18, 18, 18).addComponent(this.dirButton)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.tfFirstZ, GroupLayout.Alignment.LEADING).addComponent(this.tfFirstTP, GroupLayout.Alignment.LEADING, -1, 69, Short.MAX_VALUE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabel1).addComponent(this.jLabel2).addComponent(this.coolButton)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(90, 90, 90).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.tfLastZ).addComponent(this.tfLastTP, -1, 69, Short.MAX_VALUE)).addGap(32, 32, 32).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel3, GroupLayout.Alignment.TRAILING).addComponent(this.jLabel4, GroupLayout.Alignment.TRAILING))).addGroup(layout.createSequentialGroup().addGap(29, 29, 29).addComponent(this.nowayButton))))).addGap(16, 16, 16)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(32, 32, 32).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jTextField2, -2, -1, -2).addComponent(this.dirButton)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(55, 55, 55).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.tfFirstTP, -2, -1, -2).addComponent(this.tfLastTP, -2, -1, -2)).addGap(58, 58, 58).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.tfFirstZ, -2, -1, -2))).addGroup(layout.createSequentialGroup().addGap(63, 63, 63).addComponent(this.jLabel3).addGap(58, 58, 58).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.tfLastZ, -2, -1, -2)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.coolButton).addComponent(this.nowayButton)).addGap(27, 27, 27)));
        this.pack();
    }

    private void jTextField2ActionPerformed(ActionEvent evt) {
        this.currentDirectory = this.jTextField2.getText();
        this.parameters = this.readDataInfo();
        this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
        this.tfLastTP.setText(String.valueOf(this.parameters[1]));
        this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
        this.tfLastZ.setText(String.valueOf(this.parameters[3]));
        this.mov = new Open_MoviePlugin();
        this.mov.run(this.currentDirectory + File.separator + this.baseName + "_w1488" + "_t" + this.parameters[0] + ".TIF");
        this.mov.sett(1);
    }

    private void tfFirstTPActionPerformed(ActionEvent evt) {
        this.parameters[0] = Integer.valueOf(this.tfFirstTP.getText());
    }

    private void dirButtonActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        if (null == this.currentDirectory || this.currentDirectory.equals("")) {
            this.currentDirectory = OpenDialog.getDefaultDirectory();
        }
        chooser.setCurrentDirectory(new File(this.currentDirectory));
        chooser.setFileFilter(new ImageFilter());
        int returnVal = chooser.showOpenDialog(this.getParent());
        if (returnVal == 0) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            this.currentDirectory = path.substring(0, path.lastIndexOf(File.separator));
            this.aFileName = path.substring(path.lastIndexOf(File.separator) + 1);
            int ind1 = this.aFileName.indexOf("_w") + 2;
            int ind2 = this.aFileName.indexOf("_t");
            String wavelength = this.aFileName.substring(ind1, ind2);
            ind1 = this.aFileName.lastIndexOf(".");
            String ext = this.aFileName.substring(ind1);
            this.jTextField2.setText(path);
            this.parameters = this.readDataInfo();
            this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
            this.tfLastTP.setText(String.valueOf(this.parameters[1]));
            this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
            this.tfLastZ.setText(String.valueOf(this.parameters[3]));
            if (this.mov != null) {
                if (this.mov.img != null) {
                    this.mov.img.close();
                    this.mov.img.flush();
                }
                this.mov = null;
            }
            this.mov = new Open_MoviePlugin();
            this.mov.run(this.currentDirectory + File.separator + this.baseName + "_w" + wavelength + "_t" + this.parameters[0] + ext);
            this.mov.sett(1);
        }
    }

    private void tfLastTPActionPerformed(ActionEvent evt) {
        this.parameters[1] = Integer.valueOf(this.tfLastTP.getText());
    }

    private void nowayButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void coolButtonActionPerformed(ActionEvent evt) {
        if (this.currentDirectory == null || this.aFileName == null) {
            return;
        }
        File exportDir = new File(this.currentDirectory + File.separator + "projected_" + this.baseName);
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        this.projectData(exportDir);
        this.parameters[0] = this.parameters[1] + 1;
        this.parameters[1] = this.parameters[1] + 1;
        this.parameters[2] = this.parameters[2] + 1;
        this.parameters[3] = this.parameters[3] + 1;
        this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
        this.tfLastTP.setText(String.valueOf(this.parameters[1]));
        this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
        this.tfLastZ.setText(String.valueOf(this.parameters[3]));
    }

    private void tfFirstZActionPerformed(ActionEvent evt) {
        this.parameters[2] = Integer.valueOf(this.tfFirstZ.getText());
    }

    private void tfLastZActionPerformed(ActionEvent evt) {
        this.parameters[3] = Integer.valueOf(this.tfLastZ.getText());
    }

    private void jTextField2FocusLost(FocusEvent evt) {
        this.currentDirectory = this.jTextField2.getText();
        this.parameters = this.readDataInfo();
        this.tfFirstTP.setText(String.valueOf(this.parameters[0]));
        this.tfLastTP.setText(String.valueOf(this.parameters[1]));
        this.tfFirstZ.setText(String.valueOf(this.parameters[2]));
        this.tfLastZ.setText(String.valueOf(this.parameters[3]));
    }

    private void tfFirstTPFocusLost(FocusEvent evt) {
        this.parameters[0] = Integer.valueOf(this.tfFirstTP.getText());
    }

    private void tfLastTPFocusLost(FocusEvent evt) {
        this.parameters[1] = Integer.valueOf(this.tfLastTP.getText());
    }

    private void tfFirstZFocusLost(FocusEvent evt) {
        this.parameters[2] = Integer.valueOf(this.tfFirstZ.getText());
    }

    private void tfLastZFocusLost(FocusEvent evt) {
        this.parameters[3] = Integer.valueOf(this.tfLastZ.getText());
    }

    private void jTextField2FocusGained(FocusEvent evt) {
        this.jTextField2.setSelectionStart(0);
        this.jTextField2.setSelectionEnd(this.jTextField2.getText().length());
    }

    private void tfFirstTPFocusGained(FocusEvent evt) {
        this.tfFirstTP.setSelectionStart(0);
        this.tfFirstTP.setSelectionEnd(this.tfFirstTP.getText().length());
    }

    private void tfLastTPFocusGained(FocusEvent evt) {
        this.tfLastTP.setSelectionStart(0);
        this.tfLastTP.setSelectionEnd(this.tfLastTP.getText().length());
    }

    private void tfFirstZFocusGained(FocusEvent evt) {
        this.tfFirstZ.setSelectionStart(0);
        this.tfFirstZ.setSelectionEnd(this.tfFirstZ.getText().length());
    }

    private void tfLastZFocusGained(FocusEvent evt) {
        this.tfLastZ.setSelectionStart(0);
        this.tfLastZ.setSelectionEnd(this.tfLastZ.getText().length());
    }

    private int[] readDataInfo() {
        int m;
        int[] result = new int[4];
        int firstTP = 0;
        int lastTP = 0;
        boolean firstZ = false;
        boolean lastZ = false;
        result[0] = firstTP;
        result[1] = lastTP;
        result[2] = 0;
        result[3] = 0;
        if (this.currentDirectory == null || this.aFileName == null) {
            return result;
        }
        File dir = new File(this.currentDirectory);
        if (!dir.isDirectory()) {
            return null;
        }
        File[] theFiles = dir.listFiles();
        String fileName = this.aFileName;
        int i1 = fileName.lastIndexOf("_w");
        if (i1 < 0 && (i1 = fileName.lastIndexOf("_t")) < 0) {
            return result;
        }
        this.baseName = fileName.substring(0, i1);
        i1 = fileName.lastIndexOf("_t");
        if (i1 < 0) {
            return result;
        }
        int i2 = fileName.indexOf(".");
        if (i2 < i1) {
            return result;
        }
        firstTP = m = Integer.valueOf(fileName.substring(i1 + 2, i2)).intValue();
        lastTP = m;
        try {
            ImagePlus imp = new Opener().openImage(this.currentDirectory, fileName);
            if (imp != null) {
                int width = imp.getWidth();
                int height = imp.getHeight();
                int depth = imp.getStackSize();
                result[2] = 1;
                result[3] = depth;
            }
        }
        catch (Exception ex) {
            System.out.println("Error opening " + fileName + ".\n");
            return result;
        }
        for (int iF = 0; iF < theFiles.length; ++iF) {
            fileName = theFiles[iF].getName();
            i1 = fileName.lastIndexOf("_t");
            if (i1 < 0 || (i2 = fileName.indexOf(".")) < i1) continue;
            try {
                m = Integer.valueOf(fileName.substring(i1 + 2, i2));
            }
            catch (Exception e) {
                continue;
            }
            firstTP = Math.min(firstTP, m);
            lastTP = Math.max(lastTP, m);
        }
        result[0] = firstTP;
        result[1] = lastTP;
        return result;
    }

    private boolean projectData(File targetDir) {
        if (this.currentDirectory == null || this.aFileName == null) {
            return false;
        }
        File dir = new File(this.currentDirectory);
        int firstTP = this.parameters[0] = Integer.valueOf(this.tfFirstTP.getText()).intValue();
        int lastTP = this.parameters[1] = Integer.valueOf(this.tfLastTP.getText()).intValue();
        int firstZ = this.parameters[2] = Integer.valueOf(this.tfFirstZ.getText()).intValue();
        int lastZ = this.parameters[3] = Integer.valueOf(this.tfLastZ.getText()).intValue();
        if (!dir.isDirectory()) {
            return false;
        }
        File[] theFiles = dir.listFiles();
        for (int iF = 0; iF < theFiles.length; ++iF) {
            int m;
            int n;
            String theBaseName;
            String fileName = theFiles[iF].getName();
            int i1 = fileName.lastIndexOf("_w");
            if (i1 < 0 && (i1 = fileName.lastIndexOf("_t")) < 0 || !this.baseName.equals(theBaseName = fileName.substring(0, i1))) continue;
            int i2 = fileName.indexOf("488");
            if (i2 < i1) {
                i2 = fileName.indexOf("568");
            }
            if (i2 > i1 + 2) {
                n = Integer.valueOf(fileName.substring(i1 + 2, i2));
            } else {
                i2 = fileName.lastIndexOf("_t");
                n = 1;
            }
            i1 = fileName.lastIndexOf("_t");
            if (i1 < 0) continue;
            String channelName = fileName.substring(i2, i1);
            i2 = fileName.indexOf(".");
            if (i2 < i1 || (m = Integer.valueOf(fileName.substring(i1 + 2, i2)).intValue()) < firstTP || m > lastTP) continue;
            try {
                ImagePlus imp = new Opener().openImage(this.currentDirectory, fileName);
                if (imp == null) continue;
                int width = imp.getWidth();
                int height = imp.getHeight();
                int depth = imp.getStackSize();
                ZProjector proj = new ZProjector(imp);
                proj.setMethod(1);
                proj.setStartSlice(firstZ);
                proj.setStopSlice(lastZ);
                proj.doProjection();
                ImagePlus projection = proj.getProjection();
                this.writeTIFF(projection, fileName, targetDir.getAbsolutePath());
                continue;
            }
            catch (Exception ex) {
                System.out.println("Error copying " + fileName + ".\n");
                ex.printStackTrace();
                return false;
            }
        }
        return true;
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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new ExportJFrame().setVisible(true);
            }
        });
    }
}

