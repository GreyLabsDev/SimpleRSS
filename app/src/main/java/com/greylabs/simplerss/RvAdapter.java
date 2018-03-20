package com.greylabs.simplerss;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greyson on 11.03.18.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    List<String> titles = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    List<String> pubDates = new ArrayList<>();
    List<String> urls = new ArrayList<>();
    List<String> sources = new ArrayList<>();

    Context context;

    public RvAdapter(List<String> inTitles, List<String> inDescriptions,
                     List<String> inPubDates, List<String> inUrls,
                     List<String> inSources,Context inContext) {
        titles = inTitles;
        pubDates = inPubDates;
        urls = inUrls;
        descriptions = inDescriptions;
        sources = inSources;
        context = inContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.rss_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         holder.tvTitle.setText(titles.get(position));

        /*
        TODO
        More clean description
         */
        Document htmlDoc = Jsoup.parse(descriptions.get(position));
        try {
            htmlDoc.select("img").remove();
        } catch (Exception e) {e.printStackTrace();}


        Spanned htmlStringAsSpanned = Html.fromHtml(htmlDoc.toString());
         holder.tvDescription.setText(htmlStringAsSpanned);
         holder.tvPubDate.setText(pubDates.get(position));
         holder.tvSourceRSS.setText(sources.get(position));
         holder.btnFullVersion.setOnClickListener(l -> {
             Uri uri = Uri.parse(urls.get(position));
             Intent intent = new Intent(Intent.ACTION_VIEW, uri);
             context.startActivity(intent);
         });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView rss_card;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPubDate;
        TextView tvSourceRSS;
        ImageButton btnFullVersion;

        public ViewHolder(View view) {
            super(view);

            rss_card = view.findViewById(R.id.rss_card);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvPubDate = view.findViewById(R.id.tvPubDate);
            tvSourceRSS = view.findViewById(R.id.tvSourceRSS);
            btnFullVersion = view.findViewById(R.id.btnFullVersion);
        }
    }
}
