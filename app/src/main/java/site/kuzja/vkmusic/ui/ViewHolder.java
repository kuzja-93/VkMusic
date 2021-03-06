package site.kuzja.vkmusic.ui;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Holder для отображение элемента списка с аудиозаписями
 */

class ViewHolder {
    ImageView mImageView;
    TextView mArtistView;
    TextView mTitleView;
    ProgressBar mProgressBar;
    ProgressBar mDownloadingProgress;
    ImageView mDownloadImage;
}