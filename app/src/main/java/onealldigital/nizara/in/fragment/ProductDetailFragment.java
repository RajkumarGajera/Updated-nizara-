package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.AdapterStyle1;
import onealldigital.nizara.in.adapter.BrandAdapter;
import onealldigital.nizara.in.adapter.ReviewAdapter;
import onealldigital.nizara.in.adapter.SliderAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.DatabaseHelper;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Address;
import onealldigital.nizara.in.model.Brand;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.Favorite;
import onealldigital.nizara.in.model.PriceVariation;
import onealldigital.nizara.in.model.Product;
import onealldigital.nizara.in.model.Reviews;
import onealldigital.nizara.in.model.Slider;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class ProductDetailFragment extends Fragment implements View.OnClickListener {
    static ArrayList<Slider> sliderArrayList;
    TextView tvPinCode, tvSeller, showDiscount, tvMfg, tvMadeIn, txtProductName,tv_see_more_reviews, txtPrice, tvCurrency, txtoriginalprice, txtMeasurement, txtstatus, tvTitleMadeIn, tvTitleMfg;
    WebView webDescription;
    private EditText txtqty;
    ViewPager viewPager;
    Spinner spinner;
    LinearLayout lytSpinner;
//    ImageView imgIndicator;
    LinearLayout mMarkersLayout;
    RelativeLayout lytMfg, lytMadeIn;
    ConstraintLayout lytqty;
//    RelativeLayout lytmainprice, lytDiscount,;
    ScrollView scrollView;
    Session session;
    boolean favorite;
    ImageView imgFav;
    ImageButton imgAdd, imgMinus;
    LinearLayout lytsave;
//    LinearLayout lytSimilar;
    ImageView lytshare;
    int size, count;
    View root;
    int vpos;
    String from, id,cid;
    boolean isLogin;
    Product product;
    PriceVariation priceVariation;
    ArrayList<PriceVariation> priceVariationslist;
    DatabaseHelper databaseHelper;
    int position = 0;
    private Button btnCart, btnBuyNow;
    Activity activity;
    RecyclerView recyclerView, reviewsRecycler;
//    RelativeLayout relativeLayout;
    TextView tvMore;
    ImageView imgReturnable, imgCancellable;
    TextView tvReturnable, tvCancellable;
    String taxPercentage;
    LottieAnimationView lottieAnimationView;
    ShimmerFrameLayout mShimmerViewContainer;

    EditText review;
    Button Send;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    RatingBar ratingBar;
    ReviewAdapter reviewAdapter;
    ArrayList list;

    String productid;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_product_detail, container, false);

        setHasOptionsMenu(true);
        activity = getActivity();

        Constant.CartValues = new HashMap<>();

        session = new Session(activity);
        isLogin = session.getBoolean(Constant.IS_USER_LOGIN);
        databaseHelper = new DatabaseHelper(activity);

        from = getArguments().getString(Constant.FROM);

        taxPercentage = "0";

        vpos = getArguments().getInt("vpos", 0);
        id = getArguments().getString("id");


        if (from.equals("fragment") || from.equals("sub_cate") || from.equals("favorite") || from.equals("search") || from.equals("seller")) {
            position = getArguments().getInt("position");
        }

        lytqty = root.findViewById(R.id.lytqty);
        scrollView = root.findViewById(R.id.scrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        sliderArrayList = new ArrayList<>();
        viewPager = root.findViewById(R.id.viewPager);
        txtProductName = root.findViewById(R.id.txtproductname);
        txtoriginalprice = root.findViewById(R.id.txtoriginalprice);
        webDescription = root.findViewById(R.id.txtDescription);
        txtPrice = root.findViewById(R.id.txtprice);
        tvCurrency = root.findViewById(R.id.tv_currency);
//        lytDiscount = root.findViewById(R.id.lytDiscount);
        txtMeasurement = root.findViewById(R.id.txtmeasurement);
        imgFav = root.findViewById(R.id.imgFav);
//        lytmainprice = root.findViewById(R.id.lytmainprice);
        txtqty = root.findViewById(R.id.txtqty);
        txtstatus = root.findViewById(R.id.txtstatus);
        imgAdd = root.findViewById(R.id.btnaddqty);
        imgMinus = root.findViewById(R.id.btnminusqty);
        tv_see_more_reviews = root.findViewById(R.id.tv_see_more_reviews);
        spinner = root.findViewById(R.id.spinner);
        lytSpinner = root.findViewById(R.id.lytSpinner);
//        imgIndicator = root.findViewById(R.id.imgIndicator);
        showDiscount = root.findViewById(R.id.showDiscount);
        tvSeller = root.findViewById(R.id.tvSeller);
        tvSeller.setPaintFlags(tvSeller.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lytshare = root.findViewById(R.id.img_share);
        lytsave = root.findViewById(R.id.lytsave);
//        lytSimilar = root.findViewById(R.id.lytSimilar);
        tvMadeIn = root.findViewById(R.id.tvMadeIn);
        tvTitleMadeIn = root.findViewById(R.id.tvTitleMadeIn);
        tvMfg = root.findViewById(R.id.tvMfg);
        tvTitleMfg = root.findViewById(R.id.tvTitleMfg);
        lytMfg = root.findViewById(R.id.lytMfg);
        lytMadeIn = root.findViewById(R.id.lytMadeIn);
        btnCart = root.findViewById(R.id.btnCart);
        btnBuyNow = root.findViewById(R.id.btnBuy);
        recyclerView = root.findViewById(R.id.recyclerView);
//        relativeLayout = root.findViewById(R.id.relativeLayout);
        tvMore = root.findViewById(R.id.tvMore);
        tvPinCode = root.findViewById(R.id.tvPinCode);

        tvReturnable = root.findViewById(R.id.tvReturnable);
        tvCancellable = root.findViewById(R.id.tvCancellable);
        imgReturnable = root.findViewById(R.id.imgReturnable);
        imgCancellable = root.findViewById(R.id.imgCancellable);

        lottieAnimationView = root.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation("add_to_wish_list.json");

        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);


        ratingBar = root.findViewById(R.id.rate);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                int rat = (int) rating;
                String message = null;

                switch(rat){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;

                }

            }
        });

        review = root.findViewById(R.id.Enter_review);
        Send = root.findViewById(R.id.Send_review);

        tv_see_more_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getContext(),revire_recycle.class);
                intent.putExtra("Productid",id);
                startActivity(intent);
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Review= review.getText().toString();
                float rate = ratingBar.getRating();
                String User_name=session.getData(Constant.NAME);
                String Id = myRef.push().getKey();

                Reviews reviews = new Reviews(Review, rate, User_name);

                myRef.child("Review").child(id).child(id).child(Id).setValue(reviews).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        review.setText("");
                        ratingBar.setRating(3);
                    }
                });

            }


        });



        reviewsRecycler = root.findViewById(R.id.reviews_recycler);

