
#include "Responder.h"

#define CODE_SEND_MISTAKES "400|"
#define LOGIN_RESPONSE "401|"
#define LOGOUT_RESPONSE "402|"
#define REGISTER_RESPONSE "403|"
#define CODE_WANTS2PLAY "406|"
#define CODE_START_GAME "407|"
#define CODE_NEW_WORD "408|"
#define CODE_BACK2MENU "409|"
#define CODE_SEND_SCORE "410|"

using namespace std;
Responder::Responder(int socket){
	this->socket = socket;			//przypisujem do respondera socket jednego z klientów 
	srand(time(NULL));
}

Responder::~Responder(){}


struct Buffer {
        Buffer() {data = (char*) malloc(len);}
        Buffer(const char* srcData, ssize_t srcLen) : len(srcLen) {data = (char*) malloc(len); memcpy(data, srcData, len);}
        ~Buffer() {free(data);}
        Buffer(const Buffer&) = delete;
        void doube() {len*=2; data = (char*) realloc(data, len);}
        ssize_t remaining() {return len - pos;}
        char * dataPos() {return data + pos;}
        char * data;
        ssize_t len = 64;
        ssize_t pos = 0;
    };


void Responder::readAndRespond(){
	Buffer readBuffer;
    list<Buffer> dataToWrite;																//dopasowanie
    ssize_t count;																			// odebranego komunikatu wysyłanego przez klienta
	while ((count=read(this->socket, readBuffer.dataPos(), readBuffer.remaining()))>0) {	//np przypisanie do odpowiednich zmiennych takich jak "kod"
		printf("Received: %s ", readBuffer.data);
		readBuffer.pos += count;
		string bufo=readBuffer.data;
		bufo = bufo.substr(0,readBuffer.pos);

        char * eol = (char*) memchr(readBuffer.data, '\n', readBuffer.pos);
        if(eol == nullptr) {
            if(0 == readBuffer.remaining())
                readBuffer.doube();
        }
        else{
            do {
                auto thismsglen = eol - readBuffer.data + 1;
                auto nextmsgslen =  readBuffer.pos - thismsglen;
                memmove(readBuffer.data, eol+1, nextmsgslen);
                readBuffer.pos = nextmsgslen;
            } while((eol = (char*) memchr(readBuffer.data, '\n', readBuffer.pos)));

        }

		if(bufo.at(0)=='4') continue;
		bufo = bufo.substr(0, bufo.length()-1);
		if(!isalpha(bufo.back()) && !isdigit(bufo.back()) && bufo.back()!='|'){
			bufo = bufo.substr(0, bufo.length()-1);
		} 
   
		cout << bufo <<  ", dlugosc:" << bufo.length() << endl;
		
		if(bufo.length() < 4) continue;
		if(!isdigit(bufo.at(0)) || (!isdigit(bufo.at(1))) || (!isdigit(bufo.at(2))) ||
			!(bufo.at(3) == '|')) continue;

		int kod = stoi(bufo.substr(0, 3));
		cout<<"KOD "<<kod<<endl;
		bufo = bufo.substr(4, bufo.length() - 4);
		
		switch (kod){			//------------------poniżej switch ktory na podstawie kodu wysłanego przez klienta wybiera
		case 101:				// 				jakie działanie ( metoda) ma nastąpić 
			login(bufo);
			break;
		case 102:
			logout();
			break;
		case 103:
			user_register(bufo);
			break;
		case 104:
			add1point(bufo);
			break;
		case 105:
			receiveMistake(bufo);
			//checkLetter(bufo);
			break;
		case 106:
			want2Play(bufo);
			break;
		case 107:
			sendNewWord(bufo);
			break;
		case 108:
			checkIfAnyonePlayin(bufo);
			break;
		case 109:
			startGame();
			break;
		default:
			break;
		}

	}
	if(this -> klient) logout();// poprawiono - przy zabiciu procesu klienta logout() zostaje wywołane

}


