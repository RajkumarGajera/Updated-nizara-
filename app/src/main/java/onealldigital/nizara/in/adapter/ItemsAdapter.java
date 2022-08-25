package onealldigital.nizara.in.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.fragment.TrackerDetailFragment;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.helper.VolleyCallback;
import onealldigital.nizara.in.model.OrderTracker;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CartItemHolder> {

    final Activity activity;
    final ArrayList<OrderTracker> orderTrackerArrayList;
    final Session session;
    String from = "";

    public ItemsAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList, String from) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
        this.from = from;
        session = new Session(activity);
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_items, null);
        return new CartItemHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final CartItemHolder holder, final int position) {

        final OrderTracker order = orderTrackerArrayList.get(position);

        ApiConfig.setOrderTrackerLayout(activity, order, holder);

        String payType = "";
        if (order.getPayment_method().equalsIgnoreCase("cod"))
            payType = activity.getResources().getString(R.string.cod);
        else
            payType = order.getPayment_method();
        holder.txtqty.setText(order.getQuantity());

        String taxPercentage = order.getTax_percent();
        double price;

        if (order.activeStatus.equals(Constant.CANCELLED) || order.activeStatus.equals(Constant.RETURNED)) {
            holder.lyttracker.setVisibility(View.GONE);
            if (order.activeStatus.equals(Constant.CANCELLED)) {
                holder.txtstatus.setVisibility(View.VISIBLE);
                holder.txtstatus.setText(activity.getString(R.string.cancelled));
            } else if (order.activeStatus.equals(Constant.RETURNED)) {
                holder.txtstatus.setVisibility(View.VISIBLE);
                holder.txtstatus.setText(activity.getString(R.string.returned));
            } else {
                holder.txtstatus.setVisibility(View.GONE);
            }
        } else {
            holder.lyttracker.setVisibility(View.VISIBLE);
        }

        if (order.cancelable_status.equals("1")) {
            if (order.till_status.equals(Constant.RECEIVED) && order.activeStatus.equals(Constant.RECEIVED)) {
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else if (order.till_status.equals(Constant.PROCESSED) && (order.activeStatus.equals(Constant.RECEIVED) || order.activeStatus.equals(Constant.PROCESSED))) {
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else if (order.till_status.equals(Constant.SHIPPED) && (order.activeStatus.equals(Constant.RECEIVED) || order.activeStatus.equals(Constant.PROCESSED) || order.activeStatus.equals(Constant.SHIPPED))) {
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else {
                holder.btnCancel.setVisibility(View.GONE);
            }
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        if (order.return_status.equals("1")) {
            if (order.activeStatus.equals(Constant.DELIVERED)) {
                holder.btnReturn.setVisibility(View.VISIBLE);
            }
        } else {
            holder.btnReturn.setVisibility(View.GONE);
        }

        if (order.getDiscounted_price().equals("0") || order.getDiscounted_price().equals("")) {
            price = (((Float.parseFloat(order.getPrice()) + ((Float.parseFloat(order.getPrice()) * Float.parseFloat(taxPercentage)) / 100))) * Integer.parseInt(order.getQuantity()));
        } else {
            price = (((Float.parseFloat(order.getDiscounted_price()) + ((Float.parseFloat(order.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100))) * Integer.parseInt(order.getQuantity()));
        }
        holder.txtprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + price));

        holder.txtpaytype.setText(activity.getResources().getString(R.string.via) + payType);
        holder.txtname.setText(order.getName() + "(" + order.getMeasurement() + order.getUnit() + ")");

        Picasso.get().
                load(order.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgorder);

        holder.carddetail.setOnClickListener(new View.OnClickListener() {
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


        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus(activity, order, Constant.CANCELLED, holder, from, position);
            }
        });

        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                Date date = new Date();
                //System.out.println (myFormat.format (date));
                String inputString1 = order.getActiveStatusDate();
                String inputString2 = myFormat.format(date);
                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    //  System.out.println("Days: "+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= Integer.parseInt(order.getReturn_days())) {
                        updateOrderStatus(activity, order, Constant.RETURNED, holder, from, position);
                    } else {
                        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.product_return) + Integer.parseInt(new Session(activity).getData(Constant.max_product_return_days)) + activity.getString(R.string.day_max_limit), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(activity.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();

                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        View snackbarView = snackbar.getView();
                        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
                        textView.setMaxLines(5);
                        snackbar.show();

                    }
                } catch (ParseException e) {

                }
            }
        });

    }

    private void updateOrderStatus(final Activity activity, final OrderTracker order, final String status, final CartItemHolder holder, final String from, int position) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        if (status.equals(Constant.CANCELLED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.cancel_item));
            alertDialog.setMessage(activity.getResources().getString(R.string.cancel_msg));
        } else if (status.equals(Constant.RETURNED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.return_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.return_msg));
        }
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Map<String, String> params = new HashMap<>();
                params.put(Constant.UPDATE_ORDER_STATUS, Constant.GetVal);
                params.put(Constant.ORDER_ID, order.getOrder_id());
                params.put(Constant.ORDER_ITEM_ID, order.getId());
                params.put(Constant.STATUS, status);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        // System.out.println("================= " + response);
                        if (result) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (!object.getBoolean(Constant.ERROR)) {
                                    if (status.equals(Constant.CANCELLED)) {
                                        holder.btnCancel.setVisibility(View.GONE);
                                        order.status = status;
                                        if (from.equals("detail")) {
                                            if (orderTrackerArrayList.size() == 1) {

                                            }
                                        }
                                        ApiConfig.getWalletBalance(activity, new Session(activity));
                                    } else {
                                        holder.btnReturn.setVisibility(View.GONE);
                                    }
                                    Constant.isOrderCancelled = true;
                                }
                                Toast.makeText(activity, object.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {

                            }
                        }
                    }
                }, activity, Constant.ORDERPROCESS_URL, params, true);

            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        final TextView txtqty;
        final TextView txtprice;
        final TextView txtpaytype;
        final TextView txtstatus;
        final TextView txtname;
        final ImageView imgorder;
        final CardView carddetail;
        final RecyclerView recyclerView;
        final Button btnCancel;
        final Button btnReturn;
        final LinearLayout lyttracker;

        public CartItemHolder(View itemView) {
            super(itemView);

            txtqty = itemView.findViewById(R.id.txtqty);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtpaytype = itemView.findViewById(R.id.txtpaytype);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            txtname = itemView.findViewById(R.id.txtname);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgorder = itemView.findViewById(R.id.imgorder);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            lyttracker = itemView.findViewById(R.id.lyttracker);
        }
    }

}
