Day 4: Behavioral Patterns (Observer & Strategy)
Objective: Manage state changes and algorithms dynamically. Scenario: A Stock Market Feed.

The Assignment:

The Subject (StockExchange):

Maintains a list of StockObservers.

When setPrice(String ticker, double price) is called, it loops through the list and calls .update().

Strict Rule: StockExchange must not know what a "MobileApp" or "Bot" is. It only knows the StockObserver interface.

The Observer (MobileAlert):

Implements StockObserver.

It contains a Strategy field called AlertFilter.

The Strategy (AlertFilter interface):

Define an interface AlertFilter with method boolean shouldAlert(double price).

Implement AllEventsFilter (Returns true always).

Implement HighValueFilter (Returns true only if price > 100).

Execution:

Inject the HighValueFilter into MobileAlert.

Register MobileAlert to StockExchange.

Show that a price of $50 triggers nothing, but $150 triggers a print.

Write the code.