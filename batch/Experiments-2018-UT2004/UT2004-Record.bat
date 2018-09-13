REM same full screen game problem
REM ffmpeg -f dshow -i video="screen-capture-recorder" %1.mp4

REM vlc cannot handle full screen game recording well
vlc screen:// -I rc --screen-follow-mouse --screen-fps 24 :sout=#transcode{vcodec=h264,vb=1800,scale=1}:std{access=file,dst=%1.mp4}
