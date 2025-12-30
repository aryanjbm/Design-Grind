package ParkingLot;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.Map;

record ParkingSpot(String id, String floor, ParkingSpotType type) {
}

enum ParkingSpotType {
    BIKE, CAR,
    TRUCK
}

interface  ParkingStrategy {
    // Strategy pattern can be implemented here for different parking strategies
    ParkingSpot allocateSpot(Map<ParkingSpotType, BlockingQueue<ParkingSpot>> availableSpots, ParkingSpotType type);
}

class DefaultParkingStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot allocateSpot(Map<ParkingSpotType, BlockingQueue<ParkingSpot>> availableSpots, ParkingSpotType type) {
        BlockingQueue<ParkingSpot> spotsQueue = availableSpots.get(type);
        ParkingSpot spot = spotsQueue.poll();
        if (spot == null && type == ParkingSpotType.BIKE) {
            spotsQueue = availableSpots.get(ParkingSpotType.CAR);
            spot = spotsQueue.poll();
        }
        return spot;
    }
}

class ParkingLot {
    private Map<ParkingSpotType, BlockingQueue<ParkingSpot>> avalibleParkingSpots;
    private Map<String, ParkingSpot> occupiedParkingSpots;

    public ParkingLot(Map<ParkingSpotType, BlockingQueue<ParkingSpot>> avalibleParkingSpots) {
        this.avalibleParkingSpots = avalibleParkingSpots;
        this.occupiedParkingSpots = new ConcurrentHashMap<>();
    }

    public boolean parkVehicle(ParkingSpotType type, String vehicleId,ParkingStrategy strategy) {
         ParkingSpot spot = strategy.allocateSpot(avalibleParkingSpots, type);
         if (spot == null) {
             return false; // No available spot
         }
        occupiedParkingSpots.put(vehicleId, spot);
        return true;
    }

    public boolean unparkVehicle(String vehicleId) {
        ParkingSpot spot = occupiedParkingSpots.remove(vehicleId);
        if (spot == null) {
            return false; // Vehicle not found
        }
        BlockingQueue<ParkingSpot> spotsQueue = avalibleParkingSpots.get(spot.type());
        spotsQueue.offer(spot);
        return true;
    }
}



public class Main {
    public static void main(String[] args) throws InterruptedException {
        testBikeFallbackStrategy();
        testConcurrency();
    }
    public static void testBikeFallbackStrategy() {
        Map<ParkingSpotType, BlockingQueue<ParkingSpot>> availableSpots = new HashMap<>();
        availableSpots.put(ParkingSpotType.BIKE, new LinkedBlockingQueue<>());
        availableSpots.get(ParkingSpotType.BIKE).offer(new ParkingSpot("B1", "F1", ParkingSpotType.BIKE));
        availableSpots.put(ParkingSpotType.CAR, new LinkedBlockingQueue<>());
        availableSpots.get(ParkingSpotType.CAR).offer(new ParkingSpot("C1", "F1", ParkingSpotType.CAR));

        ParkingLot parkingLot = new ParkingLot(availableSpots);
        ParkingStrategy strategy = new DefaultParkingStrategy();

        boolean parked1 = parkingLot.parkVehicle(ParkingSpotType.BIKE, "V1", strategy);
        boolean parked2 = parkingLot.parkVehicle(ParkingSpotType.BIKE, "V2", strategy);
        boolean parked3 = parkingLot.parkVehicle(ParkingSpotType.BIKE, "V3", strategy);
        assert parked1 : "Bike should be parked in Car spot as fallback";
        assert parked2 : "Bike should be parked in Car spot as fallback";
        assert !parked3 : "No spots should be available";

        System.out.println("Bike Fallback Strategy Test Passed!");
    }


    public static void testConcurrency() throws InterruptedException {
        Map<ParkingSpotType, BlockingQueue<ParkingSpot>> availableSpots = new HashMap<>();
        availableSpots.put(ParkingSpotType.CAR, new LinkedBlockingQueue<>());
        for (int i = 1; i <= 100; i++) {
            availableSpots.get(ParkingSpotType.CAR).offer(new ParkingSpot("C" + i, "F1", ParkingSpotType.CAR));
        }

        ParkingLot parkingLot = new ParkingLot(availableSpots);
        ParkingStrategy strategy = new DefaultParkingStrategy();

        ExecutorService executorService = Executors.newFixedThreadPool(150);
        CountDownLatch latch = new CountDownLatch(150);
        ConcurrentHashMap<String, Boolean> results = new ConcurrentHashMap<>();

        for (int i = 1; i <= 150; i++) {
            final int vehicleId = i;
            executorService.submit(() -> {
                try{
                    boolean parked = parkingLot.parkVehicle(ParkingSpotType.CAR, "V" + vehicleId, strategy);
                    results.put("V" + vehicleId, parked);
                } finally{
                    latch.countDown();
                }
            
            });
        }
        latch.await();
        executorService.shutdown();

        long successCount = results.values().stream().filter(b -> b).count();
        System.out.println("Successfully Parked: " + successCount);

        // We had 100 spots, so exactly 100 should succeed.
        assert successCount == 100 : "Error: " + successCount + " cars parked (Expected 100)";
        
        System.out.println("Concurrency Test Passed!");

    }
}
