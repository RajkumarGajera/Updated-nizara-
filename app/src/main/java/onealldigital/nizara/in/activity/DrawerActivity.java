package onealldigital.nizara.in.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.AddressListFragment;
import onealldigital.nizara.in.fragment.CartFragment;
import onealldigital.nizara.in.fragment.FaqFragment;
import onealldigital.nizara.in.fragment.NotificationFragment;
import onealldigital.nizara.in.fragment.ProfileFragment;
import onealldigital.nizara.in.fragment.ReferEarnFragment;
import onealldigital.nizara.in.fragment.TrackOrderFragment;
import onealldigital.nizara.in.fragment.TransactionFragment;
import onealldigital.nizara.in.fragment.WalletTransactionFragment;
import onealldigital.nizara.in.fragment.WebViewFragment;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.Utils;
import onealldigital.nizara.in.ui.CircleTransform;

@SuppressLint("StaticFieldLeak")
public class DrawerActivity extends AppCompatActivity {
    static final float END_SCALE = 0.7f;
    public static TextView tvName, tvWallet;
    public static DrawerLayout drawer_layout;
    public static ImageView imgProfile;
    public static NavigationView navigationView;
    public ActionBarDrawerToggle drawerToggle;
    public TextView tvMobile;
    protected FrameLayout frameLayout;
    Session session;
    LinearLayout lytProfile;
    Activity activity;
    private ImageView imgEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawer_layout = findViewById(R.id.drawer_layout);
        try {
            View header = navigationView.getHeaderView(0);
            tvWallet = header.findViewById(R.id.tvWallet);
            tvName = header.findViewById(R.id.header_name);
            tvMobile = header.findViewById(R.id.tvMobile);
            lytProfile = header.findViewById(R.id.lytProfile);
            imgProfile = header.findViewById(R.id.imgProfile);
            imgEditProfile = header.findViewById(R.id.btn_edt_profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        activity = DrawerActivity.this;
        session = new Session(activity);

        animateNavigationDrawer();

        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            tvName.setText(session.getData(Constant.NAME));
            tvMobile.setText(session.getData(Constant.MOBILE));
            tvWallet.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(session.getData(Constant.PROFILE))
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .transform(new CircleTransform())
                    .into(imgProfile);

            ApiConfig.getWalletBalance(activity, session);

        } else {
            tvWallet.setVisibility(View.GONE);
            tvName.setText(getResources().getString(R.string.is_login));
            tvMobile.setText(getResources().getString(R.string.is_mobile));
            Picasso.get()
                    .load("-")
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .transform(new CircleTransform())
                    .into(imgProfile);
        }

        lytProfile.setOnClickListener(v -> {
            onEditProfile();
        });

        imgEditProfile.setOnClickListener(v -> onEditProfile());

        setupNavigationDrawer();
    }

    private void onEditProfile(){
        drawer_layout.closeDrawers();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            MainActivity.fm.beginTransaction().add(R.id.container, new ProfileFragment()).addToBackStack(null).commit();
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra(Constant.FROM, "drawer"));
        }
    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawer_layout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                frameLayout.setScaleX(offsetScale);
                frameLayout.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = frameLayout.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                frameLayout.setTranslationX(xTranslation);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    void setupNavigationDrawer() {
        Menu nav_Menu = navigationView.getMenu();

        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
            nav_Menu.setGroupVisible(R.id.group1, true);
            nav_Menu.setGroupVisible(R.id.group2, true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
            nav_Menu.setGroupVisible(R.id.group1, false);
            nav_Menu.setGroupVisible(R.id.group2, false);
        }

        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            if (session.getData(Constant.is_refer_earn_on).equals("0")) {
                nav_Menu.findItem(R.id.menu_refer).setVisible(false);
            }
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawer_layout.closeDrawers();
            Fragment fragment;
            Bundle bundle;
            switch (menuItem.getItemId()) {
                case R.id.menu_transaction_history:
                    fragment = new TransactionFragment();
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_wallet_history:
                    fragment = new WalletTransactionFragment();
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_notifications:
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new NotificationFragment()).addToBackStack(null).commit();
                    break;
                case R.id.menu_faq:
                    getSupportFragmentManager().beginTransaction().add(R.id.container, new FaqFragment()).addToBackStack(null).commit();
                    break;
                case R.id.menu_terms:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Terms & Conditions");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_contact:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Contact Us");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_about_us:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "About Us");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_privacy:
                    fragment = new WebViewFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Privacy Policy");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_home:
                    MainActivity.homeClicked = false;
                    MainActivity.categoryClicked = false;
                    MainActivity.favoriteClicked = false;
                    MainActivity.trackingClicked = false;
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.FROM, "");
                    startActivity(intent);
                    finish();
                    break;
                case R.id.menu_tracker:
                    fragment = new TrackOrderFragment();
                    bundle = new Bundle();
                    bundle.putString("type", "Your Order");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
