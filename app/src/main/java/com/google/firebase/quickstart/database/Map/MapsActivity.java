package com.google.firebase.quickstart.database.Map;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.quickstart.database.NewPostActivity;
import com.google.firebase.quickstart.database.R;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,ActivityCompat.OnRequestPermissionsResultCallback,GoogleMap.OnMarkerClickListener {


/*  지도를 띄우고 자기 위치를 찾은 후 마커로 찍고 좌표값을 DB에 저장한다.
     2017_09_19 이재인 MapActivity 추가
     2017_09_19 이재인 ThirdFragment 에서 위치 정보값을 받아와서 DB에 저장완료함

 */
    private Button searchBtn;
//    private EditText searchEdit;
    LocationManager locationManager;
    private  String provider;
    private  static  final  int LOCATION_PERMISSION_REQUEST_CODE=1;
    private  boolean mPermissionDenied = false;
    public static double lon;
    public static double lat;
    private GoogleMap mMap;
    String TAG = getClass().getSimpleName();
    private static final int LAUNCHED_ACTIVITY =1;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        enableMyLocation();//내 위치 찾기s
        //주소 찾기 버튼
        searchBtn = (Button)findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch();
            }
        });



    }//onCreate End



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setOnMarkerClickListener(this);

        enableMyLocation();

       mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
           @Override
           public void onMapClick(LatLng latLng) {
               MarkerOptions markerOptions = new MarkerOptions();

               markerOptions.position(latLng);

               markerOptions.title(latLng.latitude+":"+latLng.longitude);
               //마커 이미지
               markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));

               mMap.clear();

               mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

               mMap.addMarker(markerOptions);

                /*
                마커로 찍은 좌표값을 alertdialog 확인 버튼이 눌리면  마커 찍은 좌표값을 db에 저장한다.

                 */
               lat = latLng.latitude;
               lon= latLng.longitude;

               System.out.println(lat);



               // 위치 정보값 저장했고
           }
       });
    }







    public void onSearch() {//검색기능
        /*
        2017_09-19 이재인 만약 searchEdit에 값이 들어오지 않았다면 실행 x  ->  기능을 추가해야함
         */
        EditText searchEdit = (EditText) findViewById(R.id.SearchLoc);
        String stSearch = searchEdit.getText().toString();

        if (stSearch.getBytes().length<=0){
            //글을 쓰지 않았다면
            Toast.makeText(this,"장소를 입력해주세요",Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(this,MapsActivity.class);
            startActivity(intent);
            finish();

            }else{

            String location = searchEdit.getText().toString();
            List<Address> addressList = null;

            if (location != null || location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


            }


        }



    }

/*
    2017_09_19 이재인 권한 추가
 */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }
            // 2017_09_19 이재인 마커 클릭 시 이벤트
            //sharedPreference로  ThirdFragment 로 정보 전달

            @Override
            public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(MapsActivity.this,"marker clicked",Toast.LENGTH_LONG).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog.Builder builder1 = builder.setTitle("위치지정")
                        .setMessage("위도:" + lat + "\n" + "경도" + lon + "\n" + "위치를 지정하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //확인 버튼 누를 시 이벤트 처리
                                //확인 눌렀을 때 lat , lon 값을 sharedpreference에 저장해서 ThirdFragment로 보내서 같이 DB에 저장
                                Toast.makeText(MapsActivity.this, "좌표값" + lat, Toast.LENGTH_SHORT).show();
                                Toast.makeText(MapsActivity.this, "좌표값" + lon, Toast.LENGTH_SHORT).show();

                                    /*
                                    2017_09_19 이재인 DB에 들어갈때 String 으로 변환되서 뒤에 값이 조금 짤린다 -> 수정할 것
                                    2017_09_19 이재인 위치 등록 시 다시 글 쓰는 페이지로 넘어가는 것 해야함

                                    저장
                                     */
                                SharedPreferences pref = getSharedPreferences("GPS",Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putFloat("lon",(float)lon);
                                editor.putFloat("lat",(float)lat);
                                editor.commit();




                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //취소 버튼 클릭 시  설정
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();






                return false;
            }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }



}