//        reviewsRecycler.setAdapter(reviewAdapter);
        list = new ArrayList<>();
        reviewsRecycler.setHasFixedSize(true);
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        reviewAdapter = new ReviewAdapter( getContext(),list);
        reviewAdapter = new ReviewAdapter(getContext(), activity, list, R.layout.lyt_reviews, "sub_review",ProductDetailFragment.this);
        reviewsRecycler.setAdapter(reviewAdapter);

        myRef.child("Review").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Reviews reviews = dataSnapshot1.getValue(Reviews.class);
                        list.add(reviews);
//                        list.add(dataSnapshot1.getKey());
//                        list.add(reviews.getname());
//                        list.add(reviews.getRate());
//                        list.add(reviews.getRe());
                    }
                }
                System.out.println(list);
//                reviewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//                String st=list.get(0).toString();
//                float rat = (float) list.get(1);
//                reviewAdapter = new ReviewAdapter(st,rat,st);


        GetProductDetail(id);
        ApiConfig.GetSettings(activity);


        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSimilar();
            }
        });

        /*lytSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSimilar();
            }
        });*/

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isConnected(activity)) {
                    if (txtqty.getText() == null || txtqty.getText().toString().isEmpty()){
                        count = 1;
                        txtqty.setText("1");
                    }else {
                        count = Integer.parseInt(txtqty.getText().toString());
                    }
                    if (!(count > Float.parseFloat(priceVariationslist.get(vpos).getStock()))) {
                        if (count < Integer.parseInt(String.valueOf(5000))) {
                            Constant.CLICK = true;

                            if (isLogin) {
                                txtqty.setText("0");
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }
                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                    NotifyData(count);
                }
            }
        });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isConnected(activity)) {
                    if (txtqty.getText() == null || txtqty.getText().toString().isEmpty()){
                        count = 1;
                        txtqty.setText("1");
                    }else {
                        count = Integer.parseInt(txtqty.getText().toString());
                    }
                    if (!(count > Float.parseFloat(priceVariationslist.get(vpos).getStock()))) {
                        if (count < Integer.parseInt(String.valueOf(5000))) {
                            Constant.CLICK = true;

                            if (isLogin) {
                                txtqty.setText("0");
                                if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                    } else {
                                        Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                        Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                    }
                                } else {
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }
                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                    NotifyData(count);
                }
                MainActivity.fm.beginTransaction().add(R.id.container, new CartFragment()).addToBackStack(null).commit();

            }
        });

        lytshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lytshare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = Constant.MAINBASEUrl + "itemdetail/" + product.getSlug();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_via));
                        startActivity(shareIntent);
                    }
                });
            }
        });

        tvPinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenBottomDialog(activity);
            }
        });

        lytsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    favorite = product.isIs_favorite();
                    if (ApiConfig.isConnected(activity)) {
                        if (favorite) {
                            favorite = false;
                            lottieAnimationView.setVisibility(View.GONE);
                            product.setIs_favorite(false);
                            imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                        } else {
                            favorite = true;
                            product.setIs_favorite(true);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                        }
                        ApiConfig.AddOrRemoveFavorite(activity, session, product.getId(), favorite);
                    }
                } else {
                    favorite = databaseHelper.getFavouriteById(product.getId());
                    if (favorite) {
                        favorite = false;
                        lottieAnimationView.setVisibility(View.GONE);
                        imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                    } else {
                        favorite = true;
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        lottieAnimationView.playAnimation();
                    }
                    databaseHelper.AddOrRemoveFavorite(product.getId(), favorite);
                }
                if (from.equals("fragment")) {
                    ProductListFragment.productArrayList.get(position).setIs_favorite(favorite);
                    ProductListFragment.mAdapter.notifyDataSetChanged();
                } else if (from.equals("sub_cate")) {
                    ProductListFragment.productArrayList.get(position).setIs_favorite(favorite);
                    ProductListFragment.mAdapter.notifyDataSetChanged();
                } else if (from.equals("favorite")) {
                    if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                        Favorite favProduct = new Favorite();
                        favProduct.setId(product.getId());
                        favProduct.setProduct_id(product.getId());
                        favProduct.setName(product.getName());
                        favProduct.setSlug(product.getSlug());
                        favProduct.setSubcategory_id(product.getSubcategory_id());
                        favProduct.setImage(product.getImage());
                        favProduct.setStatus(product.getStatus());
                        favProduct.setDate_added(product.getDate_added());
                        favProduct.setCategory_id(product.getCategory_id());
                        favProduct.setIndicator(product.getIndicator());
                        favProduct.setManufacturer(product.getManufacturer());
                        favProduct.setMade_in(product.getMade_in());
                        favProduct.setReturn_status(product.getReturn_status());
                        favProduct.setCancelable_status(product.getCancelable_status());
                        favProduct.setTill_status(product.getTill_status());
                        favProduct.setPriceVariations(product.getPriceVariations());
                        favProduct.setOther_images(product.getOther_images());
                        favProduct.setIs_favorite(true);
                        if (favorite) {
                            FavoriteFragment.favoriteArrayList.add(favProduct);
                        } else {
                            FavoriteFragment.favoriteArrayList.remove(position);
                        }
                        FavoriteFragment.favoriteLoadMoreAdapter.notifyDataSetChanged();
                    } else {
                        if (favorite) {
                            FavoriteFragment.productArrayList.add(product);
                        } else {
                            FavoriteFragment.productArrayList.remove(position);
                        }
                        FavoriteFragment.offlineFavoriteAdapter.notifyDataSetChanged();
                    }
                } else if (from.equals("search")) {
                    SearchFragment.productArrayList.get(position).setIs_favorite(favorite);
                    SearchFragment.productAdapter.notifyDataSetChanged();
                } else if (from.equals("seller")) {
                    BrandProductsFragment.productArrayList.get(position).setIs_favorite(favorite);
                    BrandProductsFragment.mAdapter.notifyDataSetChanged();
                }
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                if (txtqty.getText() == null || txtqty.getText().toString().isEmpty()){
                    count = 1;
                    txtqty.setText(count+"");
                } else{
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (count > 1){
                        count--;
                        txtqty.setText(count+"");
                    }
                }
                /*if (ApiConfig.isConnected(activity)) {
                    Constant.CLICK = true;
                    count = Integer.parseInt(txtqty.getText().toString());
                    if (!(count <= 0)) {
                        count--;
                        txtqty.setText("" + count);
                        if (isLogin) {
                            if (Constant.CartValues.containsKey(priceVariationslist.get(vpos).getId())) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Constant.CartValues.replace(priceVariationslist.get(vpos).getId(), "" + count);
                                } else {
                                    Constant.CartValues.remove(priceVariationslist.get(vpos).getId());
                                    Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                                }
                            } else {
                                Constant.CartValues.put(priceVariationslist.get(vpos).getId(), "" + count);
                            }

                            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                        } else {
                            databaseHelper.AddOrderData(priceVariationslist.get(vpos).getId(), priceVariation.getProduct_id(), "" + count);
                        }
                        NotifyData(count);

                    }
                }*/

            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                if (txtqty.getText() == null || txtqty.getText().toString().isEmpty()){
                    count = 1;
                } else {
                    count = Integer.parseInt(txtqty.getText().toString());
                    count += 1;
                }
                txtqty.setText(count+"");
            }
        });

        return root;
    }


    public void OpenBottomDialog(final Activity activity) {
        try {
            View sheetView = activity.getLayoutInflater().inflate(R.layout.dialog_check_pincode, null);
            ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }

            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            EditText edtPinCode = sheetView.findViewById(R.id.edtPinCode);
            Button btnApply = sheetView.findViewById(R.id.btnApply);
            ImageView imgPincodeClose = sheetView.findViewById(R.id.imgPincodeClose);
            mBottomSheetDialog.setCancelable(true);


            imgPincodeClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();
                }
            });

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pinCode = edtPinCode.getText().toString();

                    if (ApiConfig.CheckValidattion(pinCode, false, false)) {
                        edtPinCode.requestFocus();
                        edtPinCode.setError(activity.getString(R.string.enter_an_pincode));
                    } else if (ApiConfig.isConnected(activity)) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
                        params.put(Constant.PRODUCT_ID, product.getId());
                        params.put(Constant.PINCODE, pinCode);

                        ApiConfig.RequestToVolley(new VolleyCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(boolean result, String response) {
                                if (result) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                                            tvPinCode.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                                            tvPinCode.setText(activity.getString(R.string.deliverable_to) + pinCode);
                                        } else {
                                            tvPinCode.setTextColor(ContextCompat.getColor(activity, R.color.red));
                                            tvPinCode.setText(activity.getString(R.string.can_not_deliverable_to) + pinCode);
                                        }
                                        mBottomSheetDialog.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, activity, Constant.GET_PRODUCTS_URL, params, false);
                    }
                }
            });

            mBottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowSimilar() {
        Fragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", product.getId());
        bundle.putString("cat_id", product.getCategory_id());
        bundle.putString(Constant.FROM, "similar");
        bundle.putString("name", "Similar Products");
        fragment.setArguments(bundle);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }


    void GetSimilarData(Product product) {
        ArrayList<Product> productArrayList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SIMILAR_PRODUCT, Constant.GetVal);
        params.put(Constant.PRODUCT_ID, product.getId());
        params.put(Constant.CATEGORY_ID, product.getCategory_id());
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);

