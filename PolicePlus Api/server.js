require('dotenv').config();
const express = require('express');
const mysql = require('mysql2');  // Use mysql2 for better async support
const cors = require('cors');
const axios = require('axios'); // Import axios to call external API

const app = express();
app.use(cors());
app.use(express.json());

// MySQL Connection (Cars Database)
const db = mysql.createConnection({
    host: process.env.DB_HOST, 
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME
});

db.connect(err => {
    if (err) console.error('Database connection failed:', err);
    else console.log('Connected to MySQL (Cars DB)');
});

// Fetch car details from local database and insurance data from external API
app.get('/cars/:plate', async (req, res) => {
    const plate = req.params.plate;

    try {
        // Fetch car details from local MySQL database
        const [carResults] = await db.promise().query(
            'SELECT * FROM cars WHERE license_plate = ?',
            [plate]
        );

        if (carResults.length === 0) {
            return res.status(404).json({ message: "Car not found" });
        }

        // Fetch insurance data from external API
        const insuranceResponse = await axios.get(`http://localhost:5002/api/insurance/${plate}`);

        // Merge data and send response
        const carData = { 
            ...carResults[0], 
            insurance_start: insuranceResponse.data.insurance_start,
            insurance_end: insuranceResponse.data.insurance_end
          };
                  
        res.json(carData);

    } catch (error) {
        console.error("Error fetching data:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Start Server
const PORT = 5001;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});
