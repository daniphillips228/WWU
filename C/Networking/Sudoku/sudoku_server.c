/*------------------------------------------------------------------------
* Jayce Brewer, Quinn Murphy, Gavin Harris 
* Program: prog2_server.c  
*
*
* Syntax: ./prog2_server player_port observer_port
*
*
*------------------------------------------------------------------------
*/

#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <fcntl.h>
#include <errno.h>
#define QLEN 6 /* size of request queue */
#define PARMAX 20 /* max number of participants */
#define OBSMAX 255 /* max number of Observers */
int visits = 0; /* counts client connections */

//Checks if guess is valid
bool checkGuess(char* board,int row[9][9],int x,int y,char val){
    if ((x < 1 || x > 9) || (y < 1 || y > 9) || (val < '1' || val > '9')){
        return false;
    }
    for (int i = 0; i < 9; i++){
        if (val == board[row[x-1][i]]){
            return false;
        } else if (val == board[row[i][y-1]]){
            return false;
        }
    }
    int xind=(x-1)/3;
    int yind=(y-1)/3;
    for(int i=0;i<3;i++){
        for(int j=0;j<3;j++){
            if(val==board[row[(xind*3)+i][(yind*3)+j]]){
                return false;
            }
        }
    }
    return true; 
            
          
}

//sends to all observers
void sendToObs(char* str, int obsArr[OBSMAX]){
    for(int i=0;i<OBSMAX;i++){
        if(obsArr[i]!=-1){
            send(obsArr[i],str,strlen(str),MSG_DONTWAIT&MSG_NOSIGNAL);
        }
    }
}