//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                if (jsonObject != null) {
//                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                    System.out.println(jsonObject1);
//                                    String str=product.getSeller_id();
//                                    product.setSeller_id(str);
//                                    System.out.println("hi"+str);
//                                    if(jsonObject1.getString("seller_id").equals(product.getSubcategory_id())){
//                                        productArrayList.add(ApiConfig.GetProductList(jsonArray).get(i));
//                                    }
//
//                                }else{
//                                }
////                                product1 = gson.fromJson(jsonObject.toString(), Product.class);
////                                System.out.println("Rajjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"+ product1.getSubcategory_id());
////                                id = product1.getId();
////                                productArrayList.add(product1);
//
//                            }
//                            System.out.println(productArrayList);

                            try {
                                productArrayList.addAll(ApiConfig.GetProductList(jsonArray));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            AdapterStyle1 adapter = new AdapterStyle1(getContext(), activity, productArrayList, R.layout.offer_layout);
                            recyclerView.setAdapter(adapter);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }

    public void NotifyData(int count) {
        switch (from) {
            case "fragment":
                ProductListFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                ProductListFragment.mAdapter.notifyItemChanged(position, ProductListFragment.productArrayList.get(position));
                if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                    ApiConfig.getCartItemCount(activity, session);
                } else {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "seller":
                BrandProductsFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                BrandProductsFragment.mAdapter.notifyItemChanged(position, ProductListFragment.productArrayList.get(position));
                if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                    ApiConfig.getCartItemCount(activity, session);
                } else {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "favorite":
                if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                    FavoriteFragment.favoriteArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.favoriteLoadMoreAdapter.notifyItemChanged(position, FavoriteFragment.favoriteArrayList.get(position));
                } else {
                    FavoriteFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                    FavoriteFragment.offlineFavoriteAdapter.notifyItemChanged(position, FavoriteFragment.productArrayList.get(position));
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "search":
                SearchFragment.productArrayList.get(position).getPriceVariations().get(vpos).setQty(count);
                SearchFragment.productAdapter.notifyItemChanged(position, SearchFragment.productArrayList.get(position));
                if (!session.getBoolean(Constant.IS_USER_LOGIN)) {
                    databaseHelper.getTotalItemOfCart(activity);
                }
                activity.invalidateOptionsMenu();
                break;
            case "section":
            case "share":
                if (!session.getBoolean(Constant.IS_USER_LOGIN)) {
                    databaseHelper.getTotalItemOfCart(activity);
                } else {
                    ApiConfig.getCartItemCount(activity, session);
                }
                activity.invalidateOptionsMenu();
                break;
        }
    }

    void GetProductDetail(final String productid) {
        scrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }
        if (from.equals("share")) {
            params.put(Constant.SLUG, productid);
        } else {
            params.put(Constant.PRODUCT_ID, productid);
        }
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
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
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                                        JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                                        for (int j = 0; j < pricearray.length(); j++) {
                                            JSONObject obj = pricearray.getJSONObject(j);
                                            String discountpercent = "0";
                                            if (!obj.getString(Constant.DISCOUNTED_PRICE).equals("0")) {
                                                discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                                            }
                                            priceVariations.add(new PriceVariation(obj.getString(Constant.CART_ITEM_COUNT), obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                                        }
                                        product = new Product(jsonObject.getString(Constant.SELLER_NAME), jsonObject.getString(Constant.RETURN_DAYS), jsonObject.getString(Constant.IS_APPROVED), jsonObject.getString(Constant.SELLER_ID), jsonObject.getString(Constant.TAX_ID), jsonObject.getString(Constant.TAX_PERCENT), jsonObject.getString(Constant.ROW_ORDER), jsonObject.getString(Constant.TILL_STATUS), jsonObject.getString(Constant.CANCELLABLE_STATUS), jsonObject.getString(Constant.MANUFACTURER), jsonObject.getString(Constant.MADE_IN), jsonObject.getString(Constant.RETURN_STATUS), jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getBoolean(Constant.IS_FAVORITE), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString(Constant.INDICATOR));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            priceVariationslist = product.getPriceVariations();

                            SetProductDetails(product);
                            GetSimilarData(product);

                        }
                        scrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    } catch (JSONException e) {
                        scrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                }
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    void SetProductDetails(final Product product) {
        try {

            txtProductName.setText(product.getName());
            try {
                taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            sliderArrayList = new ArrayList<>();

            JSONArray jsonArray = product.getOther_images();
            size = jsonArray.length();

            sliderArrayList.add(new Slider(product.getImage()));

            if (product.getMade_in().length() > 0) {
                lytMadeIn.setVisibility(View.VISIBLE);
                tvMadeIn.setText(product.getMade_in());
            }

            if (product.getManufacturer().length() > 0) {
                lytMfg.setVisibility(View.VISIBLE);
                tvMfg.setText(product.getManufacturer());
            }

            tvSeller.setText(product.getSeller_name());

            if (session.getBoolean(Constant.GET_SELECTED_PINCODE)) {
                if (!session.getData(Constant.GET_SELECTED_PINCODE_NAME).equals(activity.getString(R.string.all))) {
                    tvPinCode.setText(activity.getString(R.string.deliverable_to) + session.getData(Constant.GET_SELECTED_PINCODE_NAME));
                }
            }

//            tvSeller.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Fragment fragment = new BrandProductsFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Constant.ID, product.getSeller_id());
//                    bundle.putString(Constant.TITLE, product.getSeller_name());
//                    bundle.putString(Constant.FROM, from);
//                    fragment.setArguments(bundle);
//                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
//                }
//            });

            if (isLogin) {
                if (product.isIs_favorite()) {
                    favorite = true;
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    favorite = false;
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            } else {
                if (databaseHelper.getFavouriteById(product.getId())) {
                    imgFav.setImageResource(R.drawable.ic_is_favorite);
                } else {
                    imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                }
            }

            if (isLogin) {
                if (Constant.CartValues.containsKey(product.getPriceVariations().get(0).getId())) {
                    txtqty.setText("" + Constant.CartValues.get(product.getPriceVariations().get(0).getId()));
                } else {
                    if (product.getPriceVariations().get(0).getCart_count().equals("0"))
                        txtqty.setText("1");
                    else
                        txtqty.setText(product.getPriceVariations().get(0).getCart_count());

                }
            } else {
                if (databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getPriceVariations().get(0).getProduct_id()).equals("0"))
                    txtqty.setText("1");
                else
                    txtqty.setText(databaseHelper.CheckOrderExists(product.getPriceVariations().get(0).getId(), product.getPriceVariations().get(0).getProduct_id()));
            }

            if (product.getReturn_status().equalsIgnoreCase("1")) {
                imgReturnable.setImageDrawable(getResources().getDrawable(R.drawable.ic_returnable));
                tvReturnable.setText(product.getReturn_days() + getString(R.string.days) + getString(R.string.returnable));
            } else {
                imgReturnable.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_returnable));
                tvReturnable.setText(getString(R.string.not_returnable));
            }

            if (product.getCancelable_status().equalsIgnoreCase("1")) {
                imgCancellable.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancellable));
                tvCancellable.setText(getString(R.string.cancellable_till) + ApiConfig.toTitleCase(product.getTill_status()));
            } else {
                imgCancellable.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_cancellable));
                tvCancellable.setText(getString(R.string.not_cancellable));
            }


            for (int i = 0; i < jsonArray.length(); i++) {
                sliderArrayList.add(new Slider(jsonArray.getString(i)));
            }

            viewPager.setAdapter(new SliderAdapter(sliderArrayList, activity, R.layout.lyt_detail_slider, "detail"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, getContext());


            if (priceVariationslist.size() == 1) {
                spinner.setVisibility(View.INVISIBLE);
                lytSpinner.setVisibility(View.INVISIBLE);
//                lytmainprice.setEnabled(false);
                priceVariation = priceVariationslist.get(0);
                session.setData(Constant.PRODUCT_VARIANT_ID, "" + 0);
                SetSelectedData();
            }

            /*if (!product.getIndicator().equals("0")) {
                imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    imgIndicator.setImageResource(R.drawable.ic_veg_icon);
                else if (product.getIndicator().equals("2"))
                    imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
            }*/
            ProductDetailFragment.CustomAdapter customAdapter = new ProductDetailFragment.CustomAdapter();
            spinner.setAdapter(customAdapter);

            webDescription.setVerticalScrollBarEnabled(true);
            webDescription.loadDataWithBaseURL("", product.getDescription(), "text/html", "UTF-8", "");
            webDescription.setBackgroundColor(getResources().getColor(R.color.white));
            txtProductName.setText(product.getName());

            spinner.setSelection(vpos);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    priceVariation = product.getPriceVariations().get(i);
                    vpos = i;
                    session.setData(Constant.PRODUCT_VARIANT_ID, "" + i);
                    SetSelectedData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            scrollView.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
            Toast.makeText(requireContext(), ""+ignored.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.app_name);
        activity.invalidateOptionsMenu();
        MainActivity.showHideSearchBar(false);
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


    @SuppressLint("SetTextI18n")
    public void SetSelectedData() {

        txtMeasurement.setText(" ( " + priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + " ) ");
        txtstatus.setText(priceVariation.getServe_for());

        double price, oPrice;
        String taxPercentage = "0";
        try {
            taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
//            lytDiscount.setVisibility(View.INVISIBLE);
            showDiscount.setVisibility(View.INVISIBLE);
            txtoriginalprice.setVisibility(View.GONE);
            price = ((Float.parseFloat(priceVariation.getPrice()) + ((Float.parseFloat(priceVariation.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            txtoriginalprice.setVisibility(View.VISIBLE);
            price = ((Float.parseFloat(priceVariation.getDiscounted_price()) + ((Float.parseFloat(priceVariation.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            oPrice = (Float.parseFloat(priceVariation.getPrice()) + ((Float.parseFloat(priceVariation.getPrice()) * Float.parseFloat(taxPercentage)) / 100));

            txtoriginalprice.setPaintFlags(txtoriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtoriginalprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + oPrice));

            //lytDiscount.setVisibility(View.VISIBLE);
            showDiscount.setVisibility(View.VISIBLE);
            showDiscount.setText(priceVariation.getDiscountpercent().replace("(", "").replace(")", ""));
        }
        txtPrice.setText(ApiConfig.StringFormat("" + price));
        tvCurrency.setText(session.getData(Constant.CURRENCY));


        if (isLogin) {
//            System.out.println("priceVariation.getId()) : " + Constant.CartValues);
            if (Constant.CartValues.containsKey(priceVariation.getId())) {
                txtqty.setText(Constant.CartValues.get(priceVariation.getId()));
            } else {
                if (priceVariation.getCart_count().equals("0"))
                    txtqty.setText("1");
                else
                    txtqty.setText(priceVariation.getCart_count());

            }
        } else {
            if (databaseHelper.CheckOrderExists(priceVariation.getId(), priceVariation.getProduct_id()).equals("0"))
                txtqty.setText("1");
            else
                txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), priceVariation.getProduct_id()));

        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        } else {
            txtstatus.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
    }

    @Override
    public void onClick(View v) {


    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
//            TextView price = view.findViewById(R.id.txtprice);

            PriceVariation priceVariation = product.getPriceVariations().get(i);
            measurement.setText(priceVariation.getMeasurement() + " " + priceVariation.getMeasurement_unit_name());
//            price.setText(session.getData(Constant.CURRENCY) + priceVariation.getPrice());

            if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(getResources().getColor(R.color.red));
//                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(getResources().getColor(R.color.black));
//                price.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }
}
