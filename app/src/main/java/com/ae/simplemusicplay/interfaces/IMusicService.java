//package com.ae.simplemusicplay.interfaces;
//
//import com.ae.simplemusicplay.model.SongInfo;
//
//import java.util.ArrayList;
//
//public interface IMusicService {
//    /**
//     * 播放音乐
//     *
//     * @param listName 列表名称
//     * @param position 指针
//     */
//    public void playMusic(String listName, int position);
//
//    /**
//     * 暂停播放
//     */
//    public void pauseMusic();
//
//    /**
//     * 停止音乐播放
//     */
//    public void stopMusic();
//
//    /**
//     * 获取当前音乐播放列表
//     *
//     * @return 当前音乐播放列表
//     */
//    public ArrayList<SongInfo> getCurrentMusicList();
//
//    /**
//     * 通过列表Id获取音乐列表
//     *
//     * @param listId
//     * @return
//     */
//    public ArrayList<SongInfo> getMusicListByName(Integer listId);
//
//    /**
//     * 察看当前用户所有音乐列表
//     *
//     * @return 音乐列表的列表
//     */
//    public ArrayList<MusicList> getMusiclistList();
//
//    /**
//     * 添加音乐到指定列表
//     *
//     * @param music  音乐
//     * @param listId 列表Id
//     */
//    public void addNetWorkMusic(SongInfo music, Integer listId);
//
//    /**
//     * 扫描本地音乐
//     *
//     * @param filter 是否过滤30秒以下的音乐
//     * @return 添加的音乐文件的数量
//     */
//    public Integer findMusicOnMobile(Boolean filter);
//
//}
