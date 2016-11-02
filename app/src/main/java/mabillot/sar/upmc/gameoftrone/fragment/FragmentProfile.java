package mabillot.sar.upmc.gameoftrone.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mabillot.sar.upmc.gameoftrone.R;

/**
 * Created by paulo on 09/01/2016.
 */
public class FragmentProfile extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_profile, container, false);
        return view;
    }

}
