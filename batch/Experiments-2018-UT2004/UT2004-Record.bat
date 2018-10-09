REM same full screen game problem
REM ffmpeg -f dshow -i video="screen-capture-recorder" %1.mp4

REM May need to adjust resolution for particular system. %1 is the output file name
ffmpeg -f gdigrab -framerate 30 -offset_x 0 -offset_y 0 -video_size 800x600 -show_region 1 -i desktop %1.mkv

REM ffmpeg -f gdigrab -framerate 30 -offset_x 0 -offset_y 0 -video_size 1280x1024 -show_region 1 -i desktop %1.mkv

REM vlc cannot handle full screen game recording well
REM vlc screen:// -I rc --screen-follow-mouse --screen-fps 24 :sout=#transcode{vcodec=h264,vb=1800,scale=1}:std{access=file,dst=%1.mp4}

exit