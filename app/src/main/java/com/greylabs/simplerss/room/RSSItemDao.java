package com.greylabs.simplerss.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by greyson on 11.03.18.
 */
@Dao
public interface RSSItemDao {

    @Insert
    void insertItem(RSSItemModel rssItemModel);

    @Delete
    void delete(RSSItemModel rssItemModel);

    @Update
    void update(RSSItemModel rssItemModel);

    @Query("SELECT * FROM RSSItemModel")
    List<RSSItemModel> getAllItems();
    
    @Query("SELECT COUNT(*) from RSSItemModel")
    int countItems();

    @Query("SELECT * FROM RSSItemModel WHERE title LIKE :search")
    List<RSSItemModel> findByStringInTitle(String search);

    @Query("SELECT * FROM RSSItemModel WHERE description LIKE :search")
    List<RSSItemModel> findByStringInDescription(String search);

    @Query("SELECT * FROM RSSItemModel WHERE rssSource LIKE :search")
    List<RSSItemModel> findByStringInSource(String search);

    @Query("SELECT * FROM RSSItemModel WHERE title LIKE :title LIMIT 1")
    RSSItemModel findByTitle(String title);
    
    @Query("DELETE FROM RSSItemModel")
    void nukeTable();

}
