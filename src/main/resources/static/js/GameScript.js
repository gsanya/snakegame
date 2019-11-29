/*
 * Global variables
 */

/*socket*/
let socket;

/*user name*/
let userName;

/*canvas*/
let canvas = document.getElementById('myCanvas');
let gc = canvas.getContext("2d");
/*canvas const*/
const CELL_SIZE = 20;
const MAP_SIZE_X = 50;
const MAP_SIZE_Y = 35;
const BODY_SCALE = 0.5;

/*html elements*/
let playerName = document.getElementById('playerName');
let scoreBoard = document.getElementById('scoreBoard');
let btnPlay = document.getElementById('connectAsPlayer');
let btnReady = document.getElementById('readyAsPlayer');

/*game is running boolean*/
let playing = false;

/*list of gamer Ids*/
let listIds = [];


/*
 * Functions
 */

/*Keydown event listener*/
document.addEventListener('keydown', function(event) {
    if(playing) {
        if (event.key == 'a' || event.key == 'ArrowLeft') {
            socket.send('LEFT');
        }
        else if (event.key == 'w' || event.key == 'ArrowUp') {
            socket.send('UP');
        }
        else if (event.key == 'd' || event.key == 'ArrowRight') {
            socket.send('RIGHT');
        }
        else if (event.key == 's' || event.key == 'ArrowDown') {
            socket.send('DOWN');

        }
    }
}, true);

/*main function called by hmtl body onload*/
function subscribeToWebSocket() {
    if ('WebSocket' in window) {
        socket = new WebSocket('ws://'+ self.location.hostname + ':4444');

        // socket connected (inflate UI and join player to game as watcher)
        socket.onopen = function () {
            // set back from "grayscale"
            document.getElementById("game").className = "";

            // inflate game elements
            drawBaseMap();
            canvas.className = "show";
            document.getElementById("div_startAGame").className = "show";
            showMessage('ONLINE');

            // get username from cookie
            let username = getCookie("username");
            if (username !== "-1")
            {
                // set user name
                userName = username;
                // refresh UI elements
                updateSignedInUser(username);
            }
            else
            {
                userName="GUEST";
                playerName.innerHTML = userName;
            }
            // join game as watcher
            socket.send('JOIN');

        };

        // socket message caught (refresh UI)
        socket.onmessage = function (msg) {
            //showMessage(msg.data);
            refreshGUI(JSON.parse(msg.data));
        };

        // socket error
        socket.onerror = function (msg) {
            showMessage('Sorry but there was an error.');
        };

        //socket closed, connection error ()
        socket.onclose = function () {
            // hide and refresh UI elements
            canvas.className = "";
            document.getElementById("div_startAGame").className="hide";
            hideBtnReady();
            btnPlay.textContent = "PLAY!";
            showMessage('Server offline.');
            document.getElementById("game").className = "grayscale";

            //game is not running
            playing=false;

            //remove players
            for(id of listIds){
                removePlayer(id);
            }

            //wait
            sleep(2000);
            // try to reconnect
            subscribeToWebSocket();
        };

    } else {
        showMessage('Your browser does not support HTML5 WebSockets.');
    }
}

function showMessage(text) {
    document.getElementById('message').innerHTML = text;
}

/*socket.onopen*/
function drawBaseMap() {
    canvas.width = CELL_SIZE * MAP_SIZE_X;
    canvas.height = CELL_SIZE * MAP_SIZE_Y;

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

function updateSignedInUser(user) {
    playerName.innerHTML = user;
    let btnSignIn = document.getElementById('btn_signin');
    btnSignIn.innerHTML = user;
    btnSignIn.href = "";
    document.getElementById('bar_signout').style.visibility = "visible";
    document.getElementById('icon_signin').style.visibility = "visible";
}

/*socket.onclose*/
function removePlayer(id) {
    let index = listIds.indexOf(id);
    listIds.splice(index, 1);

    let item = document.getElementById(id);
    item.className = "";
    closePlayerItem(item);
}

function closePlayerItem(item) {
    setTimeout(function () {
        scoreBoard.removeChild(item);
    }, 100);
}

function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}

