**TODO**

- When we need to make a lot of requests when updating a resource can lead to dropped connecttions
  1. We can fetch the records in a single or fewer paginated requests
  2. Add a sleep intervals to requests that might fire too fast.