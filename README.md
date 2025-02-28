Police License Plate Recognition App

📌 Overview

This is a mobile application designed for police officers to track and verify vehicle status using license plate recognition. The app allows officers to scan license plates and retrieve important information, such as owner details, insurance status, stolen vehicle alerts, and tickets.

🎯 Features

📸 License Plate Recognition: Automatically extract the license number from an image.

🔍 Vehicle Lookup: Search for vehicle details by entering the plate number manually.

📋 Data Retrieval: Fetch car-related data, including:

Owner information

Insurance start & end dates

Stolen vehicle status

Unpaid tickets

🏠 Home Screen:

Search bar for quick vehicle lookups

Display the last three scanned cars

Show total number of scanned cars

📜 Scan History: View previous scans with full details.

🔒 Authentication: Secure login with username and password.

🛠️ Tech Stack

Frontend (Android App - Kotlin Jetpack Compose)

Jetpack Compose (UI)

Adobe Figma(UI Design)

CameraX (License plate scanning)

ViewModel + LiveData (State management)

Room Database (Local storage)

Retrofit (API calls)

Backend (Node.js & Express)

MySQL (Database)

JWT (Authentication)

bcrypt (Password hashing)

REST API (For data retrieval and updates)

🚀 Setup Instructions

1️⃣ Backend Setup

Install Node.js and MySQL.

Clone the repository:

git clone https://github.com/your-repo/police-app.git
cd police-app/backend

Install dependencies:

npm install

Configure the .env file with database credentials.

Start the backend server:

node server.js

2️⃣ Frontend Setup (Android App)

Open the project in Android Studio.

Ensure that Kotlin and Jetpack Compose are installed.

Sync dependencies in build.gradle.

Update BASE_URL in Retrofit API service to match your backend.

Run the app on an emulator or a real device.

📌 Deployment

Backend: Can be hosted on a VPS.

Database: Hosted on a MySQL server.

🔒 Security Considerations

Use HTTPS for API communication.

Encrypt sensitive data before storing it.

Implement role-based access control (RBAC).

📌 Future Improvements

🧑‍💻 Biometric Authentication (Fingerprint/Face recognition)

🔔 Real-time Alerts for stolen vehicles

🌍 GPS Tracking of scanned vehicles

📊 Admin Dashboard for data analytics

📞 Contact

For any inquiries, feel free to contact [Your Name] at your-email@example.com.

© 2025 Police License Plate Recognition App. All rights reserved.

