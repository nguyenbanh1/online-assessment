Question 5. Please provide a solution to generate the unique transaction id which must not be
duplicated. Please describe the solution in detail, and explain what is advantage and
disadvantage

Answer: There are a lot of solution for generating the unique transaction depend on requirement

I. UUID (v4)<br>

Pros:
 + Implement is easy, no coordination, No external dependency
 + Fast

Cons:
 + No order guarantee, long string

II. ObjectId in Bson

Pros:
+ Implement is easy, no coordination, No external dependency
+ Fast

Cons:
+ order time-base (not guarantee 100% for global transaction unique ids)

II. User counter in Redis<br>

Pros:
+ Global sequence, simple
+ Fast

Cons:
+ single point of failure
+ high load to redis
+ Scaling is difficult

We can resolve single point of failure on sharding data at redis (build redis cluster with leaderless) 
base on prefix transaction to reduce traffic to redis


III. Snowflake ID (Twitter's Unique ID Generator)<br>
Note: Not used before and just on research

Pros:
+ Unique & ordered, scalable

Cons:
+ Needs distributed setup

For this question: <br><br>
I will choose ObjectId in bson for not duplicate which will implement easy<br>
and can the transactionId should be generate from client side, BE side can use it to implement idempotent or unique key for transaction

