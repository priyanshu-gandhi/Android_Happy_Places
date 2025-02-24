package com.example.happyplaces

import android.os.Parcel
import android.os.Parcelable
import android.provider.CallLog.Locations

data class HappyPlacesModel(
   // private var image : String?,
    private var title : String?,
    private var location : String?,
    private var description : String?
) : Parcelable
{
//    fun getImage(): Int
//    {
//        return image
//    }
//
//    fun setImage( image : Int)
//    {
//        this.image=image
//    }

    constructor(parcel: Parcel) : this(
       // parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

//    fun getImage(): String? {
//        return image
//    }
//
//    fun setImage(image: String) {
//        this.image = image
//    }

    fun getTitle(): String
    {
        return title!!
    }

    fun setTitle(title: String)
    {
        this.title=title
    }

    fun setLocation(location : String){
        this.location =location
    }

    fun getLocation() : String
    {
        return location!!
    }


    fun getDesc(): String
    {
        return description!!
    }

    fun setDesc(description: String)
    {
        this.description= description
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeString(image)
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HappyPlacesModel> {
        override fun createFromParcel(parcel: Parcel): HappyPlacesModel {
            return HappyPlacesModel(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlacesModel?> {
            return arrayOfNulls(size)
        }
    }
}
