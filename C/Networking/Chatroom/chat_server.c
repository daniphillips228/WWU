/* 
Gavin Harris, CSCI 367, 2pm
prog3_server.c  

*/

#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>

#define QLEN 6 /* size of request queue */
#define MaxLen 255 /* max participants and observers */

/*------------------------------------------------------------------------
* Program: prog3_server
*
* Syntax: ./prog3_server participant_port observer_port
*
* port - protocol port number to use
*
*------------------------------------------------------------------------
*/

//not implemented correctly
int minTimeout(int participantsTO[MaxLen], int observersTO[MaxLen]){
	
	int min = 80;
	for (int i = 0; i < MaxLen; i++){
		if (participantsTO[i] < min){
			min = participantsTO[i];
		}
		if (observersTO[i] < min){
			min = observersTO[i];
		}
    }
	if (min = 80){
		return 1000000;
	}else{
		return min;
	}
}


bool validUsername(char username[1000], int usernameSize){
	
	username[usernameSize]=0;
    //username too long
    if (usernameSize > 10){
		printf("Too long\n");
        return false;
    }

	for (int i = 0; i < usernameSize; i++){
		//illegal chars
		if ((username[i] > 122) || (username[i] < 97 && username[i] > 90) || (username[i] < 65 && username[i] > 57) || (username[i] < 48)){
			printf("Illegal characters\n");
			return false;
		}else{
			return true;
		}
	}
}

void privateMSG (char* msg, char* to, char* from, int observers[MaxLen], char* o_usernames[MaxLen]){
	
	//sending length of msg as uint16_t, and the msg
	uint16_t msgLength;
	uint16_t warningLen;
	char buf[1000];
	strncpy(buf, msg, sizeof buf - 1);
	msgLength = strlen(msg);
	bool sent = false;
   
	for(int i=0;i<MaxLen-1;i++){
        if(observers[i]!=-1){
		    if(strcmp(o_usernames[i], to) == 0){
				send(observers[i], &msgLength, 2, MSG_DONTWAIT|MSG_NOSIGNAL);
				send(observers[i], buf, msgLength, MSG_DONTWAIT|MSG_NOSIGNAL);
				sent = true;
		    }
		}
    }

	for(int i=0;i<MaxLen-1;i++){
	    if(observers[i]!=-1){
			if(strcmp(o_usernames[i], from) == 0){
				if(sent == true){
					send(observers[i], &msgLength, 2, MSG_DONTWAIT|MSG_NOSIGNAL);
					send(observers[i], buf, msgLength, MSG_DONTWAIT|MSG_NOSIGNAL);
				}else{
	                char* warning = (char *)malloc(sizeof(char*));
	                sprintf(warning, "Warning: User %s does not exist\n", to);
					warningLen = strlen(warning);
					send(observers[i], &warningLen, 2, MSG_DONTWAIT|MSG_NOSIGNAL);
					send(observers[i], warning, warningLen, MSG_DONTWAIT|MSG_NOSIGNAL);
				}
			}
		}
	}

}


