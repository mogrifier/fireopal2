//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.uva.emulation;

import com.cozendey.opl3.OPL3;

import java.io.*;

class CmidPlayer extends CmidPlayer_h implements CPlayer {
    final int LUCAS_STYLE = 1;
    final int CMF_STYLE = 2;
    final int MIDI_STYLE = 4;
    final int SIERRA_STYLE = 8;
    final int ADLIB_MELODIC = 0;
    final int ADLIB_RYTHM = 1;
    final int FILE_LUCAS = 1;
    final int FILE_MIDI = 2;
    final int FILE_CMF = 3;
    final int FILE_SIERRA = 4;
    final int FILE_ADVSIERRA = 5;
    final int FILE_OLDLUCAS = 6;
    static final int[] adlib_opadd = new int[]{0, 1, 2, 8, 9, 10, 16, 17, 18};
    static final int[] ops = new int[]{32, 32, 64, 64, 96, 96, 128, 128, 224, 224, 192};
    static final int[] map_chan = new int[]{20, 18, 21, 17};
    static final int[] fnums = new int[]{363, 385, 408, 432, 458, 485, 514, 544, 577, 611, 647, 686};
    static final int[] percussion_map = new int[]{6, 7, 8, 8, 7};
    OPL3 opl;
    int[][] midi_fm_instruments = new int[][]{{33, 33, 143, 12, 242, 242, 69, 118, 0, 0, 8, 0, 0, 0}, {49, 33, 75, 9, 242, 242, 84, 86, 0, 0, 8, 0, 0, 0}, {49, 33, 73, 9, 242, 242, 85, 118, 0, 0, 8, 0, 0, 0}, {177, 97, 14, 9, 242, 243, 59, 11, 0, 0, 6, 0, 0, 0}, {1, 33, 87, 9, 241, 241, 56, 40, 0, 0, 0, 0, 0, 0}, {1, 33, 147, 9, 241, 241, 56, 40, 0, 0, 0, 0, 0, 0}, {33, 54, 128, 23, 162, 241, 1, 213, 0, 0, 8, 0, 0, 0}, {1, 1, 146, 9, 194, 194, 168, 88, 0, 0, 10, 0, 0, 0}, {12, 129, 92, 9, 246, 243, 84, 181, 0, 0, 0, 0, 0, 0}, {7, 17, 151, 137, 246, 245, 50, 17, 0, 0, 2, 0, 0, 0}, {23, 1, 33, 9, 86, 246, 4, 4, 0, 0, 2, 0, 0, 0}, {24, 129, 98, 9, 243, 242, 230, 246, 0, 0, 0, 0, 0, 0}, {24, 33, 35, 9, 247, 229, 85, 216, 0, 0, 0, 0, 0, 0}, {21, 1, 145, 9, 246, 246, 166, 230, 0, 0, 4, 0, 0, 0}, {69, 129, 89, 137, 211, 163, 130, 227, 0, 0, 12, 0, 0, 0}, {3, 129, 73, 137, 116, 179, 85, 5, 1, 0, 4, 0, 0, 0}, {113, 49, 146, 9, 246, 241, 20, 7, 0, 0, 2, 0, 0, 0}, {114, 48, 20, 9, 199, 199, 88, 8, 0, 0, 2, 0, 0, 0}, {112, 177, 68, 9, 170, 138, 24, 8, 0, 0, 4, 0, 0, 0}, {35, 177, 147, 9, 151, 85, 35, 20, 1, 0, 4, 0, 0, 0}, {97, 177, 19, 137, 151, 85, 4, 4, 1, 0, 0, 0, 0, 0}, {36, 177, 72, 9, 152, 70, 42, 26, 1, 0, 12, 0, 0, 0}, {97, 33, 19, 9, 145, 97, 6, 7, 1, 0, 10, 0, 0, 0}, {33, 161, 19, 146, 113, 97, 6, 7, 0, 0, 6, 0, 0, 0}, {2, 65, 156, 137, 243, 243, 148, 200, 1, 0, 12, 0, 0, 0}, {3, 17, 84, 9, 243, 241, 154, 231, 1, 0, 12, 0, 0, 0}, {35, 33, 95, 9, 241, 242, 58, 248, 0, 0, 0, 0, 0, 0}, {3, 33, 135, 137, 246, 243, 34, 248, 1, 0, 6, 0, 0, 0}, {3, 33, 71, 9, 249, 246, 84, 58, 0, 0, 0, 0, 0, 0}, {35, 33, 74, 14, 145, 132, 65, 25, 1, 0, 8, 0, 0, 0}, {35, 33, 74, 9, 149, 148, 25, 25, 1, 0, 8, 0, 0, 0}, {9, 132, 161, 137, 32, 209, 79, 248, 0, 0, 8, 0, 0, 0}, {33, 162, 30, 9, 148, 195, 6, 166, 0, 0, 2, 0, 0, 0}, {49, 49, 18, 9, 241, 241, 40, 24, 0, 0, 10, 0, 0, 0}, {49, 49, 141, 9, 241, 241, 232, 120, 0, 0, 10, 0, 0, 0}, {49, 50, 91, 9, 81, 113, 40, 72, 0, 0, 12, 0, 0, 0}, {1, 33, 139, 73, 161, 242, 154, 223, 0, 0, 8, 0, 0, 0}, {33, 33, 139, 17, 162, 161, 22, 223, 0, 0, 8, 0, 0, 0}, {49, 49, 139, 9, 244, 241, 232, 120, 0, 0, 10, 0, 0, 0}, {49, 49, 18, 9, 241, 241, 40, 24, 0, 0, 10, 0, 0, 0}, {49, 33, 21, 9, 221, 86, 19, 38, 1, 0, 8, 0, 0, 0}, {49, 33, 22, 9, 221, 102, 19, 6, 1, 0, 8, 0, 0, 0}, {113, 49, 73, 9, 209, 97, 28, 12, 1, 0, 8, 0, 0, 0}, {33, 35, 77, 137, 113, 114, 18, 6, 1, 0, 2, 0, 0, 0}, {241, 225, 64, 9, 241, 111, 33, 22, 1, 0, 2, 0, 0, 0}, {2, 1, 26, 137, 245, 133, 117, 53, 1, 0, 0, 0, 0, 0}, {2, 1, 29, 137, 245, 243, 117, 244, 1, 0, 0, 0, 0, 0}, {16, 17, 65, 9, 245, 242, 5, 195, 1, 0, 2, 0, 0, 0}, {33, 162, 155, 10, 177, 114, 37, 8, 1, 0, 14, 0, 0, 0}, {161, 33, 152, 9, 127, 63, 3, 7, 1, 1, 0, 0, 0, 0}, {161, 97, 147, 9, 193, 79, 18, 5, 0, 0, 10, 0, 0, 0}, {33, 97, 24, 9, 193, 79, 34, 5, 0, 0, 12, 0, 0, 0}, {49, 114, 91, 140, 244, 138, 21, 5, 0, 0, 0, 0, 0, 0}, {161, 97, 144, 9, 116, 113, 57, 103, 0, 0, 0, 0, 0, 0}, {113, 114, 87, 9, 84, 122, 5, 5, 0, 0, 12, 0, 0, 0}, {144, 65, 0, 9, 84, 165, 99, 69, 0, 0, 8, 0, 0, 0}, {33, 33, 146, 10, 133, 143, 23, 9, 0, 0, 12, 0, 0, 0}, {33, 33, 148, 14, 117, 143, 23, 9, 0, 0, 12, 0, 0, 0}, {33, 97, 148, 9, 118, 130, 21, 55, 0, 0, 12, 0, 0, 0}, {49, 33, 67, 9, 158, 98, 23, 44, 1, 1, 2, 0, 0, 0}, {33, 33, 155, 9, 97, 127, 106, 10, 0, 0, 2, 0, 0, 0}, {97, 34, 138, 15, 117, 116, 31, 15, 0, 0, 8, 0, 0, 0}, {161, 33, 134, 140, 114, 113, 85, 24, 1, 0, 0, 0, 0, 0}, {33, 33, 77, 9, 84, 166, 60, 28, 0, 0, 8, 0, 0, 0}, {49, 97, 143, 9, 147, 114, 2, 11, 1, 0, 8, 0, 0, 0}, {49, 97, 142, 9, 147, 114, 3, 9, 1, 0, 8, 0, 0, 0}, {49, 97, 145, 9, 147, 130, 3, 9, 1, 0, 10, 0, 0, 0}, {49, 97, 142, 9, 147, 114, 15, 15, 1, 0, 10, 0, 0, 0}, {33, 33, 75, 9, 170, 143, 22, 10, 1, 0, 8, 0, 0, 0}, {49, 33, 144, 9, 126, 139, 23, 12, 1, 1, 6, 0, 0, 0}, {49, 50, 129, 9, 117, 97, 25, 25, 1, 0, 0, 0, 0, 0}, {50, 33, 144, 9, 155, 114, 33, 23, 0, 0, 4, 0, 0, 0}, {225, 225, 31, 9, 133, 101, 95, 26, 0, 0, 0, 0, 0, 0}, {225, 225, 70, 9, 136, 101, 95, 26, 0, 0, 0, 0, 0, 0}, {161, 33, 156, 9, 117, 117, 31, 10, 0, 0, 2, 0, 0, 0}, {49, 33, 139, 9, 132, 101, 88, 26, 0, 0, 0, 0, 0, 0}, {225, 161, 76, 9, 102, 101, 86, 38, 0, 0, 0, 0, 0, 0}, {98, 161, 203, 9, 118, 85, 70, 54, 0, 0, 0, 0, 0, 0}, {98, 161, 162, 9, 87, 86, 7, 7, 0, 0, 11, 0, 0, 0}, {98, 161, 156, 9, 119, 118, 7, 7, 0, 0, 11, 0, 0, 0}, {34, 33, 89, 9, 255, 255, 3, 15, 2, 0, 0, 0, 0, 0}, {33, 33, 14, 9, 255, 255, 15, 15, 1, 1, 0, 0, 0, 0}, {34, 33, 70, 137, 134, 100, 85, 24, 0, 0, 0, 0, 0, 0}, {33, 161, 69, 9, 102, 150, 18, 10, 0, 0, 0, 0, 0, 0}, {33, 34, 139, 9, 146, 145, 42, 42, 1, 0, 0, 0, 0, 0}, {162, 97, 158, 73, 223, 111, 5, 7, 0, 0, 2, 0, 0, 0}, {32, 96, 26, 9, 239, 143, 1, 6, 0, 2, 0, 0, 0, 0}, {33, 33, 143, 134, 241, 244, 41, 9, 0, 0, 10, 0, 0, 0}, {119, 161, 165, 9, 83, 160, 148, 5, 0, 0, 2, 0, 0, 0}, {97, 177, 31, 137, 168, 37, 17, 3, 0, 0, 10, 0, 0, 0}, {97, 97, 23, 9, 145, 85, 52, 22, 0, 0, 12, 0, 0, 0}, {113, 114, 93, 9, 84, 106, 1, 3, 0, 0, 0, 0, 0, 0}, {33, 162, 151, 9, 33, 66, 67, 53, 0, 0, 8, 0, 0, 0}, {161, 33, 28, 9, 161, 49, 119, 71, 1, 1, 0, 0, 0, 0}, {33, 97, 137, 12, 17, 66, 51, 37, 0, 0, 10, 0, 0, 0}, {161, 33, 21, 9, 17, 207, 71, 7, 1, 0, 0, 0, 0, 0}, {58, 81, 206, 9, 248, 134, 246, 2, 0, 0, 2, 0, 0, 0}, {33, 33, 21, 9, 33, 65, 35, 19, 1, 0, 0, 0, 0, 0}, {6, 1, 91, 9, 116, 165, 149, 114, 0, 0, 0, 0, 0, 0}, {34, 97, 146, 140, 177, 242, 129, 38, 0, 0, 12, 0, 0, 0}, {65, 66, 77, 9, 241, 242, 81, 245, 1, 0, 0, 0, 0, 0}, {97, 163, 148, 137, 17, 17, 81, 19, 1, 0, 6, 0, 0, 0}, {97, 161, 140, 137, 17, 29, 49, 3, 0, 0, 6, 0, 0, 0}, {164, 97, 76, 9, 243, 129, 115, 35, 1, 0, 4, 0, 0, 0}, {2, 7, 133, 12, 210, 242, 83, 246, 0, 1, 0, 0, 0, 0}, {17, 19, 12, 137, 163, 162, 17, 229, 1, 0, 0, 0, 0, 0}, {17, 17, 6, 9, 246, 242, 65, 230, 1, 2, 4, 0, 0, 0}, {147, 145, 145, 9, 212, 235, 50, 17, 0, 1, 8, 0, 0, 0}, {4, 1, 79, 9, 250, 194, 86, 5, 0, 0, 12, 0, 0, 0}, {33, 34, 73, 9, 124, 111, 32, 12, 0, 1, 6, 0, 0, 0}, {49, 33, 133, 9, 221, 86, 51, 22, 1, 0, 10, 0, 0, 0}, {32, 33, 4, 138, 218, 143, 5, 11, 2, 0, 6, 0, 0, 0}, {5, 3, 106, 137, 241, 195, 229, 229, 0, 0, 6, 0, 0, 0}, {7, 2, 21, 9, 236, 248, 38, 22, 0, 0, 10, 0, 0, 0}, {5, 1, 157, 9, 103, 223, 53, 5, 0, 0, 8, 0, 0, 0}, {24, 18, 150, 9, 250, 248, 40, 229, 0, 0, 10, 0, 0, 0}, {16, 0, 134, 12, 168, 250, 7, 3, 0, 0, 6, 0, 0, 0}, {17, 16, 65, 12, 248, 243, 71, 3, 2, 0, 4, 0, 0, 0}, {1, 16, 142, 9, 241, 243, 6, 2, 2, 0, 14, 0, 0, 0}, {14, 192, 0, 9, 31, 31, 0, 255, 0, 3, 14, 0, 0, 0}, {6, 3, 128, 145, 248, 86, 36, 132, 0, 2, 14, 0, 0, 0}, {14, 208, 0, 14, 248, 52, 0, 4, 0, 3, 14, 0, 0, 0}, {14, 192, 0, 9, 246, 31, 0, 2, 0, 3, 14, 0, 0, 0}, {213, 218, 149, 73, 55, 86, 163, 55, 0, 0, 0, 0, 0, 0}, {53, 20, 92, 17, 178, 244, 97, 21, 2, 0, 10, 0, 0, 0}, {14, 208, 0, 9, 246, 79, 0, 245, 0, 3, 14, 0, 0, 0}, {38, 228, 0, 9, 255, 18, 1, 22, 0, 1, 14, 0, 0, 0}, {0, 0, 0, 9, 243, 246, 240, 201, 0, 2, 14, 0, 0, 0}};
    static int[] my_midi_fm_vol_table = new int[]{0, 11, 16, 19, 22, 25, 27, 29, 32, 33, 35, 37, 39, 40, 42, 43, 45, 46, 48, 49, 50, 51, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 64, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 75, 76, 77, 78, 79, 80, 80, 81, 82, 83, 83, 84, 85, 86, 86, 87, 88, 89, 89, 90, 91, 91, 92, 93, 93, 94, 95, 96, 96, 97, 97, 98, 99, 99, 100, 101, 101, 102, 103, 103, 104, 104, 105, 106, 106, 107, 107, 108, 109, 109, 110, 110, 111, 112, 112, 113, 113, 114, 114, 115, 115, 116, 117, 117, 118, 118, 119, 119, 120, 120, 121, 121, 122, 122, 123, 123, 124, 124, 125, 125, 126, 126, 127};