/*socket.onmessage*/
function refreshGUI(gameData) {
    //draw base map
    drawBaseMap();

    //draw achievement
    gc.fillStyle = "#FF0000";
    gc.fillRect(CELL_SIZE * gameData.achievements.coord.x, CELL_SIZE * gameData.achievements.coord.y, CELL_SIZE, CELL_SIZE);

    //addNewPlayers
    for (player of gameData.players) {
        let playerIndex = listIds.indexOf(player.id);
        if (playerIndex < 0) {
            addNewPlayer(player);
        }
    }

    //delete exited Players
    for (let listId of listIds) {
        let found = false;
        for (let player of gameData.players) {
            if (listId === player.id) {
                found = true;
                break;
            }
        }
        if (!found) {
            removePlayer(listId);
        }
    }

    //draw players
    for (let player of gameData.players) {
        let listItem = document.getElementById(player.id);
        if (player.gameOver) {
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

        document.getElementById(player.id).children[1].textContent = player.score;

        gc.beginPath();
        gc.fillStyle = player.color;
        gc.fillRect(CELL_SIZE * player.head.x, CELL_SIZE * player.head.y, CELL_SIZE, CELL_SIZE);
        gc.strokeStyle = player.color;
        gc.lineWidth = CELL_SIZE * BODY_SCALE;
        if (player.tail.length !== 0) {
            //draw neck
            gc.moveTo(CELL_SIZE * (player.head.x + 0.5),
                CELL_SIZE * (player.head.y + 0.5));
            gc.lineTo(CELL_SIZE * (player.tail[0].x + 0.5),
                CELL_SIZE * (player.tail[0].y + 0.5));

            //draw body
            for (let i = 1; i < player.tail.length; i++) {
                strokeLine(CELL_SIZE * (player.tail[i - 1].x + 0.5),
                    CELL_SIZE * (player.tail[i - 1].y + 0.5),
                    CELL_SIZE * (player.tail[i].x + 0.5),
                    CELL_SIZE * (player.tail[i].y + 0.5)); //draw body
            }
        }
        gc.stroke();
    }
    gc.strokeStyle = "#000000";
}

function addNewPlayer(player) {
    listIds.push(player.id);
    let listItem = document.createElement('li');
    listItem.setAttribute('id', player.id);
    listItem.setAttribute('style', "background: " + player.color + "; alignment: left");
    listItem.setAttribute('class', "w3-bar");
    listItem.className = listItem.className + " grayscale";


    let playerName = document.createElement('span');
    playerName.appendChild(document.createTextNode(player.name));
    playerName.setAttribute('style', "padding: 20px")

    let score = document.createElement('span');
    score.appendChild(document.createTextNode(player.score))

    listItem.appendChild(playerName);
    listItem.appendChild(score);
    scoreBoard.appendChild(listItem);
    setTimeout(function () {
        listItem.className = listItem.className + " show";
    }, 10);
}

function strokeLine(x, y, x1, y1) {
    gc.moveTo(x, y);
    gc.lineTo(x1, y1);
}


/*onclick listeners*/
function onClickPlay() {
    if(!playing) {
        showBtnReady();
        let playerColor = document.getElementById('playerColorPicker');
        socket.send('{"name":"' + userName + '","color":"' + playerColor.value + '"}');
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

function onClickSignOut() {
    let btnSignIn = document.getElementById('btn_signin');
    btnSignIn.innerHTML = "SIGN IN";
    btnSignIn.href = "";

    document.getElementById('bar_signout').style.visibility = "hidden";
    document.getElementById('icon_signin').style.visibility = "hidden";
    socket.send("SIGNOUT "+playerName.innerHTML);

    playerName.innerHTML= "GUEST";

    //delete cookie
    setCookie("username", userName, 0)
}


/*cookie functions*/
function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "-1";
}

function setCookie(cname, cvalue, exdays) {
    let d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    let expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}