# Il Part Demain
This application sends you an alert everytime an item that might interest you
shows up on *leboncoin.fr*, so you can enjoy them good deals before everyone
else.

## Configuration
```
cp src/main/resources/application.conf{.sample,}
vi src/main/resources/application.conf

touch data/processed_items.txt

cp data/watched_items.txt{.sample,}
vi data/watched_items.txt
```

*data/watched_items.txt* contains URLs of results from *leboncoin.fr*.  
Use the website to search for the items you'd like to watch, using the filters
if you wish, then copy the URL of the results in the file; one URL per line.

## Run
```
./activator "run data/watched_items.txt data/processed_items.txt <refresh_delay> <to_email_addr> <cc_email_addr>"
```
