package org.uva.emulation;

import com.cozendey.opl3.OPL3;
import lombok.Data;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.uva.emulation.Player.FileType.*;

public class Player {
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

    }

    public void playFirst() {
        reset();
        setMusicBufferLength();
        startSourceDataLine();
        playBuffering();
        //playBuffered();
        stopSourceDataLine();
    }

    public MusicFile loadFile(File file) {
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
            cPlayer.load(musicFile.getFileBuffer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cPlayer;
    }

    private void setMusicBufferLength() {
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
        AudioFormat audioFormat = new AudioFormat(49700.0F, 16, 2, true, false);
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat, entireMusicLength);
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopSourceDataLine() {
        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    private byte[] read(int var1) {
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
        int var3 = 4 * (int) (49700.0D * var1);

        musicBuffer[bufferedArrayIndex] = read(var3);

        ++bufferedArrayIndex;

        bufferedOverallPosition += var3;
    }

    private void sleep(int var1) {
        try {
            Thread.sleep((long) var1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void playBuffering() {
        bufferedArrayIndex = 0;
        bufferedOverallPosition = 0;
        bufferStatus = Player.BufferStatus.BUFFERING;
        isWaitingBuffer = false;
        buffersWait = 1;
        musicBuffer = new byte[musicBufferLength][];
        System.gc();
        cPlayer = loadPlayer(musicFile);

        while (true) {
            double var1;
            if (cPlayer.update()) {
                var1 = 1.0D / (double) cPlayer.getrefresh();
                if (!isThreadEnding && bufferStatus != Player.BufferStatus.RENDERBUFFER) {
                    readBufferChunk(var1);
                    writeAvailable();
                    sleep(1);
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
                        sleep(1);
                        ++var3;
                        continue;
                    }
                    return;
                }
                if (playingArrayIndex < bufferedArrayIndex) {
                    sleep(1);
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
        if (isWaitingBuffer) {
            if (bufferedArrayIndex - playingArrayIndex < buffersWait && musicBuffer[playingArrayIndex - 1] != null) {
                return;
            }
            isWaitingBuffer = false;
            buffersWait *= 2;
        }
        int var1 = sourceDataLine.available();

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

    private void writeSourceDataLine(byte[] var1, int var2, int var3) {
        sourceDataLine.write(var1, var2, var3);
    }

    private void playBuffered() {
        int var1 = 198800;

        while (playingArrayIndex < bufferedArrayIndex) {
            while (playingSample < musicBuffer[playingArrayIndex].length) {
                if (isThreadEnding || bufferStatus == Player.BufferStatus.RENDERBUFFER) {
                    return;
                }
                int var2 = Math.min(musicBuffer[playingArrayIndex].length - playingSample, var1);
                writeSourceDataLine(musicBuffer[playingArrayIndex], playingSample, var2);
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
        reset();
        startSourceDataLine();
        playBuffered();
        stopSourceDataLine();
    }

    private void reset() {
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
