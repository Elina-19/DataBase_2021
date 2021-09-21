CREATE TABLE food(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20),
    production_date TIMESTAMP,
    expiration_date varchar(10)
);

create table test(
     id serial primary key,
     name varchar(20),
     date varchar(20),
     time timestamp
);

Create or replace function generate() returns void as
$$
declare
    names text[][] := '{{"banana", "7 days"}, {"milk", "3 days"}, {"chocolate", "3 years"}}';
    k integer = 0;
begin
    for i in 1..100 loop
        k = 1+random()*(array_length(names, 1)-1);
        insert into test(name, date, time) values(names[k][1], names[k][2], to_timestamp(1284908050+random()*347155200));
    end loop;
end;
$$ language plpgsql;

select generate();