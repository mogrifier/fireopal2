package org.uva.emulation;

import java.io.FileNotFoundException;
import java.io.IOException;

interface CPlayer {
    boolean load(byte[] var1) throws FileNotFoundException, IOException;

    void rewind(int var1);

    float getrefresh();

    boolean update();

    int getTotalMiliseconds();



}