void broadcastMSG (char* msg, int observers[MaxLen]){
	
	//sending length of msg as uint16_t, and the msg
	uint16_t msgLength;
	char buf[1000];
	strncpy(buf, msg, sizeof buf -1);
	msgLength = strlen(msg);
   
	for(int i=0;i<MaxLen;i++){
        if(observers[i]!=-1){
			send(observers[i], &msgLength, 2, MSG_DONTWAIT|MSG_NOSIGNAL);
			send(observers[i], buf, msgLength, MSG_DONTWAIT|MSG_NOSIGNAL);
        }
    }
}
int main(int argc, char **argv) {
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad_p, sad_o; /* structure to hold server's address */
	struct sockaddr_in cad; /* structure to hold client's address */
	struct timeval tv; //timeout structure
	int sd1, sd2, sd3, sd4; /* socket descriptors */	
	int p_port; /* participant protocol port number */
	int o_port; /* observer protocol port number */
	int highFD = 0; /* keeps track of latest input */
	int alen; /* length of address */
	int optval = 1; /* boolean value when we set socket option */
	char buf[1000]; /* buffer for string the server sends */
	char privateNameBuf[1000]; /* buffer I use for PMs */
	char privateMSGBuf[1000];/* buffer I use for PMs */
	char buf2[1000]; /* aux buffer for string the server sends */
	char spacebuf[1000]; /* aux buffer for string the server sends */
	int participants[MaxLen]; /* keep track of the participants socket id's */
	int observers[MaxLen]; /* keep track of the observers socket id's */
	int participantsTO[MaxLen]; /* keep track of the participants timeout values */
	int observersTO[MaxLen]; /* keep track of the observers timeout values */
	char* p_usernames[MaxLen]; /*Array of participant usernames */
	char* o_usernames[MaxLen]; /*Array of observer usernames */
	fd_set readFDs; //fd_set of readFDs

	if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./server server_port\n");
		exit(EXIT_FAILURE);
	}
	
	//double things for two ports

	memset((char *)&sad_p,0,sizeof(sad_p)); /* clear sockaddr structure */
	sad_p.sin_family = AF_INET; /* set family to Internet */
	sad_p.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

	memset((char *)&sad_o,0,sizeof(sad_o)); /* clear sockaddr structure */
	sad_o.sin_family = AF_INET; /* set family to Internet */
	sad_o.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

	p_port = atoi(argv[1]); /* convert argument to binary */
	if (p_port > 0) { /* test for illegal value */
		sad_p.sin_port = htons((u_short)p_port);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[1]);
		exit(EXIT_FAILURE);
	}

	o_port = atoi(argv[2]); /* convert argument to binary */
	if (o_port > 0) { /* test for illegal value */
		sad_o.sin_port = htons((u_short)o_port);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[2]);
		exit(EXIT_FAILURE);
	}

	/* Map TCP transport protocol name to protocol number */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket */
	sd1 = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	sd2 = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (sd1 < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}
	if (sd2 < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(sd1, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}

	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(sd2, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}
	/* Bind a local address to the socket */
	if (bind(sd1, (struct sockaddr *)&sad_p, sizeof(sad_p)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Bind a local address to the socket */
	if (bind(sd2, (struct sockaddr *)&sad_o, sizeof(sad_o)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(sd1, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(sd2, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}
	
	for (int i = 0; i < MaxLen; i++){
        p_usernames[i] = 0;
		o_usernames[i] = 0;
		participants[i] = -1;
		participantsTO[i] = 100;
		observers[i] = -1;
		observersTO[i] = 100;
    }

	highFD = sd2;
	alen = sizeof(cad);
    tv.tv_sec=10000000;
	//tv.tv_usec = 0;

	/* Main server loop - accept and handle requests */
	while (1) {
		memset(buf, 0, sizeof(buf));
		
		//add sockets to readfds
		FD_ZERO(&readFDs);
		FD_SET(sd1,&readFDs);
		FD_SET(sd2,&readFDs);
		
		//pay attention to sockets already found
		for(int i=0;i<MaxLen;i++){
            if(participants[i]!=-1){
                FD_SET(participants[i],&readFDs);
            }
			if(observers[i] != -1){
				FD_SET(observers[i], &readFDs);
			}
        }
		//wait for something to happen

        int activity = select(highFD+1, &readFDs, NULL, NULL, &tv);

		if (activity == -1){
			//something went wrong
			fprintf(stderr, "Select Error: %d\n", errno);
		}else if (activity == 0){
			printf("Timed out, find who to kick out\n");
			tv.tv_sec=minTimeout(participantsTO, observersTO);
		}else{

////////////////////////////////////////////////////////////////////////////////////////////////


/* decrement time passed since last activity for all timeout arrays */


/////////////////////////////////////////////////////////////////////////////////////////////////
			
			//participant connects, then thrown in readFDs to prevent blocking, set timeout value to 60 seconds
			if(FD_ISSET(sd1,&readFDs)){
				//if participant connects
				if ( (sd3=accept(sd1, (struct sockaddr *)&cad, &alen)) < 0) {
					fprintf(stderr, "Error: Accept failed\n");
					close(sd3);
				}else{

					//checking for an open spot
					for (int i = 0; i < MaxLen; i++){
		                if (participants[i] == -1){
		                    participants[i] = sd3;
							participantsTO[i] = 60;

							    tv.tv_sec=minTimeout(participantsTO, observersTO);
    							//tv.tv_usec = 0;

		                    if(highFD<sd3){
		                        highFD=sd3;
							}
		                    break;
		                }
						//if full, close connnection
		                if (i == (MaxLen - 1)){
		                    close(sd3);
		                }
		            }
				}
			}
/////////////////////////////////////////////////////////////////////////////////////////////////
			
			//observer connects, then thrown in readFDs to prevent blocking, set timeout value to 60 seconds
			if(FD_ISSET(sd2,&readFDs)){
				if ( (sd4=accept(sd2, (struct sockaddr *)&cad, &alen)) < 0) {
					fprintf(stderr, "Error: Accept failed\n");
					close(sd4);
				}else{
				
					//checking for an open spot
					for (int i = 0; i < MaxLen; i++){
		                if (observers[i] == -1){
		                    observers[i] = sd4;
							participantsTO[i] = 60;

							tv.tv_sec=minTimeout(participantsTO, observersTO);

		                    if(highFD<sd4){
		                        highFD=sd4;
							}
		                    break;
		                }
						//if full, close connection
		                if (i == (MaxLen - 1)){
		                    close(sd4);
		                }
		            }
				}
			}
/////////////////////////////////////////////////////////////////////////////////////////////////

			//chat or username came in for an observer or participant
			for (int i=0; i < MaxLen; i++){
				int r;

				//if a participant has something ready
				if(FD_ISSET(participants[i],&readFDs)){
	
					//if there is already a username left
					if (p_usernames[i] != 0){
						r = recv(participants[i], buf, sizeof(buf), 0);
			
						//participant leaves
						if (r == 0){

	                        char* left = (char *)malloc(sizeof(char*));
	                        sprintf(left, "%s has left\n", p_usernames[i]);

							//if participant leaves, then close observer connected to it
							for (int j=0; j<MaxLen-1; j++){

								//make sure the observer you are closing is correct one
								if(o_usernames[j] != 0){
									if(strcmp(o_usernames[j],p_usernames[i]) == 0){
										close(observers[j]);
										observers[j]=-1;
										observers[j]=100;
										o_usernames[j]=0;
										break;
									}
								}
							}
							close(participants[i]);
	                        p_usernames[i]=0;
	                        participants[i] =-1;
							participantsTO[i] =1000;
							broadcastMSG(left, observers);

						//else it must be a chat
						}else{
;
							//check for @ indicating private message, then deal with special case
							if (buf[0] == '@'){
								char letter;
								int k = 0;

								while(buf[k] != 32){
									letter = buf[k];
									if (letter == '@'){
									}else{
										privateNameBuf[k-1]=letter;
									}
									k++;
								}
								privateNameBuf[k]=0;
								int j = 0;
								while(buf[k] != 0){
									letter = buf[k];
									privateMSGBuf[j]=letter;
									k++;
									j++;

								}
								privateMSGBuf[j]=0;
								int numSpaces = (11 - strlen(p_usernames[i]));
								for (int l = 0; l < numSpaces; l++){
									spacebuf[l]= ' ';
								}
								spacebuf[numSpaces]=0;
								sprintf(buf2, "*%s%s: %s\n", spacebuf, p_usernames[i], privateMSGBuf);
								privateMSG(buf2, privateNameBuf,p_usernames[i], observers, o_usernames);

							//else broadcast to everyone							
							}else{
								int numSpaces = (11 - strlen(p_usernames[i]));
								for (int l = 0; l < numSpaces; l++){
									spacebuf[l]= ' ';
								}
								spacebuf[numSpaces]=0;
								sprintf(buf2, ">%s%s: %s\n",spacebuf, p_usernames[i], buf);
								broadcastMSG(buf2, observers);
							}
						}

					//Participant entered username
					}else{

						int usernameSize;
						r = recv(participants[i], &usernameSize, 1, 0);
						if (r != 0){
							r = recv(participants[i], buf, usernameSize, 0);
							char* c = (char*)malloc(sizeof(char*));
				            strcpy(c,buf);
							
							//check validity of name
							if (validUsername(c, strlen(c))){			
	
								//check if the username is already in use
								bool exists = false;
								for (int j =0; j < MaxLen-1; j++){
									if (p_usernames[j] != 0){
										if(strcmp(p_usernames[j], c) == 0){
											exists = true;
											break;
										}
									}
								}
								//if there is already a participant with that name, send T
								if (exists){
									sprintf(buf, "T");
	                        		participantsTO[i] =60;
									send(participants[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);

								//else good to go
								}else{
									p_usernames[i] = c;
	                        		participantsTO[i] =100;
									sprintf(buf, "Y");
									send(participants[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);
									sprintf(buf2, "User %s has joined \n", p_usernames[i]);
									broadcastMSG(buf2, observers);
								
								}
							//if an invalid username, send I, do not reset timer
							}else{
								sprintf(buf, "I");
								send(participants[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);
							}
						//special case where participant left and didn't enter a username
						}else{
	                        close(participants[i]);
	                        participants[i] =-1;
	                        participantsTO[i] =100;
	                        p_usernames[i]=0;

						}
					}
				}

/////////////////////////////////////////////////////////////////////////////////////////////////
				
				//if an observer has something ready
				if(FD_ISSET(observers[i],&readFDs)){

					// if there is already a username, they must be leaving
					if (o_usernames[i] != 0){
						r = recv(observers[i], buf, sizeof(buf), 0);
					
						//observer leaves
						if (r == 0){
	                        char* left = (char *)malloc(sizeof(char*));
	                        close(observers[i]);
	                        observers[i] =-1;
	                        observersTO[i] =100;
	                        o_usernames[i]=0;
						}

					//observer entered username
					}else{
						int r;
						int usernameSize;

						r = recv(observers[i], &usernameSize, 1, 0);

						if (r != 0){
							r = recv(observers[i], buf, usernameSize, 0);
							char* o = (char*)malloc(sizeof(char*));
					        strcpy(o,buf);
							//buf[usernameSize]=0;

							//check if username is being used by a participant
							bool exists = false;
							for (int j =0; j < MaxLen-1; j++){
								if (p_usernames[j] != 0){
									//Is there a valid participant to connect to with this username?
									if(strcmp(p_usernames[j], o) == 0){
										exists = true;
										break;
									}
								}
							}
							//decide whether it is taken, bad, or good
							if (exists){
								exists = false;
								int index;
								for (int j =0; j < MaxLen-1; j++){
									if (o_usernames[j] != 0){
										//check if an observer is already being used
										if(strcmp(o_usernames[j], o) == 0){
											exists = true;
											break;
										}
									}
								}
								//if there is already an observer for that participant
								if (exists){
									sprintf(buf, "T");
									/*reset timer*/
									observersTO[i] =60;
									send(observers[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);	
								//observer is good to go
								}else{
									o_usernames[i] = o;
	                        		observersTO[i] =100;
									sprintf(buf, "Y");
									send(observers[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);
								}
							//no participant has that username, send N then hangup
							}else{
								sprintf(buf, "N");
								send(observers[i],buf,1,MSG_DONTWAIT|MSG_NOSIGNAL);
								close(observers[i]);
	                        	observers[i] =-1;
	                        	observersTO[i] =100;
	                        	o_usernames[i]=0;
								break;	
							}

						//observer left without entering username
						}else{
							close(observers[i]);
	                        observers[i] =-1;
	                        observersTO[i] =100;
	                        o_usernames[i]=0;
						}
					}
				}
/////////////////////////////////////////////////////////////////////////////////////////////////
			}
		}
	}
	close(sd3);
	close(sd4);
}
