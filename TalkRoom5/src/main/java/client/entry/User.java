package client.entry;

import lombok.Data;

/**
 * @description:
 * @author: pwby
 * @create: 2020-03-27 18:06
 **/

@Data
public class User {

    /*
     * @Date 18:07 2020-03-27  18:07:47
     * @Description 用户信息
     **/
    private String userName;
    private String password;
    private String brief;
    private boolean state;
    private String qqImage;

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
            if(this.userName.equals(((User) obj).userName) && this.password.equals(((User) obj).password)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (userName == null ? 0 : userName.hashCode());
        result = 31 * result + (password == null ? 0 : password.hashCode());
        return result;
    }
}
