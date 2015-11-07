package org.uva.emulation;

import com.cozendey.opl3.OPL3;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Vector;

public class Player extends JApplet implements Runnable, ChangeListener, MouseListener, MenuListener, ActionListener {
    public static final int sampleRate = 49700;
    public Thread opl3PlayerThread = new Thread();
    public Thread saveFileThread = new Thread();
    OPL3 opl = new OPL3();
    JFileChooser jfileChooser;
    File currSaveDir = new File("");
    File currOpenDir = new File("");
    SourceDataLine sourceDataLine;
    CPlayer p;
    byte[] fileBuffer;
    public byte[][] musicBuffer;
    int musicBufferLength;
    int entireMusicLength;
    final double byteToMilliseconds = 0.005030181086519115D;
    public Player.BufferStatus bufferStatus;
    public Player.RunAction runAction;
    public Player.FileType fileType;
    JLabel statusLabel;
    JLabel playingLabel;
    JLabel totalLabel;
    JLabel fileNameLabel;
    JLabel verboseBufferLabel;
    JLabel verbosePlayingLabel;
    JLabel verboseTotalLabel;
    JSlider slider;
    BoundedRangeModel sliderModel;
    JToggleButton playButton;
    JToggleButton pauseButton;
    JButton openButton;
    JButton saveButton;
    FileWriter logFileWriter;
    JMenu examplesMenu;
    JToggleButton examplesButton;
    int bufferedArrayIndex;
    int playingArrayIndex;
    int playingSample;
    int playingOverallPosition;
    int bufferedOverallPosition;
    public boolean isThreadEnding = false;
    boolean positionChanged;
    boolean isWaitingBuffer;
    int buffersWait;
    int liberateBuffers;
    File file;
    int verbose = 0;
    int refSize = 50;
    int frameWidth = 700;
    int frameHeight = 700;

    public static void main(String[] var0) {
        if (var0.length > 0) {
            new Player(Integer.parseInt(var0[0]));
        } else {
            System.exit(1);
        }

    }

    public Player() {
    }

