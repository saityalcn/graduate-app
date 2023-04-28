package com.example.mezunapp.models

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class Graduate {
    lateinit var name: String
    lateinit var surname: String
    lateinit var startYear: String
    lateinit var graduateYear: String
    lateinit var email: String
    lateinit var profilePhotoLink: Uri
    lateinit var mediaNames: MutableList<String>
    lateinit var programName: String
    lateinit var currentJobCountry: String
    lateinit var currentJobCity: String
    lateinit var currentJobCompany: String

    constructor(
        name: String,
        surname: String,
        startYear: String,
        graduateYear: String,
        email: String,
        profilePhotoLink: Uri,
        programName: String,
        currentJobCompany: String,
        currentJobCity: String,
        currentJobCountry: String
    ){
        this.name = name
        this.surname = surname
        this.startYear = startYear
        this.graduateYear = graduateYear
        this.email = email
        this.profilePhotoLink = profilePhotoLink
        this.mediaNames = mutableListOf<String>()
        this.programName = programName
        this.currentJobCompany = currentJobCompany
        this.currentJobCity = currentJobCity
        this.currentJobCountry = currentJobCountry
    }

    fun toMap() : HashMap<String, Any>{
        val map = hashMapOf(
            "name" to name,
            "surname" to surname,
            "startYear" to startYear,
            "graduateYear" to graduateYear,
            "email" to email,
            "profilePhotoLink" to profilePhotoLink,
            "mediaNames" to mediaNames,
            "programName" to programName,
            "currentJobCompany" to currentJobCompany,
            "currentJobCity" to currentJobCity,
            "currentJobCountry" to currentJobCountry
        )
        return map
    }

    companion object{
        fun toObject(map: QueryDocumentSnapshot): Graduate{
            var grad: Graduate = Graduate(
                map["name"] as String,
                map["surname"] as String,
                map["startYear"] as String,
                map["graduateYear"] as String,
                map["email"] as String,
                Uri.parse(map["profilePhotoLink"] as String) as Uri,
                map["programName"] as String,
                map["currentJobCompany"] as String,
                map["currentJobCity"] as String,
                map["currentJobCountry"] as String
            )


            return grad
        }
    }
}