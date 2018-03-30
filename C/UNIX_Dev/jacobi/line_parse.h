//*****************************************************************************
// line_parse.h  Author: Knabb, Aidan  Date: 12/6/2017
//
// line_parse.h is the header file for line_parse.c. line_parse.h acts as the
// interface for line_parse.c which contains utility functions for jacobi.c
//*****************************************************************************
#ifndef _LINE_PARSE_H_
#define _LINE_PARSE_H_

//----------------------------------------------------------------------------
// line_parse()
// line - line is the line to be parsed
//
// line_parse() parses the given line and returns an array of the tokens that
// were in line that is NULL therminated
//----------------------------------------------------------------------------
char** line_parse(char* line);

//-----------------------------------------------------------------------------
// processline()
// matrix - matrix is one of the two matrixes used to use the jacobi method
// matrix2 - matrix2 is one of the two matrixes used to use the jacobi method
// current_line - current_line is the current row of the matrixes
// line - line is the current row of values with spaces seperating the values
//
// processline() parses line to fill the current row of both matrixes with the
// starting values. 
//-----------------------------------------------------------------------------
void processline(double (*matrix)[1024], double (*matrix2)[1024], int current_line, char* line);

//-----------------------------------------------------------------------------
// print_mtx()
// matrix - matrix is the matrix to save to file
//
// print_mtx() saves the given matrix to file
//-----------------------------------------------------------------------------
void print_mtx(double (*matrix)[1024]);

//-----------------------------------------------------------------------------
// get_wallTime()
//
// get_wallTime() retrieves the current system time and returns it in seconds
//-----------------------------------------------------------------------------
double get_wallTime();

//-----------------------------------------------------------------------------
// validate()
// matrix - The matrix of which to check the answers of
//
// validate() compares the results of the program with the known solution to
// check the correctness of the program
//-----------------------------------------------------------------------------
void validate(double (*matrix)[1024]);
#endif
