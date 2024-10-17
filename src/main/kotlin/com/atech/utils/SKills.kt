package com.atech.utils

object Skills {
    val skillList: List<String> by lazy { loadSkills() }

    private fun loadSkills(): List<String> {
        val inputStream = this::class.java.classLoader.getResourceAsStream("technical_skills_list.txt")
        return inputStream?.bufferedReader()?.useLines { lines ->
            lines.filter { it.isNotBlank() }.map { it.trim() }.toList()
        } ?: emptyList()
    }
}

