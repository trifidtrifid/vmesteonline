namespace * com.vmesteonline.be.userservice
include "error.thrift"

enum CounterType { HOT_WATER=0, COLD_WATER=1, ELECTRICITY_NIGHT=2, ELECTRICITY_DAY=3, GAS=4, OTHER=5}

struct Counter {
	1:i64 id,
	2:string location, //кухня, туалет, ванная комната, холл
	3:CounterType type,
	4:string number,
}

service UserService {

	i64 registerCounter( 1:Counter newCounter )throws (1:error.InvalidOperation exc), //создание счетчика в квартире текущего пользователя
	
	list<Counter> getCounters() throws (1:error.InvalidOperation exc), //возвращает счетчики текущего пользователя
	map< Counter, double> getCounterValues() throws (1:error.InvalidOperation exc), //возвращает счетчики b b[ gjcktlybt gjrfpfybz
	map< i32, double> getCounterHistory(1:i32 counterId) throws (1:error.InvalidOperation exc), //возвращает историю показаний счетчика
	void setCurrentCounterValue(1:i64 counterId, 2:double counterValue, 3:i32 date) throws (1:error.InvalidOperation exc), //сохраняет показания счетчика
}