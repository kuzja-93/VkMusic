package site.kuzja.vkmusic.ui;

import site.kuzja.vkmusic.DownloadFileFromURL;
import site.kuzja.vkmusic.Media.MediaService;
import site.kuzja.vkmusic.Media.MusicItem;
import site.kuzja.vkmusic.Media.MusicItemFactory;
import site.kuzja.vkmusic.R;
import site.kuzja.vkmusic.interfaces.OnPlayNextItem;
import site.kuzja.vkmusic.interfaces.OnUIUpdateListener;
import site.kuzja.vkmusic.api.VkApi;
import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.objects.AudioList;
import site.kuzja.vkmusic.api.objects.UserActor;
import site.kuzja.vkmusic.dao.DAOFactory;
import site.kuzja.vkmusic.dao.DAOSQLite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements OnUIUpdateListener,
        OnPlayNextItem {
    private UserActor userActor = null;
    private GetAudioTask mGetAudioTask = null;

    private static final Type daoType = DAOSQLite.class;
    private static final String LOG_TAG = "MainActivity";

    public View mContentMain;
    private View mProgressView;
    private MediaService mMediaService = null;
    private ListView mMusicList;

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
        mMusicList = (ListView) findViewById(R.id.music_list);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //noinspection ConstantConditions
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

    @SuppressLint("ObsoleteSdkInt")
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
            //noinspection ConstantConditions
            DAOFactory.create(getApplicationContext(), daoType)
                    .saveUserActor(userActor);
            loadAudioList();
        }
        else {
            finish();
        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        if (v.getId() == R.id.music_list)
            inflater.inflate(R.menu.music_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.play:
                play_item(info.position);
                return true;
            case R.id.save_to_file:
                save_item(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void play_item(int position) {
        MusicItem item = (MusicItem)mMusicList.getAdapter().getItem(position);
        if (item.getStatus() == MusicItem.STATUS_PREPARING
                || mMediaService == null)
            return;

        if (! mMediaService.setMusicItem(item))
            DialogFactory.createAlertDialog(getString(R.string.error_title), "Ошибка загрузки аудиозаписи",
                    DialogFactory.BUTTONS_OK, MainActivity.this).show();
    }

    private void save_item(int position) {
        if (mMediaService == null || mMusicList == null)
            return;

        MusicItem item = (MusicItem)mMusicList.getAdapter().getItem(position);

        if (item.getDownloadingStatus() != MusicItem.NOT_DOWNLOADED)
            return;

        new DownloadFileFromURL(item)
                .setOnUIUpdateListener(this)
                .execute();
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
            //noinspection ConstantConditions
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

    @Override
    public void updateUI() {
        if (mMusicList != null)
            ((BrowseAdapter) mMusicList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void playNextItem(MusicItem item) {
        if (mMusicList == null)
            return;
        BrowseAdapter adapter = (BrowseAdapter) mMusicList.getAdapter();
        int position = adapter.getPosition(item) + 1;
        position = position == adapter.getCount() ? 0 : position;
        if (! mMediaService.setMusicItem(adapter.getItem(position)))
            DialogFactory.createAlertDialog(getString(R.string.error_title), "Ошибка воспроизведения аудиозаписи",
                    DialogFactory.BUTTONS_OK, MainActivity.this).show();
    }

    private class GetAudioTask extends AsyncTask<Void, Void, Boolean> {
        AudioList audioList;
        GetAudioTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (userActor == null)
                return false;
            try {
                audioList = new VkApi().audio.get(userActor.getUserID(), userActor.getAccessToken());
            } catch (ClientException e) {
                Log.v(LOG_TAG, e.getMessage());
                return false;
            } catch (ApiException e) {
                Log.v(LOG_TAG, e.toString());
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mGetAudioTask = null;
            showProgress(false);

            if (success) {
                // присваиваем адаптер списку
                final BrowseAdapter adapter = new BrowseAdapter(MainActivity.this,
                        MusicItemFactory.getMusicItemsList(audioList));
                mMusicList.setAdapter(adapter);
                registerForContextMenu(mMusicList);
                mMediaService = new MediaService((AudioManager)getSystemService(Context.AUDIO_SERVICE));
                mMediaService.setOnUIUpdateListener(MainActivity.this);
                mMediaService.setOnPlayNextItem(MainActivity.this);
                // устанавливем обработчик нажатия
                mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        play_item(position);
                    }
                });
            } else {
                DialogFactory.createAlertDialog(getString(R.string.error_title), "Ошибка загрузки аудиозаписей",
                        DialogFactory.BUTTONS_OK, MainActivity.this).show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mGetAudioTask = null;
            showProgress(false);
        }
    }
}
