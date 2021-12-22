// import bgAudio from 'url:../audio/bg-loop.mp3';

window.addEventListener('load', () => {
    const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const gainNode = audioCtx.createGain();
    gainNode.gain.value = 0.35; // setting it to 35%
    gainNode.connect(audioCtx.destination);
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/geo-gta3/audio/bg-loop.mp3');
    xhr.responseType = 'arraybuffer';
    xhr.addEventListener('load', () => {
        audioCtx.decodeAudioData(xhr.response).then((audioBuffer) => {
            let source = audioCtx.createBufferSource();
            source.buffer = audioBuffer;
            source.connect(gainNode);
            source.loop = true;
            source.start();
        });
    });
    xhr.send();
});