package project.kiran.zync;

/**
 * Created by Kiran on 13-10-2016.
 */
import android.graphics.Typeface;

public class RowItem {
    private int imageId;
    private String title;
    private String desc;
    private String discount;
    private Boolean defaultIcon_nf;
    private String id;

    public RowItem(String title,String id) {
        this.title = title;
        //this.discount=discount;
        this.id=id;
    }
    public void setDefault()
    {

    }
    public String getItemId(){
        return this.id;
    }
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setItemId(String id){this.id=id;}
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    /*public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }*/
    public Boolean getDefault() {
        return this.defaultIcon_nf;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}