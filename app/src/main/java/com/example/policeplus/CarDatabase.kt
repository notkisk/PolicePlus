import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CarEntity::class], version = 1, exportSchema = false)
abstract class CarDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(context: Context): CarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // ✅ Use applicationContext
                    CarDatabase::class.java,
                    "car_database"
                )
                    .fallbackToDestructiveMigration() // ✅ Handle migrations safely
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
