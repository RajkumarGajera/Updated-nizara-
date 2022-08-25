package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.LoginActivity;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.CartAdapter;
import onealldigital.nizara.in.adapter.OfflineCartAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.DatabaseHelper;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Cart;
import onealldigital.nizara.in.model.OfflineCart;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CartFragment extends Fragment {
    public static LinearLayout lytempty;
    public static ConstraintLayout lytTotal;
    public static ArrayList<Cart> carts;
    public static ArrayList<OfflineCart> offlineCarts;
    public static HashMap<String, String> values;
    public static boolean isSoldOut = false;
    public static boolean isDeliverable = false;
    static TextView txttotalamount, txttotalitems, tvConfirmOrder;
    static CartAdapter cartAdapter;
    static OfflineCartAdapter offlineCartAdapter;
    static Activity activity;
    static Session session;
    static JSONObject objectbject;
    View root;
    RecyclerView cartrecycleview;
    NestedScrollView scrollView;
    double total;
    Button btnShowNow;
    DatabaseHelper databaseHelper;
    private ShimmerFrameLayout mShimmerViewContainer;
    private SwipeRefreshLayout swipeLayout;
//    public static TextView tvLocation, tvTitleLocation;
//    public static SwipeRefreshLayout.OnRefreshListener refreshListener;

    @SuppressLint("SetTextI18n")
    public static void SetData() {
        txttotalamount.setText("Grand Total : "+session.getData(Constant.CURRENCY) + ApiConfig.StringFormat(String.valueOf(Constant.FLOAT_TOTAL_AMOUNT)));
        txttotalitems.setText("Total Item : "+Constant.TOTAL_CART_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        values = new HashMap<>();
        activity = getActivity();
        session = new Session(getActivity());
        lytTotal = root.findViewById(R.id.lytTotal);
        lytempty = root.findViewById(R.id.lytempty);
        btnShowNow = root.findViewById(R.id.btnShowNow);
        txttotalamount = root.findViewById(R.id.txttotalamount);
        txttotalitems = root.findViewById(R.id.txttotalitems);
        scrollView = root.findViewById(R.id.scrollView);
        cartrecycleview = root.findViewById(R.id.cartrecycleview);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
//        tvLocation = root.findViewById(R.id.tvLocation);
//        tvTitleLocation = root.findViewById(R.id.tvTitleLocation);
        swipeLayout = root.findViewById(R.id.swipeLayout);

        databaseHelper = new DatabaseHelper(activity);

        setHasOptionsMenu(true);

        Constant.FLOAT_TOTAL_AMOUNT = 0.00;

//        tvLocation.setText(session.getData(Constant.GET_SELECTED_PINCODE_NAME));

        carts = new ArrayList<>();
        cartrecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (ApiConfig.isConnected(getActivity())) {
            GetSettings(activity);
        }

        swipeLayout.setOnRefreshListener(() -> {
            if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                getCartData();
            } else {
                GetOfflineCart();
            }
        });


        //Comment by Nabeel
        /*if (session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0") || session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("")) {
            MainActivity.pinCodeFragment = new PinCodeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "cart");
            MainActivity.pinCodeFragment.setArguments(bundle);
            MainActivity.pinCodeFragment.show(MainActivity.fm, null);
        }*/

        /*tvTitleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.pinCodeFragment = new PinCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "cart");
                MainActivity.pinCodeFragment.setArguments(bundle);
                MainActivity.pinCodeFragment.show(MainActivity.fm, null);
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.pinCodeFragment = new PinCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "cart");
                MainActivity.pinCodeFragment.setArguments(bundle);
                MainActivity.pinCodeFragment.show(MainActivity.fm, null);
            }
        });*/

        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isConnected(requireActivity())) {
                    if (!isSoldOut && !isDeliverable) {
                        if (Float.parseFloat(session.getData(Constant.min_order_amount)) <= Constant.FLOAT_TOTAL_AMOUNT) {
                            if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                                if (values.size() > 0) {
                                    ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
                                }
                                Constant.selectedAddressId = "";
                                Fragment fragment = new AddressListFragment();
                                final Bundle bundle = new Bundle();
                                bundle.putString(Constant.FROM, "process");
                                bundle.putDouble("total", Constant.FLOAT_TOTAL_AMOUNT);
                                fragment.setArguments(bundle);
                                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                            } else {
                                startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("fromto", "checkout").putExtra("total", Constant.FLOAT_TOTAL_AMOUNT).putExtra(Constant.FROM, "checkout"));
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.msg_minimum_order_amount) + session.getData(Constant.CURRENCY) + ApiConfig.StringFormat(session.getData(Constant.min_order_amount)), Toast.LENGTH_SHORT).show();
                        }
                    } else if (isDeliverable) {
                        Toast.makeText(activity, getString(R.string.msg_non_deliverable), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, getString(R.string.msg_sold_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fm.popBackStack();
            }
        });

        return root;
    }

    private void GetOfflineCart() {
        CartFragment.isDeliverable = false;
        offlineCarts = new ArrayList<>();
        cartrecycleview.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        if (databaseHelper.getTotalItemOfCart(activity) >= 1) {
            offlineCarts = new ArrayList<>();
            offlineCartAdapter = null;
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_VARIENTS_OFFLINE, Constant.GetVal);
            params.put(Constant.VARIANT_IDs, databaseHelper.getCartList().toString().replace("[", "").replace("]", "").replace("\"", ""));
            if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
                params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
            }

            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                session.setData(Constant.TOTAL, jsonObject.getString(Constant.TOTAL));

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                Gson g = new Gson();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    OfflineCart cart = g.fromJson(jsonObject1.toString(), OfflineCart.class);
                                    offlineCarts.add(cart);
                                }
                                offlineCartAdapter = new OfflineCartAdapter(getContext(), getActivity(), offlineCarts);
                                offlineCartAdapter.setHasStableIds(true);
                                cartrecycleview.setAdapter(offlineCartAdapter);
                                lytTotal.setVisibility(View.VISIBLE);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                cartrecycleview.setVisibility(View.VISIBLE);
                            } else {
                                cartrecycleview.setVisibility(View.GONE);
                                lytempty.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }, getActivity(), Constant.GET_PRODUCTS_URL, params, false);
        } else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            cartrecycleview.setVisibility(View.VISIBLE);
            cartrecycleview.setVisibility(View.GONE);
            lytempty.setVisibility(View.VISIBLE);
        }
    }

    public void GetSettings(final Activity activity) {
        Session session = new Session(activity);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIMEZONE, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);

                            session.setData(Constant.minimum_version_required, object.getString(Constant.minimum_version_required));
                            session.setData(Constant.is_version_system_on, object.getString(Constant.is_version_system_on));

                            session.setData(Constant.CURRENCY, object.getString(Constant.CURRENCY));

                            session.setData(Constant.min_order_amount, object.getString(Constant.min_order_amount));
                            session.setData(Constant.max_cart_items_count, object.getString(Constant.max_cart_items_count));
                            session.setData(Constant.area_wise_delivery_charge, object.getString(Constant.area_wise_delivery_charge));

                            if (session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
                                MainActivity.pinCodeFragment = new PinCodeFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(Constant.FROM, "cart");
                                MainActivity.pinCodeFragment.setArguments(bundle);
                                MainActivity.pinCodeFragment.show(MainActivity.fm, null);
                            } else {
                                if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                                    getCartData();
                                } else {
                                    GetOfflineCart();
                                }
                            }
                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }

                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    private void getCartData() {
        CartFragment.isDeliverable = false;
        carts = new ArrayList<>();
        cartrecycleview.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson g = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    Cart cart = g.fromJson(jsonObject1.toString(), Cart.class);
                                    carts.add(cart);
                                } else {
                                    break;
                                }
                            }
                            cartAdapter = new CartAdapter(getContext(), getActivity(), carts);
                            cartAdapter.setHasStableIds(true);
                            cartrecycleview.setAdapter(cartAdapter);

                            lytTotal.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);
                            total = Double.parseDouble(objectbject.getString(Constant.TOTAL));
                            session.setData(Constant.TOTAL, String.valueOf(total));
                            Constant.TOTAL_CART_ITEM = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            SetData();
                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }
                        } else {
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cartrecycleview.setVisibility(View.VISIBLE);
                            lytempty.setVisibility(View.VISIBLE);
                            lytTotal.setVisibility(View.GONE);
                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }

                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        cartrecycleview.setVisibility(View.VISIBLE);
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }

                    }
                }
            }
        }, getActivity(), Constant.CART_URL, params, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            if (values.size() > 0) {
                ApiConfig.AddMultipleProductInCart(session, getActivity(), values);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.cart);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
