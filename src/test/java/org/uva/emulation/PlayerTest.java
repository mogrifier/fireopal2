package org.uva.emulation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PlayerTest extends TestCase {

    public PlayerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(PlayerTest.class);
    }

    public void testApp() {
        // new File(Paths.get(getClass().getClassLoader().getResource(".").toURI()).getParent().getParent().toString(), "/third-party/assets/dott_chron-o-john_station2.laa").exists()

        assertTrue(true);
    }
}
