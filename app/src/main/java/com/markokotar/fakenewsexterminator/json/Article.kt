package com.markokotar.fakenewsexterminator.json

import java.util.*

data class Article(
    val canonical_url:String?,
    val date_published: Date?,
    val domain:String?,
    val id:Long?,
    val number_of_tweets:Long?,
    val score: Double,
    val site_type: String?,
    val title: String?
    ) {

}
