package project.kiran.zync;
/*** Created by Kiran on 26-03-2016.*/

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class customListViewAdapter extends ArrayAdapter<RowItem> {

    Context context;
    Typeface font;
    public customListViewAdapter(Context context, int resourceId,
                                 List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        TextView Di;
        RelativeLayout Container;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        font = Typeface.createFromAsset(this.context.getAssets(), "Roboto-Thin.ttf");
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.Di = (TextView) convertView.findViewById(R.id.defaultIcon);
            holder.Container = (RelativeLayout) convertView.findViewById(R.id.listContain);
            //TextView ListTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtTitle.setTypeface(font);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        //      holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
       /* if(rowItem.getDiscount()!=" ") {
            holder.Di.setText(rowItem.getDiscount() + "% OFF ");
        }else{
            holder.Di.setVisibility(View.INVISIBLE);
        }*/
        //holder.Container.setTag(R.id.listContain,rowItem.getItemId());
      //  holder.imageView.setImageBitmap(createBitmap(context, rowItem.getTitle()));
        // holder.Container.setTag(rowItem.getId());



        return convertView;
    }
    /*private Bitmap createBitmap(Context cxt,String name) {
        String oriText = name;
        name = name.substring(0, 1).toUpperCase();
        int color;

        if(oriText.equalsIgnoreCase("My Businesses")||oriText.equalsIgnoreCase("Settings")||oriText.equalsIgnoreCase("Logout")){
            color=Color.parseColor("#27ae60");
        }else {
            switch (name) {
                case "A":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "B":
                    color = Color.parseColor("#2980b9");
                    break;
                case "C":
                    color = Color.parseColor("#D24D57");
                    break;
                case "D":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "E":
                    color = Color.parseColor("#2ecc71");
                    break;
                case "F":
                    color = Color.parseColor("#EF4836");
                    break;
                case "G":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "H":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "I":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "J":
                    color = Color.parseColor("#F27935");
                    break;
                case "L":
                    color = Color.parseColor("#BE90D4");
                    break;
                case "M":
                    color = Color.parseColor("#1BBC9B");
                    break;
                case "N":
                    color = Color.parseColor("#e74c3c");
                    break;
                case "O":
                    color = Color.parseColor("#e74c3c");
                    break;
                default:
                    color = Color.parseColor("#9b59b6");
            }

        }
        GradientDrawable gradientDrawable = createDrawabale(color,color);
        View customMarkerView = ((LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.business_circle, null);
        TextView markerImageView = (TextView) customMarkerView.findViewById(R.id.text);
        LinearLayout mImageView = (LinearLayout) customMarkerView.findViewById(R.id.bg_container);
        mImageView.setBackground(gradientDrawable);
        markerImageView.setText(name);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
    public GradientDrawable createDrawabale(int bottomColor, int topColor) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]
                {bottomColor, topColor});
        gradient.setShape(GradientDrawable.OVAL);
        return gradient;
    }*/
}