# Task Tracker Application

A simple command-line tool to manage your daily tasks. Add, list, update, and delete tasks quickly.

**Project Idea Source:** [roadmap.sh Task Tracker Project](https://roadmap.sh/projects/task-tracker)

## Features

* Add new tasks
* List all tasks or filter by status
* Update task descriptions or status (In Progress / Done)
* Delete tasks
* Tasks are saved automatically to a file

## Getting Started

### Prerequisites

* Java Development Kit (JDK 17 or higher)
* Apache Maven

### Building the Project

1.  **Clone this repository:**
    ```bash
    git clone [https://github.com/YourUsername/task-tracker.git](https://github.com/YourUsername/task-tracker.git) # Replace with your actual repo URL
    cd task-tracker
    ```
2.  **Build the application:**
    ```bash
    mvn clean install
    ```
    This creates a `task-tracker-1.0-SNAPSHOT.jar` file in the `target/` folder.

## Usage

1.  **Run the application:**
    ```bash
    cd target
    java -jar task-tracker-1.0-SNAPSHOT.jar
    ```
    You'll see a prompt: `> `

2.  **Available Commands:**

    * `ADD "Your task description"`: Add a new task.
        * Example: `ADD "Buy groceries"`
    * `LIST`: Show all tasks.
    * `LIST <STATUS>`: Show tasks by status (e.g., `LIST DONE`, `LIST IN_PROGRESS`).
    * `UPDATE <ID> "New description"`: Change a task's description.
        * Example: `UPDATE 1 "Buy milk"`
    * `MARK-DONE <ID>`: Mark a task as done.
        * Example: `MARK-DONE 2`
    * `MARK-IN-PROGRESS <ID>`: Mark a task as in progress.
        * Example: `MARK-IN-PROGRESS 3`
    * `DELETE <ID>`: Remove a task.
        * Example: `DELETE 4`
    * `EXIT`: Quit the application.

## Data Storage

Your tasks are saved in a file named `tasks.json` inside a folder called `tasktracker_data`. This folder is created in the same directory where you run the application.

---
