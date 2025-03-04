require('dotenv').config();
const express = require('express');
const mysql = require('mysql2');  // Import mysql2
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// MySQL Connection (Insurance Database)
const insuranceDb = mysql.createConnection({
    host: process.env.INS_DB_HOST, 
    user: process.env.INS_DB_USER,
    password: process.env.INS_DB_PASSWORD || "",
    database: process.env.INS_DB_NAME
});

insuranceDb.connect(err => {
    if (err) console.error('Database connection failed:', err);
    else console.log('Connected to MySQL (Insurance DB)');
});

// Fetch insurance details by license plate
app.get('/api/insurance/:plate', (req, res) => {
    const plate = req.params.plate;

    insuranceDb.query(
        'SELECT insurance_start, insurance_end FROM insurance WHERE license_plate = ?',
        [plate],
        (err, results) => {
            if (err) return res.status(500).json({ error: "Database error" });
            if (results.length === 0) return res.status(404).json({ message: "No insurance found" });
            res.json(results[0]); // Return insurance data
        }
    );
});

// Start Server
const PORT = 5002;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Insurance API running on port ${PORT}`);
});
