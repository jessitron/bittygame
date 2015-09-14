WHERE="localhost:8080"
alias heroku=WHERE='https://peaceful-bayou-3271.herokuapp.com'

think() {
 game=$1
 curl -XPOST -H"Content-Type: application/json" $WHERE/game/$game/think -d '{"inventory":[]}'
}

begin() {
  game=$1
  echo "begin $game"
  curl $WHERE/game/$game/begin
}

random() {
  curl "$WHERE/random" | jq .created | sed 's/"//g'
}
