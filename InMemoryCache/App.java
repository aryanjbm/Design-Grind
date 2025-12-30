package InMemoryCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

class Node<K,V>{
    K key;
    V value;
    Node<K,V> prev;
    Node<K,V> next;
    public Node(){
    }

    public Node(K key,V value){
        this.key = key;
        this.value = value;
    }
}

class DoublyLinkedList<K,V>{
    Node<K,V> head;
    Node<K,V> tail;

    public DoublyLinkedList(){
        this.head = new Node<K,V>(); //dummy head
        this.tail = new Node<K,V>(); //dummy tail
        head.next = tail;
        tail.prev = head;
    }

    public void addToFront(Node<K,V> node){
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public void removeNode(Node<K,V> node){
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public Node<K,V> removeLast(){
        if(tail.prev == head){
            return null; //list is empty
        }
        Node<K,V> lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
}


class LRUCache<K,V>{
    private final int captacity;
    private Map<K, Node<K,V>> cache;
    private DoublyLinkedList<K,V> dll;
    

    public LRUCache(int capacity){
        this.captacity = capacity;
        this.cache = new ConcurrentHashMap<>();
        this.dll = new DoublyLinkedList<>();
    }


    public synchronized Node<K,V> getKey(K key){
        if(!cache.containsKey(key)){
            return null;
        }
        Node<K,V> cacheNode = cache.get(key);
        dll.removeNode(cacheNode);
        dll.addToFront(cacheNode);
        return cacheNode;
    }

    public synchronized void putKey(K key, V value){
        if(cache.containsKey(key)){
            Node<K,V> existingNode = cache.get(key);
            dll.removeNode(existingNode);
        }else{
            if(cache.size() >= captacity){
                System.out.println("Cache is full, evicting least recently used item.");
                Node<K,V> lruNode = dll.removeLast();
                System.out.println("Evicted Key: " + lruNode.key + ", Value: " + lruNode.value);
                if(lruNode != null){
                    cache.remove(lruNode.key);
                }
            }
        }
        Node<K,V> newNode = new Node<>(key,value);
        dll.addToFront(newNode);
        cache.put(key, newNode);
    }
}

public class App {

    public static void main(String[] args) {
        System.out.println("LRU Cache Implementation");

        LRUCache<Integer,Integer> lruCache = new LRUCache<>(3);

        lruCache.putKey(1, 100);
        lruCache.putKey(2, 200);
        lruCache.putKey(3, 300);
        System.out.println("Get Key 2: " + (lruCache.getKey(2) != null ? lruCache.getKey(2).value : "null")); // Should print 200
        lruCache.putKey(4, 400); // This should evict key 1
        System.out.println("Get Key 1: " + (lruCache.getKey(1) != null ? lruCache.getKey(1).value : "null")); // Should print null
        System.out.println("Get Key 3: " + (lruCache.getKey(3) != null ? lruCache.getKey(3).value : "null"));
        System.out.println("Get Key 4: " + (lruCache.getKey(4) != null ? lruCache.getKey(4).value : "null"));
    }
    
}
