package onealldigital.nizara.in.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import onealldigital.nizara.in.R;
import onealldigital.nizara.in.adapter.ReviewAdapter;
import onealldigital.nizara.in.adapter.seeallreviewAdapter;
import onealldigital.nizara.in.model.Product;
import onealldigital.nizara.in.model.Reviews;

public class revire_recycle extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Reviews> list;
    seeallreviewAdapter seeallreviewAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    View root;
    Activity activity;

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.recycleview, container, false);
//
//
//        recyclerView = root.findViewById(R.id.recyccleview);
//        list = new ArrayList<>();
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        seeallreviewAdapter = new seeallreviewAdapter(getContext(), activity, list, R.layout.lyt_reviews, "sub_review",revire_recycle.this);
//        recyclerView.setAdapter(seeallreviewAdapter);
//
//        myRef.child("Review").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        Reviews reviews = dataSnapshot1.getValue(Reviews.class);
//                        list.add(reviews);
////                        list.add(dataSnapshot1.getKey());
////                        list.add(reviews.getname());
////                        list.add(reviews.getRate());
////                        list.add(reviews.getRe());
//                    }
//                }
//                System.out.println(list);
//                seeallreviewAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        return root;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview);

        recyclerView = findViewById(R.id.recyccleview);
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        seeallreviewAdapter = new seeallreviewAdapter(this,list);
        recyclerView.setAdapter(seeallreviewAdapter);

        String id = getIntent().getStringExtra("Productid");

        Product product = new Product();
        myRef.child("Review").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Reviews reviews = dataSnapshot1.getValue(Reviews.class);
                        list.add(reviews);
//                        list.add(dataSnapshot1.getKey());
//                        list.add(reviews.getname());
//                        list.add(reviews.getRate());
//                        list.add(reviews.getRe());
                    }
                }
                System.out.println(list);
                seeallreviewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
