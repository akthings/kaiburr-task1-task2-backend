🌌 Project Helios: Kubernetes Executor API

Dynamic Command Execution and Ephemeral Workload Management

This repository hosts the core backend service for Project Helios, implementing a robust REST API using Spring Boot. It combines comprehensive CRUD operations with programmatic Kubernetes execution capabilities, demonstrating mastery over modern container orchestration and persistent data management.

This service serves as the core data and execution engine for the Task 3 Web UI.

🌟 Key Features

The application is structured to meet rigorous requirements for task persistence (Task 1) and dynamic cloud execution (Task 2).

Task 1: API & Persistence

RESTful Task Management: Full CRUD operations for Task objects via a high-performance Spring Boot API.

MongoDB Persistence: All task definitions, historical execution data, and resulting output logs are securely persisted in a MongoDB instance.

Task 2: Kubernetes Execution Core

Programmatic K8s Execution: The core functionality uses the Fabric8 Kubernetes Client to orchestrate temporary compute workloads.

Ephemeral Pod Lifecycle: Upon execution request (PUT /tasks/{id}/execute), the API dynamically:

Creates a short-lived busybox Pod to run the specified shell command.

Streams the stdout output in real-time.

Records the output and completion status back to the MongoDB Task object.

Ensures automatic cleanup and deletion of the temporary Pod.

Containerization: The application is packaged as a standard Docker image for seamless deployment onto Kubernetes.

🛠️ Technology Stack
Component	Technology	Description
Framework	Spring Boot 3.2+	Core RESTful API backend.
Persistence	MongoDB	Highly flexible NoSQL database for task and execution history.
K8s Client	Fabric8 Kubernetes Client	Used for programmatic interactions with the Kubernetes API server.
Container	Docker	Packaging and distribution of the application image.
Orchestration	Kubernetes	Deployment environment for the service and target for execution pods.
Image Base	maven:3.9.6-eclipse-temurin-21	Multi-stage build process ensures optimized final image size.
🚀 Deployment Guide

This guide assumes you have a running Kubernetes cluster, kubectl configured, and Helm installed.

1. Build and Publish Docker Image

Build the service image, tag it with your Docker Hub username, and push it to a registry accessible by your cluster.

code
Bash
download
content_copy
expand_less
# Set your Docker Username
export DOCKER_USER="<YOUR_USERNAME>"

# Build the Docker Image
docker build -t ${DOCKER_USER}/task-executor-backend:latest .

# Push the Image to the registry
docker push ${DOCKER_USER}/task-executor-backend:latest
2. Deploy Prerequisites (MongoDB)

It is highly recommended to deploy a persistent MongoDB instance using Helm to ensure data retention.

code
Bash
download
content_copy
expand_less
# Deploy MongoDB using Bitnami Helm Chart with persistence enabled
helm install mongo-release bitnami/mongodb \
    --set persistence.enabled=true \
    --set auth.rootPassword="your-secure-password" \
    --set auth.database="task-db" \
    --set auth.username="taskuser" \
    --set auth.password="taskpass"
3. Deploy Application and RBAC

The application requires specific permissions (Role-Based Access Control) to create, list, and delete Pods within the cluster.

Apply RBAC Manifests:

code
Bash
download
content_copy
expand_less
kubectl apply -f kubernetes-manifests/k8s-rbac.yaml

Deploy Application (Deployment & Service):
Ensure the kubernetes-manifests/k8s-deployment.yaml references the image pushed in Step 1.

code
Bash
download
content_copy
expand_less
kubectl apply -f kubernetes-manifests/k8s-deployment.yaml
kubectl apply -f kubernetes-manifests/k8s-service.yaml
🧩 API Reference

The following table outlines the main available endpoints for task management and execution.

HTTP Method	Endpoint	Description
POST	/tasks	Creates a new Task definition.
GET	/tasks	Retrieves all Task objects.
GET	/tasks/{id}	Retrieves a specific Task by ID.
PUT	/tasks/{id}/execute	Core Execution Endpoint: Triggers the Kubernetes Pod creation, execution, and cleanup routine for the task's defined command.
DELETE	/tasks/{id}	Deletes a Task definition.
✅ Operational Verification & Proofs

The following steps demonstrate the successful deployment, API availability, and the core Kubernetes execution functionality.

1. Service Availability Proof

Verify that the service is running and accessible (if using a NodePort or LoadBalancer setup).

code
Bash
download
content_copy
expand_less
kubectl get svc task-executor-service

# Expected Output (Service Access Proof):
NAME                      TYPE       CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
task-executor-service     NodePort   10.100.10.50   <none>        8080:30080/TCP   2m

Confirm the service endpoint is reachable (assuming NodePort 30080 is used):

code
Bash
download
content_copy
expand_less
curl http://localhost:30080/tasks
# Expected Output (Endpoint Availability): [] (Empty array, service is live)
2. Task Lifecycle and Kubernetes Execution Proof

This walkthrough validates Task 1 (CRUD) and the core Task 2 (K8s Execution).

Step A: Create Task Definition

Create a sample task (e.g., T003) that executes a standard system command (ls -l).

code
Bash
download
content_copy
expand_less
curl -X POST http://localhost:30080/tasks \
     -H 'Content-Type: application/json' \
     -d '{"name": "T003", "command": "ls -l /etc"}'

# Expected Response: 201 Created, returning the Task object (e.g., with ID: 65e3...)
Step B: Trigger Kubernetes Execution

Execute the newly created task via the programmatic endpoint.

code
Bash
download
content_copy
expand_less
curl -X PUT http://localhost:30080/tasks/65e3.../execute
Step C: Observe Pod Lifecycle (Critical Proof)

Immediately observe the creation and subsequent rapid deletion of the ephemeral Pod in a separate terminal.

code
Bash
download
content_copy
expand_less
# Monitor pods during execution window
kubectl get pods

# Expected Output (Pod Lifecycle Proof):
# 1. Pod is created (during execution):
# NAME                READY   STATUS      RESTARTS   AGE
# task-executor-78x   1/1     Running     0          5s
# busybox-exec-t003   1/1     Running     0          2s 

# 2. Pod is cleaned up (after execution completion):
# NAME                READY   STATUS      RESTARTS   AGE
# task-executor-78x   1/1     Running     0          10s
Step D: Verify Execution Results

Retrieve the task object to confirm the taskExecutions array now contains the streamed output and status.

code
Bash
download
content_copy
expand_less
curl -X GET http://localhost:30080/tasks/65e3...

# Expected Task JSON (K8s Execution Successful Proof):
{
  "id": "65e3...",
  "name": "T003",
  "command": "ls -l /etc",
  "taskExecutions": [
    {
      "startTime": "2024-03-01T10:00:00Z",
      "status": "COMPLETED",
      "output": "total 12\ndrwxr-xr-x 2 root root 4096 Feb 29 12:00 apache2\n-rw-r--r-- 1 root root 123 Feb 29 12:00 hosts\n..."
    }
  ]
}
