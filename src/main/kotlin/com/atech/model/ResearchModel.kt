package com.atech.model

import kotlinx.serialization.Serializable

@Serializable
data class ResearchModel(
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val authorUid: String = "",
    val tags: List<TagModel> = emptyList(),
    val questions: List<String> = emptyList(),
    val path: String = "",
    val requirements: Requirements = Requirements(),
    val created: Long = System.currentTimeMillis(),
    val deadline: Long? = null,
)

@Serializable
data class Requirements(
    val req: String = "",
    val minCgpa: Double? = null,
)
/*

{
  "title": "Advances in Artificial Intelligence",
  "description": "This is only for test.",
  "author": "Aiyu Ayaan",
  "authorUid": "TF6YASVgyRQmytXTQRVm2c5NS2I2",
  "tags": [
    {
      "created": 1695139200000,
      "createdBy": "TF6YASVgyRQmytXTQRVm2c5NS2I2",
      "name": "AI"
    },
    {
      "created": 1695139200001,
      "createdBy": "TF6YASVgyRQmytXTQRVm2c5NS2I2",
      "name": "Machine Learning"
    }
  ],
  "path": "",
  "requirements":{
	"req":"No requirements"
	},
"questions":[
 "What is the impact of teacher-student interactions on student performance?"
]
  "created": 1695139200002
}



*/
