package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;

public class GoodsListGridAdapterBackup extends BaseAdapter {
    private final Context mContext;
    private final List<GoodsGrid> goodsSpecific;
    private ArrayList<GoodsGrid> filteredList;

    public GoodsListGridAdapterBackup(Context mContext, List<GoodsGrid> goodsSpecific) {
        this.mContext = mContext;
        this.goodsSpecific = goodsSpecific;
    }

    @Override
    public int getCount() {
        return goodsSpecific.size();
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GoodsGrid book = goodsSpecific.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_goods_list, null);

            final ImageView imageView = convertView.findViewById(R.id.imageview_goods_category);
            final TextView nameTextView = convertView.findViewById(R.id.textview_goods_name);


            final ViewHolder viewHolder = new GoodsListGridAdapterBackup.ViewHolder(nameTextView, imageView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//    viewHolder.imageViewCoverArt.setImageResource(book.getImageResource());
        viewHolder.nameTextView.setText(book.getName());


        return convertView;
    }

    private class ViewHolder {
        private final TextView nameTextView;
        private final ImageView imageView;


        public ViewHolder(TextView nameTextView, ImageView imageView) {
            this.nameTextView = nameTextView;

            this.imageView = imageView;

        }
    }
}
