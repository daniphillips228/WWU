/*
	Arg Parse
	line the command line to parse
	******************************************** more here
*/

char** arg_parse(char* line, int* argcp);

//Helper function for arg_parse to find how much space we need to malloc
void arg_count(char* line, int* argcp);
