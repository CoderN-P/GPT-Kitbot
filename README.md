# GPT-Kitbot

## Overview

GPT-Kitbot is a modified version of the 2024 FRC kitbot. It has a web dashboard with the ability to convert natural language queries into executable robot commands.

## Features

- Web dashboard for controlling the robot
- Natural language processing for command execution

## How to use

### 1. Clone the repository
### 2. Install the required dependencies

#### For the python backend

```bash
     cd web-gui
     cd backend
     python3.12 -m venv venv
     source venv/bin/activate
     pip install -r requirements.txt
   ```

#### For the web frontend

```bash
     cd web-gui
     cd frontend
     npm install
     ```
### 3. Start the backend server

```bash
     cd web-gui
     cd backend
     source venv/bin/activate
     python app.py
```

### 4. Start the frontend server

```bash
     cd web-gui
     cd frontend
     npm run dev
```

### 5. Run robot code (Windows)


```bash
     cd kitbot-code
     ./gradlew deploy
```

### 6. Access dashboard

Open your web browser and go to http://localhost:3000 to access the dashboard.