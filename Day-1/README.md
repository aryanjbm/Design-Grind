Good. It hurts to look at. That if-else block for payments is a ticking time bomb, and the new InventorySystem() in the constructor means I can't test this code without a live database connection. You have successfully failed to write good code.

Day 2: The Surgery (SRP & OCP)
Objective: Fix the Payment Logic. Current State: Every time business adds a new payment method (e.g., "Bitcoin"), you have to open OrderProcessor.java, edit the if-else chain, recompile, and risk breaking the existing PayPal logic. This violates the Open/Closed Principle.

The Assignment
Refactor your code to satisfy OCP (Open for Extension, Closed for Modification) regarding Payment Methods.

Requirements:

Create an Interface: Create a PaymentStrategy (or PaymentProcessor) interface.

Concrete Implementations: Move the logic for "Paypal" and "XTS" into their own separate classes that implement this interface.

The Constraint: You must be able to add a generic "Bitcoin" payment method by only adding a new class file. You should NOT have to change a single line of code inside OrderProcessor to support a new payment type (except perhaps the initial wiring, but keep the core logic clean).

Injection: Pass the specific PaymentStrategy into the processOrder method (or set it via a setter). Do NOT use new Paypal() inside OrderProcessor.

Input: Refactor the code and paste the new OrderProcessor class and your new Interface/Classes below.