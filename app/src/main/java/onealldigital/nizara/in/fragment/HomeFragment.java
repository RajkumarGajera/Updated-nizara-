package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.AdapterStyle1;
import onealldigital.nizara.in.adapter.BrandsAdapter;
import onealldigital.nizara.in.adapter.CategoryAdapter;
import onealldigital.nizara.in.adapter.OfferAdapter;
import onealldigital.nizara.in.adapter.SectionAdapter;
import onealldigital.nizara.in.adapter.SectionAdapter1;
import onealldigital.nizara.in.adapter.SellerAdapter;
import onealldigital.nizara.in.adapter.SliderAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.helper.WrapContentViewPager;
import onealldigital.nizara.in.interfaces.ClickHome;
import onealldigital.nizara.in.model.Brand;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.Product;
import onealldigital.nizara.in.model.SearchQueryEvent;
import onealldigital.nizara.in.model.Seller;
import onealldigital.nizara.in.model.Slider;


public class HomeFragment extends Fragment implements ClickHome {

    int offset = 0;


    public Session session;

    public static ArrayList<Category> categoryArrayList, sectionList, sectionList2;
    public static ArrayList<Seller> sellerArrayList;
    public static ArrayList<Brand> brandArrayList;


    public static ArrayList<Product> productArrayList;


    ArrayList<Slider> sliderArrayList;
    Activity activity;
    private ClickHome clickInterface;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeLayout;
    View root;
    int timerDelay = 0, timerWaiting = 0;
    private CategoryAdapter categoryAdapter;
    private SellerAdapter sellerAdapter;
    SectionAdapter1 sectionAdapter1;

    RecyclerView categoryRecyclerView, sectionView, offerView, sectionView1, brandsr;
    ViewPager mPager;
    WrapContentViewPager brandPager;
    LinearLayout mMarkersLayout;
    int size;
    Timer swipeTimer;
    Handler handler;
    Runnable Update;
    int currentPage = 0;
    LinearLayout lytCategory, lytSeller;
    Menu menu;
    TextView tvMore, tvMoreSeller;
    boolean searchVisible = false;
    private ArrayList<String> offerList;
    private ShimmerFrameLayout mShimmerViewContainer;
    @SuppressLint("StaticFieldLeak")
    public static SwipeRefreshLayout.OnRefreshListener refreshListener;
    ImageView ImageAssam, ImageMeghalaya, ImageManipur, ImageNagaland, ImageTripura, ImageMizoram, Ap;

    SectionAdapter sectionAdapter;
    AdapterStyle1 mAdapter;

//    HomeFragment context=HomeFragment.th;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        session = new Session(getContext());
        activity = getActivity();

        timerDelay = 3000;
        timerWaiting = 3000;
        setHasOptionsMenu(true);

        clickInterface = this;

        swipeLayout = root.findViewById(R.id.swipeLayout);
        categoryRecyclerView = root.findViewById(R.id.categoryrecycleview);

        sectionView = root.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionView.setNestedScrollingEnabled(false);

        sectionView1 = root.findViewById(R.id.sectionView1);
        sectionView1.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionView1.setNestedScrollingEnabled(false);

        offerView = root.findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offerView.setNestedScrollingEnabled(false);

        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        lytCategory = root.findViewById(R.id.lytCategory);
        lytSeller = root.findViewById(R.id.lytSeller);
//        sellerRecyclerView = root.findViewById(R.id.sellerRecyclerView);
        tvMore = root.findViewById(R.id.tvMore);
        tvMoreSeller = root.findViewById(R.id.tvMoreSeller);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        ImageAssam = root.findViewById(R.id.ImageAssam);
        ImageManipur = root.findViewById(R.id.ImageManipur);
        ImageMeghalaya = root.findViewById(R.id.ImageMeghalaya);
        ImageMizoram = root.findViewById(R.id.ImageMizoram);
        ImageNagaland = root.findViewById(R.id.ImageNagaland);
        ImageTripura = root.findViewById(R.id.ImageTripura);
        Ap = root.findViewById(R.id.AP);


        brandPager = root.findViewById(R.id.brand_pager);


        //Changed

        /*if (!session.getBoolean(Constant.GET_SELECTED_PINCODE)) {
            MainActivity.pinCodeFragment = new PinCodeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "home");
            MainActivity.pinCodeFragment.setArguments(bundle);
            MainActivity.pinCodeFragment.show(MainActivity.fm, null);
        }*/

//        getproductData();


        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                Rect scrollBounds = new Rect();
                nestedScrollView.getHitRect(scrollBounds);
                activity.invalidateOptionsMenu();
            });
        }

        ImageAssam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "21");
                bundle.putString(Constant.NAME, "Assam");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;
