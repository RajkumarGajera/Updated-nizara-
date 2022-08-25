package onealldigital.nizara.in.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.fragment.TrackerDetailFragment;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.OrderTracker;


public class TrackerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OrderTracker> orderTrackerArrayList;
    final Context context;
    public boolean isLoading;


    public TrackerAdapter(Context context, Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.context = context;
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    public void add(int position, OrderTracker item) {
        orderTrackerArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_trackorder, parent, false);
            return new TrackerHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof TrackerHolderItems) {
            final TrackerHolderItems holder = (TrackerHolderItems) holderparent;
            final OrderTracker order = orderTrackerArrayList.get(position);
//            System.out.println(activity.getString(R.string.order_status));
            holder.txtorderid.setText(activity.getString(R.string.order_number) + order.getId());
            String[] date = order.getDate_added().split("\\s+");
            holder.txtorderdate.setText(date[0]);
            holder.txtorderamount.setText(new Session(context).getData(Constant.CURRENCY) + ApiConfig.StringFormat(order.getFinal_total()));
            Picasso.get().load(order.getItemsList().get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgOrder);
            Log.d("orderImg", "onBindViewHolder: "+order.getImage());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new TrackerDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", "");
                    bundle.putSerializable("model", order);
                    fragment.setArguments(bundle);
                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });

            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < order.getItemsList().size(); i++) {
                items.add(order.getItemsList().get(i).getName());
            }
//            holder.tvItems.setText(Arrays.toString(items.toArray()).replace("]", "").replace("[", ""));
//            holder.tvTotalItems.setText(items.size() + activity.getString(R.string.item));

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderTrackerArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public class TrackerHolderItems extends RecyclerView.ViewHolder {
        private final TextView txtorderid;
        private final TextView txtorderdate;
        private final TextView txtorderamount;
        private final ImageView imgOrder;
//        final TextView carddetail;
//        final TextView tvTotalItems;
//        final TextView tvItems;
//        final CardView cardView;

        public TrackerHolderItems(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            txtorderamount = itemView.findViewById(R.id.txtorderamount);
            imgOrder = itemView.findViewById(R.id.img_order);

            imgOrder.setClipToOutline(true);
//            carddetail = itemView.findViewById(R.id.carddetail);
//            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
//            tvItems = itemView.findViewById(R.id.tvItems);
//            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}