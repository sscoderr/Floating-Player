package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.PlaylistListsActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class FirstPageMainSliderAdapter extends SliderViewAdapter<FirstPageMainSliderAdapter.SliderAdapterVH> {

    private Context context;

    public FirstPageMainSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_page_main_slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaylistListsActivity.class);
                intent.putExtra("category_name",viewHolder.textViewDescription.getText());
                switch (position) {
                    case 0:
                        intent.putExtra("category", "hit music");
                        break;
                    case 1:
                        intent.putExtra("category", "workout music");
                        break;
                    case 2:
                        intent.putExtra("category", "electro house");
                        break;
                    case 3:
                        intent.putExtra("category", "new releases songs");
                        break;
                    case 4:
                        intent.putExtra("category", Locale.getDefault().getDisplayLanguage()+" music");
                        break;
                }
                context.startActivity(intent);
            }
        });
        switch (position) {
            case 0:
                Picasso.with(context).load(R.drawable.bg1).into(viewHolder.imageViewBackground);
                viewHolder.textViewDescription.setText(context.getResources().getString(R.string.first_page_main_slider_hits));
                break;
            case 1:
                Picasso.with(context).load(R.drawable.imgworkout).into(viewHolder.imageViewBackground);
                viewHolder.textViewDescription.setText(context.getResources().getString(R.string.first_page_main_slider_workout));
                break;
            case 2:
                Picasso.with(context).load(R.drawable.bg3).into(viewHolder.imageViewBackground);
                viewHolder.textViewDescription.setText(context.getResources().getString(R.string.first_page_main_slider_party));
                break;
            case 3:
                Picasso.with(context).load(R.drawable.bg2).into(viewHolder.imageViewBackground);
                viewHolder.textViewDescription.setText(context.getResources().getString(R.string.first_page_main_slider_new_releases));
                break;
            case 4:
                Picasso.with(context).load(R.drawable.bg3).into(viewHolder.imageViewBackground);
                viewHolder.textViewDescription.setText(Locale.getDefault().getDisplayLanguage()+" Music");
                break;
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 5;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}