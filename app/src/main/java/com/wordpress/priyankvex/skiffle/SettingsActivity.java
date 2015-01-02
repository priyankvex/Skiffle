package com.wordpress.priyankvex.skiffle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by priyank on 23/12/14.
 * Shows the UI for settings.
 * User can choose type songs as Hindi or English.
 * Charts will be displayed according to the preference of the user.
 */
public class SettingsActivity extends ActionBarActivity {

    RadioGroup group_language;
    RadioButton radio_english;
    RadioButton radio_hindi;

    Button btn_set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        group_language = (RadioGroup)findViewById(R.id.grp_language);
        radio_english = (RadioButton)findViewById(R.id.radio_english);
        radio_hindi = (RadioButton)findViewById(R.id.radio_hindi);

        btn_set = (Button)findViewById(R.id.btn_set);

        //Getting the suitable link as per the user preferences.
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "english");
        if(language == null || language.equals("english")){
            radio_english.setChecked(true);
            radio_hindi.setChecked(false);
        }
        else{
            radio_hindi.setChecked(true);
            radio_english.setChecked(false);
        }

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                String language;

                int lngID = group_language.getCheckedRadioButtonId();
                if (lngID == R.id.radio_english) {
                    language = "english";
                } else {
                    language = "hindi";
                }

                editor.putString("language", language);
                editor.commit();
                finish();
            }
        });
    }

}
