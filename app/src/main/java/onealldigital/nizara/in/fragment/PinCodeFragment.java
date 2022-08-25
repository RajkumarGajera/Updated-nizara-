package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.PinCodeAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.PinCode;

public class PinCodeFragment extends DialogFragment {
    View root;
    RecyclerView recyclerView;
    ArrayList<PinCode> pinCodes;
    PinCodeAdapter pinCodeAdapter;
    NestedScrollView scrollView;
    TextView tvAlert;
    int total = 0;
    LinearLayoutManager linearLayoutManager;
    Activity activity;
    int offset = 0;
    Session session;
    boolean isLoadMore = false;
    EditText searchView;
    TextView tvSearch, tvPinCode;
    ProgressBar progressBar;
    String from;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_pincode, container, false);

        activity = getActivity();
        session = new Session(activity);

        assert getArguments() != null;
        from = getArguments().getString(Constant.FROM);

        searchView = root.findViewById(R.id.searchView);
        recyclerView = root.findViewById(R.id.recyclerView);
        scrollView = root.findViewById(R.id.scrollView);
        progressBar = root.findViewById(R.id.progressBar);
        tvAlert = root.findViewById(R.id.tvAlert);
        tvSearch = root.findViewById(R.id.tvSearch);
        tvPinCode = root.findViewById(R.id.tvPinCode);

        searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchView.getText().toString().trim().length() > 0) {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close, 0);
                } else {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (from.equals("home")) {
            tvPinCode.setVisibility(View.VISIBLE);
        } else {
            tvPinCode.setVisibility(View.GONE);
        }

        tvPinCode.setOnClickListener(v -> {
            session.setBoolean(Constant.GET_SELECTED_PINCODE, true);
            session.setData(Constant.GET_SELECTED_PINCODE_ID, "0");
            session.setData(Constant.GET_SELECTED_PINCODE_NAME, activity.getString(R.string.all));
            /*if (HomeFragment.tvLocation != null) {
                HomeFragment.tvLocation.setText(activity.getString(R.string.all));
            }*/
            /*if (CartFragment.tvLocation != null) {
                CartFragment.tvLocation.setText(activity.getString(R.string.all));
            }*/

//            if (from.equals("home")) {
//                HomeFragment.refreshListener.onRefresh();
//            } else {
//                CartFragment.refreshListener.onRefresh();
//            }

            MainActivity.pinCodeFragment.dismiss();
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData(searchView.getText().toString().trim());
            }
        });


        searchView.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (searchView.getText().toString().trim().length() > 0) {
                    if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        searchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                        searchView.setText("");
                        GetData("");
                    }
                    return true;
                }
            }
            return false;
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        GetData("");
        return root;
    }


    void GetData(String search) {
        pinCodes = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_PINCODES, Constant.GetVal);
        params.put(Constant.SEARCH, search);
        params.put(Constant.OFFSET, "" + offset);
        params.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            try {

                                total = Integer.parseInt(objectbject.getString(Constant.TOTAL));

                                JSONObject object = new JSONObject(response);
                                JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                Gson g = new Gson();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    if (jsonObject1 != null) {
                                        PinCode pinCode = g.fromJson(jsonObject1.toString(), PinCode.class);
                                        pinCodes.add(pinCode);
                                    } else {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (offset == 0) {
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                tvAlert.setVisibility(View.GONE);
                                pinCodeAdapter = new PinCodeAdapter(activity, pinCodes,from);
                                pinCodeAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(pinCodeAdapter);
                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            if (pinCodes.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == pinCodes.size() - 1) {
                                                        //bottom of list!
                                                        pinCodes.add(null);
                                                        pinCodeAdapter.notifyItemInserted(pinCodes.size() - 1);
                                                        offset += Constant.LOAD_ITEM_LIMIT + 20;

                                                        params.put(Constant.GET_PINCODES, Constant.GetVal);
                                                        params.put(Constant.SEARCH, search);
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        params.put(Constant.LIMIT, "" + (Constant.LOAD_ITEM_LIMIT + 20));

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {
                                                                if (result) {
                                                                    try {
                                                                        JSONObject jsonObject1 = new JSONObject(response);
                                                                        if (!jsonObject1.getBoolean(Constant.ERROR)) {
                                                                            pinCodes.remove(pinCodes.size() - 1);
                                                                            pinCodeAdapter.notifyItemRemoved(pinCodes.size());

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                                                                            Gson g = new Gson();
                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                                if (jsonObject != null) {
                                                                                    PinCode pinCode = g.fromJson(jsonObject1.toString(), PinCode.class);
                                                                                    pinCodes.add(pinCode);
                                                                                } else {
                                                                                    break;
                                                                                }
                                                                            }
                                                                            pinCodeAdapter.notifyDataSetChanged();
                                                                            pinCodeAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_LOCATIONS_URL, params, false);

                                                    }
                                                    isLoadMore = true;
                                                }

                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_LOCATIONS_URL, params, false);
    }
}