
drop function if exists get_addr();
create function get_addr()
    returns Table
            (
                address varchar(50)
            )
    language plpgsql
as
$$
begin
    return query
        select address.address
        from address
        where address.address like '%' || '11' || '%' and city_id > 400 and city_id < 600;
end;
$$;

-- test
select get_addr();
