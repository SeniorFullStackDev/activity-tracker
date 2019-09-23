package com.example.m4lv2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.m4lv2.MainActivity;
import com.example.m4lv2.R;
import com.example.m4lv2.utils.UtilsPreference;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextTreckCost;


    private UtilsPreference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.buttonSave).setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTreckCost = findViewById(R.id.editTextTreckCost);

        readSetting();
    }

    private void readSetting(){
        pref = new UtilsPreference(this);
        editTextEmail.setText(pref.getSettingEmail());
        editTextTreckCost.setText(pref.getSettingTreckCost()+"");

    }
    private void saveSetting(){
        pref.setSettingEmail(editTextEmail.getText().toString());
        pref.setSettingTreckCost(Float.parseFloat(editTextTreckCost.getText().toString()));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSave){
            saveSetting();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.mybutton:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pref.getSettingHelpInfo()));
                startActivity(browserIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
