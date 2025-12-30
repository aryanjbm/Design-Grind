interface DatabaseConnection {

    
} 

class MysqlDatabaseConnection implements DatabaseConnection{

}

class PostgressDatabaseConnection implements DatabaseConnection{

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

public class Main {

    public static void main(String args[]){
        System.out.println("Starting Connection Pool Manager...");
        ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
        connectionPoolManager.getDatabaseConnection("MySql");
        System.out.println("MySql Database Connection Created");
    }
}
