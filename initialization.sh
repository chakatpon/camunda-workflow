docker exec -it camunda-database bash -c "/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'p@ssw0rd' -Q 'create database camunda'"