package com.techplicit.carnivalcommons.data;

import com.techplicit.carnivalcommons.utils.UtilityCommon;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CarnivalsPojo implements Comparable<CarnivalsPojo> {

    private Date dateTime;

    private String id, name, image, startDate, endDate;
    private boolean activeFlag;

    public CarnivalsPojo(JSONObject jsonObject) {
        this.id = jsonObject.optString(JsonMap.ID);
        this.name = jsonObject.optString(JsonMap.NAME);
        this.image = jsonObject.optString(JsonMap.IMAGE);
        this.startDate = jsonObject.optString(JsonMap.START_DATE);
        this.endDate = jsonObject.optString(JsonMap.END_DATE);
        this.activeFlag = jsonObject.optBoolean(JsonMap.ACTIVE_FLAG);

        String dateStr = UtilityCommon.getDate(Long.valueOf(this.startDate), "dd/MM/yyyy");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try{
            this.dateTime = df.parse(dateStr);
        }
        catch ( Exception ex ){
            System.err.println(ex);
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }


    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(CarnivalsPojo another) {
        if (getDateTime() == null || another.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(another.getDateTime());
    }

    private interface JsonMap{
        String ID = "id";
        String NAME = "name";
        String IMAGE = "image";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String ACTIVE_FLAG = "activeFlag";
    }
}
