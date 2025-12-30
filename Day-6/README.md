Day 7: The Grand Unification (Integration)
Objective: Patterns do not live in isolation. You must use them together. The Scenario: A high-frequency Trading Bot.

You have built:

StockExchange (Observer) - Day 4

ConnectionPool (Singleton) - Day 5

Logger (Decorator) - Day 6

The Assignment
Create a class TradingBotApplication that integrates all three.

Requirements:

The Observer: The TradingBot listens to the StockExchange.

The Logic:

When the bot receives a price update:

It must Log the event using your TimestampLogger (from Day 6).

IF the price is > 100 (Business Logic), it must acquire a connection from the ConnectionPool (from Day 5) and print "Saving Trade to DB..."

The Constraint:

The TradingBot class must not contain new ConnectionPoolManager(). It must call ConnectionPoolManager.getInstance().

The TradingBot must accept an ILogger in its constructor (Dependency Injection).

Goal: Show me one file (or a set of classes) where an Update triggers a Log AND a DB Connection access.

Go.