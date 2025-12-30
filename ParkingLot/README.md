The Prompt: Design a Parking Lot system.

Requirements:

Multiple Floors: The lot has multiple floors.

Multiple Vehicle Types: Bike, Car, Truck.

Spot Constraints:

Bikes can park in Bike or Car spots.

Cars can only park in Car spots.

Trucks can only park in Truck spots.

Concurrency (The Hard Part):

There are 3 Entry Gates active at the same time.

Multiple vehicles will try to call assignSpot() simultaneously.

Constraint: You must ensure that two vehicles are never assigned the same spot.

The Assignment: Write the core class ParkingLotManager. I want to see the parkVehicle(Vehicle vehicle) method.

It must:

Find the first available spot (according to constraints).

Mark it as occupied (Thread-safely).

Generate and return a Ticket object.

If full, throw an exception.

Do not give me generic interfaces. Give me the concrete logic that handles the thread safety.