    public int getTotalMiliseconds() {
        return 0;
    }

    void midiprintf(String var1, Object... var2) {
    }

    public CmidPlayer(OPL3 var1) {
        this.opl = var1;
        this.author = this.title = this.remarks = this.emptystr = null;
        this.flen = 0L;
        this.data = null;

        int var2;
        for (var2 = 0; var2 < this.ch.length; ++var2) {
            this.ch[var2] = new midi_channel();
        }

        for (var2 = 0; var2 < this.track.length; ++var2) {
            this.track[var2] = new midi_track();
        }

    }

    int datalook(long var1) {
        return var1 >= 0L && var1 < this.flen ? this.data[(int) var1] : 0;
    }

    long getnexti(long var1) {
        long var3 = 0L;

        for (long var5 = 0L; var5 < var1; ++var5) {
            var3 += (long) (this.datalook(this.pos) << (int) (8L * var5));
            ++this.pos;
        }

        return var3;
    }

    long getnext(long var1) {
        long var3 = 0L;

        for (long var5 = 0L; var5 < var1; ++var5) {
            var3 <<= 8;
            var3 += (long) this.datalook(this.pos);
            ++this.pos;
        }

        return var3;
    }

    long getval() {
        boolean var1 = false;
        int var2 = (int) this.getnext(1L);

        int var3;
        for (var3 = var2 & 127; (var2 & 128) != 0; var3 = (var3 << 7) + (var2 & 127)) {
            var2 = (int) this.getnext(1L);
        }

        return (long) var3;
    }

