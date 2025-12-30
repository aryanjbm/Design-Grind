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

public class StockExchange {
    private List<StockObserver> observers = new ArrayList<StockObserver>();

    private void setPrice(double amount){
        observers.forEach(it->it.update(amount));

    }

    private void addObserver(StockObserver observer){
        this.observers.add(observer);
    }

    public static void main(String args[]){
        StockExchange stockExchange = new StockExchange();
        AlertFilter alertFilter = new HighValueAlertFilter();
        StockObserver stockObserver = new MobileAlert(alertFilter);
        stockExchange.addObserver(stockObserver);
        stockExchange.setPrice(10.0);
        stockExchange.setPrice(120.0);
        
    }
}
