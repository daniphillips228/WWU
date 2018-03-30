/* prog3_participant */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

/*------------------------------------------------------------------------
* Program: prog3_participant
*
* Purpose: allocate a socket, connect to a server, and send user input to server
*
* Syntax: ./prog3_participant server_address server_port
*
* server_address - name of a computer on which server is executing
* server_port    - protocol port number server is using
*
*------------------------------------------------------------------------
*/
int main( int argc, char **argv) {
	struct hostent *ptrh; /* pointer to a host table entry */
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad; /* structure to hold an IP address */
	int sd; /* socket descriptor */
	int port; /* protocol port number */
	char *host; /* pointer to host name */
	int n; /* number of characters read */
	char buf[1000]; /* buffer for data from the server */
	char buf2[1000]; /* buffer for data from the server */

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

	uint8_t nameLength;
	char* username;
	bool valid = false;
	while (!valid){
		printf("Enter Username: ");
		scanf("%s",username);
		nameLength = strlen(username);

		//username too long
		if (nameLength > 10){
			printf("Too long\n");
		}else{

			for (int i = 0; i < nameLength; i++){
				//illegal chars
				if ((username[i] > 122) || (username[i] < 97 && username[i] > 90) || (username[i] < 65 && username[i] > 57) || (username[i] < 48)){
					printf("Illegal characters\n");
					break;
				}else{
					//sending length of username as uint8_t, and the username

					send(sd, &nameLength, 1, 0);
					send(sd, username, nameLength, 0);

					n = recv(sd, buf, sizeof(buf), 0);

					//if sent Y
					if (buf[0] == 89){
						buf[1] =0;
						printf("%s\n", buf);
						valid = true;
						break;
					//if sent I
					}else if(buf[0] == 73){
						buf[1] =0;
						printf("%s\n", buf);
					//if sent T
					}else if(buf[0] == 84){
						buf[1] =0;
						printf("%s\n", buf);
						break;
					}
				}
			}
		}
	}

	while (n > 0) {
		int size;
		fgets(buf2, sizeof(buf2), stdin);
		//size = sizeof(buf2);
		//printf("****** size of buf2 = %d ************", size);
		size = strlen(buf2);
		//printf("****** size of buf2 = %d ************", size);
		if(size > 1000){
			printf("\nMessage too long");
		}else{
			
			send(sd, buf2, strlen(buf2),0);
			printf("\nEnter Message: ");
		}
	}

	close(sd);

	exit(EXIT_SUCCESS);
}

