package cn.linyi.music.bean;

/**
 * Created by linyi on 2016/4/19.
 */
import java.util.List;

public class User {
    private int id;
    private String uname;
    private String password;//(通过MD5加密)

    public String getCollectPlaylist() {
        return collectPlaylist;
    }

    public void setCollectPlaylist(String collectPlaylist) {
        this.collectPlaylist = collectPlaylist;
    }

    private int role;
    private String nickname;
    private String collectPlaylist;//收藏的歌单id
    /**
     * 	只记录music的id 然后通过数据库来获取具体信息
     * 用户登录后第一次返回 随json返回，并在Android端建立数据表，保存起来
     * 只有在本地没有时才想服务器发送请求 查询并更新Android端数据库信息
     */
    private String favorMusic;//在数据库中存储的是Music的id每个id用，分割；
    //在dao层封装后返回

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getRole() {
        return role;
    }
    public void setRole(int role) {
        this.role = role;
    }

    public String getFavorMusic() {
        return favorMusic;
    }

    public void setFavorMusic(String favorMusic) {
        this.favorMusic = favorMusic;
    }
}

