# League-of-Legends-Stat-Tracker
Small Java Desktop Application to keep track of a League of Legends player's ranked stats.

The application will allow a person to choose a region and search a player within that region and get back their ranked stats whether it is in ranked flex or ranked solo/duo. After the person searches for the player they will receive wins, losses, tier, rank, league points, win streak, or NA depending on if the player exists or doesn't play ranked.

The Riot Games API will be used to get the information from this game. Specifically, the endpoint URLS we will be requesting the information from are the LeagueV4 API to get the ranked stats and the SummonerV4 API in order to get the encrypted summonerID. The libraries that will be used are JSON Simple to be able to parse the information received from the Riot API as well as Java Swing to be able to create a basic GUI application.
