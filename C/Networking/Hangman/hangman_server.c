/*
  Adam Amr, Gavin Harris, Seth Kvam
  Program 1 Server
  CSCI 367
*/

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <ctype.h>

#define QLEN 6 /* size of request queue */
int visits = 0; /* counts client connections */

int main(int argc, char *argv[]) {

  struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad; /* structure to hold server's address */
	struct sockaddr_in cad; /* structure to hold client's address */
	int sd, sd2; /* socket descriptors */
  int g; //incoming guess
  char guess;
  int n; //
	int port; /* protocol port number */
	int alen; /* length of address */
	int optval = 1; /* boolean value when we set socket option */
	char buf[1000]; /* buffer for string the server sends */

  if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./prog1_server server_port word\n");
		exit(EXIT_FAILURE);
	}

  int word_size = strlen(argv[2]);
  char* word_check = argv[2];
  for (int i = 0; i < word_size; i++){
    if(!isalpha(word_check[i])){
      fprintf(stderr, "Error: Please enter in only characters a-z\n");
      exit(EXIT_FAILURE);
    }
  }

  memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
	sad.sin_family = AF_INET; /* set family to Internet */
	sad.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

  port = atoi(argv[1]); /* convert argument to binary */
	if (port > 0) { /* test for illegal value */
		sad.sin_port = htons((u_short)port);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[1]);
		exit(EXIT_FAILURE);
	}
  /* Map TCP transport protocol name to protocol number */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket */
	sd = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}

	/* Bind a local address to the socket */
	if (bind(sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(sd, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}

	/* Main server loop - accept and handle requests */
	while (1) {
    if (listen(sd, QLEN) < 0) {
      fprintf(stderr,"Error: Listen failed\n");
      exit(EXIT_FAILURE);
    }
    alen = sizeof(cad);
    if ( (sd2=accept(sd, (struct sockaddr *)&cad, &alen)) < 0) {
      fprintf(stderr, "Error: Accept failed\n");
    }else{
      signal(SIGCHLD, SIG_IGN);
      pid_t pid = fork();
      switch(pid){
        case -1:
          perror("fork error");
          break;

        case 0: ;
          uint8_t length;
          uint8_t guesses;
          char* word;
          word = argv[2];
          for (int i = 0; i < strlen(word); i++){
            word[i] = tolower(word[i]);
          }
          length = strlen(word); // K
          guesses = length;
          char* blankword = (char*)malloc(length + 1);

          // Makes the blank word to send to client
          for (int i = 0; i < length; i++){
            blankword[i] = '_';
          }

          blankword[length] = '\0'; // null terminator
          bool word_guessed = false;
          while(guesses > 0 && !word_guessed){

            //send client number of guesses remaining and the board
            send(sd2, &guesses, sizeof(guesses), 0);
            sprintf(buf, "%s", blankword); //put blankword onto buf
        		n = send(sd2,buf,length,0); //send buf to client

            //receive guess from client
            g = recv(sd2, &guess, sizeof(guess), MSG_WAITALL);

            // Check if guess is correct
            bool correct_guess = false;
            for (int i = 0; i < length; i++){
              if(guess == word[i] && blankword[i] == '_'){
                blankword[i] = word[i];
                correct_guess = true;
              }
            }

            //if wrong guess, decriment
            if (!correct_guess){
              guesses--;
            }

            // Check if word has been guessed
            word_guessed = true;
            for (int i = 0; i < length; i++){
              if (blankword[i] == '_'){
                word_guessed = false;
              }
            }
          }

          //if game is over, send 255 if they won or 0 if they lost
          if(word_guessed){ // send 255
            uint8_t won = 255;
            send(sd2, &won, sizeof(won), 0);
            n = send(sd2,word,length,0);
          } else if (guesses == 0) { // send 0
            uint8_t lost = 0;
            send(sd2, &lost, sizeof(lost), 0);
            n = send(sd2,buf,length,0);
          }

          close(sd2);
          break;

        default:
          break;
      }
    }
	}
  return EXIT_SUCCESS;
}
