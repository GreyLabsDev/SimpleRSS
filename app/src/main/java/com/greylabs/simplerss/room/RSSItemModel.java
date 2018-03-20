package com.greylabs.simplerss.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by greyson on 11.03.18.
 */
@Entity
public class RSSItemModel {
    @NonNull
    @PrimaryKey
    String title;

    String description;
    String url;
    String pubDate;
    String rssSource;

    public String getRssSource() { return rssSource; }

    public void setRssSource(String rssSource) {
        this.rssSource = rssSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
}
