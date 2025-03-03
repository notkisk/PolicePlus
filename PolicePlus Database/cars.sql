-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 03, 2025 at 02:18 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `policeplus`
--

-- --------------------------------------------------------

--
-- Table structure for table `cars`
--

CREATE TABLE `cars` (
  `id` int(11) NOT NULL,
  `license_plate` varchar(20) NOT NULL,
  `owner_name` varchar(100) DEFAULT NULL,
  `insurance_start` date DEFAULT NULL,
  `insurance_end` date DEFAULT NULL,
  `inspection_start` date DEFAULT NULL,
  `inspection_end` date DEFAULT NULL,
  `tax_paid` varchar(30) DEFAULT NULL,
  `stolen_car` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cars`
--

INSERT INTO `cars` (`id`, `license_plate`, `owner_name`, `insurance_start`, `insurance_end`, `inspection_start`, `inspection_end`, `tax_paid`, `stolen_car`) VALUES
(1, '0403411834', 'Mouslem Drihem', '2025-03-01', '2026-03-01', '2025-03-01', '2026-03-01', 'Paid', 'No'),
(2, '00078012116', 'Salah Bekkari', '2025-01-14', '2027-03-18', '2025-04-11', '2026-03-26', 'Paid', 'Yes'),
(3, '04143012316', 'Haithem Bekkari', '2024-10-01', '2025-10-13', '2025-03-01', '2026-03-08', 'Paid', 'No'),
(4, '1210618219', 'Drihem Abdelmoumen', '2005-03-10', '2006-03-11', '1982-04-25', '1983-03-11', 'Not Paid', 'Yes'),
(5, '0788711216', 'Ali Sar', '2025-03-05', '2027-03-11', '2024-03-06', '2027-03-03', 'Paid', 'No');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cars`
--
ALTER TABLE `cars`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `license_plate` (`license_plate`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cars`
--
ALTER TABLE `cars`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
