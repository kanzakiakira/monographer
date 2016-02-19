package net.akira.monographer;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SitupsActivity extends AppCompatActivity implements OnClickListener, SensorEventListener {

    private TextView text_xyz;
    private TextView text_show;
    private SensorManager aSensorManager;
    private Sensor aSensor;
    private float gravity[] = new float[3];

    protected double count = 0;

    private int judge = 0;

    boolean test = false;

    int information = 0;

    private Button back_button;
    private Button start;
    private Button finish;
    private Button open_information;
    private Button close_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        text_xyz = (TextView)findViewById(R.id.TextView01);
        text_show = (TextView)findViewById(R.id.show);

        aSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        aSensorManager.registerListener(this, aSensor, aSensorManager.SENSOR_DELAY_NORMAL);

        back_button = (Button)findViewById(R.id.back_button);
        start = (Button)findViewById(R.id.start_button);
        finish = (Button)findViewById(R.id.finish_button);
        open_information = (Button)findViewById(R.id.open_information);
        close_information = (Button)findViewById(R.id.close_information);

        back_button.setOnClickListener(this);
        start.setOnClickListener(this);
        finish.setOnClickListener(this);
        open_information.setOnClickListener(this);
        close_information.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_situps, menu);
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
            case R.id.back_button:
                Intent intent = new Intent();
                intent.setClass(SitupsActivity.this, MainActivity.class);
                startActivity(intent);
                SitupsActivity.this.finish();
                break;
            case R.id.start_button:
                test = true;
                break;
            case R.id.finish_button:
                test = false;
                break;
            case R.id.open_information:
                information = 1;
                break;
            case R.id.close_information:
                information = 0;
                text_xyz.setText("");
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gravity[0] = event.values[0];
        gravity[1] = event.values[1];
        gravity[2] = event.values[2];
        if(information == 1){
            text_xyz.setText("X = " + gravity[0]
                    +"\nY = " + gravity[1]
                    +"\nZ = " + gravity[2]);
        }
        if(test == true){
            if(gravity[1] > 8.0 )
                judge = 1;
            if(judge == 1 && gravity[2] > 8.0){
                count = count + 1;
                judge = 0;
            }
        }
        text_show.setText("測試結果： " + count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
    /* 取消註冊SensorEventListener */
        aSensorManager.unregisterListener(this);
        Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        super.onPause();
    }

}