//                    startActivity(new Intent(activity, MainActivity.class).putExtra(Constant.FROM, "tracker").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.menu_refer:
                    if (session.getBoolean(Constant.IS_USER_LOGIN))
                        getSupportFragmentManager().beginTransaction().add(R.id.container, new ReferEarnFragment()).addToBackStack(null).commit();
                    else
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    break;
                case R.id.menu_manage_address:
                    fragment = new AddressListFragment();
                    bundle = new Bundle();
                    bundle.putString(Constant.FROM, "MainActivity");
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_cart:
                    fragment = new CartFragment();
                    bundle = new Bundle();
                    bundle.putString(Constant.FROM, "mainActivity");
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    break;
                case R.id.menu_change_pass:
                    OpenBottomDialog(activity);
                    break;
                case R.id.menu_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.take_a_look) + "\"" + getString(R.string.app_name) + "\" - " + Constant.PLAY_STORE_LINK + getPackageName());
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                    break;
                case R.id.menu_rate:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_RATE_US_LINK + this.getPackageName())));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + this.getPackageName())));
                    }
                    break;
                case R.id.menu_logout:
                    session.logoutUserConfirmation(activity);
                    break;
            }
            return true;
        });
    }

    public void OpenBottomDialog(final Activity activity) {
        try {
            View sheetView = activity.getLayoutInflater().inflate(R.layout.dialog_change_password, null);
            ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }

            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            EditText edtoldpsw = sheetView.findViewById(R.id.edtoldpsw);
            EditText edtnewpsw = sheetView.findViewById(R.id.edtnewpsw);
            EditText edtcnewpsw = sheetView.findViewById(R.id.edtcnewpsw);
            ImageView imgChangePasswordClose = sheetView.findViewById(R.id.imgChangePasswordClose);
            Button btnchangepsw = sheetView.findViewById(R.id.btnchangepsw);

            edtoldpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);
            edtnewpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);
            edtcnewpsw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);

            Utils.setHideShowPassword(edtoldpsw);
            Utils.setHideShowPassword(edtnewpsw);
            Utils.setHideShowPassword(edtcnewpsw);
            mBottomSheetDialog.setCancelable(true);


            imgChangePasswordClose.setOnClickListener(v -> mBottomSheetDialog.dismiss());

            btnchangepsw.setOnClickListener(view -> {
                String oldpsw = edtoldpsw.getText().toString();
                String password = edtnewpsw.getText().toString();
                String cpassword = edtcnewpsw.getText().toString();

                if (!password.equals(cpassword)) {
                    edtcnewpsw.requestFocus();
                    edtcnewpsw.setError(activity.getString(R.string.pass_not_match));
                } else if (ApiConfig.CheckValidattion(oldpsw, false, false)) {
                    edtoldpsw.requestFocus();
                    edtoldpsw.setError(activity.getString(R.string.enter_old_pass));
                } else if (ApiConfig.CheckValidattion(password, false, false)) {
                    edtnewpsw.requestFocus();
                    edtnewpsw.setError(activity.getString(R.string.enter_new_pass));
                } else if (!oldpsw.equals(new Session(activity).getData(Constant.PASSWORD))) {
                    edtoldpsw.requestFocus();
                    edtoldpsw.setError(activity.getString(R.string.no_match_old_pass));
                } else if (ApiConfig.isConnected(activity)) {
                    ChangePassword(password);
                }
            });

            mBottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ChangePassword(String password) {
        final Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.CHANGE_PASSWORD);
        params.put(Constant.PASSWORD, password);
        params.put(Constant.USER_ID, session.getData(Constant.ID));

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setTitle(getString(R.string.change_pass));
        alertDialog.setMessage(getString(R.string.reset_alert_msg));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> ApiConfig.RequestToVolley((result, response) -> {
            //  System.out.println("=================*changepsw " + response);
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(Constant.ERROR)) {
                        session.logoutUser(activity);
                    }
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.REGISTER_URL, params, true));
        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
