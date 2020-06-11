# This is not a shebang
# Make sure you have all the libraries installed
# This uses Python 3.7.3 and wget
# Add this file to your Home directory and run using "python3 get_images.py"
# Still a WIP, scroll to bottom to see TODO

import os
import sys
import requests
import xml.etree.ElementTree as ET
import random
import zipfile
import threading
import time

start = 0
rows = 1
username = "yashpaz123"
password = "neXmut-hersy8-qifbuw"
output = "sentinel-xml-data"
outputDirectory = ''
numThreads = 1

# Syntax usage and disclaimer
def printHelp():
    print('\nSYNTAX:')
    print('get_image [start] [rows] [outputDirectory], EXAMPLE: get_image 10 20 <path>')
    print('\t\t\t\t\tOR')
    print('get_image [start] [rows] [outputDirectory] [numThreads], EXAMPLE: get_image 10 20 <path> 3')
    print('\nNOTE:')
    print('If numThreads is not specified, the program will run as one process and one thread (not visible).')
    print('\tIncreasing numThreads will mostly increase download efficiency, however, if')
    print('\ttoo many threads are used they will interfere with one another and the')
    print('\tdownloaded data might become corrupt. As another side note, more threads')
    print('\tgenerally means more memory, so avoid using more than 26 threads on an')
    print('\t8 GB RAM computer. For best results, numThreads should be set to less')
    print("\tthan half of the number of rows you wish to download. I'd recommend keeping")
    print('\tconstant tabs on your computer memory, although most cases this should')
    print('\trun smoothly as long as you are not running CPU, memory, disk, or network')
    print('\tintensive processes in parallel. Do not delete any folders this program')
    print('\tcreates while it is still running, as this can lead to data corruption of')
    print('\tother folders on your computer. Press "CONTROL+C" to exit the program. Make')
    print('\tsure your disk has enough space on it, since the files downloaded can range')
    print('\tfrom 100 MB to 1.5 GB in size. You can interface this program with other')
    print('\tapplications via your system shell or by piping. Ideally, this program would')
    print('\trun on a big server capable of running this very fast. Sometimes the unzip')
    print("\tautomation section of this program doesn't work, in which case you will have")
    print('\tto manually decompress them.')
    exit()

# Argument handling
if(len(sys.argv) == 1):
    print('ERROR! You must enter at least one argument')
    printHelp()
elif(len(sys.argv) == 4):
    try:
        start = int(sys.argv[1])
        rows = int(sys.argv[2])
        outputDirectory = sys.argv[3].replace('\ ', ' ')
    except:
        print('ERROR! Invalid arguments')
        printHelp()
elif(len(sys.argv) == 5):
    try:
        start = int(sys.argv[1])
        rows = int(sys.argv[2])
        outputDirectory = sys.argv[3].replace('\ ', ' ')
        numThreads = int(sys.argv[4])
    except:
        print('ERROR! Invalid arguments')
        printHelp()
else:
    print('ERROR! Invalid number of arguments')
    printHelp()

if(len(sys.argv) == 1):
    print('ERROR! You must enter at least one argument')
    exit()
else:
    output = sys.argv[1]

# This is used to differentiate different "get_image" sessions, so the folders won't overwrite, it's just a random hex string
processID = str(hex(random.randint(0, 64*64*64*64-1)))
print('\nProcess ID: '+processID)

outputDirectory += '/SENTINEL-GET-IMAGE-ID-'+processID
os.mkdir(outputDirectory)
outputDirectory += '/'

# Get XML list of URLs, which we use to contact the Sentinel web server with to retrieve the data
print('Accessing Web Server: "https://scihub.copernicus.eu/dhus"...')
url = "https://scihub.copernicus.eu/dhus/search?start="+str(start)+"&rows="+str(rows)+"&q=*"
os.system('wget --no-check-certificate --user='+username+' --password='+password+' --output-document='+outputDirectory+output+'-'+processID+'.xml "'+url+'"')

print('SUCCESS: File saved as "'+outputDirectory+output+'-'+processID+'.xml"')

imageDataURLSet = []

# Parse XML
print('Parsing XML...')
tree = ET.parse(outputDirectory+output+'-'+processID+'.xml')
root = tree.getroot()

# Generate list of URLs
print('Filtering URL List...')
for elem in root.iter():
    tempURL = elem.attrib.get('href', '*')
    if('/$value' in tempURL and "/Products('Quicklook')" not in tempURL):
        imageDataURLSet.append(tempURL)

