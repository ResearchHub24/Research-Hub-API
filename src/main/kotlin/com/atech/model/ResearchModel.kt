package com.atech.model

import kotlinx.serialization.Serializable

@Serializable
data class ResearchModel(
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val authorUid: String = "",
    val tags: List<TagModel> = emptyList(),
    val path : String = "",
    val created: Long = System.currentTimeMillis(),
)
/*

{
    "title": "Advances in Artificial Intelligence",
    "description": "A comprehensive study on recent developments in AI",
    "author": "Jane Doe",
    "authorUid": "jd123",
    "tags": [
    {
        "created": 1695139200000,
        "createdBy": "jd123",
        "name": "AI"
    },
    {
        "created": 1695139200001,
        "createdBy": "jd123",
        "name": "Machine Learning"
    }
    ],
    "path": "/research/ai-advances",
    "created": 1695139200002
}

*/
