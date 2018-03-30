/* **************************************************************************** */
/*   Program: assignment1.c 																			 							*/
/*   Description: following a black line with one light sensor  								*/
/*   Author: Gavin Harris  																											*/
/*   Date: January 14, 2016 																										*/
/*                          																										*/
/*   NOTES:                          																						*/
/*   1.              							                                              */
/*                                                                  						*/
/*    MOTORS & SENSORS:                                              						*/
/*    [I/O Port]   [Name]     [Type]   [Description]                 						*/
/*    Port A        motorA      NXT     Forward motor                 					*/
/*                                                                       				*/
/*   References:                                                         				*/
/*   [1]                     																										*/
/* **************************************************************************** */


#pragma config(Sensor, S1 , eye, sensorSONAR)
#pragma config(Motor,  motorA , Forward, tmotorNXT, PIDControl, encoder)

task main()
{
int eye = 0;
int Forward = 0;

	while(1) {

		if(SensorValue[eye] > 50) { //nothing in the way, go forward
			motor[Forward] = 75;
	  }
	  else{ // something in the way, stop
	 		motor[Forward] = 0;
	  }

	}
}
