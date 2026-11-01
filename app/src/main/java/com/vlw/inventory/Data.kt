package com.vlw.inventory

import android.content.Context
import androidx.room.*

@Entity(tableName = "sarees", indices = [Index(value = ["barcode"], unique = true)])
data class Saree(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String,
    val name: String,
    val costPrice: Double,
    val piecePrice: Double,
    val setPrice: Double,
    val piecesPerSet: Int,
    val stock: Int
)

@Dao
interface SareeDao {
    @Query("SELECT * FROM sarees ORDER BY name")
    suspend fun all(): List<Saree>

    @Query("SELECT * FROM sarees WHERE barcode = :code LIMIT 1")
    suspend fun byBarcode(code: String): Saree?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(saree: Saree)

    @Query("UPDATE sarees SET stock = MAX(stock - :qty, 0) WHERE id = :id")
    suspend fun reduce(id: Long, qty: Int)
}

@Database(entities = [Saree::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun dao(): SareeDao

    companion object {
        @Volatile private var inst: AppDb? = null
        fun get(ctx: Context): AppDb = inst ?: synchronized(this) {
            inst ?: Room.databaseBuilder(ctx.applicationContext, AppDb::class.java, "vlw.db")
                .build().also { inst = it }
        }
    }
}
