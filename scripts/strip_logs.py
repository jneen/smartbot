import sys

logFile = open(sys.argv[1], 'r+')
loglines = logFile.readlines()
messages = []
for line in loglines:
    if "<" in line:
        logEntry = line.split("<")[1]
        if ">" in logEntry:
            name, message = logEntry.split(">")[0], ''.join(logEntry.split(">")[1:])
            messages.append(message.lstrip().rstrip())

strippedFile = open(sys.argv[2], 'w+')
for item in messages:
  strippedFile.write("%s\n" % item)
