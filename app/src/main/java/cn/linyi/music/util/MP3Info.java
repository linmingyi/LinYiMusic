package cn.linyi.music.util;

/**
 * Created by linyi on 2016/3/23.
 */
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import cn.linyi.music.bean.Music;


/**
 * 获得MP3文件的信息
 *
 */
public class MP3Info {

    public static  Music getMusicInfo(File file,Music music) {
        music = new Music();
        //TODO 演示

        String path = file.getPath();
        music.setPath(path);
        Log.i("YI",path+"________");
        String title = path.substring(path.lastIndexOf(File.separator)+1,path.lastIndexOf(".mp3"));
        System.out.println("NAMENAMENAME:    "+title);
        music.setTitle(title);
        try {
            MP3Info info = new MP3Info(file);
            info.setCharset("UTF-8");
            music.setTitle(info.getSongName());
            music.setArtist(info.getArtist());
            music.setAlbum(info.getAlbum());

         /*   System.out.println(info.getSongName());
            System.out.println(info.getArtist());
            System.out.println(info.getAlbum());
            System.out.println(info.getYear());
            System.out.println(info.getComment());*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return music;

    }


    private String charset = "utf-8";//解析MP3信息时用的字符编码

    private byte[] buf;//MP3的标签信息的byte数组

    /**
     * 实例化一个获得MP3文件的信息的类
     * @param mp3 MP3文件
     * @throws IOException 读取MP3出错或则MP3文件不存在
     */
    public MP3Info(File mp3) throws IOException{

        buf = new byte[128];//初始化标签信息的byte数组

        RandomAccessFile raf = new RandomAccessFile(mp3, "r");//随机读写方式打开MP3文件
        raf.seek(raf.length() - 128);//移动到文件MP3末尾
        raf.read(buf);//读取标签信息

        raf.close();//关闭文件

        if(buf.length != 128){//数据是否合法
            throw new IOException("MP3标签信息数据长度不合法!");
        }

        if(!"TAG".equalsIgnoreCase(new String(buf,0,3))){//信息格式是否正确
            throw new IOException("MP3标签信息数据格式不正确!");
        }

    }

    /**
     * 获得目前解析时用的字符编码
     * @return 目前解析时用的字符编码
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置解析时用的字符编码
     * @param charset 解析时用的字符编码
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSongName(){
        try {
            return new String(buf,3,30,charset).trim();
        } catch (UnsupportedEncodingException e) {
            return new String(buf,3,30).trim();
        }
    }

    public String getArtist(){
        try {
            return new String(buf,33,30,charset).trim();
        } catch (UnsupportedEncodingException e) {
            return new String(buf,33,30).trim();
        }
    }

    public String getAlbum(){
        try {
            return new String(buf,63,30,charset).trim();
        } catch (UnsupportedEncodingException e) {
            return new String(buf,63,30).trim();
        }
    }

    public String getYear(){
        try {
            return new String(buf,93,4,charset).trim();
        } catch (UnsupportedEncodingException e) {
            return new String(buf,93,4).trim();
        }
    }

    public String getComment(){
        try {
            return new String(buf,97,28,charset).trim();
        } catch (UnsupportedEncodingException e) {
            return new String(buf,97,28).trim();
        }
    }


}
