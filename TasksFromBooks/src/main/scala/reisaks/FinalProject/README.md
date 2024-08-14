**Spinning Wheel game**

**Task Description**

Spinning wheel contains 100 different sectors, numbered from 1 to 100. During each round, wheel is being rotated for 5 seconds and it stops randomly at some sector.
Odd sectors have a winning multiplier equal to : bet x 2
Even sectors (except for sector number 100) has a winning multiplier : bet x 3
Sector number 100 has a winning multiplier : bet x 50

Game rounds happen one after another, using automatic scheduling. Each round has 2 phases GAME START -> GAME FINISH. Duration of each round is 5 seconds.

Player can join table at any time in between GAME_START and GAME_FINISH and place a bet on a random sector.
Bet is a structure, that contains two fields <BET_CODE> | <AMOUNT>
Player is a structure, that contains one field <PLAYER_ID>
Table is a structure, that contains 3 fields <TABLE_ID> | <PLAYERS> | <BETS>

Please implement Spinning wheel game using Akka framework (essentially our main actor is table actor, no persistence is required).

Players should connect to server application using web sockets, so in fact 2 applications have to be developed
a) server side
b) client side (as a Scala app that can be launched from CLI, with following parameters a) server web socket url b) player id)

At the end of each round, server should announce to each player a) the result of the game b) the amount of player winning
Client app should log the result in console.

Sub-tasks of spinning wheel game implementation

1. Define Domain Models (Server-Side) (Player, Bet, Table, etc.) (Est. time 3 days)
2. Game Logic Implementation (Server-Side) (Spinning wheel logic) (Est. time 4 days)
3. Implement Actors (Server-Side) (Est. time 4 days)
4. Implement WebSocket Server (Server-Side) (Est. time 4 days)
5. Client Application (Client-Side) (Est. time 3 days)
6. Testing phase (Est. time 2 days)


