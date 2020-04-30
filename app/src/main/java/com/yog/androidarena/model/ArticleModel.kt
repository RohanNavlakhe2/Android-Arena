package com.yog.androidarena.model

data class ArticleModel(val title:String,val source:String,val link:String) {
    constructor() : this("","","")
}