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

class OrderProcessor{
    private InventorySystem inventorySystem;
    private EmailSender emailSender;

    public OrderProcessor(){
        inventorySystem = new InventorySystem();
        emailSender = new EmailSender();
    }

    public Double processOrder(Order order, String paymentMode){
        Order orderEntity = inventorySystem.getOrder(order);
        Double amount = 0.0;
        if(inventorySystem.checkOrder(order)){            
            amount = orderEntity.getPrice();
        }
        else{
            return 0.0;
        }
        if(paymentMode== "Paypal"){
            amount+= 10.0;
        }
        else if(paymentMode=="XTS"){
            amount+=0.1;
        }
        emailSender.sendEmail("zys", "Paypal");
        return amount;
    }

}


