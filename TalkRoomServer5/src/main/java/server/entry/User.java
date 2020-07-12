package server.entry;
import lombok.Data;
@Data
public class User {

    private String userName;      //用户名
    private String password;      //密码
    private String brief;         //个性签名
    private boolean state;        //状态
    private String qqImage;       //qq图像

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(obj instanceof User){
            User other = (User) obj;
            //需要比较的字段相等，则这两个对象相等
            if(this.userName.equals(((User) obj).userName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (userName == null ? 0 : userName.hashCode());
        return result;
    }
}
