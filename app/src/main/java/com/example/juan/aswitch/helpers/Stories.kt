package com.example.juan.aswitch.helpers

import com.example.juan.aswitch.models.Story

object Stories {

    private var array =  ArrayList<ArrayList<Story>>()

    fun add(stories: ArrayList<Story>): Int {
        val newArray = ArrayList<Story>()
        stories.forEach { story -> newArray.add(story) }
        array.add(newArray)
        return array.size - 1
    }

    fun get(index: Int): ArrayList<Story> {
        return array[index]
    }
}