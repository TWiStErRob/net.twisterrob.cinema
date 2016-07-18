rem Alternative to the SVN external reference, this directly checks code out and installs in the local .m2 repo.
set TEMP_FOLDER=.gson-extras
rmdir /S /Q %TEMP_FOLDER%
mkdir %TEMP_FOLDER%
pushd %TEMP_FOLDER%
git clone https://github.com/google/gson.git .
cd extras
call mvn package install -U -DskipTests -Dmaven.javadoc.skip=true
popd
echo You can delete the %TEMP_FOLDER% folder if everything went smootly.
pause
