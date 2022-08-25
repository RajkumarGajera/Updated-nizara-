package onealldigital.nizara.in.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.SubCategoryFragment;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements Filterable {
    public final ArrayList<Category> categorylist;
    public final ArrayList<Category> filteredCategoryList;
    final int layout;
    final Activity activity;
    final Context context;
    final String from;
    final int visibleNumber;


    public CategoryAdapter(Context context, Activity activity, ArrayList<Category> categorylist, int layout, String from, int visibleNumber) {
        this.context = context;
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        this.visibleNumber = visibleNumber;
        filteredCategoryList = new ArrayList<>(categorylist);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = filteredCategoryList.get(position);
        holder.txttitle.setText(model.getName());

        Picasso.get()
                .load(model.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgcategory);

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, model.getId());
                bundle.putString(Constant.NAME, model.getName());
                bundle.putString(Constant.FROM, "category");
                fragment.setArguments(bundle);
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        int categories;
        if (filteredCategoryList.size() > visibleNumber && from.equals("home")) {
            categories = visibleNumber;
        } else {
            categories = filteredCategoryList.size();
        }
        return categories;
    }

    @Override
    public Filter getFilter() {
        return categoryFilter;
    }

    Filter categoryFilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
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
            filteredCategoryList.clear();
            filteredCategoryList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
}
