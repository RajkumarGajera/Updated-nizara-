package onealldigital.nizara.in.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.adapter.ProductLoadMoreAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Product;


public class SearchFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ProductLoadMoreAdapter productAdapter;
    public ProgressBar progressBar;
    View root;
    RecyclerView recyclerView;
    TextView noResult, msg;
    Session session;
    Activity activity;
    boolean isGrid = false;
    int resource;
    ListView listView;
    EditText searchview;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        activity = getActivity();

        setHasOptionsMenu(true);

        recyclerView = root.findViewById(R.id.recyclerView);
        productArrayList = new ArrayList<>();
        noResult = root.findViewById(R.id.noResult);
        msg = root.findViewById(R.id.msg);
        progressBar = root.findViewById(R.id.pBar);
        listView = root.findViewById(R.id.listView);
        progressBar.setVisibility(View.GONE);
        session = new Session(getContext());
        searchview = root.findViewById(R.id.searchviews);


        Constant.CartValues = new HashMap<>();
        String[] productsName = session.getData(Constant.GET_ALL_PRODUCTS_NAME).split(",");
        searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(productsName)));
        listView.setAdapter(arrayAdapter);


        searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                arrayAdapter.getFilter().filter(searchview.getText().toString().trim());
                if (searchview.getText().toString().trim().length() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close, 0);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchRequest(v.getText().toString().trim());

                return false;
            }
        });

        searchview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                showSoftKeyboard(searchview);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchview.getText().toString().trim().length() > 0) {
                        if (event.getRawX() >= (searchview.getRight() - searchview.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_close_, 0);
                            searchview.setText("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchview.setText(arrayAdapter.getItem(position));
                listView.setVisibility(View.VISIBLE);
                SearchRequest(arrayAdapter.getItem(position));
            }
        });



        recyclerView = root.findViewById(R.id.recyclerView);

        if (session.getGrid("grid")) {
            resource = R.layout.lyt_item_grid;
            isGrid = true;
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));

        } else {
            resource = R.layout.lyt_item_list;
            isGrid = false;
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }

        return root;
    }

    public void SearchRequest(final String query) {  //json request for product search
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        hideKeyboard();
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }
        params.put(Constant.SEARCH, query);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        productArrayList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            productAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, "search");
                            recyclerView.setAdapter(productAdapter);
                            noResult.setVisibility(View.GONE);
                            msg.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            noResult.setVisibility(View.VISIBLE);
                            msg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            productArrayList.clear();
                            recyclerView.setAdapter(new ProductLoadMoreAdapter(activity, productArrayList, resource, "search"));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
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
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.search);
        activity.invalidateOptionsMenu();
        searchview.requestFocus();
        showSoftKeyboard(searchview);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constant.CartValues.size() > 0) {
            ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
        }
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
}