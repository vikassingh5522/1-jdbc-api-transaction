![image](https://github.com/user-attachments/assets/615a84cb-d430-457a-b3ae-2c6c004433d7)
Transaction Management in JDBC
A transaction is a sequence of one or more SQL operations executed as a single unit of work. It ensures data integrity and reliability by following the ACID properties:

Atomicity: Either all operations within a transaction are executed successfully, or none are.

Consistency: Ensures that the database transitions from one valid state to another.

Isolation: Each transaction is independent and unaffected by others until it is completed.

Durability: Once committed, the transaction's changes are permanent, even in the event of a system failure.

Transactions in JDBC (Java Database Connectivity)
In JDBC, a transaction can include multiple SQL statements executed together. By default, JDBC runs in auto-commit mode, meaning each statement is executed and committed immediately. To manage transactions manually, auto-commit must be disabled.

Steps to Handle Transactions in JDBC
1. Disable Auto-Commit Mode
Auto-commit is enabled by default:

connection.setAutoCommit(false);
This allows you to manage the transaction boundaries manually.

2. Execute SQL Operations
Perform your SQL operations (e.g., INSERT, UPDATE, DELETE) using:

Statement

PreparedStatement

CallableStatement

These changes will not be reflected in the database until explicitly committed.

3. Commit the Transaction
If all operations execute successfully:

connection.commit();  // Makes changes permanent
4. Rollback on Failure
In case of any exception:


connection.rollback();  // Undoes all changes since the last commit
This ensures the database remains in a consistent state.

5. Restore Auto-Commit (Optional)
After completing the transaction, auto-commit can be re-enabled:

connection.setAutoCommit(true);
6. Use Savepoints (Optional)

Savepoint savepoint = connection.setSavepoint("MySavepoint");
Rollback to a specific point:

connection.rollback(savepoint);