void Responder::login(string buf){  // metoda sprawdzająca czy podane w oknie logowania dane znajdują się w liście klientów
	
	list<string> data = split_string(buf, '|');		
	string login = data.front(); data.pop_front();
	string password = data.front(); data.pop_front();
	cout << "Logowanie: " << login << " " << password<<"KONIEC"<<endl;
	pthread_mutex_lock(&Klient::clients_mutex); 
	map<string,Klient*>::iterator klient = Klient::CLIENTS.find(login);
	if (klient!=Klient::CLIENTS.end() && klient->second->password == password && !klient->second->logged_in)
	{
		pthread_mutex_unlock(&Klient::clients_mutex); //dostęp do listy klientów jest chroniony mutexem
		klient->second->logged_in = true;
		klient->second->socket = this->socket;
		this->klient = klient->second;
		string temp ="Udane|" +this->klient->nick + "|" ;
	
		if(temp.at(temp.length()-1) == ',') temp = temp.substr(0, temp.length() - 1);
		cout << "Udane\n";
									//jeżeli dane są zgodne z danymi w liście wysyłany jest komunikat o poprawności logowania
		send_info_code(LOGIN_RESPONSE+string(temp));

		return;
	
	}
	else{
		pthread_mutex_unlock(&Klient::clients_mutex);
		cout << "Nieudane\n";		//jeżeli dane nie są zgodne z danymi w liście wysyłany jest komunikat o niepoprawnym logowaniu 
		send_info_code(LOGIN_RESPONSE+string("0"));
	}
	
	return;
}


void Responder::add1point(string bufo){		//metoda dodaje punkt na konto gracza który poprawnie zgadł literę 
	if (this -> klient && this->klient->logged_in){ // oraz wysyłą informacje o jego obecnym wyniku do innych użytkowników
		this->klient->score++;
		sendScore();
	}
}

void Responder::logout(){   //metoda zmienia wartości pól gracza podczas wylogowywania (zamknięcia klienta)
	if (this -> klient && this->klient->logged_in) {
		cout << "Logout: " << this->klient->login << endl;
		this->klient->score=0;			// zeruje jego wynik a nastepnie wysyłą go do innych użytkowników
		sendScore();
		this->klient->logged_in = false;		//zmienia wartość "zalogowany" na false
		this->klient->play=false;
		this->klient->game=NULL;				//przypisuje NULL do pola "gra" ( gracz ktory sie wyloguje poprzez zamkniecie klienta nie moze brac udzialu w grze)
		this->klient->wants2play=false;
		this->klient->guessed=0;
		pthread_mutex_lock(&Klient::playingClients_mutex); 
		Klient::PLAYINGCLIENTS.remove(this->klient);	//usuwamy klienta z listy osob majacych wyswietlone okno gry
		pthread_mutex_unlock(&Klient::playingClients_mutex); 
		pthread_mutex_lock(&Klient::clients_mutex);		
		int counter=0;
		for(auto const& klient : Klient::CLIENTS){			//liczymy ile jest osob ktore są chetne do gry
			if(klient.second->wants2play){				//(wliczając osoby bedące obecnie w grze)
				counter++;
			}
		}
		string code = CODE_WANTS2PLAY+ to_string(counter)+"\n";
		const char* c = code.c_str();
		for(auto const& klient : Klient::CLIENTS) {		//wysylamy informacje o liczbie chetnych graczy do 
			if (klient.second->wants2play){				//tych własnie graczy w celu zaktualizowania informacji o liczbie
				write(klient.second->socket,c,code.length());	//graczy na ich oknach klienta
			}
		}
		pthread_mutex_unlock(&Klient::clients_mutex);

		pthread_mutex_lock(&Klient::playingClients_mutex); 	
		counter=0;		
		for(auto const& klient : Klient::PLAYINGCLIENTS) {			//sprawdzamy ilu graczy (po wylogowaniu tego klienta) 
			if (klient->play){counter++;}							//nadal zgaduje slowa
		}

		if (counter==0){			// jesli liczba zgadujacych jest równa 0	
			Game::ENABLE=true;		//to graczy ktorzy zbudowali całą szubienice przenosimy do menu ( wysylajac do nich sygnal o numerze 409)
			Game::TOGUESS={};		//a także odblokowujemy mozliwosc rozpoczecia rogrywki i usuwamy dotychczasowo wylosowane slowa
			string code =CODE_BACK2MENU+string("GoToMenu\n");
			const char* c = code.c_str();
			for(auto const& klient : Klient::PLAYINGCLIENTS){
				write(klient->socket,c,code.length());
			}
		}
		pthread_mutex_unlock(&Klient::playingClients_mutex); 
		this->klient->socket = -1;
		this->klient = NULL;		// usuwamy przypisanie klienta do tego respondera oraz jego gniazdo
		
		cout << "Udane\n";
		return;
	}
	else {
		cout << "Nieudane\n";
		send_info_code(LOGOUT_RESPONSE+string("0"));
		return;
	}

}

