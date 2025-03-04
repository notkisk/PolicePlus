-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 04, 2025 at 06:00 AM
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
-- Database: `insurance`
--

-- --------------------------------------------------------

--
-- Table structure for table `insurance`
--

CREATE TABLE `insurance` (
  `id` int(11) NOT NULL,
  `owner` varchar(100) DEFAULT NULL,
  `license_plate` varchar(20) NOT NULL,
  `insurance_start` date NOT NULL,
  `insurance_end` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `insurance`
--

INSERT INTO `insurance` (`id`, `owner`, `license_plate`, `insurance_start`, `insurance_end`) VALUES
(1, 'Mouslem Drihem', '0403411834', '2024-01-02', '2025-03-14'),
(2, 'Drihem Abdelmoumen', '1210618219', '1985-05-01', '1986-07-02'),
(3, 'Salah Bekkari', '00078012116', '2023-12-13', '2025-08-02'),
(4, 'Haithem Bekkari', '04143012316', '2024-12-15', '2026-03-31'),
(5, 'Ali Sar', '0788711216', '2023-11-04', '2024-10-31');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `insurance`
--
ALTER TABLE `insurance`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `license_plate` (`license_plate`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `insurance`
--
ALTER TABLE `insurance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
