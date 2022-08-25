package onealldigital.nizara.in.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.AddressAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Address;
import onealldigital.nizara.in.model.Area;
import onealldigital.nizara.in.model.City;
import onealldigital.nizara.in.model.filterDate;

public class AddressAddUpdateFragment extends Fragment implements OnMapReadyCallback {
    public static TextView tvCurrent;
    public static double latitude = 0.00, longitude = 0.00;
    public static Address address1;
    public static SupportMapFragment mapFragment;
    public static OnMapReadyCallback mapReadyCallback;
    View root;
    public static String pincodeId = "0", areaId = "0", cityId = "0";
    ArrayList<City> cityArrayList;
    ArrayList<Area> areaList;
    Spinner areaSpinner, citySpinner;
    Button btnsubmit;
    ProgressBar progressBar;
    CheckBox chIsDefault;
    RadioButton rdHome, rdOffice, rdOther;
    Session session;
    String isDefault = "0";
    TextView tvUpdate, edtName, edtMobile, edtAlternateMobile, edtAddress, edtLanmark, edtState, edtCounty, edtgst, edtpan;
    public static TextView edtPinCode;
    ScrollView scrollView;
    String name, mobile, alternateMobile, address2, landmark, pincode, state, country, isdefault, addressType, gst, pan;
    int position;
    Activity activity;
    int offset = 0;
    String For;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String User_name;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_address_add_update, container, false);
        activity = getActivity();
        setHasOptionsMenu(true);

        edtPinCode = root.findViewById(R.id.edtPinCode);
        areaSpinner = root.findViewById(R.id.areaSpinner);
        citySpinner = root.findViewById(R.id.citySpinner);
        edtName = root.findViewById(R.id.edtName);
        edtMobile = root.findViewById(R.id.edtMobile);
        edtAlternateMobile = root.findViewById(R.id.edtAlternateMobile);
        edtLanmark = root.findViewById(R.id.edtLanmark);
        edtAddress = root.findViewById(R.id.edtAddress);
        edtState = root.findViewById(R.id.edtState);
        edtCounty = root.findViewById(R.id.edtCountry);
        btnsubmit = root.findViewById(R.id.btnsubmit);
        scrollView = root.findViewById(R.id.scrollView);
        progressBar = root.findViewById(R.id.progressBar);
        chIsDefault = root.findViewById(R.id.chIsDefault);
        rdHome = root.findViewById(R.id.rdHome);
        rdOffice = root.findViewById(R.id.rdOffice);
        rdOther = root.findViewById(R.id.rdOther);
        tvCurrent = root.findViewById(R.id.tvCurrent);
        tvUpdate = root.findViewById(R.id.tvUpdate);


        edtpan = root.findViewById(R.id.edt_pan);
        edtgst = root.findViewById(R.id.edt_gst);

        session = new Session(activity);
        User_name=session.getData(Constant.NAME);



        edtName.setText(session.getData(Constant.NAME));
        edtAddress.setText(session.getData(Constant.ADDRESS));
        edtMobile.setText(session.getData(Constant.MOBILE));


