package apis.amap.com.move;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener{

    private MapView mapView;

    private AMap aMap;

    private Button mStartButton;
    private SmoothMoveMarker moveMarker;


    private static final int START_STATUS=0;

    private static final int MOVE_STATUS=1;

    private static final int PAUSE_STATUS=2;
    private static final int FINISH_STATUS=3;

    private int mMarkerStatus=START_STATUS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map);
        mStartButton= (Button) findViewById(R.id.move_start_button);
        mStartButton.setOnClickListener(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initMoveMarker();

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    private void initMoveMarker(){
        addPolylineInPlayGround();
        // 获取轨迹坐标点
        List<LatLng> points = readLatLngs();
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0 ; i < points.size(); i++) {
            b.include(points.get(i));
        }
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        moveMarker = new SmoothMoveMarker(aMap);
        // 设置滑动的图标
        moveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.car));

        /*
        //当移动Marker的当前位置不在轨迹起点，先从当前位置移动到轨迹上，再开始平滑移动
        // LatLng drivePoint = points.get(0);//设置小车当前位置，可以是任意点，这里直接设置为轨迹起点
        LatLng drivePoint = new LatLng(39.980521,116.351905);//设置小车当前位置，可以是任意点
        Pair<Integer, LatLng> pair = PointsUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());
        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);*/

        moveMarker.setPoints(points);//设置平滑移动的轨迹list
        moveMarker.setTotalDuration(40);//设置平滑移动的总时间

        aMap.setInfoWindowAdapter(infoWindowAdapter);
        moveMarker.setMoveListener(
                new SmoothMoveMarker.MoveListener() {
                    @Override
                    public void move(final double distance) {

                        Log.i("MY","distance:  "+distance);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (infoWindowLayout != null && title != null && moveMarker.getMarker().isInfoWindowShown()) {
                                    title.setText("距离终点还有： " + (int) distance + "米");
                                }
                                if(distance == 0){
                                    moveMarker.getMarker().hideInfoWindow();
                                    mMarkerStatus=FINISH_STATUS;
                                    mStartButton.setText("开始");


                                }
                            }
                        });
                    }
                });
        moveMarker.getMarker().showInfoWindow();
    }


    public void move(View view) {

        moveMarker.startSmoothMove();
    }

    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        @Override
        public View getInfoContents(Marker marker) {


            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(MainActivity.this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(MainActivity.this);
            snippet = new TextView(MainActivity.this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    private void addPolylineInPlayGround() {
        List<LatLng> list = readLatLngs();
        List<Integer> colorList = new ArrayList<Integer>();

        aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)) //setCustomTextureList(bitmapDescriptors)
                .addAll(list)
                .useGradient(true)
                .width(18));
    }

    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < len; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }

    public double[] coords = PointActivity.GetPoint();
    public int len = PointActivity.GetPointLen();

    @Override
    public void onClick(View v) {
        if (mMarkerStatus==START_STATUS){
            moveMarker.startSmoothMove();
            mMarkerStatus=MOVE_STATUS;
            mStartButton.setText("暂停");
        }
       else if (mMarkerStatus==MOVE_STATUS){
            moveMarker.stopMove();
            mMarkerStatus=PAUSE_STATUS;
            mStartButton.setText("继续");
        }
        else if (mMarkerStatus==PAUSE_STATUS){
           moveMarker.startSmoothMove();
           mMarkerStatus=MOVE_STATUS;
            mStartButton.setText("暂停");
        }
        else if (mMarkerStatus==FINISH_STATUS){
            moveMarker.setPosition(new LatLng( coords[1],coords[0]));
            List<LatLng> points = readLatLngs();
            moveMarker.setPoints(points);
            moveMarker.getMarker().showInfoWindow();
            moveMarker.startSmoothMove();

            mMarkerStatus=MOVE_STATUS;
            mStartButton.setText("暂停");

        }


    }
}
