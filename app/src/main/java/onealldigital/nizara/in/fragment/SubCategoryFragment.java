package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.AdapterStyle1;
import onealldigital.nizara.in.adapter.ProductLoadMoreAdapter;
import onealldigital.nizara.in.adapter.SubCategoryAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.Product;
import onealldigital.nizara.in.model.SearchQueryEvent;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SubCategoryFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ArrayList<Category> categoryArrayList;
    public static ProductLoadMoreAdapter mAdapter;
    private AdapterStyle1 productAdapter;
    View root;
    Session session;
    int total;
    int position;
    NestedScrollView nestedScrollView;
    private SubCategoryAdapter subCategoryAdapter;
    Activity activity;
    int offset = 0;
    String id, filterBy, from;
    RecyclerView subCategoryRecycler;
    SwipeRefreshLayout swipeLayout;
    int filterIndex;
    TextView tvAlert;
    boolean isSort = false, isLoadMore = false;
    boolean isGrid = false;
    int resource;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sub_category, container, false);
        setHasOptionsMenu(true);
        offset = 0;
        activity = getActivity();

        session = new Session(activity);

        from = getArguments().getString(Constant.FROM);
        id = getArguments().getString("id");

        /*if (session.getGrid("grid")) {
            resource = R.layout.lyt_item_grid;
            isGrid = true;

            recyclerView = root.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));

        } else {
            resource = R.layout.lyt_item_list;
            isGrid = false;

            recyclerView = root.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }*/

        swipeLayout = root.findViewById(R.id.swipeLayout);
        tvAlert = root.findViewById(R.id.tvAlert);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);

        subCategoryRecycler = root.findViewById(R.id.sub_category_recycler);
        subCategoryRecycler.setLayoutManager(new GridLayoutManager(getContext(), Constant.GRIDCOLUMN));
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);

        ApiConfig.GetSettings(activity);

        filterIndex = -1;


        /*if (ApiConfig.isConnected(activity)) {
            switch (from) {
                case "regular":
                    GetData();
                    isSort = true;
                    break;
                case "category":
                    GetCategory();
                    GetProducts();
                    break;
                case "section":
                    position = getArguments().getInt("position", 0);
                    productArrayList = HomeFragment.sectionList.get(position).getProductList();
                    mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, from);
                    recyclerView.setAdapter(mAdapter);
                    break;
            }
        }*/

        GetCategory();

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                swipeLayout.setRefreshing(false);
                /*if (from.equals("regular")) {
                    GetData();
                } else if (from.equals("category")) {
                    GetCategory();
                    GetProducts();
                }*/
                GetCategory();
            }
        });

        return root;
    }

    void GetCategory() {
        categoryArrayList = new ArrayList<>();
        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, id);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setId(jsonObject.getString(Constant.ID));
                                category.setCategory_id(jsonObject.getString(Constant.CATEGORY_ID));
                                category.setName(jsonObject.getString(Constant.NAME));
                                category.setSlug(jsonObject.getString(Constant.SLUG));
                                category.setSubtitle(jsonObject.getString(Constant.SUBTITLE));
                                category.setImage(jsonObject.getString(Constant.IMAGE));
                                categoryArrayList.add(category);
                            }

                            subCategoryAdapter = new SubCategoryAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_category, "sub_cate");
                            subCategoryRecycler.setAdapter(subCategoryAdapter);
                        }
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    } catch (JSONException e) {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                }
            }
        }, activity, Constant.SubCATEGORY_URL, params, false);
    }

