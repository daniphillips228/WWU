#pragma config(Sensor, S1, light, sensorLightActive)
#pragma config(Motor,  motorC, LeftWheel, tmotorNXT, PIDControl, encoder)
#pragma config(Motor,  motorB, RightWheel, tmotorNXT, PIDControl, encoder)

/* **************************************************************************** */
/*   Program: Lab3																			 							          */
/*   Description: Random movements inside circle, once it detects black it      */
/*   backs up																																		*/
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
	int white = 63;

	int threshold = (black+white)/2;

	//create a function to read the color automatically readColor();

	while(1) {
		while(SensorValue[light] <= threshold)
		{
			motor[LeftWheel] = -75;
			motor[RightWheel] = -75;
		}
		while(SensorValue[light] > threshold )
		{
			int randomValue = random(5);

			if (randomValue == 0){
				//left spin
				motor[LeftWheel] = -40;
				motor[RightWheel] = 40;
				wait1Msec(500);
			}
			if (randomValue == 1){
				//right spin
				motor[LeftWheel] = 40;
				motor[RightWheel] = -40;
				wait1Msec(500);
			}
			if (randomValue == 2){
				//left swing
				motor[LeftWheel] = 0;
				motor[RightWheel] = 40;
				wait1Msec(500);
			}
			if (randomValue == 3){
				//right swing
				motor[LeftWheel] = 40;
				motor[RightWheel] = 0;
				wait1Msec(500);
			}
			if (randomValue == 4){
				//left curve
				motor[LeftWheel] = 15;
				motor[RightWheel] = 40;
				wait1Msec(500);
			}
			if (randomValue == 5){
				//right curve
				motor[LeftWheel] = 40;
				motor[RightWheel] = 15;
				wait1Msec(500);
			}
		}
	}
}
