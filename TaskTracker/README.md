# Task Tracker CLI

A lightweight, high-performance Command Line Interface (CLI) application built with **Java 21** and **Spring Boot** to track and manage your tasks. This project follows the Model-View-Controller (MVC) architectural pattern and stores tasks in a local `tasks.json` file in the current working directory.

---

## Features

- **Add Tasks**: Create new tasks with a description. Status defaults to `todo`.
- **Update Tasks**: Modify the description of existing tasks.
- **Delete Tasks**: Remove tasks by ID.
- **Mark Status**: Update task status to `in-progress` or `done`.
- **List Tasks**:
  - List all tasks.
  - List tasks filtered by status (`todo`, `in-progress`, `done`).
- **Data Persistence**: Tasks are saved locally to a `tasks.json` file.
- **Clean UI**: Outputs tasks in a clean, beautifully formatted ASCII table.
- **Robust Error Handling**: Graceful, user-friendly error messages (no stack traces).

---

## Prerequisites

- **Java Development Kit (JDK) 21** or higher.
- Java should be configured in your system environment path (`java` command available).

---

## How to Build the Project

We use the Maven Wrapper (`mvnw`) included in the project, so you don't need Maven pre-installed.

Open your terminal (PowerShell or Command Prompt on Windows) in the project root directory and run:

```cmd
.\mvnw.cmd clean package
```

This compiles the code, runs the test suite (15 unit and integration tests), and packages the application into a runnable JAR file under `target/task-tracker-0.0.1-SNAPSHOT.jar`.

---

## How to Run the App (Usage)

To make it easier to run, a batch script `task-cli.bat` is provided in the project root. You can run all commands using `.\task-cli.bat <command> [arguments]`.

### Adding a new task
```cmd
.\task-cli.bat add "Buy groceries"
# Output: Task added successfully (ID: 1)
```

### Updating a task description
```cmd
.\task-cli.bat update 1 "Buy groceries and cook dinner"
# Output: Task updated successfully (ID: 1)
```

### Deleting a task
```cmd
.\task-cli.bat delete 1
# Output: Task deleted successfully (ID: 1)
```

### Marking a task status
```cmd
# Mark as in-progress
.\task-cli.bat mark-in-progress 1
# Output: Task marked as in-progress successfully (ID: 1)

# Mark as done
.\task-cli.bat mark-done 1
# Output: Task marked as done successfully (ID: 1)
```

### Listing tasks
```cmd
# List all tasks
.\task-cli.bat list

# List only todo tasks
.\task-cli.bat list todo

# List only in-progress tasks
.\task-cli.bat list in-progress

# List only done tasks
.\task-cli.bat list done
```

---

## Task Schema

Each task stored in the `tasks.json` file contains:

- `id`: A unique auto-incrementing integer identifier.
- `description`: A short description of the task.
- `status`: String representation of the status (`todo`, `in-progress`, `done`).
- `createdAt`: ISO-8601 string of the date and time when the task was created.
- `updatedAt`: ISO-8601 string of the date and time when the task was last updated.

Example entry in `tasks.json`:
```json
[ {
  "id" : 1,
  "description" : "Buy groceries and cook dinner",
  "status" : "done",
  "createdAt" : "2026-06-28T17:22:39",
  "updatedAt" : "2026-06-28T17:23:38"
} ]
```

---

## Architectural Details (MVC)

The backend follows the MVC design pattern:

- **Model (`com.tasktracker.model`)**: 
  - `Task`: A class containing the task properties.
  - `TaskStatus`: An Enum defining valid task states with custom Jackson annotations.
- **View (`com.tasktracker.controller.TaskController` printing logic)**:
  - CLI tables are formatted and printed using Java's `System.out.format` to build responsive ASCII grids dynamically.
- **Controller (`com.tasktracker.controller.TaskController`)**:
  - Validates and parses CLI arguments, redirects actions to the Service layer, catches errors, and formats outputs.
- **Service (`com.tasktracker.service`)**:
  - Contains core business logic (e.g. validating descriptions, updating timestamps, status checks).
- **Repository (`com.tasktracker.repository`)**:
  - Interacts with the filesystem `tasks.json` using standard Java NIO (`java.nio.file.Files`) and Jackson `ObjectMapper` for JSON serialization.
