//now the locations must be between 0 and 39...


//----------------constants/initvalues----------------
state=1; //1: waiting; 2:playing; 3:ending
gs=10;
let socket = new WebSocket("ws://localhost:4444");

//----------------Variables to test----------------
let snake1 = {
    trail:[{x:10,y:10},{x:10,y:11},{x:10,y:12}],
    color:"green",
};
let snake2 = {
    trail:[{x:3,y:10},{x:3,y:11},{x:3,y:12}],
    color:"blue",
};
let snakes=[snake1,snake2]; //players
let apples=[{x:0,y:0},{x:0,y:39},{x:1,y:39},{x:39,y:39},{x:39,y:0}];
let gameState={
    snakes:snakes,
    apples:apples
};




//----------------socket functions----------------
socket.onopen = function(e) {
    document.getElementById("console").innerHTML = "Connection established";
};

socket.onmessage = function(event) {
    document.getElementById("message").innerHTML = event.data;
    if(event.data=="state:1")
    {
        state=1;
        document.getElementById("console").innerHTML ='state:1';

    }
    if(event.data=="state:2")
    {
        state=2;
        document.getElementById("console").innerHTML ='state:2';
    }
    if(event.data=="state:3")
    {
        state=3;
        document.getElementById("console").innerHTML ='state:3';
    }
    else
    {
        //TODO:process message and than drawGame
    }
};

socket.onclose = function(event) {
    if (event.wasClean) {
        document.getElementById("console").innerHTML = `[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`;
    } else {
        document.getElementById("console").innerHTML ='[close] Connection died';
    }
};

socket.onerror = function(error) {
    document.getElementById("console").innerHTML ='[error] ${error.message}';
};


//----------------window functions----------------
window.onload=function() {
    canv=document.getElementById("gameCanvas");
    ctx=canv.getContext("2d");
    document.addEventListener("keydown",keyPush);
    drawWait();
    drawGame(gameState);
    //drawEnding("gsanya");
};

window.onclose=function () {
    socket.onclose = function () {}; // disable onclose handler first
    socket.close();
};

window.onabort=function () {
    socket.onclose = function () {}; // disable onclose handler first
    socket.close();
};

window.on
//----------------gamestate create----------------
function createGameState(message)
{
    //TODO: create gamestate object from massage
}


//----------------drawing functions----------------
function drawWait(){
    ctx.fillStyle="black";
    ctx.fillRect(0,0,canv.width,canv.height);
    ctx.font = "30px";
    ctx.fillStyle = "white";
    ctx.textAlign = "center";
    ctx.fillText("Waiting for players", canv.width/2, canv.height/2);
}

function drawEnding(Winner){
    ctx.fillStyle="black";
    ctx.fillRect(0,0,canv.width,canv.height);
    ctx.font = "30px";
    ctx.fillStyle = "white";
    ctx.textAlign = "center";
    ctx.fillText("Game has ended. Winner is "+Winner, canv.width/2, canv.height/2);
}

function drawGame(gameState) {
    ctx.fillStyle="black";
    ctx.fillRect(0,0,canv.width,canv.height);


    for(let i=0;i<gameState.snakes.length;i++) {
        ctx.fillStyle=gameState.snakes[i].color;
        for(let j=0;j<gameState.snakes[i].trail.length;j++) {
            ctx.fillRect(gameState.snakes[i].trail[j].x * gs+2, gameState.snakes[i].trail[j].y * gs+2, gs - 4, gs - 4);
        }
    }
    ctx.fillStyle="red";
    for(let i=0;i<gameState.apples.length;i++)
    {
        ctx.fillRect(gameState.apples[i].x*gs+2,gameState.apples[i].y*gs+2,gs-4,gs-4);
    }
}

function keyPush(evt) {
    switch(evt.which) {
        case 37://left
            socket.send("left");
            break;
        case 38://down
            socket.send("up");
            break;
        case 39://right
            socket.send("right");
            break;
        case 40://up
            socket.send("down");
            break;
    }
}