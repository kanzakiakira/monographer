package net.akira.monographer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class JumpActivity extends FragmentActivity implements OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;   //定位管理器
    private ProgressDialog MyDialog;            //顯示GPS定位進度
    private String mLocationPrivider = "";        //GPS provider
    private Location mLocation;                 //GPS座標點
    private Button mButton01;
    private Button mButton02;
    private Button mButton03;
    private Button mButton04;
    private Button back_button;

    TextView tv_show_gps;   //顯示GPS座標
    TextView start_point;
    TextView finish_point;
    TextView show_distance;

    LatLng p1;  //出發點
    LatLng p2;  //結束點

    int test = 0;
    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();

        if (info == null) {
            new AlertDialog.Builder(JumpActivity.this)
                    .setTitle("系統訊息")
                    .setMessage("目前無法上網，所以無法使用系統！")
                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            findviews_nonet();
                        }
                    })
                    .show();
        }
        else {
            //對應xml畫面
            findviews();
            tv_show_gps = (TextView) findViewById(R.id.showgeo);
            show_distance = (TextView)findViewById(R.id.show_distance);
            start_point = (TextView)findViewById(R.id.start);
            finish_point = (TextView)findViewById(R.id.finish);
            createCancelProgressDialog("定位中", "定位中...請稍後", "取消");
            try {
                //如果沒有開啟GPS或WiFi
                mLocationManager = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                } else {
                    //到系統開啟GPS與WiFi服務的畫面
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                //如果沒有開啟GPS
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //Provider初始化
                getLocationPrivider();
                //設定GPS的Listener
                mLocationManager.requestLocationUpdates(mLocationPrivider, 1000,  0, mLocationListener);
                if (mLocation != null) {

                    p1 = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    p2 = p1;

                    tv_show_gps.setText(
                            "目前位置\n" + "緯度： " + mLocation.getLatitude()
                                         + "\n經度： " + mLocation.getLongitude()
                    );
                }
            } catch (Exception e) {
                new AlertDialog.Builder(JumpActivity.this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MyDialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationManager != null){
            mLocationManager.requestLocationUpdates(mLocationPrivider, 1000, 0, mLocationListener);
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
        super.onPause();
    }

    private void findviews_nonet() {
        mButton01 = (Button) findViewById(R.id.myButton1);   //開始記錄
        mButton02 = (Button) findViewById(R.id.myButton2);   //結束記錄
        mButton01.setVisibility(View.INVISIBLE);           //結束記錄
        mButton02.setVisibility(View.INVISIBLE);           //結束記錄
        mButton03 = (Button) findViewById(R.id.myButton3);  //我的路徑
        back_button = (Button)findViewById(R.id.back_button);
        // 設定onclick事件
        mButton03.setOnClickListener(this);
        back_button.setOnClickListener(this);
    }

    private void findviews(){
        mButton01 = (Button)findViewById(R.id.myButton1);   //開始記錄
        //mButton01.setVisibility(View.INVISIBLE);            //結束記錄
        mButton02 = (Button)findViewById(R.id.myButton2);   //結束記錄
        //mButton02.setVisibility(View.INVISIBLE);            //結束記錄
        mButton03 = (Button)findViewById(R.id.myButton3);   //我的路徑
        mButton04 = (Button)findViewById(R.id.myButton4);   //重新定位
        back_button = (Button)findViewById(R.id.back_button);
        //設定onClick事件
        mButton01.setOnClickListener(this);
        mButton02.setOnClickListener(this);
        mButton03.setOnClickListener(this);
        mButton04.setOnClickListener(this);
        back_button.setOnClickListener(this);
    }

    private void createCancelProgressDialog(String title, String message, String buttonText) {
        MyDialog = new ProgressDialog(this);
        MyDialog.setTitle(title);
        MyDialog.setMessage(message);
        MyDialog.setButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        MyDialog.show();    //顯示進度
    }

    //取得LocationProvider
    public void getLocationPrivider() {
        Criteria mCriteria01 = new Criteria();
        mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria01.setAltitudeRequired(false);  //需要高度
        mCriteria01.setBearingRequired(false);
        mCriteria01.setSpeedRequired(false);     //速度
        mCriteria01.setCostAllowed(true);
        mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
        mLocationPrivider = mLocationManager.getBestProvider(mCriteria01, true);
        mLocation = mLocationManager.getLastKnownLocation(mLocationPrivider);
    }
    //計算距離，單位是公尺
    private double gps2m(LatLng gp1,LatLng gp2) {
        double EARTH_RADIUS = 6378137.0;
        double radLat1 = (gp2.latitude * Math.PI / 180.0);
        double radLat2 = (gp1.latitude * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (gp2.longitude - gp1.longitude) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public String disformat(double num){
        NumberFormat formatter = new DecimalFormat("###.##");
        String s=formatter.format(num);
        return s;
    }

    public final LocationListener mLocationListener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            tv_show_gps.setText(
                    "目前位置\n" + "緯度：" + location.getLatitude()
                            + "\n經度：" + location.getLongitude()
            );
            MyDialog.dismiss(); //結束進度
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.myButton1:    //開始記錄
                distance=0; //總長度=0
                p1 = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                start_point.setText("起始點" + "\n緯度： " + p1.latitude + "\n經度：" + p1.longitude);
                //切換按鈕狀態
                break;
            case R.id.myButton2:    //結束記錄
                p2 = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                finish_point.setText("終止點" + "\n緯度： " + p2.latitude + "\n經度：" + p2.longitude);
                break;
            case R.id.myButton3:    //我的路徑
                distance = gps2m(p1, p2);
                show_distance.setText(
                        "測驗結果: " + disformat(100 * distance) +" cm"
                );
                break;
            case R.id.myButton4:    //重新定位
                test = 0;
                createCancelProgressDialog("定位中","定位中..請稍待！","取消");
                // Provider 初始化
                getLocationPrivider();
                // 設定GPS的Listener
                mLocationManager.requestLocationUpdates(mLocationPrivider, 1000, 0, mLocationListener);
                break;
            case R.id.back_button:
                Intent intent = new Intent();
                intent.setClass(JumpActivity.this, MainActivity.class);
                startActivity(intent);
                JumpActivity.this.finish();
                break;
        }
    }
}
