

import java.util.List;
import java.util.Observer;
import java.util.ArrayList;


interface AlertFilter{
    public boolean filter(double price);
}

class AllAlertFilter implements AlertFilter{
    public boolean filter(double price){
        return true;
    }
}

class HighValueAlertFilter implements AlertFilter{
    public boolean filter(double price){
        return price>100;
    }
}


interface StockObserver{

    public void update(double price);
}


class MobileAlert implements StockObserver{
    private AlertFilter alertFilter;
    public MobileAlert(AlertFilter alertFilter){
        this.alertFilter = alertFilter;
    }

    public void update(double price){
        if(alertFilter.filter(price)){
            System.out.println("Mobile Alert for price : "+ price);
        }
    }
}

class StockExchange {
    private List<StockObserver> observers = new ArrayList<StockObserver>();

    public void setPrice(double amount){
        observers.forEach(it->it.update(amount));

    }

    public void addObserver(StockObserver observer){
        this.observers.add(observer);
    }
}





interface DatabaseConnection {
    public void saveTodb(double price);

    
} 

class MysqlDatabaseConnection implements DatabaseConnection{
    public void saveTodb(double price){
        System.out.println("Saving to MySql Database: " + price);
    }

}

class PostgressDatabaseConnection implements DatabaseConnection{
    public void saveTodb(double price){
        System.out.println("Saving to Postgress Database: " + price);
    }

}



class ConnectionPoolManager{
    private static volatile ConnectionPoolManager instance;
    private ConnectionPoolManager(){}

    public static ConnectionPoolManager getInstance(){
        if(instance == null){
            synchronized(ConnectionPoolManager.class){
                if(instance == null){
                  instance = new ConnectionPoolManager();  
                }
            }
        }
        return instance;
    }

    public DatabaseConnection getDatabaseConnection(String type){
        if(type.equals("MySql")){
            return new MysqlDatabaseConnection();
        }
        else{
            return new PostgressDatabaseConnection();
        }
    }

}


interface ILogger {
    void log(String message);
}

class BasicLogger implements ILogger {
    @Override
    public void log(String message) {
        System.out.println("Log: " + message);
    }
}

abstract class LoggerDecorator implements ILogger {
    protected ILogger decoratedLogger;

    public LoggerDecorator(ILogger logger) {
        this.decoratedLogger = logger;
    }
}

class TimestampLogger extends LoggerDecorator {
    public TimestampLogger(ILogger logger) {
        super(logger);
    }

    @Override
    public void log(String message) {
        String timestampedMessage = "[" + System.currentTimeMillis() + "] " + message;
        this.decoratedLogger.log(timestampedMessage);
    }
}

class SplunkLogLib{
    public void sendToSplunk(String message){
        System.out.println("Sending to Splunk: " + message);
    }
}


class SplunkLogger implements ILogger {
    private SplunkLogLib splunkLogLib;
    public SplunkLogger(SplunkLogLib splunkLogLib) {
        this.splunkLogLib = splunkLogLib;
    }

    @Override
    public void log(String message) {
        splunkLogLib.sendToSplunk(message);
    }
}



class TradingBotApplication implements StockObserver{
    private ILogger logger;
    private AlertFilter alertFilter;
    public TradingBotApplication(ILogger logger, AlertFilter alertFilter){
        this.logger = logger;
        this.alertFilter = alertFilter;
    }

    @Override
    public void update(double price) {
        logger.log("Stock price updated to: " + price);
        if(alertFilter.filter(price)){
            DatabaseConnection connection = ConnectionPoolManager.getInstance().getDatabaseConnection("MySql");
            connection.saveTodb(price);
        }
        
    }

}

public class Application {
    
    public static void main(String args[]){
        System.out.println("Starting Trading Bot Application...");

        // Setting up Logger with Timestamp
        ILogger basicLogger = new BasicLogger();
        ILogger timestampLogger = new TimestampLogger(basicLogger);

        // Setting up Alert Filter
        AlertFilter highValueAlertFilter = new HighValueAlertFilter();

        // Creating Trading Bot Application
        TradingBotApplication tradingBot = new TradingBotApplication(timestampLogger, highValueAlertFilter);

        // Setting up Stock Exchange and adding observer
        StockExchange stockExchange = new StockExchange();
        stockExchange.addObserver(tradingBot);

        // Simulating stock price updates
        stockExchange.setPrice(50.0);
        stockExchange.setPrice(150.0);
    }
}
