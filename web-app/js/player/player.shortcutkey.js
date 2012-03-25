function initShortCutKeys()
{
	//console.log("bind keys");
	
	//bind alt+ctrl+p to play
	$.shortcut.add("alt+ctrl+p", function(){
		//console.log("play");
		multimedia.play();
	});
	//bind alt+ctrl+z to pause
	$.shortcut.add("alt+ctrl+z", function(){
		multimedia.pause();
	});
	//bind alt+ctrl+s to stop
	$.shortcut.add("alt+ctrl+s", function(){
		multimedia.stop();
	});
	//bind alt+ctrl+r to rewind
	$.shortcut.add("alt+ctrl+r", function(){
		multimedia.rewind();
	});
	
	//bind alt+ctrl+n to forward
	$.shortcut.add("alt+ctrl+n", function(){
		multimedia.forward();
	});
}