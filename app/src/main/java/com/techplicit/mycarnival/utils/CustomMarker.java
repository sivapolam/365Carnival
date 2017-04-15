package com.techplicit.mycarnival.utils;

/**
 * Created by pnaganjane001 on 20/12/15.
 */
public class CustomMarker {

        private String mLabel;
        private String mIcon;
        private Double mLatitude;
        private Double mLongitude;

        public CustomMarker(String label, String icon, Double latitude, Double longitude)
        {
            this.mLabel = label;
            this.mLatitude = latitude;
            this.mLongitude = longitude;
            this.mIcon = icon;
        }

        public String getLabel()
        {
            return mLabel;
        }

        public void setLabel(String mLabel)
        {
            this.mLabel = mLabel;
        }

        public String getIcon()
        {
            return mIcon;
        }

        public void setIcon(String icon)
        {
            this.mIcon = icon;
        }

        public Double getLatitude()
        {
            return mLatitude;
        }

        public void setLatitude(Double mLatitude)
        {
            this.mLatitude = mLatitude;
        }

        public Double getLongitude()
        {
            return mLongitude;
        }

        public void setLongitude(Double mLongitude)
        {
            this.mLongitude = mLongitude;
        }


}
