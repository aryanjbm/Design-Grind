Objective: Design a game engine that handles rule changes without rewriting code. The Prompt: Design a generic Board Game engine for Snake & Ladder.

The "Gauntlet" Requirement: If I ask you to add a "Trampoline" (moves you forward current_roll spaces) or a "Mine" (moves you back 5 spaces) or a "Portal" (moves you to a random cell), you must be able to do it without modifying the Board class.

The Trap: Most candidates write this:

Java

// BAD CODE
if (cell == 99) player.pos = 2; // Snake
else if (cell == 4) player.pos = 14; // Ladder
This is a failure. It violates OCP.

The Design: The "Jump" Abstraction
You need a system where cells contain "Special Objects".

Interface: CellEntity (or BoardEntity) with a method int processJump(int start, int end).

Implementation: Snake, Ladder, Trampoline implement this interface.

Board: Just holds a Map<Integer, CellEntity>. If a player lands on a cell, check if it has an entity. If yes, execute it.

The Assignment
Write the code for:

Dice: Support rolling a generic number (1-6).

Board: Initialize with size N. Add generic "Special Entities" to it.

Game: The main loop.

The Proof: Implement Snake, Ladder, and Trampoline classes.

Constraint:

The Board class must not contain the words "Snake" or "Ladder". It should only know about the abstract interface.