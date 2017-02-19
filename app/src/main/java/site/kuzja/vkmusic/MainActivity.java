package site.kuzja.vkmusic;

import site.kuzja.vkmusic.api.VkApi;
import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.objects.Audio;
import site.kuzja.vkmusic.api.objects.AudioList;
import site.kuzja.vkmusic.api.objects.UserActor;
import site.kuzja.vkmusic.dao.DAOFactory;
import site.kuzja.vkmusic.dao.DAOSQLite;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {
    private UserActor userActor = null;
    private AudioList audioList = null;
    private GetAudioTask mGetAudioTask = null;

    private static final Type daoType = DAOSQLite.class;
    private static final String LOG_TAG = "MainActivity";

    private View mContentMain;
    private View mProgressView;
    private MediaService mMediaService = null;

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
                    .deleteUserActor();
            mMediaService.release();
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
     *
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
                final BrowseAdapter adapter = new BrowseAdapter(MainActivity.this, audioList);
                mMusicList.setAdapter(adapter);
                mMediaService = new MediaService(new MediaService.UpdateImpl() {
                    @Override
                    public void update() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void playNextItem(Audio item) {
                        int position = adapter.getPosition(item) + 1;
                        position = position == adapter.getCount() ? 0 : position;
                        if (! mMediaService.setAudioItem(adapter.getItem(position)))
                            AlertDialogFactory.create(getString(R.string.error_title), "Ошибка загрузки аудиозаписи",
                                    AlertDialogFactory.BUTTONS_OK, MainActivity.this).show();
                    }
                });
                // устанавливем обработчик нажатия
                mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (audioList.getItems().get(position).getStatus() == Audio.STATUS_PREPARING)
                            return;
                        if (! mMediaService.setAudioItem(audioList.getItems().get(position)))
                            AlertDialogFactory.create(getString(R.string.error_title), "Ошибка загрузки аудиозаписи",
                                    AlertDialogFactory.BUTTONS_OK, MainActivity.this).show();
                    }
                });
            } else {
                AlertDialogFactory.create(getString(R.string.error_title), "Ошибка загрузки аудиозаписей",
                        AlertDialogFactory.BUTTONS_OK, MainActivity.this).show();
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
        ImageView mImageView;
         TextView mArtistView;
         TextView mTitleView;
         ProgressBar mProgressBar;
    }

    private static class BrowseAdapter extends ArrayAdapter<Audio> {
        private static ColorStateList sColorStatePlaying;
        private static ColorStateList sColorStateNotPlaying;

        BrowseAdapter(Activity context, AudioList list) {
            super(context, R.layout.music_item_layout, list.getItems());
            sColorStateNotPlaying = ColorStateList.valueOf(context.getResources().getColor(
                    R.color.media_item_icon_not_playing));
            sColorStatePlaying = ColorStateList.valueOf(context.getResources().getColor(
                    R.color.media_item_icon_playing));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // ViewHolder буферизирует оценку различных полей шаблона элемента

            ViewHolder holder;
            Audio item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.music_item_layout, parent, false);
                holder = new ViewHolder();
                holder.mArtistView = (TextView) convertView.findViewById(R.id.artist);
                holder.mTitleView = (TextView) convertView.findViewById(R.id.title);
                holder.mImageView = (ImageView) convertView.findViewById(R.id.play_eq);
                holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mArtistView.setText(item.getArtist());
            holder.mTitleView.setText(item.getTitle());

            Drawable drawable;
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mImageView.setImageTintList(sColorStatePlaying);
            switch (item.getStatus()) {
                case Audio.STATUS_STOPED:
                    drawable = ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_play_arrow_black_36dp);
                    holder.mImageView.setImageTintList(sColorStateNotPlaying);
                    break;
                case Audio.STATUS_PLAYING:
                    drawable = ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_pause_black_36dp);
                    break;
                case Audio.STATUS_PAUSED:
                    drawable = ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_play_arrow_black_36dp);
                    break;
                case Audio.STATUS_PREPARING:
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                default:
                    drawable = null;
            }
            if (drawable != null) {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.mImageView.setImageDrawable(drawable);
            } else {
                holder.mImageView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }


}
