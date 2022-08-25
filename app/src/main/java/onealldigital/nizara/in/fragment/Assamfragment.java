package onealldigital.nizara.in.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

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
import onealldigital.nizara.in.adapter.CategoryAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.SearchQueryEvent;

public class Assamfragment extends Fragment {

    public static ArrayList<Category> categoryArrayList;
    TextView txtnodata;
    RecyclerView categoryrecycleview;
    SwipeRefreshLayout swipeLayout;
    View root;
    Activity activity;
    private ShimmerFrameLayout mShimmerViewContainer;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.state_fragment, container, false);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        activity = getActivity();

        setHasOptionsMenu(true);


        txtnodata = root.findViewById(R.id.txtnodata);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        categoryrecycleview = root.findViewById(R.id.categoryrecycleview);

        categoryrecycleview.setLayoutManager(new GridLayoutManager(getContext(), Constant.GRIDCOLUMN));
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                    ApiConfig.getWalletBalance(activity, new Session(activity));
                }
                GetCategory();
            }
        });

        if (ApiConfig.isConnected(activity)) {
            if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
            }
            GetCategory();
        }

        return root;
    }

    void GetCategory() {
        categoryArrayList = new ArrayList<>();
        categoryrecycleview.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //System.out.println("======cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            Gson gson = new Gson();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category =new Category();
                                category = gson.fromJson(jsonObject.toString(), Category.class);
//                                categoryArrayList.add(category);
//                                if(category.getId().equals("21") || category.getId().equals("22") || category.getId().equals("23") || category.getId().equals("24") || category.getId().equals("25") || category.getId().equals("26") || category.getId().equals("27")){
//                                    categoryArrayList.remove(category);
//                                }
                                if (category.getId().equals("21")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("22")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("23")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("24")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("25")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("26")){
                                    categoryArrayList.add(category);
                                }
                                if (category.getId().equals("27")){
                                    categoryArrayList.add(category);
                                }
                            }
                            categoryAdapter = new CategoryAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_category, "category", 0);
                            categoryrecycleview.setAdapter(categoryAdapter);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            categoryrecycleview.setVisibility(View.VISIBLE);
                        } else {
                            txtnodata.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            categoryrecycleview.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        categoryrecycleview.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.CATEGORY_URL, params, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.title_category);
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
        categoryAdapter.getFilter().filter(query);
    }
}

