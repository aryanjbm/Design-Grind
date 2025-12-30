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

class EmailSender{

    public void sendEmail(String emailaddress,String body){
        System.out.println("Sending Email to "+ emailaddress + "with body :\n"+body);
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
    private InventorySystem inventorySystem;
    private EmailSender emailSender;

    public OrderProcessor(){
        inventorySystem = new InventorySystem();
        emailSender = new EmailSender();
    }

    public Double processOrder(Order order, PaymentProcessor paymentProcessor){
        Order orderEntity = inventorySystem.getOrder(order);
        Double amount = paymentProcessor.processPayment(orderEntity);
        emailSender.sendEmail("zys", "Paypal");
        return amount;
    }

}


