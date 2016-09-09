#!/bin/sh

################
# Package JSON #
################

if [ -z "$1" ] ; then
  echo "You must specify the version to package"
  exit 1
fi

if [ ! -d target/site/apidocs ] ; then
  echo "Creating the JSON documentation"
  mvn javadoc:javadoc
fi

VERSION="$1"

if [ ! -d package ] ; then
  mkdir package
fi

cd package
rm -R *
mkdir apidocs
cp ../ChangeLog.txt ../LICENSE ../README.md .
cp ../target/JSON-$VERSION.jar .
cp -r ../target/site/apidocs/* apidocs
zip -r JSON-$VERSION.zip ChangeLog.txt LICENSE README.md JSON-$VERSION.jar apidocs
dos2unix ChangeLog.txt LICENSE README.md 
tar zcf JSON-$VERSION.tar.gz ChangeLog.txt LICENSE README.md JSON-$VERSION.jar apidocs
exit 0

