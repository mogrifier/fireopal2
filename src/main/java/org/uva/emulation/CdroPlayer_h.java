//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.uva.emulation;

import java.io.FileNotFoundException;
import java.io.IOException;

abstract class CdroPlayer_h implements CPlayer {
    int[] data;
    long pos;
    long length;
    long msdone;
    long mstotal;
    int delay;
    int index;
    int opl3_mode;

    CdroPlayer_h() {
    }

    public abstract boolean load(byte[] var1) throws FileNotFoundException, IOException;

    public abstract boolean update();

    public abstract void rewind(int var1);

    public abstract float getrefresh();

    String gettype() {
        return "DOSBox Raw OPL";
    }

    public int getTotalMiliseconds() {
        return (int) this.mstotal;
    }
}
