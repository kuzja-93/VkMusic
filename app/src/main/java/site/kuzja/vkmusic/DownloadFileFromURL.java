package site.kuzja.vkmusic;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import site.kuzja.vkmusic.Media.MusicItem;
import site.kuzja.vkmusic.interfaces.OnProgressUpdateListener;
import site.kuzja.vkmusic.interfaces.OnUIUpdateListener;

/**
 * Ассинхронная задача для скачивания аудиозаписи
 */

public class DownloadFileFromURL extends AsyncTask<Void, Integer, Boolean> {
    private MusicItem item;
    private OnProgressUpdateListener onProgressUpdateListener;
    private static final String LOG_TAG = "DownloadFileFromURL";
    public DownloadFileFromURL(MusicItem item) {
        this.item = item;
    }

    public DownloadFileFromURL setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        this.onProgressUpdateListener = onProgressUpdateListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        item.setDownloadingStatus(MusicItem.DOWNLOADING);
        if (onProgressUpdateListener != null)
            onProgressUpdateListener.progressUpdate();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        int count;
        try {
            URL url = new URL(item.getUrl());
            URLConnection connection = url.openConnection();
            connection.connect();

            int lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            File parent = new File(new File(item.getFileName()).getParent());
            if(!parent.mkdirs())
                Log.v(LOG_TAG, "Путь не создан");
            OutputStream output = new FileOutputStream(item.getFileName());

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }

            output.flush();

            output.close();
            input.close();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return false;
        }

        return true;
    }

    protected void onProgressUpdate(Integer... progress) {
        item.setDownloadingProgress(progress[0]);
        if (onProgressUpdateListener != null)
            onProgressUpdateListener.progressUpdate();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Log.i(LOG_TAG, String.format("Загружен файл: %s",item.getFileName()));
            item.setDownloadingStatus(MusicItem.DOWNLOADED);
        } else {
            Log.i(LOG_TAG, String.format("Незагружен файл: %s",item.getFileName()));
            item.setDownloadingStatus(MusicItem.NOT_DOWNLOADED);
        }
        if (onProgressUpdateListener != null) {
            onProgressUpdateListener.progressUpdate();
        }
    }

}