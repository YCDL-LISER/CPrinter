package net.lingin.max.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import net.lingin.max.android.R;
import net.lingin.max.android.model.BluetoothDeviceDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2019/12/24 15:35
 */
public class BluetoothLinearItemAdapter extends DelegateAdapter.Adapter<BluetoothLinearItemAdapter.RecyclerViewItemHolder> {

    private Context context;

    private LayoutHelper mHelper;

    private List<BluetoothDeviceDTO> data = new ArrayList<>();

    private OnItemClickListener onItemClickListener = null;

    public BluetoothLinearItemAdapter(Context context, LayoutHelper helper) {
        this.context = context;
        this.mHelper = helper;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mHelper;
    }

    @NotNull
    @Override
    public RecyclerViewItemHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_bluetooth_list_item, parent, false);
        return new RecyclerViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerViewItemHolder holder, final int position) {
        holder.qmuiCommonListItemView.setText(data.get(position).getName());
        holder.qmuiCommonListItemView.setDetailText(data.get(position).getMac());
        holder.qmuiCommonListItemView.setOrientation(QMUICommonListItemView.VERTICAL);
        if (onItemClickListener != null) {
            holder.qmuiCommonListItemView.setOnClickListener(view -> onItemClickListener.ItemClickListener(view, position));
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void addBluetooth(BluetoothDeviceDTO bluetoothDeviceDTO) {
        data.add(bluetoothDeviceDTO);
        notifyDataSetChanged();
    }

    public void addBluetooth(List<BluetoothDeviceDTO> bluetoothDeviceDTOS) {
        data.addAll(bluetoothDeviceDTOS);
        notifyDataSetChanged();
    }

    public void deleteBluetooth(int postion) {
        data.remove(postion);
        notifyDataSetChanged();
    }

    public void deleteBluetooth(String mac) {
        int position = -1;
        for (int i = 0; i < this.data.size(); i++) {
            BluetoothDeviceDTO dto = data.get(i);
            if (dto.getMac().equals(mac)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    public void initBluetoothes() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void addOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 正常条目的item的ViewHolder
     */
    class RecyclerViewItemHolder extends RecyclerView.ViewHolder {

        public QMUICommonListItemView qmuiCommonListItemView;

        public RecyclerViewItemHolder(View itemView) {
            super(itemView);
            qmuiCommonListItemView = itemView.findViewById(R.id.qmui_common_list_item_view);
        }
    }

    public interface OnItemClickListener {
        void ItemClickListener(View view, int postion);
    }
}
