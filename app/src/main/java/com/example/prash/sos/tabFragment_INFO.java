package com.example.prash.sos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class tabFragment_INFO extends Fragment implements View.OnClickListener {

    public Button stopButton;
    private static final int TIMER_LENGTH = 1;

    private TimerView mTimerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_tab, container, false);

        mTimerView = (TimerView) view.findViewById(R.id.timer);
        mTimerView.start(TIMER_LENGTH);
        mTimerView.setOnClickListener(this);

        stopButton = (Button) view.findViewById(R.id.stop_service_button);
        stopButton.setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop_service_button:
                MainActivity.stopServiceNotification();
                getActivity().stopService(new Intent(getContext(), SOSservice.class));
                break;
            case R.id.timer:
                getActivity().startService(new Intent(getContext(), SOSservice.class));
                break;
        }
    }
}
