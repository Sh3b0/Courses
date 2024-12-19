# Lab 7 - Software Quality and Reliability

## Environment Setup

1. Create a virtual Python environment and install `psycopg2-binary`

   ```bash
   python3 -m venv venv
   source venv/bin/activate
   pip3 install psycopg2-binary
   ```

2. Run PostgreSQL database using docker

   ```bash
   docker-compose up
   ```

3. Exec into `postgres` container and run the queries to initialize the database.

   ```sql
   $ docker exec -it postgres bash
   postgres@postgres:/$ psql
   \c postgres
   CREATE TABLE Player (username TEXT UNIQUE, balance INT CHECK(balance >= 0));
   CREATE TABLE Shop (product TEXT UNIQUE, in_stock INT CHECK(in_stock >= 0), price INT CHECK (price >= 0));
   INSERT INTO Player (username, balance) VALUES ('Alice', 100);
   INSERT INTO Player (username, balance) VALUES ('Bob', 200);
   INSERT INTO Shop (product, in_stock, price) VALUES ('marshmello', 10, 10);
   ```

## Homework

### Part 1

Modify the `buy_product()` function so it does not lead to data inconsistencies

- The two queries for `buy_decrease_balance` and `buy_decrease_stock` should be executed as a single database transaction (transactions are atomic - either executed as a whole, or not executed at all).

- This can be achieved using SQL keywords for transactions (`BEGIN-COMMIT-ROLLBACK`)

  ```python
  try:
      cur.execute("BEGIN")
      cur.execute(buy_decrease_balance, obj)
      if cur.rowcount != 1:
          raise Exception("Wrong username")
      cur.execute(buy_decrease_stock, obj)
      if cur.rowcount != 1:
          raise Exception("Wrong product or out of stock")
      cur.execute("COMMIT")
  except Exception as e:
      conn.rollback()
      raise e
  ```

### Part 2

1. Create a new table for the player inventory.

   ```sql
   CREATE TABLE Inventory (
       username TEXT REFERENCES Player(username) NOT NULL,
       product TEXT REFERENCES Shop(product) NOT NULL,
       amount INT CHECK(amount >= 0),
       UNIQUE(username, product)
   );
   ```

2. Add queries to create, read, and update an inventory entry

3. Include inventory checks and updates in the transaction

   ```python
   cur.execute(read_inventory, obj)
   inventory_amount = cur.fetchone()[0]
   if not inventory_amount:
       cur.execute(create_inventory, obj)
   elif inventory_amount + amount > 100:
       raise Exception("Inventory is full")
   cur.execute(update_inventory, obj)
   ```

### Part 3 (Extra)

- If the function fails after committing the transaction and the client re-executed the transaction, duplication may occur.
- To avoid such cases, we can introduce an additional database table maintaining a list of successful transactions (identified by some unique ID).
- Such table would be updated during transaction and checked to avoid duplication.

### Part 4 (Extra)

- In the case where there is multiple systems involved, one may undo the effects of a failed operation by creating a new inverse operation. In case of money, the inverse operation for withdrawal is a top-up.
- For the given example, it suffices to just decrease the balance in the database once we get an acknowledgement from the external system that real-money was deducted.
- We may as well introduce checks against the external system to retry until the operation is successful.
