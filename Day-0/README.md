Create a class file named OrderProcessor (or OrderService). Inside it, write a single public method called processOrder(Order order, String paymentMode).

Your code MUST violate the following:

SRP (Single Responsibility): This method must do ALL of the following inline:

Validate the order (check if items exist).

Calculate the total price + tax logic.

Connect to a database (mock the connection logic with print statements, but strictly use new DatabaseConnection() inside the method).

Send an email notification (mock with print, but logic must be here).

OCP (Open/Closed):

Handle payments using a hard if-else or switch block inside the method.

if (paymentMode == "CreditCard") { ... }

else if (paymentMode == "PayPal") { ... }

DIP (Dependency Inversion):

Do not use Dependency Injection.

Instantiate your helper classes (e.g., EmailSender, InventorySystem) directly inside the processOrder method using the new keyword.

The Constraints
Time Limit: 45 minutes.

Outcome: The code must compile/run.

Mental Check: If you feel the urge to create an interface or a separate class for "TaxCalculation," stop. You are failing the assignment. Force the spaghetti logic.