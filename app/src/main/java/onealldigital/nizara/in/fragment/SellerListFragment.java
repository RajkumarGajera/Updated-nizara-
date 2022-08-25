package onealldigital.nizara.in.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.SellerAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.model.Brand;
import onealldigital.nizara.in.model.SearchQueryEvent;
import onealldigital.nizara.in.model.Seller;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class SellerListFragment extends Fragment {

    public static ArrayList<Brand> brandList;
    TextView txtnodata;
    RecyclerView sellerRecyclerView;
    SwipeRefreshLayout swipeLayout;
    View root;
    Activity activity;
    private ShimmerFrameLayout mShimmerViewContainer;
    Session session;
    private SellerAdapter sellerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_seller_list, container, false);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        activity = getActivity();
        session = new Session(activity);
        setHasOptionsMenu(true);

        txtnodata = root.findViewById(R.id.txtnodata);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        sellerRecyclerView = root.findViewById(R.id.sellerRecyclerView);

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(() -> {
            if (ApiConfig.isConnected(activity)) {
                if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                    ApiConfig.getWalletBalance(activity, new Session(activity));
                }
                GetSellerList();
            }
            swipeLayout.setRefreshing(false);
        });

        if (ApiConfig.isConnected(activity)) {
            GetSellerList();
        }

        return root;
    }

    void GetSellerList() {
        sellerRecyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SELLER_DATA, Constant.GetVal);
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }
        ApiConfig.RequestToVolley((result, response) -> {
            //System.out.println("======cate " + response);
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    brandList = new ArrayList<>();
                    if (!object.getBoolean(Constant.ERROR)) {
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Brand brand = gson.fromJson(jsonObject.toString(), Brand.class);
                            brandList.add(brand);
                        }
                        sellerAdapter = new SellerAdapter(getContext(), activity, brandList, R.layout.lyt_seller, "category", 0);
                        sellerRecyclerView.setAdapter(sellerAdapter);
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        sellerRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        txtnodata.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        sellerRecyclerView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    sellerRecyclerView.setVisibility(View.GONE);
                }
            }
        }, activity, Constant.BRAND_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.seller);
        getActivity().invalidateOptionsMenu();
        MainActivity.clearSearchBar();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQueryEvent event) {
        String query=event.getQuery();
        sellerAdapter.getFilter().filter(query);
    }
}