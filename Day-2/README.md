Objective: Make the system testable and flexible using Dependency Inversion and Interface Segregation.

Current State:

OrderProcessor creates its own dependencies (Violation of DIP).

EmailSender is a concrete class. What if we want to send an SMS instead? Or a Slack alert? You'd have to rewrite OrderProcessor to change the variable type.

The Assignment
Refactor OrderProcessor to be fully loosely coupled.

Requirements:

Apply DIP (Dependency Inversion Principle):

Remove new InventorySystem() and new EmailSender() from the OrderProcessor constructor.

Inject them via the constructor arguments.

Apply Abstraction (Interface):

EmailSender is too specific. Create an interface NotificationService with a method sendNotification(String msg).

Make EmailSender implement this interface.

Create a new class SmsSender that also implements this interface.

The Test (Proof of Decoupling):

Update OrderProcessor to depend on NotificationService, not EmailSender.

In your main method (or setup), instantiate OrderProcessor passing in an InventorySystem and an SmsSender (not Email).

Constraint:

OrderProcessor code must NOT contain the word EmailSender or SmsSender. It should only know about NotificationService.

Input: Paste the refactored OrderProcessor, the new NotificationService interface, and the main method showing how you wire it all together.