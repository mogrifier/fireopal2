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

    public static void main(String[] args) {
        if (args.length != 1 || isBlank(args[0])) {
            throw new IllegalArgumentException("Missing file path for reading");
        }
        Player player = new Player(args[0]);
        player.playFirst();
        player.playAgain();
    }

    public Player(String filePath) {
        musicFile = loadFile(new File(filePath));

        cPlayer = loadPlayer(musicFile);

        LOGGER.setLevel(Level.SEVERE);
    }

    public void playFirst() {
        LOGGER.info("Enter in playing first method");

        reset();
        setMusicBufferLength();
        startSourceDataLine();
        playBuffering();
        //playBuffered();
        //stopSourceDataLine();
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
        LOGGER.info("Enter in start source data line method");

        AudioFormat audioFormat = new AudioFormat(49700.0F, 16, 2, true, false);
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, entireMusicLength);

            LOGGER.info("Starting source date line");
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopSourceDataLine() {
        LOGGER.info("Enter in start source data line method");

        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    private byte[] read(int var1) {
        LOGGER.warning("Enter in read method");

        byte[] var2 = new byte[var1];

        for (int var6 = 0; var6 < var1; var6 += 4) {
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

    private void readBufferChunk(double var1) {
        LOGGER.info("Enter in read buffer chunk method");

        int var3 = 4 * (int) (49700.0D * var1);

        musicBuffer[bufferedArrayIndex] = read(var3);

        ++bufferedArrayIndex;

        bufferedOverallPosition += var3;
    }

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

        while (true) {
            double var1;
            if (cPlayer.update()) {
                var1 = 1.0D / (double) cPlayer.getrefresh();

                if (!isThreadEnding && bufferStatus != Player.BufferStatus.RENDERBUFFER) {
                    readBufferChunk(var1);
                    writeAvailable();
                    continue;
                }
                return;
            }
            var1 = 0.1D;
            int var3 = 0;

            while (true) {
                if (var3 < 30) {
                    if (!isThreadEnding && bufferStatus != Player.BufferStatus.RENDERBUFFER) {
                        readBufferChunk(var1);
                        writeAvailable();
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

            for (var3 = 0; var3 < var1 && playingArrayIndex < bufferedArrayIndex; ++playingSample) {
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
