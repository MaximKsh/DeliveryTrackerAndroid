package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.TypeConverter
import java.util.*



class Converters {
    companion object {
        @JvmStatic
        @TypeConverter
        fun fromStringToUUID(uuidString: String) : UUID {
            return UUID.fromString(uuidString)
        }

        @JvmStatic
        @TypeConverter
        fun fromUuidToString(uuid: UUID) : String {
            return uuid.toString()
        }

        @JvmStatic
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return if (value != null) Date(value) else null
        }

        @JvmStatic
        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}