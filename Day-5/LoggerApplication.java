interface ILogger {
    void log(String message);
}

class BasicLogger implements ILogger {
    @Override
    public void log(String message) {
        System.out.println("Log: " + message);
    }
}

abstract class LoggerDecorator implements ILogger {
    protected ILogger decoratedLogger;

    public LoggerDecorator(ILogger logger) {
        this.decoratedLogger = logger;
    }
}

class TimestampLogger extends LoggerDecorator {
    public TimestampLogger(ILogger logger) {
        super(logger);
    }

    @Override
    public void log(String message) {
        String timestampedMessage = "[" + System.currentTimeMillis() + "] " + message;
        this.decoratedLogger.log(timestampedMessage);
    }
}

class SplunkLogLib{
    public void sendToSplunk(String message){
        System.out.println("Sending to Splunk: " + message);
    }
}


class SplunkLogger implements ILogger {
    private SplunkLogLib splunkLogLib;
    public SplunkLogger(SplunkLogLib splunkLogLib) {
        this.splunkLogLib = splunkLogLib;
    }

    @Override
    public void log(String message) {
        splunkLogLib.sendToSplunk(message);
    }
}

public class LoggerApplication {
    public static void main(String args[]){
        ILogger logger = new BasicLogger();
        ILogger timestampLogger = new TimestampLogger(logger);
        timestampLogger.log("This is a timestamped log message.");

        SplunkLogLib splunkLogLib = new SplunkLogLib();
        ILogger splunkLogger = new SplunkLogger(splunkLogLib);
        splunkLogger.log("This is a log message sent to Splunk.");
    }
    
}
