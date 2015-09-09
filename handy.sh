think() {
 game=$1
 curl -XPOST -H"Content-Type: application/json" localhost:8080/game/$game/think -d '{"inventory":[]}'
}

begin() {
  game=$1
  echo "begin $game"
  curl localhost:8080/game/$game/begin
}

random() {
  curl localhost:8080/random | jq .created | sed 's/"//g'
}
