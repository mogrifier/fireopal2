package org.uva.emulation;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings(value = "all")
public class PlayerTest {

    @Test
    public void should_read_laa_sample_file() throws Exception {
        Path path = Paths.get(PlayerTest.class.getClassLoader().getResource("dott_chron-o-john_station2.laa").toURI());
        Player player = new Player(path.toString());

        player.loadFile(path.toFile());
        player.playFirst();

        assertNotNull(player.musicBuffer);
        assertEqualsBuffer(player.musicBuffer);
    }

    private static void assertEqualsBuffer(byte[][] buffer) throws Exception {
        StringBuilder acutalBuffer = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                acutalBuffer.append(buffer[i][j]);
            }
        }
        byte[] actualBuffer = acutalBuffer.toString().getBytes();
        byte[] expectedBuffer = readFileToByteArray(Paths.get(PlayerTest.class.getClassLoader().getResource("dott_chron-o-john_station2.raw").toURI()).toFile());
        assertArrayEquals(expectedBuffer, actualBuffer);
    }
}
