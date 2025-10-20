package com.kaiburr.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id; // Maps to Task ID [cite: 34]
    private String name; // Task name [cite: 35]
    private String owner; // Task owner [cite: 36]
    private String command; // Shell command [cite: 37]
    private List<TaskExecution> taskExecutions = new ArrayList<>(); // Execution history [cite: 38]
}