create table accounts (
                      account_number bigint primary key,
                      last_statment_balance decimal(10,4) default 0,
                      open_date timestamp not null default CURRENT_TIMESTAMP
                      );
create table transactions (
                      id bigint auto_increment primary key,
                      from_account_number int not null,
                      to_account_number int not null,
                      amount decimal(10,4),
                      created_date timestamp not null default CURRENT_TIMESTAMP,
                      foreign key (from_account_number) references accounts(account_number),
                      foreign key (to_account_number) references accounts(account_number)
                      );

create view account_balance as
      select a.account_number, (a.last_statment_balance + IFNULL(debit,0) - IFNULL(credit,0)) as balance from accounts a
            left join
              (select to_account_number as account_number, sum(amount) as debit from transactions group by to_account_number) debit on debit.account_number = a.account_number
            left join
              (select from_account_number as account_number, sum(amount) as credit from transactions group by from_account_number) credit on credit.account_number = a.account_number;

