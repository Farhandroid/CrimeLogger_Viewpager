package tanvir.crimelogger_playstore.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by HP on 25-Nov-17.
 */

public class UserPostMC implements Serializable {

    String userName, crimePlace , crimeDate , crimeTime , crimeType , crimeDesc , postDateAndTime , howManyImage , howManyReport;


    public UserPostMC(String userName, String crimePlace, String crimeDate, String crimeTime, String crimeType, String crimeDesc, String postDateAndTime, String howManyImage, String howManyReport) {
        super();
        this.userName = userName;
        this.crimePlace = crimePlace;
        this.crimeDate = crimeDate;
        this.crimeTime = crimeTime;
        this.crimeType = crimeType;
        this.crimeDesc = crimeDesc;

        this.postDateAndTime = postDateAndTime;
        this.howManyImage = howManyImage;
        this.howManyReport=howManyReport;
    }






    public String getHowManyReport() {
        return howManyReport;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getCrimePlace() {
        return crimePlace;
    }

    public void setCrimePlace(String crimePlace) {
        this.crimePlace = crimePlace;
    }

    public String getCrimeDate() {
        return crimeDate;
    }

    public void setCrimeDate(String crimeDate) {
        this.crimeDate = crimeDate;
    }

    public String getCrimeTime() {
        return crimeTime;
    }

    public void setCrimeTime(String crimeTime) {
        this.crimeTime = crimeTime;
    }

    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public String getCrimeDesc() {
        return crimeDesc;
    }

    public void setCrimeDesc(String crimeDesc) {
        this.crimeDesc = crimeDesc;
    }

    public String getPostDateAndTime() {
        return postDateAndTime;
    }

    public void setPostDateAndTime(String postDateAndTime) {
        this.postDateAndTime = postDateAndTime;
    }

    public String getHowManyImage() {
        return howManyImage;
    }

    public void setHowManyImage(String howManyImage) {
        this.howManyImage = howManyImage;
    }


}


