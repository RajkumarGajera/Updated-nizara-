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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.BrandProductsFragment;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.model.Brand;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ViewHolder> implements Filterable {
    public final ArrayList<Brand> brandList;
    public final ArrayList<Brand> filteredBrandList;
    final int layout;
    final Activity activity;
    final Context context;
    final String from;
    final int visibleNumber;
//    private DisplayMetrics displayMetrics;
//    private int screenWidth = 0;

    public SellerAdapter(Context context, Activity activity, ArrayList<Brand> brandList, int layout, String from, int visibleNumber) {
        this.context = context;
        this.brandList = brandList;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        this.visibleNumber = visibleNumber;
        filteredBrandList = new ArrayList<>(brandList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
//        context.getWindowManager().defaultDisplay.getMetrics(displayMetrics);
//        screenWidth = displayMetrics.widthPixels;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Brand model = filteredBrandList.get(position);
//        holder.txttitle.setText(model.getName());

        Picasso.get()
                .load(model.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgSeller);

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BrandProductsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ID, model.getId());
                bundle.putString(Constant.TITLE, model.getName());
                bundle.putString(Constant.FROM, "Seller");
                bundle.putString("image", model.getImage());
                fragment.setArguments(bundle);
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        int categories;
        if (filteredBrandList.size() > visibleNumber && from.equals("home")) {
            categories = visibleNumber;
        } else {
            categories = filteredBrandList.size();
        }
        return categories;
    }

    @Override
    public Filter getFilter() {
        return sellerFilter;
    }

    Filter sellerFilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Brand> filteredList = new ArrayList<>();

            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                filteredList.addAll(brandList);
            } else {
                for (Brand brand : brandList){
                    if (brand.getName().toLowerCase().contains(charString)){
                        filteredList.add(brand);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredBrandList.clear();
            filteredBrandList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

//        public final TextView txttitle;
        final ImageView imgSeller;
        final CardView lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgSeller = itemView.findViewById(R.id.imgSeller);
//            txttitle = itemView.findViewById(R.id.txttitle);

        }

    }
}
