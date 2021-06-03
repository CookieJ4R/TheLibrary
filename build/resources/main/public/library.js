var http = new XMLHttpRequest();
var eventSource = new EventSource("/sse");

eventSource.onmessage = function(event){
    document.getElementById("riddleText").innerHTML = event.data;
}

function httpOpenAndSend(method, url, body){
    http.open(method, url);
    body == null ? http.send() : http.send(body);
}

function loadRiddleText(){
    checkCookie();
    httpOpenAndSend("GET", "/getcurriddle");
}

function submitAnswer(){
    httpOpenAndSend("POST", "/submitanswer","answer=" + document.getElementById("answerInput").value);
    document.getElementById("answerInput").value = "";

}

function login(){
    httpOpenAndSend("POST", "/login","username=" + document.getElementById("usernameLoginInput").value + "&password=" + document.getElementById("passwordLoginInput").value);
}

function register(){
    httpOpenAndSend("POST", "/register","username=" + document.getElementById("usernameRegisterInput").value +
        "&password=" + document.getElementById("passwordRegisterInput").value + "&email=" + document.getElementById("emailRegisterInput").value);
}

function showLeaderboard(){
    checkCookie();
    httpOpenAndSend("GET", "/getleaderboard");
}

function checkCookie(){
    if(!document.cookie.includes('Library') && !document.location.href.includes('/login.html'))
        document.location.href = "/login.html";
    else if(document.cookie.includes('Library') && document.location.href.includes('/login.html'))
        document.location.href = "/index.html";
}

function logout(){
    httpOpenAndSend("POST", "/logout");
}

function loadRiddleWithNumber(){
    httpOpenAndSend("GET", "/loadriddlewithnumber?riddleid=" + document.getElementById("riddleID").value);

}

function displayLeaderboard(response){
    var splittedResult = response.split("|");
    var data = splittedResult[1].split(";");
    document.getElementById("yourPoints").innerHTML = "Your Points: " + splittedResult[0];
    var name =""; var points = "";
    for(var i = 0; i < data.length; i++)
        i == 0 || i % 2 == 0 ? name += data[i] + "<br>" : points += data[i] + "<br>";
    document.getElementById("leaderboardNames").innerHTML = name;
    document.getElementById("leaderboardData").innerHTML = points;
}

http.onreadystatechange = function() {

    checkCookie();

    if(!document.location.href.includes('/login.html'))
        document.getElementById("username").innerHTML = document.cookie.split("Library")[1].split(";").toString().replace("=", "").trim();

    if (this.readyState == 4 && this.status == 200) {

        if(http.getResponseHeader("currentRiddle"))
            document.getElementById("riddleText").innerHTML = this.response;
        if(http.getResponseHeader("leaderboard"))
            displayLeaderboard(this.response);
        if(http.getResponseHeader("archiveRiddle"))
            document.getElementById("archiveRiddle").innerHTML = this.response;
        if(http.getResponseHeader("riddleSolveAttempt"))
            if(this.response.toString() == "wrongAnswer") {
                document.getElementById("answerInput").style.border = "2px solid #d91c32";
                document.getElementById("answerResult").innerHTML = "Your answer was wrong!";
                document.getElementById("answerResult").style.color = "#d91c32";
            }
            if(this.response.toString() == "rightAnswer") {
                document.getElementById("answerResult").innerHTML = "Your answer was correct!<br>100 Points have been awarded to you!";
                document.getElementById("answerResult").style.color = "#1ddb43";
                document.getElementById("answerInput").style.border = "border: 2px solid #afbab9";
            }
        }
    if(http.getResponseHeader("login")){
        document.getElementById("usernameLoginInput").style.border = "2px solid #d91c32";
        document.getElementById("passwordLoginInput").style.border = "2px solid #d91c32";
    }

}


