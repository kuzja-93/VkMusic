package site.kuzja.vkmusic.ui;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import site.kuzja.vkmusic.Media.MusicItem;
import site.kuzja.vkmusic.R;

/**
 * Адаптер списока аудиозаписей для listView
 */


class BrowseAdapter extends ArrayAdapter<MusicItem> {
    private static ColorStateList sColorStatePlaying;
    private static ColorStateList sColorStateNotPlaying;

    BrowseAdapter(Activity context, List<MusicItem> list) {
        super(context, R.layout.music_item_layout, list);
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
        MusicItem item = getItem(position);
        if (item == null)
            return convertView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.music_item_layout, parent, false);
            holder = new ViewHolder();
            holder.mArtistView = (TextView) convertView.findViewById(R.id.artist);
            holder.mTitleView = (TextView) convertView.findViewById(R.id.title);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.play_eq);
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.mDownloadingProgress = (ProgressBar) convertView.findViewById(R.id.downloadProgressBar);
            holder.mDownloadImage = (ImageView) convertView.findViewById(R.id.downloadImage);

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
            case MusicItem.STATUS_STOPPED:
                drawable = ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_play_arrow_black_36dp);
                holder.mImageView.setImageTintList(sColorStateNotPlaying);
                break;
            case MusicItem.STATUS_PLAYING:
                drawable = ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_pause_black_36dp);
                break;
            case MusicItem.STATUS_PAUSED:
                drawable = ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_play_arrow_black_36dp);
                break;
            case MusicItem.STATUS_PREPARING:
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
        switch (item.getDownloadingStatus()) {
            case MusicItem.DOWNLOADING:
                holder.mDownloadingProgress.setVisibility(View.VISIBLE);
                holder.mDownloadingProgress.setProgress(item.getDownloadingProgress());
                holder.mDownloadImage.setImageTintList(sColorStateNotPlaying);
                holder.mDownloadImage.setVisibility(View.VISIBLE);
                break;
            case MusicItem.DOWNLOADED:
                holder.mDownloadingProgress.setVisibility(View.GONE);
                holder.mDownloadImage.setImageTintList(sColorStatePlaying);
                holder.mDownloadImage.setVisibility(View.VISIBLE);
                break;
            case MusicItem.NOT_DOWNLOADED:
                holder.mDownloadingProgress.setVisibility(View.GONE);
                holder.mDownloadImage.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

}