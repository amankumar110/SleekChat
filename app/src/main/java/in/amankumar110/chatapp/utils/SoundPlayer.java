package in.amankumar110.chatapp.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public static void playSound(Context context, int resId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);

        if (mediaPlayer != null) {
            // Free up resources after playback
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        }
    }

}
