let socket;
let canvas = document.getElementById('myCanvas');
let gc = canvas.getContext("2d");
let CELL_SIZE = 20;
let MAP_SIZE_X = 50;
let MAP_SIZE_Y = 35;
let BODY_SCALE = 0.5;
let scoreBoard = document.getElementById('scoreBoard');

let playerName = document.getElementById('playerNameInput');
let playerColor = document.getElementById('playerColorPicker');

let btnPlay = document.getElementById('connectAsPlayer');
let btnReady = document.getElementById('readyAsPlayer');

let playing = false;
let listIds = [];

let username = document.cookie;

initWindow();

document.addEventListener('keydown', function(event) {
    if(playing) {
        if (event.key === 'a' || event.key === 'ArrowLeft') {
            socket.send('LEFT');
        }
        else if (event.key === 'w' || event.key === 'ArrowUp') {
            socket.send('UP');
        }
        else if (event.key === 'd' || event.key === 'ArrowRight') {
            socket.send('RIGHT');
        }
        else if (event.key === 's' || event.key === 'ArrowDown') {
            socket.send('DOWN');

        }
    }
}, true);

function onClickPlay() {
    if(!playing) {
        showBtnReady();
        socket.send('{"name":"' + playerName.value + '","color":"' + playerColor.value + '"}');
        btnPlay.textContent = "LEAVE";
        playing=true;
    }
    else{
        hideBtnReady();
        socket.send('LEAVE');
        btnPlay.textContent = "PLAY!";
        playing=false;
    }
}

function showBtnReady() {
    btnPlay.className = "w3-bar-item w3-button w3-medium shortPlay";
    setTimeout(function () {
        btnReady.className = "w3-bar-item w3-button w3-medium showReady";
    },100);
}

function hideBtnReady() {
    btnReady.className = "w3-bar-item w3-button w3-medium hideReady";
    setTimeout(function () {
        btnPlay.className = "w3-bar-item w3-button w3-medium widePlay";
    },100);
}

function onClickReady() {
    socket.send('READY');

}

function initWindow() {
    canvas.width = CELL_SIZE * MAP_SIZE_X;
    canvas.height = CELL_SIZE * MAP_SIZE_Y;
}

function drawBaseMap() {

    gc.fillStyle = "#FFFFFF";
    gc.fillRect(0, 0, canvas.width, canvas.height);
    gc.beginPath();
    gc.lineWidth = 0.5;
    for (i = 1; i < MAP_SIZE_X; i++) {
        gc.moveTo(i * CELL_SIZE, 0);
        gc.lineTo(i * CELL_SIZE, canvas.height);
    }

    for (j = 1; j < MAP_SIZE_Y; j++) {
        gc.moveTo(0, j * CELL_SIZE);
        gc.lineTo(canvas.width, j * CELL_SIZE);
    }
    gc.stroke();
}

function removePlayer(id) {
    var index = listIds.indexOf(id);
    listIds.splice(index, 1);
    var item = document.getElementById(id);
    // closePlayerItem(item);
    item.className = "";
    closePlayerItem(item);
}

function closePlayerItem(item) {
    setTimeout(function () {
        scoreBoard.removeChild(item);
    }, 100);
}

function addNewPlayer(player) {
    listIds.push(player.id);
    var listItem = document.createElement('li');
    listItem.setAttribute('id', player.id);
    listItem.setAttribute('style', "background: " + player.color + "; alignment: left");
    listItem.setAttribute('class', "w3-bar");
    listItem.className = listItem.className + " grayscale";


    var playerName = document.createElement('span');
    playerName.appendChild(document.createTextNode(player.name));
    playerName.setAttribute('style', "padding: 20px")

    var score = document.createElement('span');
    score.appendChild(document.createTextNode(player.score))

    listItem.appendChild(playerName);
    listItem.appendChild(score);
    scoreBoard.appendChild(listItem);
    setTimeout(function () {
        listItem.className = listItem.className + " show";
    }, 10);
}

