## Java-Challenge - The Asset Management Digital Challenge

### JSON Request for http://localhost:18080/v1/accounts/transferMoney

{"accountFromId":"Id-123","accountToId":"Id-456","transferAmount":1000}

### Improvement needed to make the application production and support ready :

1) Generate and return the transaction Id to make the transactions trackable for future references.

2) Use Spring Transactions and JPA with Hibernate to manage accounts and transactions in database instead of in-memory system. This will take care of tranasction 
   roll back in case of any failure.
   
3) Enhance the JUnit test cases to cover more multithreading scenario for concurrrent transaction like :
   a) If Account A is transferring the amount to Account B and at the same time account B tries to transfer money to account A.
   b) Performance and stress test cases to test the concurrency.
   
4) Spring Cloud Config Server can be used to centralized property files for DB configuration of different environments like QA,UAT and Production.

5) Internationalization of message notifications.

### Optional Improvement :

1) EntityManagers Optimistic locking could be considered for this type of scenarios. However, further analysis has to be done before incorporating it. 

