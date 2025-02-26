# DAT251 Project
Reducing carbon emissions can be a tedious and boring task, causing many too lose their motivation to make the right choices. Therefore we have made CO₂mpass - a CO₂-tracker created for the purpose of helping individuals make more environmentally friendly choices. Our software  is not your average dull CO₂-tracker - instead, we have highly emphasized motivation when developing our product. By gamifying certain aspects of our software, we give the users a sense of progression, showing you what kind of difference YOU are making, all the while making it fun to do so.


This project consists of a backend and a frontend. Follow the instructions below to set up and run each component.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) (version 11 or later)
- [Gradle](https://gradle.org/install/)
- [Node.js and npm](https://nodejs.org/) (version 14 or later)

You also need a Google Maps API key set in an environment variable:

```sh
export GOOGLE_MAPS_API_KEY="<YOUR_API_KEY>"
```

## Backend Setup

1. Navigate to the `demo` directory:

   ```bash
   cd demo
   ```

2. Clean and build the project:

   ```bash
   ./gradlew clean build
   ```

3. Run the backend:
   ```bash
   ./gradlew bootRun
   ```

## Frontend Setup

1. Navigate to the `frontend` directory:

   ```bash
   cd frontend
   ```

2. Install the dependencies:

   ```bash
   npm install
   ```

3. Run the frontend development server:
   ```bash
   npm run dev
   ```
