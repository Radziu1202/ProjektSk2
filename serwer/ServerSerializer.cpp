#include "ServerSerializer.h"

void ServerSerializer::serialize() {
	pthread_mutex_lock(&Klient::clients_mutex);
	this->CLIENTS = Klient::CLIENTS;
	pthread_mutex_unlock(&Klient::clients_mutex);
	std::ofstream ofs("serializedServer.txt");
	boost::archive::text_oarchive oa(ofs);
	oa << *this;
	return;
}

void ServerSerializer::deserialize()
{
	std::ifstream ifs("serializedServer.txt");
	boost::archive::text_iarchive ia(ifs);
	ia >> *this;

	Klient::CLIENTS = this->CLIENTS;
	return;
}


ServerSerializer::ServerSerializer(){}


ServerSerializer::~ServerSerializer(){}
