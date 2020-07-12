package client.util;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    /*
     * @Date 9:35 2020-03-31  09:35:28
     * @Description 通过类加载器加载资源文件
     * @Param 所加载资源的文件名
     * @return  properties
     **/
    public static Properties loadProperties(String fileName){
        Properties properties = new Properties();
        InputStream in = CommonUtil.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }

    /*
     * @Date 9:37 2020-03-31  09:37:45
     * @Description 将任意对象序列化为json字符串
     * @Param  对象
     * @return 字符串
     **/
    public static String object2Json(Object obj){
        return GSON.toJson(obj);
    }

    /*
     * @Date 9:40 2020-03-31  09:40:08
     * @Description 将json字符串反序列化为对象
     * @Param
     * @return
     **/
    public static <T> T json2Object(String jsonStr,Class<T> typeClass){
        return GSON.fromJson(jsonStr,typeClass);
    }
    public static <T> Set<T> json2Set(String jsonStr, Class<T> cls){
        Set<T> set = new HashSet<T>();
        JsonArray arry = new JsonParser().parse(jsonStr).getAsJsonArray();
        for(JsonElement jsonElement:arry){
            set.add(GSON.fromJson(jsonElement,cls));
        }
        return set;
    }

    /*
     * @Date 9:42 2020-03-31  09:42:27
     * @Description 将时间转换为"yyyy-MM-dd HH:mm:ss"字符串
     * @Param
     * @return
     **/
    public static String getTime(){
        long time=System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }
}
