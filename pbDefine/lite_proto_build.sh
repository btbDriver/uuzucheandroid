#! /bin/bash

echo "====start===="

lite_fold=com.uu.client.lite
rm -rf ${lite_fold}
cp -R com.uu.client ${lite_fold}
cd ${lite_fold}
ls -l | fgrep proto | awk '{print $NF}' | while read filename;
do
echo "trans ${filename}"
awk 'BEGIN{print "option optimize_for = LITE_RUNTIME;"} {print}' ${filename} >  ${filename}.lite
rm -rf ${filename}
mv ${filename}.lite ${filename}
done
echo "====done, and start to compile===="
rm -rf ../../src/main/java/*
protoc --proto_path=./ --java_out=../../src/main/java ./*.proto
cd ..
rm -rf ${lite_fold}
echo "====end compile===="
cd ..
mvn clean install