print('\nThese are the URLs:')
for i in imageDataURLSet:
    print('\t'+i)

# Requests data from web server, shows loading bar because it looks cool
def downloadURLLoading(temp_url, filename):
    print('\tDownloading "'+temp_url+'"...')
    print('\t\tSaving to "'+name+'"...')
    with open(filename, 'wb') as f:
        response = requests.get(temp_url, stream=True)
        total = response.headers.get('content-length')
        if total is None:
            f.write(response.content)
        else:
            downloaded = 0
            total = int(total)
            for data in response.iter_content(chunk_size=max(int(total/1000), 1024*1024)):
                downloaded += len(data)
                f.write(data)
                done = int(50*downloaded/total)
                sys.stdout.write('\r\t\t[{}{}]'.format('█' * done, '.' * (50-done)))
                sys.stdout.flush()
    sys.stdout.write('\n')

# Requests data from web server without loading bar, for multithreading purpose
def downloadURL(temp_url, name, threadName):
    print('\t'+threadName+'Downloading "'+temp_url+'"...')
    r = requests.get(temp_url, allow_redirects=True)
    print('\t\t'+threadName+'Saving to "'+name+'"...')
    open(name, 'wb').write(r.content)

imageDataZipFileNames = []

# So the threads know when to stop, a sort of central controller for them
numDownloadsRemaining = len(imageDataURLSet)

# Handles individual threads
def singleThreadDownloadURL(threadName):
    global numDownloadsRemaining
    setLength = len(imageDataURLSet)
    while(setLength > 0):
        i = setLength - 1
        temp_imgDatURL = imageDataURLSet[i]
        del imageDataURLSet[i]
        temp_url = temp_imgDatURL.replace('https://', 'https://'+username+':'+password+'@')
        name = 'sentinel-image-data-'+processID+'-'+str(i)+'.zip'
        downloadURL(temp_url, outputDirectory+name, threadName)
        imageDataZipFileNames.append(outputDirectory+name)
        setLength = len(imageDataURLSet)
        numDownloadsRemaining -= 1

# Downloads the data using the functions listed above
print('\nDownloading URLs...')
if(numThreads > 1):
    # Method using threads (awesome)
    try:
        threads = []
        for i in range(numThreads):
            x = threading.Thread(target=singleThreadDownloadURL, args=("[THREAD "+str(i+1)+"] : ",))
            threads.append(x)
        for thread in threads:
            thread.start()
        print('(Multiple threads ('+str(len(threads))+') are in use, so progress bars cannot be shown)')
    except:
        print("ERROR: Unable to start thread")
    # So the code won't continue until all threads have finished their downloads
    while(numDownloadsRemaining > 0):
        time.sleep(2)
else:
    # Method without threads (not awesome)
    for i in range(len(imageDataURLSet)):
        temp_url = imageDataURLSet[i].replace('https://', 'https://'+username+':'+password+'@')
        name = 'sentinel-image-data-'+processID+'-'+str(i)+'.zip'
        downloadURLLoading(temp_url, outputDirectory+name)
        imageDataZipFileNames.append(outputDirectory+name)

print('SUCCESS: All URLs downloaded')

unzippedDirectory = "sentinel-image-data-"+processID

# Unzips the downloaded data files, sometimes doesn't work if using multithreads
print('\nUnzipping Files...')
for zipName in imageDataZipFileNames:
    print('\tSaving Unzipped "'+zipName+'"')
    with zipfile.ZipFile(zipName, "r") as zip_ref:
        zip_ref.extractall(outputDirectory+unzippedDirectory)
    print('\tDeleting Zip File "'+zipName+'"')
    os.remove(zipName)
print('SUCCESS: All unzipped folders saved to "'+outputDirectory+unzippedDirectory+'"')

# Finally!
print('\nDone!\n')
exit()

'''
TODO:
Find way to take lat-long coordinates instead, and convert them into start-row, so it's easier to find more specific areas of the globe.
Add extra section for reading the downloaded data file structure and generating one image folder to dump all the "*TCI*" "*.jp2" type images into.
Expand code to multiprocesses to make even faster, at the expense of memory and and harder to integrate them and communicate, maybe make this optional, for multicore servers.
Run this program in Java using subshell to retrieve path of output image files, and display interactive image data using Swing and BufferedImage.
Fix bug where sometimes doesn't work with folders with spaces in the title
'''

