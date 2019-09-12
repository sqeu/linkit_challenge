## Docker

Build
    
    - docker build -t linkitspark .

Run

By default, it will execute both sections of the challenge.

    - docker run --rm -it --network cda --add-host=sandbox-hdp.hortonworks.com:172.18.0.3 linkitspark