void Responder::user_register(string buf){  // metoda obsługująca proces rejestracji 
	list<string> data = split_string(buf, '|');		//na podstawie przesłanych danych tworzy obiekt klasy Klient
	string nick = data.front(); data.pop_front();
	string login = data.front(); data.pop_front();
	string password = data.front(); data.pop_front();
	
	cout << "Rejestracja: " << nick << " " << login << " " << password << endl;
	bool valid = check_registration_validity(nick, login, password);
	if (valid) {
		cout << "Udane\n";
		Klient* k = new Klient(nick, login, password);
		cout<<"created new accout "<<k->login<<endl;
		send_info_code(REGISTER_RESPONSE+string("1"));
	}
	else{
		cout << "Nieudane\n";
		send_info_code(REGISTER_RESPONSE+string("0"));
	} 
	return;
}


void Responder::receiveMistake(string login){		//metoda która po odebraniu informacji o blędzie uzytkownika
	if(this->klient && this->klient->login ==login){
		this->klient->game->mistakes++;		//zwieksza liczbe błedow uzytkownika na serwerze
		sendMistakes();		//oraz wysyła informacje o liczbie błędów gracza  do innych graczy ( w celu narysowania szubienicy)
	}
}

void Responder::sendScore(){	// metoda wysyłająca do graczy mających wyswietlone okno rozgrywki wynika gracza
	if(this->klient && this-> klient->logged_in && this->klient->play){
		string code =CODE_SEND_SCORE;
		code=code+this->klient->login+"@"+to_string(this->klient->score)+"\n";
		const char* c = code.c_str();
		pthread_mutex_lock(&Klient::playingClients_mutex); 
		for(auto const& klient : Klient::PLAYINGCLIENTS) {
			//if (klient.second->play)
			write(klient-> socket, c,code.length());
		}
		pthread_mutex_unlock(&Klient::playingClients_mutex); 
			
	}
}


