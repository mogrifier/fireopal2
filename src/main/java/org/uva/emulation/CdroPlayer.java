//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.uva.emulation;

import com.cozendey.opl3.OPL3;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class CdroPlayer extends CdroPlayer_h implements CPlayer {
    long total = 0L;
    OPL3 opl;
    int currChip = 0;
    boolean verbosePlayer;

    public CdroPlayer(OPL3 var1, boolean var2) {
        this.verbosePlayer = var2;
        this.opl = var1;
        this.opl3_mode = 0;
    }

    public boolean load(byte[] var1) throws FileNotFoundException, IOException {
        ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
        StringBuffer var3 = new StringBuffer();
        byte[] var6 = new byte[8];
        var2.read(var6);

        int var7;
        for (var7 = 0; var7 < 8; ++var7) {
            var3.append((char) var6[var7]);
        }

        if (var3.indexOf("DBRAWOPL") != 0) {
            var2.close();
            return false;
        } else {
            var7 = 0;

            int var8;
            for (var8 = 0; var8 < 4; ++var8) {
                var7 |= var2.read() << var8 * 8;
            }

            this.mstotal = 0L;

            for (var8 = 0; var8 < 4; ++var8) {
                this.mstotal |= (long) (var2.read() << var8 * 8);
            }

            this.length = 0L;

            for (var8 = 0; var8 < 4; ++var8) {
                this.length |= (long) (var2.read() << var8 * 8);
            }

            this.opl3_mode = 0;

            for (var8 = 0; var8 < 4; ++var8) {
                this.opl3_mode |= var2.read() << var8 * 8;
            }

            if (this.verbosePlayer) {
                System.out.printf("id:%s\n", new Object[]{var3});
                System.out.printf("version:%x\n", new Object[]{Integer.valueOf(var7)});
                System.out.printf("mstotal:%d\n", new Object[]{Long.valueOf(this.mstotal)});
                System.out.printf("length:%d\n", new Object[]{Long.valueOf(this.length)});
                System.out.printf("oplType:%d\n", new Object[]{Integer.valueOf(this.opl3_mode)});
            }

            this.data = new int[(int) this.length];

            long var4;
            for (var4 = 0L; var4 < this.length; ++var4) {
                this.data[(int) var4] = var2.read();
            }

            var2.close();
            this.total = 0L;
            ByteArrayInputStream var11 = new ByteArrayInputStream(var1);
            int var9 = var11.available();
            int[] var10 = new int[var9];

            for (var4 = 0L; var4 < (long) var9; ++var4) {
                var10[(int) var4] = var11.read();
            }

            this.rewind(0);
            if (this.opl3_mode == 1 || this.opl3_mode == 2) {
                this.opl.write(1, 5, 1);
            }

            return true;
        }
    }

    public boolean update() {
        if (this.delay > 500) {
            this.delay -= 500;
            return true;
        } else {
            this.delay = 0;

            while (this.pos < this.length) {
                int var1 = this.data[(int) this.pos];
                ++this.pos;
                switch (var1) {
                    case 0:
                        this.delay = 1 + this.data[(int) this.pos];
                        ++this.pos;
                        return true;
                    case 1:
                        this.delay = 1 + this.data[(int) this.pos] + (this.data[(int) this.pos + 1] << 8);
                        this.pos += 2L;
                        return true;
                    case 2:
                        this.currChip = 0;
                        break;
                    case 3:
                        this.currChip = 1;
                        break;
                    default:
                        if (var1 == 4) {
                            var1 = this.data[(int) this.pos];
                            ++this.pos;
                        }

                        if (this.opl3_mode == 0) {
                            this.opl.write(0, var1, this.data[(int) this.pos]);
                        } else {
                            this.opl.write(this.currChip, var1, this.data[(int) this.pos]);
                        }

                        ++this.pos;
                        this.total += (long) (this.currChip + var1 + this.data[(int) this.pos - 1]);
                }
            }

            return this.pos < this.length;
        }
    }

    public void rewind(int var1) {
        this.delay = 1;
        this.pos = (long) (this.index = 0);

        int var2;
        for (var2 = 0; var2 < 256; ++var2) {
            this.opl.write(this.currChip, var2, 0);
        }

        this.currChip = 1;

        for (var2 = 0; var2 < 256; ++var2) {
            this.opl.write(this.currChip, var2, 0);
        }

        this.currChip = 0;
        this.total = 0L;
    }

    public float getrefresh() {
        return this.delay > 500 ? 2.0F : 1000.0F / (float) this.delay;
    }
}
