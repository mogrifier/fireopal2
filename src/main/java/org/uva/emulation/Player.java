package org.uva.emulation;

import com.cozendey.opl3.OPL3;
import lombok.Data;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.uva.emulation.Player.FileType.*;

public class Player {
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    private OPL3 opl = new OPL3();
    private SourceDataLine sourceDataLine;
    private CPlayer cPlayer;
    public byte[][] musicBuffer;
    private int musicBufferLength;
    private int entireMusicLength;
    private Player.BufferStatus bufferStatus;
    private Player.FileType fileType;
    private int bufferedArrayIndex;
    private int playingArrayIndex = 0;
    private int playingSample = 0;
    private int bufferedOverallPosition;
    private boolean isThreadEnding = false;
    private boolean isWaitingBuffer;
    private int buffersWait;
    private MusicFile musicFile;

   int[][] instruments;

    private int dataRead = 0;

    public static void main(String[] args) {
        if (args.length != 1 || isBlank(args[0])) {
            throw new IllegalArgumentException("Missing file path for reading");
        }
        Player player = new Player(args[0]);

        player.instruments = new int[][]{{33, 33, 143, 12, 242, 242, 69, 118, 0, 0, 8, 0, 0, 0},
                {49, 33, 75, 9, 242, 242, 84, 86, 0, 0, 8, 0, 0, 0}, {49, 33, 73, 9, 242, 242, 85, 118, 0, 0, 8, 0, 0, 0},
                {177, 97, 14, 9, 242, 243, 59, 11, 0, 0, 6, 0, 0, 0}, {1, 33, 87, 9, 241, 241, 56, 40, 0, 0, 0, 0, 0, 0},
                {1, 33, 147, 9, 241, 241, 56, 40, 0, 0, 0, 0, 0, 0}, {33, 54, 128, 23, 162, 241, 1, 213, 0, 0, 8, 0, 0, 0},
                {1, 1, 146, 9, 194, 194, 168, 88, 0, 0, 10, 0, 0, 0}, {12, 129, 92, 9, 246, 243, 84, 181, 0, 0, 0, 0, 0, 0},
                {7, 17, 151, 137, 246, 245, 50, 17, 0, 0, 2, 0, 0, 0}, {23, 1, 33, 9, 86, 246, 4, 4, 0, 0, 2, 0, 0, 0},
                {24, 129, 98, 9, 243, 242, 230, 246, 0, 0, 0, 0, 0, 0}, {24, 33, 35, 9, 247, 229, 85, 216, 0, 0, 0, 0, 0, 0},
                {21, 1, 145, 9, 246, 246, 166, 230, 0, 0, 4, 0, 0, 0}, {69, 129, 89, 137, 211, 163, 130, 227, 0, 0, 12, 0, 0, 0},
                {3, 129, 73, 137, 116, 179, 85, 5, 1, 0, 4, 0, 0, 0}, {113, 49, 146, 9, 246, 241, 20, 7, 0, 0, 2, 0, 0, 0},
                {114, 48, 20, 9, 199, 199, 88, 8, 0, 0, 2, 0, 0, 0}, {112, 177, 68, 9, 170, 138, 24, 8, 0, 0, 4, 0, 0, 0},
                {35, 177, 147, 9, 151, 85, 35, 20, 1, 0, 4, 0, 0, 0}, {97, 177, 19, 137, 151, 85, 4, 4, 1, 0, 0, 0, 0, 0},
                {36, 177, 72, 9, 152, 70, 42, 26, 1, 0, 12, 0, 0, 0}, {97, 33, 19, 9, 145, 97, 6, 7, 1, 0, 10, 0, 0, 0},
                {33, 161, 19, 146, 113, 97, 6, 7, 0, 0, 6, 0, 0, 0}, {2, 65, 156, 137, 243, 243, 148, 200, 1, 0, 12, 0, 0, 0}, {3, 17, 84, 9, 243, 241, 154, 231, 1, 0, 12, 0, 0, 0}, {35, 33, 95, 9, 241, 242, 58, 248, 0, 0, 0, 0, 0, 0}, {3, 33, 135, 137, 246, 243, 34, 248, 1, 0, 6, 0, 0, 0}, {3, 33, 71, 9, 249, 246, 84, 58, 0, 0, 0, 0, 0, 0}, {35, 33, 74, 14, 145, 132, 65, 25, 1, 0, 8, 0, 0, 0}, {35, 33, 74, 9, 149, 148, 25, 25, 1, 0, 8, 0, 0, 0}, {9, 132, 161, 137, 32, 209, 79, 248, 0, 0, 8, 0, 0, 0}, {33, 162, 30, 9, 148, 195, 6, 166, 0, 0, 2, 0, 0, 0}, {49, 49, 18, 9, 241, 241, 40, 24, 0, 0, 10, 0, 0, 0}, {49, 49, 141, 9, 241, 241, 232, 120, 0, 0, 10, 0, 0, 0}, {49, 50, 91, 9, 81, 113, 40, 72, 0, 0, 12, 0, 0, 0}, {1, 33, 139, 73, 161, 242, 154, 223, 0, 0, 8, 0, 0, 0}, {33, 33, 139, 17, 162, 161, 22, 223, 0, 0, 8, 0, 0, 0}, {49, 49, 139, 9, 244, 241, 232, 120, 0, 0, 10, 0, 0, 0}, {49, 49, 18, 9, 241, 241, 40, 24, 0, 0, 10, 0, 0, 0}, {49, 33, 21, 9, 221, 86, 19, 38, 1, 0, 8, 0, 0, 0}, {49, 33, 22, 9, 221, 102, 19, 6, 1, 0, 8, 0, 0, 0}, {113, 49, 73, 9, 209, 97, 28, 12, 1, 0, 8, 0, 0, 0}, {33, 35, 77, 137, 113, 114, 18, 6, 1, 0, 2, 0, 0, 0}, {241, 225, 64, 9, 241, 111, 33, 22, 1, 0, 2, 0, 0, 0}, {2, 1, 26, 137, 245, 133, 117, 53, 1, 0, 0, 0, 0, 0}, {2, 1, 29, 137, 245, 243, 117, 244, 1, 0, 0, 0, 0, 0}, {16, 17, 65, 9, 245, 242, 5, 195, 1, 0, 2, 0, 0, 0}, {33, 162, 155, 10, 177, 114, 37, 8, 1, 0, 14, 0, 0, 0}, {161, 33, 152, 9, 127, 63, 3, 7, 1, 1, 0, 0, 0, 0}, {161, 97, 147, 9, 193, 79, 18, 5, 0, 0, 10, 0, 0, 0}, {33, 97, 24, 9, 193, 79, 34, 5, 0, 0, 12, 0, 0, 0}, {49, 114, 91, 140, 244, 138, 21, 5, 0, 0, 0, 0, 0, 0}, {161, 97, 144, 9, 116, 113, 57, 103, 0, 0, 0, 0, 0, 0}, {113, 114, 87, 9, 84, 122, 5, 5, 0, 0, 12, 0, 0, 0}, {144, 65, 0, 9, 84, 165, 99, 69, 0, 0, 8, 0, 0, 0}, {33, 33, 146, 10, 133, 143, 23, 9, 0, 0, 12, 0, 0, 0}, {33, 33, 148, 14, 117, 143, 23, 9, 0, 0, 12, 0, 0, 0}, {33, 97, 148, 9, 118, 130, 21, 55, 0, 0, 12, 0, 0, 0}, {49, 33, 67, 9, 158, 98, 23, 44, 1, 1, 2, 0, 0, 0}, {33, 33, 155, 9, 97, 127, 106, 10, 0, 0, 2, 0, 0, 0}, {97, 34, 138, 15, 117, 116, 31, 15, 0, 0, 8, 0, 0, 0}, {161, 33, 134, 140, 114, 113, 85, 24, 1, 0, 0, 0, 0, 0}, {33, 33, 77, 9, 84, 166, 60, 28, 0, 0, 8, 0, 0, 0}, {49, 97, 143, 9, 147, 114, 2, 11, 1, 0, 8, 0, 0, 0}, {49, 97, 142, 9, 147, 114, 3, 9, 1, 0, 8, 0, 0, 0}, {49, 97, 145, 9, 147, 130, 3, 9, 1, 0, 10, 0, 0, 0}, {49, 97, 142, 9, 147, 114, 15, 15, 1, 0, 10, 0, 0, 0}, {33, 33, 75, 9, 170, 143, 22, 10, 1, 0, 8, 0, 0, 0}, {49, 33, 144, 9, 126, 139, 23, 12, 1, 1, 6, 0, 0, 0}, {49, 50, 129, 9, 117, 97, 25, 25, 1, 0, 0, 0, 0, 0}, {50, 33, 144, 9, 155, 114, 33, 23, 0, 0, 4, 0, 0, 0}, {225, 225, 31, 9, 133, 101, 95, 26, 0, 0, 0, 0, 0, 0}, {225, 225, 70, 9, 136, 101, 95, 26, 0, 0, 0, 0, 0, 0}, {161, 33, 156, 9, 117, 117, 31, 10, 0, 0, 2, 0, 0, 0}, {49, 33, 139, 9, 132, 101, 88, 26, 0, 0, 0, 0, 0, 0}, {225, 161, 76, 9, 102, 101, 86, 38, 0, 0, 0, 0, 0, 0}, {98, 161, 203, 9, 118, 85, 70, 54, 0, 0, 0, 0, 0, 0}, {98, 161, 162, 9, 87, 86, 7, 7, 0, 0, 11, 0, 0, 0}, {98, 161, 156, 9, 119, 118, 7, 7, 0, 0, 11, 0, 0, 0}, {34, 33, 89, 9, 255, 255, 3, 15, 2, 0, 0, 0, 0, 0}, {33, 33, 14, 9, 255, 255, 15, 15, 1, 1, 0, 0, 0, 0}, {34, 33, 70, 137, 134, 100, 85, 24, 0, 0, 0, 0, 0, 0}, {33, 161, 69, 9, 102, 150, 18, 10, 0, 0, 0, 0, 0, 0}, {33, 34, 139, 9, 146, 145, 42, 42, 1, 0, 0, 0, 0, 0}, {162, 97, 158, 73, 223, 111, 5, 7, 0, 0, 2, 0, 0, 0}, {32, 96, 26, 9, 239, 143, 1, 6, 0, 2, 0, 0, 0, 0}, {33, 33, 143, 134, 241, 244, 41, 9, 0, 0, 10, 0, 0, 0}, {119, 161, 165, 9, 83, 160, 148, 5, 0, 0, 2, 0, 0, 0}, {97, 177, 31, 137, 168, 37, 17, 3, 0, 0, 10, 0, 0, 0}, {97, 97, 23, 9, 145, 85, 52, 22, 0, 0, 12, 0, 0, 0}, {113, 114, 93, 9, 84, 106, 1, 3, 0, 0, 0, 0, 0, 0}, {33, 162, 151, 9, 33, 66, 67, 53, 0, 0, 8, 0, 0, 0}, {161, 33, 28, 9, 161, 49, 119, 71, 1, 1, 0, 0, 0, 0}, {33, 97, 137, 12, 17, 66, 51, 37, 0, 0, 10, 0, 0, 0}, {161, 33, 21, 9, 17, 207, 71, 7, 1, 0, 0, 0, 0, 0}, {58, 81, 206, 9, 248, 134, 246, 2, 0, 0, 2, 0, 0, 0}, {33, 33, 21, 9, 33, 65, 35, 19, 1, 0, 0, 0, 0, 0}, {6, 1, 91, 9, 116, 165, 149, 114, 0, 0, 0, 0, 0, 0}, {34, 97, 146, 140, 177, 242, 129, 38, 0, 0, 12, 0, 0, 0}, {65, 66, 77, 9, 241, 242, 81, 245, 1, 0, 0, 0, 0, 0}, {97, 163, 148, 137, 17, 17, 81, 19, 1, 0, 6, 0, 0, 0}, {97, 161, 140, 137, 17, 29, 49, 3, 0, 0, 6, 0, 0, 0}, {164, 97, 76, 9, 243, 129, 115, 35, 1, 0, 4, 0, 0, 0}, {2, 7, 133, 12, 210, 242, 83, 246, 0, 1, 0, 0, 0, 0}, {17, 19, 12, 137, 163, 162, 17, 229, 1, 0, 0, 0, 0, 0}, {17, 17, 6, 9, 246, 242, 65, 230, 1, 2, 4, 0, 0, 0}, {147, 145, 145, 9, 212, 235, 50, 17, 0, 1, 8, 0, 0, 0}, {4, 1, 79, 9, 250, 194, 86, 5, 0, 0, 12, 0, 0, 0}, {33, 34, 73, 9, 124, 111, 32, 12, 0, 1, 6, 0, 0, 0}, {49, 33, 133, 9, 221, 86, 51, 22, 1, 0, 10, 0, 0, 0}, {32, 33, 4, 138, 218, 143, 5, 11, 2, 0, 6, 0, 0, 0}, {5, 3, 106, 137, 241, 195, 229, 229, 0, 0, 6, 0, 0, 0}, {7, 2, 21, 9, 236, 248, 38, 22, 0, 0, 10, 0, 0, 0}, {5, 1, 157, 9, 103, 223, 53, 5, 0, 0, 8, 0, 0, 0}, {24, 18, 150, 9, 250, 248, 40, 229, 0, 0, 10, 0, 0, 0}, {16, 0, 134, 12, 168, 250, 7, 3, 0, 0, 6, 0, 0, 0}, {17, 16, 65, 12, 248, 243, 71, 3, 2, 0, 4, 0, 0, 0}, {1, 16, 142, 9, 241, 243, 6, 2, 2, 0, 14, 0, 0, 0}, {14, 192, 0, 9, 31, 31, 0, 255, 0, 3, 14, 0, 0, 0}, {6, 3, 128, 145, 248, 86, 36, 132, 0, 2, 14, 0, 0, 0}, {14, 208, 0, 14, 248, 52, 0, 4, 0, 3, 14, 0, 0, 0}, {14, 192, 0, 9, 246, 31, 0, 2, 0, 3, 14, 0, 0, 0}, {213, 218, 149, 73, 55, 86, 163, 55, 0, 0, 0, 0, 0, 0}, {53, 20, 92, 17, 178, 244, 97, 21, 2, 0, 10, 0, 0, 0}, {14, 208, 0, 9, 246, 79, 0, 245, 0, 3, 14, 0, 0, 0}, {38, 228, 0, 9, 255, 18, 1, 22, 0, 1, 14, 0, 0, 0}, {0, 0, 0, 9, 243, 246, 240, 201, 0, 2, 14, 0, 0, 0}};

        //now rewrite the instruments data
        player.instruments  = player.rewriteInstruments(player.instruments);


        player.playFirst();



        //player.saveMusicBuffer();
        //player.playAgain();
    }

