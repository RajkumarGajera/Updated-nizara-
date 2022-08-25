package onealldigital.nizara.in.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import onealldigital.nizara.in.fragment.CurrentOrderFragment;
import onealldigital.nizara.in.fragment.OrderHistoryFragment;

public class TrackOrderAdapter extends FragmentStateAdapter {

    public TrackOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0)
            fragment = new CurrentOrderFragment();
        else
            fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
