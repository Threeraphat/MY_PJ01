package com.example.my_pj01;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.my_pj01.Models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class TypeDataAdapter extends RecyclerView.Adapter<TypeDataAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<ProductModel> productObjects;
    private List<ProductModel> productObjectsTotal;

    public TypeDataAdapter(Context context, List<ProductModel> productObjects){
        this.context = context;
        this.productObjects = productObjects;
        this.productObjectsTotal = new ArrayList<>(productObjects);
    }

    @NonNull
    @Override
    public TypeDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_holder_type, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TypeDataAdapter.ViewHolder holder, int position) {
        holder.c_type.setText(productObjects.get(position).getType());
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
        private TextView c_type;

        ViewHolder(View itemView) {
            super(itemView);
            this.c_type = itemView.findViewById(R.id.card_type);
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
                    if(productItem.getType().toLowerCase().contains(filterPattern)){
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