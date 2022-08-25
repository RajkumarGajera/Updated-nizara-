package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

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
import onealldigital.nizara.in.adapter.AdapterStyle1;
import onealldigital.nizara.in.adapter.BrandAdapter;
import onealldigital.nizara.in.adapter.ProductLoadMoreAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.interfaces.OnSubCatClick;
import onealldigital.nizara.in.model.Brand;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.Product;
import onealldigital.nizara.in.model.SearchQueryEvent;
import onealldigital.nizara.in.model.getbrand;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class BrandProductsFragment extends Fragment implements OnSubCatClick {
    public static ArrayList<Product> productArrayList;
    public static ArrayList<Category> categoryArrayList;
    @SuppressLint("StaticFieldLeak")
    public static ProductLoadMoreAdapter mAdapter;
    View root;
    Session session;
    private AdapterStyle1 productAdapter;
    int total;
    private BrandAdapter brandAdapter;
    NestedScrollView nestedScrollView;
    Activity activity;
    int offset = 0;
    String id, filterBy, from;
    RecyclerView recyclerView, subBrandRecycler;
    SwipeRefreshLayout swipeLayout;
    int filterIndex;
    TextView tvAlert;
    boolean isSort = false, isLoadMore = false;
    boolean isGrid = false;
    int resource;
    private ShimmerFrameLayout mShimmerViewContainer;
    ImageView imgTitle;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_brand_products, container, false);
        setHasOptionsMenu(true);
        offset = 0;
        activity = getActivity();

        session = new Session(activity);

        id = getArguments().getString(Constant.ID);
        from = getArguments().getString(Constant.FROM);
        String brandImg = getArguments().getString("image");

        /*if (session.getGrid("grid")) {
            resource = R.layout.lyt_item_grid;
            isGrid = true;

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
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        imgTitle = root.findViewById(R.id.imgTitle);
        subBrandRecycler = root.findViewById(R.id.subBrandRecycler);
        recyclerView = root.findViewById(R.id.recyclerView);

        Picasso.get()
                .load(brandImg)
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imgTitle);


        ApiConfig.GetSettings(activity);

        filterIndex = -1;

        if (ApiConfig.isConnected(activity)) {
//            GetSellerData();
            getBrandProducts();
        }

        swipeLayout.setColorSchemeResources(R.color.colorPrimary);

        swipeLayout.setOnRefreshListener(() -> {
            if (categoryArrayList != null && categoryArrayList.size() > 0) {
                offset = 0;

                categoryArrayList.clear();
//                    GetSellerData();
                getBrandProducts();
            }
        });

        return root;
    }

    void getBrandProducts() {
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
                        largeLog("Sub_brand", response);
                        System.out.println("Hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"+response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setCategory_id(jsonObject.getString("brand_id"));
                                if(jsonObject.getString("brand_id").equals(id)){

                                    System.out.println(jsonObject.getString(Constant.NAME));
//                                    System.out.println("Brand"+jsonObject.getString(Constant.BRANDS));


                                    category.setId(jsonObject.getString(Constant.ID));
                                    category.setName(jsonObject.getString(Constant.NAME));
                                //  category.setSlug(jsonObject.getString(Constant.BRANDS));
                                    category.setImage(jsonObject.getString(Constant.IMAGE));
                                    categoryArrayList.add(category);
                                }


                            }
                            System.out.println(categoryArrayList);

                            brandAdapter = new BrandAdapter(getContext(), activity, categoryArrayList, R.layout.lyt_subcategory, "sub_cate", BrandProductsFragment.this);
                            subBrandRecycler.setAdapter(brandAdapter);
                        }
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }
                    } catch (JSONException e) {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }
                    }
                }
            }
        }, activity, Constant.SUB_BRAND_URL, params, false);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                /*if (item1 != -1)
                    GetData();*/
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        menu.findItem(R.id.toolbar_sort).setVisible(isSort);
//        menu.findItem(R.id.toolbar_search).setVisible(true);
//        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getArguments().getString("title");
        activity.invalidateOptionsMenu();
        MainActivity.showHideSearchBar(false);
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

    @Override
    public void onSubCatClick(String id, String name, String from) {
        getProduct(id);
    }

    void getProduct(String id) {
        recyclerView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
        params.put("subbrand_id", id);
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, "" + offset);
        if (filterIndex != -1) {
            params.put(Constant.SORT, filterBy);
        }

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (offset == 0) {
                                productArrayList = new ArrayList<>();
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            try {
                                System.out.println("Rajjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"+jsonArray);
                                productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            } catch (Exception e) {
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            if (offset == 0) {
                                productAdapter = new AdapterStyle1(requireContext(), activity, productArrayList, R.layout.lyt_product);
                                productAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(productAdapter);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        productAdapter.notifyItemInserted(productArrayList.size() - 1);

                                                        offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
                                                        params.put(Constant.SUB_CATEGORY_ID, id);
                                                        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
                                                            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
                                                        }
                                                        params.put(Constant.USER_ID, session.getData(Constant.ID));
                                                        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                        params.put(Constant.OFFSET, "" + offset);
                                                        if (filterIndex != -1) {
                                                            params.put(Constant.SORT, filterBy);
                                                        }

                                                        ApiConfig.RequestToVolley(new VolleyCallback() {
                                                            @Override
                                                            public void onSuccess(boolean result, String response) {

                                                                if (result) {
                                                                    try {
                                                                        JSONObject objectbject = new JSONObject(response);
                                                                        if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                            JSONObject object = new JSONObject(response);
                                                                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                                                            productArrayList.remove(productArrayList.size() - 1);
                                                                            productAdapter.notifyItemRemoved(productArrayList.size());
                                                                            try {
                                                                                System.out.println("fehbdcjkvhndxkjlnslkcnsjlkzh"+jsonArray);
                                                                                productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            productAdapter.notifyDataSetChanged();
//                                                                            mAdapter.setLoaded();
                                                                            isLoadMore = false;
                                                                        }
                                                                    } catch (JSONException e) {

                                                                    }
                                                                }
                                                            }
                                                        }, activity, Constant.GET_PRODUCTS_URL, params, false);
                                                        isLoadMore = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            if (offset == 0) {
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQueryEvent event) {
        String query=event.getQuery();
        brandAdapter.getFilter().filter(query);
    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
}