function drawPlayers(gameData) {
    drawBaseMap();
    //draw achievement
    gc.fillStyle = "#FF0000";
    gc.fillRect(CELL_SIZE * gameData.achievements.coord.x, CELL_SIZE * gameData.achievements.coord.y, CELL_SIZE, CELL_SIZE);
    //var index = 0;
    //gc.beginPath();


    //addNewPlayers
    for (player of gameData.players) {
        var playerIndex = listIds.indexOf(player.id);
        if (playerIndex < 0) {
            addNewPlayer(player);
        }
    }
    //delete exited Players
    for (listId of listIds) {
        var found = false;
        for (player of gameData.players) {
            if (listId === player.id) {
                found = true;
                break;
            }
        }
        if (!found) {
            removePlayer(listId);
        }
    }

    for (player of gameData.players) {


        // if(Number(player.id)>Number(index)){
        //     removePlayer(player.id);
        //     //numOfPlayers--;
        //     index=player.id;
        // }
        // if(player.id>=numOfPlayers){
        //     numOfPlayers=index+1;
        //     addNewPlayer(player);
        // }
        listItem = document.getElementById(player.id);
        if (player.gameOver) {
            //index++;
            listItem.className = "w3-bar show grayscale";
            continue;
        }
        else {
            if (player.ready) {
                listItem.className = "w3-bar show";
            }
            else {
                listItem.className = listItem.className + " grayscale";
            }
        }

        document.getElementById(player.id).children[1].textContent = player.score;//toString(player.score);

        gc.beginPath();
        gc.fillStyle = player.color;
        gc.fillRect(CELL_SIZE * player.head.x, CELL_SIZE * player.head.y, CELL_SIZE, CELL_SIZE);
        gc.strokeStyle = player.color;
        gc.lineWidth = CELL_SIZE * BODY_SCALE;
        if (player.tail.length != 0) {
            //draw neck
            gc.moveTo(CELL_SIZE * (player.head.x + 0.5),
                CELL_SIZE * (player.head.y + 0.5));
            gc.lineTo(CELL_SIZE * (player.tail[0].x + 0.5),
                CELL_SIZE * (player.tail[0].y + 0.5));

            //draw body
            for (i = 1; i < player.tail.length; i++) {
                strokeLine(CELL_SIZE * (player.tail[i - 1].x + 0.5),
                    CELL_SIZE * (player.tail[i - 1].y + 0.5),
                    CELL_SIZE * (player.tail[i].x + 0.5),
                    CELL_SIZE * (player.tail[i].y + 0.5)); //draw body
            }
        }
        gc.stroke();
        //index++;
    }
    // if(index<numOfPlayers){
    //     for(var i=index; i<numOfPlayers; i++){
    //         removePlayer(i);
    //         numOfPlayers--;
    //     }
    // }

    //gc.stroke();
    gc.strokeStyle = "#000000";
}

function strokeLine(x, y, x1, y1) {
    gc.moveTo(x, y);
    gc.lineTo(x1, y1);
}

function drawLine() {
    ctx.moveTo(0, 0);
    ctx.lineTo(200, 100);
    ctx.stroke();
}

var text1 = '{ "employees" : [' +
    '{ "firstName":"John" , "lastName":"Doe" },' +
    '{ "firstName":"Anna" , "lastName":"Smith" },' +
    '{ "firstName":"Peter" , "lastName":"Jones" } ]}';

function refreshGUI(obj) {
    //document.getElementById('message').innerHTML = gameData.achievements.coord.x;
    drawPlayers(obj);
}

function sendMessage() {
    var message = document.getElementById('userInput').value;
    socket.send(message);
}

function showMessage(text) {
    document.getElementById('message').innerHTML = text;
}

function subscribeToWebSocket() {
    if ('WebSocket' in window) {
        socket = new WebSocket('ws://'+ self.location.hostname + ':4444');
        socket.onopen = function () {
            document.getElementById("game").className = "";
            drawBaseMap();
            //canvas.style.display="inline-block";
            canvas.className = "show";
            document.getElementById("div_startAGame").className="show";
            showMessage('ONLINE');
            checkCookie();
            socket.send('GUEST');
        };
        socket.onmessage = function (msg) {
            //showMessage(msg.data);
            refreshGUI(JSON.parse(msg.data));
        };
        socket.onerror = function (msg) {
            showMessage('Sorry but there was an error.');
        };
        socket.onclose = function () {
            //canvas.style.display="none";
            canvas.className = "";
            document.getElementById("div_startAGame").className="hide";
            hideBtnReady();
            for(id of listIds){
                removePlayer(id);
            }

            btnPlay.textContent = "PLAY!";
            playing=false;


            showMessage('Server offline.');
            document.getElementById("HTML").className = "grayscale";
            sleep(2000);
            subscribeToWebSocket();
        };
    } else {
        showMessage('Your browser does not support HTML5 WebSockets.');
    }
}

function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}

//Cookies
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function checkCookie() {
    var user = getCookie("username");
    if (user !== "") {
        updateSignedInUser(user);
        alert("Welcome again " + user);
    } else {
        user = prompt("Please enter your name:", "");
        if (user !== "" && user != null) {
            setCookie("username", user, 365);
        }
    }
}

//egyelőre gány módon összevissza

function updateSignedInUser(user) {
    playerName.innerHTML = user;
    document.getElementById('btn_signin').innerHTML = user;
    document.getElementById('bar_signup').style.visibility = "visible";
}


function onClickSignOut() {

}