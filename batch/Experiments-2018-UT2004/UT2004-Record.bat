vlc screen:// -I rc --screen-follow-mouse --screen-fps 24 :sout=#transcode{vcodec=h264,vb=1800,scale=1}:std{access=file,dst=%1.mp4}
