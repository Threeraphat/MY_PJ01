package com.example.my_pj01;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private Context context;
    private List<PromotionModel> productObjects;

    public DataAdapter(Context context, List<PromotionModel> productObjects){
        this.context = context;
        this.productObjects = productObjects;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_holder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapter.ViewHolder holder, int position) {
        //System.out.println("log_test column" + productObjects.get(position).getColumn());
        holder.c_name.setText(productObjects.get(position).getName());
        holder.c_promo.setText(productObjects.get(position).getPromotion());
        holder.c_price.setText(productObjects.get(position).getPrice());
        final StorageReference c_pic = FirebaseStorage.getInstance().getReference(productObjects.get(position).getPicture());
        final File file;
        try {
            file = File.createTempFile("images", "jpeg");
            c_pic.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Picasso.with(context)
                            .load(file)
                            .fit()
                            .centerCrop()
                            .into(holder.c_pic);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (productObjects != null && !productObjects.isEmpty()) {
            return productObjects.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView c_name;
        private TextView c_price;
        private TextView c_promo;
        private ImageView c_pic;

        ViewHolder(View itemView) {
            super(itemView);
            this.c_name = itemView.findViewById(R.id.card_name);
            this.c_pic = itemView.findViewById(R.id.card_Image);
            this.c_price = itemView.findViewById(R.id.card_price);
            this.c_promo = itemView.findViewById(R.id.card_promotion);
        }
    }
}
