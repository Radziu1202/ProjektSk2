#pragma once
#include <boost/serialization/map.hpp>
#include "Klient.h"
#include <boost/serialization/list.hpp>

class ServerSerializer
{
private:
	std::map<std::string, Klient*> CLIENTS;

	//std::map<Message*, Klient*> UNSENT_MSGS;

	friend class boost::serialization::access;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version){

		ar & CLIENTS;
		//ar & UNSENT_MSGS;
	}
public:
	void serialize();
	void deserialize();
	ServerSerializer();
	~ServerSerializer();
};

