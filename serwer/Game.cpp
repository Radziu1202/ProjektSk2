#include "Game.h"
using namespace std;


list<string > Game::TOGUESS;
bool Game::ENABLE=true;

pthread_mutex_t Game::toguess_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t Game::enable_mutex = PTHREAD_MUTEX_INITIALIZER;
Game::Game(){
	this->slowa={};
	this->mistakes=0;
	
}


Game::~Game(){}


string Game::findRandomWord(list<string> words, list<string> alreadylisted){ //metoda losująca losowe słowa
	cout<<"words size "<<words.size()<<endl;
	list<string>::iterator it = words.begin();
	int number= rand() % words.size();
	cout<<"words size "<<words.size()<<" number "<<number<<endl;
	advance(it, number);
	
	this->keyWord=*it;
	cout<<"zwracam randomword"<<endl;
    return  *it;
}

list<string> Game::readFile(){ //metoda odczytująca plik ze słowami
    string myText;
    list<string> slowa;
    ifstream MyReadFile("words.txt");
    while (getline (MyReadFile, myText)) {
        cout<<"dodawane slowo"<<myText<<"koniec slowa"<<endl;
        this->slowa.push_back(myText);
        //cout<<myText<<endl;
    }
    cout<<this->slowa.size()<<endl;
    return slowa;

}



list<string> Game::getSlowa(){return this->slowa;}

string Game::getKeyWord(){return this->keyWord;}
void Game::setSlowa(list<string> newSlowa){
	this->slowa=newSlowa;

}

void Game::setKeyWord(string word){
   		this->keyWord=word;
   	}

