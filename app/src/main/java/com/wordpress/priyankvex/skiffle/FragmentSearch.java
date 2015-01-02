package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by priyank on 23/12/14.
 * Class to provide search UI
 */
public class FragmentSearch extends Fragment {
    
    EditText searchField;
    Button btn_search;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    Activity activity;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentSearch newInstance(int sectionNumber) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSearch() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchField = (EditText)rootView.findViewById(R.id.field_search);
        btn_search = (Button)rootView.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchField.getText().toString();
                if( searchQuery.equals("")){
                    Toast.makeText(activity, "Whoa! What about entering some search terms?", Toast.LENGTH_LONG).show();
                }
                else{
                    Bundle b = new Bundle();
                    searchQuery = searchQuery.replaceAll(" ", "+");
                    String url = "https://itunes.apple.com/search?term=" + searchQuery;
                    b.putString("url", url);
                    Intent i = new Intent(activity, ActivitySearchResults.class);
                    i.putExtras(b);
                    startActivity(i);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        this.activity = activity;
    }
}