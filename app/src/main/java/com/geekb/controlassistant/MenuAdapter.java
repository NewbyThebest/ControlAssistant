package com.geekb.controlassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    private String[] mControls = {"自定义手势1", "自定义手势2", "自定义手势3", "自定义手势4", "自定义手势5",
            "自定义手势6", "自定义手势7", "自定义手势8"};
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
        holder.mTitle.setText(data.operation);
        holder.mSwitch.setChecked(data.isBound);
        ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mControls);
        holder.mType.setAdapter(adapter);
        if (data.isBound) {
            holder.mType.setSelection(data.gesture);
        }
        holder.mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.gesture = position;
                NetManager.getInstance().getNetService().updateOperation(data.gesture,data.operation)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(mContext,"更新失败",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(Boolean success) {
                                if (success){
                                    Toast.makeText(mContext,"更新成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(mContext,"更新失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

    public void setDatas(List<BindData> datas){
        mList = datas;
    }

    public List<BindData> getDatas(){
        return mList;
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