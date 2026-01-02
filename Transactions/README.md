Here is your finalized **Engineering Guide**. I have formatted this as a technical documentation file you can save (e.g., to Notion, Obsidian, or a private GitHub repo) for quick review before system design interviews.

---

# 📘 Engineering Guide: Database Concurrency & Distributed Transactions

**Target Audience:** Staff/Principal Engineers
**Core Topic:** ACID, Isolation Levels, and Distributed Failure Modes

---

## Part 1: The Problem Space

In large-scale systems, "happy paths" are rare. Applications face three distinct types of partial failures:

1. **Database Crash:** Occurs during a series of writes (memory loss).
2. **App Server Crash:** Occurs while processing a task (partial logic execution).
3. **Network Partition:** The connection drops between App and DB, leaving the state unknown.

**Transactions** are the abstraction layer that hides these failures, allowing us to treat multiple operations as a single unit of work.

---

## Part 2: ACID Under the Hood

Junior engineers know the acronym. Staff engineers know the implementation.

### 1. Atomicity (Abortability)

* **Definition:** "All or Nothing." If a transaction fails, the DB discards all writes in that specific transaction.
* **Implementation:** Achieved via the **Write-Ahead Log (WAL)** (or Undo Log).
* *Mechanism:* The DB records the "old value" in the log before overwriting. If the txn crashes, the DB replays the log to restore the old state.


* **Staff Insight:** Atomicity is not just about writing; it is the enabler for **Safe Retries**. Because a failed transaction leaves no side effects, the application can safely retry the operation without corrupting data.

### 2. Consistency (Invariants)

* **Definition:** Data moves from one valid state to another.
* **Implementation:** Constraints (Foreign Keys, Unique Keys, Not Null).
* **Staff Insight:** This is largely an **Application Responsibility**. The database enforces the rules you set, but the code defines what "valid" means (e.g., `credits - debits = 0`).
> ⚠️ **Warning:** Do not confuse ACID Consistency with **CAP Consistency** (Linearizability/Freshness). They are unrelated concepts.



### 3. Isolation (Concurrency Control)

* **Definition:** The illusion that concurrent transactions are running serially.
* **Reality:** True serializability kills performance (throughput). We use weaker isolation levels (Read Committed, Snapshot Isolation) to trade absolute correctness for speed.

### 4. Durability (Persistence)

* **Definition:** Once the DB says `COMMIT OK`, the data survives a power loss.
* **Implementation:** Achieved by `fsync`-ing the WAL to non-volatile storage (SSD/HDD).
* **Staff Insight:** **Replication is for Availability, not Durability.** If the primary node crashes after acknowledging a write but *before* replicating to the secondary, data is lost (unless you use Synchronous Replication, which adds latency).

---

## Part 3: Isolation Levels & MVCC

Modern databases (Postgres, Oracle, MySQL InnoDB) rely on **MVCC (Multi-Version Concurrency Control)** rather than simple locking for reads.

### How MVCC Works

* **The Mechanism:** Writers don't overwrite rows; they create a *new version* of the row.
* **The Rule:** "Readers don't block Writers, and Writers don't block Readers."
* **Visibility:** Readers see a specific "snapshot" of the database based on Transaction IDs (XID).

### Isolation Level Hierarchy

| Isolation Level | Dirty Read? | Non-Repeatable Read? | Phantom Read? | Write Skew? |
| --- | --- | --- | --- | --- |
| **Read Committed** | ❌ Protected | ⚠️ Possible | ⚠️ Possible | ⚠️ Possible |
| **Snapshot Isolation** | ❌ Protected | ❌ Protected | ⚠️ Possible | ⚠️ Possible |
| **Serializable** | ❌ Protected | ❌ Protected | ❌ Protected | ❌ Protected |

---

## Part 4: Concurrency Anomalies (The "Hard" Problems)

### 🛑 1. Dirty Read / Dirty Write

* **Scenario:** Reading data that hasn't been committed yet.
* **Fix:** **Read Committed**. The DB only returns rows where the XID is marked as "Committed."