    private int[][] rewriteInstruments(int[][] bank) {

        //only tweak first 8 values
        int value = 0;
        for (int i = 0; i < bank.length; i++) {
            for (int j = 0; j < 8; j++) {
                //update values then range check them to be 0 to 255.
                //reset it
                value = bank[i][j];
                value += 25;
                if (value > 255) {
                    value = 241;
                }

                bank[i][j] = value;
            }


        }

        return bank;
    }

    public Player(String filePath) {
        musicFile = loadFile(new File(filePath));
        cPlayer = loadPlayer(musicFile);
        System.out.println("done loading");

        LOGGER.setLevel(Level.SEVERE);
    }

    public void playFirst() {
        LOGGER.info("Enter in playing first method");

        reset();
        setMusicBufferLength();
        System.out.println("music buffer length " + musicBufferLength);
        System.out.println("entire music length " + entireMusicLength);

        startSourceDataLine();
        //loads the buffer
        playBuffering();
        //play the buffer
        //playBuffered();
        //stopSourceDataLine();
        //System.out.println(dataRead);
    }

    /*
    No support for regular MIDI files (smf), even though OPL3 class supports. Hmm.
     */
    public MusicFile loadFile(File file) {
        LOGGER.info("Loading and define audio type for file=" + file);

        if (!file.exists()) {
            throw new IllegalArgumentException("Unable to find the specified file");
        }
        try {
            byte[] fileBuffer = toByteArray(openInputStream(file));

            switch (getExtension(file.getName()).toLowerCase()) {
                case "laa":
                case "cmf":
                    fileType = MID;
                    break;
                case "dro":
                    if (fileBuffer[10] == 1) {
                        fileType = DRO1;
                    } else if (fileBuffer[8] == 2) {
                        fileType = DRO2;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unrecognized extension file found");
            }
            return new MusicFile(file, fileType, fileBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CPlayer loadPlayer(MusicFile musicFile) {
        CPlayer cPlayer;

        switch (musicFile.getFileType()) {
            case MID:
                cPlayer = new CmidPlayer(opl);
                break;
            case DRO1:
                cPlayer = new CdroPlayer(opl, true);
                break;
            case DRO2:
                cPlayer = new Cdro2Player(opl, true);
                break;
            default:
                throw new RuntimeException("Unable to find corresponding player");
        }
        try {
            LOGGER.info("Found compatible player, loading file type=" + musicFile.getFileType());
            cPlayer.load(musicFile.getFileBuffer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cPlayer;
    }

    private void setMusicBufferLength() {
        LOGGER.info("Enter in music buffer length method");

        int var1;
        double var2;
        for (musicBufferLength = 0; cPlayer.update(); ++musicBufferLength) {
            var2 = 1.0D / (double) cPlayer.getrefresh();
            var1 = 4 * (int) (49700.0D * var2);
            entireMusicLength += var1;
        }
        var2 = 0.1D;

        for (int var4 = 0; var4 < 30; ++var4) {
            var1 = 4 * (int) (49700.0D * var2);
            entireMusicLength += var1;
            ++musicBufferLength;
        }

    }

    private void startSourceDataLine() {
        LOGGER.info("Starting the audio system");
        int bufferSize = 8192 * 2;

        AudioFormat audioFormat = new AudioFormat(49700.0F, 16, 2, true, false);
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, bufferSize); // entireMusicLength);

            LOGGER.info("Starting source data line");
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopSourceDataLine() {
        LOGGER.info("Shutting down the audio system");

        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    /*
    This reads var1 number of bytes and stores in blocks of 4 (4 channels of data).
     */
    private byte[] read(int var1) {
        LOGGER.warning("Enter in read method");

        byte[] var2 = new byte[var1];

        for (int var6 = 0; var6 < var1; var6 += 4) {

            //reads output audio samples from OPl3
            short[] var3 = opl.read();
            short var4 = var3[0];
            short var5 = var3[1];
            var2[var6] = (byte) (var4 & 255);
            var2[var6 + 1] = (byte) (var4 >> 8 & 255);
            var2[var6 + 2] = (byte) (var5 & 255);
            var2[var6 + 3] = (byte) (var5 >> 8 & 255);
        }
        return var2;
    }

    /*
    Reads samples based on a time input- number of seconds. Samples per second times #seconds * 4. This
    gived the number of bytes to read and then get written into the musicBuffer. Gets incremented as it goes.
     */
    private void readBufferChunk(double var1) {
        LOGGER.info("Enter in read buffer chunk method");

        int var3 = 4 * (int) (49700.0D * var1);

        musicBuffer[bufferedArrayIndex] = read(var3);

        ++bufferedArrayIndex;

        bufferedOverallPosition += var3;
        //System.out.println("read " + var3 + " bytes from OPL3");
        dataRead += var3;
    }

    /*
    I think intent, despite bad name, is that this buffers all the data from OPL3.
     */
    private synchronized void playBuffering() {
        LOGGER.info("Enter in play buffering method");

        bufferedArrayIndex = 0;
        bufferedOverallPosition = 0;
        bufferStatus = Player.BufferStatus.BUFFERING;
        isWaitingBuffer = false;
        buffersWait = 1;
        musicBuffer = new byte[musicBufferLength][];
        //System.gc();
        cPlayer = loadPlayer(musicFile);
        int sampleSize =0;

        boolean reload = true;

        while (true) {
            double var1;
            if (cPlayer.update()) {
                var1 = 1.0D / (double) cPlayer.getrefresh();
                sampleSize = 4 * (int) (49700.0D * var1);
                if (!isThreadEnding && bufferStatus != Player.BufferStatus.RENDERBUFFER) {

                    //read a chunk of data from OPL3

                    if (dataRead > 7000000 && reload) {
                        reload = false;
                        //tweak instruments in real time.
                        CmidPlayer test = (CmidPlayer) cPlayer;
                        //int[][] bank = test.getMyInsBank();
                        System.out.println("reloaded instruments");
                        test.setMyInsBank(instruments);
                    }





                   // readBufferChunk(var1);
                   // sourceDataLine.write(musicBuffer[playingArrayIndex], 0, );

                    byte[] samples = read(sampleSize);
                    sourceDataLine.write(samples, 0, sampleSize);
                    dataRead += sampleSize;
                    //System.out.println("data read= " + dataRead);
                    continue;
                }
                return;
            }
            var1 = 0.1D;
            int var3 =0;
            sampleSize = 4 * (int) (49700.0D * var1);

            while (true) {
                if (var3 < 30) {
                    if (!isThreadEnding && (bufferStatus != Player.BufferStatus.RENDERBUFFER)) {

                        //read a chunk of data from OPL3
                        //readBufferChunk(var1);

                        //writeAvailable();
                        byte[] samples = read(sampleSize);
                        sourceDataLine.write(samples, 0, sampleSize);

                        ++var3;
                        continue;
                    }
                    return;
                }
                if (playingArrayIndex < bufferedArrayIndex) {
                    ++playingArrayIndex;
                }
                if (bufferStatus == Player.BufferStatus.BUFFERING) {
                    bufferStatus = Player.BufferStatus.BUFFERED;
                }
                isWaitingBuffer = false;

                //what the hell? the while loop has returns in it. weird. really bad idiom. and it fails to play
                //all the music
                return;
            }
        }
    }

    private void writeAvailable() {
        LOGGER.config("Enter in write available method");

        if (isWaitingBuffer) {
            if (bufferedArrayIndex - playingArrayIndex < buffersWait && musicBuffer[playingArrayIndex - 1] != null) {
                return;
            }
            isWaitingBuffer = false;
            buffersWait *= 2;
        }
        int var1 = sourceDataLine.available();
        //System.out.println(var1);

        if (bufferedOverallPosition >= var1 || var1 >= entireMusicLength) {
            int var3;

            for (var3 = 0; var3 < var1 && (playingArrayIndex < bufferedArrayIndex); ++playingSample) {
                if (playingSample >= musicBuffer[playingArrayIndex].length) {
                    playingSample = 0;
                    ++playingArrayIndex;
                    if (playingArrayIndex >= bufferedArrayIndex) {
                        break;
                    }
                }
                ++var3;
            }
            if (var3 < var1) {
                isWaitingBuffer = true;
                if (buffersWait > musicBuffer.length - bufferedArrayIndex) {
                    buffersWait = musicBuffer.length - bufferedArrayIndex;
                }
                sourceDataLine.flush();
            }
        }
    }


    private void saveMusicBuffer() {

        File outputFile = new File("cozendey.wav");
        try  {
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            //let's just write the buffer to a file. it is an array of byte arrays.
            for (int i = 0; i < musicBuffer.length; i++) {

                //write each byte array

                outputStream.write(musicBuffer[i]);
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
        }





    private void playBuffered() {
        LOGGER.config("Enter play buffered method");

        int var1 = 19880;

        while (playingArrayIndex < bufferedArrayIndex) {
            while (playingSample < musicBuffer[playingArrayIndex].length) {
                if (isThreadEnding || bufferStatus == Player.BufferStatus.RENDERBUFFER) {
                    return;
                }
                int var2 = Math.min(musicBuffer[playingArrayIndex].length - playingSample, var1);

                //only call for writing data. Hmm. Too much code for this task. Pipes would have been better.

                sourceDataLine.write(musicBuffer[playingArrayIndex], playingSample, var2);
                playingSample += var2;
            }
            playingSample = 0;
            ++playingArrayIndex;
        }
        if (bufferStatus == Player.BufferStatus.PARTIALBUFFER && buffersWait <= 1024) {
            bufferStatus = Player.BufferStatus.RENDERBUFFER;
        }
    }

    private void playAgain() {
        LOGGER.info("Enter in playing again method");

        reset();

        saveMusicBuffer();

       // startSourceDataLine();
       // playBuffered();
       // stopSourceDataLine();
    }

    private void reset() {
        LOGGER.info("Reset indexes");

        playingArrayIndex = 0;
        playingSample = 0;
        entireMusicLength = 0;
    }

    public enum FileType {
        MID,
        DRO1,
        DRO2
    }

    public enum BufferStatus {
        RENDERBUFFER,
        BUFFERING,
        BUFFERED,
        PARTIALBUFFER
    }

    @Data
    private static class MusicFile {
        private final File path;
        private final FileType fileType;
        private final byte[] fileBuffer;
    }
}
