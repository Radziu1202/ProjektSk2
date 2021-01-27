#pragma once

#include <list>
#include <pthread.h>
#include <fstream>
#include <iostream>
#include <iterator>
#include <string>
#include <algorithm>

using namespace std;

class Game{
private:
    bool running=false;
    list<string> slowa;
	
	string keyWord;


public:
	int mistakes;
	static list<string> TOGUESS;
	static bool ENABLE;
  static pthread_mutex_t toguess_mutex;
  static pthread_mutex_t enable_mutex;
	list<string> words4clients;
	Game();
    ~Game();
    list<string> readFile();
    string findRandomWord(list<string> words, list<string> alreadylisted);
   	

   	const list<string> &getSlowa();

   	string getKeyWord();
   	void setSlowa(list<string> newSlowa);

   	void setKeyWord(string word);

};
