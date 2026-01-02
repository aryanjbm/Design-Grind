import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

class Counter{
    private Map<String, Integer> countMap = new ConcurrentHashMap<>();

    public synchronized void increment(String key) throws InterruptedException {
            int currentCount = countMap.getOrDefault(key, 0);
            Thread.sleep(2000); // Simulate some delay
            countMap.put(key, currentCount+ 1);
    }

    public int getCount(String key){
        return countMap.getOrDefault(key, 0);
    }
}




class RaceConditionApp{
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Counter counter = new Counter();
        CountDownLatch latch = new CountDownLatch(10);
        for(int i=0; i<10; i++){
            executor.submit(() -> {
                try {
                    counter.increment("test");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }
        latch.await();
        
        executor.shutdown();
        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTime - startTime)/1000 + " s");
        System.out.println("Final count is: " + counter.getCount("test"));
    }

}