package net.akira.monographer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button jump_button;
    private Button situps_button;
    private Button height_button;//add button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jump_button = (Button)findViewById(R.id.jump_button);
        situps_button = (Button)findViewById(R.id.situps_button);
        height_button = (Button)findViewById(R.id.height_button);

        jump_button.setOnClickListener(this);
        situps_button.setOnClickListener(this);
        height_button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.situps_button:
                Intent situps_intent = new Intent();
                situps_intent.setClass(MainActivity.this, SitupsActivity.class);
                startActivity(situps_intent);
                MainActivity.this.finish();
                break;
            case R.id.jump_button:
                Intent jump_intent = new Intent();
                jump_intent.setClass(MainActivity.this, JumpActivity.class);
                startActivity(jump_intent);
                MainActivity.this.finish();
                break;
            case R.id.height_button:
                Intent height_intent = new Intent();
                height_intent.setClass(MainActivity.this, HeightActivity.class);
                startActivity(height_intent);
                MainActivity.this.finish();
                break;
        }
    }
}
