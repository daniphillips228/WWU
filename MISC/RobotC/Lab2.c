#pragma config(Motor,  motorB , LeftWheel, tmotorNXT, PIDControl, encoder)
#pragma config(Motor,  motorC , RightWheel, tmotorNXT, PIDControl, encoder)
/* **************************************************************************** */
/*   Program: assignment1.c 																			 							*/
/*   Description: following a black line with one light sensor  								*/
/*   Author: Gavin Harris  																											*/
/*   Date: January 21, 2016 																										*/
/*                          																										*/
/*   NOTES:                          																						*/
/*   1.              							                                              */
/*                                                                  						*/
/*    MOTORS & SENSORS:                                              						*/
/*    [I/O Port]   [Name]     [Type]   [Description]                 						*/
/*    Port B        LeftWheel      NXT     Left motor                 					*/
/*    Port C        RightWheel     NXT     Right motor                 					*/
/*                                                                       				*/
/*   References:                                                         				*/
/*   [1]                     																										*/
/* **************************************************************************** */

task main()
{

	while(1) {

		int randomValue = random(7);

		if (randomValue == 1){
			//both forward
			motor[LeftWheel] = 75;
			motor[RightWheel] = 75;
			wait1Msec(2000);
	  }
		if (randomValue == 2){
			//both backward
			motor[LeftWheel] = -75;
			motor[RightWheel] = -75;
			wait1Msec(2000);
		}
		if (randomValue == 3){
			//left spin
			motor[LeftWheel] = -75;
			motor[RightWheel] = 75;
			wait1Msec(2000);
		}
		if (randomValue == 4){
			//right spin
			motor[LeftWheel] = 75;
			motor[RightWheel] = -75;
			wait1Msec(2000);
		}
		if (randomValue == 5){
			//left swing
			motor[LeftWheel] = 0;
			motor[RightWheel] = 75;
			wait1Msec(2000);
		}
		if (randomValue == 6){
			//right swing
			motor[LeftWheel] = 75;
			motor[RightWheel] = 0;
			wait1Msec(2000);
		}
		if (randomValue == 7){
			//left curve
			motor[LeftWheel] = 35;
			motor[RightWheel] = 75;
			wait1Msec(2000);
		}
		if (randomValue == 0){
			//right curve
			motor[LeftWheel] = 75;
			motor[RightWheel] = 35;
			wait1Msec(2000);
		}
	}
}