////                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
//


            }
        });

        ImageTripura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "25");
                bundle.putString(Constant.NAME, "Tripura");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;

            }
        });

        ImageNagaland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "24");
                bundle.putString(Constant.NAME, "Nagaland");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;

            }
        });


        Ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "27");
                bundle.putString(Constant.NAME, "Arunachal Pradesh");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;


            }
        });

        ImageMizoram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "26");
                bundle.putString(Constant.NAME, "Mizoram");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;
            }
        });

        ImageMeghalaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "22");
                bundle.putString(Constant.NAME, "Meghalaya");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;
            }
        });

        ImageManipur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, "23");
                bundle.putString(Constant.NAME, "Manipur");
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).show(MainActivity.subcategoryFregment).addToBackStack(null).commit();
                MainActivity.active = MainActivity.subcategoryFregment;
            }
        });


        tvMore.setOnClickListener(v -> {
            /*if (!MainActivity.categoryClicked) {
                MainActivity.fm.beginTransaction().add(R.id.container, MainActivity.categoryFragment).show(MainActivity.categoryFragment).addToBackStack(null).commit();
                MainActivity.categoryClicked = true;
            } else {
                MainActivity.fm.beginTransaction().show(MainActivity.categoryFragment).addToBackStack(null).commit();
            }*/
            MainActivity.fm.beginTransaction().add(R.id.container, MainActivity.categoryFragment).show(MainActivity.categoryFragment).addToBackStack(null).commit();

//            MainActivity.bottomNavigationView.setItemActiveIndex(1);
            MainActivity.active = MainActivity.categoryFragment;
        });

        tvMoreSeller.setOnClickListener(v -> MainActivity.fm.beginTransaction().add(R.id.container, new SellerListFragment()).addToBackStack(null).commit());

        mPager = root.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, getContext());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        refreshListener = () -> {
            if (swipeTimer != null) {
                swipeTimer.cancel();
            }
            timerDelay = 3000;
            timerWaiting = 3000;
            if (ApiConfig.isConnected(getActivity())) {
                ApiConfig.getWalletBalance(activity, session);
                if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                    ApiConfig.getWalletBalance(activity, new Session(activity));
                }
                GetHomeData();
            }
        };

        swipeLayout.setOnRefreshListener(this::GetHomeData);

        if (ApiConfig.isConnected(getActivity())) {
            GetHomeData();
            if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
            }
        } else {
            nestedScrollView.setVisibility(View.VISIBLE);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        }



        return root;
    }

    public void GetHomeData() {
        if (swipeTimer != null) {
            swipeTimer.cancel();
        }
        timerDelay = 3000;
        timerWaiting = 3000;
        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
//                    largeLog("ApiConfig", "HomeData: "+response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        GetOfferImage(jsonObject.getJSONArray(Constant.OFFER_IMAGES));
                        GetCategory(jsonObject);
                        SectionProductRequest(jsonObject.getJSONArray(Constant.SECTIONS));
                        GetSlider(jsonObject.getJSONArray(Constant.SLIDER_IMAGES));
//                        GetSeller(jsonObject.getJSONArray(Constant.SELLER));
                        getBrandsData(jsonObject.getJSONArray(Constant.BRANDS));
                    } else {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
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
        }, getActivity(), Constant.GET_ALL_DATA_URL, params, false);

    }

    public void GetOfferImage(JSONArray jsonArray) {
        offerList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    offerList.add(object.getString(Constant.IMAGE));
                }
                offerView.setAdapter(new OfferAdapter(offerList, R.layout.offer_lyt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }


    void GetCategory(JSONObject object) {
        categoryArrayList = new ArrayList<>();
        try {
            int visible_count;
            int column_count;

            JSONArray jsonArray = object.getJSONArray(Constant.CATEGORIES);

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category category = new Category();
                    category = new Gson().fromJson(jsonObject.toString(), Category.class);
                    categoryArrayList.add(category);
                    if (category.getId().equals("21") || category.getId().equals("22") || category.getId().equals("23") || category.getId().equals("24") || category.getId().equals("25") || category.getId().equals("26") || category.getId().equals("27")) {
                        categoryArrayList.remove(category);
                    }
                }

                if (!object.getString("style").equals("")) {
                    if (object.getString("style").equals("style_1")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        column_count = Integer.parseInt(object.getString("column_count"));
                        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), column_count));
                        categoryAdapter = new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_grid, "home", visible_count);
                        categoryRecyclerView.setAdapter(categoryAdapter);
                    } else if (object.getString("style").equals("style_2")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        categoryAdapter = new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_list, "home", visible_count);
                        categoryRecyclerView.setAdapter(categoryAdapter);
                    }
                } else {
                    categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    categoryAdapter = new CategoryAdapter(getContext(), getActivity(), categoryArrayList, R.layout.lyt_category_list, "home", 6);
                    categoryRecyclerView.setAdapter(categoryAdapter);
                }
            } else {
                lytCategory.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SectionProductRequest(JSONArray jsonArray) {  //json request for product search
        sectionList = new ArrayList<>();
        sectionList2 = new ArrayList<>();
        try {
            for (int j=(jsonArray.length()-1); j > (jsonArray.length()-3); j--) {
                Category section = new Category();
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                section.setName(jsonObject.getString(Constant.TITLE));
                section.setId(jsonObject.getString(Constant.ID));
                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                section.setProductList(ApiConfig.GetProductList(productArray));
                sectionList.add(section);
            }
            sectionView.setVisibility(View.VISIBLE);
            sectionAdapter = new SectionAdapter(getContext(), getActivity(), sectionList);
            sectionView.setAdapter(sectionAdapter);
            for (int j = (jsonArray.length()-3); j >= 0; j--) {
                Category section = new Category();
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                section.setName(jsonObject.getString(Constant.TITLE));
                section.setId(jsonObject.getString(Constant.ID));
                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                section.setProductList(ApiConfig.GetProductList(productArray));
                sectionList2.add(section);
            }
            sectionView1.setVisibility(View.VISIBLE);
            sectionAdapter1 = new SectionAdapter1(getContext(), getActivity(), sectionList2);
            sectionView1.setAdapter(sectionAdapter1);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void GetSlider(JSONArray jsonArray) {
        sliderArrayList = new ArrayList<>();
        try {
            size = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
            }
            mPager.setAdapter(new SliderAdapter(sliderArrayList, getActivity(), R.layout.lyt_slider, "home"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());
            handler = new Handler();
            Update = () -> {
                if (currentPage == size) {
                    currentPage = 0;
                }
                try {
                    mPager.setCurrentItem(currentPage++, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, timerDelay, timerWaiting);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }

    void GetSeller(JSONArray jsonArray) {
        try {
            sellerArrayList = new ArrayList<>();
            if (jsonArray.length() > 0) {
                lytSeller.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Seller seller = new Gson().fromJson(jsonObject.toString(), Seller.class);
                    sellerArrayList.add(seller);
                }

//                sellerRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2,  GridLayoutManager.VERTICAL, false));
//                sellerRecyclerView.setAdapter(new SellerAdapter(getContext(), getActivity(), sellerArrayList, R.layout.lyt_seller, "home", 6));
            } else {
                lytSeller.setVisibility(View.GONE);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    void getBrandsData(JSONArray jsonArray) {
        try {
            brandArrayList = new ArrayList<>();
            if (jsonArray.length() > 0) {
                lytSeller.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Brand brand = new Gson().fromJson(jsonObject.toString(), Brand.class);
                    brandArrayList.add(brand);
//                    largeLog("BrandsData", jsonObject.toString());
                }

//                sellerRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2,  GridLayoutManager.VERTICAL, false));
                sellerAdapter = new SellerAdapter(getContext(), activity, brandArrayList, R.layout.lyt_seller, "category", 0);
                brandPager.setAdapter(new BrandsAdapter(getContext(), brandArrayList, clickInterface));
//                sellerRecyclerView.setAdapter(new BrandAdapter(getContext(), getActivity(), brandArrayList, R.layout.lyt_seller, "home", 6));
            } else {
                lytSeller.setVisibility(View.GONE);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.invalidateOptionsMenu();
        ApiConfig.GetSettings(activity);
        MainActivity.showHideSearchBar(true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        hideKeyboard();
    }

    @Override
    public void onPause() {
//        brandsr.setVisibility(View.GONE);
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_notification).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQueryEvent event) {
        String query = event.getQuery();
        categoryAdapter.getFilter().filter(query);
//        brandsr.setVisibility(View.VISIBLE);
//        brandsr.setAdapter(sellerAdapter);
        sellerAdapter.getFilter().filter(query);
        sectionAdapter.getCategoryFilter().filter(query);
//        sectionAdapter.getCategoryFilter().filter(query);

    }

    @Override
    public void onClickBrand(Brand model) {
        Fragment fragment = new BrandProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ID, model.getId());
        bundle.putString(Constant.TITLE, model.getName());
        bundle.putString(Constant.FROM, "Seller");
        bundle.putString("image", model.getImage());
        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClickCategory(int position) {

    }
}