//    void GetProducts() {
//        nestedScrollView.setVisibility(View.GONE);
//        mShimmerViewContainer.setVisibility(View.VISIBLE);
//        mShimmerViewContainer.startShimmer();
//        productArrayList = new ArrayList<>();
//        Map<String, String> params = new HashMap<>();
//        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
//        params.put(Constant.CATEGORY_ID, id);
//        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
//            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
//        }
//        params.put(Constant.SUB_CATEGORY_ID, "0");
//        params.put(Constant.USER_ID, session.getData(Constant.ID));
//        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
//        params.put(Constant.OFFSET, "" + offset);
//        if (filterIndex != -1) {
//            params.put(Constant.SORT, filterBy);
//        }
//
//        ApiConfig.RequestToVolley(new VolleyCallback() {
//            @Override
//            public void onSuccess(boolean result, String response) {
//                if (result) {
//                    try {
//                        JSONObject objectbject = new JSONObject(response);
//                        if (!objectbject.getBoolean(Constant.ERROR)) {
//                            isSort = true;
//                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
//                            JSONObject object = new JSONObject(response);
//                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
//                            try {
//                                productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
//                            } catch (Exception e) {
//                                nestedScrollView.setVisibility(View.VISIBLE);
//                                mShimmerViewContainer.setVisibility(View.GONE);
//                                mShimmerViewContainer.stopShimmer();
//                            }
//                            if (offset == 0) {
//                                mAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, from);
//                                mAdapter.setHasStableIds(true);
//                                recyclerView.setAdapter(mAdapter);
//                                nestedScrollView.setVisibility(View.VISIBLE);
//                                mShimmerViewContainer.setVisibility(View.GONE);
//                                mShimmerViewContainer.stopShimmer();
//                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                                    @Override
//                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                                        // if (diff == 0) {
//                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                                            if (productArrayList.size() < total) {
//                                                if (!isLoadMore) {
//                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
//                                                        //bottom of list!
//                                                        productArrayList.add(null);
//                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);
//
//                                                        offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
//                                                        Map<String, String> params = new HashMap<>();
//                                                        params.put(Constant.CATEGORY_ID, id);
//                                                        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
//                                                            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
//                                                        }
//                                                        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
//                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
//                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
//                                                        params.put(Constant.OFFSET, offset + "");
//                                                        if (filterIndex != -1) {
//                                                            params.put(Constant.SORT, filterBy);
//                                                        }
//
//                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
//                                                            @Override
//                                                            public void onSuccess(boolean result, String response) {
//
//                                                                if (result) {
//                                                                    try {
//                                                                        JSONObject objectbject = new JSONObject(response);
//                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {
//
//                                                                            JSONObject object = new JSONObject(response);
//                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
//                                                                            productArrayList.remove(productArrayList.size() - 1);
//                                                                            mAdapter.notifyItemRemoved(productArrayList.size());
//                                                                            try {
//                                                                                productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
//                                                                            } catch (Exception e) {
//                                                                                nestedScrollView.setVisibility(View.VISIBLE);
//                                                                                mShimmerViewContainer.setVisibility(View.GONE);
//                                                                                mShimmerViewContainer.stopShimmer();
//                                                                            }
//
//                                                                            mAdapter.notifyDataSetChanged();
//                                                                            mAdapter.setLoaded();
//                                                                            isLoadMore = false;
//                                                                        }
//                                                                    } catch (JSONException e) {
//                                                                        nestedScrollView.setVisibility(View.VISIBLE);
//                                                                        mShimmerViewContainer.setVisibility(View.GONE);
//                                                                        mShimmerViewContainer.stopShimmer();
//                                                                    }
//                                                                }
//                                                            }
//                                                        }, activity, Constant.GET_PRODUCTS_URL, params, false);
//                                                        isLoadMore = true;
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                        } else {
//                            activity.invalidateOptionsMenu();
//                            nestedScrollView.setVisibility(View.VISIBLE);
//                            mShimmerViewContainer.setVisibility(View.GONE);
//                            mShimmerViewContainer.stopShimmer();
//                        }
//                    } catch (JSONException e) {
//                        nestedScrollView.setVisibility(View.VISIBLE);
//                        mShimmerViewContainer.setVisibility(View.GONE);
//                        mShimmerViewContainer.stopShimmer();
//                    }
//                }
//            }
//        }, activity, Constant.GET_PRODUCTS_URL, params, false);
//    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (isSort) {
            if (item.getItemId() == R.id.toolbar_sort) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getString(R.string.filterby));
                builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, (dialog, item1) -> {
                    filterIndex = item1;
                    switch (item1) {
                        case 0:
                            filterBy = Constant.NEW;
                            break;
                        case 1:
                            filterBy = Constant.OLD;
                            break;
                        case 2:
                            filterBy = Constant.HIGH;
                            break;
                        case 3:
                            filterBy = Constant.LOW;
                            break;
                    }
                    /*if (item1 != -1) {
                        if (from.equals("regular")) {
                            GetData();
                        } else if (from.equals("category")) {
                            GetProducts();
                        }
                        dialog.dismiss();
                    }*/
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }

        return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_sort).setVisible(isSort);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getArguments().getString("name");
        activity.invalidateOptionsMenu();
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
    public void onPause() {
        super.onPause();
        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQueryEvent event) {
        String query=event.getQuery();
        subCategoryAdapter.getFilter().filter(query);
    }

}