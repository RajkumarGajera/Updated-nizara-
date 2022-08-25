package onealldigital.nizara.in.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.CartFragment;
import onealldigital.nizara.in.helper.ApiConfig;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.helper.DatabaseHelper;
import onealldigital.nizara.in.helper.Session;
import onealldigital.nizara.in.model.OfflineCart;


public class OfflineCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    final ArrayList<OfflineCart> items;
    final DatabaseHelper databaseHelper;
    final Session session;
    final Context context;
    public boolean isLoading;


    public OfflineCartAdapter(Context context, Activity activity, ArrayList<OfflineCart> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        databaseHelper = new DatabaseHelper(activity);
        session = new Session(context);
    }

    public void add(int position, OfflineCart item) {
        if (position != (getItemCount() + 1)) {
            items.add(position, item);
        } else {
            items.add(item);
        }
        notifyItemInserted(position);
    }

    public void removeItem(int position) {

        OfflineCart cart = items.get(position);

        databaseHelper.AddOrderData(items.get(position).getId(), cart.getProduct_id(), "0");
        items.remove(cart);
        showUndoSnackbar(cart, position);
        notifyDataSetChanged();
        CartFragment.SetData();
        Constant.FLOAT_TOTAL_AMOUNT = 0.00;
        databaseHelper.getTotalItemOfCart(activity);
        activity.invalidateOptionsMenu();
        if (getItemCount() == 0) {
            CartFragment.lytempty.setVisibility(View.VISIBLE);
            CartFragment.lytTotal.setVisibility(View.GONE);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.lyt_cartlist, parent, false);
            return new ProductHolderItems(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ProductHolderItems) {
            final ProductHolderItems holder = (ProductHolderItems) holderparent;
            final OfflineCart cart = items.get(position);

            Picasso.get()
                    .load(cart.getItem().get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgproduct);

            holder.txtproductname.setText(cart.getItem().get(0).getName());
            holder.txtmeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());
            double price, oPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(cart.getTax_percentage()) > 0 ? cart.getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!cart.getItem().get(0).getServe_for().equalsIgnoreCase("available")) {
                holder.txtstatus.setVisibility(View.VISIBLE);
                holder.txtstatus.setText(activity.getString(R.string.sold_out));
                holder.lytqty.setVisibility(View.GONE);
                CartFragment.isSoldOut = true;
            } else {
                holder.txtstatus.setVisibility(View.GONE);
            }

            if (Boolean.parseBoolean(cart.getItem().get(0).getIs_item_deliverable())) {
                holder.txtDeliveryStatus.setVisibility(View.VISIBLE);
                holder.txtDeliveryStatus.setText(activity.getString(R.string.msg_non_deliverable_to) + session.getData(Constant.GET_SELECTED_PINCODE_NAME));
                CartFragment.isDeliverable = true;
            } else {
                holder.txtDeliveryStatus.setVisibility(View.GONE);
            }

            holder.txtprice.setText(new Session(activity).getData(Constant.CURRENCY) + (cart.getDiscounted_price().equals("0") ? cart.getPrice() : cart.getDiscounted_price()));
            holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                }
            });
            if (cart.getDiscounted_price().equals("0") || cart.getDiscounted_price().equals("")) {
                price = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                price = ((Float.parseFloat(cart.getDiscounted_price()) + ((Float.parseFloat(cart.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                oPrice = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                holder.txtoriginalprice.setPaintFlags(holder.txtoriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtoriginalprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + oPrice));
            }
            holder.txtprice.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + price));

            holder.txtproductname.setText(cart.getItem().get(0).getName());
            holder.txtmeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());

            holder.txtQuantity.setText(databaseHelper.CheckOrderExists(items.get(position).getId(), cart.getProduct_id()));
            cart.getItem().get(0).setCart_count(databaseHelper.CheckOrderExists(items.get(position).getId(), cart.getProduct_id()));

            holder.txttotalprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + price * Integer.parseInt(databaseHelper.CheckOrderExists(items.get(position).getId(), cart.getProduct_id()))));

            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + (price * Integer.parseInt(databaseHelper.CheckOrderExists(items.get(position).getId(), cart.getProduct_id())));
            CartFragment.SetData();

            final double finalPrice = price;
            holder.btnaddqty.setOnClickListener(view -> {
                if (ApiConfig.isConnected(activity)) {
                    if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) >= Float.parseFloat(cart.getItem().get(0).getStock()))) {
                        if (!(Integer.parseInt(holder.txtQuantity.getText().toString()) + 1 > Integer.parseInt(session.getData(Constant.max_cart_items_count)))) {
                            int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                            count++;
                            cart.getItem().get(0).setCart_count("" + count);
                            holder.txtQuantity.setText("" + count);
                            holder.txttotalprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + finalPrice * count));
                            Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT + finalPrice;
                            databaseHelper.AddOrderData(items.get(position).getId(), cart.getProduct_id(), "" + count);
                            CartFragment.SetData();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                }

            });

            holder.btnminusqty.setOnClickListener(view -> {
                if (ApiConfig.isConnected(activity)) {
                    if (Integer.parseInt(holder.txtQuantity.getText().toString()) > 1) {
                        int count = Integer.parseInt(holder.txtQuantity.getText().toString());
                        count--;
                        cart.getItem().get(0).setCart_count("" + count);
                        holder.txtQuantity.setText("" + count);
                        holder.txttotalprice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + finalPrice * count));
                        Constant.FLOAT_TOTAL_AMOUNT = Constant.FLOAT_TOTAL_AMOUNT - finalPrice;
                        databaseHelper.AddOrderData(items.get(position).getId(), cart.getProduct_id(), "" + count);
                        CartFragment.SetData();
                    }
                }
            });

            if (getItemCount() == 0) {
                CartFragment.lytempty.setVisibility(View.VISIBLE);
                CartFragment.lytTotal.setVisibility(View.GONE);
            } else {
                CartFragment.lytempty.setVisibility(View.GONE);
                CartFragment.lytTotal.setVisibility(View.VISIBLE);
            }

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        OfflineCart cart = items.get(position);
        if (cart != null)
            return Integer.parseInt(items.get(position).getId());
        else
            return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public static class ProductHolderItems extends RecyclerView.ViewHolder {
        final ImageView imgproduct;
        final ImageView btnminusqty;
        final ImageView btnaddqty;
        final ImageView imgRemove;
        final TextView txtproductname;
        final TextView txtmeasurement;
        final TextView txtprice;
        final TextView txtoriginalprice;
        final TextView txtQuantity;
        final TextView txttotalprice;
        final TextView txtstatus;
        final TextView txtDeliveryStatus;
        final LinearLayout lytqty;

        public ProductHolderItems(@NonNull View itemView) {
            super(itemView);
            imgproduct = itemView.findViewById(R.id.imgproduct);
            imgRemove = itemView.findViewById(R.id.imgRemove);

            btnminusqty = itemView.findViewById(R.id.btnminusqty);
            btnaddqty = itemView.findViewById(R.id.btnaddqty);

            txtproductname = itemView.findViewById(R.id.txtproductname);
            txtmeasurement = itemView.findViewById(R.id.txtmeasurement);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtoriginalprice = itemView.findViewById(R.id.txtoriginalprice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txttotalprice = itemView.findViewById(R.id.txttotalprice);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            lytqty = itemView.findViewById(R.id.lytqty);
            txtDeliveryStatus = itemView.findViewById(R.id.txtDeliveryStatus);
        }
    }

    void showUndoSnackbar(OfflineCart cart, int position) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.undo_message), Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(activity, R.color.gray));
        snackbar.setAction(activity.getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();

                Constant.FLOAT_TOTAL_AMOUNT = 0.00;

                databaseHelper.AddOrderData(cart.getId(), cart.getItem().get(0).getProduct_id(), cart.getItem().get(0).getCart_count());

                add(position, cart);

                notifyDataSetChanged();
                CartFragment.SetData();
                CartFragment.isSoldOut = false;
                Constant.TOTAL_CART_ITEM = getItemCount();
                CartFragment.values.put(cart.getId(), databaseHelper.CheckOrderExists(cart.getId(), cart.getItem().get(0).getProduct_id()));
                CartFragment.SetData();
                activity.invalidateOptionsMenu();

            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }
}