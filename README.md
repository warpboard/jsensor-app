# jsensor-app
Java application to read and display sensor data.

This code shows a simple Java application to read data from a prototype of a sensor board (Temperature, Pressure, Humidity, Ambient Light)
featuring also a programmable led. The sensor board has been connected to the Warp interposed board where the desired I/O pins have been 
populated to work with the sensor board.

The complete deployment of the app from the development workstation to the board is handled by gradle though SSH.