    boolean load_sierra_ins(String var1) throws FileNotFoundException, IOException {
        int[] var10 = new int[28];
        StringBuffer var11 = new StringBuffer(var1.length() + 9);
        var11.append(var1);
        long var4 = 0L;

        long var2;
        for (var2 = (long) (var11.length() - 1); var2 >= 0L; --var2) {
            if (var11.charAt((int) var2) == 47 || var11.charAt((int) var2) == 92) {
                var4 = var2 + 1L;
                break;
            }
        }

        var11.replace((int) (var4 + 3L), var11.length() - 1, "patch.003");
        File var13 = new File(var11.toString());
        FileInputStream var12 = new FileInputStream(var13);
        if (var12 == null) {
            return false;
        } else {
            var12.skip(2L);
            this.stins = 0;

            for (var2 = 0L; var2 < 2L; ++var2) {
                for (long var6 = 0L; var6 < 48L; ++var6) {
                    long var8 = var2 * 48L + var6;
                    this.midiprintf("\n%2d: ", new Object[]{Long.valueOf(var8)});

                    for (var4 = 0L; var4 < 28L; ++var4) {
                        var10[(int) var4] = var12.read();
                    }

                    this.myinsbank[(int) var8][0] = var10[9] * 128 + var10[10] * 64 + var10[5] * 32 + var10[11] * 16 + var10[1];
                    this.myinsbank[(int) var8][1] = var10[22] * 128 + var10[23] * 64 + var10[18] * 32 + var10[24] * 16 + var10[14];
                    this.myinsbank[(int) var8][2] = (var10[0] << 6) + var10[8];
                    this.myinsbank[(int) var8][3] = (var10[13] << 6) + var10[21];
                    this.myinsbank[(int) var8][4] = (var10[3] << 4) + var10[6];
                    this.myinsbank[(int) var8][5] = (var10[16] << 4) + var10[19];
                    this.myinsbank[(int) var8][6] = (var10[4] << 4) + var10[7];
                    this.myinsbank[(int) var8][7] = (var10[17] << 4) + var10[20];
                    this.myinsbank[(int) var8][8] = var10[26];
                    this.myinsbank[(int) var8][9] = var10[27];
                    this.myinsbank[(int) var8][10] = (var10[2] << 1) + (1 - (var10[12] & 1));

                    for (var4 = 0L; var4 < 11L; ++var4) {
                        this.midiprintf("%02X ", new Object[]{Integer.valueOf(this.myinsbank[(int) var8][(int) var4])});
                    }

                    ++this.stins;
                }

                var12.skip(2L);
            }

            var12.close();
            this.smyinsbank = (int[][]) this.myinsbank.clone();
            return true;
        }
    }

