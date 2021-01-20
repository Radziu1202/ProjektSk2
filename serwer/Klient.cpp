#include "Klient.h"
using namespace std;

pthread_mutex_t Klient::clients_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t Klient::playingClients_mutex = PTHREAD_MUTEX_INITIALIZER;
std::map<std::string, Klient*>Klient::CLIENTS;
list<Klient *> Klient::PLAYINGCLIENTS;


Klient::Klient(std::string nick, std::string login, std::string password) {
	this -> nick = nick;
	this->login = login;		//pole z loginem danego klienta
	this->password = password; //pole z hasłem danego klienta
	this->logged_in = false;	//pole informujące czy dany użytkownik jest juz zalogowany
	this->play = false;			//pole informujące czy dany gracz zgaduje jeszcze słowo
	this->wants2play=false;		//pole informujace czy dany uzytkownik chce zagrac
	this->socket = -1;			//gniazdo klienta
	this->guessed=0;			//liczba odgadniętych slow
	this->score=0;				//liczba punktów

	pthread_mutex_lock(&Klient::clients_mutex);
	Klient::CLIENTS[login] = this;
	pthread_mutex_unlock(&Klient::clients_mutex);
}

std::list<std::string> split_string(std::string text, char sep) {
	std::string temp;
	std::list<std::string> list;
	for (size_t i = 0; i < text.length(); i++) {
		if (text[i] != sep)
			temp = temp + text[i];
		else {
			list.push_back(std::string(temp));
			temp.clear();
		}
	}
	list.push_back(std::string(temp));
	return list;
}