    public Player(int var1) {
        this.verbose = var1;
        JFrame var2 = new JFrame("OPL3 Player");
        var2.setLayout((LayoutManager) null);
        var2.setBounds(0, 0, this.frameWidth, this.frameHeight);
        JLayeredPane var3 = new JLayeredPane();
        this.addCommon(var3, this.frameWidth, this.frameHeight, this.refSize);
        var2.getContentPane().add(var3);
        var2.setVisible(true);
        var2.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
                Player.this.endThreads();
                System.exit(0);
            }
        });
    }

    ImageIcon getPlayIcon(int var1) {
        int var2 = (int) ((double) var1 * 0.25D);
        int var3 = var1 - 2 * var2;
        int[][] var4 = new int[][]{{var2, var2 + var3, var2}};
        int[][] var5 = new int[][]{{var2, var1 / 2, var2 + var3}};
        return this.getIcon(var1, var4, var5);
    }

    ImageIcon getPauseIcon(int var1) {
        int var2 = (int) ((double) var1 * 0.3D) + 1;
        int var3 = (int) ((double) var1 * 0.25D);
        int var4 = var1 - 2 * var2;
        int var5 = var1 - 2 * var3;
        int var6 = var4 / 3;
        int[][] var7 = new int[][]{{var2, var2 + var6, var2 + var6, var2}, {var2 + 2 * var6, var2 + 3 * var6, var2 + 3 * var6, var2 + 2 * var6}};
        int[][] var8 = new int[][]{{var3, var3, var3 + var5, var3 + var5}, {var3, var3, var3 + var5, var3 + var5}};
        return this.getIcon(var1, var7, var8);
    }

    ImageIcon getIcon(int var1, int[][] var2, int[][] var3) {
        BufferedImage var4 = new BufferedImage(var1, var1, 2);
        Graphics var5 = var4.getGraphics();
        var5.setColor(new Color(0, 0, 0, 128));

        for (int var6 = 0; var6 < var2.length; ++var6) {
            var5.fillPolygon(var2[var6], var3[var6], var2[var6].length);
        }

        return new ImageIcon(var4);
    }

    void addCommon(JLayeredPane var1, int var2, int var3, int var4) {
        var1.setBounds(0, 0, var2, var3);
        var1.setLayout((LayoutManager) null);
        byte var5 = 40;
        byte var6 = 5;
        byte var7 = 6;
        byte var8 = 100;
        byte var9 = 30;
        int var10 = var5 + var4;
        byte var11 = 10;
        byte var12 = 10;
        int var13 = var4 / 2;
        JToolBar var15 = new JToolBar();
        var15.setBounds(0, 0, var2, var5);
        var15.setLayout((LayoutManager) null);
        var15.setFloatable(false);
        this.openButton = new JButton("Open");
        this.openButton.setBounds(var6, var7, var8, var9);
        this.openButton.addActionListener(this);
        var15.add(this.openButton);
        JMenuBar var16 = new JMenuBar();
        var16.setBounds(var6 + var8, var7, var8, var9);
        this.addExamplesMenu(var16);
        var1.add(var16);
        this.examplesButton = new JToggleButton("Examples");
        this.examplesButton.setBounds(var6 + var8, var7, var8, var9);
        this.examplesButton.addChangeListener(this);
        var15.add(this.examplesButton);
        this.saveButton = new JButton("Save");
        this.saveButton.setBounds(var6 + 2 * var8, var7, var8, var9);
        this.saveButton.addActionListener(this);
        this.saveButton.setVisible(false);
        var15.add(this.saveButton);
        var1.add(var15, JLayeredPane.PALETTE_LAYER);
        ButtonGroup var17 = new ButtonGroup();
        this.playButton = new JToggleButton();
        this.playButton.setIcon(this.getPlayIcon(var4));
        this.playButton.setBounds(var11, var10, var4, var4);
        this.playButton.addChangeListener(this);
        this.playButton.setEnabled(false);
        var17.add(this.playButton);
        var1.add(this.playButton);
        this.pauseButton = new JToggleButton();
        this.pauseButton.setIcon(this.getPauseIcon(var4));
        this.pauseButton.setBounds(var11 + var4, var10, var4, var4);
        this.pauseButton.setEnabled(false);
        var17.add(this.pauseButton);
        var1.add(this.pauseButton);
        int var18 = var10 + var4 / 3;
        int var19 = var11 + 2 * var4 + var12;
        int var20 = var1.getWidth() - 2 * var11 - var19;
        int var21 = var4 / 3;
        this.slider = new JSlider();
        this.slider.setBounds(var19, var18, var20, var21);
        this.slider.addMouseListener(this);
        this.slider.setEnabled(false);
        var1.add(this.slider);
        this.sliderModel = this.slider.getModel();
        this.sliderModel.setMinimum(0);
        this.sliderModel.setValue(0);
        this.fileNameLabel = new JLabel();
        this.fileNameLabel.setHorizontalAlignment(0);
        this.fileNameLabel.setBounds(var19, var18 - var21, var20, var21);
        var1.add(this.fileNameLabel);
        this.statusLabel = new JLabel();
        this.statusLabel.setHorizontalAlignment(0);
        this.statusLabel.setBounds(var19, var18 + var21, var20, var21);
        this.statusLabel.setText("");
        var1.add(this.statusLabel);
        this.playingLabel = new JLabel();
        this.playingLabel.setHorizontalAlignment(2);
        this.playingLabel.setBounds(var19, var18 + var21, var20, var21);
        var1.add(this.playingLabel);
        this.totalLabel = new JLabel();
        this.totalLabel.setHorizontalAlignment(4);
        this.totalLabel.setBounds(var19, var18 + var21, var20, var21);
        var1.add(this.totalLabel);
        this.verbosePlayingLabel = new JLabel();
        this.verboseBufferLabel = new JLabel();
        this.verboseTotalLabel = new JLabel();
        if (this.verbose >= 2) {
            JLabel var22 = new JLabel("Playing: ");
            int var23 = var10 + 3 * var4;
            byte var25 = 100;
            short var26 = 380;
            var22.setBounds(var11, var23, var25, var4);
            var1.add(var22);
            this.verbosePlayingLabel.setBounds(var25, var23, var26, var4);
            var1.add(this.verbosePlayingLabel);
            var22 = new JLabel("Buffered: ");
            var22.setBounds(var11, var23 + var4, var25, var4);
            var1.add(var22);
            this.verboseBufferLabel.setBounds(var25, var23 + var4, var26, var4);
            var1.add(this.verboseBufferLabel);
            var22 = new JLabel("Total: ");
            var22.setBounds(var11, var23 + 2 * var4, var25, var4);
            var1.add(var22);
            this.verboseTotalLabel.setBounds(var25, var23 + 2 * var4, var26, var4);
            var1.add(this.verboseTotalLabel);
        }

    }

    int getPercent(int var1, int var2) {
        return (int) ((double) var1 / (double) var2 * 100.0D);
    }

    String getTime(int var1) {
        StringBuffer var2 = new StringBuffer();
        int var3 = (int) (0.005030181086519115D * (double) var1 / 1000.0D);
        int var4 = var3 / 3600;
        var3 %= 3600;
        int var5 = var3 / 60;
        var3 %= 60;
        if (var4 != 0) {
            var2.append(var4 + ":");
            if (var5 < 10) {
                var2.append("0");
            }
        }

        var2.append(var5 + ":");
        if (var3 < 10) {
            var2.append("0" + var3);
        } else {
            var2.append(var3);
        }

        return var2.toString();
    }

    public void menuSelected(MenuEvent var1) {
    }

    public void menuDeselected(MenuEvent var1) {
        this.examplesButton.setSelected(false);
    }

    public void menuCanceled(MenuEvent var1) {
        this.examplesButton.setSelected(false);
    }

    public void mouseClicked(MouseEvent var1) {
    }

    public void mousePressed(MouseEvent var1) {
    }

    public void mouseEntered(MouseEvent var1) {
    }

    public void mouseExited(MouseEvent var1) {
    }

    public void mouseReleased(MouseEvent var1) {
        this.positionChanged = true;
    }

    public void stateChanged(ChangeEvent var1) {
        if (var1.getSource().equals(this.playButton) && this.playButton.isSelected() && !this.opl3PlayerThread.isAlive()) {
            this.runAction = Player.RunAction.REPLAY;
            this.startThread();
        } else if (var1.getSource().equals(this.pauseButton) && this.pauseButton.isSelected() && this.sourceDataLine != null) {
            this.sourceDataLine.flush();
        } else if (var1.getSource().equals(this.examplesButton) && this.examplesButton.isSelected()) {
            this.examplesMenu.doClick();
        }

    }

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource().equals(this.openButton)) {
            this.open();
        } else if (var1.getSource().equals(this.saveButton)) {
            if (this.saveFileThread.isAlive()) {
                return;
            }

            this.runAction = Player.RunAction.SAVE;
            this.saveFileThread = new Thread(this);
            this.saveFileThread.start();
        }

    }

    String getExtension(String var1) {
        StringBuffer var2 = new StringBuffer(var1);
        int var3 = var2.lastIndexOf(".");
        return var2.substring(var3 + 1);
    }

    public void run() {
        switch (1) {
            case 1:
                this.loadFile();
                break;
            case 2:
                this.save();
                break;
            case 3:
                this.play();
        }

    }

    void endThread(Thread var1) {
        if (var1.isAlive()) {
            try {
                var1.join();
            } catch (InterruptedException var3) {
                if (this.verbose >= 1) {
                    var3.printStackTrace();
                }
            }
        }

    }

    void endThreads() {
        this.isThreadEnding = true;
        this.endThread(this.opl3PlayerThread);
        this.endThread(this.saveFileThread);
        this.isThreadEnding = false;
    }

    void startThread() {
        this.bufferStatus = Player.BufferStatus.RENDERBUFFER;
        this.opl3PlayerThread = new Thread(this);
        this.opl3PlayerThread.start();
    }

    public void loadFile() {
        try {
            FileInputStream var1 = new FileInputStream(this.file);
            int var2 = var1.available();
            this.fileBuffer = new byte[var2];
            var1.read(this.fileBuffer);
            String var3 = this.getExtension(this.file.getName());
            this.setFileType(var3);
        } catch (IOException var4) {
            if (this.verbose >= 1) {
                var4.printStackTrace();
            }
        }

        this.fileNameLabel.setText(this.file.getName());
        this.play();
    }

    public void setFileType(String var1) {
        if (var1.toLowerCase().equals("dro")) {
            if (this.fileBuffer[10] == 1) {
                this.fileType = Player.FileType.DRO1;
            } else if (this.fileBuffer[8] == 2) {
                this.fileType = Player.FileType.DRO2;
            }
        } else if (var1.toLowerCase().equals("laa") || var1.toLowerCase().equals("cmf")) {
            this.fileType = Player.FileType.MID;
        }

    }

    void setCPlayer(boolean var1) {
        if (this.verbose == 0) {
            var1 = false;
        }

        this.opl = new OPL3();
        switch (1) {
            case 1:
                this.p = new CmidPlayer(this.opl);
                break;
            case 2:
                this.p = new CdroPlayer(this.opl, var1);
                break;
            case 3:
                this.p = new Cdro2Player(this.opl, var1);
        }

        try {
            this.p.load(this.fileBuffer);
        } catch (IOException var3) {
            if (this.verbose >= 1) {
                var3.printStackTrace();
            }
        }

    }

    void setMusicBufferLength() {
        this.setCPlayer(true);
        this.entireMusicLength = 0;

        int var1;
        double var2;
        for (this.musicBufferLength = 0; this.p.update(); ++this.musicBufferLength) {
            var2 = 1.0D / (double) this.p.getrefresh();
            var1 = 4 * (int) (49700.0D * var2);
            this.entireMusicLength += var1;
        }

        var2 = 0.1D;

        for (int var4 = 0; var4 < 30; ++var4) {
            var1 = 4 * (int) (49700.0D * var2);
            this.entireMusicLength += var1;
            ++this.musicBufferLength;
        }

        this.sliderModel.setMaximum(this.entireMusicLength - 1);
    }

    void startSourceDataLine() {
        AudioFormat var1 = new AudioFormat(49700.0F, 16, 2, true, false);

        try {
            this.sourceDataLine = AudioSystem.getSourceDataLine(var1);
            this.sourceDataLine.open(var1, this.entireMusicLength);
        } catch (LineUnavailableException var3) {
            if (this.verbose >= 1) {
                var3.printStackTrace();
            }
        }

        this.sourceDataLine.start();
    }

    void stopSourceDataLine() {
        this.sourceDataLine.drain();
        this.sourceDataLine.stop();
        this.sourceDataLine.close();
    }

    void setTotalLabel() {
        String var1 = this.getTime(this.entireMusicLength);
        this.totalLabel.setText(var1);
        if (this.verbose >= 2) {
            this.verboseTotalLabel.setText("100% / " + var1 + " / " + this.entireMusicLength / 4 + " samples in " + this.musicBuffer.length + " buffers.");
        }

    }

    void setBufferLabel() {
        if (this.verbose >= 2) {
            if (this.liberateBuffers > 1) {
                this.verboseBufferLabel.setText(this.getPercent(this.bufferedOverallPosition, this.entireMusicLength) + "% / " + this.getTime(this.bufferedOverallPosition) + " / " + this.bufferedOverallPosition / 4 + " samples. Last liberated " + this.liberateBuffers / 2 + " buffers.");
            } else {
                this.verboseBufferLabel.setText(this.getPercent(this.bufferedOverallPosition, this.entireMusicLength) + "% / " + this.getTime(this.bufferedOverallPosition) + " / " + this.bufferedOverallPosition / 4 + " samples.");
            }

        }
    }

    void updateBufferingStatus() {
        this.statusLabel.setText("Buffering " + this.getPercent(this.bufferedArrayIndex - this.playingArrayIndex, this.buffersWait) + "%");
    }

    void setPlayingLabel() {
        if (!this.slider.getValueIsAdjusting() && !this.positionChanged) {
            this.sliderModel.setValue(this.playingOverallPosition);
        }

        String var1 = this.getTime(this.playingOverallPosition);
        this.playingLabel.setText(var1);
        if (this.verbose >= 2) {
            if (this.isWaitingBuffer) {
                this.verbosePlayingLabel.setText(this.getPercent(this.playingOverallPosition, this.entireMusicLength) + "% / " + var1 + " / " + this.playingOverallPosition / 4 + " samples. Waiting " + this.buffersWait + " buffers...");
            } else {
                this.verbosePlayingLabel.setText(this.getPercent(this.playingOverallPosition, this.entireMusicLength) + "% / " + var1 + " / " + this.playingOverallPosition / 4 + " samples.");
            }
        }

    }

    public void play() {
        this.playButton.setEnabled(true);
        this.pauseButton.setEnabled(true);
        this.slider.setEnabled(true);
        this.playButton.setSelected(true);

        while (!this.isThreadEnding && this.bufferStatus == Player.BufferStatus.RENDERBUFFER) {
            this.playFirst();
        }

        while (!this.isThreadEnding && this.bufferStatus == Player.BufferStatus.BUFFERED) {
            this.playAgain();
        }

        this.playingLabel.setText("");
        this.totalLabel.setText("");
        this.statusLabel.setText("");
        this.sliderModel.setValue(0);
        this.slider.setEnabled(false);
        this.pauseButton.setSelected(true);
    }

    void open() {
        File assetsDirectoryPath = getAssetsDirectoryPath();

        JFileChooser var1 = new JFileChooser(assetsDirectoryPath.getPath());
        var1.addChoosableFileFilter(new FileNameExtensionFilter("Creative Music File (.cmf)", new String[]{"cmf"}));
        var1.setFileFilter(new FileNameExtensionFilter("LucasArts Adlib Audio (.laa) ", new String[]{"laa"}));
        var1.setFileFilter(new FileNameExtensionFilter("DOSBox Raw OPL (.dro) ", new String[]{"dro"}));
        var1.setFileFilter(new FileNameExtensionFilter("All media files (.cmf .laa .dro)", new String[]{"cmf", "laa", "dro"}));
        int var2 = var1.showOpenDialog(this);
        if (var2 == 0) {
            this.endThreads();
            this.file = var1.getSelectedFile();
            this.currOpenDir = this.file.getParentFile();
            this.runAction = Player.RunAction.FILE;
            this.startThread();
        }

    }

    String getWavFileName(String var1) {
        StringBuffer var2 = new StringBuffer(var1);
        int var3 = var2.lastIndexOf(".");
        return var2.substring(0, var3) + ".wav";
    }

    synchronized void save() {
        if (this.bufferStatus == Player.BufferStatus.BUFFERED) {
            JFileChooser var1 = new JFileChooser(this.currSaveDir);
            FileNameExtensionFilter var2 = new FileNameExtensionFilter("Microsoft Windows WAV (.wav)", new String[]{"wav"});
            var1.setFileFilter(var2);
            var1.setSelectedFile(new File(this.currSaveDir, this.getWavFileName(this.fileNameLabel.getText())));
            int var3 = var1.showSaveDialog(this);
            if (var3 == 0) {
                this.saveButton.setEnabled(false);
                this.statusLabel.setText("Saving...");
                File var4 = var1.getSelectedFile();
                this.currSaveDir = var4.getParentFile();
                if (var1.getFileFilter().equals(var2) && !var4.getName().toLowerCase().endsWith(".wav")) {
                    var4 = new File(var1.getSelectedFile().getAbsolutePath() + ".wav");
                }

                AudioFormat var5 = new AudioFormat(49700.0F, 16, 2, true, false);

                try {
                    Vector var6 = new Vector();

                    for (int var7 = 0; var7 < this.musicBuffer.length; ++var7) {
                        var6.add(new ByteArrayInputStream(this.musicBuffer[var7]));
                    }

                    SequenceInputStream var15 = new SequenceInputStream(var6.elements());
                    AudioInputStream var8 = new AudioInputStream(var15, var5, (long) (this.entireMusicLength / 4));
                    Player.SaveOutputStream var9 = new Player.SaveOutputStream(var4);
                    AudioSystem.write(var8, AudioFileFormat.Type.WAVE, var9);
                    var8.close();
                    var9.close();
                } catch (IOException var13) {
                    if (!this.isThreadEnding && this.verbose >= 1) {
                        var13.printStackTrace();
                    }

                    if (var13.getMessage().toLowerCase().contains("access is denied")) {
                        JOptionPane.showMessageDialog(this, "Access to write in \"" + this.currSaveDir + "\" denied");
                    }
                } finally {
                    this.statusLabel.setText("");
                    this.saveButton.setEnabled(true);
                }
            }

        }
    }

    byte[] read(int var1) {
        byte[] var2 = new byte[var1];

        for (int var6 = 0; var6 < var1; var6 += 4) {
            short[] var3 = this.opl.read();
            short var4 = var3[0];
            short var5 = var3[1];
            var2[var6] = (byte) (var4 & 255);
            var2[var6 + 1] = (byte) (var4 >> 8 & 255);
            var2[var6 + 2] = (byte) (var5 & 255);
            var2[var6 + 3] = (byte) (var5 >> 8 & 255);
        }

        return var2;
    }

    void readBufferChunk(double var1) {
        int var3 = 4 * (int) (49700.0D * var1);
        this.musicBuffer[this.bufferedArrayIndex] = this.read(var3);
        ++this.bufferedArrayIndex;
        this.bufferedOverallPosition += var3;
        this.sliderModel.setExtent(this.sliderModel.getMaximum() - this.bufferedOverallPosition);
        this.setBufferLabel();
    }

    void sleep(int var1) {
        try {
            Thread.sleep((long) var1);
        } catch (InterruptedException var3) {
            if (this.verbose >= 1) {
                var3.printStackTrace();
            }
        }

    }

    boolean isMemoryLow(double var1) {
        int var3 = 4 * (int) (49700.0D * var1);
        MemoryMXBean var4 = ManagementFactory.getMemoryMXBean();
        MemoryUsage var5 = var4.getHeapMemoryUsage();
        int var6 = var3 * 2 + 1048576;
        if (var5.getUsed() + (long) var6 >= var5.getMax()) {
            if (this.verbose >= 2) {
                System.out.println("\nMemory is low.\nMemory usage: " + var5.getUsed() + "\nMaximum available: " + var5.getMax());
            }

            return true;
        } else {
            return false;
        }
    }

    void liberateMemory() {
        int var1;
        for (var1 = 0; var1 < this.liberateBuffers && var1 < this.playingArrayIndex; ++var1) {
            this.musicBuffer[var1] = null;
        }

        System.gc();
        if (var1 == this.liberateBuffers) {
            this.liberateBuffers *= 2;
        }

    }

    void checkMemory(double var1) {
        while (this.isMemoryLow(var1)) {
            this.liberateMemory();
            if (this.bufferStatus == Player.BufferStatus.BUFFERING) {
                this.verboseBufferLabel.setForeground(Color.RED);
                this.bufferStatus = Player.BufferStatus.PARTIALBUFFER;
            }

            if (this.pauseButton.isSelected()) {
                this.sleep(1000);
            } else {
                this.writeAvailable();
            }
        }

    }

    void getPosition() {
        this.positionChanged = false;
        int var1 = this.slider.getValue();
        var1 -= var1 % 4;
        this.sourceDataLine.flush();

        for (; var1 < this.playingOverallPosition; --this.playingOverallPosition) {
            --this.playingSample;
            if (this.playingSample < 0) {
                if (this.playingArrayIndex <= 0) {
                    this.playingSample = 0;
                    break;
                }

                if (this.musicBuffer[this.playingArrayIndex - 1] == null) {
                    this.playingSample = 0;
                    int var2 = this.playingOverallPosition / 2;
                    if (var1 <= var2) {
                        this.bufferStatus = Player.BufferStatus.RENDERBUFFER;
                        return;
                    }
                    break;
                }

                --this.playingArrayIndex;
                this.playingSample = this.musicBuffer[this.playingArrayIndex].length - 1;
            }
        }

        for (; var1 > this.playingOverallPosition; ++this.playingOverallPosition) {
            ++this.playingSample;
            if (this.playingSample >= this.musicBuffer[this.playingArrayIndex].length) {
                if (this.playingArrayIndex >= this.bufferedArrayIndex - 1) {
                    --this.playingSample;
                    break;
                }

                ++this.playingArrayIndex;
                this.playingSample = 0;
            }
        }

    }

    synchronized void playBuffering() {
        this.bufferedArrayIndex = 0;
        this.bufferedOverallPosition = 0;
        this.bufferStatus = Player.BufferStatus.BUFFERING;
        this.saveButton.setVisible(false);
        this.verboseBufferLabel.setForeground(Color.BLACK);
        this.statusLabel.setText("");
        this.isWaitingBuffer = false;
        this.buffersWait = 1;
        this.liberateBuffers = 1;
        this.musicBuffer = new byte[this.musicBufferLength][];
        System.gc();
        this.setTotalLabel();
        this.setCPlayer(false);

        while (true) {
            double var1;
            if (this.p.update()) {
                var1 = 1.0D / (double) this.p.getrefresh();
                this.checkMemory(var1);
                if (this.positionChanged) {
                    this.getPosition();
                }

                if (!this.isThreadEnding && this.bufferStatus != Player.BufferStatus.RENDERBUFFER) {
                    this.readBufferChunk(var1);
                    this.writeAvailable();
                    this.sleep(1);
                    continue;
                }

                return;
            }

            var1 = 0.1D;
            int var3 = 0;

            while (true) {
                if (var3 < 30) {
                    this.checkMemory(var1);
                    if (this.positionChanged) {
                        this.getPosition();
                    }

                    if (!this.isThreadEnding && this.bufferStatus != Player.BufferStatus.RENDERBUFFER) {
                        this.readBufferChunk(var1);
                        this.writeAvailable();
                        this.sleep(1);
                        ++var3;
                        continue;
                    }

                    return;
                }

                while (this.pauseButton.isSelected()) {
                    this.sleep(1000);
                }

                if (this.playingArrayIndex < this.bufferedArrayIndex) {
                    var3 = this.musicBuffer[this.playingArrayIndex].length - this.playingSample;
                    //this.writeSourceDataLine(this.musicBuffer[this.playingArrayIndex], this.playingSample, var3);
                    this.sleep(1);
                    ++this.playingArrayIndex;
                }

                if (this.bufferStatus == Player.BufferStatus.BUFFERING) {
                    this.bufferStatus = Player.BufferStatus.BUFFERED;
                    this.saveButton.setVisible(true);
                }

                this.isWaitingBuffer = false;
                this.statusLabel.setText("");
                this.verbosePlayingLabel.setForeground(Color.BLACK);
                return;
            }
        }
    }

    void writeAvailable() {
        if (this.isWaitingBuffer) {
            if (this.bufferedArrayIndex - this.playingArrayIndex < this.buffersWait && this.musicBuffer[this.playingArrayIndex - 1] != null && !this.pauseButton.isSelected()) {
                this.updateBufferingStatus();
                return;
            }

            this.isWaitingBuffer = false;
            this.statusLabel.setText("");
            this.verbosePlayingLabel.setForeground(Color.BLACK);
            this.buffersWait *= 2;
        }

        if (this.pauseButton.isSelected()) {
            this.sleep(10);
        } else {
            int var1 = this.sourceDataLine.available();
            if (this.bufferedOverallPosition >= var1 || var1 >= this.entireMusicLength) {
                byte[] var2 = new byte[var1];

                int var3;
                for (var3 = 0; var3 < var1 && this.playingArrayIndex < this.bufferedArrayIndex; ++this.playingSample) {
                    if (this.playingSample >= this.musicBuffer[this.playingArrayIndex].length) {
                        this.playingSample = 0;
                        ++this.playingArrayIndex;
                        if (this.playingArrayIndex >= this.bufferedArrayIndex) {
                            break;
                        }
                    }

                    var2[var3] = this.musicBuffer[this.playingArrayIndex][this.playingSample];
                    ++var3;
                }

                if (var3 < var1) {
                    this.isWaitingBuffer = true;
                    if (this.buffersWait > this.musicBuffer.length - this.bufferedArrayIndex) {
                        this.buffersWait = this.musicBuffer.length - this.bufferedArrayIndex;
                    }

                    this.sourceDataLine.flush();
                    this.verbosePlayingLabel.setForeground(Color.RED);
                }

                //this.writeSourceDataLine(var2, 0, var3);
            }
        }
    }

    void writeSourceDataLine(byte[] var1, int var2, int var3) {
        this.sourceDataLine.write(var1, var2, var3);
        this.playingOverallPosition += var3;
        this.setPlayingLabel();
    }

    void playBuffered() {
        int var1 = 198800;

        while (this.playingArrayIndex < this.bufferedArrayIndex) {
            while (this.playingSample < this.musicBuffer[this.playingArrayIndex].length && !this.positionChanged) {
                if (this.isThreadEnding || this.bufferStatus == Player.BufferStatus.RENDERBUFFER) {
                    return;
                }

                if (this.pauseButton.isSelected()) {
                    this.sleep(1000);
                } else {
                    int var2 = Math.min(this.musicBuffer[this.playingArrayIndex].length - this.playingSample, var1);
                    this.writeSourceDataLine(this.musicBuffer[this.playingArrayIndex], this.playingSample, var2);
                    this.playingSample += var2;
                }
            }

            if (this.positionChanged) {
                this.getPosition();
            } else {
                this.playingSample = 0;
                ++this.playingArrayIndex;
            }
        }

        if (this.bufferStatus == Player.BufferStatus.PARTIALBUFFER && this.buffersWait <= 1024) {
            this.bufferStatus = Player.BufferStatus.RENDERBUFFER;
        }

    }

    void initPlay() {
        this.playingArrayIndex = 0;
        this.playingSample = 0;
        this.playingOverallPosition = 0;
        if (!this.slider.getValueIsAdjusting() && !this.positionChanged) {
            this.sliderModel.setValue(0);
        }

        this.verbosePlayingLabel.setForeground(Color.BLACK);
    }

    void playFirst() {
        this.initPlay();
        this.setMusicBufferLength();
        this.startSourceDataLine();
        this.playBuffering();
        this.playBuffered();
        this.stopSourceDataLine();
    }

    void playAgain() {
        this.initPlay();
        this.startSourceDataLine();
        this.playBuffered();
        this.stopSourceDataLine();
    }

    String getNameWithSpaces(String var1) {
        StringBuffer var2 = new StringBuffer(var1);

        for (int var3 = var2.lastIndexOf("_"); var3 != -1; var3 = var2.lastIndexOf("_")) {
            var2.replace(var3, var3 + 1, " ");
        }

        return var2.toString();
    }

    void addExamplesMenu(JMenuBar var1) {
        File var2 = new File("/Works/Code/opl3-emu/html/Examples");
        this.examplesMenu = new JMenu("Examples");
        this.examplesMenu.addMenuListener(this);
        FileSystemView var3 = FileSystemView.getFileSystemView();
        File var4 = var3.getParentDirectory(var2);
        File var5 = var3.createFileObject(var4, "dir.txt");

        try {
            this.logFileWriter = new FileWriter(var5);
            File[] var6 = var3.getFiles(var2, false);
            File[] var7 = var6;
            int var8 = var6.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                File var10 = var7[var9];
                this.makeNode(var3, var10, this.examplesMenu);
            }

            this.logFileWriter.close();
        } catch (IOException var11) {
            if (this.verbose >= 1) {
                var11.printStackTrace();
            }
        }

        var1.add(this.examplesMenu);
    }

    void makeNode(FileSystemView var1, File var2, JMenu var3) throws IOException {
        if (var2.isDirectory()) {
            JMenu var4 = new JMenu(this.getNameWithSpaces(var2.getName()));
            var3.add(var4);
            this.logFileWriter.write(var2.getName() + "[");
            File[] var5 = var1.getFiles(var2, false);
            File[] var6 = var5;
            int var7 = var5.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                File var9 = var6[var8];
                this.makeNode(var1, var9, var4);
            }

            this.logFileWriter.write("]");
        } else {
            Player.ExamplesMenuAction var10 = new Player.ExamplesMenuAction(var2);
            JMenuItem var11 = new JMenuItem(var10);
            var3.add(var11);
            this.logFileWriter.write(var2.getName() + ";");
        }
    }

    private static File getAssetsDirectoryPath() {
        try {
            return new File(Paths.get(Player.class.getClassLoader().getResource(".").toURI()).getParent().getParent().toString(), "/third-party/assets");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    class SaveOutputStream extends FileOutputStream {
        final int wavHeaderLength = 44;
        int entireFileLength;
        int bytesWritten;
        int oldPercent;
        int newPercent;

        SaveOutputStream(File var2) throws FileNotFoundException {
            super(var2);
            this.entireFileLength = Player.this.entireMusicLength + 44;
            this.bytesWritten = 0;
            this.oldPercent = 0;
            this.newPercent = 0;
        }

        void updateSavingStatus() {
            this.newPercent = Player.this.getPercent(this.bytesWritten, this.entireFileLength);
            if (this.newPercent > this.oldPercent) {
                Player.this.statusLabel.setText("Saving " + this.newPercent + "%");
                this.oldPercent = this.newPercent;
            }

        }

        public void write(byte[] var1) throws IOException {
            if (Player.this.isThreadEnding) {
                throw new IOException();
            } else {
                super.write(var1);
                this.bytesWritten += var1.length;
                this.updateSavingStatus();
            }
        }

        public void write(byte[] var1, int var2, int var3) throws IOException {
            if (Player.this.isThreadEnding) {
                throw new IOException();
            } else {
                super.write(var1, var2, var3);
                this.bytesWritten += var3;
                this.updateSavingStatus();
            }
        }

        public void write(int var1) throws IOException {
            if (Player.this.isThreadEnding) {
                throw new IOException();
            } else {
                super.write(var1);
                ++this.bytesWritten;
                this.updateSavingStatus();
            }
        }
    }

    class ExamplesMenuAction extends AbstractAction {
        File exampleFile;

        public ExamplesMenuAction(File var2) {
            this.exampleFile = var2;
            this.putValue("Name", var2.getName());
        }

        public void actionPerformed(ActionEvent var1) {
            Player.this.endThreads();
            Player.this.file = this.exampleFile;
            Player.this.runAction = Player.RunAction.FILE;
            Player.this.startThread();
        }
    }

    public static enum FileType {
        MID,
        DRO1,
        DRO2;

        private FileType() {
        }
    }

    public static enum RunAction {
        FILE,
        URL,
        SAVE,
        REPLAY;

        private RunAction() {
        }
    }

    public static enum BufferStatus {
        RENDERBUFFER,
        BUFFERING,
        BUFFERED,
        PARTIALBUFFER;

        private BufferStatus() {
        }
    }
}
