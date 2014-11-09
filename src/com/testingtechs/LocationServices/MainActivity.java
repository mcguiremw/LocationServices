package com.testingtechs.LocationServices;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//Libraries pulled from Maven's Central Repository
import org.apache.commons.lang3.ArrayUtils;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;

/**
 * Created by matthewmcguire on 11/5/14.
 */
public class MainActivity extends Activity implements LocationListener {

    /* Bundle to save state of app */
    public final Bundle saveState = new Bundle();
    private final String LAT_LONG_ONE = "First Location";
    private final String LAT_LONG_TWO = "Second Location";
    private final String DISTANCE = "Calculated Distance";


    /* Make determining saved state easier to keep track of*/
    private final String SAVED_STATE = "Saved State";

    /* Globals to use for grabbing lat longs */
    private LocationManager locationManager;

    private Double latitude, longitude;

    /**
     * Overridden onCreate.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState != null) {
            Bundle bun = savedInstanceState.getBundle(SAVED_STATE);

            try {//Try to restore the first location text if it was saved
                TextView latLon = (TextView) findViewById(R.id.first_latLong);
                double[] latLongArray = bun.getDoubleArray(LAT_LONG_ONE);
                latLon.setText(latLongArray[0] + "\n" + latLongArray[1]);
                //Disable the button if the location is set already
                (findViewById(R.id.first_loc_button)).setEnabled(false);
            } catch (Exception e) {
                Log.d("Reloading Saved State",
                        "Exception when reloading saved state: " + e);
            }

            try {//Try to restore the second location text if it was saved
                TextView latLon = (TextView) findViewById(R.id.sec_latLong);
                double[] latLongArray = bun.getDoubleArray(LAT_LONG_TWO);
                latLon.setText(latLongArray[0] + "\n" + latLongArray[1]);
                //Disable the button if the location is set already
                (findViewById(R.id.second_loc_button)).setEnabled(false);
            } catch (Exception e) {
                Log.d("Reloading Saved State",
                        "Exception when reloading saved state: " + e);
            }
            ((TextView)findViewById(R.id.distance))
                    .setText(bun.getString(DISTANCE));
        }
    }

    /**
     * Ensure GPS sensor is loaded and running.
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!enableGps()) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }

    /**
     * Add reset button to the menu bar.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * When the reset button from the menu bar is selected reset the app.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.reset:
                onCreate(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            saveState.putDoubleArray(LAT_LONG_ONE,
                    ArrayUtils.toPrimitive(saveLatLongs(
                            findViewById(R.id.first_latLong))));
        } catch (NullPointerException npe) {
            Log.d("Saving Lat Longs",
                    "Lat Long must not have been set, " + npe);
        }

        try {
            saveState.putDoubleArray(LAT_LONG_TWO,
                    ArrayUtils.toPrimitive(saveLatLongs(
                            findViewById(R.id.sec_latLong))));
            savedInstanceState.putBundle(SAVED_STATE, saveState);
        } catch (NullPointerException npe) {
            Log.d("Saving Lat Longs",
                    "Lat Long must not have been set, " + npe);
        }

        saveState.putString(DISTANCE,
                (String) ((TextView) findViewById(R.id.distance)).getText());
    }

    /**
     * Handle all logic when a location is request from the user.
     *
     * @param view
     */
    public void onChooseLocationClicked(View view) {
        String gpsProvider = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(gpsProvider)) {
            TextView textView;
            Double[] latLong = getLatLongCoordinates();
            if (view.getId() == R.id.first_loc_button) {
                textView = (TextView) findViewById(R.id.first_latLong);
                textView.setText(latLong[0] + "\n" + latLong[1]);
            } else if (view.getId() == R.id.second_loc_button) {
                textView = (TextView) findViewById(R.id.sec_latLong);
                textView.setText(latLong[0] + "\n" + latLong[1]);
            } else {
                Log.e("Unknown View ID",
                        "onChooseLocationClicked received bad ID ");
            }
            calculateDistance();

            //Once location set it cannot be changed until app is reset
            view.setEnabled(false);
        } else {
            Log.e("GPS Provider", "GPS not available");
        }
    }

    /**
     * If both coordinates have been set calculate the distance between the two.
     */
    private double calculateDistance() {
        String firstLoc = (String) (((TextView)
                findViewById(R.id.first_latLong)).getText());
        String secLoc = (String) (((TextView)
                findViewById(R.id.sec_latLong)).getText());
        double distance = 0.0;
        try {
            String[] firstLocParts = firstLoc.split("\n");
            LatLng point1 = new LatLng(Double.parseDouble(firstLocParts[0]),
                    Double.parseDouble(firstLocParts[1]));

            String[] secLocParts = secLoc.split("\n");
            LatLng point2 = new LatLng(Double.parseDouble(secLocParts[0]),
                    Double.parseDouble(secLocParts[1]));

            distance = LatLngTool.distance(point1, point2, LengthUnit.METER);
        } catch (Exception e) {
            Log.d("Calculating Distance",
                    "One of the points has not been set: " + e);
        }
        ((TextView)findViewById(R.id.distance)).setText("Distance: "
                + distance + " meters");
        return distance;
    }

    /**
     * Method that will set new Lat Longs from Location Listener and return them
     * to be displayed properly when the user chooses to update location.
     *
     * @return
     */
    private Double[] getLatLongCoordinates() {
        return new Double[]{this.latitude, this.longitude};
    }

    /**
     * Place lat longs into Double[] so that may be saved in a bundle to be
     * restored if the app is paused.
     *
     * @param view
     * @return
     */
    private Double[] saveLatLongs(View view) {
        TextView textView = (TextView) findViewById(view.getId());
        String text = (String) textView.getText();
        String[] textByParts = text.split("\n");
        Double[] latLongs = new Double[]{};
        try {
            latLongs = new Double[]{Double.parseDouble(textByParts[0]),
                    Double.parseDouble(textByParts[1])};
        } catch (Exception e) {
            Log.d("Saving Lat Longs",
                    "Lat Long must not have been set, " + e);
        }
        return latLongs;
    }

    /**
     * Determine if GPS is available, if not return false so that the user can
     * be directed to the settings to turn it on.
     *
     * @return
     */
    private boolean enableGps() {
        locationManager = (LocationManager)
                this.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /* Overridden methods for LocationListener */
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
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
    ////////////////////////////////////////////////////////////////////////////
}
