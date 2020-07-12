package server.util;
import com.google.gson.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class CommUtils {
    //创建GSON对象
    private static final Gson GSON = new GsonBuilder().create();

    //加载Properties文件
    public static Properties loadProperties(String fileName){
        Properties properties = new Properties();
        //通过类加载器获取文件输入流
        InputStream in = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try{
            properties.load(in);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }

    /*
     * @Description 将任意对象序列化为json字符串
     **/
    public static String object2Json(Object obj){
        return GSON.toJson(obj);
    }

    /*
     * @Description 将任意对象反序列化为对象
     */
    public static <T> T json2Object(String jsonStr,Class<T> typeClass){
        return GSON.fromJson(jsonStr,typeClass);
    }
}
