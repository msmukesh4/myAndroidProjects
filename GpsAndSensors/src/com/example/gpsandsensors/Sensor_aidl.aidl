package com.example.gpsandsensors;

interface Sensor_aidl{
	Location send_Location();
	float distance(int steps);
}