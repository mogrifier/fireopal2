package org.uva.emulation;

import com.cozendey.opl3.OPL3;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class Cdro2Player implements CPlayer {
    int[] data;
    long pos;
    long length;
    long msdone;
    long mstotal;
    int delay;
    int delay256;
    int delayShift8;
    int[] toReg;
    int opl3Type;
    long total = 0L;
    OPL3 opl;
    boolean verbosePlayer;

    public int getTotalMiliseconds() {
        return (int) this.mstotal;
    }

    public Cdro2Player(OPL3 var1, boolean var2) {
        this.verbosePlayer = var2;
        this.opl = var1;
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

            if (var7 != 2) {
                var2.close();
                return false;
            } else {
                var8 = 0;

                int var9;
                for (var9 = 0; var9 < 4; ++var9) {
                    var8 |= var2.read() << var9 * 8;
                }

                this.length = (long) (var8 * 2);
                this.mstotal = 0L;

                for (var9 = 0; var9 < 4; ++var9) {
                    this.mstotal |= (long) (var2.read() << var9 * 8);
                }

                this.opl3Type = var2.read();
                if (var2.read() != 0) {
                    var2.close();
                    return false;
                } else if (var2.read() != 0) {
                    var2.close();
                    return false;
                } else {
                    this.delay256 = var2.read();
                    this.delayShift8 = var2.read();
                    var9 = var2.read();
                    this.toReg = new int[var9];

                    for (int var10 = 0; var10 < var9; ++var10) {
                        this.toReg[var10] = var2.read();
                    }

                    if (this.verbosePlayer) {
                        System.out.printf("id:%s\n", new Object[]{var3});
                        System.out.printf("version:%x\n", new Object[]{Integer.valueOf(var7)});
                        System.out.printf("length:%d\n", new Object[]{Long.valueOf(this.length)});
                        System.out.printf("mstotal:%d\n", new Object[]{Long.valueOf(this.mstotal)});
                        System.out.printf("opl3Type:%d\n", new Object[]{Integer.valueOf(this.opl3Type)});
                        System.out.printf("delay256:%d\n", new Object[]{Integer.valueOf(this.delay256)});
                        System.out.printf("delayShift8:%d\n", new Object[]{Integer.valueOf(this.delayShift8)});
                    }

                    this.data = new int[(int) this.length];

                    long var4;
                    for (var4 = 0L; var4 < this.length; ++var4) {
                        this.data[(int) var4] = var2.read();
                    }

                    var2.close();
                    this.total = 0L;
                    ByteArrayInputStream var13 = new ByteArrayInputStream(var1);
                    int var11 = var13.available();
                    int[] var12 = new int[var11];

                    for (var4 = 0L; var4 < (long) var11; ++var4) {
                        var12[(int) var4] = var13.read();
                    }

                    this.rewind(0);
                    if (this.opl3Type != 0) {
                        this.opl.write(1, 5, 1);
                    }

                    return true;
                }
            }
        }
    }

    public boolean update() {
        this.delay = 0;

        while (this.pos < this.length) {
            int var1 = this.data[(int) this.pos] & 255;
            ++this.pos;
            if (var1 == this.delay256) {
                this.delay += 1 + (this.data[(int) this.pos] & 255);
                ++this.pos;
                return true;
            }

            if (var1 == this.delayShift8) {
                this.delay = 1 + (this.data[(int) this.pos] & 255) << 8;
                ++this.pos;
                return true;
            }

            int var2 = this.toReg[var1 & 127] & 255;
            int var3 = var1 >> 7 & 1;
            int var4 = this.data[(int) this.pos] & 255;
            ++this.pos;
            this.opl.write(var3, var2, var4);
        }

        return this.pos < this.length;
    }

    public void rewind(int var1) {
        this.delay = 1;
        this.pos = 0L;

        for (int var2 = 0; var2 < 256; ++var2) {
            this.opl.write(0, var2, 0);
            this.opl.write(1, var2, 0);
        }

        this.total = 0L;
    }

    public float getrefresh() {
        return 1000.0F / (float) this.delay;
    }
}
