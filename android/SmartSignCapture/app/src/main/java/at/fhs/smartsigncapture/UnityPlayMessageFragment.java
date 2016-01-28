package at.fhs.smartsigncapture;


import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnityPlayMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnityPlayMessageFragment extends DialogFragment {


    protected UnityPlayer mUnityPlayer;
    protected ViewGroup unityViewHolder;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UnityPlayMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UnityPlayMessageFragment newInstance() {
        UnityPlayMessageFragment fragment = new UnityPlayMessageFragment();

        return fragment;
    }

    public UnityPlayMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unity_play_message, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startUnity();
    }

    protected void startUnity() {
        getActivity().getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        mUnityPlayer = new UnityPlayer(this.getActivity());

        this.unityViewHolder = (FrameLayout) this.getView().findViewById(R.id.unityPlayerHolder);
        unityViewHolder.addView(this.mUnityPlayer.getView());
        mUnityPlayer.requestFocus();

        //this.getActivity().getWindow().takeSurface(null);
    }

    protected void stopUnity() {
        unityViewHolder.removeView(this.mUnityPlayer.getView());

        mUnityPlayer.quit();
    }



    public void onPlayerClosed() {
        Log.d("RES", "onPlayerClosed");
    }

    public void onEditorClosed(String jsonResult) {
        Log.d("RES", jsonResult);
    }

//    // Quit Unity
//    @Override
//    protected void onDestroy() {
//        mUnityPlayer.quit();
//        super.onDestroy();
//    }
//
//    // Pause Unity
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mUnityPlayer.pause();
//    }
//
//    // Resume Unity
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mUnityPlayer.resume();
//    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

//    // Notify Unity of the focus change.
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        mUnityPlayer.windowFocusChanged(hasFocus);
//    }

    // For some reason the multiple keyevent type is not supported by the ndk.
//    // Force event injection by overriding dispatchKeyEvent().
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
//            return mUnityPlayer.injectEvent(event);
//        return super.dispatchKeyEvent(event);
//    }

//    // Pass any events not handled by (unfocused) views straight to UnityPlayer
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        return mUnityPlayer.injectEvent(event);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return mUnityPlayer.injectEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mUnityPlayer.injectEvent(event);
//    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }
}
