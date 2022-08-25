package onealldigital.nizara.in.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.fragment.ProductDetailFragment;
import onealldigital.nizara.in.fragment.revire_recycle;
import onealldigital.nizara.in.model.Reviews;


public class seeallreviewAdapter extends RecyclerView.Adapter<seeallreviewAdapter.ReviewHolder>{

    int lyt_reviews;
    Activity activity;
    Context context;
    ArrayList<Reviews> list1;
    String sub_review;
    revire_recycle revire_recycle;

    public seeallreviewAdapter(Context context, ArrayList<Reviews> list) {
        this.context=context;
        this.list1=list;
    }

    public seeallreviewAdapter(Context context, Activity activity, ArrayList<Reviews> list, int lyt_reviews, String sub_review, revire_recycle revire_recycle) {
        this.context=context;
        this.list1=list;
        this.activity=activity;
        this.lyt_reviews=lyt_reviews;
        this.sub_review=sub_review;
        this.revire_recycle=revire_recycle;
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Reviews reviews = list1.get(position);
        holder.tvReviewerName.setText(reviews.getname());
        holder.tvReview.setText(reviews.getRe());
        holder.reviewRating.setRating(reviews.getRate());
    }

    @Override
    public int getItemCount() {
        return list1.size();
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_reviews, parent, false);
        return new ReviewHolder(view);
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        private final TextView tvReviewerName;
        private final TextView tvReview;
        private final RatingBar reviewRating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            tvReviewerName = itemView.findViewById(R.id.tv_reviewer_name);
            tvReview = itemView.findViewById(R.id.tv_review);
            reviewRating = itemView.findViewById(R.id.review_rating);
        }
    }
}
