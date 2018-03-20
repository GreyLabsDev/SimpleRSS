package com.greylabs.simplerss.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greyson on 11.03.18.
 */
@Database(entities = {RSSItemModel.class}, version = 1)
public abstract class RSSDatabase extends RoomDatabase {

    public abstract RSSItemDao getRssIdemDao();

    /*
    TODO
    expected structure:
    source one
    source two
    source three
    source one
     */
    public List<RSSItemModel> mixItems (List<RSSItemModel> inItems, List<String> sources) {
        List<RSSItemModel> mixedItems = new ArrayList<>();

        for (int i = 0; i < inItems.size(); i++) {
            for (int j = 0; j < sources.size(); j++) {
                if (inItems.get(i).getRssSource() == sources.get(j)) {
                    mixedItems.add(inItems.get(i));
                    j++;
                    if (j == sources.size()) {j = 0;}
                }
            }


        }

        return mixedItems;
    }
}
