package com.app.book.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.book.R;
import com.app.book.model.Book;
import com.app.book.ui.book.DetailActivity;
import com.app.book.util.GlideImageLoader;

import java.util.List;

public class MyRvListAdapter extends RecyclerView.Adapter<MyRvListAdapter.ViewHolder> {

    public List<Book> mData;

    public void setData(List<Book> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Book book = mData.get(position);
        holder.tvName.setText(book.getBookName());

        new GlideImageLoader().displayImage(holder.ivICon.getContext(), book.getPicUrl(), holder.ivICon);

        holder.tvAuthor.setText(book.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("id", book.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivICon;
        TextView tvName;
        TextView tvAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivICon = itemView.findViewById(R.id.pic);
            tvName = itemView.findViewById(R.id.name);
            tvAuthor = itemView.findViewById(R.id.author);
        }
    }
}
