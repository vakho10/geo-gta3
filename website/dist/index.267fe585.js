window.addEventListener('load', ()=>{
    const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const gainNode = audioCtx.createGain();
    gainNode.gain.value = 0.4; // setting it to 40%
    gainNode.connect(audioCtx.destination);
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'https://vakho10.github.io/geo-gta3/audio/bg-loop.mp3');
    xhr.responseType = 'arraybuffer';
    xhr.addEventListener('load', ()=>{
        audioCtx.decodeAudioData(xhr.response).then((audioBuffer)=>{
            let source = audioCtx.createBufferSource();
            source.buffer = audioBuffer;
            source.connect(gainNode);
            source.loop = true;
            source.start();
            playsound(audioBuffer);
        });
    });
    xhr.send();
});

//# sourceMappingURL=index.267fe585.js.map
