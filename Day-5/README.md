Day 6: Structural Patterns (Decorator & Adapter)
Objective: Wrap existing classes to change their behavior or interface without modifying them. The Scenario: You are building a Logging Library. You have a basic Logger class that writes to the console. We want to:

Add timestamps to logs. (Decorator)

Support a 3rd-party logging library (like Splunk) that has a completely different method name (logToSplunk instead of log). (Adapter)

The Problem:

If you use inheritance (TimestampLogger extends Logger), you get an explosion of classes (TimestampAndErrorLogger, TimestampLogger, ErrorLogger).

If you want to use Splunk, you can't just pass the Splunk object where a Logger is expected because the interfaces don't match.

The Assignment
Part 1: Decorator Pattern

Create an interface ILogger with method log(String msg).

Create a BasicLogger that prints msg.

Create an abstract LoggerDecorator that implements ILogger.

Create a TimeStampingLogger that extends the decorator. It should prepend [Time] to the message.

Part 2: Adapter Pattern

Assume there is a legacy class SplunkLogLib (create this class) that has a method saveLog(String msg). (Note: different method name).

Create a class SplunkAdapter that implements ILogger.

Inside SplunkAdapter, translate the log(msg) call to splunkLogLib.saveLog(msg).

Input: Paste the code demonstrating:

A decorated logger: new TimeStampingLogger(new BasicLogger()).

An adapted logger: new SplunkAdapter(new SplunkLogLib()).

A main method that treats both of them exactly the same (calling .log()).

Go.