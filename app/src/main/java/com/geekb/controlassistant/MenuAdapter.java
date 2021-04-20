package com.geekb.controlassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    private String[] mControls = {"自定义手势1", "自定义手势2", "自定义手势3", "自定义手势4", "自定义手势5"};
    private Context mContext;
    private List<BindData> mList;
    private ItemClickListener mItemClickListener;

    public MenuAdapter(Context context, List<BindData> list) {
        mContext = context;
        mList = list;
    }

    interface ItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);

        return new MenuHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        BindData data = mList.get(position);
        holder.mTitle.setText(data.name);
        holder.mSwitch.setChecked(data.isBound);
        ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mControls);
        holder.mType.setAdapter(adapter);
        if (data.isBound) {
            holder.mType.setSelection(data.type);
        }
        holder.mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MenuHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private Spinner mType;
        private SwitchCompat mSwitch;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mType = itemView.findViewById(R.id.sp_type);
            mSwitch = itemView.findViewById(R.id.sw_bing);
        }

    }
}