void Responder::sendMistakes(){ //metoda wysyła do graczy mających wyswietlone okno rozgrywki liczbe błedow gracza 
	if(this->klient && this->klient->logged_in && this->klient->play){
		string code=CODE_SEND_MISTAKES;
		code=code+this->klient->login+"@"+to_string(this->klient->game->mistakes)+"\n";
		const char* c = code.c_str();
		pthread_mutex_lock(&Klient::playingClients_mutex); 
		for(auto const& klient : Klient::PLAYINGCLIENTS) {
			if ( klient->login != this->klient->login)
				write(klient-> socket, c,code.length());
		}
		pthread_mutex_unlock(&Klient::playingClients_mutex); 
	}
}


 void Responder::startGame(){ //metoda odpowiadająca za wysłanie do graczy informacji o mozliwosci rozpoczecia gry
	int counter=0;
		pthread_mutex_lock(&Klient::clients_mutex);   
		for(auto const& klient : Klient::CLIENTS){
			if(klient.second->wants2play){				//zliczamy liczbe chetnych do gry uzytkoników
				counter++;
			}
		}
		pthread_mutex_unlock(&Klient::clients_mutex); 

	pthread_mutex_lock(&Game::enable_mutex); 
	if (counter>=2 && Game::ENABLE){			//jezeli ta liczba wynosi co najmniej 2 oraz flaga umozliwiajaca gre ma wartość true
		this->klient->game=new Game();			//gra sie rozpoczyna
		this->klient->game->readFile();
		string code =CODE_START_GAME;

		pthread_mutex_lock(&Game::toguess_mutex);
		list<string> slowa_do_zgadniecia=this->klient->game->getSlowa(); 
		string word=this->klient->game->findRandomWord(slowa_do_zgadniecia,Game::TOGUESS); //losujemy slowo
		Game::TOGUESS.push_back(word);			//dodajemy je na liste wylosowanych slow ( aby sie nie powtórzyło)
		pthread_mutex_unlock(&Game::toguess_mutex); 
		code =code +word +"\n"; 
		const char* c = code.c_str();
		pthread_mutex_lock(&Klient::clients_mutex);
		for(auto const& klient : Klient::CLIENTS) {		//a nastepnie wysyłamy je do pozostałych graczy 
			if(klient.second->wants2play){				
				Klient::PLAYINGCLIENTS.push_back(klient.second);
				klient.second->game=new Game();
				klient.second->game->setSlowa(slowa_do_zgadniecia);
				klient.second->play=true;		//zmieniamy wartosc pola play kazdego z nich 
				klient.second->game->setKeyWord(word); // ustawiamy wylosowane slowa na slowo klucz kazdego z nich
				write(klient.second->socket,c,code.length());
			}
		}
		pthread_mutex_unlock(&Klient::clients_mutex);
		
		Game::ENABLE=false;		//blokujemy mozliwosc rozpoczecia rozgrywki ( np dla osób dołączających pozniej)
	}
	pthread_mutex_unlock(&Game::enable_mutex);

	
}


void Responder::checkIfAnyonePlayin(string login){  //metoda sprawdza czy po przegraniu przez nas gry( zbudowaniu szubienicy)
	int counter=0;									// są jeszcze inni gracze którzy jej nie zbudowali ( nie przegrali)
	pthread_mutex_lock(&Klient::playingClients_mutex); 
	for(auto const& klient : Klient::PLAYINGCLIENTS) {
		if (klient->login==login){
			klient->play=false;
			klient->score=0;
		}
		if (klient->play){counter++;}
	}
	pthread_mutex_unlock(&Klient::playingClients_mutex);
	if (counter ==0){	//jesli nie to przenosimy sie do menu orazy wysylamy komunikat do innych graczy aby zrobili to samo 
		Game::ENABLE=true;		//oraz odblokowujemy mozliwosc rozpoczecia rozgrywki
		Game::TOGUESS={};
		string code =CODE_BACK2MENU+string("GoToMenu\n");		

		const char* c = code.c_str();
		pthread_mutex_lock(&Klient::playingClients_mutex); 
		for(auto const& klient : Klient::PLAYINGCLIENTS) {
			write(klient->socket,c,code.length());
		}
		pthread_mutex_unlock(&Klient::playingClients_mutex); 

	}
	else{send_info_code(CODE_BACK2MENU+string("0"));}

}

 

