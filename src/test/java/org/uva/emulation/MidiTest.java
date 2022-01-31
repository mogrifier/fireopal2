package org.uva.emulation;

import com.cozendey.opl3.OPL3;
import org.junit.Test;

public class MidiTest {


    @Test
    public void playNote() {

        OPL3 opl = new OPL3();
        CmidPlayer player = new CmidPlayer(opl);
        int [] ins = {49, 33, 73, 9, 242, 242, 85, 118, 0, 0, 8, 0, 0, 0};
        int [] perc = {6, 7, 8, 8, 7};
        player.midi_fm_instrument(5, ins);
        for (int i = 0; i < 1000; i++) {
            player.midi_fm_playnote(0, 64, 80);
           // player.midi_fm_percussion(1, perc);

            player.midi_fm_endnote( 64);
        }

        for (int j = 0; j < 1000; j++) {
           System.out.print(player.getval() + "  ");
        }
    }
}
