package com.example.notesapplicationusingjetpack.feature_note.domain.util

sealed class NoteOrder(val orderType: OrderType) {// This tells we have 3 different ways
    // of ordering notes i.e. ordering based on title , date or color
    class Title(orderType: OrderType): NoteOrder(orderType)
    class Date(orderType: OrderType): NoteOrder(orderType)
    class Color(orderType: OrderType): NoteOrder(orderType)

    fun copy(orderType: OrderType): NoteOrder {
        return when(this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Color -> Color(orderType)
        }
    }
}