    void sierra_next_section() {
        int var1;
        for (var1 = 0; var1 < 16; ++var1) {
            this.track[var1].on = 0;
        }

        this.midiprintf("\n\nnext adv sierra section:\n", new Object[0]);
        this.pos = this.sierra_pos;
        var1 = 0;

        for (int var2 = 0; var1 != 255; var1 = (int) this.getnext(1L)) {
            this.getnext(1L);
            this.curtrack = var2++;
            this.track[this.curtrack].on = 1;
            this.track[this.curtrack].spos = this.getnext(1L);
            this.track[this.curtrack].spos += (this.getnext(1L) << 8) + 4L;
            this.track[this.curtrack].tend = this.flen;
            this.track[this.curtrack].iwait = 0L;
            this.track[this.curtrack].pv = 0;
            this.midiprintf("track %d starts at %lx\n", new Object[]{Integer.valueOf(this.curtrack), Long.valueOf(this.track[this.curtrack].spos)});
            this.getnext(2L);
        }

        this.getnext(2L);
        this.deltas = 32L;
        this.sierra_pos = this.pos;
        this.fwait = 0.0F;
        this.doing = 1;
    }

    public boolean load(byte[] var1) throws FileNotFoundException, IOException {
        ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
        if (var2 == null) {
            return false;
        } else {
            int[] var4 = new int[6];
            byte[] var5 = new byte[6];
            var2.read(var5);

            int var6;
            for (var6 = 0; var6 < 6; ++var6) {
                var4[var6] = var5[var6] & 255;
            }

            byte var3 = 0;
            this.subsongs = 0;
            switch (var4[0]) {
                case 65:
                    if (var4[1] == 68 && var4[2] == 76) {
                        var3 = 1;
                    }
                    break;
                case 67:
                    if (var4[1] == 84 && var4[2] == 77 && var4[3] == 70) {
                        var3 = 3;
                    }
                    break;
                case 77:
                    if (var4[1] == 84 && var4[2] == 104 && var4[3] == 100) {
                        var3 = 2;
                    }
                    break;
                case 132:
                    if (var4[1] == 0 && this.load_sierra_ins(var1.toString())) {
                        if (var4[2] == 240) {
                            var3 = 5;
                        } else {
                            var3 = 4;
                        }
                    }
                    break;
                default:
                    if (var4[4] == 65 && var4[5] == 68) {
                        var3 = 6;
                    }
            }

            if (var3 == 0) {
                var2.close();
                return false;
            } else {
                this.subsongs = 1;
                this.type = var3;
                var2.close();
                var2 = new ByteArrayInputStream(var1);
                this.flen = (long) var2.available();
                this.data = new int[(int) this.flen];
                var5 = new byte[(int) this.flen];
                var2.read(var5);

                for (var6 = 0; (long) var6 < this.flen; ++var6) {
                    this.data[var6] = var5[var6] & 255;
                }

                var2.close();
                this.rewind(0);
                return true;
            }
        }
    }

    void midi_write_adlib(int var1, int var2) {
        this.opl.write(0, var1, var2);
        this.adlib_data[var1] = var2;
    }

    void midi_fm_instrument(int var1, int[] var2) {
        if ((this.adlib_style & 8) != 0) {
            this.midi_write_adlib(189, 0);
        }

        this.midi_write_adlib(32 + adlib_opadd[var1], var2[0]);
        this.midi_write_adlib(35 + adlib_opadd[var1], var2[1]);
        if ((this.adlib_style & 1) != 0) {
            this.midi_write_adlib(67 + adlib_opadd[var1], 63);
            if ((var2[10] & 1) == 0) {
                this.midi_write_adlib(64 + adlib_opadd[var1], var2[2]);
            } else {
                this.midi_write_adlib(64 + adlib_opadd[var1], 63);
            }
        } else if ((this.adlib_style & 8) != 0) {
            this.midi_write_adlib(64 + adlib_opadd[var1], var2[2]);
            this.midi_write_adlib(67 + adlib_opadd[var1], var2[3]);
        } else {
            this.midi_write_adlib(64 + adlib_opadd[var1], var2[2]);
            if ((var2[10] & 1) == 0) {
                this.midi_write_adlib(67 + adlib_opadd[var1], var2[3]);
            } else {
                this.midi_write_adlib(67 + adlib_opadd[var1], 0);
            }
        }

        this.midi_write_adlib(96 + adlib_opadd[var1], var2[4]);
        this.midi_write_adlib(99 + adlib_opadd[var1], var2[5]);
        this.midi_write_adlib(128 + adlib_opadd[var1], var2[6]);
        this.midi_write_adlib(131 + adlib_opadd[var1], var2[7]);
        this.midi_write_adlib(224 + adlib_opadd[var1], var2[8]);
        this.midi_write_adlib(227 + adlib_opadd[var1], var2[9]);
        this.midi_write_adlib(192 + var1, 240 | var2[10]);
    }

    void midi_fm_percussion(int var1, int[] var2) {
        int var3 = map_chan[var1 - 12];
        this.midi_write_adlib(32 + var3, var2[0]);
        this.midi_write_adlib(64 + var3, var2[2]);
        this.midi_write_adlib(96 + var3, var2[4]);
        this.midi_write_adlib(128 + var3, var2[6]);
        this.midi_write_adlib(224 + var3, var2[8]);
        this.midi_write_adlib(192 + var3, 240 | var2[10]);
    }

