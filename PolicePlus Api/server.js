
require('dotenv').config();
const express = require('express');
const mysql = require('mysql');
const cors = require('cors');
const res = require('express/lib/response');

const app = express();
app.use(cors());
app.use(express.json()); // To parse JSON requests

// MySQL Connection
const db = mysql.createConnection({
    host: process.env.DB_HOST,  // Load from .env file
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD||"",
    database: process.env.DB_NAME
});

db.connect(err => {
    if (err) {
        console.error('Database connection failed:', err);
    } else {
        console.log('Connected to MySQL');
    }
});

// API Endpoint: Get All Cars
app.get('/cars', (req, res) => {
    db.query('SELECT * FROM cars', (err, results) => {
        if (err) return res.status(500).send(err);
        res.json(results);
    });
});

app.get('/cars/:plate', (req, res) => {
    const plate = req.params.plate; // Get plate from URL
    db.query('SELECT * FROM cars WHERE license_plate = ?', [plate], (err, results) => {  
        if (err) return res.status(500).send(err);
        if (results.length === 0) return res.status(404).json({ message: "Car not found"}); // Handle not found case
        res.json(results[0]); // Return only the first matching car
    });
});



// Start Server
const PORT = 5001;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});
