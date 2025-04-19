package in.amankumar110.chatapp.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AnimationUtil {

    private static final int ANIMATION_DURATION = 300;

    public static void fadeInTogether(List<View> views, float initialValue, float finalValue) {
        List<ObjectAnimator> objectAnimators = new ArrayList<>();

        for (View view : views) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", initialValue,finalValue);
            objectAnimator.setDuration(ANIMATION_DURATION); // Set the duration of animation (300ms)
            objectAnimators.add(objectAnimator);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new ArrayList<>(objectAnimators)); // Play all animations together
        animatorSet.start(); // Start the animation
    }

    public static void fadeOutTogether(List<View> views, float initialValue, float finalValue) {
        List<ObjectAnimator> objectAnimators = new ArrayList<>();

        for (View view : views) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", initialValue,finalValue);
            objectAnimator.setDuration(ANIMATION_DURATION); // Set the duration of animation (300ms)
            objectAnimators.add(objectAnimator);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new ArrayList<>(objectAnimators)); // Play all animations together
        animatorSet.start(); // Start the animation
    }

    public static void fadeIn(View view,Runnable callback) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha",  1f);
        fadeIn.setDuration(ANIMATION_DURATION);

        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                callback.run();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });

        fadeIn.start();
    }

    public static void fadeOut(View view,Runnable callback) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha",  0f);
        fadeOut.setDuration(ANIMATION_DURATION);

        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                callback.run();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });

        fadeOut.start();
    }
}
