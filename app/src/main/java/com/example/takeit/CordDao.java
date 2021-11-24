package com.example.takeit;




import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * The Room Magic is in this file, where you map a Java method call to an SQL query.
 *
 * When you are using complex data types, such as Date, you have to also supply type converters.
 * To keep this example basic, no types that require type converters are used.
 * See the documentation at
 * https://developer.android.com/topic/libraries/architecture/room.html#type-converters
 */

@Dao

public interface CordDao {
    @Insert
    void insertAll(Coordins... coordins);

    @Delete
    void delete(Coordins... coordins);

    @Query("SELECT * FROM coordins")
    List<Coordins> getAll();
}
