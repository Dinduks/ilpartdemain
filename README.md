# Il Part Demain
This application sends an alert everytime an item of interest
shows up on *leboncoin.fr*, so you can enjoy them good deals before everyone
else.

## Usage

Build the image:

```
sbt assembly
docker build -t ilpartdemain .
```

Use the image:

```
touch data/processed_items.txt
cp data/watched_items.txt{.sample,}
vi data/watched_items.txt

cp .env-sample .env
vi .env

docker run -d --name ilpartdemain -v $PWD/data:/data --env-file .env ilpartdemain
```

*data/watched_items.txt* contains URLs of results from *leboncoin.fr*.
Use the website to search for the items you'd like to watch, using the filters
if you wish, then copy the URL of the results in the file; one URL per line.

You don't use Docker? 1998 just called, and they're looking for you.
