package site.kuzja.vkmusic;

import site.kuzja.vkmusic.api.VkApi;
import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.objects.Audio;
import site.kuzja.vkmusic.api.objects.AudioList;
import site.kuzja.vkmusic.api.objects.UserActor;
import site.kuzja.vkmusic.dao.DAOFactory;
import site.kuzja.vkmusic.dao.DAOSQLite;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {
    private UserActor userActor = null;
    private AudioList audioList = null;
    private GetAudioTask mGetAudioTask = null;

    private static final Type daoType = DAOSQLite.class;

    private View mContentMain;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentMain = findViewById(R.id.content_main);
        mProgressView = findViewById(R.id.load_progress);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        userActor = DAOFactory.create(getApplicationContext(), daoType)
                .getUserActor();

        if (userActor == null) {
            login();
        } else {
            loadAudioList();
        }

    }

    private void login(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(i, 1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContentMain.setVisibility(show ? View.GONE : View.VISIBLE);
            mContentMain.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentMain.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mContentMain.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    // Function to read the result from newly created activity
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            userActor = new UserActor(extras.getInt("user_id"), extras.getString("access_token"),
                    extras.getInt("expires_in"));
            DAOFactory.create(getApplicationContext(), daoType)
                    .saveUserActor(userActor);
            loadAudioList();
        }
        else {
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
        if (id == R.id.action_quit) {
            userActor = null;
            audioList = null;
            DAOFactory.create(getApplicationContext(), daoType)
                    .clear();
            login();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAudioList() {
        showProgress(true);
        mGetAudioTask = new MainActivity.GetAudioTask();
        mGetAudioTask.execute((Void) null);
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GetAudioTask extends AsyncTask<Void, Void, Boolean> {
        GetAudioTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (userActor == null)
                return false;
            try {
                audioList = new VkApi().audio.get(userActor.getUserID(), userActor.getAccessToken());
                Log.v("audioList", audioList.toString());
            } catch (ClientException e) {
                Log.v("ClientException", e.getMessage());
                return false;
            } catch (ApiException e) {
                Log.v("ApiException", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetAudioTask = null;
            showProgress(false);

            if (success) {
                // находим список
                ListView mMusicList = (ListView) findViewById(R.id.music_list);
                // присваиваем адаптер списку
                mMusicList.setAdapter(new BrowseAdapter(MainActivity.this, audioList));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Ошибка!")
                        .setMessage("Ошибка загрузки аудиозаписей!")
                        .setIcon(R.drawable.ic_launcher)
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mGetAudioTask = null;
            showProgress(false);
        }
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
