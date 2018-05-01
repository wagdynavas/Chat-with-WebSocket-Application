var ws;

function connect() {
	var username = document.getElementById("username").value;
	ws = new WebSocket("ws://" + document.location.host + "/navas-webchat/chat/" + username);
	
	
	ws.onmessage = function(event) {
	    var log = document.getElementById("log");
	        console.log(event.data);
	        var message = JSON.parse(event.data);
	        log.innerHTML += message.sender + " : " + message.content + "\n";
	    };
}


function send() {
    var content = document.getElementById("msg").value;
    var receiver = document.getElementById("receiver").value;
    var json = JSON.stringify({
        "receiver":receiver,
        "content":content
    });

    ws.send(json);
    log.innerHTML += "Me : " + content + "\n";
}