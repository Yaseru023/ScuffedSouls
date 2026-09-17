package net.yaseruxd.scuffedsouls.client;

public class ClientHollowData {

    private static int hollowLevel = 0;

    public static void set(int level) {
        hollowLevel = level;
    }

    public static int get() {
        return hollowLevel;
    }
}