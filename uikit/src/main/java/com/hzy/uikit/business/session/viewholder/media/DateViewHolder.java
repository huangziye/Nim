package com.hzy.uikit.business.session.viewholder.media;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hzy.uikit.R;

/**
 * Created by winnie on 2017/9/18.
 */

public class DateViewHolder extends RecyclerView.ViewHolder {

    public TextView dateText;

    public DateViewHolder(View itemView) {
        super(itemView);
        dateText = (TextView) itemView.findViewById(R.id.date_tip);
    }
}
