package onealldigital.nizara.in.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.fragment.ProductListFragment;
import onealldigital.nizara.in.helper.Constant;
import onealldigital.nizara.in.model.Category;


public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionHolder> {

    public final ArrayList<Category> sectionList;
    public final ArrayList<Category> filteredCategoryList;
    public final Activity activity;
    final Context context;
    int Counter=0;
    int co=0;
    String from="category";
    int visibleNumber = 0;

    public SectionAdapter(Context context, Activity activity, ArrayList<Category> sectionList) {
        this.context = context;
        this.activity = activity;
        this.sectionList = sectionList;
        filteredCategoryList = new ArrayList<>(sectionList);

    }

//    @Override
//    public int getItemCount() {
//        return sectionList.size();
//    }

    @Override
    public void onBindViewHolder(SectionHolder holder1, final int position) {

//        if(Counter<2) {
//            Counter++;
            final Category section;
            section = sectionList.get(position);
            holder1.tvTitle.setText(section.getName());
            holder1.tvSubTitle.setText(section.getSubtitle());
            holder1.tvSubTitle.setVisibility(View.GONE);

            switch (section.getStyle()) {
                case "style_1":
                    holder1.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    AdapterStyle1 adapter = new AdapterStyle1(context, activity, section.getProductList(), R.layout.offer_layout);
                    holder1.recyclerView.setAdapter(adapter);
                    break;
                case "style_2":
                    holder1.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    AdapterStyle2 adapterStyle2 = new AdapterStyle2(context, activity, section.getProductList());
                    holder1.recyclerView.setAdapter(adapterStyle2);
                    break;
                case "style_3":
                    holder1.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                    AdapterStyle1 adapter3 = new AdapterStyle1(context, activity, section.getProductList(), R.layout.lyt_style_3);
                    holder1.recyclerView.setAdapter(adapter3);
                    break;
            }
            holder1.tvMore.setText("See All >>");
            holder1.tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment fragment = new ProductListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.FROM, "section");
                    bundle.putString(Constant.NAME, section.getName());
                    bundle.putString(Constant.ID, section.getId());
                    fragment.setArguments(bundle);

                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
//        }else {
//        }
    }

    @Override
    public SectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_layout, parent, false);
        return new SectionHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    @NonNull
    public Filter getCategoryFilter() {
        return categoryFilter;
    }

    Filter categoryFilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Category> filteredList = new ArrayList<>();

            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                filteredList.addAll(sectionList);
            } else {
                for (Category category : sectionList){
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

    public class SectionHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvSubTitle;
        final TextView tvMore;
        final RecyclerView recyclerView;
        final RelativeLayout relativeLayout;

        public SectionHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvMore = itemView.findViewById(R.id.tvMore);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


}
