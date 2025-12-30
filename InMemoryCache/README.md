Day 9: The In-Memory Cache (LRU)
Objective: Manage memory under strict constraints. The Prompt: Design a Thread-Safe Least Recently Used (LRU) Cache.

Why this matters: This problem tests your ability to combine two data structures (HashMap and Doubly Linked List) and synchronize them perfectly.

The Requirements
Capacity: The cache has a fixed size N.

int get(int key): Return the value.

Side effect: Move this key to the "front" (Most Recently Used).

Return -1 if not found.

void put(int key, int value): Update or Add.

If the key exists, update value and move to front.

If the key is new, add to front.

Eviction: If adding a new key exceeds capacity, remove the Least Recently Used item (the tail) to make space.

Thread Safety: Multiple threads will call get and put concurrently.

The "Gauntlet" Constraints
Strictly Forbidden: You cannot use Java's LinkedHashMap. You must build the ordering logic yourself.

Data Structure: You must use:

HashMap<Integer, Node> for O(1) lookups.

Doubly Linked List (Node class with prev, next) for O(1) moves.

Concurrency: You cannot use ConcurrentHashMap alone because get is a write operation (it moves nodes). You must use a Lock (e.g., ReentrantLock or synchronized).

Your Task: Write the LRUCache class.

Define your Node inner class.

Implement get and put.

Implement the helper methods addToHead and removeNode.