-- Non-rented Horror/Sci-Fi films with rating R or PG-13

explain analyze
select film.film_id, film.title
from film
         inner join category on (category.name = 'Horror' or category.name = 'Sci-Fi')
where (rating = 'R' or rating = 'PG-13')

      -- Exclude rented films
    except (
          select i.film_id, f.title
          from film f,
               payment p
                   inner join rental r on p.rental_id = r.rental_id
                   inner join inventory i on r.inventory_id = i.inventory_id
      );

-- the most expensive query is the except one, as it uses several joins
-- adding an index on rental_id will optimize

-- get best stores
explain analyze
select store_id, sum(amount) as total from payment p inner join staff s on p.staff_id = s.staff_id
  group by store_id order by total desc
