package com.kierasis.qpasslaurel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class featured_adapter extends RecyclerView.Adapter<featured_adapter.ViewHolder> {
    LayoutInflater inflater;
    List<featured_helper_class> cases;
    private OnNoteListener mOnNoteListener;

    public featured_adapter(Context ctx, List<featured_helper_class> cases, OnNoteListener onCaseListener){
        this.inflater = LayoutInflater.from(ctx);
        this.cases = cases;
        this.mOnNoteListener = onCaseListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_list_layout,parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.caseNumber.setText(cases.get(position).getTitle());
        holder.songArtists.setText(cases.get(position).getArtist());
        Picasso.get()
                .load(cases.get(position).getCoverImage())
                .placeholder(R.drawable.no_icon_featured)
                .into(holder.songCoverImage);
    }

    @Override
    public int getItemCount() {
        return cases.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView caseNumber, songArtists;
        ImageView songCoverImage;
        OnNoteListener onCaseListener;
        public ViewHolder(@NonNull View itemView, OnNoteListener onCaseListener) {
            super(itemView);

            caseNumber = itemView.findViewById(R.id.mv_Title);
            songArtists = itemView.findViewById(R.id.mv_desc);
            songCoverImage = itemView.findViewById(R.id.mv_image);
            this.onCaseListener = onCaseListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCaseListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface  OnNoteListener{
        void onNoteClick(int position);
    }
}
