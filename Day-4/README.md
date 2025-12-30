Day 5: Creational Patterns (Singleton & Factory)
Objective: Control how objects are created. The Scenario: You are building a Database Connection Pool. Creating a new DB connection for every request is expensive (handshake, authentication). We want a central pool that keeps 5-10 connections open and reuses them.

The Problem:

We need exactly one ConnectionPoolManager in the entire application. (Singleton).

Multiple threads will try to get the instance at the same time. If you aren't careful, you might create two pools by accident. (Thread Safety).

The client shouldn't care how a connection is created (MySQL vs PostgreSQL). (Factory).

The Assignment
Implement a Thread-Safe Singleton and a Simple Factory.

Requirements:

The Singleton (ConnectionPool):

It must have a private static instance variable.

It must have a private constructor (so no one can do new ConnectionPool()).

It must have a public static getInstance() method.

Strict Constraint: You must implement Double-Checked Locking to ensure thread safety. Do not just add synchronized to the method signature (that kills performance).

The Factory Logic:

Inside the ConnectionPool, create a method DatabaseConnection getConnection(String type).

If type is "MySQL", return a MySQLConnection.

If type is "Postgres", return a PostgresConnection.

(You can mock the connection classes with empty classes).

Input: Paste the ConnectionPool class showing the double-checked locking logic and the factory method.