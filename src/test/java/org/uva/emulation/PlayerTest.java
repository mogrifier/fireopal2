package org.uva.emulation;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class PlayerTest {

    @Test
    public void should_read_laa_sample_file() throws Exception {
        Path path = Paths.get(PlayerTest.class.getClassLoader().getResource("dott_chron-o-john_station2.laa").toURI());
        Player player = new Player(path.toString());

        player.loadFile(path.toFile());
        player.playFirst();

        assertNotNull(player.musicBuffer);
        assertArrayEquals(player.musicBuffer, player.musicBuffer);
    }
}