### 🛑 2. Lost Updates (The Clobbering)

* **Scenario:** The Read-Modify-Write cycle.
* Alice reads `count=10`. Bob reads `count=10`.
* Alice writes `11`. Bob writes `11`.
* *Result:* Count is `11`. Should be `12`.


* **The Nuance:** Snapshot Isolation does *not* prevent this (both saw a valid snapshot of `10`).

**Solutions & Trade-offs:**

1. **Atomic Operations (Best):**
```sql
UPDATE counters SET value = value + 1 WHERE id = 1;

```


* *Pros:* Fast, simple.
* *Cons:* Limited logic (math only).


2. **Compare-and-Set (Optimistic):**
```sql
UPDATE counters SET value = 11 WHERE id = 1 AND value = 10;

```


* *Limit:* Requires **Strong Consistency**. In Eventual Consistency models (Cassandra/Dynamo), this can fail if replicas are out of sync.


3. **Explicit Locking (Pessimistic):**
```sql
SELECT * FROM counters WHERE id = 1 FOR UPDATE;

```


* *Limit:* Works in distributed systems (Spanner/CockroachDB), but introduces **latency** because the Transaction Coordinator must acquire locks across network boundaries.



### 🛑 3. Write Skew & Phantoms

* **Definition:** Two transactions make disjoint updates based on a shared (but obsolete) premise.
* **Scenario:** Meeting Room Booking.
1. Alice checks 10 AM. Result: Empty.
2. Bob checks 10 AM. Result: Empty.
3. Alice inserts Booking A.
4. Bob inserts Booking B.


* *Result:* Double Booking.



**Why `SELECT FOR UPDATE` fails here:**
You cannot lock rows that **do not exist yet**. The `SELECT` returned nothing (a Phantom), so there was nothing to lock.

**The Staff-Level Solution: Materialize the Conflict**
If you can't lock the data, lock the *container*.

1. **Create a Table:** `RoomSlots` (with rows for 9am, 10am, 11am...).
2. **The Fix:**
```sql
-- Transaction Start
SELECT * FROM RoomSlots
WHERE room_id = 'A' AND time_slot = '10:00'
FOR UPDATE; -- <--- This row ALWAYS exists, so we CAN lock it.

-- Now check bookings, insert if safe.
COMMIT;

```



---

## Part 5: Visual Reference (ASCII)

**Materializing Conflicts Logic:**

```text
Problem: The 'Bookings' table is empty. There is no row to lock!

+-------------------------------------------------------+
|  SOLUTION: MATERIALIZED CONFLICT (The 'Anchor' Table) |
+-------------------------------------------------------+

Step 1: Create a static table of all possible slots
Table: Room_Slots
+---------+----------+---------------------+
| Room_ID | Time     | Locked_By_Txn_ID?   |
+---------+----------+---------------------+
| A       | 09:00    | NULL                |
| A       | 10:00    | <--- ROW EXISTS!    |  <-- Target for locking
| A       | 11:00    | NULL                |
+---------+----------+---------------------+

Step 2: The Transaction Flow

      Transaction A (Alice)                     Transaction B (Bob)
      ---------------------                     -------------------
1. BEGIN TXN                               1. BEGIN TXN

2. SELECT * FROM Room_Slots             2. SELECT * FROM Room_Slots
   WHERE room='A' AND time='10'            WHERE room='A' AND time='10'
   FOR UPDATE;                             FOR UPDATE;

3. [DB GRANTS LOCK on Row '10:00']      3. [DB BLOCKS TXN B!]
                                           (Waits for A to commit)
4. Check 'Bookings' table.
   Empty? Good.

5. INSERT INTO Bookings...

6. COMMIT;
   (Lock released)

                                        7. [DB UNBLOCKS TXN B]
                                           (B acquires lock)

                                        8. Check 'Bookings' table.
                                           Found Alice's row!
                                           (Logic Failure -> Abort)

```
