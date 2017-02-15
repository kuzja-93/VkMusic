package site.kuzja.vkmusic;

import site.kuzja.vkmusic.objects.Audio;
import site.kuzja.vkmusic.objects.AudioList;
import site.kuzja.vkmusic.objects.UserActor;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        VkApi api = new VkApi();
        try {
            UserActor actor = api.auch("79132469232", "Ax357522064");
            Log.v("UserActor", actor.toString());
            AudioList audioList = api.audioGet(actor.getUserID(), actor.getAccessToken());
            Log.v("audioList", audioList.toString());
            // находим список
            ListView mMusicList = (ListView) findViewById(R.id.music_list);
            // присваиваем адаптер списку
            mMusicList.setAdapter(new BrowseAdapter(this, audioList));
        } catch (ClientException e) {
            Log.v("ClientException", e.toString());
            finish();
        } catch (ApiException e) {
            Log.v("ApiException", e.toString());
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    static class ViewHolder {
        public ImageView imageView;
        public TextView artistView;
        public TextView titleView;
    }

    private static class BrowseAdapter extends ArrayAdapter<Audio> {


        public BrowseAdapter(Activity context, AudioList list) {
            super(context, R.layout.music_item_layout, list.getItems());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // ViewHolder буферизирует оценку различных полей шаблона элемента

            ViewHolder holder;
            Audio item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from((Activity) getContext())
                        .inflate(R.layout.music_item_layout, parent, false);
                holder = new ViewHolder();
                holder.artistView = (TextView) convertView.findViewById(R.id.artist);
                holder.titleView = (TextView) convertView.findViewById(R.id.title);
                holder.imageView = (ImageView) convertView.findViewById(R.id.play_eq);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.artistView.setText(item.getArtist());
            holder.titleView.setText(item.getTitle());

            return convertView;
        }
    }
}
