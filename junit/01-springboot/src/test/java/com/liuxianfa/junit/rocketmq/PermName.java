package com.liuxianfa.junit.rocketmq;

public class PermName {
    public static final int PERM_PRIORITY = 1 << 3;
    public static final int PERM_READ = 1 << 2;
    public static final int PERM_WRITE = 1 << 1;
    public static final int PERM_INHERIT = 1;

    public static String perm2String(final int perm) {
        final StringBuilder sb = new StringBuilder("---");
        if (isReadable(perm)) {
            sb.replace(0, 1, "R");
        }

        if (isWriteable(perm)) {
            sb.replace(1, 2, "W");
        }

        if (isInherited(perm)) {
            sb.replace(2, 3, "X");
        }

        return sb.toString();
    }

    public static boolean isReadable(final int perm) {
        return (perm & PERM_READ) == PERM_READ;
    }

    public static boolean isWriteable(final int perm) {
        return (perm & PERM_WRITE) == PERM_WRITE;
    }

    /**
     * 判断是否可继承
     */
    public static boolean isInherited(final int perm) {
        return (perm & PERM_INHERIT) == PERM_INHERIT;
    }


    public static void main(String[] args) {

        System.out.println(PERM_PRIORITY);
        System.out.println(PERM_READ);
        System.out.println(PERM_WRITE);
        System.out.println(PERM_INHERIT);


        int perm = PERM_READ | PERM_WRITE | PERM_INHERIT;
        System.out.println(perm2String(perm));

        // 减去可继承
        perm &= ~PermName.PERM_INHERIT;
        System.out.println(perm2String(perm));

        // 减去可读
        perm &= ~PermName.PERM_WRITE;
        System.out.println(perm2String(perm));


        // 添加可写
        perm |= PERM_WRITE;
        System.out.println(perm2String(perm));
    }
}
