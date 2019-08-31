ffmpeg -f gdigrab -framerate 30 -offset_x 0 -offset_y 0 -video_size 1600x900 -show_region 1 -i desktop %1.mkv
REM Change resolution to match screen resolution on whichever machine we use for testing.
exit