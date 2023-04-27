package com.example.mezunapp.models

import com.google.firebase.Timestamp

class Graduate {
    lateinit var name: String
    lateinit var surname: String
    lateinit var startDate: Timestamp
    lateinit var graduateDate: Timestamp
    lateinit var email: String
    lateinit var profilePhotoName: String
    lateinit var mediaNames: MutableList<String>
    lateinit var programName: String
    lateinit var currentJobCountry: String
    lateinit var currentJobCity: String
    lateinit var currentJobCompany: String

    constructor(
        name: String,
        surname: String,
        startDate: Timestamp,
        graduateDate: Timestamp,
        email: String,
        profilePhotoName: String,
        programName: String,
        currentJobCountry: String,
        currentJobCity: String,
        currentJobCompany: String
    ){
        this.name = name
        this.surname = surname
        this.startDate = startDate
        this.graduateDate = graduateDate
        this.email = email
        this.profilePhotoName = profilePhotoName
        this.mediaNames = mutableListOf<String>()
        this.programName = programName
        this.currentJobCountry = currentJobCountry
        this.currentJobCity = currentJobCity
        this.currentJobCompany = currentJobCompany
    }

    fun toMap() : HashMap<String, Any>{
        val map = hashMapOf(
            "name" to name,
            "surname" to surname,
            "startDate" to startDate,
            "graduateDate" to graduateDate,
            "email" to email,
            "profilePhotoName" to profilePhotoName,
            "mediaNames" to mediaNames,
            "programName" to programName,
            "currentJobCountry" to currentJobCountry,
            "currentJobCity" to currentJobCity,
            "currentJobCompany" to currentJobCompany
        )
        return map
    }

    companion object{
        fun toObject(map: HashMap<String, Any>){
            var grad: Graduate = Graduate(
                map["name"] as String,
                map["surname"] as String,
                map["startDate"] as Timestamp,
                map["graduateDate"] as Timestamp,
                map["email"] as String,
                map["profilePhotoName"] as String,
                map["programName"] as String,
                map["currentjobCountry"] as String,
                map["currentJobCity"] as String,
                map["currentJobCompany"] as String
            )
        }
    }
}