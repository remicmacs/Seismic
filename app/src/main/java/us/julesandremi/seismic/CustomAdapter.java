package us.julesandremi.seismic;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 09/06/2017.
 */

class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private Context context;
    private List listSeism;

    public CustomAdapter(Context context, List listSeism) {
        this.listSeism = listSeism;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listSeism.size();
    }

    public void setListSeism(List listSeism) {
        this.listSeism = listSeism;
    }

    @Override

    public Object getItem(int position) {
        return listSeism.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listSeism.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.seism_item_layout, parent, false);

            mViewHolder = new MyViewHolder();
            mViewHolder.textTitle = (TextView) convertView
                    .findViewById(R.id.seism_title);
            mViewHolder.textLocation = (TextView) convertView
                    .findViewById(R.id.seism_location);
            mViewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.seism_intensity_icon);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Seism seism = (Seism) getItem(position);

        mViewHolder.textTitle.setText(seism.getTitle());
        mViewHolder.textLocation.setText(seism.getPlace());
        mViewHolder.imageView.setImageResource(R.drawable.ic_tsunami);

        return convertView;
    }

    private class MyViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textLocation;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        // Des trucs stylés

    }

}
