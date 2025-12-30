Objective: Manage complex relationships and graph simplification. The Prompt: Design the core backend for Splitwise.

The Challenges:

Data Modeling: How do you store "A owes B $50" and "A owes C $20"?

The Algorithm (Debt Simplification):

Alice pays $30 for Bob. (Bob owes Alice $30).

Bob pays $30 for Charlie. (Charlie owes Bob $30).

Naive View: Bob owes Alice, Charlie owes Bob.

Simplified View: Charlie owes Alice $30. Bob is clear.

The Assignment
Implement the ExpenseManager and a DebtSimplificationStrategy.

Requirements:

addExpense(String paidBy, double amount, List<String> splits):

Assume an "Equal Split" for now. If Alice pays $100 for [Alice, Bob, Charlie, Dave], each owes $25. Alice is owed $75.

showBalances(): Print the raw debts (e.g., "Bob owes Alice $25").

simplifyDebts() (The Hard Part):

Calculate the Net Balance for every user.

Example: A pays 10 for B. B pays 10 for C.

A Net: +10

B Net: 0 (-10 + 10)

C Net: -10

Algorithm: Match the person who owes the most (Max Negative) with the person who is owed the most (Max Positive). Settle the debt and repeat until everyone is 0.

The Constraints:

You don't need a database. Use in-memory Maps.

Focus on the class SplitwiseService.

Hint: Use a Map<String, Double> to store the Net Balance of each user first.

Positive Value = "I am owed money".

Negative Value = "I owe money".

Go.