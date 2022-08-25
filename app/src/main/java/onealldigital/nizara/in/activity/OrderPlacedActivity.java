package onealldigital.nizara.in.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.CartFragment;
import onealldigital.nizara.in.fragment.HomeFragment;
import onealldigital.nizara.in.fragment.NotificationFragment;
import onealldigital.nizara.in.fragment.SearchFragment;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.InputStreamVolleyRequest;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;

public class OrderPlacedActivity extends AppCompatActivity implements Response.Listener<byte[]>, Response.ErrorListener {

    Activity activity;
    ProgressBar progressBar;
//    Button btnShopping, btnSummary;
    LottieAnimationView lottieAnimationView;
    private TextView tvOrderId;
    TextView bill;
    private Menu menu;
    Session session;
    InputStreamVolleyRequest request;
    String orderId;
    int count;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        tvOrderId = findViewById(R.id.tv_order_id);
        bill = findViewById(R.id.bill);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey(Constant.ORDERID)) {

            Intent intent = getIntent();
            orderId = intent.getStringExtra(Constant.ORDERID);

            tvOrderId.setText("ORDER ID - "+orderId);
        }

        activity = this;
        Session session = new Session(activity);
        progressBar = findViewById(R.id.progressBar);
//        btnShopping = findViewById(R.id.btnShopping);
//        btnSummary = findViewById(R.id.btnSummary);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
//        setHasOptionsMenu(true);

        RemoveAllItemFromCart(activity, session);

        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,MainActivity.class);
                startActivity(intent);
            }
        });

        Map<String, String> params = new HashMap<>();
        params.put(Constant.REMOVE_FROM_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUrl=Constant.Invoice + "?id=" + orderId;
                System.out.println(mUrl);
                request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, OrderPlacedActivity.this, OrderPlacedActivity.this, null);
                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                        new HurlStack());
                mRequestQueue.add(request);
                System.out.println(mRequestQueue);
            };
        });

    }

    public void onResponse(byte[] response) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        System.out.println(response);
        try {
            if (response!=null) {

                //Read file name from headers
//                String content =request.responseHeaders.get("Content-Disposition")
//                        .toString();
//
//                StringTokenizer st = new StringTokenizer(content, "=");
//                String[] arrTag = st.toarray();

                String filename = orderId;
                filename = filename.replace(":", ".");
                Log.d("DEBUG::RESUME FILE NAME", filename);

                try{
                    long lenghtOfFile = response.length;

                    //covert reponse to input stream
                    InputStream input = new ByteArrayInputStream(response);
                    File path = new File("/emulated/0/Android/data/{package}/files/Download",filename);
//                    File file = new File(path, filename);
                    map.put("resume_path", path.toString());
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path));
                    byte data[] = new byte[100000000];

                    long total = 0;


                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    output.flush();

                    output.close();
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE. ERROR:: "+error.getMessage());
    }

    public void RemoveAllItemFromCart(final Activity activity, Session session) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.REMOVE_FROM_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            ApiConfig.getCartItemCount(activity, session);
                            Constant.CartValues.clear();
                            progressBar.setVisibility(View.GONE);
                            lottieAnimationView.playAnimation();

                            /*btnSummary.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity.homeClicked = false;
                                    MainActivity.categoryClicked = false;
                                    MainActivity.favoriteClicked = false;
                                    MainActivity.trackingClicked = false;
                                    MainActivity.active = null;
                                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "tracker").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            });

                            btnShopping.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                }
                            });*/

                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);

                    }
                }
            }
        }, activity, Constant.CART_URL, params, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
//        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_notification).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));

        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_cart:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("LOAD_FRAG", "CART");
                startActivity(intent);
                finish();
                break;
            case R.id.toolbar_notification:
                Intent intentNotify = new Intent(this, MainActivity.class);
                intentNotify.putExtra("LOAD_FRAG", "NOTIFICATION");
                startActivity(intentNotify);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MainActivity.toolbar.setVisibility(View.GONE);
        lottieAnimationView.setAnimation("confetti.json");
//        activity.invalidateOptionsMenu();
//        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

/*public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*@Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
    }*/

}