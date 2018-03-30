#pragma config(Sensor, S1, light, sensorLightActive)
#pragma config(Motor,  motorC, LeftWheel, tmotorNXT, PIDControl, encoder)
#pragma config(Motor,  motorB, RightWheel, tmotorNXT, PIDControl, encoder)

/* **************************************************************************** */
/*   Program: Lab4																			 							          */
/*   Description: Travel along black circle outline												      */
/*   Author: Gavin Harris  																											*/
/*   Date: February 4, 2016 																										*/
/*                          																										*/
/*   NOTES:                          																						*/
/*   1.              							                                              */
/*                                                                  						*/
/*    MOTORS & SENSORS:                                              						*/
/*    [I/O Port]   [Name]     [Type]   [Description]                 						*/
/*    Port C        LeftWheel      NXT     Left motor                 					*/
/*    Port B        RightWheel     NXT     Right motor                 					*/
/*		Sensor 1      light					 NXT		 Light Sensor                         */
/*                                                                       				*/
/*   References:                                                         				*/
/*   [1]                     																										*/
/* **************************************************************************** */


task main()
{

	int black = 27;
	int white = 58;
	int maxSpeed = 40;
	int minSpeed = 20;
	int threshold = (black+white)/2;

	//create a function to read the color automatically readColor();

	while(1) {
		while(SensorValue[light] <= threshold)
		{
			motor[LeftWheel] = minSpeed;
			motor[RightWheel] = maxSpeed;
		}
		while(SensorValue[light] > threshold )
		{
			motor[LeftWheel] = maxSpeed;
			motor[RightWheel] = minSpeed;
		}
	}
}
