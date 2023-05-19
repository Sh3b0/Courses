import psycopg2


price_request = "SELECT price FROM Shop WHERE product = %(product)s"
buy_decrease_balance = f"UPDATE Player SET balance = balance - ({price_request}) * %(amount)s WHERE username = %(username)s"
buy_decrease_stock = "UPDATE Shop SET in_stock = in_stock - %(amount)s WHERE product = %(product)s"
create_inventory = "INSERT INTO Inventory (username, product, amount) VALUES (%(username)s,%(product)s,%(amount)s)"
read_inventory = "SELECT SUM(amount) FROM Inventory WHERE username = %(username)s"
update_inventory = "UPDATE Inventory SET amount = amount + %(amount)s WHERE username = %(username)s AND product = %(product)s"


def get_connection():
    return psycopg2.connect(
        dbname="postgres",
        user="postgres",
        password="postgres",
        host="localhost",
        port=5432
    )


def buy_product(username, product, amount):
    obj = {"product": product, "username": username, "amount": amount}
    with get_connection() as conn:
        with conn.cursor() as cur:
            try:
                cur.execute("BEGIN")
                cur.execute(buy_decrease_balance, obj)
                if cur.rowcount != 1:
                    raise Exception("Wrong username")
                cur.execute(buy_decrease_stock, obj)
                if cur.rowcount != 1:
                    raise Exception("Wrong product or out of stock")
                cur.execute(read_inventory, obj)
                inventory_amount = cur.fetchone()[0]
                if not inventory_amount:
                    cur.execute(create_inventory, obj)
                elif inventory_amount + amount > 100:
                    raise Exception("Inventory is full")
                cur.execute(update_inventory, obj)
                cur.execute("COMMIT")
            except Exception as e:
                conn.rollback()
                raise e


buy_product('Alice', 'marshmello', 1)
