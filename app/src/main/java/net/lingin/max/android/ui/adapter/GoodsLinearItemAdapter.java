package net.lingin.max.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import net.lingin.max.android.R;
import net.lingin.max.android.model.GoodsPrintDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2019/12/27 17:51
 */
public class GoodsLinearItemAdapter extends DelegateAdapter.Adapter<GoodsLinearItemAdapter.RecyclerViewItemHolder> {

    private Context context;

    private LayoutHelper mHelper;

    private List<GoodsPrintDTO> data = new ArrayList<>();

    public GoodsLinearItemAdapter(Context context, LayoutHelper helper) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_goods_list_item, parent, false);
        return new RecyclerViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerViewItemHolder holder, final int position) {
        GoodsPrintDTO printDTO = data.get(position);
        holder.itemNoTV.setText(printDTO.getItemNo());
        holder.itemNameTV.setText(printDTO.getItemName());
        holder.itemSizeTV.setText(printDTO.getItemSize());
        holder.salePriceTV.setText(String.valueOf(printDTO.getSalePrice()));
        holder.unitNoTV.setText(printDTO.getUnitNo());
        holder.goodsCountsTV.setText(String.valueOf(printDTO.getGoodsCounts()));
        holder.addNumberBtn.setOnClickListener(view -> {
            int goodsCounts = printDTO.getGoodsCounts();
            printDTO.setGoodsCounts(goodsCounts + 1);
            holder.goodsCountsTV.setText(String.valueOf(printDTO.getGoodsCounts()));
        });
        holder.deleteNumberBtn.setOnClickListener(view -> {
            int goodsCounts = printDTO.getGoodsCounts();
            if (goodsCounts < 1) {
                return;
            }
            printDTO.setGoodsCounts(goodsCounts - 1);
            holder.goodsCountsTV.setText(String.valueOf(printDTO.getGoodsCounts()));
        });
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public List<GoodsPrintDTO> getData() {
        return data;
    }

    public void setData(List<GoodsPrintDTO> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class RecyclerViewItemHolder extends RecyclerView.ViewHolder {

        TextView itemNoTV;

        TextView itemNameTV;

        TextView itemSizeTV;

        TextView salePriceTV;

        TextView unitNoTV;

        TextView goodsCountsTV;

        QMUIRoundButton addNumberBtn;

        QMUIRoundButton deleteNumberBtn;

        public RecyclerViewItemHolder(View itemView) {
            super(itemView);
            itemNoTV = itemView.findViewById(R.id.item_no);
            itemNameTV = itemView.findViewById(R.id.item_name);
            itemSizeTV = itemView.findViewById(R.id.item_size);
            salePriceTV = itemView.findViewById(R.id.sale_price);
            unitNoTV = itemView.findViewById(R.id.unit_no);
            goodsCountsTV = itemView.findViewById(R.id.goods_counts);
            addNumberBtn = itemView.findViewById(R.id.add_number);
            deleteNumberBtn = itemView.findViewById(R.id.delete_number);
        }
    }
}
