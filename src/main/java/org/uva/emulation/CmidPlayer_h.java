//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.uva.emulation;

import java.io.FileNotFoundException;
import java.io.IOException;

abstract class CmidPlayer_h {
    StringBuffer author;
    StringBuffer title;
    StringBuffer remarks;
    StringBuffer emptystr;
    long flen;
    long pos;
    long sierra_pos;
    int subsongs;
    int[] data;
    int[] adlib_data = new int[256];
    int adlib_style;
    int adlib_mode;

    //let's screw with these in realtime!!
    public int[][] myinsbank = new int[128][16];
    public int[][] smyinsbank = new int[128][16];



    CmidPlayer_h.midi_channel[] ch = new CmidPlayer_h.midi_channel[16];
    int[][] chp = new int[18][3];
    long deltas;
    long msqtr;
    CmidPlayer_h.midi_track[] track = new CmidPlayer_h.midi_track[16];
    int curtrack;
    float fwait;
    long iwait;
    int doing;
    int type;
    int tins;
    int stins;

    CmidPlayer_h() {
    }

    abstract boolean load(byte[] var1) throws FileNotFoundException, IOException;

    abstract boolean update();

    abstract void rewind(int var1);

    abstract float getrefresh();

    abstract String gettype();


    StringBuffer gettitle() {
        return this.title;
    }

    StringBuffer getauthor() {
        return this.author;
    }

    StringBuffer getdesc() {
        return this.remarks;
    }

    int getinstruments() {
        return this.tins;
    }

    int getsubsongs() {
        return this.subsongs;
    }

    abstract boolean load_sierra_ins(String var1) throws FileNotFoundException, IOException;

    abstract void midiprintf(String var1, Object... var2);

    abstract int datalook(long var1);

    abstract long getnexti(long var1);

    abstract long getnext(long var1);

    abstract long getval();

    abstract void sierra_next_section();

    abstract void midi_write_adlib(int var1, int var2);

    abstract void midi_fm_instrument(int var1, int[] var2);

    abstract void midi_fm_percussion(int var1, int[] var2);

    abstract void midi_fm_volume(int var1, int var2);

    abstract void midi_fm_playnote(int var1, int var2, int var3);

    abstract void midi_fm_endnote(int var1);

    abstract void midi_fm_reset();

    class midi_track {
        long tend;
        long spos;
        long pos;
        long iwait;
        int on;
        int pv;

        midi_track() {
        }
    }

    class midi_channel {
        int inum;
        int[] ins = new int[11];
        int vol;
        int nshift;
        int on;

        midi_channel() {
        }
    }
}
