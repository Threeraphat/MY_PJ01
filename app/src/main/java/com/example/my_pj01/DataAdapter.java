package com.example.my_pj01;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_pj01.Models.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<ProductModel> productObjects;
    private List<ProductModel> productObjectsTotal;

    public DataAdapter(Context context, List<ProductModel> productObjects){
        this.context = context;
        this.productObjects = productObjects;
        this.productObjectsTotal = new ArrayList<>(productObjects);
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_holder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapter.ViewHolder holder, int position) {
        holder.c_name.setText(productObjects.get(position).getName());
        holder.c_promo.setText(productObjects.get(position).getPromotion());
        holder.c_price.setText(productObjects.get(position).getPrice().concat(" THB."));
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClickListener(v,getAdapterPosition());
            }
        }
    }

    private onRecyclerViewItemClickListener mItemClickListener;

    void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener{
        void onItemClickListener(View view,int position);
    }

    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductModel> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(productObjectsTotal);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                /*getType Search term*/
                for(ProductModel productItem : productObjectsTotal){
                    if(productItem.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(productItem);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productObjects.clear();
            productObjects.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}