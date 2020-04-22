package com.yog.androidarena.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

public object General
{
    fun settingFirebaseCacheToFalse(db:FirebaseFirestore){
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
        db.firestoreSettings = settings
    }


}