int main(int argc, char **argv) {
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad1, sad2; /* structure to hold server's address */
	struct sockaddr_in cad; /* structure to hold participant's address */
	struct timeval tv; //timeout structure
	int numpar=0; //number of participants
	int numobs=0; //number of observers
	int sd, sd2, x, y; /* socket descriptors */
	int port_participant; /* protocol port number for participants*/
	int port_observer; /* protocol port number for observers */
	int alen; /* length of address */
	int n; //recv return val
	int highfd=0; //highest socket descriptor value
	int usernameSize; //size of username
	unsigned int round = 1; //round number
	int optval = 1; /* boolean value when we set socket option */
	char buf[1000]; /* buffer for string the server sends */
	int parArr[PARMAX]; /* keep track of the participants socket id's */
	char* usernames[PARMAX]; /*Array of usernames */
	int scores[PARMAX]; /*Array of scores*/ 
	
	//zeros out arrays
    for (int i = 0; i < PARMAX; i++){
        scores[i] = 0;
        usernames[i] = 0;
    }
    
	int obsArr[OBSMAX]; /* keep track of the observers socket id's */
	bool brk=false; //for breaking from loops with loops inside of them
	fd_set readfds; //read socket set
	

    //checks correct number of args
	if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./server server_port_Participant server_port_Observer\n");
		exit(EXIT_FAILURE);
	}

	memset((char *)&sad1,0,sizeof(sad1)); /* clear sockaddr structure */
	memset((char *)&sad2,0,sizeof(sad2)); /* clear sockaddr structure */
	sad1.sin_family = AF_INET; /* set family to Internet */
	sad1.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */
	sad2.sin_family = AF_INET; /* set family to Internet */
	sad2.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

	port_participant = atoi(argv[1]); /* convert argument to binary */
	if (port_participant > 0) { /* test for illegal value */
		sad1.sin_port = htons((u_short)port_participant);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[1]);
		exit(EXIT_FAILURE);
	}
	
	port_observer = atoi(argv[2]); /* convert argument to binary */
	if (port_observer > 0) { /* test for illegal value */
		sad2.sin_port = htons((u_short)port_observer);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[2]);
	    exit(EXIT_FAILURE);
	}

	/* Map TCP transport protocol name to protocol number */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a sockets */
	sd = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	sd2 = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}
	if (sd2 < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}
	if( setsockopt(sd2, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}

	/* Bind a local address to the socket */
	if (bind(sd, (struct sockaddr *)&sad1, sizeof(sad1)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}
	
	if (bind(sd2, (struct sockaddr *)&sad2, sizeof(sad2)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(sd, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}
	if (listen(sd2, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}
	
	for (int i=0; i < PARMAX; i++){
	    parArr[i] = -1;
	}
	for (int i=0; i < OBSMAX; i++){
	    obsArr[i] = -1;
	}
	
	/*Begining of Game Stuff*/
	
	//creates the board
	char board[307];
	
	//all column and row indexes for spots in board where guesses should go
	int row[9][9]={
    {25,27,29,31,33,35,37,39,41},
	{47,49,51,53,55,57,59,61,63},
	{69,71,73,75,77,79,81,83,85},
	{113,115,117,119,121,123,125,127,129},
	{135,137,139,141,143,145,147,149,151},
	{157,159,161,163,165,167,169,171,173},
	{201,203,205,207,209,211,213,215,217},
	{223,225,227,229,231,233,235,237,239},
	{245,247,249,251,253,255,257,259,261}};
	
	//formats board
	sprintf(board,"  -------------------\n1 |     |     |     |\n2 |     |     |     |\n3 |     |     |     |\n  ------+-----+------\n4 |     |     |     |\n5 |     |     |     |\n6 |     |     |     |\n  ------+-----+------\n7 |     |     |     |\n8 |     |     |     |\n9 |     |     |     |\n  -------------------\n   A B C D E F G H I\n");
	alen = sizeof(cad);
	highfd = sd2;
	x = 0;
	y = 0;
    tv.tv_sec=60;
    tv.tv_usec = 0;
	/* Main server loop - accept and handle requests */
	
	
	while (1) {
	    memset(buf, 0, sizeof(buf));
	    
	    //add all sockets to readfds
	    FD_ZERO(&readfds);
		FD_SET(sd,&readfds);
	    FD_SET(sd2,&readfds);
	    for(int i=0;i<PARMAX;i++){
            if(parArr[i]!=-1){
                FD_SET(parArr[i],&readfds);
            }
        }
        
        //wait for input
        int selret = select(highfd+1, &readfds, NULL, NULL, &tv);
        if (selret == -1){
            fprintf(stderr, "Select Error: %d\n", errno);
        
        //timeout
        }else if(selret == 0){
            sprintf(buf, "No one made a correct move for 60 seconds... round %d over\n\n", round);
            sendToObs(buf, obsArr);
            if (round == 0xffffffff){
                round = 0;
            }else{
                round++;
            }
            memset(buf, 0, sizeof(buf));
            sprintf(buf, "Scores: \n");
            sendToObs(buf, obsArr);
            memset(buf, 0, sizeof(buf));            
            for (int j = 0; j < PARMAX; j++){
                if (usernames[j] != 0){
                    sprintf(buf,"%s %d\n",usernames[j], scores[j]);
                    sendToObs(buf, obsArr);
                    memset(buf, 0, sizeof(buf));
                }
            }
            tv.tv_sec=60;
            tv.tv_usec = 0;
            sprintf(buf, "\nRound %d\n\n", round);
            sendToObs(buf, obsArr);
        	sprintf(board,"  -------------------\n1 |     |     |     |\n2 |     |     |     |\n3 |     |     |     |\n  ------+-----+------\n4 |     |     |     |\n5 |     |     |     |\n6 |     |     |     |\n  ------+-----+------\n7 |     |     |     |\n8 |     |     |     |\n9 |     |     |     |\n  -------------------\n   A B C D E F G H I\n");
        
        //connection or guess recieved
        }else{
        
            //participant connection 
            if(FD_ISSET(sd,&readfds)){
                if ((x=accept(sd, (struct sockaddr *)&cad, &alen)) < 0) {
		            fprintf(stderr, "Accept failed\n");
		            close(x);
	            }else{
	                for (int i = 0; i < PARMAX; i++){
                        if (parArr[i] == -1){
	                        parArr[i] = x;
	                        if(highfd<x)
                                highfd=x;
                            break;
                        }        
                        if (i == PARMAX - 1){
                            close(x);
                        }
                    }
                }
                
            //observer connection
            }if (FD_ISSET(sd2,&readfds)){
                if ((y=accept(sd2, (struct sockaddr *)&cad, &alen)) < 0) {
		            fprintf(stderr, "Accept failed\n");
		            close(y);
	            }
	            if (y > 0){
	                for (int i = 0; i < OBSMAX; i++){
	                    if (obsArr[i] == -1){
	                        obsArr[i] = y;
	                        sprintf(buf, "Round %d\n\n", round);
	                        send(obsArr[i], buf, strlen(buf), 0);
	                        break;
	                    }
	                    if (i == OBSMAX - 1){
	                        close(y);
	                    }
	                }
	            }
	            
            //guess or username input
            }for(int i=0;i<PARMAX;i++){
	            if(FD_ISSET(parArr[i],&readfds)){
	                
	                //guess
	                if(usernames[i] != 0){
	                    n = recv(parArr[i], buf, sizeof(buf), 0);
	                    
	                    //particpant left
	                    if (n == 0){
	                        char* left = (char *)malloc(sizeof(char*));
	                        sprintf(left, "%s has left the game\n", usernames[i]);
	                        sendToObs(left, obsArr);
	                        close(parArr[i]);
	                        parArr[i] =-1;
	                        usernames[i]=0;
	                        scores[i]=0;
	                        
	                    //handle guess    	                        
	                    }else{
                            
                            //illegal guess
    	                    if (strlen(buf) != 3){
    	                        if (scores[i] == 0x8000000){
    	                            scores[i]++;
    	                        }
	                            scores[i]--;
		                        sprintf(buf, "User %s has made an illegaly formatted guess\n\n", usernames[i]);
                                sendToObs(buf, obsArr);
                                
                            //legal guess (may still not be a successful guess)
	                        }else{
	                            int rowval = buf[0]-48;
		                        int colval = buf[1]-64;
		                        char val = buf[2];
		                        char colchar = buf[1];
		                        
		                        //correct guess
		                        if(checkGuess(board,row,rowval,colval,val)){
		                            board[row[rowval-1][colval-1]]=val;
		                            if(scores[i] == 0x7fffffff){
    	                                scores[i]--;
    	                            }
		                            scores[i]++;
		                            sprintf(buf, "User %s puts %c on %d%c, now has %d point(s)\n\n", usernames[i], val, rowval, colchar, scores[i]);
                                    sendToObs(buf, obsArr);
		                            tv.tv_sec=60;
                                    tv.tv_usec = 0;
                                    
                                //incorrect guess
	                            }else{
	                                scores[i]--;
		                            sprintf(buf, "User %s fails to put %c on %d%c, now has %d point(s)\n\n", usernames[i], val, rowval, colchar, scores[i]);
                                    sendToObs(buf, obsArr);
	                            }
		                        sendToObs(board, obsArr);
	                        }
                        }
                    
                    //username
                    }else{    
                        n = recv(parArr[i], &usernameSize, 1, 0);
                        n = recv(parArr[i], buf, usernameSize, 0);
                        buf[usernameSize]=0;
                        
                        //username too long
                        if (usernameSize > 10){
                            sprintf(buf, "N - to long\n");
                            send(parArr[i],buf,strlen(buf),0);
                            parArr[i] = -1;
                            close(parArr[i]);
                            break;
                        }
                        
                        //username with invalid characters
                        for (int j = 0; j < usernameSize; j++){
                            if ((buf[j] > 122) || (buf[j] < 97 && buf[j] > 90) || (buf[j] < 65 && buf[j] > 57) || (buf[j] < 48)){
                                sprintf(buf, "N - invalid chars \n");
                                send(parArr[i],buf,strlen(buf),0);
                                close(parArr[i]);
                                parArr[i] = -1;
                                brk=true;
                                break;
                            }
                        }
                        if(brk==true)
                            break;
                            
                        //username already in use
                        for(int j=0;j<PARMAX;j++){
                            if((parArr[j] != -1) && (usernames[j] != 0)){
                                if(strcmp(usernames[j],buf)==0){
                                    sprintf(buf, "N - already in use\n");
                                    send(parArr[i],buf,strlen(buf),0);
                                    close(parArr[i]);
                                    parArr[i] = -1;
                                    brk=true;
                                    break;
                                }
                            }
                        }
                        if(brk==true)
                            break;
                            
                        //if name is valid
                        char* c = (char*)malloc(sizeof(char*));
                        strcpy(c,buf);
                        usernames[i]=c;
                        scores[i] = 0;
                        sprintf(buf, "Y");
                        send(parArr[i],buf,1,0);
                        break;
                    }
	            }
            }
        }
	    
	}
	close(sd2);
	close(sd);
}