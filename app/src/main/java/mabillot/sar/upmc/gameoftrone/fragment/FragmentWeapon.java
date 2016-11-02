package mabillot.sar.upmc.gameoftrone.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import mabillot.sar.upmc.gameoftrone.MainActivity;
import mabillot.sar.upmc.gameoftrone.R;

/**
 * Created by paulo on 09/01/2016.
 */
public class FragmentWeapon extends ListFragment implements AdapterView.OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_weapon, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.armes, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.getArbitre().getJoueur().setArme(MainActivity.getArbitre().getArmes().get(position));
        Toast.makeText(getActivity(), "Vous avez équipé: " + MainActivity.getArbitre().getArmes().get(position).getNom(), Toast.LENGTH_SHORT).show();
    }
}
