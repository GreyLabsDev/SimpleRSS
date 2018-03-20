package com.greylabs.simplerss;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import com.greylabs.simplerss.room.RSSDatabase;
import com.greylabs.simplerss.room.RSSItemModel;
import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {
    private String firstRSS = "https://www.androidauthority.com/feed/";

    RecyclerView rv;
    EditText etChannelRSS;
    Toolbar toolbar;
    SearchView searchView;
    Button btnOk;
    ConstraintLayout constraintAddPanel;
    
    ViewGroup scene;

    List<String> titles = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    List<String> pubDates = new ArrayList<>();
    List<String> urls = new ArrayList<>();
    List<String> sources = new ArrayList<>();

    LiveData<List<RSSItemModel>> uiData;

    RSSController rssController = new RSSController();

    RSSDatabase database;

    MenuItem addSourceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        database = Room.databaseBuilder(getApplicationContext(),
                RSSDatabase.class, "rss-database")
                .build();


        btnOk = findViewById(R.id.btnOk);
        etChannelRSS = findViewById(R.id.etChannelRSS);
        rv = findViewById(R.id.rvFeed);
        toolbar = findViewById(R.id.toolbar);
        constraintAddPanel = findViewById(R.id.constraintAddPanel);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        scene = (ViewGroup) this.findViewById(R.id.toolbar).getRootView();
    
        SnacksMachine.setCurrentView(rv);
        SnacksMachine.initColors();

        rssController.init();
        rssController.setItemsData(titles, descriptions, pubDates,
                urls, sources);
        rssController.setActiveDatabase(database);
        rssController.addSource(firstRSS);

        uiData = rssController.getData();
        uiData.observe(this, new android.arch.lifecycle.Observer<List<RSSItemModel>>() {
            @Override
            public void onChanged(@Nullable List<RSSItemModel> rssItemModels) {
                updateRvAdapter(rssItemModels);
//                SnacksMachine.showSnackbar("RSS feed updated/read", "green");
            }
        });

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        
//        OLD - for using floating button to add new sources
//        --------------------------------------------------
//        rv.addOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
//                    fab.hide();
//                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
//                    fab.show();
//                }
//            }
//        });

        rssController.fetchRSS();
    }

    public void onClickBtnFetchRSS(View view) {
        
        final String source = etChannelRSS.getText().toString();

        if (!source.contains("http")) {
            SnacksMachine.showLongSnackbar("Please, check RSS url", "orange");
        } else {

        try {
                rssController.addSource(source);

                rssController.fetchRSS();

                TransitionManager.beginDelayedTransition(scene);
                etChannelRSS.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
                constraintAddPanel.setVisibility(View.GONE);
                addSourceItem.setIcon(R.drawable.bookmark_unfill_drawable);
                etChannelRSS.setText("");
                SnacksMachine.showSnackbar("Added new RSS channel", "green");
        } catch (Exception e) {}
        }
    }
    
//        OLD - for using floating button to add new sources
//        --------------------------------------------------
//    public void onClickBtnAddSource(View view) {
//        if (etChannelRSS.getVisibility() != View.GONE) {
//            TransitionManager.beginDelayedTransition(scene);
//            etChannelRSS.setVisibility(View.GONE);
//            btnOk.setVisibility(View.GONE);
//            constraintAddPanel.setVisibility(View.GONE);
//
//        } else {
//            TransitionManager.beginDelayedTransition(scene);
//            etChannelRSS.setVisibility(View.VISIBLE);
//            btnOk.setVisibility(View.VISIBLE);
//            constraintAddPanel.setVisibility(View.VISIBLE);
//
//        }
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem svItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) svItem.getActionView();
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByString(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByString(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDropDb:
                SnacksMachine.showSnackbar("Deleting RSS data", "orange");
                new Thread(() -> {
                    database.getRssIdemDao().nukeTable();
                    sources.clear();
                    urls.clear();
                    SnacksMachine.showSnackbar("RSS data and sources deleted", "red");
                }).start();
                return super.onOptionsItemSelected(item);
            case R.id.actionFetch:
                rssController.fetchRSS();
                return super.onOptionsItemSelected(item);
            case R.id.actionAddSrc:
                if (etChannelRSS.getVisibility() != View.GONE) {
                    TransitionManager.beginDelayedTransition(scene);
                    etChannelRSS.setVisibility(View.GONE);
                    btnOk.setVisibility(View.GONE);
                    constraintAddPanel.setVisibility(View.GONE);
                    if (item.getItemId() == R.id.actionAddSrc) {
                        item.setIcon(R.drawable.bookmark_unfill_drawable);
                        addSourceItem = item;
                    }
                    return super.onOptionsItemSelected(item);
                } else {
                    TransitionManager.beginDelayedTransition(scene);
                    etChannelRSS.setVisibility(View.VISIBLE);
                    btnOk.setVisibility(View.VISIBLE);
                    constraintAddPanel.setVisibility(View.VISIBLE);
                    if (item.getItemId() == R.id.actionAddSrc) {
                        item.setIcon(R.drawable.plus_bookmark_drawable);
                        addSourceItem = item;
                    }
                    return super.onOptionsItemSelected(item);
        
                }
            default:
                getSupportActionBar().setIcon(R.drawable.search_drawable);
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }
    
    public void updateRvAdapter(List<RSSItemModel> items) {
        titles.clear();
        descriptions.clear();
        pubDates.clear();
        urls.clear();
        sources.clear();

        for (int i = 0; i < items.size(); i++) {
            titles.add(items.get(i).getTitle());
            urls.add(items.get(i).getUrl());
            pubDates.add(items.get(i).getPubDate());
            descriptions.add(items.get(i).getDescription());
            sources.add(items.get(i).getRssSource());
        }

        runOnUiThread(() -> {
                final RvAdapter adapterNew = new RvAdapter(titles, descriptions, pubDates,
                        urls, sources, this);
                rv.setAdapter(adapterNew);
            });
    }

    public void searchByString(String query) {
        final String searchWord = "%" + query + "%";

        new Thread(() -> {
            try {
                List<RSSItemModel> foundItems;
                if (searchWord.replaceAll("%", "") != "") {
                    foundItems = database.getRssIdemDao().findByStringInTitle(searchWord);
                    if (foundItems.size() == 0) {
                        foundItems = database.getRssIdemDao().findByStringInSource(searchWord);
                        if (foundItems.size() == 0) {
                            foundItems = database.getRssIdemDao().findByStringInDescription(searchWord);
                        }
                    }
                } else {
                    foundItems = database.getRssIdemDao().getAllItems();
                }

                updateRvAdapter(foundItems);
            } catch (Exception e) {}
        }).start();
    }


}
