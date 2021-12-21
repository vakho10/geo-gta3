window.addEventListener('load', () => {
    let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/geo-gta3/assets/audio/bg-loop.mp3');
    xhr.responseType = 'arraybuffer';
    xhr.addEventListener('load', () => {
        audioCtx.decodeAudioData(xhr.response).then((audioBuffer) => {
            let source = audioCtx.createBufferSource();
            source.buffer = audioBuffer;
            source.connect(audioCtx.destination);
            source.loop = true;
            source.start();
            playsound(audioBuffer);
        });
    });
    xhr.send();
});