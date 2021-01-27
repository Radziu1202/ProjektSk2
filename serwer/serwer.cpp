#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include "Klient.h"
#include <list>
#include <string>
#include <iostream>
#include <fstream>
#include <utility>
#include "Responder.h"
#include "ServerSerializer.h"
#include <signal.h>
#include <error.h>



	
#define QUEUE_SIZE 7
bool WORK = true;
int SERVER_PORT;
//struktura zawierająca dane, które zostaną przekazane do wątku
/*struct thread_data_t
{
	char buf[1000];					//TU ZAKOMENTOWANA STRUKTURA
	int socket;
};
*/

//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *)
void *ThreadBehavior1(void *t_data){
	int socket = *((int *) t_data);
    free(t_data);
	pthread_detach(pthread_self());
	//int* socket = (int*) t_data;
	Responder *resp = new Responder(socket);
	resp->readAndRespond();

	delete resp;
	close(socket);

	cout << "Connection lost on socket " << socket << endl;
	pthread_exit(NULL);
}


//funkcja obsługująca połączenie z nowym klientem
void handleConnection(int csd) {
	//wynik funkcji tworzącej wątek
	int *arg =(int*) malloc(sizeof(*arg));
	*arg = csd;
	pthread_t thread2;
	pthread_create(&thread2, NULL, ThreadBehavior1, arg);
}


void* connection_accepter(void *server_socket){
	//pthread_detach(pthread_self());
	int* server_socket_descriptor = (int*) server_socket;
	while(1){
		int connection_socket_descriptor = accept(*server_socket_descriptor, NULL, NULL);
		cout << "New connection on socket " << connection_socket_descriptor<< endl;
		handleConnection(connection_socket_descriptor);
	}
}




int main(int argc, char** argv)
{	
	if(argc!=2)
        error(1,0,"Usage: %s <port>", argv[1]);

	SERVER_PORT=atoi(argv[1]);
	cout<<SERVER_PORT<<endl;
	int server_socket_descriptor;
	int bind_result;
	int listen_result;
	char reuse_addr_val = 1;
	sockaddr_in server_address;

	//inicjalizacja gniazda serwera
	memset(&server_address, 0, sizeof(struct sockaddr));
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr = htonl(INADDR_ANY);
	server_address.sin_port = htons(SERVER_PORT);

	server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
	
	if (server_socket_descriptor < 0)
	{
		fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda..\n", argv[0]);
		exit(1);
	}

	setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, (char*)&reuse_addr_val, sizeof(reuse_addr_val));

	bind_result = bind(server_socket_descriptor, (struct sockaddr*)&server_address, sizeof( sockaddr));
	if (bind_result < 0)
	{
		fprintf(stderr, "%s: Błąd przy próbie dowiązania adresu IP i numeru portu do gniazda.\n", argv[0]);
		exit(1);
	}

	listen_result = listen(server_socket_descriptor, 100);
	if (listen_result < 0) {
		fprintf(stderr, "%s: Błąd przy próbie ustawienia wielkości kolejki.\n", argv[0]);
		exit(1);
	}

	pthread_t thread;
	pthread_create(&thread, NULL, connection_accepter, &server_socket_descriptor);

	ServerSerializer s;
	std::ifstream pFile("serializedServer.txt", std::ifstream::in);
	bool empty = pFile.peek() == ifstream::traits_type::eof();
	if(!empty) s.deserialize();
	cout << "Entering the loop\nTo stop the server press c+Enter\n";
	char c;
	while (WORK)
	{	
		
		cout<<Klient::CLIENTS.size()<<endl;
		cin >> c;
		if(c == 'c'){
			s.serialize();

			WORK = false;

		}else if(c == 'k'){
			for (auto const& x : Klient::CLIENTS){
				std::cout << *x.second  << endl; 
			}
		}
		else if(c == 'd'){
			string login;
			cout << "login: ";
			cin >> login;
			pthread_mutex_lock(&Klient::clients_mutex);
			std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
			if (klient_it != Klient::CLIENTS.end())
				Klient::CLIENTS.erase(login);
			pthread_mutex_unlock(&Klient::clients_mutex);
		}
		else if(c == 'h'){
			cout << "k - wyświetl klientów" << endl;
			cout << "d - usuń klienta" << endl;
			cout << "c - serializuj i wyłącz serwer" << endl;
		}
		sleep(1);
	}
	return(0);
}