void Responder::sendNewWord(string bufo){		//metoda wysyłająca nowe słowo do wszystkich graczy po tym jak jeden z nich zgadnie obecne słowo
	
	list<string> newSlowa=this->klient->game->getSlowa();

	newSlowa.remove(this->klient->game->getKeyWord());
	this->klient->game->setSlowa(newSlowa);

	pthread_mutex_lock(&Game::toguess_mutex);			//blokujemy mozliwosc aby dwoch graczy naraz zgadło to słowo jako pierwsi
	cout<<"moje keyword "<< this->klient->game->getKeyWord()<<" toguess back: "<<Game::TOGUESS.back()<<endl;
	if (this->klient->game->getKeyWord()==Game::TOGUESS.back()){

		this->klient->guessed++;
		string code =CODE_NEW_WORD;

		this->klient->score+=2;  //najszybszemu graczowi dodajemy bonus +2pkt 
		this->klient->game->mistakes=0;		
		sendScore();		
		sendMistakes();
		cout<<"ERROR!"<<endl;
		string newWord=this->klient->game->findRandomWord(this->klient->game->getSlowa(),Game::TOGUESS);
		cout<<"EROROR@@@"<<endl;
		Game::TOGUESS.push_back(newWord);
		code =code +newWord;
		code=code+"\n";
		const char* c = code.c_str();
		pthread_mutex_lock(&Klient::clients_mutex); 
		for(auto const& klient : Klient::CLIENTS) {
			if (klient.second->play){
				klient.second->game->setSlowa(newSlowa);
				klient.second->game->setKeyWord(newWord);
				klient.second->game->mistakes=0; //zerujemy błedy wszystkich graczy z poprzedniego slowa
				cout<<"NOWE SLOWO TRAFIA DO "<<klient.second->login<<endl;
				write(klient.second->socket,c,code.length());  //wysyłamy nowe słowo do kazdego
			}  
		pthread_mutex_unlock(&Klient::clients_mutex); 
		}
	}
	pthread_mutex_unlock(&Game::toguess_mutex);
}


void Responder::want2Play(string buf){  //metoda zmienia wartość pola wants2play klieta jesli ten wyrazi chec gry
	string login =buf;					// informuje o tym takze innych uzytkowników
	if (this->klient && this->klient->logged_in && this->klient->wants2play==false) {
		this->klient->wants2play=true;
		pthread_mutex_lock(&Klient::clients_mutex);
		
		int counter=0;
		
		for(auto const& klient : Klient::CLIENTS){
			if(klient.second->wants2play){
				counter++;
			}
		}


		string code = CODE_WANTS2PLAY+ to_string(counter)+"\n";
		const char* c = code.c_str();
		for(auto const& klient : Klient::CLIENTS) {
			if (klient.second->wants2play){
				write(klient.second->socket,c,code.length());
			}
		
		}
		pthread_mutex_unlock(&Klient::clients_mutex);
		return;
	}
	cout << "Nieudane\n";
	send_info_code(CODE_WANTS2PLAY+string("0"));
	return;
}

list<string> Responder::split_string(string text, char sep, bool msg) { //metoda pomocnicza ma na celu obróbke tekstu
	string temp = "";
	list<string> list;
	for (size_t i = 0; i < text.length(); i++) {
		if (text[i] != sep || (msg && list.size() == 1))
			temp = temp + text[i];
		else {
			list.push_back(string(temp));
			temp.clear();
		}
	}
	list.push_back(string(temp));
	return list;
}

void Responder::send_info_code(string code) { //metoda wysyłająca prosty kod do klienta
	cout << code << endl;
	code += "\n";
	const char* c = code.c_str();
	write(this -> socket, c, code.length());

	return;
}

bool Responder::check_registration_validity(string nick, string login, string password) //metoda sprawdzająca poprawnosc rejestracji
{
	char separators[] = { ',', '.', '|', '\\', '\'', '\"'};
	bool n = nick.length() > 1 && !contains(nick, separators);
	bool l = login.length() > 1 && !contains(login, separators);
	bool p = password.length() > 1 && !contains(password, separators);
	pthread_mutex_lock(&Klient::clients_mutex);
	map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
	bool nz = (klient_it == Klient::CLIENTS.end());
	pthread_mutex_unlock(&Klient::clients_mutex);
	return n && l && p && nz;
}

bool Responder::contains(string text, char* chars) { //metoda  sprawdzająca zawieranie sie znaków w stringu (pomocnicza)
	for (size_t i = 0; i < strlen(chars); i++) {
		for (size_t j = 0; j < text.length(); j++) {
			if (text[j] == chars[i]) return true;
		}
	}
	return false;
}


