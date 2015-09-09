think() {
 game=$1
 curl -XPOST -H"Content-Type: application/json" localhost:8080/game/$game/think -d '{"inventory":[]}'
}
