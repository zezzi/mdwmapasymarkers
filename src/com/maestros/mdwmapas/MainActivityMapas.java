package com.maestros.mdwmapas;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class MainActivityMapas extends Activity implements  OnMenuItemClickListener{
	
	public static final String APPTAG = "MDWRestaurantes";
	private GoogleMap map;
	Location mCurrentLocation;
	private HashMap<Marker, Lugares> restauranteMarkerMap;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_mapas); 
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map!=null){
        	/* MAP_TYPE_NORMAL, MAP_TYPE_HYBRID, MAP_TYPE_SATELLITE, MAP_NONE, MAP_TERRAIN*/
        	 map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
             setUpRestaurantes();
             moveMapToMyLocation();
          } 
        map.setInfoWindowAdapter(new CustomInfoWindow(getLayoutInflater()));
    }

    
    private void moveMapToMyLocation() {
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	Criteria crit = new Criteria();
    	Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(crit, false));
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),loc.getLongitude()), 35)); 
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        Marker myLocation = map.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(),loc.getLongitude())).title("Mi Posicion").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.logomdw)).draggable(true));
        Lugares actual = new Lugares(new LatLng(loc.getLatitude(),loc.getLongitude()),"Yo","Mi posicion actual","LI","Mi posicion");
        restauranteMarkerMap.put(myLocation,actual);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_mapas, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_licencia:
            	Log.d("menu"," menu-licencia");
            	Intent nextScreen2 = new Intent(MainActivityMapas.this, Contactenos.class);
                startActivityForResult(nextScreen2, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }  

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return false;
        }
    }
    
    private void setUpRestaurantes() {
    	  List <Lugares> restaurantes_lista=new ArrayList<Lugares>();
    	  Lugares restaurante1 = new Lugares(new LatLng(18.007939,-98.884911),"Restaurante Chino Juan","Restaurante de Comida Rapida","FF","43 ave 56-89 zona 18");
    	  Lugares restaurante2 = new Lugares(new LatLng(19.340121,-100.598778),"Restaurante Italiano Alfredo","Restaurante de Comida italiana","IT","7ma Calle Poniente 34-89 ");
    	  Lugares restaurante3 = new Lugares(new LatLng(21.236085, -100.466942),"Restaurante Thai Chin","Restaurante de Comida Thai","HC","17sth Street 34-56 zona 21");
    	  Lugares restaurante4 = new Lugares(new LatLng(18.800187,-103.499168),"Restaurante de Comida Mexicana","Restaurante de Comida Casera","MX","1ave 94-28 San Juan");

    	  restaurantes_lista.add(restaurante1);
    	  restaurantes_lista.add(restaurante2);
    	  restaurantes_lista.add(restaurante3);
    	  restaurantes_lista.add(restaurante4);
    	  restauranteMarkerMap = new HashMap<Marker, Lugares>();
    	  for (Lugares temp : restaurantes_lista) {
    		  Marker marker_res = map.addMarker(new MarkerOptions()
              					.position(temp.getLatLng())
              					.title(temp.getTitle())
              					.snippet(temp.getDesc()+"  "+temp.getCode())
              					.icon(BitmapDescriptorFactory
              					.fromResource(R.drawable.logomdw)));
    		  CircleOptions circleOptions = new CircleOptions()
    		    							.center(temp.getLatLng())
    		    							.radius(1000).fillColor(Color.BLUE);
    		  Circle circle = map.addCircle(circleOptions);
    		  restauranteMarkerMap.put(marker_res, temp);
    	  }
    	  map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
              @Override
              public void onInfoWindowClick(Marker marker) {
                  Log.d("markerdetail", marker.getTitle()); 
                  Lugares lugar_presionado=restauranteMarkerMap.get(marker);
                  Intent nextScreen = new Intent(MainActivityMapas.this, DetailActivity.class);
                  nextScreen.putExtra("lugar_title",lugar_presionado.getTitle());
                  nextScreen.putExtra("lugar_desc",lugar_presionado.getDesc());
                  nextScreen.putExtra("lugar_address",lugar_presionado.getAddress());
                  nextScreen.putExtra("lugar_code",lugar_presionado.getCode());
                  startActivityForResult(nextScreen, 0);
              }
          });

    } 
}