    void midi_fm_volume(int var1, int var2) {
        if ((this.adlib_style & 8) == 0) {
            int var3 = var2 >> 2;
            if ((this.adlib_style & 1) != 0) {
                if ((this.adlib_data[192 + var1] & 1) == 1) {
                    this.midi_write_adlib(64 + adlib_opadd[var1], 63 - var3 | this.adlib_data[64 + adlib_opadd[var1]] & 192);
                }

                this.midi_write_adlib(67 + adlib_opadd[var1], 63 - var3 | this.adlib_data[67 + adlib_opadd[var1]] & 192);
            } else {
                if ((this.adlib_data[192 + var1] & 1) == 1) {
                    this.midi_write_adlib(64 + adlib_opadd[var1], 63 - var3 | this.adlib_data[64 + adlib_opadd[var1]] & 192);
                }

                this.midi_write_adlib(67 + adlib_opadd[var1], 63 - var3 | this.adlib_data[67 + adlib_opadd[var1]] & 192);
            }
        }

    }

    void midi_fm_playnote(int var1, int var2, int var3) {
        if (var2 < 0) {
            var2 = 12 - var2 % 12;
        }

        int var4 = fnums[var2 % 12];
        int var5 = var2 / 12;
        this.midi_fm_volume(var1, var3);
        this.midi_write_adlib(160 + var1, var4 & 255);
        int var6 = ((var4 & 768) >> 8) + (var5 << 2) + (this.adlib_mode != 0 && var1 >= 6 ? 0 : 32);
        this.midi_write_adlib(176 + var1, var6);
    }

    void midi_fm_endnote(int var1) {
        this.midi_write_adlib(176 + var1, this.adlib_data[176 + var1] & 223);
    }

    void midi_fm_reset() {
        int var1;
        for (var1 = 0; var1 < 256; ++var1) {
            this.midi_write_adlib(var1, 0);
        }

        for (var1 = 192; var1 <= 200; ++var1) {
            this.midi_write_adlib(var1, 240);
        }

        this.midi_write_adlib(1, 32);
        this.midi_write_adlib(189, 192);
    }

