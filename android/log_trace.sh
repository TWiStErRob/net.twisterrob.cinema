if [ -z "$SDK_DIR" ]
then
	SDK_DIR='/home/zoli/appz/android-sdk-linux'
fi

DIR="$SDK_DIR/platform-tools"

cd "$DIR"

./adb shell setprop log.tag.cw.JSON VERBOSE
./adb shell setprop log.tag.cw.ACCESS VERBOSE
./adb shell setprop log.tag.cw.UI VERBOSE
./adb shell setprop log.tag.cw.GEO VERBOSE
./adb shell setprop log.tag.cw.SYSTEM VERBOSE

