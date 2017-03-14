package com.mts.pos.Common;
/**
 * 防止用户频繁的二次点击
 */
public class NoDoubleClickUtils {
	private static long lastClickTime;
	private final static int SPACE_TIME = 3000;

	public static void initLastClickTime() {
		lastClickTime = 0;
	}

	public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if ((currentTime - lastClickTime) > SPACE_TIME) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;
        return isClick2;
    }
}
