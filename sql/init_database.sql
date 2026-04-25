/*
    SQL Server Initialization Script for PlanetSim Project
    Author: Antigravity Assistant
    Tone: Objective / Professional
*/

-- 1. Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'PlanetSim')
BEGIN
    CREATE DATABASE PlanetSim;
END
GO

USE PlanetSim;
GO

-- 2. Drop tables if exists (for re-runnability)
IF OBJECT_ID('RoutingLogs', 'U') IS NOT NULL DROP TABLE RoutingLogs;
IF OBJECT_ID('Satellites', 'U') IS NOT NULL DROP TABLE Satellites;
IF OBJECT_ID('Planets', 'U') IS NOT NULL DROP TABLE Planets;
GO

-- 3. Create Planets table
CREATE TABLE Planets (
    planet_id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL,
    mass_kg FLOAT NOT NULL,             -- Mass in kg
    radius_km FLOAT NOT NULL,           -- Mean radius in km
    distance_sun_km FLOAT NOT NULL,     -- Average distance from Sun
    orbital_period_days FLOAT,          -- Time to orbit Sun
    texture_file NVARCHAR(255),         -- Filename for texture
    rotation_speed FLOAT DEFAULT 0.5    -- Visual rotation speed
);
GO

-- 4. Create Satellites table
CREATE TABLE Satellites (
    satellite_id INT PRIMARY KEY IDENTITY(1,1),
    planet_id INT NOT NULL,
    name NVARCHAR(50) NOT NULL,
    altitude_km FLOAT NOT NULL,         -- Height above surface in km
    longitude FLOAT DEFAULT 0,          -- Longitude position (0-360)
    latitude FLOAT DEFAULT 0,           -- Latitude position (-90 to 90)
    orbital_velocity FLOAT,             -- Calculated velocity (km/s)
    signal_range_km FLOAT DEFAULT 5000, -- Range for routing algorithm
    is_natural BIT DEFAULT 0,           -- 1 for Moons, 0 for Artificial
    CONSTRAINT FK_Satellite_Planet FOREIGN KEY (planet_id) REFERENCES Planets(planet_id)
);
GO

-- 5. Create RoutingLogs table
CREATE TABLE RoutingLogs (
    log_id INT PRIMARY KEY IDENTITY(1,1),
    source_sat_id INT,
    dest_sat_id INT,
    path_description NVARCHAR(MAX),
    total_distance_km FLOAT,
    log_time DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Log_Source FOREIGN KEY (source_sat_id) REFERENCES Satellites(satellite_id),
    CONSTRAINT FK_Log_Dest FOREIGN KEY (dest_sat_id) REFERENCES Satellites(satellite_id)
);
GO

-- 6. Insert Default Planet Data (NASA Data)
-- Distance and Period scaled for simulation visibility
INSERT INTO Planets (name, mass_kg, radius_km, distance_sun_km, orbital_period_days, texture_file, rotation_speed) VALUES 
('Mercury', 3.3011e23, 2439.7, 57900000, 88, 'mercury.jpg', 0.2),
('Venus', 4.8675e24, 6051.8, 108200000, 224, 'venus.jpg', 0.1),
('Earth', 5.97237e24, 6371.0, 149600000, 365, 'earth.jpg', 0.5),
('Mars', 6.4171e23, 3389.5, 227900000, 687, 'mars.jpg', 0.4),
('Jupiter', 1.8982e27, 69911.0, 778600000, 4333, 'jupiter.jpg', 1.0),
('Saturn', 5.6834e26, 58232.0, 1433500000, 10759, 'saturn.jpg', 0.9),
('Uranus', 8.6810e25, 25362.0, 2872500000, 30687, 'uranus.jpg', 0.7),
('Neptune', 1.02413e26, 24622.0, 4495100000, 60190, 'neptune.jpg', 0.7);
GO

-- 7. Insert The Moon
INSERT INTO Satellites (planet_id, name, altitude_km, longitude, latitude, orbital_velocity, signal_range_km, is_natural) 
VALUES ((SELECT planet_id FROM Planets WHERE name = 'Earth'), 'Moon', 384400, 0, 0, 1.022, 1000000, 1);
GO

-- 8. Insert some Mock Artificial Satellites for Earth (Distributed)
INSERT INTO Satellites (planet_id, name, altitude_km, longitude, latitude, signal_range_km, is_natural) VALUES 
((SELECT planet_id FROM Planets WHERE name = 'Earth'), 'SAT-Alpha', 20000, 0, 0, 30000, 0),
((SELECT planet_id FROM Planets WHERE name = 'Earth'), 'SAT-Bravo', 20000, 90, 0, 30000, 0),
((SELECT planet_id FROM Planets WHERE name = 'Earth'), 'SAT-Charlie', 20000, 180, 0, 30000, 0),
((SELECT planet_id FROM Planets WHERE name = 'Earth'), 'SAT-Delta', 20000, 270, 0, 30000, 0);
GO