    public boolean update() {
        int var19 = 0;
        if (this.doing == 1) {
            for (this.curtrack = 0; this.curtrack < 16; ++this.curtrack) {
                if (this.track[this.curtrack].on != 0) {
                    this.pos = this.track[this.curtrack].pos;
                    if (this.type != 4 && this.type != 5) {
                        this.track[this.curtrack].iwait += this.getval();
                    } else {
                        this.track[this.curtrack].iwait += this.getnext(1L);
                    }

                    this.track[this.curtrack].pos = this.pos;
                }
            }

            this.doing = 0;
        }

        this.iwait = 0L;
        boolean var25 = true;

        while (this.iwait == 0L && var25) {
            for (this.curtrack = 0; this.curtrack < 16; ++this.curtrack) {
                if (this.track[this.curtrack].on != 0 && this.track[this.curtrack].iwait == 0L && this.track[this.curtrack].pos < this.track[this.curtrack].tend) {
                    this.pos = this.track[this.curtrack].pos;
                    long var3 = this.getnext(1L);
                    if (var3 < 128L) {
                        var3 = (long) this.track[this.curtrack].pv;
                        --this.pos;
                    }

                    this.track[this.curtrack].pv = (int) var3;
                    int var21 = (int) (var3 & 15L);
                    this.midiprintf("[%2X]", new Object[]{Long.valueOf(var3)});
                    long var5;
                    long var7;
                    long var13;
                    int var20;
                    label359:
                    switch ((int) var3 & 240) {
                        case 128:
                            var5 = this.getnext(1L);
                            var7 = this.getnext(1L);
                            var19 = 0;

                            while (true) {
                                if (var19 >= 9) {
                                    break label359;
                                }

                                if (this.chp[var19][0] == var21 && (long) this.chp[var19][1] == var5) {
                                    this.midi_fm_endnote(var19);
                                    this.chp[var19][0] = -1;
                                }

                                ++var19;
                            }
                        case 144:
                            var5 = this.getnext(1L);
                            var7 = this.getnext(1L);
                            byte var24;
                            if (this.adlib_mode == 1) {
                                var24 = 6;
                            } else {
                                var24 = 9;
                            }

                            if (this.ch[var21].on != 0) {
                                for (var19 = 0; var19 < 18; ++var19) {
                                    ++this.chp[var19][2];
                                }

                                int var22;
                                if (var21 >= 11 && this.adlib_mode != 0) {
                                    var22 = percussion_map[var21 - 11];
                                } else {
                                    boolean var26 = false;
                                    var22 = -1;
                                    int var23 = 0;

                                    for (var19 = 0; var19 < var24; ++var19) {
                                        if (this.chp[var19][0] == -1 && this.chp[var19][2] > var23) {
                                            var23 = this.chp[var19][2];
                                            var22 = var19;
                                            var26 = true;
                                        }
                                    }

                                    if (var22 == -1) {
                                        var23 = 0;

                                        for (var19 = 0; var19 < var24; ++var19) {
                                            if (this.chp[var19][2] > var23) {
                                                var23 = this.chp[var19][2];
                                                var22 = var19;
                                            }
                                        }
                                    }

                                    if (!var26) {
                                        this.midi_fm_endnote(var22);
                                    }
                                }

                                if (var7 != 0L && this.ch[var21].inum >= 0 && this.ch[var21].inum < 128) {
                                    if (this.adlib_mode != 0 && var21 >= 12) {
                                        this.midi_fm_percussion(var21, this.ch[var21].ins);
                                    } else {
                                        this.midi_fm_instrument(var22, this.ch[var21].ins);
                                    }

                                    long var11;
                                    if ((this.adlib_style & 4) != 0) {
                                        var11 = (long) this.ch[var21].vol * var7 / 128L;
                                        if ((this.adlib_style & 1) != 0) {
                                            var11 *= 2L;
                                        }

                                        if (var11 > 127L) {
                                            var11 = 127L;
                                        }

                                        var11 = (long) my_midi_fm_vol_table[(int) var11];
                                        if ((this.adlib_style & 1) != 0) {
                                            var11 = (long) ((int) ((float) Math.sqrt((double) ((float) var11)) * 11.0F));
                                        }
                                    } else {
                                        var11 = var7;
                                    }

                                    this.midi_fm_playnote(var22, (int) (var5 + (long) this.ch[var21].nshift), (int) var11 * 2);
                                    this.chp[var22][0] = var21;
                                    this.chp[var22][1] = (int) var5;
                                    this.chp[var22][2] = 0;
                                    if (this.adlib_mode == 1 && var21 >= 11) {
                                        this.midi_write_adlib(189, this.adlib_data[189] & ~(16 >> var21 - 11));
                                        this.midi_write_adlib(189, this.adlib_data[189] | 16 >> var21 - 11);
                                    }
                                } else if (var7 == 0L) {
                                    for (var19 = 0; var19 < 9; ++var19) {
                                        if (this.chp[var19][0] == var21 && (long) this.chp[var19][1] == var5) {
                                            this.midi_fm_endnote(var19);
                                            this.chp[var19][0] = -1;
                                        }
                                    }
                                } else {
                                    this.chp[var22][0] = -1;
                                    this.chp[var22][2] = 0;
                                }

                                this.midiprintf(" [%d:%d:%d:%d]\n", new Object[]{Integer.valueOf(var21), Integer.valueOf(this.ch[var21].inum), Long.valueOf(var5), Long.valueOf(var7)});
                            } else {
                                this.midiprintf("off", new Object[0]);
                            }
                            break;
                        case 160:
                            var5 = this.getnext(1L);
                            var7 = this.getnext(1L);
                            break;
                        case 176:
                            long var9 = this.getnext(1L);
                            var7 = this.getnext(1L);
                            switch ((int) var9) {
                                case 7:
                                    this.midiprintf("(pb:%d: %d %d)", new Object[]{Integer.valueOf(var21), Long.valueOf(var9), Long.valueOf(var7)});
                                    this.ch[var21].vol = (int) var7;
                                    this.midiprintf("vol", new Object[0]);
                                    break label359;
                                case 103:
                                    this.midiprintf("\n\nhere:%d\n\n", new Object[]{Long.valueOf(var7)});
                                    if ((this.adlib_style & 2) != 0) {
                                        this.adlib_mode = (int) var7;
                                        if (this.adlib_mode == 1) {
                                            this.midi_write_adlib(189, this.adlib_data[189] | 32);
                                        } else {
                                            this.midi_write_adlib(189, this.adlib_data[189] & -33);
                                        }
                                    }
                                default:
                                    break label359;
                            }
                        case 192:
                            var13 = this.getnext(1L);
                            this.ch[var21].inum = (int) var13;
                            var20 = 0;

                            while (true) {
                                if (var20 >= 11) {
                                    break label359;
                                }

                                this.ch[var21].ins[var20] = this.myinsbank[this.ch[var21].inum][var20];
                                ++var20;
                            }
                        case 208:
                            var13 = this.getnext(1L);
                            break;
                        case 224:
                            var13 = this.getnext(1L);
                            var13 = this.getnext(1L);
                            break;
                        case 240:
                            long var15;
                            switch ((int) var3) {
                                case 240:
                                case 247:
                                    var15 = this.getval();
                                    if (this.datalook(this.pos + var15) == 247) {
                                        var19 = 1;
                                    }

                                    this.midiprintf("{%d}", new Object[]{Long.valueOf(var15)});
                                    this.midiprintf("\n", new Object[0]);
                                    if (this.datalook(this.pos) == 125 && this.datalook(this.pos + 1L) == 16 && this.datalook(this.pos + 2L) < 16) {
                                        this.adlib_style = 5;

                                        for (var19 = 0; (long) var19 < var15; ++var19) {
                                            this.midiprintf("%x ", new Object[]{Integer.valueOf(this.datalook(this.pos + (long) var19))});
                                            if ((var19 - 3) % 10 == 0) {
                                                this.midiprintf("\n", new Object[0]);
                                            }
                                        }

                                        this.midiprintf("\n", new Object[0]);
                                        this.getnext(1L);
                                        this.getnext(1L);
                                        var21 = (int) this.getnext(1L);
                                        this.getnext(1L);
                                        this.ch[var21].ins[0] = (int) ((this.getnext(1L) << 4) + this.getnext(1L));
                                        this.ch[var21].ins[2] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L) & 63L));
                                        this.ch[var21].ins[4] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L)));
                                        this.ch[var21].ins[6] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L)));
                                        this.ch[var21].ins[8] = (int) ((this.getnext(1L) << 4) + this.getnext(1L));
                                        this.ch[var21].ins[1] = (int) ((this.getnext(1L) << 4) + this.getnext(1L));
                                        this.ch[var21].ins[3] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L) & 63L));
                                        this.ch[var21].ins[5] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L)));
                                        this.ch[var21].ins[7] = (int) (255L - ((this.getnext(1L) << 4) + this.getnext(1L)));
                                        this.ch[var21].ins[9] = (int) ((this.getnext(1L) << 4) + this.getnext(1L));
                                        var19 = (int) ((this.getnext(1L) << 4) + this.getnext(1L));
                                        this.ch[var21].ins[10] = var19;
                                        this.midiprintf("\n%d: ", new Object[]{Integer.valueOf(var21)});

                                        for (var19 = 0; var19 < 11; ++var19) {
                                            this.midiprintf("%2X ", new Object[]{Integer.valueOf(this.ch[var21].ins[var19])});
                                        }

                                        this.getnext(var15 - 26L);
                                    } else {
                                        this.midiprintf("\n", new Object[0]);

                                        for (var20 = 0; (long) var20 < var15; ++var20) {
                                            this.midiprintf("%2X ", new Object[]{Long.valueOf(this.getnext(1L))});
                                        }
                                    }

                                    this.midiprintf("\n", new Object[0]);
                                    if (var19 == 1) {
                                        this.getnext(1L);
                                    }
                                case 241:
                                case 244:
                                case 245:
                                case 249:
                                case 253:
                                case 254:
                                default:
                                    break label359;
                                case 242:
                                    this.getnext(2L);
                                    break label359;
                                case 243:
                                    this.getnext(1L);
                                    break label359;
                                case 246:
                                case 248:
                                case 250:
                                case 251:
                                case 252:
                                    if (this.type == 4 || this.type == 5) {
                                        this.track[this.curtrack].tend = this.pos;
                                        this.midiprintf("endmark: %ld -- %lx\n", new Object[]{Long.valueOf(this.pos), Long.valueOf(this.pos)});
                                    }
                                    break label359;
                                case 255:
                                    var3 = this.getnext(1L);
                                    var15 = this.getval();
                                    this.midiprintf("\n", new Object[0]);
                                    this.midiprintf("{%X_%X}", new Object[]{Long.valueOf(var3), Long.valueOf(var15)});
                                    if (var3 == 81L) {
                                        long var17 = this.getnext(var15);
                                        this.msqtr = var17;
                                        this.midiprintf("(qtr=%ld)", new Object[]{Long.valueOf(this.msqtr)});
                                        break label359;
                                    } else {
                                        var19 = 0;

                                        while (true) {
                                            if ((long) var19 >= var15) {
                                                break label359;
                                            }

                                            this.midiprintf("%2X ", new Object[]{Long.valueOf(this.getnext(1L))});
                                            ++var19;
                                        }
                                    }
                            }
                        default:
                            this.midiprintf("!", new Object[]{Long.valueOf(var3)});
                    }

                    if (this.pos < this.track[this.curtrack].tend) {
                        long var1;
                        if (this.type != 4 && this.type != 5) {
                            var1 = this.getval();
                        } else {
                            var1 = this.getnext(1L);
                        }

                        this.track[this.curtrack].iwait = var1;
                    } else {
                        this.track[this.curtrack].iwait = 0L;
                    }

                    this.track[this.curtrack].pos = this.pos;
                }
            }

            var25 = false;
            this.iwait = 0L;

            for (this.curtrack = 0; this.curtrack < 16; ++this.curtrack) {
                if (this.track[this.curtrack].on == 1 && this.track[this.curtrack].pos < this.track[this.curtrack].tend) {
                    var25 = true;
                }
            }

            if (var25) {
                this.iwait = 16777215L;

                for (this.curtrack = 0; this.curtrack < 16; ++this.curtrack) {
                    if (this.track[this.curtrack].on == 1 && this.track[this.curtrack].pos < this.track[this.curtrack].tend && this.track[this.curtrack].iwait < this.iwait) {
                        this.iwait = this.track[this.curtrack].iwait;
                    }
                }
            }
        }

        if (this.iwait != 0L && var25) {
            for (this.curtrack = 0; this.curtrack < 16; ++this.curtrack) {
                if (this.track[this.curtrack].on != 0) {
                    this.track[this.curtrack].iwait -= this.iwait;
                }
            }

            this.fwait = 1.0F / ((float) this.iwait / (float) this.deltas * ((float) this.msqtr / 1000000.0F));
        } else {
            this.fwait = 50.0F;
        }

        this.midiprintf("\n", new Object[0]);

        for (var19 = 0; var19 < 16; ++var19) {
            if (this.track[var19].on != 0) {
                if (this.track[var19].pos < this.track[var19].tend) {
                    this.midiprintf("<%d>", new Object[]{Long.valueOf(this.track[var19].iwait)});
                } else {
                    this.midiprintf("stop", new Object[0]);
                }
            }
        }

        if (var25) {
            return true;
        } else {
            return false;
        }
    }

    public float getrefresh() {
        return this.fwait > 0.01F ? this.fwait : 0.01F;
    }

    public void rewind(int var1) {
        int[] var14 = new int[16];
        this.pos = 0L;
        this.tins = 0;
        this.adlib_style = 6;
        this.adlib_mode = 0;

        long var2;
        long var4;
        for (var2 = 0L; var2 < 128L; ++var2) {
            for (var4 = 0L; var4 < 14L; ++var4) {
                this.myinsbank[(int) var2][(int) var4] = this.midi_fm_instruments[(int) var2][(int) var4];
            }

            this.myinsbank[(int) var2][14] = 0;
            this.myinsbank[(int) var2][15] = 0;
        }

        for (var2 = 0L; var2 < 16L; ++var2) {
            this.ch[(int) var2].inum = 0;

            for (var4 = 0L; var4 < 11L; ++var4) {
                this.ch[(int) var2].ins[(int) var4] = this.myinsbank[this.ch[(int) var2].inum][(int) var4];
            }

            this.ch[(int) var2].vol = 127;
            this.ch[(int) var2].nshift = -25;
            this.ch[(int) var2].on = 1;
        }

        for (var2 = 0L; var2 < 9L; ++var2) {
            this.chp[(int) var2][0] = -1;
            this.chp[(int) var2][2] = 0;
        }

        this.deltas = 250L;
        this.msqtr = 500000L;
        this.fwait = 123.0F;
        this.iwait = 0L;
        this.subsongs = 1;

        for (var2 = 0L; var2 < 16L; ++var2) {
            this.track[(int) var2].tend = 0L;
            this.track[(int) var2].spos = 0L;
            this.track[(int) var2].pos = 0L;
            this.track[(int) var2].iwait = 0L;
            this.track[(int) var2].on = 0;
            this.track[(int) var2].pv = 0;
        }

        this.curtrack = 0;
        this.pos = 0L;
        var2 = this.getnext(1L);
        long var10;
        switch (this.type) {
            case 1:
                this.getnext(24L);
                this.adlib_style = 5;
            case 2:
                if (this.type != 1) {
                    this.tins = 128;
                }

                this.getnext(11L);
                this.deltas = this.getnext(2L);
                this.midiprintf("deltas:%ld\n", new Object[]{Long.valueOf(this.deltas)});
                this.getnext(4L);
                this.curtrack = 0;
                this.track[this.curtrack].on = 1;
                this.track[this.curtrack].tend = this.getnext(4L);
                this.track[this.curtrack].spos = this.pos;
                this.midiprintf("tracklen:%ld\n", new Object[]{Long.valueOf(this.track[this.curtrack].tend)});
                break;
            case 3:
                this.getnext(3L);
                this.getnexti(2L);
                long var6 = this.getnexti(2L);
                long var8 = this.getnexti(2L);
                this.deltas = this.getnexti(2L);
                this.msqtr = 1000000L / this.getnexti(2L) * this.deltas;
                var2 = this.getnexti(2L);
                if (var2 != 0L) {
                    for (this.title = new StringBuffer(); var2 < (long) this.data.length; ++var2) {
                        this.title.append((char) this.data[(int) var2]);
                        if (this.data[(int) var2] == 0) {
                            break;
                        }
                    }
                }

                var2 = this.getnexti(2L);
                if (var2 != 0L) {
                    for (this.author = new StringBuffer(); var2 < (long) this.data.length; ++var2) {
                        this.author.append((char) this.data[(int) var2]);
                        if (this.data[(int) var2] == 0) {
                            break;
                        }
                    }
                }

                var2 = this.getnexti(2L);
                if (var2 != 0L) {
                    for (this.remarks = new StringBuffer(); var2 < (long) this.data.length; ++var2) {
                        this.remarks.append((char) this.data[(int) var2]);
                        if (this.data[(int) var2] == 0) {
                            break;
                        }
                    }
                }

                this.getnext(16L);
                var2 = this.getnexti(2L);
                if (var2 > 128L) {
                    var2 = 128L;
                }

                this.getnexti(2L);
                this.midiprintf("\nioff:%d\nmoff%d\ndeltas:%ld\nmsqtr:%ld\nnumi:%d\n", new Object[]{Long.valueOf(var6), Long.valueOf(var8), Long.valueOf(this.deltas), Long.valueOf(this.msqtr), Long.valueOf(var2)});
                this.pos = var6;
                this.tins = (int) var2;

                for (var4 = 0L; var4 < var2; ++var4) {
                    this.midiprintf("\n%d: ", new Object[]{Long.valueOf(var4)});

                    for (var10 = 0L; var10 < 16L; ++var10) {
                        this.myinsbank[(int) var4][(int) var10] = (int) this.getnext(1L);
                        this.midiprintf("%2X ", new Object[]{Integer.valueOf(this.myinsbank[(int) var4][(int) var10])});
                    }
                }

                for (var2 = 0L; var2 < 16L; ++var2) {
                    this.ch[(int) var2].nshift = -13;
                }

                this.adlib_style = 2;
                this.curtrack = 0;
                this.track[this.curtrack].on = 1;
                this.track[this.curtrack].tend = this.flen;
                this.track[this.curtrack].spos = var8;
                break;
            case 4:
                this.myinsbank = (int[][]) this.smyinsbank.clone();
                this.tins = this.stins;
                this.getnext(2L);
                this.deltas = 32L;
                this.curtrack = 0;
                this.track[this.curtrack].on = 1;
                this.track[this.curtrack].tend = this.flen;

                for (var2 = 0L; var2 < 16L; ++var2) {
                    this.ch[(int) var2].nshift = -13;
                    this.ch[(int) var2].on = (int) this.getnext(1L);
                    this.ch[(int) var2].inum = (int) this.getnext(1L);

                    for (var4 = 0L; var4 < 11L; ++var4) {
                        this.ch[(int) var2].ins[(int) var4] = this.myinsbank[this.ch[(int) var2].inum][(int) var4];
                    }
                }

                this.track[this.curtrack].spos = this.pos;
                this.adlib_style = 12;
                break;
            case 5:
                this.myinsbank = (int[][]) this.smyinsbank.clone();
                this.tins = this.stins;
                this.deltas = 32L;
                this.getnext(11L);
                long var12 = this.sierra_pos = this.pos;
                this.sierra_next_section();

                while (this.datalook(this.sierra_pos - 2L) != 255) {
                    this.sierra_next_section();
                    ++this.subsongs;
                }

                if (var1 < 0 || var1 >= this.subsongs) {
                    var1 = 0;
                }

                this.sierra_pos = var12;
                this.sierra_next_section();

                for (var2 = 0L; var2 != (long) var1; ++var2) {
                    this.sierra_next_section();
                }

                this.adlib_style = 12;
                break;
            case 6:
                this.msqtr = 250000L;
                this.pos = 9L;
                this.deltas = this.getnext(1L);
                var2 = 8L;
                this.pos = 25L;
                this.tins = (int) var2;

                for (var4 = 0L; var4 < var2; ++var4) {
                    this.midiprintf("\n%d: ", new Object[]{Long.valueOf(var4)});

                    for (var10 = 0L; var10 < 16L; ++var10) {
                        var14[(int) var10] = (int) this.getnext(1L);
                    }

                    this.myinsbank[(int) var4][10] = var14[2];
                    this.myinsbank[(int) var4][0] = var14[3];
                    this.myinsbank[(int) var4][2] = var14[4];
                    this.myinsbank[(int) var4][4] = var14[5];
                    this.myinsbank[(int) var4][6] = var14[6];
                    this.myinsbank[(int) var4][8] = var14[7];
                    this.myinsbank[(int) var4][1] = var14[8];
                    this.myinsbank[(int) var4][3] = var14[9];
                    this.myinsbank[(int) var4][5] = var14[10];
                    this.myinsbank[(int) var4][7] = var14[11];
                    this.myinsbank[(int) var4][9] = var14[12];

                    for (var10 = 0L; var10 < 11L; ++var10) {
                        this.midiprintf("%2X ", new Object[]{Integer.valueOf(this.myinsbank[(int) var4][(int) var10])});
                    }
                }

                for (var2 = 0L; var2 < 16L; ++var2) {
                    if (var2 < (long) this.tins) {
                        this.ch[(int) var2].inum = (int) var2;

                        for (var4 = 0L; var4 < 11L; ++var4) {
                            this.ch[(int) var2].ins[(int) var4] = this.myinsbank[this.ch[(int) var2].inum][(int) var4];
                        }
                    }
                }

                this.adlib_style = 5;
                this.curtrack = 0;
                this.track[this.curtrack].on = 1;
                this.track[this.curtrack].tend = this.flen;
                this.track[this.curtrack].spos = 152L;
        }

        for (var2 = 0L; var2 < 16L; ++var2) {
            if (this.track[(int) var2].on != 0) {
                this.track[(int) var2].pos = this.track[(int) var2].spos;
                this.track[(int) var2].pv = 0;
                this.track[(int) var2].iwait = 0L;
            }
        }

        this.doing = 1;
        this.midi_fm_reset();
    }

    String gettype() {
        switch (this.type) {
            case 1:
                return "LucasArts AdLib MIDI";
            case 2:
                return "General MIDI";
            case 3:
                return "Creative Music Format (CMF MIDI)";
            case 4:
                return "Sierra On-Line EGA MIDI";
            case 5:
                return "Sierra On-Line VGA MIDI";
            case 6:
                return "Lucasfilm Adlib MIDI";
            default:
                return "MIDI unknown";
        }
    }
}
