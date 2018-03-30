/*
  Adam Amr, Gavin Harris, Seth Kvam
  Program 1 Client
  CSCI 367
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <ctype.h>

int main( int argc, char **argv) {
	struct hostent *ptrh; /* pointer to a host table entry */
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad; /* structure to hold an IP address */
	int sd; /* socket descriptor */
	int port; /* protocol port number */
	char *host; /* pointer to host name */
	int n; /* number of characters read */
	char buf[1000]; /* buffer for data from the server */

	memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
	sad.sin_family = AF_INET; /* set family to Internet */

	if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./client server_address server_port\n");
		exit(EXIT_FAILURE);
	}

	port = atoi(argv[2]); /* convert to binary */
	if (port > 0) /* test for legal value */
		sad.sin_port = htons((u_short)port);
	else {
		fprintf(stderr,"Error: bad port number %s\n",argv[2]);
		exit(EXIT_FAILURE);
	}

	host = argv[1]; /* if host argument specified */

	/* Convert host name to equivalent IP address and copy to sad. */
	ptrh = gethostbyname(host);
	if ( ptrh == NULL ) {
		fprintf(stderr,"Error: Invalid host: %s\n", host);
		exit(EXIT_FAILURE);
	}

	memcpy(&sad.sin_addr, ptrh->h_addr, ptrh->h_length);

	/* Map TCP transport protocol name to protocol number. */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket. */
	sd = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Connect the socket to the specified server. */
	if (connect(sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
		fprintf(stderr,"connect failed\n");
		exit(EXIT_FAILURE);
	}

	uint8_t guesses;
	char guess;

	//receive initial board and guesses from server
	n = recv(sd, &guesses, sizeof(guesses), 0);
	char board[guesses + 1];
	n = recv(sd, &board, sizeof(board) - 1, MSG_WAITALL);
	board[guesses] = 0;

  // keep listening for updated guesses/board from server
	while(guesses != 0 && guesses != 255){

		//display board and prompt for user
		printf("Board: %s (%d guesses left)\n", board, guesses);
		printf("Enter Guess: ");

		//receive user input and send guess to server
		scanf(" %c", &guess);
		while(!isalpha(guess)){
			printf("Please enter a letter a-z\n");
			printf("Enter Guess: ");
			scanf(" %c", &guess);
		}
		send(sd, &guess, sizeof(guess), 0);

		//receive updated info from last guess
		n = recv(sd, &guesses, sizeof(guesses), 0);
		n = recv(sd, &board, sizeof(board) - 1, MSG_WAITALL);

	}

	//if we popped out of the loop game is over, either won or lost
	if (guesses == 0) {
		printf("Board: %s\nYou lost\n", board);
	} else if (guesses == 255) {
		printf("Board: %s\nYou won\n", board);
	}

	close(sd);

	exit(EXIT_SUCCESS);
}
