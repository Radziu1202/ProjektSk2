#pragma once
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <map>
#include <boost/archive/text_oarchive.hpp>
#include <boost/archive/text_iarchive.hpp>
#include "Game.h"

class Klient {
public:
	//login, klient
	static std::map<std::string, Klient*> CLIENTS;
	static std::list<Klient *> PLAYINGCLIENTS;
	static pthread_mutex_t clients_mutex;
	static pthread_mutex_t playingClients_mutex;
	std::string nick;
	std::string login;
	std::string password;
	bool logged_in;
	bool play;
	bool wants2play;
	int score;
	long unsigned int guessed;
	Game* game;
	int socket;


	Klient(std::string nick, std::string login, std::string password);
	Klient(){}

	static void initialize_clients();

	friend class boost::serialization::access;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & nick;
		ar & login;  
		ar & password;
	}

	std::string str_log(){
		if(this->logged_in) return "1";
		return "0";
	}

	friend std::ostream& operator<< (std::ostream& os, const Klient& klient) {
		os << klient.nick << " : " << klient.login << " : " <<
		 klient.password; 
		return os;
	}
};