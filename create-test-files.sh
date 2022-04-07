#!/bin/bash
declare filecount=200 # how many of each file
declare filesizes=('1MB' '2MB' '3MB' '4MB' '5MB' '10MB' '20MB' '50MB' '100MB') # array of file sizes to generate
declare filelist='./files/fileList.csv' # CSV file list

make_files () {
    echo "Creating $1 files of $2..."

    # Trim "B" from "MB" for head command, e.g. 1MB -> 1M
    size=${2::-1}

    for (( i=0; i<$1; i++ ))
    do
        head -c $size </dev/urandom >./files/$2.$i
        echo "$2.$i,/$2.$i" >> $filelist
        printf "  $2.$i     \r"
    done
}

# Clear existing files
echo "Removing previous files..."
rm ./files/*

# Create file list
echo "Creating file list..."
echo "name,uri" > $filelist

# Loop through arrays to create files
for filesize in "${filesizes[@]}"
do
    make_files $filecount $filesize
done

echo ""
echo "Done."
