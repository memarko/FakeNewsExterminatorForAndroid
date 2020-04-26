package com.markokotar.fakenewsexterminator.json

data class HoaxResult(val articles: List<Article>?, val num_of_entries: Int?, val status: String?, val total_hits: Int?) {
}