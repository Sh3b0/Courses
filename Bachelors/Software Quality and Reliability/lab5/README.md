# Lab 5 - Software Quality and Reliability

1. List the parameters to be tested

   | Parameter          | Equivalence Class                 |
   | ------------------ | --------------------------------- |
   | `type`             | `budget`; `luxury`; nonsense      |
   | `plan`             | `minute`; `fixed_price`; nonsense |
   | `distance`         | `<=0`; `>0`                       |
   | `planned_distance` | `<=0`; `>0`                       |
   | `time`             | `<=0`; `>0`                       |
   | `planned_time`     | `<=0`; `>0`                       |
   | `inno_discount`    | `yes`; `no`; nonsense             |

2. Build the decision table. It shows all value combinations and the expected results (an actual value or `Invalid Request`)

   | Parameters          | Values                            |  R1   |  R2   |  R3   |  R4   |    R5    |    R6    |    R7    |      R8       |    R9    |   R10    |      R11      |      R12      |   R13    |   R14    |
   | ------------------- | --------------------------------- | :---: | :---: | :---: | :---: | :------: | :------: | :------: | :-----------: | :------: | :------: | :-----------: | :-----------: | :------: | :------: |
   | `distance`          | `>0`; `<=0`                       | `<=0` | `>0`  | `>0`  | `>0`  |   `>0`   |   `>0`   |   `>0`   |     `>0`      |   `>0`   |   `>0`   |     `>0`      |     `>0`      |   `>0`   |   `>0`   |
   | `planned_distance`  | `>0`; `<=0`                       |   *   | `<=0` | `>0`  | `>0`  |   `>0`   |   `>0`   |   `>0`   |     `>0`      |   `>0`   |   `>0`   |     `>0`      |     `>0`      |   `>0`   |   `>0`   |
   | `time`              | `>0`; `<=0`                       |   *   |   *   | `<=0` | `>0`  |   `>0`   |   `>0`   |   `>0`   |     `>0`      |   `>0`   |   `>0`   |     `>0`      |     `>0`      |   `>0`   |   `>0`   |
   | `planned_time`      | `>0`; `<=0`                       |   *   |   *   |   *   | `<=0` |   `>0`   |   `>0`   |   `>0`   |     `>0`      |   `>0`   |   `>0`   |     `>0`      |     `>0`      |   `>0`   |   `>0`   |
   | `type`              | `budget`, `luxury`, nonsense      |   *   |   *   |   *   |   *   | nonsense |    *     |    *     |   `luxury`    | `budget` | `budget` |   `budget`    |   `budget`    | `luxury` | `luxury` |
   | `plan`              | `minute`, `fixed_price`, nonsense |   *   |   *   |   *   |   *   |    *     | nonsense |    *     | `fixed_price` | `minute` | `minute` | `fixed_price` | `fixed_price` | `minute` | `minute` |
   | `inno_discount`     | `yes`, `no`, nonsense             |   *   |   *   |   *   |   *   |    *     |    *     | nonsense |       *       |   `no`   |  `yes`   |     `no`      |     `yes`     |   `no`   |  `yes`   |
   |                     |                                   |       |       |       |       |          |          |          |               |          |          |               |               |          |          |
   | **Expected Result** |                                   |       |       |       |       |          |          |          |               |          |          |               |               |          |          |
   | Invalid Request     |                                   |   X   |   X   |   X   |   X   |    X     |    X     |    X     |       X       |          |          |               |               |          |          |
   | 200                 |                                   |       |       |       |       |          |          |          |               |          |          |               |               |          |          |

3. Query the API for my parameters

   ```bash
   $ curl -L 'https://script.google.com/macros/s/<API_KEY>/exec?action=get&&service=getSpec&email=<EMAIL>'
   Here is InnoCar Specs:
   Budet car price per minute = 24
   Luxury car price per minute = 58
   Fixed price per km = 13
   Allowed deviations in % = 11
   Inno discount in % = 8
   ```

4. Write a [script](./bva.py) that runs tests given these parameters. **To achieve 100% coverage**, the following cases should be tested:

   ```bash
   min-1, min, normal, max, max+1
   ```

   - For invalid values `(min-1, max+1)`. It's sufficient to invalidate one parameter and keep the rest normal.

   - For boundary values `(min, max)`. It's sufficient to fix one value on each boundary and keep others normal

   - For categorical (non-numerical) values,  there is no notion of `min` and `max`, there is only `valid` and `invalid`.

5. Run tests and check output

   ![output](./output.png)

6. Analyze the results. Based on the shown failed results, the following bugs were identified:

   - `inno_discount` is not correctly calculated for `budget` car type with `fixed_price` plan.
   - `distance` and `planned_distance` are not being validated for invalid (negative) values.
   - Zero values for `distance` and `planned_time` yield non-zero results.
   - Values for `distance`, `time`, `planned_distance`, and `planned_time` that are just about the maximum of `100000` yield numerical results instead of an invalid request.
