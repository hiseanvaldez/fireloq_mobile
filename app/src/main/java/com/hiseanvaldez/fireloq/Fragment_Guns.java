package com.hiseanvaldez.fireloq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Guns extends Fragment {
    private View view;

    FloatingActionButton addGun;

    public Fragment_Guns() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guns, container, false);

        addGun = view.findViewById(R.id.fb_addGun);
        addGun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_AddGun dialog_addGun = new Dialog_AddGun();
                dialog_addGun.show(getFragmentManager(), "Dialog_AddGun");
            }
        });
        return view;
    }
}
