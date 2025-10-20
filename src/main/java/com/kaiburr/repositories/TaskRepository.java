package com.kaiburr.repositories;

import com.kaiburr.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    // Find tasks where the name contains the search string (case-insensitive) [cite: 74, 75]
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Task> findByNameContaining(String name);
}