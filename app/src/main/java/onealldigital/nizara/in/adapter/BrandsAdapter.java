package onealldigital.nizara.in.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.model.Brand;
import onealldigital.nizara.in.interfaces.ClickHome;
import onealldigital.nizara.in.model.Category;
import onealldigital.nizara.in.model.getbrand;

public class BrandsAdapter extends PagerAdapter implements Filterable {

    Context context;
    Activity activity;
    LayoutInflater layoutInflater;
    ClickHome clickInterface;
    ArrayList<Brand> brands;
    ArrayList<Brand> brandList;
    ArrayList<Brand> filteredBrandList;
    int visibleNumber=0;
    int layout;
    String from="category";

    public BrandsAdapter(Context context, ArrayList<Brand> brands, ClickHome clickInterface) {
        this.context = context;
        this.brands = brands;
        this.clickInterface = clickInterface;
    }

    public BrandsAdapter(Context context, FragmentActivity activity, ArrayList<Brand> brandArrayList) {
        this.context = context;
        this.activity=activity;
        this.brandList=brandArrayList;

    }

    @Override
    public int getCount() {

        if(brands.size()%6 == 0){
            return brands.size()/6;
        }else {
            return brands.size()/6+1;
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (LinearLayout)o;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.brand_layut,container,false);
        ImageView image_slide_1 = view.findViewById(R.id.image_slide_1);
        ImageView image_slide_2 = view.findViewById(R.id.image_slide_2);
        ImageView image_slide_3 = view.findViewById(R.id.image_slide_3);
        ImageView image_slide_4 = view.findViewById(R.id.image_slide_4);
        ImageView image_slide_5 = view.findViewById(R.id.image_slide_5);
        ImageView image_slide_6 = view.findViewById(R.id.image_slide_6);

        CardView brand_1 = view.findViewById(R.id.brand_1);
        CardView brand_2 = view.findViewById(R.id.brand_2);
        CardView brand_3 = view.findViewById(R.id.brand_3);
        CardView brand_4 = view.findViewById(R.id.brand_4);
        CardView brand_5 = view.findViewById(R.id.brand_5);
        CardView brand_6 = view.findViewById(R.id.brand_6);

        int currentBrandNum = Math.min(brands.size() - (position) * 6, 6);

        if (currentBrandNum < 6) brand_6.setVisibility(View.INVISIBLE);
        if (currentBrandNum < 5) brand_5.setVisibility(View.INVISIBLE);
        if (currentBrandNum < 4) brand_4.setVisibility(View.INVISIBLE);
        if (currentBrandNum < 3) brand_3.setVisibility(View.INVISIBLE);
        if (currentBrandNum < 2) brand_2.setVisibility(View.INVISIBLE);
        if (currentBrandNum < 1) brand_1.setVisibility(View.INVISIBLE);


        if(currentBrandNum > 0) {
            Picasso.get().load(brands.get(position * 6).getImage()).into(image_slide_1);
        }
        if (currentBrandNum > 1) {
            Picasso.get().load(brands.get(position*6+1).getImage()).into(image_slide_2);
        }

        if (currentBrandNum > 2) {
            Picasso.get().load(brands.get(position*6+2).getImage()).into(image_slide_3);
        }

        if (currentBrandNum > 3) {
            Picasso.get().load(brands.get(position*6+3).getImage()).into(image_slide_4);
        }

        if (currentBrandNum > 4) {
            Picasso.get().load(brands.get(position*6+4).getImage()).into(image_slide_5);
        }

        if (currentBrandNum > 5) {
            Picasso.get().load(brands.get(position*6+5).getImage()).into(image_slide_6);

        }

        brand_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    clickInterface.onClickBrand(brands.get(position*6));
                }
            }
        });

        brand_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    clickInterface.onClickBrand(brands.get(position*6 +1));
                }
            }
        });

        brand_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    clickInterface.onClickBrand(brands.get(position*6 + 2));
                }
            }
        });

        brand_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    clickInterface.onClickBrand(brands.get(position*6 + 3));
                }
            }
        });

        brand_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    clickInterface.onClickBrand(brands.get(position*6 + 4));
                }
            }
        });

        brand_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position*6 + currentBrandNum <= brands.size())
                {
                    getbrand getbrand = new getbrand(brands.get(position*6+5).getId().toString());
                    getbrand.setBrand_ID(brands.get(position*6+5).getId().toString());
                    clickInterface.onClickBrand(brands.get(position*6 + 5));
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object );
    }

//    @Override
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
        return Brandfilter;
    }

    Filter Brandfilter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Brand> filteredList = new ArrayList<>();

            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                filteredList.addAll(brands);
            } else {
                for (Brand brand : brands){
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

}
