#pragma once
#include <string>
#include <list>
#include <map>
#include <ctime>
#include <cstring>
#include "Klient.h"
#include <pthread.h>
#include <chrono>
#include <thread>
using namespace std;


class Responder{


private:
	int socket;
	list<Klient*>* clients;
	
	pthread_mutex_t* clients_mutex;
	pthread_mutex_t* playingClients_mutex;
	char buf[1000];
	Klient* klient;
	
	void login(string buf);
	void logout();	
	void user_register(string buf);
	void want2Play(string buf);
	void startGame();
	void sendNewWord(string bufo);
	void checkIfAnyonePlayin(string login);
	void sendScore();
	void sendMistakes();
	void receiveMistake(string login);
	void add1point(string bufo);
	bool check_registration_validity(string nick, string login, string password);
	void send_info_code(string code);

	static bool contains(string text, char* chars);
	static list<string> split_string(string text, char sep, bool msg = false);

public:
	
	void readAndRespond();
	Responder(int socket);
	~Responder();
	
};

