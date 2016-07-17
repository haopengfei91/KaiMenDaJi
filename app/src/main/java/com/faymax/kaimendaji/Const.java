package com.faymax.kaimendaji;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HPF on 2016/7/9.
 */
public class Const {

    public static List<Song> songList = new ArrayList<Song>();

    public static final int TOTAL_COINS = 1000;

    public static final int TOTAL_COUNTS = 78;

    public static String[] tone = {"enter.mp3", "cancel.mp3", "coin.mp3"};
    public static final int INDEX_TONE_ENTER = 0;
    public static final int INDEX_TONE_CANCEL = 1;
    public static final int INDEX_TONE_COIN = 2;

    public static final String FILE_NAME_SAVE = "data.dat";

    public static String[] getFileName(Context context) {
        String[] fileName = null;
        try {
            fileName = context.getAssets().list("song");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static String[] songName = {"最炫民族风", "壮志在我胸", "祝你平安", "致青春", "真心英雄", "咱们屯里的人", "在希望的田野上",
            "再生花", "缘分的天空", "永远", "阴天", "鸭子", "许愿池的希腊少女", "小城故事",
            "小城大事", "乡恋", "希望", "无所谓", "无间道", "我这个你不爱的人", "我用自己的方式爱你",
            "我和草原有个约定", "蜗牛与黄鹂鸟", "忘情水", "万物生", "外婆的澎湖湾", "突然想起你", "童年",
            "同桌的你", "听妈妈讲那过去的事情", "天涯", "天路", "天空", "他不爱我", "盛夏的果实",
            "生日礼物", "伤心太平洋", "如果你是我的传说", "让我们荡起双桨", "燃烧吧小宇宙", "秋天的童话", "秋天不回来",
            "青花瓷", "千言万语", "你知道我在等你吗", "茉莉花", "明天我要嫁给你", "美丽的草原我的家", "刘海砍樵",
            "恋爱百分百", "狼爱上羊", "狂野之城", "靠近我", "开门大吉", "酒醉的探戈", "简单爱", "家乡",
            "花好月圆夜", "蝴蝶泉边", "洪湖水浪打浪", "和自己赛跑的人", "滚滚长江东逝水", "甘心情愿", "父亲",
            "哆啦A梦", "电台情歌", "当爱已成往事", "但愿人长久", "大哥你好吗", "春天在哪里", "差一点", "曹操",
            "采蘑菇的小姑娘", "表白", "北国之春", "爱是你我", "爱平才会赢", "爱的呼唤" };

    public static List<Song> getDatas(Context context) {

        String[] fileName = getFileName(context);

        for (int i=0; i<fileName.length; i++) {
            Song song = new Song();
            song.setFileName(fileName[i]);
            song.setSongName(songName[i]);
            songList.add(song);
        }
        return songList;
    }

//    private static String[] file = {"__00000.m4a", "__00001.m4a", "__00002.m4a",
//            "__00003.m4a", "__00004.m4a", "__00005.m4a",
//            "__00006.m4a", "__00007.m4a", "__00008.m4a",
//            "__00009.m4a", "__000010.m4a"};
//
//    private static String[] name = {"征服", "童话", "同桌的你",
//            "七里香", "传奇", "大海",
//            "后来", "你的背包", "再见",
//            "老男孩", "龙的传人"};

//    public static List<Song> getDatas() {
//
//
//        for (int i = 0; i < file.length; i++) {
//            Song song = new Song();
//            song.setFileName(file[i]);
//            song.setSongName(name[i]);
//            songList.add(song);
//        }
//        return songList;
//    }

    public static final int INDEX_LOAD_STAGE = 0;
    public static final int INDEX_LOAD_COINS = 1;
}
