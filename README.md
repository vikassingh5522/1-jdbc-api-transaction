![image](https://github.com/user-attachments/assets/615a84cb-d430-457a-b3ae-2c6c004433d7)
Transaction Management
Let’s see, How the transection happens using the JDBC API.
A transaction is a sequence of one or more SQL operations treated as a single unit of work. It ensures data integrity by adhering to the ACID properties:
•
Atomicity: All operations in a transaction are completed successfully, or none are applied.
•
Consistency: The database remains in a consistent state before and after the transaction.
•
Isolation: Transactions are isolated from each other until complete.
•
Durability: Committed transactions are permanently saved, even in case of system failure.
In the context of the JDBC (Java Database Connectivity) API, transactions allow you to execute multiple SQL statements as a single unit, ensuring that either all statements succeed or none are applied.
Archer InfoTech Pune Contact: 9850678451
https://archerinfotech.in
Handling Transactions Using JDBC API
JDBC provides mechanisms to manage transactions explicitly. By default, JDBC operates in auto-commit mode, where each SQL statement is treated as a separate transaction and committed immediately. To handle transactions manually, you disable auto-commit and use explicit commit or rollback operations.
Here’s how transactions are handled using the JDBC API:
1.
Disable Auto-Commit Mode:
o
By default, Connection objects in JDBC have auto-commit enabled (setAutoCommit(true)).
o
To manage a transaction manually, disable auto-commit by calling:
connection.setAutoCommit(false);
2.
Execute SQL Statements:
o
Perform the desired SQL operations (e.g., INSERT, UPDATE, DELETE) using Statement, PreparedStatement, or CallableStatement objects.
o
These operations are not committed to the database until explicitly instructed.
3.
Commit the Transaction:
o
If all operations succeed, commit the transaction to make changes permanent:
connection.commit();
4.
Rollback on Failure:
o
If an error occurs, roll back the transaction to undo all changes:
connection.rollback();
o
This ensures the database remains consistent.
5.
Restore Auto-Commit (Optional):
o
After completing the transaction, you can re-enable auto-commit if needed:
connection.setAutoCommit(true);
6.
Use Savepoints (Optional):
o
For partial rollbacks, you can set savepoints within a transaction:
Savepoint savepoint = connection.setSavepoint("SavepointName");
o
Roll back to a specific savepoint if needed:
connection.rollback(savepoint);
