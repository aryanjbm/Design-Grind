class InventorySystem{
    private DatabaseConnection connection;
    public InventorySystem(){
        connection = new DatabaseConnection();
    }

    public Order getOrder(Order order){
        // using connection get order
        return new Order();
    }
    public Boolean checkOrder(Order order){
        return true;
    }


}

class Order{
    private Double price;
    public Double getPrice(){
        return price;
    }
}

interface NotifcationService{
    public void sendNotification(String reciverAddress, String body);
}

class EmailNotification implements NotifcationService{

    public void sendNotification(String reciverAddress,String body){
        System.out.println("Sending Email to "+ reciverAddress + "with body :\n"+body);
    }
}

class SmsNotification implements NotifcationService{

    public void sendNotification(String reciverAddress,String body){
        System.out.println("Sending SMS to "+ reciverAddress + "with body :\n"+body);
    }
}

class DatabaseConnection{

}

interface PaymentProcessor{
    double processPayment(Order order);
}

class PaypalPaymentProcessor implements PaymentProcessor{
    public double processPayment(Order order){
        double amount = order.getPrice();
        return amount+10.0;
    }
}


class XtsPaymentProcessor implements PaymentProcessor{
    public double processPayment(Order order){
        double amount = order.getPrice();
        return amount+0.1;
    }
}


class OrderProcessor{
    private NotifcationService notificationService;
    private InventorySystem inventorySystem;

    public OrderProcessor(InventorySystem inventorySystem, NotifcationService notificationService){
       this.inventorySystem = inventorySystem;
       this.notificationService = notificationService;
    }

    public Double processOrder(Order order, PaymentProcessor paymentProcessor){
        Order orderEntity = inventorySystem.getOrder(order);
        Double amount = paymentProcessor.processPayment(orderEntity);
        notificationService.sendNotification("zys", "Paypal");
        return amount;
    }

}


