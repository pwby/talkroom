package utils;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

/**
 * @description:
 * @author: pwby
 * @create: 2020-05-08 09:52
 **/
public class TimeUtils {
    public static String getTime(){
        long now = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(now);
    }
}
