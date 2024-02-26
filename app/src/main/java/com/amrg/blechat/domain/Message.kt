package com.amrg.blechat.domain

sealed class Message(val text: String) {
    class Local(text: String) : Message(text)
    class Remote(text: String) : Message(text)
}