//        edtgst.setText(session.getData(Constant.GST));
//        edtpan.setText(session.getData(Constant.PAN));




        pincodeId = session.getData(Constant.CITY_ID);
        areaId = session.getData(Constant.AREA_ID);



        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Bundle bundle = getArguments();
        assert bundle != null;
        For = bundle.getString("for");
        position = bundle.getInt("position");

        SetCitySpinnerData();

        address1 = new Address();
        if (For.equals("update")) {
            btnsubmit.setText(getString(R.string.update));
            address1 = (Address) bundle.getSerializable("model");
            pincodeId = address1.getPincode_id();
            areaId = address1.getArea_id();
            cityId = address1.getCity_id();

//            gst = address1.getGST();
//            pan = address1.getPAN();


            latitude = Double.parseDouble(address1.getLatitude());
            longitude = Double.parseDouble(address1.getLongitude());
            tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, getActivity()));
            mapFragment.getMapAsync(this);
            SetData();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            btnsubmit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            showKeyboard();
            edtAlternateMobile.requestFocus();
        }

        mapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull GoogleMap googleMap) {
                googleMap.clear();
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(getString(R.string.current_location)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
        };

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddUpdateAddress();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 110);
                } else {
                    Fragment fragment = new MapFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.FROM, "address");
                    bundle.putDouble("latitude", latitude);
                    bundle.putDouble("longitude", longitude);
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }

            }
        });

        chIsDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDefault.equalsIgnoreCase("0")) {
                    isDefault = "1";
                } else {
                    isDefault = "0";
                }
            }
        });

        return root;
    }

    void SetData() {


        final HashMap<String, String>[] hasmap = new HashMap[]{new HashMap<>()};
        myRef.child("Address").child(session.getData(Constant.NAME)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    hasmap[0] = (HashMap<String, String>) dataSnapshot.getValue();
                    edtgst.setText(hasmap[0].get("GST"));
                    edtpan.setText(hasmap[0].get("PAN"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        name = address1.getName();
        mobile = address1.getMobile();
        address2 = address1.getAddress();
        alternateMobile = address1.getAlternate_mobile();
        landmark = address1.getLandmark();
        pincode = address1.getPincode();
        state = address1.getState();
        country = address1.getCountry();
        isdefault = address1.getIs_default();
        addressType = address1.getType();
//        gst = address1.getGST();
//        pan = address1.getPAN();

        progressBar.setVisibility(View.VISIBLE);
        edtName.setText(name);
        edtMobile.setText(mobile);
        edtAlternateMobile.setText(alternateMobile);
        edtAddress.setText(address2);
        edtLanmark.setText(landmark);
        edtState.setText(state);


//        edtgst.setText(gst);
//        edtpan.setText(pan);


        edtCounty.setText(country);
        chIsDefault.setChecked(isdefault.equalsIgnoreCase("1"));

        if (addressType.equalsIgnoreCase("home")) {
            rdHome.setChecked(true);
        } else if (addressType.equalsIgnoreCase("office")) {
            rdOffice.setChecked(true);
        } else {
            rdOther.setChecked(true);
        }

        progressBar.setVisibility(View.GONE);

        btnsubmit.setVisibility(View.VISIBLE);
        btnsubmit.setVisibility(View.VISIBLE);

        showKeyboard();
        edtName.requestFocus();
    }

    void AddUpdateAddress() {


        Map<String, String > hasmap = new HashMap<>();
        hasmap.put("GST",edtgst.getText().toString().trim());




        String isDefault = chIsDefault.isChecked() ? "1" : "0";
        String type = rdHome.isChecked() ? "Home" : rdOffice.isChecked() ? "Office" : "Other";
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.requestFocus();
            edtName.setError("Please enter name!");
        } else if (edtMobile.getText().toString().trim().isEmpty()) {
            edtMobile.requestFocus();
            edtMobile.setError("Please enter mobile!");

        } else if (edtpan.getText().toString().trim().isEmpty()) {
            edtpan.requestFocus();
            edtpan.setError("Please enter PAN Details!");
        }else if (edtpan.getText().toString().trim().length() != 10) {
            edtpan.requestFocus();
            edtpan.setError("Please enter PAN Details Propley!");
        } else if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.requestFocus();
            edtAddress.setError("Please enter address!");
        } else if (edtLanmark.getText().toString().trim().isEmpty()) {
            edtLanmark.requestFocus();
            edtLanmark.setError("Please enter landmark!");
        } else if (edtState.getText().toString().trim().isEmpty()) {
            edtState.requestFocus();
            edtState.setError("Please enter state!");

        } else if (edtCounty.getText().toString().trim().isEmpty()) {
            edtCounty.requestFocus();
            edtCounty.setError("Please enter country");
        } else {
            Map<String, String> params = new HashMap<>();
            if (For.equalsIgnoreCase("add")) {
                params.put(Constant.ADD_ADDRESS, Constant.GetVal);
            } else if (For.equalsIgnoreCase("update")) {
                params.put(Constant.UPDATE_ADDRESS, Constant.GetVal);
                params.put(Constant.ID, address1.getId());
            }

            params.put(Constant.USER_ID, session.getData(Constant.ID));
            params.put(Constant.TYPE, type);

            hasmap.put("PAN",edtpan.getText().toString().trim());
            myRef.child("Address").child(User_name).child(User_name).setValue(hasmap);

//            params.put(Constant.GST, edtgst.getText().toString().trim());
//            params.put(Constant.PAN, edtpan.getText().toString().trim());


            params.put(Constant.NAME, edtName.getText().toString().trim());
            params.put(Constant.MOBILE, edtMobile.getText().toString().trim());
            params.put(Constant.ADDRESS, edtAddress.getText().toString().trim());
            params.put(Constant.LANDMARK, edtLanmark.getText().toString().trim());
            params.put(Constant.AREA_ID, areaId);
            params.put(Constant.CITY_ID, cityId);
            params.put(Constant.PINCODE_ID, pincodeId);
            params.put(Constant.STATE, edtState.getText().toString().trim());
            params.put(Constant.COUNTRY, edtCounty.getText().toString().trim());
            params.put(Constant.ALTERNATE_MOBILE, edtAlternateMobile.getText().toString().trim());
            params.put(Constant.COUNTRY_CODE, session.getData(Constant.COUNTRY_CODE));
            if (address1 != null && (address1.getLongitude() != null && address1.getLatitude() != null)) {
                params.put(Constant.LONGITUDE, address1.getLongitude());
                params.put(Constant.LATITUDE, address1.getLatitude());
            }
            params.put(Constant.IS_DEFAULT, isDefault);

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {

                            String msg;
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                offset = 0;
                                Gson g = new Gson();
                                Address address = g.fromJson(jsonObject.toString(), Address.class);

                                if (address.getIs_default().equals("1")) {
                                    for (int i = 0; i < AddressListFragment.addresses.size(); i++) {
                                        AddressListFragment.addresses.get(i).setIs_default("0");
                                    }
                                }

                                if (For.equalsIgnoreCase("add")) {
                                    msg = "Address added.";
                                    if (AddressListFragment.addressAdapter != null) {
                                        AddressListFragment.addresses.add(address);
                                    } else {
                                        AddressListFragment.addresses = new ArrayList<>();
                                        AddressListFragment.addresses.add(address);
                                        AddressListFragment.addressAdapter = new AddressAdapter(getContext(), getActivity(), AddressListFragment.addresses);
                                        AddressListFragment.recyclerView.setAdapter(AddressListFragment.addressAdapter);
                                        AddressListFragment.recyclerView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    AddressListFragment.addresses.set(position, address);
                                    msg = "Address updated.";
                                }

                                AddressListFragment.tvAlert.setVisibility(View.GONE);

                                if (AddressListFragment.addressAdapter != null) {
                                    AddressListFragment.addressAdapter.notifyDataSetChanged();
                                }

                                if (address.getIs_default().equals("1")) {
                                    Constant.selectedAddressId = address.getId();
                                } else {
                                    if (Constant.selectedAddressId.equals(address.getId()))
                                        Constant.selectedAddressId = "";
                                }
                                MainActivity.fm.popBackStack();
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, getActivity(), Constant.GET_ADDRESS_URL, params, true);
        }
    }

    void SetCitySpinnerData() {
        cityArrayList = new ArrayList<>();
        try {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_CITIES, Constant.GetVal);
            Activity activity = getActivity();
            if (activity != null) {
                ApiConfig.RequestToVolley((result, response) -> {
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            if (!objectbject.getBoolean(Constant.ERROR)) {

                                if (getContext() != null) {
                                    JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        City pinCode = new Gson().fromJson(jsonObject.toString(), City.class);
                                        cityArrayList.add(pinCode);
                                    }
                                    AddressAddUpdateFragment.CityAdapter cityAdapter = new AddressAddUpdateFragment.CityAdapter(activity, cityArrayList, citySpinner);
                                    citySpinner.setAdapter(cityAdapter);
                                    cityAdapter.setItem(cityId, areaId);
                                }
                            } else {
                                Toast.makeText(activity, activity.getString(R.string.blank_city_message), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity, Constant.GET_LOCATIONS_URL, params, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getActivity().getString(R.string.address);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeKeyboard();
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        double saveLatitude, saveLongitude;
        if (For.equals("update")) {
            btnsubmit.setText(getString(R.string.update));
            assert getArguments() != null;
            address1 = (Address) getArguments().getSerializable("model");
            pincodeId = address1.getPincode_id();
            areaId = address1.getArea_id();
            latitude = Double.parseDouble(address1.getLatitude());
            longitude = Double.parseDouble(address1.getLongitude());
        }
        if (latitude <= 0 || longitude <= 0) {
            saveLatitude = Double.parseDouble(session.getCoordinates(Constant.LATITUDE));
            saveLongitude = Double.parseDouble(session.getCoordinates(Constant.LONGITUDE));
        } else {
            saveLatitude = latitude;
            saveLongitude = longitude;
        }
        googleMap.clear();

        LatLng latLng = new LatLng(saveLatitude, saveLongitude);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }


    public class CityAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<City> cities;
        final LayoutInflater inflter;
        Spinner pinCodeSpinner;


        public CityAdapter(Context applicationContext, ArrayList<City> cities, Spinner pinCodeSpinner) {
            this.context = applicationContext;
            this.cities = cities;
            this.pinCodeSpinner = pinCodeSpinner;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setItem(String pinCodeId, String areaId) {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).getId().equals(pinCodeId)) {
                    citySpinner.setSelection(i);
                    areaList = cities.get(i).getAreas();
                    AddressAddUpdateFragment.AreaAdapter areaAdapter;
                    try {
                        areaAdapter = new AddressAddUpdateFragment.AreaAdapter(activity, areaList, areaSpinner);
                        areaSpinner.setAdapter(areaAdapter);
                        areaAdapter.setItem(areaId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, activity.getString(R.string.blank_area_message), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);

            City city = cities.get(position);
            measurement.setText(city.getCity_name());

            pinCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cityId = city.getId();
                    AddressAddUpdateFragment.AreaAdapter areaAdapter;
                    try {
                        areaAdapter = new AddressAddUpdateFragment.AreaAdapter(activity, city.getAreas(), areaSpinner);
                        areaSpinner.setAdapter(areaAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, activity.getString(R.string.blank_area_message), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    public static class AreaAdapter extends BaseAdapter {
        final Context context;
        final ArrayList<Area> areas;
        final LayoutInflater inflter;
        Spinner areaSpinner;

        public AreaAdapter(Context applicationContext, ArrayList<Area> areas, Spinner areaSpinner) {
            this.context = applicationContext;
            this.areas = areas;
            this.areaSpinner = areaSpinner;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return areas.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setItem(String areaId) {
            try {
                Thread.sleep(1000);
                if (areaId.equals("0")) {
                    areaSpinner.setSelection(0);
                } else {
                    for (int i = 0; i < areas.size(); i++) {
                        if (areas.get(i).getId().equals(areaId)) {
                            areaSpinner.setSelection(i);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);

            Area area = areas.get(position);
            measurement.setText(area.getArea_name());

            areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    areaId = area.getId();
                    pincodeId = area.getPincode_id();
                    edtPinCode.setText(area.getPincode());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }
}