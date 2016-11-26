package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uyan on 07/11/16.
 */
public class MyRemoteViewsWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(),intent);
    }

    class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        Context mContext=null;
        private Cursor mCursor;

        public WidgetRemoteViewsFactory(Context context, Intent intent){

            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            //Refresh cursor
            if(mCursor != null){
                mCursor.close();
            }

            //http:stackoverflow.com/questions/13187284/android-permission-denial-in-widget
            // -remoteviewsfactory-for-content
            final long token = Binder.clearCallingIdentity();
            try{
                mCursor = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        null,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null
                );
            }finally {
                Binder.restoreCallingIdentity(token);
            }


        }


        @Override
        public void onDestroy() {
            if(mCursor!=null){
                mCursor.close();
            }
        }

        @Override
        public int getCount() {

            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            if(mCursor.moveToPosition(i)){

                StringBuilder sb  =new StringBuilder();
                sb.append(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)) + " ");
                sb.append(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));


                RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                        android.R.layout.simple_list_item_1);
                rv.setTextViewText(android.R.id.text1,sb.toString());

                Intent fillInintent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("symbol",
                        mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
                fillInintent.putExtras(bundle);
                rv.setOnClickFillInIntent(android.R.id.text1,fillInintent);
                return rv;
            }
            else {
                return null;
            }

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            //Have only one type of view
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
