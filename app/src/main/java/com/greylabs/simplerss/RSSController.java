package com.greylabs.simplerss;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.greylabs.simplerss.room.RSSDatabase;
import com.greylabs.simplerss.room.RSSItemModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by greyson on 12.03.18.
 */

public class RSSController {
    List<List<String>> itemsData = new ArrayList<>();
    List<String> sourcesRSS = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();

    MutableLiveData<List<RSSItemModel>> conrollerData;

    RSSDatabase activeDatabase;

    int TITLES = 0;
    int DESCRIPTIONS = 1;
    int PUBDATES = 2;
    int URLS = 3;
    int SOURCES = 4;

    public void init() {
        conrollerData = new MutableLiveData<>();
    }

    public LiveData<List<RSSItemModel>> getData() {
        return  conrollerData;
    }

    public void setItemsData(List<String> inTitles, List<String> inDescriptions,
                             List<String> inPubDates, List<String> inUrls,
                             List<String> inSources) {
        itemsData.add(inTitles);
        itemsData.add(inDescriptions);
        itemsData.add(inPubDates);
        itemsData.add(inUrls);
        itemsData.add(inSources);

    }

    public void addSource(String inSource) {
        if (!sourcesRSS.contains(inSource)) {
            sourcesRSS.add(inSource);
        }
    }

    public void removeSource(String inSource) {
        if (sourcesRSS.contains(inSource)) {
            int removeId = sourcesRSS.indexOf(inSource);
            sourcesRSS.remove(removeId);
        }
    }

    public RSSDatabase getActiveDatabase() {
        return activeDatabase;
    }

    public void setActiveDatabase(RSSDatabase activeDatabase) {
        this.activeDatabase = activeDatabase;
    }

    public void fetchRSS() {
        for (int i = 0; i < itemsData.size(); i++) {
            itemsData.get(i).clear();
        }
        final boolean[] breakFlag = {false};
        for (String source : sourcesRSS) {
            final String sourceIn = source;
            Request request = new Request.Builder()
                    .url(source)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    SnacksMachine.showSnackbar("Please, check network connection and RSS url", "red");
                    breakFlag[0] = false;
                    List<RSSItemModel> allItems = activeDatabase.getRssIdemDao().getAllItems();
                    conrollerData.postValue(allItems);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respBodyString = response.body().string();

                    try {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(new ByteArrayInputStream(respBodyString.getBytes()));
                        doc.getDocumentElement().normalize();

                        NodeList items = doc.getElementsByTagName("item");

                        for (int i = 0; i < items.getLength(); i++) {
                            Node node = items.item(i);
                            Element element = (Element) node;

                            itemsData.get(TITLES).add(element.getElementsByTagName("title").item(0).getTextContent());
                            Log.d("RSS_TITLE", element.getElementsByTagName("title").item(0).getTextContent());
                            itemsData.get(DESCRIPTIONS).add(element.getElementsByTagName("description").item(0).getTextContent());
                            itemsData.get(PUBDATES).add(element.getElementsByTagName("pubDate").item(0).getTextContent());
                            itemsData.get(URLS).add(element.getElementsByTagName("link").item(0).getTextContent());
                            itemsData.get(SOURCES).add(sourceIn.split("://")[1].split("/")[0]);

                            RSSItemModel newItem = new RSSItemModel();

                            newItem.setTitle(itemsData.get(TITLES).get(i));
                            newItem.setDescription(itemsData.get(DESCRIPTIONS).get(i).replaceAll("\n", ""));
                            newItem.setPubDate(itemsData.get(PUBDATES).get(i));
                            newItem.setUrl(itemsData.get(URLS).get(i));
                            newItem.setRssSource(itemsData.get(SOURCES).get(i));

                            if (activeDatabase.getRssIdemDao().findByTitle(itemsData.get(TITLES).get(i)) == null) {
                                activeDatabase.getRssIdemDao().insertItem(newItem);
                            }

                        }

                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }

                    List<RSSItemModel> allItems = activeDatabase.getRssIdemDao().getAllItems();
                    conrollerData.postValue(allItems);
                }
            });
            if (breakFlag[0]) {

                break;
            }
        }

    }


}
