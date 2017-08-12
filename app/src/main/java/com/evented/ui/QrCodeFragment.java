package com.evented.ui;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;

/**
 * Created by yaaminu on 8/12/17.
 */

public class QrCodeFragment extends BaseFragment implements Camera.PictureCallback {

    private static final String TAG = "QrCodeFragment";

    VerifyTicketCallbacks callbacks;
    private Camera camera;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = ((VerifyTicketCallbacks) context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera.setDisplayOrientation(0);
        } catch (Exception e) {
            PLog.d(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.camera_preview);
        CameraPreview cameraPreview = new CameraPreview(getContext(), camera);
        cameraPreview.setOnTouchListener(onTouchListener);
        frameLayout.addView(cameraPreview, 0);
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (camera != null) {
                camera.takePicture(null, null, QrCodeFragment.this);
                camera.stopPreview();
                camera.stopPreview();
            }
            return true;
        }
    };


    @Override
    public void onDestroy() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_verify_qrcode;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        // TODO: 8/12/17 process image here
    }
}
