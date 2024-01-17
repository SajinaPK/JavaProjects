#!/bin/bash

iterations=$1
currentDir=`pwd`
ORIGTEXTFILE=$currentDir/classics.txt
TESTTEXTFILE=$currentDir/classics_1g.txt

echo "Creating a temporary text file of 1GB size"
rm -f $TESTTEXTFILE
iter=0
while [ $iter -lt 512 ]; do
	cat $ORIGTEXTFILE >> $TESTTEXTFILE
	iter=$((iter+1))
done

COMPRESSEDTEXTFILE=$currentDir/classics_compressed_1g.txt
DECOMPRESSEDTEXTFILE=$currentDir/classics_decompressed_1g.txt

if [ -z $iterations ]; then
	iterations=1
fi
if [ -z $TEST_JDK_HOME ]; then
	echo "Set TEST_JDK_HOME to point to the JDK"
	rm -f $TESTTEXTFILE
	exit
fi

JAVA_HOME=$TEST_JDK_HOME
$JAVA_HOME/bin/java -version
if [ $? -ne 0 ]; then
	echo "Can not run Java version, point TEST_JDK_HOME to valid JDK"
	rm -f $TESTTEXTFILE
	exit
fi
$JAVA_HOME/bin/javac *.java

if [ -z $JVM_OPTIONS ]; then
	JVM_OPTIONS="-Xmx8g"
fi

declare -a inputBufferSizeList=(1024 2048 4096 8192 16384 65536 262144)

echo "Running InMemory Compression-Decompression test using java.util.zip.Deflater and java.util.zip.Inflater"
iter=0
rm -f inmemorycompression.results.txt
while [ $iter -lt $iterations ]; do
	for inputBufferSize in "${inputBufferSizeList[@]}"; do
		$JAVA_HOME/bin/java $JVM_OPTIONS JavaCompression $TESTTEXTFILE $inputBufferSize   | tee -a inmemorycompression.results.txt
	done
	iter=$((iter+1))
done


echo "Running File Compression using java.util.zip.DeflaterOutputStream"
rm -f DeflaterOutputStreamCompressionResults.txt
rm -f InflaterOutputStreamCompressionResults.txt
iter=0
while [ $iter -lt $iterations ]; do
	for inputBufferSize in "${inputBufferSizeList[@]}"; do
		$JAVA_HOME/bin/java JavaDeflaterStream $TESTTEXTFILE  $COMPRESSEDTEXTFILE $inputBufferSize | tee -a DeflaterOutputStreamCompressionResults.txt
		$JAVA_HOME/bin/java JavaInflaterStream $COMPRESSEDTEXTFILE $DECOMPRESSEDTEXTFILE $inputBufferSize | tee -a InflaterOutputStreamCompressionResults.txt
		diff $TESTTEXTFILE $DECOMPRESSEDTEXTFILE
		if [ $? -ne '0' ]; then
			echo "FILE COMPRESSION USING DEFLATER failed"
			exit
		fi
		rm -f $DECOMPRESSEDTEXTFILE $COMPRESSEDTEXTFILE
	done
	iter=$((iter+1))
done

echo "Running File Compression using java.util.zip.GZIPOutputStream"
rm -f GZIPCompressionResults.txt
iter=0
while [ $iter -lt $iterations ]; do
	for inputBufferSize in "${inputBufferSizeList[@]}"; do
		$JAVA_HOME/bin/java JavaGZip $TESTTEXTFILE  $COMPRESSEDTEXTFILE $inputBufferSize | tee -a GZIPCompressionResults.txt
		$JAVA_HOME/bin/java JavaUnGZip $COMPRESSEDTEXTFILE $DECOMPRESSEDTEXTFILE $inputBufferSize | tee -a GZIPCompressionResults.txt
		diff $TESTTEXTFILE $DECOMPRESSEDTEXTFILE
		if [ $? -ne '0' ]; then
			echo "FILE COMPRESSION USING DEFLATER failed"
			exit
		fi
		rm -f $DECOMPRESSEDTEXTFILE $COMPRESSEDTEXTFILE
	done
	iter=$((iter+1))
done

rm -f $TESTTEXTFILE	

