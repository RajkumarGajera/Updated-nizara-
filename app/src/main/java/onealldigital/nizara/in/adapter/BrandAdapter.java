package onealldigital.nizara.in.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.interfaces.OnSubCatClick;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.Product;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> implements Filterable {
    public final ArrayList<Category> categorylist;
    public final ArrayList<Category> filteredSubCatList;
    final int layout;
    final Activity activity;
    final Context context;
    String from = "";
    private final OnSubCatClick listener;
    private int selectedItem = -1;


    public BrandAdapter(Context context, Activity activity, ArrayList<Category> categorylist, int layout,
                        String from, OnSubCatClick listener) {
        this.context = context;
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        filteredSubCatList = new ArrayList<>(categorylist);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = filteredSubCatList.get(position);
        Product model1 = new Product();
        holder.txttitle.setText(model.getName());

        Picasso.get()
                .load(model.getImage())
                .fit()
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into(holder.imgcategory);

        if (selectedItem == position) {
            holder.lytMain.setAlpha(1);
        } else if (selectedItem != -1){
            holder.lytMain.setAlpha(0.7f);
        }

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                listener.onSubCatClick(model.getId(), model.getName(), from);
                selectedItem = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredSubCatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView txttitle;
        final ImageView imgcategory;
        final LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgcategory = itemView.findViewById(R.id.imgcategory);
            txttitle = itemView.findViewById(R.id.txttitle);
        }

    }

    @Override
    public Filter getFilter() {
        return categoryFilter;
    }

    Filter categoryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Category> filteredList = new ArrayList<>();

            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                filteredList.addAll(categorylist);
            } else {
                for (Category category : categorylist){
                    if (category.getName().toLowerCase().contains(charString)){
                        filteredList.add(category);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredSubCatList.clear();
            filteredSubCatList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
