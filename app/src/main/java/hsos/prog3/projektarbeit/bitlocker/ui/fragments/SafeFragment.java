package hsos.prog3.projektarbeit.bitlocker.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper;
import hsos.prog3.projektarbeit.bitlocker.logik.RecyclerAdapter;
import hsos.prog3.projektarbeit.bitlocker.ui.MainMenuHolder;
import hsos.prog3.projektarbeit.bitlocker.ui.PasswordCreationScreen;
import hsos.prog3.projektarbeit.bitlocker.ui.PasswordViewScreen;

/**
 * The SafeFragment class is held by the MainMenuHolder Activity and is the logical user interface
 * when clicking on the Safe menu tab. This fragment is responsible for holding a openPasswordCreationBtn
 * as well as displaying all the users created websites through a recyclerView.
 *
 * @author Andreas Morasch
 * @see MainMenuHolder
 */

public class SafeFragment extends Fragment {

    private TextView nothingToShowTxt;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> websiteList;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;

    /**
     * onCreate method for the SafeFragment containing an onClick method for the openPasswordCreationBtn
     * leading to the PasswordCreationScreen activity.
     *
     * @param inflater           is used to instantiate the contents of layout XML files into their corresponding View objects
     * @param container          required parameter for the inflater
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState
     * @return a view with the corresponding contents of the fragment
     * @see PasswordCreationScreen
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tresor, container, false);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        this.websiteList = new ArrayList<>();
        this.databaseHelper = new DatabaseHelper(getContext());
        this.nothingToShowTxt = view.findViewById(R.id.nothingToShowTextView);

        setWebsiteInfo();
        setAdapter();

        Button buttonOpenPasswordCreationActivity = (Button) view.findViewById(R.id.openPasswordCreationBtn);
        buttonOpenPasswordCreationActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPasswordCreationActivity();
            }
        });
        return view;
    }

    /**
     * This method will call the setOnClickListener() method to initialize the listener variable with a
     * new RecyclerAdapter.RecyclerViewClickListener and holding an onClick method reacting to the users
     * clicks on the recyclerView. Moreover it will create an instance of the RecyclerAdapter with the
     * corresponding parameters to display the recyclerView content on the fragments layout.
     *
     * @see RecyclerAdapter
     */

    private void setAdapter() {
        setOnClickListener();
        RecyclerAdapter adapter = new RecyclerAdapter(websiteList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    /**
     * This method will initialize the listener variable with a
     * new RecyclerAdapter.RecyclerViewClickListener and holding an onClick method reacting to the users
     * clicks on the recyclerView. When clicked on a recyclerView object it will start a new intent
     * which will start the PasswordViewScreen activity.
     *
     * @see PasswordViewScreen
     */

    private void setOnClickListener() {
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                MainMenuHolder.cancelTimer();
                Intent intent = new Intent(getActivity().getApplicationContext(), PasswordViewScreen.class);
                intent.putExtra("website", websiteList.get(position));
                intent.putExtra("decryptedPassword", getActivity().getIntent().getStringExtra("decryptedPassword"));
                startActivity(intent);
            }
        };
    }

    /**
     * This method will initialize the websiteList variable with all the website data from the
     * database. This method must be called before the setAdapter() method because the websiteList
     * cannot be empty! If the the database is empty (no passwords created yet), where will be a textView
     * displayed in the middle of the fragments layout saying that you haven't created any password yet.
     */

    private void setWebsiteInfo() {
        Cursor cursor = databaseHelper.getDataCursor();
        // Der erste Eintrag wird übersprungen, da es sich um das Masterpasswort handelt, welches nicht dargestellt wird.
        cursor.moveToPosition(0);
        while (cursor.moveToNext()) {
            websiteList.add(cursor.getString(0));
        }

        if (websiteList.isEmpty()) {
            nothingToShowTxt.setText(requireContext().getString(R.string.nothing_to_show));
        }
    }

    /**
     * This method is called by the openPasswordCreationBtn and will start the PasswordCreationScreen activity.
     */

    private void startPasswordCreationActivity() {
        MainMenuHolder.cancelTimer();
        Intent intent = new Intent(getActivity(), PasswordCreationScreen.class);
        intent.putExtra("decryptedPassword", getActivity().getIntent().getStringExtra("decryptedPassword"));
        startActivity(intent);
    }


}