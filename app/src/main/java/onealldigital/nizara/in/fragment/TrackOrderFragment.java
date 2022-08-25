package onealldigital.nizara.in.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import org.greenrobot.eventbus.EventBus;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.DrawerActivity;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.TrackOrderAdapter;
import onealldigital.nizara.in.adapter.TrackerAdapter;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.interfaces.backpress;
import onealldigital.nizara.in.model.CurrentOrder;

public class TrackOrderFragment extends Fragment {

    private TabLayout tab;
    private ViewPager2 viewPager;
    private String[] tabTitles =  {"Current Order", "Order History"};
    private TrackOrderAdapter adapter;
    Activity activity;
    View view;
//    backpress listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_track_order, container, false);

        tab = view.findViewById(R.id.tabs_layout);
        viewPager = view.findViewById(R.id.view_pager);

        setHasOptionsMenu(true);

        adapter = new TrackOrderAdapter(getActivity());

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tab, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_sort).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_notification).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = "tracker";
//        Session.setCount(Constant.UNREAD_WALLET_COUNT, 0, getContext());
//        ApiConfig.updateNavItemCounter(DrawerActivity.navigationView, R.id.menu_wallet_history, Session.getCount(Constant.UNREAD_WALLET_COUNT, getContext()));
//        activity.invalidateOptionsMenu();
        MainActivity.showHideSearchBar(false);

    }


//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        Intent intent = new Intent(getContext(),MainActivity.class);
//        startActivity(intent);
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Intent intent = new Intent(getContext(),MainActivity.class);
//        startActivity(intent);
//    }
        @Override
        public void onPause() {
//            Intent intent = new Intent(getContext(),MainActivity.class);
//            getActivity().startActivity(intent);
//            listener=null;
            super.onPause();
        }

//    @Override
//    public void onBackPressed() {
//
//    }
}
//class backpress extends AppCompatActivity{
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//}